package com.siemens.datalayer.connector.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"avg_irradiance",
"city",
"voltage_degree",
"commissioning_date",
"type",
"power_station",
"capacity",
"avg_annual_eq_hours",
"sys_engaged_date",
"name",
"irradiance",
"grid_inject_production",
"location",
"id",
"state"
})
public class Schemas {

@JsonProperty("avg_irradiance")
private String avgIrradiance;
@JsonProperty("city")
private String city;
@JsonProperty("voltage_degree")
private String voltageDegree;
@JsonProperty("commissioning_date")
private String commissioningDate;
@JsonProperty("type")
private String type;
@JsonProperty("power_station")
private String powerStation;
@JsonProperty("capacity")
private String capacity;
@JsonProperty("avg_annual_eq_hours")
private String avgAnnualEqHours;
@JsonProperty("sys_engaged_date")
private String sysEngagedDate;
@JsonProperty("name")
private String name;
@JsonProperty("irradiance")
private String irradiance;
@JsonProperty("grid_inject_production")
private String gridInjectProduction;
@JsonProperty("location")
private String location;
@JsonProperty("id")
private String id;
@JsonProperty("state")
private String state;

@JsonProperty("avg_irradiance")
public String getAvgIrradiance() {
return avgIrradiance;
}

@JsonProperty("avg_irradiance")
public void setAvgIrradiance(String avgIrradiance) {
this.avgIrradiance = avgIrradiance;
}

@JsonProperty("city")
public String getCity() {
return city;
}

@JsonProperty("city")
public void setCity(String city) {
this.city = city;
}

@JsonProperty("voltage_degree")
public String getVoltageDegree() {
return voltageDegree;
}

@JsonProperty("voltage_degree")
public void setVoltageDegree(String voltageDegree) {
this.voltageDegree = voltageDegree;
}

@JsonProperty("commissioning_date")
public String getCommissioningDate() {
return commissioningDate;
}

@JsonProperty("commissioning_date")
public void setCommissioningDate(String commissioningDate) {
this.commissioningDate = commissioningDate;
}

@JsonProperty("type")
public String getType() {
return type;
}

@JsonProperty("type")
public void setType(String type) {
this.type = type;
}

@JsonProperty("power_station")
public String getPowerStation() {
return powerStation;
}

@JsonProperty("power_station")
public void setPowerStation(String powerStation) {
this.powerStation = powerStation;
}

@JsonProperty("capacity")
public String getCapacity() {
return capacity;
}

@JsonProperty("capacity")
public void setCapacity(String capacity) {
this.capacity = capacity;
}

@JsonProperty("avg_annual_eq_hours")
public String getAvgAnnualEqHours() {
return avgAnnualEqHours;
}

@JsonProperty("avg_annual_eq_hours")
public void setAvgAnnualEqHours(String avgAnnualEqHours) {
this.avgAnnualEqHours = avgAnnualEqHours;
}

@JsonProperty("sys_engaged_date")
public String getSysEngagedDate() {
return sysEngagedDate;
}

@JsonProperty("sys_engaged_date")
public void setSysEngagedDate(String sysEngagedDate) {
this.sysEngagedDate = sysEngagedDate;
}

@JsonProperty("name")
public String getName() {
return name;
}

@JsonProperty("name")
public void setName(String name) {
this.name = name;
}

@JsonProperty("irradiance")
public String getIrradiance() {
return irradiance;
}

@JsonProperty("irradiance")
public void setIrradiance(String irradiance) {
this.irradiance = irradiance;
}

@JsonProperty("grid_inject_production")
public String getGridInjectProduction() {
return gridInjectProduction;
}

@JsonProperty("grid_inject_production")
public void setGridInjectProduction(String gridInjectProduction) {
this.gridInjectProduction = gridInjectProduction;
}

@JsonProperty("location")
public String getLocation() {
return location;
}

@JsonProperty("location")
public void setLocation(String location) {
this.location = location;
}

@JsonProperty("id")
public String getId() {
return id;
}

@JsonProperty("id")
public void setId(String id) {
this.id = id;
}

@JsonProperty("state")
public String getState() {
return state;
}

@JsonProperty("state")
public void setState(String state) {
this.state = state;
}

}