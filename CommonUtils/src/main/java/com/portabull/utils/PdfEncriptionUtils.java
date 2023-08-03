package com.portabull.utils;

import com.portabull.utils.fileutils.FileHandling;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.file.Files;

@Component
public class PdfEncriptionUtils {

    private PdfEncriptionUtils() {

    }

    /**
     * It will prepare pdf password
     *
     * @param fileInputStream
     * @param password
     * @return
     * @throws IOException
     */

    public static InputStream protectPdfPassword(InputStream fileInputStream, String password) throws IOException {

        if (StringUtils.isEmpty(password) || fileInputStream == null)
            return fileInputStream;

        InputStream inputStream;
        File tempFile;
        try (PDDocument pdDocument = PDDocument.load(fileInputStream)) {
            AccessPermission accessPermission = new AccessPermission();

            StandardProtectionPolicy standardProtectionPolicy
                    = new StandardProtectionPolicy(password, password, accessPermission);

            standardProtectionPolicy.setEncryptionKeyLength(128);

            standardProtectionPolicy.setPermissions(accessPermission);

            pdDocument.protect(standardProtectionPolicy);

            tempFile = FileHandling.createTempFile("passwordProtected", "pdf");

            pdDocument.save(tempFile);

            inputStream = getInputStream(tempFile);
            Files.delete(tempFile.toPath());
        }

        return inputStream;
    }

    public static InputStream getInputStream(File file) throws IOException {
        InputStream inputStream;
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            inputStream = new ByteArrayInputStream(IOUtils.toByteArray(fileInputStream));
        }
        return inputStream;
    }

    /**
     * It will remove the pdf password
     *
     * @param file
     * @param password
     * @return
     * @throws IOException
     */

    public static InputStream removePdfPassword(InputStream file, String password) throws IOException {
        File tempFile;
        InputStream inputStream;
        try (PDDocument pdDocument = PDDocument.load(file, password)) {
            pdDocument.setAllSecurityToBeRemoved(true);

            tempFile = FileHandling.createTempFile("decrypted", "pdf");

            pdDocument.save(tempFile);

            inputStream = getInputStream(tempFile);

            Files.delete(tempFile.toPath());
        }
        return inputStream;

    }


}
