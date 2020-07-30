package com.siemens.datalayer.apiengine.model;

public class EntitiesApiResponse {
    private int code;
    private String message;
    Object data;


    // Getter Methods

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    // Setter Methods

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(Object dataObject) {
        this.data = dataObject;
    }
}

