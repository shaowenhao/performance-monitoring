package com.siemens.devops.monitoring.model;

public class RestResult<T> {

	public static final int SUCCESS = 0;

	public static final String SUCCESS_MSG = "Operate success.";

	public static final String FAILED_MSG = "Operate failed.";

	public static String getSuccessMsg() {
		return SUCCESS_MSG;
	}

	int code;

	String message;

	T data;

	public RestResult(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public RestResult(int code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public RestResult() {

	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public static <T> RestResult<T> sucess() {
		return new RestResult<>(SUCCESS, SUCCESS_MSG, null);
	}

	public static <T> RestResult<T> sucess(T data) {
		return new RestResult<>(SUCCESS, SUCCESS_MSG, data);
	}

	public static <T> RestResult<T> sucess(T data, String message) {
		return new RestResult<>(SUCCESS, message, data);
	}

	public static RestResult<Void> error(int code, String message) {
		return new RestResult<>(code, message);
	}

	public static RestResult<Void> error(int code) {
		return error(code, FAILED_MSG);
	}

	public static RestResult<Void> error() {
		return error(-1, FAILED_MSG);
	}

}
