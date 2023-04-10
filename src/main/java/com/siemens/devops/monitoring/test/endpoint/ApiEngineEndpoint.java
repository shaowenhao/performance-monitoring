package com.siemens.devops.monitoring.test.endpoint;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;

public class ApiEngineEndpoint {

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

	// API Engine Query Endpoint: getData (restful interface)
	@Step("Send a restful query request 'getData' to data-layer-api-engine")
	public static Response getEntities(HashMap<String, String> parameters) {
		resetRestAssured();

		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header("Content-Type", "application/json");

		Response response = httpRequest.queryParams(parameters).filter(new AllureRestAssured()).get("/entities");

		return response;
	}

	// API Engine Query Endpoint: getData (graphql interface)
	@Step("Send a graphql 'query or mutation' request to data-layer-api-engine")
	public static Response postGraphql(String query) {
		resetRestAssured();

		RequestSpecification httpRequest = RestAssured.given();

		Response response = httpRequest.body(query).contentType("application/json").filter(new AllureRestAssured())
				.post("/graphql");

		return response;
	}

	// API Engine Query Endpoint with HTTPS: getData (graphql interface)
	@Step("Send a graphql https query request 'getData' to data-layer-api-engine")
	public static Response postHttpsGraphql(String query, String accessToken) {
		resetRestAssured();

		RequestSpecification httpRequest = RestAssured.given().relaxedHTTPSValidation();
		httpRequest.header("Authorization", "Bearer " + accessToken);
		Response response = httpRequest.body(query).contentType("application/json").filter(new AllureRestAssured())
				.post("jinzu-test/api-engine/graphql");

		return response;
	}

	public static Response getGraphqlApiSchema() {
		resetRestAssured();

		RequestSpecification httpRequest = RestAssured.given();

		Response response = httpRequest.filter(new AllureRestAssured()).get("/graphql-api/schema");

		return response;
	}

	// evictAllCache
	@Step("send a request to delete all cache'")
	public static Response evictAllCache() {
		resetRestAssured();

		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header("content-type", "application/json");

		Response response = httpRequest.filter(new AllureRestAssured()).delete("cache");
		return response;
	}

}
