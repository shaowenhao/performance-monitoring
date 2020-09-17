package com.siemens.datalayer.apiservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;

public class SubscriptionSp5Result {
    @JsonProperty("SensorData")
    ArrayList<SensorData> SensorData;


    // Getter Methods


    public ArrayList<SensorData> getSensorData() {
        return SensorData;
    }

    // Setter Methods

    public void setSensorData(ArrayList<SensorData> dataObject) {
        this.SensorData = dataObject;
    }
}

