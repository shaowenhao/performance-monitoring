package com.siemens.datalayer.connector.test;

import java.util.HashMap;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Endpoint {
	
	private static String BASE_URL = "";
	
	private static String port = "";
	
	private static String domain_name = "";
	
	public static void setBaseUrl(String base_url)
	{
		BASE_URL = base_url;
	}
	
	public static void setPort(String comm_port)
	{
		port = comm_port;
	}
	
	public static void setDomain(String domainName)
	{
		domain_name = domainName;
	}
	
	// Connector Interface: Get All Entities name
	@Step("Send a request of 'Get All Entities Name'")
	public static Response getAllEntitiesName() {
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();
		RestAssured.basePath = "api/connector/entities";
		
        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");
        
        Response response = httpRequest.filter(new AllureRestAssured())
        							   .get();
        
        return response;
	}
	
	// Connector Interface: Get concept model data by condition
	@Step("Send a request of 'Get concept model data by condition'")
	public static Response getConceptModelDataByCondition(HashMap<String, String> parameters)
	{
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();
		RestAssured.basePath = "api/connector/searchData";
		
		RequestSpecification httpRequest = RestAssured.given();

		Response response = httpRequest.queryParams(parameters) 
									   .filter(new AllureRestAssured())
									   .get();
		
		return response;
	}	
	
	// Entity Interface: Get concept model definition by model name
	@Step("Send a request of 'Get concept model definition by model name'")
	public static Response getConceptModelDefinitionByModelName(String domain, String name)
	{
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();
		RestAssured.basePath = "api/v1/entity/searchModelSchemaByName";
		
		RequestSpecification httpRequest = RestAssured.given();
		
		HashMap<String, String> parameters = new HashMap<>();
		
		if (domain.equals("default")) {
			parameters.put("domainName", domain_name);
		}
		else if (!domain.isEmpty()) {
			parameters.put("domainName", domain);
		}
		
		if (!name.isEmpty()) parameters.put("name", name);
		
		Response response = httpRequest.queryParams(parameters)
									   .filter(new AllureRestAssured())
									   .get();
		
		return response;
	}

	// Connector Interface: Get concept model data by page condition
	@Step("Send a request of 'Get concept model data by page condition'")
	public static Response getConceptModelDataByPageCondition(HashMap<String, String> parameters)
	{
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();
		RestAssured.basePath = "api/connector/searchPageData";
		
		RequestSpecification httpRequest = RestAssured.given();

		Response response = httpRequest.queryParams(parameters) 
									   .filter(new AllureRestAssured())
									   .get();
		
		return response;
	}

	// Developer Tools: CheckSuperMapper
	public static Response checkSuperMapper()
	{
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();
		RestAssured.basePath = "api/dev-tools/checkSuperMapper";
		
        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("accept", "*/*");
        
        Response response = httpRequest.get();
        
        return response;
	}
	
	// Developer Tools: clearAllCaches
	public static Response clearAllCaches()
	{
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();
		RestAssured.basePath = "api/dev-tools/clear-caches";
		
        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("accept", "*/*");
        
        Response response = httpRequest.get();
        
        return response;
	}
	
	// Developer Tools: executeSQL
	public static Response executeSQL(String sql)
	{
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();
		RestAssured.basePath = "api/dev-tools/execute-sql";
		
        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("accept", "*/*");
        
		Response response = httpRequest.queryParam("name", sql) 
									   .filter(new AllureRestAssured())
                					   .get();
        
        return response;
	}
	
	// Mapper Rule Interface: insert test data name
	public static Response insertTestDataName()
	{
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();
		RestAssured.basePath = "api/mapper/test";
		
        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("accept", "*/*");
        
        Response response = httpRequest.get();
        
        return response;
	}
}
