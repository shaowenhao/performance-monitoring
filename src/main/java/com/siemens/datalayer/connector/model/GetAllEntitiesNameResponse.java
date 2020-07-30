package com.siemens.datalayer.connector.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAllEntitiesNameResponse {

	private Integer code;
	private String message;
	private List<String> data = null;
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	
	public Integer getCode() {
		return code;
	}
	
	public void setCode(Integer code) {
		this.code = code;
	}
	
	public GetAllEntitiesNameResponse withCode(Integer code) {
		this.code = code;
		return this;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public GetAllEntitiesNameResponse withMessage(String message) {
		this.message = message;
		return this;
	}
	
	public List<String> getData() {
		return data;
	}
	
	public void setData(List<String> data) {
		this.data = data;
	}
	
	public GetAllEntitiesNameResponse withData(List<String> data) {
		this.data = data;
		return this;
	}
	
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}
	
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}
	
	public GetAllEntitiesNameResponse withAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
		return this;
	}

}