package com.siemens.devops.monitoring.controller;

import com.siemens.devops.monitoring.model.HttpRequest;
import com.siemens.devops.monitoring.model.HttpResponse;
import com.siemens.devops.monitoring.model.RestResult;
import com.siemens.devops.monitoring.service.HttpRequestService;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
			@RequestParam(value = "fixedDelayInSecond", defaultValue = "5", required = false) Integer fixedDelayInSecond,
			@RequestParam(value = "statisticExpiryInMinuteForPerformance", defaultValue = "30", required = false) Integer statisticExpiryInMinuteForPerformance,
			@RequestParam(value = "statisticExpiryInMinuteForFunction", defaultValue = "5", required = false) Integer statisticExpiryInMinuteForFunction) {
		if (!started) {
			started = true;
			logger.info("Start metrics");
			startMetrics(fixedDelayInSecond, statisticExpiryInMinuteForPerformance, statisticExpiryInMinuteForFunction);
		} else {
			logger.info("Metrics had been started");
			return RestResult.error(-1, "Metrics had been started");
		}
		return RestResult.sucess();
	}

	@ApiOperation("Stop metrics")
	@GetMapping("/stopMetrics")
	public RestResult<Object> stopTest(
			@RequestParam(value = "clearMetrics", defaultValue = "false", required = false) boolean clearMetrics) {
		if (started) {
			started = false;
			logger.info("Stop metrics");
			if (clearMetrics) {
				meterRegistry.clear();
				logger.info("Clear metrics");
				clearGaugeMap();
			}
		} else {
			logger.info("Metrics had been stopped");
			return RestResult.error(-1, "Metrics had been stopped");
		}
		return RestResult.sucess();
	}

	@ApiOperation("Clear metrics")
	@GetMapping("/clearMetrics")
	public RestResult<Object> clearMetrics() {
		meterRegistry.clear();
		logger.info("Clear metrics");
		clearGaugeMap();
		initGaugeMap();
		return RestResult.sucess();
	}

	private void startMetrics(Integer fixedDelayInSecond, Integer statisticExpiryInMinuteForPerformance,
			Integer statisticExpiryInMinuteForFunction) {
		int windowSizeForFunction = (int) (statisticExpiryInMinuteForFunction * 60 / fixedDelayInSecond);
		threadPoolTaskExecutor.execute(() -> {
			initGaugeMap();

			while (started) {
				service.getRequestList().forEach(httpRequest -> {
					HttpResponse response = service.handleRequest(httpRequest);
					Timer timer = Timer.builder("http.request")
							.tags("url", response.getUrl(), "method", response.getMethod(), "name",
									httpRequest.getName())
							.publishPercentiles(0.5, 0.9, 0.99)
							.distributionStatisticExpiry(Duration.ofMinutes(statisticExpiryInMinuteForPerformance))
							.register(meterRegistry);
					timer.record(response.getExecTime(), TimeUnit.MILLISECONDS);

					String key = generateKey(httpRequest);
					StatisticMonitorForFunction statisticMonitorForFunction = getGaugeMap().get(key);
					statisticMonitorForFunction.compute(getTimeWindowMap(), windowSizeForFunction, key,
							response.isPassed());

				});
				try {
					Thread.sleep(fixedDelayInSecond * 1000);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
		});

	}

	private Map<String, CircularFifoQueue<Integer>> timeWindowMap = new ConcurrentHashMap<>();
	private Map<String, StatisticMonitorForFunction> gaugeMap = new ConcurrentHashMap<>();

	private void clearGaugeMap() {
		this.gaugeMap.clear();
		this.timeWindowMap.clear();
	}

	private Map<String, StatisticMonitorForFunction> initGaugeMap() {
		service.getRequestList().forEach(httpRequest -> {
			String key = generateKey(httpRequest);
			StatisticMonitorForFunction statistic = new StatisticMonitorForFunction();
			List<Tag> tags = new ArrayList<>();
			Tag tag = Tag.of("url", httpRequest.getUrlWithParams());
			tags.add(tag);
			tag = Tag.of("method", httpRequest.getType());
			tags.add(tag);
			tag = Tag.of("name", httpRequest.getName());
			tags.add(tag);
			StatisticMonitorForFunction statisticMonitorForFunction = meterRegistry
					.gauge("http.request.failure.percent", tags, statistic, statistic::getGaugeValue);
			gaugeMap.put(key, statisticMonitorForFunction);
		});

		return gaugeMap;
	}

	private Map<String, StatisticMonitorForFunction> getGaugeMap() {
		return this.gaugeMap;
	}

	private Map<String, CircularFifoQueue<Integer>> getTimeWindowMap() {
		return this.timeWindowMap;
	}

	private String generateKey(HttpRequest httpRequest) {
		String key = httpRequest.getName() + httpRequest.getUrlWithParams() + httpRequest.getType();
		return key;
	}

	private static class StatisticMonitorForFunction {
		Logger logger = LoggerFactory.getLogger(this.getClass());
		private double gaugeValue;

		public void compute(Map<String, CircularFifoQueue<Integer>> timeWindowMap, int windowSize, String key,
				boolean casePassed) {
			if (!timeWindowMap.containsKey(key)) {
				timeWindowMap.put(key, new CircularFifoQueue<Integer>(windowSize));
			}
			CircularFifoQueue<Integer> queue = timeWindowMap.get(key);
			queue.offer(casePassed ? 0 : 1);
			int failureTimes = queue.stream().mapToInt(i -> (int) i).sum();
			int execTimes = queue.size();
			double percent = (double) failureTimes / (double) execTimes;
			logger.debug("key=" + key);
			logger.debug("percent=" + percent);
			logger.debug("queue=" + queue);
			gaugeValue = percent;
		}

		public double getGaugeValue(StatisticMonitorForFunction statisticMonitor) {
			return statisticMonitor.gaugeValue;
		}
	}
}
