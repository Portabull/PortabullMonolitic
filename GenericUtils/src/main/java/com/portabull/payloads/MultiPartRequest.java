package com.portabull.payloads;

import org.springframework.lang.NonNull;

import java.io.File;
import java.io.InputStream;

public class MultiPartRequest {

    @NonNull
    private String fileNameWithExtension;

    private byte[] bytes;

    private File file;

    private InputStream inputStream;

    @NonNull
    private String contentType;

    @NonNull
    private String type;

    @NonNull
    private String parameterName;

    public String getFileNameWithExtension() {
        return fileNameWithExtension;
    }

    public void setFileNameWithExtension(String fileNameWithExtension) {
        this.fileNameWithExtension = fileNameWithExtension;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @NonNull
    public String getContentType() {
        return contentType;
    }

    public void setContentType(@NonNull String contentType) {
        this.contentType = contentType;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }

    @NonNull
    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(@NonNull String parameterName) {
        this.parameterName = parameterName;
    }
}
