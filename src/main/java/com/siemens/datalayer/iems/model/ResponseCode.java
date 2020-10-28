package com.siemens.datalayer.iems.model;

public enum ResponseCode {
	// 成功请求
    SDL_SUCCESS("200", "OK"),
	// 服务器错误
    SDL_SERVER_ERROR("500","Server Error"),
	// 参数不合法
    SDL_PARAM_ERROR("1001", "Parameter is invalid"),
    SDL_PARAM_ERROR_DATATIMERANGE("1002", "Parameter is invalid : startTime > endTime"),
	// Entity Base
    SDL_KG_SVC_DEVICE_NOT_EXIST("102101", "Device not exist"),
    SDL_KG_SVC_SENSOR_NOT_EXIST("102102", "Sensor not exist"),
    SDL_KG_SVC_SENSOR_DATA_NOT_EXIST("102103", "Sensor data not exist"),
    
    SDL_KG_SVC_SUBSCRIPTIONS_DELETE_ERROR("102105", "Subscriptions not exist"),
    SDL_KG_SVC_SUBSCRIPTIONS_SENSOR_DATA_ERROR("102106", "Subscriptions sensor data error"),
    SDL_KG_SVC_SUBSCRIPTIONS_KPI_DATA_ERROR("102107", "Subscriptions kpi data error"),
    ;

    private String code;
    private String message;

    private ResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public String getCode() {
        return this.code;
    }
}