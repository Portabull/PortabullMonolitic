package com.portabull.response;

import com.portabull.constants.MessageConstants;
import com.portabull.constants.PortableConstants;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileResponse {

    private String fileName;

    private String filePath;

    private String absolutePath;

    private byte[] bytes;

    private InputStream inputStream;

    private InputStreamResource inputStreamResource;

    private File file;

    private String message;

    private String status;

    private Long contentLength;

    private String contentType;

    private MediaType mediaType;

    public String getContentType() {
        return contentType;
    }

    public FileResponse() {

    }

    public FileResponse(String fileName, InputStream inputStream, InputStreamResource inputStreamResource) throws IOException {
        this.fileName = fileName;
        if (this.filePath != null) {
            this.setAbsolutePath(fileName + this.filePath);
        }

        this.inputStream = inputStream;
        this.setContentLength(Long.valueOf(inputStream.available()));
        this.setInputStreamResource(getInputStreamResource(inputStream));

        this.inputStreamResource = inputStreamResource;
    }

    public FileResponse(String fileName, InputStream inputStream) throws IOException {
        this.fileName = fileName;
        this.inputStream = inputStream;
        this.setContentLength(Long.valueOf(inputStream.available()));
    }

    public FileResponse setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public Long getContentLength() {
        return contentLength;
    }

    public FileResponse setContentLength(Long contentLength) {
        this.contentLength = contentLength;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public FileResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getStatus() {
        if (StringUtils.isEmpty(this.fileName) ||
                ObjectUtils.isEmpty(this.inputStreamResource) ||
                ObjectUtils.isEmpty(this.mediaType)) {
            this.message = "Cannot Download without mediaType/fileName/inputStreamResource";
            return PortableConstants.FAILED;
        }

        return status;
    }

    public FileResponse setStatus(String status) {
        this.status = status;
        return this;
    }

    public File getFile() {
        return file;
    }

    public FileResponse setFile(File file) throws IOException {
        if (!file.exists()) throw new FileNotFoundException();
        this.file = file;

        this.setInputStreamResource(getInputStreamResource(file));
        this.setInputStream(getInputStream(file));
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public FileResponse setFileName(String fileName) throws IOException {
        this.fileName = fileName;
        if (this.filePath != null) {
            this.setAbsolutePath(fileName + this.filePath);
        }
        return this;
    }

    public String getFilePath() {
        return filePath;
    }

    public FileResponse setFilePath(String filePath) throws IOException {
        this.filePath = filePath;

        if (this.fileName != null) {
            this.setAbsolutePath(this.fileName + filePath);
        }
        return this;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public FileResponse setAbsolutePath(String absolutePath) throws IOException {
        this.absolutePath = absolutePath;
        this.setMediaType(getMediaType(absolutePath));
        return this;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public FileResponse setBytes(byte[] bytes) {
        if (bytes == null)
            return this;
        this.bytes = bytes;
        this.setContentLength(Long.valueOf(bytes.length));
        return this;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public FileResponse setInputStream(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;
        this.setContentLength(Long.valueOf(inputStream.available()));
        this.setInputStreamResource(getInputStreamResource(inputStream));
        return this;
    }

    public InputStreamResource getInputStreamResource() {
        return inputStreamResource;
    }

    public FileResponse setInputStreamResource(InputStreamResource inputStreamResource) {
        this.inputStreamResource = inputStreamResource;
        return this;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public FileResponse setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    public static FileResponse createFileResponse(String fileName, String filePath) {
        FileResponse fileResponse = new FileResponse();
        try {
            fileResponse.setFileName(fileName).setFilePath(filePath).setFile(new File(fileResponse.getAbsolutePath()));
        } catch (FileNotFoundException fileNotFoundException) {
            return fileResponse.setMessage(MessageConstants.FILE_NOT_FOUND).setStatus(PortableConstants.FAILED);
        } catch (IOException e) {
            return fileResponse.setMessage("").setStatus(PortableConstants.FAILED);
        }
        return fileResponse.setStatus(PortableConstants.SUCCESS);
    }

    public static FileResponse createFileResponse(String absolutePath) {
        FileResponse fileResponse = new FileResponse();
        try {
            fileResponse.setAbsolutePath(absolutePath).setFile(new File(fileResponse.getAbsolutePath()));
        } catch (FileNotFoundException fileNotFoundException) {
            return fileResponse.setMessage(MessageConstants.FILE_NOT_FOUND).setStatus(PortableConstants.FAILED);
        } catch (IOException e) {
            return fileResponse.setMessage("").setStatus(PortableConstants.FAILED);
        }
        return fileResponse.setStatus(PortableConstants.SUCCESS);
    }

    public static FileResponse createFileResponse(File file) {
        try {
            return new FileResponse()
                    .setFile(file)
                    .setStatus(PortableConstants.SUCCESS)
                    .setFileName(file.getName())
                    .setAbsolutePath(file.getPath());
        } catch (FileNotFoundException fileNotFoundException) {
            return new FileResponse().setMessage(MessageConstants.FILE_NOT_FOUND).setStatus(PortableConstants.FAILED);
        } catch (IOException e) {
            return new FileResponse().setMessage("").setStatus(PortableConstants.FAILED);
        }
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        return IOUtils.toByteArray(inputStream);
    }

    public static byte[] getBytes(String absolutePath) throws IOException {
        return IOUtils.toByteArray(new FileInputStream(absolutePath));
    }

    public static byte[] getBytes(File file) throws IOException {
        return IOUtils.toByteArray(new FileInputStream(file));
    }

    public static String getContentType(File file) throws IOException {
        return file.toURL().openConnection().getContentType();
    }

    public static Long getFileLength(File file) {
        return file.length();
    }

    public static InputStream getInputStream(File file) throws IOException {
        try (InputStream inputStream = new FileInputStream(file)) {
            return new ByteArrayInputStream(IOUtils.toByteArray(inputStream));
        }
    }

    public static InputStream getInputStream(byte[] bytes) {
        return new ByteArrayInputStream(bytes);
    }

    public static InputStreamResource getInputStreamResource(InputStream inputStream) {
        return new InputStreamResource(inputStream);
    }

    public static InputStreamResource getInputStreamResource(byte[] bytes) {
        return new InputStreamResource(new ByteArrayInputStream(bytes));
    }

    public static MediaType getMediaType(String absoluatePath) throws IOException {
        String mimeType = Files.probeContentType(Paths.get(absoluatePath));

        if (StringUtils.isEmpty(mimeType) || "null".equalsIgnoreCase(mimeType)) {
            File file = new File(absoluatePath);
            Path source = Paths.get(absoluatePath);
            MimetypesFileTypeMap m = new MimetypesFileTypeMap(source.toString());
            mimeType = m.getContentType(file);
        }

        return MediaType
                .parseMediaType(mimeType);
    }

    public static InputStreamResource getInputStreamResource(File file) throws IOException {
        return getInputStreamResource(getInputStream(file));
    }


}
