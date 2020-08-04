package com.siemens.datalayer.apiservice.model;

public class BadRequestResponse {
    private int status;
    private String message;
    private String timestamp;
    private String error;
    private String path;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getError() {
        return error;
    }

    public String getPath() {
        return path;
    }
}

