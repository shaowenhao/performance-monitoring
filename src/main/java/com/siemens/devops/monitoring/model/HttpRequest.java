package com.siemens.devops.monitoring.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class HttpRequest {
	private String url;
	private String type;
	private List<Map<String, Object>> params;
	private Map<String, Object> headers;
	private String body;
	private String name;
	private Integer expectedCode;
	private boolean monitorPerformance = true;
	private boolean monitorFunction = true;

	public String getUrlWithParams() {
		String url = (this.getParams() != null && this.getParams().size() > 0)
				? generateUrlWithParams(this.getUrl(), this.getParams())
				: this.getUrl();
		return url;
	}

	private String generateUrlWithParams(String url, List<Map<String, Object>> params) {
		StringBuilder sb = new StringBuilder(url).append("?");
		params.forEach(param -> {
			param.entrySet().forEach(entry -> {
				sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
			});
		});
		return sb.substring(0, sb.length() - 1).toString();
	}
}
