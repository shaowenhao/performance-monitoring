package com.siemens.datalayer.apiservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;

public class SubscriptionKPIResult {
    @JsonProperty("HeatPumpKpiData")
    ArrayList<HeatPumpKpiData> heatPumpKpiData;


    // Getter Methods


    public ArrayList<HeatPumpKpiData> getHeatPumpKpiData() {
        return heatPumpKpiData;
    }

    // Setter Methods


    public void setHeatPumpKpiData(ArrayList<HeatPumpKpiData> dataObject) {
        this.heatPumpKpiData = dataObject;
    }
}

