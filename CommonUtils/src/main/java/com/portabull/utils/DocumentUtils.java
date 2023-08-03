package com.portabull.utils;

import com.portabull.utils.fileutils.FileHandling;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.Date;

public class DocumentUtils {

    private DocumentUtils() {
    }

    public static File convertMultiPartToFile(MultipartFile file) {
        File convertedFile = new File(FileHandling.prepareTempPath() + File.separator + new Date().getTime() + file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            LoggerUtils.logError("While converting multipart to file it throws error", e);
        }
        return convertedFile;
    }

    public static boolean deleteFile(File file) throws IOException {
        Files.delete(file.toPath());
        return true;
    }

    public static File createFile(InputStream inputStream, String fileName) throws IOException {
        File file = FileHandling.createTempFile(fileName);
        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(IOUtils.toByteArray(inputStream));
        }
        return file;
    }
}
