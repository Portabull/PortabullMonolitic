package com.portabull.payloads;

public class PageLimit {

    private int startPage;

    private int endPage;

    private boolean hasPageLimit;

    private int noOfRecords;

    private int maxResults;

    public PageLimit() {
        this.hasPageLimit = false;
    }

    public PageLimit(Integer startPage, Integer endPage) {

        this.hasPageLimit = false;

        if (startPage != null && endPage != null) {
            this.startPage = startPage;
            this.endPage = endPage;
            this.noOfRecords = this.endPage - this.startPage;
            this.maxResults = this.endPage - this.startPage;
            this.hasPageLimit = this.endPage > 0;
            return;
        }

        if (endPage != null) {
            this.startPage = 0;
            this.endPage = endPage;
            this.noOfRecords = this.endPage - this.startPage;
            this.maxResults = this.endPage - this.startPage;
            this.hasPageLimit = this.endPage > 0;
        }

    }

    public PageLimit(int endPage) {
        this.startPage = 0;
        this.endPage = endPage;
        this.noOfRecords = this.endPage - this.startPage;
        this.maxResults = this.endPage - this.startPage;
        this.hasPageLimit = this.endPage > 0;
    }

    public int getStartPage() {
        return startPage;
    }

    public PageLimit setStartPage(int startPage) {
        this.startPage = startPage;
        return this;
    }

    public int getEndPage() {
        return endPage;
    }

    public PageLimit setEndPage(int endPage) {
        this.endPage = endPage;
        this.noOfRecords = this.endPage - this.startPage;
        this.maxResults = this.endPage - this.startPage;
        this.hasPageLimit = this.endPage > 0;
        return this;
    }

    public boolean hasPageLimit() {
        return hasPageLimit;
    }

    public PageLimit getDefaultPageLimit() {
        this.startPage = 0;
        this.endPage = 20;
        this.noOfRecords = this.endPage - this.startPage;
        this.maxResults = this.endPage - this.startPage;
        return this;
    }

    public int getNoOfRecords() {
        return noOfRecords;
    }

    public int getMaxResults() {
        return maxResults;
    }
}
