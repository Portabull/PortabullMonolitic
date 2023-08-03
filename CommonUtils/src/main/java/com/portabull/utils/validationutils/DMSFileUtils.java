package com.portabull.utils.validationutils;

import com.portabull.execption.ValidatonException;
import com.portabull.utils.fileutils.FileHandling;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.*;


public class DMSFileUtils extends DMSHelperUtils {

    public static boolean isExecutableFile(MultipartFile file) throws IOException, TikaException, SAXException {

        Metadata metadata = new Metadata();

        DMSHelperUtils.parser.parse(new ByteArrayInputStream(file.getBytes()),
                new BodyContentHandler(new StringWriter()), metadata, new ParseContext());

        return DMSHelperUtils.invokeFilter(metadata, metadata.names());
    }

    public static boolean isExecutableFile(File file) throws IOException, TikaException, SAXException {

        Metadata metadata = new Metadata();

        DMSHelperUtils.parser.parse(new ByteArrayInputStream(FileHandling.convertFileToBytes(file)),
                new BodyContentHandler(new StringWriter()), metadata, new ParseContext());

        return DMSHelperUtils.invokeFilter(metadata, metadata.names());
    }

    public static void isCorruptedFile(MultipartFile file) throws ValidatonException, IOException, TikaException, SAXException {
        Metadata metadata = new Metadata();

        DMSHelperUtils.parser.parse(new ByteArrayInputStream(file.getBytes()),
                new BodyContentHandler(new StringWriter()), metadata, new ParseContext());

        DMSHelperUtils.invokeCorruptedFilter(metadata, file);

    }

    public static void isCorruptedFile(File file) throws ValidatonException, IOException, TikaException, SAXException {
        Metadata metadata = new Metadata();

        DMSHelperUtils.parser.parse(new ByteArrayInputStream(FileHandling.convertFileToBytes(file)),
                new BodyContentHandler(new StringWriter()), metadata, new ParseContext());

        DMSHelperUtils.invokeCorruptedFilter(metadata, file);

    }

    public static String getContentType(File file) throws IOException {
        return DMSHelperUtils.tika.detect(file);
    }

    public static String getContentType(String fileName) {
        return DMSHelperUtils.tika.detect(fileName);
    }


}
