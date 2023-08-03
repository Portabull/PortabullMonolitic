package com.portabull.utils.validationutils;

import com.portabull.constants.MessageConstants;
import com.portabull.execption.ValidatonException;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.Parser;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class DMSHelperUtils {

    protected static Parser parser;

    protected static Tika tika;

    static {

        buildParser();

        buildTika();

    }

    public static void buildParser() {
        parser = new AutoDetectParser();
    }

    private static void buildTika() {
        tika = new Tika();
    }

    private static final String CONTENTTYPE = "Content-Type";

    private static final String APPLICATION_DOWNLOAD = "application/x-msdownload";

    private static final List<String> EXECUTABLE = Arrays.asList("application/x-executable", "application/x-msdownload; format=pe");

    private static final String APPLICATION = "application/(.*)";

    private static final String APPLICATIONORPDF = "application/pdf";

    private static final String TEXT = "text/(.*)";

    protected static void invokeCorruptedFilter(Metadata metadata, MultipartFile file) throws ValidatonException {
        String contentType = tika.detect(file.getOriginalFilename());

        for (String name : metadata.names()) {
            if (CONTENTTYPE.equalsIgnoreCase(name) && (!metadata.get(name).contains(contentType))) {
                if (!contentType.matches(APPLICATION) ||
                        (contentType.equalsIgnoreCase(APPLICATIONORPDF))) {
                    if (contentType.matches(TEXT)) {
                        throw new ValidatonException(MessageConstants.TEXT_FILE_UPLOAD);
                    }
                    throw new ValidatonException(MessageConstants.FILE_EXTENSION_CHANGED);
                }
            }
        }
    }

    protected static void invokeCorruptedFilter(Metadata metadata, File file) throws ValidatonException {
        String contentType = tika.detect(file.getName());

        for (String name : metadata.names()) {
            if (CONTENTTYPE.equalsIgnoreCase(name) && (!metadata.get(name).contains(contentType))) {
                if (!contentType.matches(APPLICATION) ||
                        (contentType.equalsIgnoreCase(APPLICATIONORPDF))) {
                    if (contentType.matches(TEXT)) {
                        throw new ValidatonException(MessageConstants.TEXT_FILE_UPLOAD);
                    }
                    throw new ValidatonException(MessageConstants.FILE_EXTENSION_CHANGED);
                }
            }
        }
    }

    protected static boolean invokeFilter(Metadata metadata, String[] metadataNames) {
        return Arrays.stream(metadataNames).filter(metadataName -> filterMetaData(metadata, metadataName)).findAny().orElse(null) != null;
    }

    private static boolean filterMetaData(Metadata metadata, String metadataName) {
        return CONTENTTYPE.equalsIgnoreCase(metadataName) && (APPLICATION_DOWNLOAD.contains(metadata.get(metadataName)) ||
                (isExe(metadata, metadataName)));
    }

    private static boolean isExe(Metadata metadata, String metadataName) {
        String contentType = metadata.get(metadataName);

        for (String exe : EXECUTABLE) {
            if (exe.contains(contentType))
                return true;
        }
        return false;
    }

}
