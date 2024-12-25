package com.portabull.git;

import com.portabull.git.models.GitConfigDTO;
import com.portabull.git.models.GitDiffResponse;
import com.portabull.git.models.GitDownloadResponse;
import com.portabull.utils.commonutils.CommonUtils;
import com.portabull.utils.encoder.EncoderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@EnableScheduling
public class BuildService {

    Logger log = LoggerFactory.getLogger(BuildController.class);

    private final GitService gitService;

    public BuildService(GitService gitService) {
        this.gitService = gitService;
    }

    Map<String, String> branchWithCommitSha = new HashMap<>();

    @Scheduled(cron = "1 * * * * *")
    public void cronJobSch() throws IOException {
        log.info("BuildController :: started");
        GitConfigDTO dto = new GitConfigDTO();
        dto.setBranch("master");
        dto.setProvider(GitConfigDTO.GitProviderType.GITHUB);
        dto.setAccessToken(EncoderUtils.decode("yqmadi_bom_55EGCD4KI6TZmkcMIDWwuQ_9qcBkUzW1LFkjL36fD6UkrwxIcgiZ7pwNOFKkC2mhmC24F4MY7HPPAkt6ZK"));
        dto.setOwner("Portabull");
        dto.setRepository("worktree-hrms-admin");
        dto.setProviderURL("https://api.github.com");
        Map<String, String> map = new HashMap<>();
        String command = "mvn clean install";
        String targetDir = "target\\worktree-hrms-admin-0.0.1-SNAPSHOT.jar";
        String deployementFilePath = "C:\\Users\\Administrator\\Downloads\\deployments\\";
        map.put("port", "443");
        map.put("command", command);
        map.put("targetDir", targetDir);
        map.put("deployementFilePath", deployementFilePath);
        deployJar(dto, map);
        log.info("BuildController :: ends");
    }

    public synchronized void deployJar(GitConfigDTO dto, Map<String, String> internalPojo) throws IOException {
        String command = internalPojo.get("command");
        String targetDir = internalPojo.get("targetDir");
        String deployementFilePath = internalPojo.get("deployementFilePath");
        int port = Integer.valueOf(internalPojo.get("port"));

        if (deployementFilePath.endsWith(File.separator)) {
            deployementFilePath = deployementFilePath.substring(0, deployementFilePath.length() - 1);
        }

        String deployementFileName = deployementFilePath + File.separator + "download_" + new Date().getTime() + ".zip";

        if (getCommitSha(dto) == null) {
            downloadAndBuild(dto, deployementFilePath, deployementFileName, port, command, targetDir);
        } else {
            GitDiffResponse gitDiffernces = gitService.getGitDiffernces(dto, getCommitSha(dto));
            if (gitDiffernces != null && Integer.valueOf(gitDiffernces.getNoOfCommitsBehind()) != 0) {
                downloadAndBuild(dto, deployementFilePath, deployementFileName, port, command, targetDir);
            } else {
                log.info("No recent Commits...");
            }
        }
    }

    private void downloadAndBuild(GitConfigDTO dto, String deployementFilePath, String deployementFileName, int port, String command, String targetDir) throws IOException {
        log.info("downloading project zip file");
        GitDownloadResponse response = gitService.downloadRepository(dto);

        setCommitSha(dto, response.getLatestCommitSha());
        log.info("downloaded successfully");
        try (InputStream stream = new FileInputStream(response.getFile())) {
            Files.copy(stream, Paths.get(deployementFileName),
                    StandardCopyOption.REPLACE_EXISTING);
        }

        log.info("File Deleted : {} , path : {}", FileSystemUtils.deleteRecursively(response.getFile()), response.getFile().getAbsolutePath());

        extractAndBuild(deployementFilePath, deployementFileName, port, command, targetDir);

        log.info("File Deleted : {} , path : {}", FileSystemUtils.deleteRecursively(Paths.get(deployementFileName)), deployementFileName);
    }

    private void extractAndBuild(String deployementFilePath, String deployementFileName, int port, String command, String targetDir) throws IOException {
        String dir = deployementFilePath + File.separator + new Date().getTime() + "";
        File fileDir = new File(dir);
        fileDir.mkdirs();

        log.info("unzipping the project...");
        unzip(deployementFileName, dir);

        log.info("unzip donw");

        dir = new File(dir).listFiles()[0].getAbsolutePath() + File.separator;

        String[] commands = {"cmd.exe", "/c",
                "cd " + dir + " && " + command};

        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(commands);


        log.info("installing maven");

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));

        String s;
        StringBuilder buildOutput = new StringBuilder();
        while ((s = stdInput.readLine()) != null) {
            buildOutput.append("<p>").append(s).append("</p>");
        }

        while ((s = stdError.readLine()) != null) {
            buildOutput.append("<p>").append(s).append("</p>");
        }

        log.info(buildOutput.toString());

        log.info("maven installed");

        log.info("shutdown the current application");
        killPort(port);
        log.info("shutdown done");

        String jarLocation = dir + targetDir;
        String finalExeJar = deployementFilePath + File.separator + new File(jarLocation).getName();

        if (new File(finalExeJar).exists()) {
            new File(finalExeJar).delete();
        }

        try (InputStream stream = new FileInputStream(jarLocation)) {

            try (OutputStream outputStream = new FileOutputStream(finalExeJar)) {

                byte[] buffer = new byte[1024];
                int bytesRead;

                // Read from InputStream and write to the OutputStream
                while ((bytesRead = stream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

        }

        log.info("File Deleted : {} , path : {}", FileSystemUtils.deleteRecursively(fileDir), fileDir);

        String jarRunnableCommand = "start javaw -Xmx4000m -Dserver.port=" + port + " -jar " + finalExeJar;

        String batFilePath = generateBatFile(jarRunnableCommand);

        try {
            log.info("Application Starting...");
            Process process = Runtime.getRuntime().exec(batFilePath);

            process.waitFor(1, TimeUnit.MINUTES);


//            BufferedReader stdInput1 = new BufferedReader(new
//                    InputStreamReader(process.getInputStream()));
//
//            BufferedReader error = new BufferedReader(new
//                    InputStreamReader(process.getErrorStream()));
//
//            String s1;
//            StringBuilder appRunOutput = new StringBuilder();
//            while ((s1 = stdInput1.readLine()) != null) {
//                appRunOutput.append("<p>").append(s1).append("</p>");
//            }
//
//
//            String s2;
//            StringBuilder appRunError = new StringBuilder();
//            while ((s2 = error.readLine()) != null) {
//                appRunError.append("<p>").append(s2).append("</p>");
//            }

            new File(batFilePath).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String generateBatFile(String jarRunnableCommand) {
        String filePath = prepareTempPath() + File.separator + prepareTempName("jarExecution.bat");
        try {
            FileWriter myWriter = new FileWriter(filePath);
            myWriter.write(jarRunnableCommand);
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    private String prepareTempPath() {
        String tmpDir = System.getProperty("java.io.tmpdir");

        if (!tmpDir.endsWith(File.separator)) {
            tmpDir = tmpDir + File.separator;
        }

        tmpDir = tmpDir + "portabull_server_temp_dir_files_sd" + File.separator;

        if (!new File(tmpDir).exists()) {
            new File(tmpDir).mkdirs();
        }
        return tmpDir;
    }

    private String prepareTempName(String defaultFileName) {
        return new StringBuilder("Doc").append("_").
                append(new Date().getTime()).append("_").
                append(new Random().nextInt(1000)).append(defaultFileName).toString();
    }


    private void killPort(int port) {
        String command = "netstat -ano | findstr :" + port;

        try {
            // Use cmd.exe to run the command in the Windows command prompt
            String fullCommand = "cmd.exe /c " + command;
            log.info(fullCommand);
            Process process = Runtime.getRuntime().exec(fullCommand);

            // Step 2: Process the output from netstat
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean processFound = false;

            while ((line = reader.readLine()) != null) {
                log.info(line);
                System.out.println("Netstat Output: " + line);  // Print the raw output for debugging

                // Split the line into parts (the PID is the last field in the output)
                String[] parts = line.trim().split("\\s+");

                if (parts.length >= 5) {  // Ensure there are enough parts to extract the PID
                    String pid = parts[parts.length - 1];  // The last part is the PID
                    log.info("Found PID: {} ", pid);

                    // Step 3: Kill the process using the PID
                    String killCommand = "taskkill /F /PID " + pid;
                    Process killProcess = Runtime.getRuntime().exec(killCommand);
                    killProcess.waitFor();  // Wait for the kill process to complete

                    log.info(killCommand);
                    processFound = true;
                    break;  // Exit the loop after killing the process
                }
            }

            // If no process was found on the port
            if (!processFound) {
                log.error("No process found on port {}", port);
            }

        } catch (Exception e) {
            log.error("Exception", e);
        }
    }


    private void unzip(String zipFile, String outputFolder) throws IOException {

        final byte[] buffer = new byte[1024];
        final ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            final File newFile = newFile(new File(outputFolder), zipEntry);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                final FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    private String getCommitSha(GitConfigDTO config) {
        return branchWithCommitSha.get(config.getBranch() + config.getRepository() + config.getOwner());
    }

    private String setCommitSha(GitConfigDTO config, String commitSha) {
        return branchWithCommitSha.put(config.getBranch() + config.getRepository() + config.getOwner(), commitSha);
    }
}
