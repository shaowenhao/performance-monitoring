package com.siemens.devops.monitoring.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Configuration
public class MicrometerConfiguration {

//	@Value("${duration_s}")
//	private int durationInSecond;

//	@Bean
//	MeterRegistryCustomizer meterRegistryCustomizer(MeterRegistry meterRegistry) {
//		return meterRegistry1 -> {
//			meterRegistry1.config().commonTags("application", "micrometer-app");
//		};
//	}

}
