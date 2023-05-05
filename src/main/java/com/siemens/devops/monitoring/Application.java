package com.siemens.devops.monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.JvmMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.Log4J2MetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.LogbackMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.SystemMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.task.TaskExecutorMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.web.servlet.WebMvcMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.web.tomcat.TomcatMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication(exclude = { LogbackMetricsAutoConfiguration.class, JvmMetricsAutoConfiguration.class,
		SystemMetricsAutoConfiguration.class, Log4J2MetricsAutoConfiguration.class,
		TomcatMetricsAutoConfiguration.class, TaskExecutorMetricsAutoConfiguration.class,
		WebMvcMetricsAutoConfiguration.class })
@ServletComponentScan
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
