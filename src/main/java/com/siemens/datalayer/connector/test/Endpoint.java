package com.siemens.datalayer.connector.test;

import java.util.HashMap;

import io.restassured.RestAssured;
import io.restassured.http.Method;
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
	
	public static Response getAllEntitiesName() {
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();
		
        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");
        
        Response response = httpRequest.request(Method.GET, "/api/com.siemens.datalayer.connector/entities");
        
        return response;
	}
	
	public static Response searchModelSchemaByName(String name)
	{
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();
		
		RequestSpecification httpRequest = RestAssured.given();
		
		Response response = httpRequest.queryParam("name", name) 
		                   .get("/api/com.siemens.datalayer.connector/searchModelSchemaByName");
		
		return response;
	}
	
	public static Response getConceptModelDataByCondition(HashMap<String, String> parameters)
	{
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();
		
		RequestSpecification httpRequest = RestAssured.given();

		Response response = httpRequest.queryParams(parameters) 
		                   .get("/api/com.siemens.datalayer.connector/searchData");
		
		return response;
	}

	public static Response checkSuperMapper()
	{
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();
		
        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("accept", "*/*");
        
        Response response = httpRequest.request(Method.GET, "/api/dev-tools/checkSuperMapper");
        
        return response;
	}
	
	public static Response insertTestDataName()
	{
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();
		
        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("accept", "*/*");
        
        Response response = httpRequest.request(Method.GET, "/api/mapper/test");
        
        return response;
	}
}
