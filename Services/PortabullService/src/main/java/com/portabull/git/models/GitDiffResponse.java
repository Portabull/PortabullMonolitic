package com.portabull.git.models;


import java.util.List;

public class GitDiffResponse {

    private String latestCommitSha;

    private String message;

    private String status = "SUCCESS";

    private List<GitDifferences> diffs;

    private int diffCount;

    private int statusCode = 200;

    private String noOfCommitsBehind = "0";


    public String getLatestCommitSha() {
        return latestCommitSha;
    }

    public void setLatestCommitSha(String latestCommitSha) {
        this.latestCommitSha = latestCommitSha;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<GitDifferences> getDiffs() {
        return diffs;
    }

    public void setDiffs(List<GitDifferences> diffs) {
        this.diffs = diffs;
    }

    public int getDiffCount() {
        return diffCount;
    }

    public void setDiffCount(int diffCount) {
        this.diffCount = diffCount;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getNoOfCommitsBehind() {
        return noOfCommitsBehind;
    }

    public void setNoOfCommitsBehind(String noOfCommitsBehind) {
        this.noOfCommitsBehind = noOfCommitsBehind;
    }


}
