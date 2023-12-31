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

	public HttpResponse handleRequest(HttpRequest request) {
		// handle headers
		HttpHeaders headers = new HttpHeaders();
		if (request.getHeaders() != null && request.getHeaders().size() > 0) {
			request.getHeaders().entrySet().forEach(header -> {
				headers.add(header.getKey(), header.getValue().toString());
			});
		}

		HttpEntity httpEntity = new HttpEntity<>(request.getBody(), headers);
		HttpResponse response = new HttpResponse();
		String url = request.getUrlWithParams();
		response.setUrl(url);
		response.setMethod(request.getType());
		response.setErrMsg("");
		response.setTimeout(false);
		response.setTimestamp(getCurrentDate());
		long stime = System.currentTimeMillis();
		long etime = stime;
		try {
			logger.info("name: " + request.getName() + ", url: " + url + ", method: " + request.getType() + ", body: "
					+ request.getBody());
			ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.valueOf(request.getType()),
					httpEntity, String.class);
			etime = System.currentTimeMillis();
			
			boolean pass = checkBody(result.getBody(), request);
			response.setPassed(pass);			
//			if (!pass) {
//				etime = stime + extendedValue;
//			}
			response.setStatusCode(result.getStatusCode().toString());
		} catch (HttpClientErrorException | HttpServerErrorException e) {
//			etime = stime + extendedValue;
			etime = System.currentTimeMillis();
			response.setErrMsg(e.getResponseBodyAsString());
			response.setStatusCode(e.getStatusCode().toString());
			response.setPassed(false);
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
//			etime = stime + extendedValue;
			etime = System.currentTimeMillis();
			response.setTimeout(true);
			response.setPassed(false);
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
