package com.rbc.clear.dashboard.dto;

public class UploadHolidaysResult implements java.io.Serializable {
    private int totalRecords;
    private int successfulRecords;
    private int failedRecords;

    public UploadHolidaysResult(int totalRecords, int successfulRecords, int failedRecords) {
        this.totalRecords = totalRecords;
        this.successfulRecords = successfulRecords;
        this.failedRecords = failedRecords;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getSuccessfulRecords() {
        return successfulRecords;
    }

    public void setSuccessfulRecords(int successfulRecords) {
        this.successfulRecords = successfulRecords;
    }

    public int getFailedRecords() {
        return failedRecords;
    }

    public void setFailedRecords(int failedRecords) {
        this.failedRecords = failedRecords;
    }
}
