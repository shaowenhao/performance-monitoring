package com.siemens.devops.monitoring.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.siemens.devops.monitoring.model.HttpRequest;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "")
@Data
public class HttpRequestsConfig {

    private List<HttpRequest> httpRequests;

}
