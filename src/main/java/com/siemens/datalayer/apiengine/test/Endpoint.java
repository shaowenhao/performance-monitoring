package com.siemens.datalayer.apiengine.test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;

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
	
	public static Response getEntities(HashMap<String, String> parameters) {
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();
		
        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");
        
        Response response = httpRequest.queryParams(parameters).get("/entities");
        
        return response;
	}
	
	public static Response postGraphql(String query)
	{
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();
		
		RequestSpecification httpRequest = RestAssured.given();
		
		Response response = httpRequest.body(query)
		                   .post("/graphql");
		
		return response;
	}
	
	public static Response getGraphqlApiSchema()
	{
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();
		
		RequestSpecification httpRequest = RestAssured.given();

		Response response = httpRequest
		                   .get("/graphql-api/schema");
		
		return response;
	}

}
