package com.portabull.utils.validationutils;

import com.portabull.constants.FileConstants;
import com.portabull.constants.MessageConstants;
import com.portabull.constants.PortableConstants;
import com.portabull.execption.ValidatonException;
import com.portabull.response.DocumentResponse;
import com.portabull.utils.fileutils.FileHandling;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.exception.TikaException;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FileValidationUtils {

    private FileValidationUtils() {

    }

    public static DocumentResponse validate(MultipartFile file) {
        DocumentResponse documentResponse;

        try {

            if (file == null) {
                throw new ValidatonException("File is Empty");
            }

            validateFileName(file);

            validateFileIsCorrupted(file);

            validateExecutableFile(file);

            documentResponse = new DocumentResponse(
                    MessageConstants.VALIDATION_SUCCESS,
                    200L, PortableConstants.SUCCESS, null);

        } catch (ValidatonException validatonException) {
            documentResponse = new DocumentResponse(
                    validatonException.getMessage(),
                    400L, PortableConstants.FAILED, null);
        }

        return documentResponse;
    }

    public static DocumentResponse validate(String file, String fileName) {
        DocumentResponse documentResponse;

        try {

            if (file == null) {
                throw new ValidatonException("File is Empty");
            }

            File multipartFile = FileHandling.convertBase64File(file, fileName);

            validateFileName(multipartFile);

            validateFileIsCorrupted(multipartFile);

            validateExecutableFile(multipartFile);

            documentResponse = new DocumentResponse(
                    MessageConstants.VALIDATION_SUCCESS,
                    200L, PortableConstants.SUCCESS, multipartFile);

        } catch (ValidatonException validatonException) {
            documentResponse = new DocumentResponse(
                    validatonException.getMessage(),
                    400L, PortableConstants.FAILED, null);
        } catch (Exception e) {
            documentResponse = new DocumentResponse(
                    "File Invalid",
                    400L, PortableConstants.FAILED, null);
        }

        return documentResponse;
    }

    public static boolean isFileIsPdf(MultipartFile file) {
        return FileConstants.PDF.equalsIgnoreCase(FilenameUtils.getExtension(file.getOriginalFilename()));
    }

    private static void validateFileIsCorrupted(MultipartFile file) throws ValidatonException {
        try {
            DMSFileUtils.isCorruptedFile(file);
        } catch (ValidatonException validatonException) {
            throw validatonException;
        } catch (IOException | TikaException | SAXException e) {
            throw new ValidatonException(MessageConstants.CORRUPTED_FILE);
        }
    }

    private static void validateFileIsCorrupted(File file) throws ValidatonException {
        try {
            DMSFileUtils.isCorruptedFile(file);
        } catch (ValidatonException validatonException) {
            throw validatonException;
        } catch (IOException | TikaException | SAXException e) {
            throw new ValidatonException(MessageConstants.CORRUPTED_FILE);
        }
    }

    private static void validateExecutableFile(MultipartFile file) throws ValidatonException {
        boolean isExecutableFile;

        try {
            isExecutableFile = DMSFileUtils.isExecutableFile(file);
        } catch (IOException | TikaException | SAXException e) {
            throw new ValidatonException(MessageConstants.CORRUPTED_FILE);
        }

        if (isExecutableFile) {
            throw new ValidatonException(MessageConstants.EXECUTABLE_FILE);
        }
    }

    private static void validateExecutableFile(File file) throws ValidatonException {
        boolean isExecutableFile;

        try {
            isExecutableFile = DMSFileUtils.isExecutableFile(file);
        } catch (IOException | TikaException | SAXException e) {
            throw new ValidatonException(MessageConstants.CORRUPTED_FILE);
        }

        if (isExecutableFile) {
            throw new ValidatonException(MessageConstants.EXECUTABLE_FILE);
        }
    }

    private static void validateFileName(MultipartFile file) throws ValidatonException {
        List<String> extensionList;

        if (StringUtils.isEmpty(file.getOriginalFilename())) {
            throw new ValidatonException(MessageConstants.FILE_NAME_EMPTY);
        }

        if (!file.getOriginalFilename().contains(".")) {
            throw new ValidatonException(MessageConstants.FILE_EXTENSION_EMPTY);
        }

        extensionList = Arrays.asList(file.getOriginalFilename().substring(file.getOriginalFilename().indexOf('.') + 1).split("\\."));

        if (extensionList.size() > 1) {
            throw new ValidatonException(MessageConstants.FILE_CONTAINS_MULTIPLE_EXTENSION);
        }
    }

    private static void validateFileName(File file) throws ValidatonException {
        List<String> extensionList;

        if (StringUtils.isEmpty(file.getName())) {
            throw new ValidatonException(MessageConstants.FILE_NAME_EMPTY);
        }

        if (!file.getName().contains(".")) {
            throw new ValidatonException(MessageConstants.FILE_EXTENSION_EMPTY);
        }

        extensionList = Arrays.asList(file.getName().substring(file.getName().indexOf('.') + 1).split("\\."));

        if (extensionList.size() > 1) {
            throw new ValidatonException(MessageConstants.FILE_CONTAINS_MULTIPLE_EXTENSION);
        }
    }


}
