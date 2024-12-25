package com.portabull.git.models;


import java.io.File;

public class GitDownloadResponse {

    private String message;

    private int statusCode = 200;

    private String status = "SUCCESS";

    private String latestCommitSha;

    private byte[] gitRepositoryZip;

    private File file;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLatestCommitSha() {
        return latestCommitSha;
    }

    public void setLatestCommitSha(String latestCommitSha) {
        this.latestCommitSha = latestCommitSha;
    }

    public byte[] getGitRepositoryZip() {
        return gitRepositoryZip;
    }

    public void setGitRepositoryZip(byte[] gitRepositoryZip) {
        this.gitRepositoryZip = gitRepositoryZip;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}

