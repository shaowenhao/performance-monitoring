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
			@RequestParam(value = "fixedDelayInSecond", defaultValue = "5", required = true) Integer fixedDelayInSecond,
			@RequestParam(value = "expiryInMinuteForExecTimeQuantile", defaultValue = "60", required = true) Integer expiryInMinuteForExecTimeQuantile,
			@RequestParam(value = "expiryInMinuteForExecTime", defaultValue = "5", required = true) Integer expiryInMinuteForExecTime,
			@RequestParam(value = "expiryInMinuteForFailurePercent", defaultValue = "5", required = true) Integer expiryInMinuteForFailurePercent) {
		if (!started) {
			started = true;
			logger.info("Start metrics");
			startMetrics(fixedDelayInSecond, expiryInMinuteForExecTimeQuantile, expiryInMinuteForExecTime,
					expiryInMinuteForFailurePercent);
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

	private void startMetrics(Integer fixedDelayInSecond, Integer expiryInMinuteForExecTimeQuantile,
			Integer expiryInMinuteForExecTime, Integer expiryInMinuteForFailurePercent) {
		int windowSizeForFailurePercent = (int) (expiryInMinuteForFailurePercent * 60 / fixedDelayInSecond);
		int windowSizeForExecTimeAvg = (int) (expiryInMinuteForExecTime * 60 / fixedDelayInSecond);
		int windowSizeForExecTimeMax = windowSizeForExecTimeAvg;
		int windowSizeForExecTimeMin = windowSizeForExecTimeAvg;
		initGaugeMap();
		threadPoolTaskExecutor.execute(() -> {
			while (started) {
				service.getRequestList().forEach(httpRequest -> {
					if (httpRequest.isMonitorPerformance() || httpRequest.isMonitorFunction()) {
						String key = generateKey(httpRequest);
						HttpResponse response = service.handleRequest(httpRequest);
						if (httpRequest.isMonitorPerformance()) {
							Timer timer = Timer.builder("http.request")
									.tags("url", response.getUrl(), "method", response.getMethod(), "name",
											httpRequest.getName())
									.publishPercentiles(0.5, 0.9, 0.95, 0.99)
									.distributionStatisticExpiry(Duration.ofMinutes(expiryInMinuteForExecTimeQuantile))
									.register(meterRegistry);
							timer.record(response.getExecTime(), TimeUnit.MILLISECONDS);

							GaugeForExecTimeAvg gaugeForExecTimeAvg = getGaugeMapForExecTimeAvg().get(key);
							if (gaugeForExecTimeAvg != null) {
								gaugeForExecTimeAvg.compute(getTimeWindowMapForExecTimeAvg(), windowSizeForExecTimeAvg,
										key, response.getExecTime());

							} else {
								logger.error("Does not exist gaugeForExecTimeAvg, key=" + key);
							}

							GaugeForExecTimeMax gaugeForExecTimeMax = getGaugeMapForExecTimeMax().get(key);
							if (gaugeForExecTimeMax != null) {
								gaugeForExecTimeMax.compute(getTimeWindowMapForExecTimeMax(), windowSizeForExecTimeMax,
										key, response.getExecTime());

							} else {
								logger.error("Does not exist gaugeForExecTimeMax, key=" + key);
							}

							GaugeForExecTimeMin gaugeForExecTimeMin = getGaugeMapForExecTimeMin().get(key);
							if (gaugeForExecTimeMin != null) {
								gaugeForExecTimeMin.compute(getTimeWindowMapForExecTimeMin(), windowSizeForExecTimeMin,
										key, response.getExecTime());

							} else {
								logger.error("Does not exist gaugeForExecTimeMin, key=" + key);
							}
						}
						if (httpRequest.isMonitorFunction()) {
							GaugeForFailurePercent gaugeForFailurePercent = getGaugeMapForFailurePercent().get(key);
							if (gaugeForFailurePercent != null) {
								gaugeForFailurePercent.compute(getTimeWindowMapForFailurePercent(),
										windowSizeForFailurePercent, key, response.isPassed());
							} else {
								logger.error("Does not exist gaugeForFailurePercent, key=" + key);
							}

						}
					}
				});
				try {
					Thread.sleep(fixedDelayInSecond * 1000);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
		});
	}

	private Map<String, CircularFifoQueue<Integer>> timeWindowMapForFailurePercent = new ConcurrentHashMap<>();
	private Map<String, GaugeForFailurePercent> gaugeMapForFailurePercent = new ConcurrentHashMap<>();

	private Map<String, CircularFifoQueue<Long>> timeWindowMapForExecTimeAvg = new ConcurrentHashMap<>();
	private Map<String, GaugeForExecTimeAvg> gaugeMapForExecTimeAvg = new ConcurrentHashMap<>();

	private Map<String, CircularFifoQueue<Long>> timeWindowMapForExecTimeMax = new ConcurrentHashMap<>();
	private Map<String, GaugeForExecTimeMax> gaugeMapForExecTimeMax = new ConcurrentHashMap<>();

	private Map<String, CircularFifoQueue<Long>> timeWindowMapForExecTimeMin = new ConcurrentHashMap<>();
	private Map<String, GaugeForExecTimeMin> gaugeMapForExecTimeMin = new ConcurrentHashMap<>();

	private void clearGaugeMap() {
		this.gaugeMapForFailurePercent.clear();
		this.timeWindowMapForFailurePercent.clear();

		this.gaugeMapForExecTimeAvg.clear();
		this.timeWindowMapForExecTimeAvg.clear();

		this.gaugeMapForExecTimeMax.clear();
		this.timeWindowMapForExecTimeMax.clear();

		this.gaugeMapForExecTimeMin.clear();
		this.timeWindowMapForExecTimeMin.clear();
	}

	private Map<String, GaugeForFailurePercent> initGaugeMap() {
		service.getRequestList().forEach(httpRequest -> {
			if (httpRequest.isMonitorFunction()) {
				String key = generateKey(httpRequest);
				GaugeForFailurePercent statisticForFailurePercent = new GaugeForFailurePercent();
				List<Tag> tags = new ArrayList<>();
				Tag tag = Tag.of("url", httpRequest.getUrlWithParams());
				tags.add(tag);
				tag = Tag.of("method", httpRequest.getType());
				tags.add(tag);
				tag = Tag.of("name", httpRequest.getName());
				tags.add(tag);
				GaugeForFailurePercent gaugeForFailurePercent = meterRegistry.gauge("http.request.failure.percent",
						tags, statisticForFailurePercent, statisticForFailurePercent::getGaugeValue);
				gaugeMapForFailurePercent.put(key, gaugeForFailurePercent);

				List<Tag> avgTags = new ArrayList<>();
				avgTags.addAll(tags);
				avgTags.add(Tag.of("algorithm", "avg"));
				GaugeForExecTimeAvg statisticForExecTimeAvg = new GaugeForExecTimeAvg();
				GaugeForExecTimeAvg gaugeForExecTimeAvg = meterRegistry.gauge("http.request.duration.seconds", avgTags,
						statisticForExecTimeAvg, statisticForExecTimeAvg::getGaugeValue);
				gaugeMapForExecTimeAvg.put(key, gaugeForExecTimeAvg);

				List<Tag> maxTags = new ArrayList<>();
				maxTags.addAll(tags);
				maxTags.add(Tag.of("algorithm", "max"));
				GaugeForExecTimeMax statisticForExecTimeMax = new GaugeForExecTimeMax();
				GaugeForExecTimeMax gaugeForExecTimeMax = meterRegistry.gauge("http.request.duration.seconds", maxTags,
						statisticForExecTimeMax, statisticForExecTimeMax::getGaugeValue);
				gaugeMapForExecTimeMax.put(key, gaugeForExecTimeMax);

				List<Tag> minTags = new ArrayList<>();
				minTags.addAll(tags);
				minTags.add(Tag.of("algorithm", "min"));
				GaugeForExecTimeMin statisticForExecTimeMin = new GaugeForExecTimeMin();
				GaugeForExecTimeMin gaugeForExecTimeMin = meterRegistry.gauge("http.request.duration.seconds", minTags,
						statisticForExecTimeMin, statisticForExecTimeMin::getGaugeValue);
				gaugeMapForExecTimeMin.put(key, gaugeForExecTimeMin);
			}
		});
		return gaugeMapForFailurePercent;
	}

	private Map<String, GaugeForFailurePercent> getGaugeMapForFailurePercent() {
		return this.gaugeMapForFailurePercent;
	}

	private Map<String, CircularFifoQueue<Integer>> getTimeWindowMapForFailurePercent() {
		return this.timeWindowMapForFailurePercent;
	}

	private Map<String, GaugeForExecTimeAvg> getGaugeMapForExecTimeAvg() {
		return this.gaugeMapForExecTimeAvg;
	}

	private Map<String, CircularFifoQueue<Long>> getTimeWindowMapForExecTimeAvg() {
		return this.timeWindowMapForExecTimeAvg;
	}

	private Map<String, GaugeForExecTimeMax> getGaugeMapForExecTimeMax() {
		return this.gaugeMapForExecTimeMax;
	}

	private Map<String, CircularFifoQueue<Long>> getTimeWindowMapForExecTimeMax() {
		return this.timeWindowMapForExecTimeMax;
	}

	private Map<String, GaugeForExecTimeMin> getGaugeMapForExecTimeMin() {
		return this.gaugeMapForExecTimeMin;
	}

	private Map<String, CircularFifoQueue<Long>> getTimeWindowMapForExecTimeMin() {
		return this.timeWindowMapForExecTimeMin;
	}

	private String generateKey(HttpRequest httpRequest) {
		String key = httpRequest.getName() + httpRequest.getUrlWithParams() + httpRequest.getType();
		return key;
	}

	private static class GaugeForFailurePercent {
		Logger logger = LoggerFactory.getLogger(this.getClass());
		private double gaugeValue;

		public void compute(Map<String, CircularFifoQueue<Integer>> timeWindowMap, int windowSize, String key,
				boolean casePassed) {
			if (!timeWindowMap.containsKey(key)) {
				timeWindowMap.put(key, new CircularFifoQueue<Integer>(windowSize));
			}
			CircularFifoQueue<Integer> queue = timeWindowMap.get(key);
			queue.offer(casePassed ? 0 : 1);
			int failureTimes = queue.stream().mapToInt(i -> i.intValue()).sum();
			int execTimes = queue.size();
			double percent = (double) failureTimes / (double) execTimes;
			logger.debug("key=" + key);
			logger.debug("failed_case_percent=" + percent);
			logger.debug("queue=" + queue);
			gaugeValue = percent;
		}

		public double getGaugeValue(GaugeForFailurePercent GaugeForFailurePercent) {
			return GaugeForFailurePercent.gaugeValue;
		}
	}

	private static class GaugeForExecTimeAvg {
		Logger logger = LoggerFactory.getLogger(this.getClass());
		private double gaugeValue;

		public void compute(Map<String, CircularFifoQueue<Long>> timeWindowMap, int windowSize, String key,
				long durationInMilliSecond) {
			if (!timeWindowMap.containsKey(key)) {
				timeWindowMap.put(key, new CircularFifoQueue<Long>(windowSize));
			}
			CircularFifoQueue<Long> queue = timeWindowMap.get(key);
			queue.offer(durationInMilliSecond);
			double duration = queue.stream().mapToLong(i -> i.longValue()).average().orElse(0);
			duration = duration / (double) 1000;
			logger.debug("key=" + key);
			logger.debug("duration_avg(s)=" + duration);
			logger.debug("queue=" + queue);
			gaugeValue = duration;
		}

		public double getGaugeValue(GaugeForExecTimeAvg gaugeForExecTimeAvg) {
			return gaugeForExecTimeAvg.gaugeValue;
		}
	}

	private static class GaugeForExecTimeMax {
		Logger logger = LoggerFactory.getLogger(this.getClass());
		private double gaugeValue;

		public void compute(Map<String, CircularFifoQueue<Long>> timeWindowMap, int windowSize, String key,
				long durationInMilliSecond) {
			if (!timeWindowMap.containsKey(key)) {
				timeWindowMap.put(key, new CircularFifoQueue<Long>(windowSize));
			}
			CircularFifoQueue<Long> queue = timeWindowMap.get(key);
			queue.offer(durationInMilliSecond);
			double duration = queue.stream().mapToLong(i -> i.longValue()).max().orElse(0);
			duration = duration / (double) 1000;
			logger.debug("key=" + key);
			logger.debug("duration_max(s)=" + duration);
			logger.debug("queue=" + queue);
			gaugeValue = duration;
		}

		public double getGaugeValue(GaugeForExecTimeMax gaugeForExecTimeMax) {
			return gaugeForExecTimeMax.gaugeValue;
		}
	}

	private static class GaugeForExecTimeMin {
		Logger logger = LoggerFactory.getLogger(this.getClass());
		private double gaugeValue;

		public void compute(Map<String, CircularFifoQueue<Long>> timeWindowMap, int windowSize, String key,
				long durationInMilliSecond) {
			if (!timeWindowMap.containsKey(key)) {
				timeWindowMap.put(key, new CircularFifoQueue<Long>(windowSize));
			}
			CircularFifoQueue<Long> queue = timeWindowMap.get(key);
			queue.offer(durationInMilliSecond);
			double duration = queue.stream().mapToLong(i -> i.longValue()).min().orElse(0);
			duration = duration / (double) 1000;
			logger.debug("key=" + key);
			logger.debug("duration_min(s)=" + duration);
			logger.debug("queue=" + queue);
			gaugeValue = duration;
		}

		public double getGaugeValue(GaugeForExecTimeMin gaugeForExecTimeMin) {
			return gaugeForExecTimeMin.gaugeValue;
		}
	}
}
