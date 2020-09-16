package com.siemens.datalayer.apiservice.model;

import java.util.ArrayList;
import java.util.HashMap;

public class SubscriptionSp5Result {
    private int code;
    private String message;
    HashMap<String, ArrayList<SensorData>> data;


    // Getter Methods

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HashMap<String, ArrayList<SensorData>> getData() {
        return data;
    }

    // Setter Methods

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(HashMap<String, ArrayList<SensorData>> dataObject) {
        this.data = dataObject;
    }
}

