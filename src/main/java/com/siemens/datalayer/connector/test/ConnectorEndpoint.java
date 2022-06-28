package com.siemens.datalayer.connector.test;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;

public class ConnectorEndpoint {
	
	private static String BASE_URL = "";
	
	private static String port = "";
	
	private static String domain_name = "";
	
	public static void setBaseUrl(String base_url)
	{
		BASE_URL = base_url;
		RestAssured.basePath = "";
	}
	
	public static void setPort(String comm_port)
	{
		port = comm_port;
	}
	
	public static void setDomain(String domainName)
	{
		domain_name = domainName;
	}
	
	public static String getResourcePath()
	{
		String resourcePath;
		
		switch (domain_name)
		{
			case "iEMS":
				resourcePath = "json-model-schema/iems/";
				break;
				
			case "JinZu":
				resourcePath = "json-model-schema/jinzu/";
				break;
				
			case "SNC":
				resourcePath = "json-model-schema/snc/";
				break;
				
			case "IOT":
				resourcePath = "json-model-schema/iot/";
				break;
				
			default:
				resourcePath = "";
		}
		
		return resourcePath;
	}
	
	// Connector Interface: Get All Entities name
	@Step("Send a request of 'Get All Entities Name'")
	public static Response getAllEntitiesName() {
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();
//		RestAssured.basePath = "api/connector/entities";
		
        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");
        // httpRequest.header("version", "1.0");

        Response response = httpRequest.filter(new AllureRestAssured())
        							   .get("/api/connectors/entities");
        
        return response;
	}
	
	// Connector Interface: Get concept model data by condition
	@Step("Send a request of 'Get concept model data by condition'")
	public static Response getConceptModelDataByCondition(HashMap<String, String> parameters)
	{
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();
//		RestAssured.basePath = "api/connector/searchData";
		
		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header("content-type","application/json");
		// httpRequest.header("version","1.0");

		Response response = httpRequest.queryParams(parameters) 
									   .filter(new AllureRestAssured())
									   .get("/api/connectors/searchData");
		
		return response;
	}

	//Connector Interface: DML insert operator
	@Step("Send a request of 'DML Insert operator'")
	public static Response dmlInsertOperator(String insertInfo){
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();

		RequestSpecification httpRequest = RestAssured.given();

		Response response = httpRequest.body(insertInfo)
				.contentType("application/json")
				.filter(new AllureRestAssured())
				.post("api/connectors/dmlData");
		return response;
	}

	//Connector Interface: DML insert operator
	@Step("Send a request of 'DML Insert operator'")
	public static Response dmlInsertOperator(HashMap<String,String> authInfo, String insertInfo){
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();

		RequestSpecification httpRequest = RestAssured.given();

		Response response = httpRequest.queryParams(authInfo)
				                       .body(insertInfo)
				                       .contentType("application/json")
				                       .filter(new AllureRestAssured())
				                       .post("api/connectors/dmlData");
		return response;
	}

	// Test Developer Tools:clearRedisCaches
	@Step("send a request of 'clearRedisCaches'")
	public static Response clearRedisCaches()
	{
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();

		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header("content-type","application/json");

		Response response = httpRequest.filter(new AllureRestAssured())
				.get("/api/cache-tools/redis-clearance");
		return response;
	}

	// Test Developer Tools:clearAllCaches
	@Step("send a request of 'clearAllCaches'")
	public static Response clearAllCaches()
	{
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();

		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header("content-type","application/json");

		Response response = httpRequest.filter(new AllureRestAssured())
				.delete("/api/cache/allCaches");
		return response;
	}

	// Test Developer Tools:clearRedisCache
	@Step("send a request of 'clearRedisCache'")
	public static Response clearRedisCache()
	{
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();

		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header("content-type","application/json");

		Response response = httpRequest.filter(new AllureRestAssured())
				.get("/api/dev-tools/redisCache-clearance");
		return response;
	}
}
