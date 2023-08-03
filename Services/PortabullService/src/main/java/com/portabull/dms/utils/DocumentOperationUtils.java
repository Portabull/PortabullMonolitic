package com.portabull.dms.utils;

import com.portabull.constants.LoggerErrorConstants;
import com.portabull.constants.MessageConstants;
import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.dms.configuration.EncriptionConfiguration;
import com.portabull.entitys.DynamicDocumentSecurity;
import com.portabull.execption.BadRequestException;
import com.portabull.execption.ServerException;
import com.portabull.response.DocumentResponse;
import com.portabull.response.FileResponse;
import com.portabull.response.PortableResponse;
import com.portabull.utils.datautils.StringUtils;
import com.portabull.utils.encoder.EncoderUtils;
import com.portabull.utils.fileutils.FileHandling;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Component
public class DocumentOperationUtils {

    static final Splitter splitter;

    static {
        splitter = new Splitter();
    }

    @Autowired
    DMSUtils dmsUtils;

    @Autowired
    EncriptionConfiguration encriptionConfiguration;

    private DocumentOperationUtils() {

    }

    static Logger logger = LoggerFactory.getLogger(DocumentOperationUtils.class);

    public DocumentResponse createPassword(MultipartFile file, String pdfPassword) {
        DocumentResponse documentResponse;
        try {

            File passwordFile = FileHandling.preparePdfPassword(pdfPassword, file);

            documentResponse = new DocumentResponse().setFileResponse(new FileResponse().setFile(passwordFile).setFileName(file.getOriginalFilename()));

            FileHandling.deleteFile(passwordFile);

        } catch (Exception e) {
            logger.error(LoggerErrorConstants.CREATING_PDF_PASSWORD_ERROR, e);
            documentResponse = new DocumentResponse().setStatus(PortableConstants.FAILED).setMessage(MessageConstants.SERVICE_FAILED);
        }

        return documentResponse;
    }


    public DocumentResponse removePassword(MultipartFile file, String password) {
        DocumentResponse documentResponse;
        try {

            File decryptedFile = FileHandling.removePdfPassword(password, file);

            documentResponse = new DocumentResponse().setFileResponse(new FileResponse().setFile(decryptedFile).setFileName(file.getOriginalFilename()));

            FileHandling.deleteFile(decryptedFile);

        } catch (InvalidPasswordException pass) {
            documentResponse = new DocumentResponse().setStatus(PortableConstants.FAILED).setMessage(MessageConstants.GIVEN_PASSWORD_INVALID);
        } catch (Exception e) {
            logger.error(LoggerErrorConstants.REMOVING_PDF_PASSWORD_ERROR, e);
            documentResponse = new DocumentResponse().setStatus(PortableConstants.FAILED).setMessage(MessageConstants.SERVICE_FAILED);
        }
        return documentResponse;
    }

    public DocumentResponse compressToZip(List<MultipartFile> files) {
        DocumentResponse documentResponse = new DocumentResponse();
        try {
            String tempPath = FileHandling.prepareTempPath();
            String zipFileName = new StringBuilder("Zip").append(new Date().getTime()).append("File").append(".zip").toString();
            File tempFile = new File(StringUtils.append(tempPath, File.separator, zipFileName));

            List<String> fileNames = new ArrayList<>();

            try (OutputStream fos = new FileOutputStream(tempFile)) {
                try (ZipOutputStream zos = new ZipOutputStream(fos)) {
                    for (MultipartFile file : files) {

                        String fileName;
                        if (checkIfAlreadyFileNamePresent(fileNames, file.getOriginalFilename())) {
                            fileName = StringUtils.append(new Date().getTime(), file.getOriginalFilename());
                        } else {
                            fileName = file.getOriginalFilename();
                        }


                        zos.putNextEntry(new ZipEntry(fileName));
                        byte[] bytes = file.getBytes();
                        zos.write(bytes, 0, bytes.length);
                        zos.closeEntry();
                        fileNames.add(file.getOriginalFilename());
                    }
                }
            }

            try (InputStream inputStream = new FileInputStream(tempFile)) {
                InputStream zipFile = new ByteArrayInputStream(IOUtils.toByteArray(inputStream));
                documentResponse.setFileResponse(new FileResponse().setInputStream(zipFile)
                        .setInputStreamResource(FileResponse.getInputStreamResource(zipFile)).setFileName(zipFileName));
                documentResponse.setStatus(PortableConstants.SUCCESS);
            }
            FileHandling.deleteFile(tempFile);
        } catch (Exception ioException) {
            logger.error(LoggerErrorConstants.EXCEPTION_OCCURRED, ioException);
            documentResponse.setStatus(PortableConstants.FAILED);
            documentResponse.setMessage(MessageConstants.UNABLE_TO_COMPRESS_ZIP);
        }
        return documentResponse;
    }

    public DocumentResponse compressToZipFile(List<DocumentResponse> files) {
        DocumentResponse documentResponse = new DocumentResponse();
        try {

            List<String> fileNames = new ArrayList<>();
            if (CollectionUtils.isEmpty(files)) {
                return documentResponse.setStatus(PortableConstants.FAILED).setMessage("No Documents present");
            }

            String tempPath = FileHandling.prepareTempPath();
            String zipFileName = new StringBuilder("Zip").append(new Date().getTime()).append("File").append(".zip").toString();
            File tempFile = new File(StringUtils.append(tempPath, File.separator, zipFileName));

            try (OutputStream fos = new FileOutputStream(tempFile)) {
                try (ZipOutputStream zos = new ZipOutputStream(fos)) {
                    for (DocumentResponse file : files) {
                        if (!PortableConstants.FAILED.equalsIgnoreCase(file.getStatus())) {
                            String fileName;
                            if (checkIfAlreadyFileNamePresent(fileNames, file.getFileResponse().getFileName())) {
                                fileName = StringUtils.append(new Date().getTime(), file.getFileResponse().getFileName());
                            } else {
                                fileName = file.getFileResponse().getFileName();
                            }
                            zos.putNextEntry(new ZipEntry(fileName));
                            byte[] bytes = IOUtils.toByteArray(file.getFileResponse().getInputStream());
                            zos.write(bytes, 0, bytes.length);
                            zos.closeEntry();
                            fileNames.add(file.getFileResponse().getFileName());
                        }
                    }
                }
            }

            try (InputStream inputStream = new FileInputStream(tempFile)) {
                InputStream zipFile = new ByteArrayInputStream(IOUtils.toByteArray(inputStream));
                documentResponse.setFileResponse(new FileResponse().setInputStream(zipFile)
                        .setInputStreamResource(FileResponse.getInputStreamResource(zipFile)).setFileName(zipFileName));
                documentResponse.setStatus(PortableConstants.SUCCESS);
            }

            FileHandling.deleteFile(tempFile);
        } catch (Exception ioException) {
            logger.error(LoggerErrorConstants.EXCEPTION_OCCURRED, ioException);
            documentResponse.setStatus(PortableConstants.FAILED);
            documentResponse.setMessage(MessageConstants.UNABLE_TO_COMPRESS_ZIP);
        }
        return documentResponse;
    }

    private static boolean checkIfAlreadyFileNamePresent(List<String> fileNames, String fileName) {
        if (CollectionUtils.isEmpty(fileNames)) {
            return false;
        }

        return fileNames.stream().filter(ifileName -> ifileName != null && ifileName.equalsIgnoreCase(fileName)).findAny().orElse(null) != null;
    }

    public DocumentResponse combineMultiplePdfs(List<MultipartFile> files) throws IOException {
        DocumentResponse response = new DocumentResponse();
        PDFMergerUtility obj = new PDFMergerUtility();

        String tempPath = FileHandling.prepareTempPath();
        String fileName = FileHandling.prepareTempName("mergeDoc", "pdf");
        String tempFilePath = StringUtils.append(tempPath, File.separator, fileName);

        obj.setDestinationFileName(tempFilePath);

        for (MultipartFile file : files) {
            obj.addSource(new ByteArrayInputStream(file.getBytes()));
        }
        obj.mergeDocuments();

        File tempFile = new File(tempFilePath);
        try (InputStream inputStream = new FileInputStream(tempFile)) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(IOUtils.toByteArray(inputStream));
            response.setFileResponse(new FileResponse().setInputStream(byteArrayInputStream).
                    setInputStreamResource(FileResponse.getInputStreamResource(byteArrayInputStream)).
                    setFileName(fileName)).setStatus(PortableConstants.SUCCESS).setStatus("200");
        }
        tempFile.delete();
        return response;
    }

    public DocumentResponse combineMultiplePdfS(List<InputStream> files) throws IOException {
        DocumentResponse response = new DocumentResponse();
        PDFMergerUtility obj = new PDFMergerUtility();

        String tempPath = FileHandling.prepareTempPath();
        String fileName = FileHandling.prepareTempName("mergeDoc", "pdf");
        String tempFilePath = StringUtils.append(tempPath, File.separator, fileName);

        obj.setDestinationFileName(tempFilePath);

        for (InputStream file : files) {
            obj.addSource(file);
        }
        obj.mergeDocuments();

        File tempFile = new File(tempFilePath);
        try (InputStream inputStream = new FileInputStream(tempFile)) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(IOUtils.toByteArray(inputStream));
            response.setFileResponse(new FileResponse().setInputStream(byteArrayInputStream).
                    setInputStreamResource(FileResponse.getInputStreamResource(byteArrayInputStream)).
                    setFileName(fileName)).setStatus(PortableConstants.SUCCESS).setStatus("200");
        }
        tempFile.delete();
        return response;
    }

    public DocumentResponse splitPdf(MultipartFile file, List<Integer> pageNoInSequence, boolean wantFirstPage, boolean wantLastPage) throws IOException {
        try (PDDocument document = PDDocument.load(file.getBytes())) {

            List<PDDocument> pages = splitter.split(document);

            if (CollectionUtils.isEmpty(pages) || pages.size() == 1) {
                return new DocumentResponse(null, 200L, PortableConstants.SUCCESS, null, new FileResponse(file.getOriginalFilename(),
                        file.getInputStream(), FileResponse.getInputStreamResource(file.getBytes())));
            }

            List<Integer> sequencePageNos = new ArrayList<>();

            if (wantFirstPage) {
                sequencePageNos.add(1);
            }

            if (wantFirstPage && wantLastPage) {
                sequencePageNos.add(pages.size());
            } else {
                for (Integer pageNo : pageNoInSequence) {
                    if (pageNo <= pages.size()) {
                        sequencePageNos.add(pageNo);
                    }
                }
            }

            if (wantLastPage) {
                sequencePageNos.add(pages.size());
            }

            List<InputStream> mergedFiles = new ArrayList<>();
            Map<Integer, InputStream> fileWithPageNumber = new LinkedHashMap<>();

            for (int pageNumber = 0; pageNumber < pages.size(); pageNumber++) {
                if (sequencePageNos.contains(pageNumber + 1)) {
                    File tempFile = new File(StringUtils.append(FileHandling.prepareTempPath(), File.separator,
                            FileHandling.prepareTempName("splitPdf", "pdf")));
                    pages.get(pageNumber).save(tempFile);
                    try (InputStream inputStream = new FileInputStream(tempFile)) {
                        fileWithPageNumber.put(pageNumber + 1, new ByteArrayInputStream(IOUtils.toByteArray(inputStream)));
                    }
                    tempFile.delete();
                }
            }

            sequencePageNos.stream()
                    .distinct().forEach(sequencePageNo ->
                            mergedFiles.add(fileWithPageNumber.get(sequencePageNo))
                    );

            DocumentResponse documentResponse = combineMultiplePdfS(mergedFiles);
            if (!PortableConstants.FAILED.equalsIgnoreCase(documentResponse.getStatus())) {
                documentResponse.getFileResponse().setFileName(file.getOriginalFilename());
            }
            return documentResponse;
        }
    }

    public PortableResponse getBase64Response(DocumentResponse documentResponse) throws IOException {

        Map<String, Object> fileResponse = new HashMap<>();

        fileResponse.put("file", "data:;base64," + Base64.getEncoder().encodeToString(IOUtils.toByteArray(documentResponse.getFileResponse().getInputStream())));

        fileResponse.put("fileName", documentResponse.getFileResponse().getFileName());

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, fileResponse);
    }

    public DocumentResponse encryptAnyFile(MultipartFile file) {

        try {

            DynamicDocumentSecurity dynamicDocumentSecurity = encriptionConfiguration.getDynamicDocumentSecurity();

            InputStream encryptedLevelOneFile = new ByteArrayInputStream(encriptionConfiguration.encryptFile(file.getBytes(),
                    dynamicDocumentSecurity.getDynamicSecrectKey()));

            DocumentResponse encryptedFile = new DocumentResponse("", StatusCodes.C_200,
                    PortableConstants.SUCCESS, null, new FileResponse(file.getOriginalFilename(), encryptedLevelOneFile));

            String basicEncoded = EncoderUtils.encode(dynamicDocumentSecurity.getDynamicKey());

            String encryptedLevelOneKey = Base64.getEncoder().encodeToString(basicEncoded.getBytes());

            String encryptedLevelTwoKey = EncoderUtils.encode(encryptedLevelOneKey);

            String encryptedLevelThreeKey = Base64.getEncoder().encodeToString(encryptedLevelTwoKey.getBytes());

            String encryptedFinalLevelKey = EncoderUtils.encode(encryptedLevelThreeKey);

            File keyStoreFile = FileHandling.createTempFile("keystore.dat");

            String keyStoreFileName = keyStoreFile.getName();

            try (FileOutputStream fos = new FileOutputStream(keyStoreFile);

                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {

                oos.writeObject(encryptedFinalLevelKey);

            }

            InputStream encryptedLevelOneKeystore = FileHandling.getInputStream(keyStoreFile);

            keyStoreFile.delete();

            DocumentResponse keyStore = new DocumentResponse("", StatusCodes.C_200,
                    PortableConstants.SUCCESS, null, new FileResponse(keyStoreFileName, encryptedLevelOneKeystore));

            return compressToZipFile(Arrays.asList(keyStore, encryptedFile));

        } catch (Exception e) {
            throw new ServerException("Server Unable to process your request");
        }

    }

    public DocumentResponse decryptAnyFile(MultipartFile file) {
        DocumentResponse documentResponse = new DocumentResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, null, new FileResponse());
        InputStream fis;
        List<File> files = new ArrayList<>();
        byte[] buffer = new byte[1024];
        try {
            fis = file.getInputStream();
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {

                String fileName = ze.getName();

                File tempFile = FileHandling.createTempFile(fileName);

                logger.info(tempFile.getAbsolutePath());

                FileOutputStream fos = new FileOutputStream(tempFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                zis.closeEntry();
                ze = zis.getNextEntry();

                files.add(tempFile);
            }

            zis.closeEntry();
            zis.close();
            fis.close();

            if (CollectionUtils.isEmpty(files) || files.size() < 2) {
                throw new BadRequestException("Unable to decrypt (Please make sure u uploaded correct file)");
            }

            File keyStoreFile = null;
            File userEncryptedFile = null;
            for (File encryptedFile : files) {
                if (encryptedFile.getName().endsWith(".dat")) {
                    keyStoreFile = encryptedFile;
                } else {
                    userEncryptedFile = encryptedFile;
                }
            }

            if (keyStoreFile == null || userEncryptedFile == null) {
                throw new BadRequestException("File Corrupted");
            }

            try (FileInputStream fileIn = new FileInputStream(keyStoreFile);
                 ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {

                String key = (String) objectIn.readObject();

                String basicDecoded = EncoderUtils.decode(key);

                String decodedLevelOneKey = new String(Base64.getDecoder().decode(basicDecoded.getBytes()));

                String decodedLevelTwoKey = EncoderUtils.decode(decodedLevelOneKey);

                String decodedLevelThreeKey = new String(Base64.getDecoder().decode(decodedLevelTwoKey.getBytes()));

                String decodedFinalLevelKey = EncoderUtils.decode(decodedLevelThreeKey);

                DynamicDocumentSecurity dynamicDocumentSecurity = new DynamicDocumentSecurity();

                dynamicDocumentSecurity.setDynamicKey(decodedFinalLevelKey);

                InputStream decryptedFile = new ByteArrayInputStream(encriptionConfiguration.decryptFile(IOUtils.toByteArray(FileHandling.getInputStream(userEncryptedFile)),
                        dynamicDocumentSecurity.getDynamicSecrectKey()));

                documentResponse.getFileResponse().setInputStream(decryptedFile).setFileName(userEncryptedFile.getName());

            }

            keyStoreFile.delete();
            userEncryptedFile.delete();

        } catch (BadRequestException be) {
            throw be;
        } catch (Exception e) {
            throw new ServerException("Server Unable to process your request");
        }
        return documentResponse;
    }

    public DocumentResponse createPassword(File file, String pdfPassword) {
        DocumentResponse documentResponse;
        try {

           FileHandling.preparePdfPassword(pdfPassword, file);

            documentResponse = new DocumentResponse().setFileResponse(new FileResponse().setFile(file).setFileName(file.getName()));

            FileHandling.deleteFile(file);

        } catch (Exception e) {
            logger.error(LoggerErrorConstants.CREATING_PDF_PASSWORD_ERROR, e);
            documentResponse = new DocumentResponse().setStatus(PortableConstants.FAILED).setMessage(MessageConstants.SERVICE_FAILED);
        }

        return documentResponse;
    }


    public DocumentResponse removePassword(File file, String password) {
        DocumentResponse documentResponse;
        try {

            FileHandling.removePdfPassword(password, file);

            documentResponse = new DocumentResponse().setFileResponse(new FileResponse().setFile(file).setFileName(file.getName()));

            FileHandling.deleteFile(file);

        } catch (InvalidPasswordException pass) {
            documentResponse = new DocumentResponse().setStatus(PortableConstants.FAILED).setMessage(MessageConstants.GIVEN_PASSWORD_INVALID);
        } catch (Exception e) {
            logger.error(LoggerErrorConstants.REMOVING_PDF_PASSWORD_ERROR, e);
            documentResponse = new DocumentResponse().setStatus(PortableConstants.FAILED).setMessage(MessageConstants.SERVICE_FAILED);
        }
        return documentResponse;
    }

}
