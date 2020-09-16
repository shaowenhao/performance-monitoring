package com.siemens.datalayer.apiservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;

public class HeatPumpKpiData {

    private String deviceName;
    private String updateTime;
    private String compressor_poly_efficiency = null;
    private String condensor_hot_outlet_pressure = null;
    private String condensor_hot_outlet_temperature = null;
    private String condensor_hot_sat_temperature = null;
    private String condensor_water_inlet_temperature = null;
    private String condensor_water_outlet_temperature = null;
    private String dt_condensor = null;
    private String dt_evaporator = null;
    private String evaporator_cold_inlet_pressure = null;
    private String evaporator_cold_inlet_temperature = null;
    private String evaporator_heat = null;
    private String evaporator_water_back_temperature = null;
    private String evaporator_water_leave_temperature = null;
    private float lubricate_press_diff;
    private float motor_current_percent;
    private String motor_work = null;
    private float oil_tank_press_high;
    private float oil_tank_press_low;
    private float run_time;
    private float start_up_time;


    // Getter Methods

    public String getDeviceName() {
        return deviceName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getCompressor_poly_efficiency() {
        return compressor_poly_efficiency;
    }

    public String getCondensor_hot_outlet_pressure() {
        return condensor_hot_outlet_pressure;
    }

    public String getCondensor_hot_outlet_temperature() {
        return condensor_hot_outlet_temperature;
    }

    public String getCondensor_hot_sat_temperature() {
        return condensor_hot_sat_temperature;
    }

    public String getCondensor_water_inlet_temperature() {
        return condensor_water_inlet_temperature;
    }

    public String getCondensor_water_outlet_temperature() {
        return condensor_water_outlet_temperature;
    }

    public String getDt_condensor() {
        return dt_condensor;
    }

    public String getDt_evaporator() {
        return dt_evaporator;
    }

    public String getEvaporator_cold_inlet_pressure() {
        return evaporator_cold_inlet_pressure;
    }

    public String getEvaporator_cold_inlet_temperature() {
        return evaporator_cold_inlet_temperature;
    }

    public String getEvaporator_heat() {
        return evaporator_heat;
    }

    public String getEvaporator_water_back_temperature() {
        return evaporator_water_back_temperature;
    }

    public String getEvaporator_water_leave_temperature() {
        return evaporator_water_leave_temperature;
    }

    public float getLubricate_press_diff() {
        return lubricate_press_diff;
    }

    public float getMotor_current_percent() {
        return motor_current_percent;
    }

    public String getMotor_work() {
        return motor_work;
    }

    public float getOil_tank_press_high() {
        return oil_tank_press_high;
    }

    public float getOil_tank_press_low() {
        return oil_tank_press_low;
    }

    public float getRun_time() {
        return run_time;
    }

    public float getStart_up_time() {
        return start_up_time;
    }

    // Setter Methods

    public void setDeviceName( String deviceName ) {
        this.deviceName = deviceName;
    }

    public void setUpdateTime( String updateTime ) {
        this.updateTime = updateTime;
    }

    public void setCompressor_poly_efficiency( String compressor_poly_efficiency ) {
        this.compressor_poly_efficiency = compressor_poly_efficiency;
    }

    public void setCondensor_hot_outlet_pressure( String condensor_hot_outlet_pressure ) {
        this.condensor_hot_outlet_pressure = condensor_hot_outlet_pressure;
    }

    public void setCondensor_hot_outlet_temperature( String condensor_hot_outlet_temperature ) {
        this.condensor_hot_outlet_temperature = condensor_hot_outlet_temperature;
    }

    public void setCondensor_hot_sat_temperature( String condensor_hot_sat_temperature ) {
        this.condensor_hot_sat_temperature = condensor_hot_sat_temperature;
    }

    public void setCondensor_water_inlet_temperature( String condensor_water_inlet_temperature ) {
        this.condensor_water_inlet_temperature = condensor_water_inlet_temperature;
    }

    public void setCondensor_water_outlet_temperature( String condensor_water_outlet_temperature ) {
        this.condensor_water_outlet_temperature = condensor_water_outlet_temperature;
    }

    public void setDt_condensor( String dt_condensor ) {
        this.dt_condensor = dt_condensor;
    }

    public void setDt_evaporator( String dt_evaporator ) {
        this.dt_evaporator = dt_evaporator;
    }

    public void setEvaporator_cold_inlet_pressure( String evaporator_cold_inlet_pressure ) {
        this.evaporator_cold_inlet_pressure = evaporator_cold_inlet_pressure;
    }

    public void setEvaporator_cold_inlet_temperature( String evaporator_cold_inlet_temperature ) {
        this.evaporator_cold_inlet_temperature = evaporator_cold_inlet_temperature;
    }

    public void setEvaporator_heat( String evaporator_heat ) {
        this.evaporator_heat = evaporator_heat;
    }

    public void setEvaporator_water_back_temperature( String evaporator_water_back_temperature ) {
        this.evaporator_water_back_temperature = evaporator_water_back_temperature;
    }

    public void setEvaporator_water_leave_temperature( String evaporator_water_leave_temperature ) {
        this.evaporator_water_leave_temperature = evaporator_water_leave_temperature;
    }

    public void setLubricate_press_diff( float lubricate_press_diff ) {
        this.lubricate_press_diff = lubricate_press_diff;
    }

    public void setMotor_current_percent( float motor_current_percent ) {
        this.motor_current_percent = motor_current_percent;
    }

    public void setMotor_work( String motor_work ) {
        this.motor_work = motor_work;
    }

    public void setOil_tank_press_high( float oil_tank_press_high ) {
        this.oil_tank_press_high = oil_tank_press_high;
    }

    public void setOil_tank_press_low( float oil_tank_press_low ) {
        this.oil_tank_press_low = oil_tank_press_low;
    }

    public void setRun_time( float run_time ) {
        this.run_time = run_time;
    }

    public void setStart_up_time( float start_up_time ) {
        this.start_up_time = start_up_time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        HeatPumpKpiData data = (HeatPumpKpiData) o;

        return new EqualsBuilder()
                .append(deviceName, data.deviceName)
                .append(updateTime, data.updateTime)
                .append(compressor_poly_efficiency, data.compressor_poly_efficiency)
                .append(condensor_hot_outlet_pressure, data.condensor_hot_outlet_pressure)
                .append(condensor_hot_outlet_temperature, data.condensor_hot_outlet_temperature)
                .append(condensor_hot_sat_temperature, data.condensor_hot_sat_temperature)
                .append(condensor_water_inlet_temperature, data.condensor_water_inlet_temperature)
                .append(condensor_water_outlet_temperature, data.condensor_water_outlet_temperature)
                .append(dt_condensor, data.dt_condensor)
                .append(dt_evaporator, data.dt_evaporator)
                .append(evaporator_cold_inlet_pressure, data.evaporator_cold_inlet_pressure)
                .append(evaporator_cold_inlet_temperature, data.evaporator_cold_inlet_temperature)
                .append(dt_condensor, data.dt_condensor)
                .append(dt_condensor, data.dt_condensor)
                .append(dt_condensor, data.dt_condensor)
                .append(dt_condensor, data.dt_condensor)
                .isEquals();
    }

}
