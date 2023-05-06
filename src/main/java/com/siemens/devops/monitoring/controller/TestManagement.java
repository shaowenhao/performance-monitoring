package com.siemens.devops.monitoring.controller;

import com.siemens.devops.monitoring.model.HttpResponse;
import com.siemens.devops.monitoring.model.RestResult;
import com.siemens.devops.monitoring.service.HttpRequestService;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import io.prometheus.client.Gauge;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@RestController
@Api(tags = "Test Management")
public class TestManagement {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private HttpRequestService service;

	@Autowired
	private MeterRegistry meterRegistry;

	@Autowired
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;

	private volatile boolean started = false;

	@ApiOperation("Start metrics")
	@GetMapping("/startMetrics")
	public RestResult<Object> startTest(
			@RequestParam(value = "fixedDelayInSecond", defaultValue = "10", required = false) Integer fixedDelayInSecond,
			@RequestParam(value = "statisticExpiryInMinute", defaultValue = "60", required = false) Integer statisticExpiryInMinute) {
		if (!started) {
			started = true;
			logger.info("Start metrics");
			startMetrics(fixedDelayInSecond, statisticExpiryInMinute);
		} else {
			logger.info("Metrics has been started");
		}
		return RestResult.sucess();
	}

	@ApiOperation("Stop metrics")
	@GetMapping("/stopMetrics")
	public RestResult<Object> stopTest() {
		if (started) {
			started = false;
			logger.info("Stop metrics");
		} else {
			logger.info("Metrics has been stopped");
		}
		return RestResult.sucess();
	}

	private void startMetrics(Integer fixedDelayInSecond, Integer statisticExpiryInMinute) {
		threadPoolTaskExecutor.execute(() -> {
			while (started) {
				service.getRequestList().forEach(httpRequest -> {
					HttpResponse response = service.handleRequest(httpRequest);
					Timer timer = Timer.builder("http.request")
							.tags("url", response.getUrl(), "method", response.getMethod(), "name",
									httpRequest.getName())
							.publishPercentiles(0.5, 0.9, 0.95, 0.99)
							.distributionStatisticExpiry(Duration.ofMinutes(statisticExpiryInMinute))
							.register(meterRegistry);
					timer.record(response.getExecTime(), TimeUnit.MILLISECONDS);

				});
				try {
					Thread.sleep(fixedDelayInSecond * 1000);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
		});

	}

}
