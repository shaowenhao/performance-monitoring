package com.siemens.devops.monitoring.model;

import lombok.Data;

@Data
public class HttpResponse {
    private String url;
    private String method;
    private String statusCode;
    private String errMsg;
    private String timestamp;
    private long execTime;
    private Boolean timeout;
}
