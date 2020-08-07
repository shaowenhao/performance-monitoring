package com.siemens.datalayer.connector.test;

import java.util.HashMap;

import io.qameta.allure.restassured.AllureRestAssured;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Endpoint {
	
	private static String BASE_URL = "";
	
	private static String port = "";
	
	public static void setBaseUrl(String base_url)
	{
		BASE_URL = base_url;
	}
	
	public static void setPort(String comm_port)
	{
		port = comm_port;
	}
	
	// Connector Interface: Get All Entities name
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
	
	// Connector Interface: Get concept model definition by model name
	public static Response getConceptModelDefinitionByModelName(String name)
	{
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();
		RestAssured.basePath = "api/connector/searchModelSchemaByName";
		
		RequestSpecification httpRequest = RestAssured.given();
		
		Response response = httpRequest.queryParam("name", name) 
									   .filter(new AllureRestAssured())
									   .get();
		
		return response;
	}

	// Connector Interface: Get concept model data by page condition
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
