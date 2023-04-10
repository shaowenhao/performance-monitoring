package com.siemens.devops.monitoring.test.endpoint;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import com.alibaba.fastjson.JSONObject;

public class ConnectorConfigureEndpoint {

	private static String BASE_URL = "";

	private static int PORT = 0;

	public static void setBaseUrlAndPort(String baseUrl, String port) {
		BASE_URL = baseUrl;
		PORT = Integer.valueOf(port).intValue();

	}

	private static void resetRestAssured() {
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = PORT;
		RestAssured.basePath = "";
	}

	// Test Developer Tools:clear all cache
	@Step("send a request of 'clear all cache'")
	public static Response clearAllCache() {
		resetRestAssured();

		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header("content-type", "application/json");

		Response response = httpRequest.filter(new AllureRestAssured()).get("/cache/clean");
		return response;
	}

	// Test Developer Tools:deleteCache
	@Step("send a request of 'deleteCache'")
	public static Response deleteCache(String name) {
		resetRestAssured();

		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header("content-type", "application/json");

		Response response = httpRequest.filter(new AllureRestAssured()).delete("/cache/" + name);

		return response;
	}

	// connector-domain-controller:getAllConnectors
	@Step("Send a request of 'getAllConnectors'")
	public static Response getAllConnectors() {
		resetRestAssured();

		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header("content-type", "application/json");

		Response response = httpRequest.filter(new AllureRestAssured()).get("/connectors");

		return response;
	}

	// connector-domain-controller:save connector
	@Step("Send a request of 'save connector'")
	public static Response saveConnector(String body) {
		resetRestAssured();

		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header("content-type", "application/json");

		Response response = httpRequest.body(body).filter(new AllureRestAssured()).post("/connectors");

		return response;
	}

	// connector-domain-controller:deleteConnector
	@Step("Send a request of 'deleteConnector'")
	public static Response deleteConnector(String connectorName) {
		resetRestAssured();

		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header("content-type", "application/json");

		Response response = httpRequest.filter(new AllureRestAssured()).delete("/connectors/" + connectorName);
		return response;
	}

	// connector-domain-controller:getConnector
	@Step("Send a request of 'getConnector'")
	public static Response getConnector(String connectorName) {
		resetRestAssured();

		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header("content-type", "application/json");

		Response response = httpRequest.filter(new AllureRestAssured()).get("/connectors/" + connectorName);

		return response;
	}

	// connector-domain-controller:updateConnector
	@Step("Send a request of 'updateConnector'")
	public static Response updateConnector(String connectorName, String body) {
		resetRestAssured();

		JSONObject jsonObject = JSONObject.parseObject(body);

		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header("content-type", "application/json");

		Response response = httpRequest.body(jsonObject).filter(new AllureRestAssured())
				.put("/connectors/" + connectorName);

		return response;
	}

	// Cache Config Controller:allCacheConfigs
	@Step("Send a request of 'getAllCacheConfigs'")
	public static Response getAllCacheConfigs() {
		resetRestAssured();

		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header("content-type", "application/json");

		Response response = httpRequest.filter(new AllureRestAssured()).get("/cache-config");

		return response;
	}

	// Cache Config Controller:moduleCacheConfig
	@Step("Send a request of 'getModuleCacheConfig'")
	public static Response getModuleCaheConfig(String moduleName) {
		resetRestAssured();

		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header("content-type", "application/json");

		Response response = httpRequest.filter(new AllureRestAssured()).get("/cache-config/" + moduleName);

		return response;
	}

	// Cache Config Controller:saveCacheConfig
	@Step("Send a request of 'saveCacheConfig'")
	public static Response saveCacheConfig(String body) {
		resetRestAssured();

		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header("content-type", "application/json");

		Response response = httpRequest.body(body).filter(new AllureRestAssured()).post("/cache-config");

		return response;
	}

	// Cache Config Controller:updateCacheConfig
	@Step("Send a request of 'updateCacheConfig'")
	public static Response updateCacheConfig(String body) {
		resetRestAssured();

		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header("content-type", "application/json");

		Response response = httpRequest.body(body).filter(new AllureRestAssured()).put("/cache-config");

		return response;
	}

	// Cache Config Controller:deleteCacheConfig
	@Step("Send a request of 'deleteConnector'")
	public static Response deleteCacheConfig(String moduleName, String name) {
		resetRestAssured();

		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header("content-type", "application/json");

		Response response = httpRequest.filter(new AllureRestAssured())
				.delete("/cache-config/" + moduleName + "/" + name);
		return response;
	}

	// Cache Config Controller:clearModuleCaches
	@Step("Send a request of 'clearModuleCaches'")
	public static Response clearModuleCaches(String moduleName) {
		resetRestAssured();

		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header("content-type", "application/json");

		Response response = httpRequest.filter(new AllureRestAssured()).delete("/cache-config/clear/" + moduleName);
		return response;
	}

	// Cache Controller: allCacheNames
	@Step("Send a request of 'getAllCacheNames'")
	public static Response getAllCacheNames() {
		resetRestAssured();

		RequestSpecification httpRequest = RestAssured.given();

		Response response = httpRequest.filter(new AllureRestAssured()).get("cache/names");

		return response;
	}

	// Cache Controller: cacheStatistics
	@Step("Send a request of 'cacheStatistics'")
	public static Response getCacheStatistics(String name) {
		resetRestAssured();

		RequestSpecification httpRequest = RestAssured.given();

		Response response = httpRequest.filter(new AllureRestAssured()).get("/cache/" + name + "/statistics");

		return response;
	}

	// Cache Controller: allCacheKeys
	@Step("Send a request of 'getallCacheKeys'")
	public static Response getAllCacheKeys(String name) {
		resetRestAssured();

		RequestSpecification httpRequest = RestAssured.given();

		Response response = httpRequest.filter(new AllureRestAssured()).get("/cache/" + name + "/keys");

		return response;
	}

	// Cache Controller: getCacheValue
	@Step("Send a request of 'getCacheValue'")
	public static Response getCacheValue(String key, String name) {
		resetRestAssured();

		RequestSpecification httpRequest = RestAssured.given();

		Response response = httpRequest.filter(new AllureRestAssured()).get("/cache/" + name + "/" + key);

		return response;
	}

}
