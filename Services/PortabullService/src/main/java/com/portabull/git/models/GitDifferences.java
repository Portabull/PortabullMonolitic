package com.portabull.git.models;


public class GitDifferences {

    private String newFilePath;

    private String oldFilePath;

    private String status;

    public String getNewFilePath() {
        return newFilePath;
    }

    public void setNewFilePath(String newFilePath) {
        this.newFilePath = newFilePath;
    }

    public String getOldFilePath() {
        return oldFilePath;
    }

    public void setOldFilePath(String oldFilePath) {
        this.oldFilePath = oldFilePath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}