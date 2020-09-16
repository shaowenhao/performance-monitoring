package com.siemens.datalayer.apiservice.model;

import java.util.ArrayList;
import java.util.HashMap;

public class SubscriptionKPIResult {
    private int code;
    private String message;
    HashMap<String, ArrayList<HeatPumpKpiData>> data;


    // Getter Methods

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HashMap<String, ArrayList<HeatPumpKpiData>> getData() {
        return data;
    }

    // Setter Methods

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(HashMap<String, ArrayList<HeatPumpKpiData>> dataObject) {
        this.data = dataObject;
    }
}

