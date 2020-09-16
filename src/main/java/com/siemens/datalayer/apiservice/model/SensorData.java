package com.siemens.datalayer.apiservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;

public class SensorData {
    @JsonProperty("Siid")
    private int Siid;
    private float value;
    private String updateTime;


    // Getter Methods

    public int getSiid() {
        return Siid;
    }

    public float getValue() {
        return value;
    }


    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    // Setter Methods

    public void setSiid(int siid) {
        this.Siid = siid;
    }

    public void setValue(float value) {
        this.value = value;
    }

}
