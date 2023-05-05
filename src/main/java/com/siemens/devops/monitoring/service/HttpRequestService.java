package com.siemens.devops.monitoring.service;

import com.alibaba.fastjson.JSONObject;
import com.siemens.devops.monitoring.config.HttpRequestsConfig;
import com.siemens.devops.monitoring.model.HttpRequest;
import com.siemens.devops.monitoring.model.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class HttpRequestService {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private HttpRequestsConfig requestsConfig;

	private int extendedValue = 1800_000;

	public List<HttpRequest> getRequestList() {
		return requestsConfig.getHttpRequests();
	}

	public HttpResponse handleRequest(HttpRequest req) {
		// handle headers
		HttpHeaders headers = new HttpHeaders();
		if (req.getHeaders() != null && req.getHeaders().size() > 0) {
			req.getHeaders().entrySet().forEach(header -> {
				headers.add(header.getKey(), header.getValue().toString());
			});
		}

		HttpEntity httpEntity = new HttpEntity<>(req.getBody(), headers);
		HttpResponse response = new HttpResponse();
		String url = req.getUrlWithParams();
		response.setUrl(url);
		response.setMethod(req.getType());
		response.setErrMsg("");
		response.setTimeout(false);
		long stime = System.currentTimeMillis();
		long etime = stime;
		response.setTimestamp(getCurrentDate());
		try {
			logger.info("name: " + req.getName() + ", url: " + url + ", method: " + req.getType() + ", body: "
					+ req.getBody());
			ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.valueOf(req.getType()), httpEntity,
					String.class);
			etime = System.currentTimeMillis();
			boolean pass = checkBody(result.getBody(), req);
			if (!pass) {
				etime = stime + extendedValue;
			}
			response.setStatusCode(result.getStatusCode().toString());
		} catch (HttpClientErrorException | HttpServerErrorException e) {
//			etime = System.currentTimeMillis();
			etime = stime + extendedValue;
			response.setErrMsg(e.getResponseBodyAsString());
			response.setStatusCode(e.getStatusCode().toString());
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
//			etime = System.currentTimeMillis();
			etime = stime + extendedValue;
			response.setTimeout(true);
			logger.error(e.getMessage(), e);
		}
		response.setExecTime(etime - stime);
		return response;
	}

	private boolean checkBody(String body, HttpRequest req) {
		Integer expectedCode = req.getExpectedCode();
		JSONObject object = JSONObject.parseObject(body);
		Integer actualCode = (Integer) object.get("code");
		if (!(expectedCode.compareTo(actualCode) == 0)) {
			logger.error("expectedCode: " + expectedCode + ", actualCode: " + actualCode + ", [" + "name: "
					+ req.getName() + ", url: " + req.getUrlWithParams() + ", method: " + req.getType() + ", body: "
					+ req.getBody() + "]");
			return false;
		}
		return true;
	}

	private String getCurrentDate() {

		return df.format(new Date());
	}
}
