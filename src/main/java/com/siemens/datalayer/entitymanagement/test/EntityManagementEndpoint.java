package com.siemens.datalayer.entitymanagement.test;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class EntityManagementEndpoint {
	
	private static String BASE_URL = "";	
	private static String port = "";	
	
	public static void setBaseUrl(String base_url)
	{
		BASE_URL = base_url;
		RestAssured.basePath = "";
	}
	
	public static void setPort(String comm_port)
	{
		port = comm_port;
	}

    // Entity Endpoint: createEntity
	@Step("Send a request of 'createEntity'")
    public static Response createEntity(String body) 
	{
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();
        /*System.out.println("BASE_URL:"+BASE_URL);
        System.out.println("port:"+Integer.valueOf(port).intValue());*/

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.body(body)
        							   .filter(new AllureRestAssured())
                					   .post("/api/entities");
        return response;
    }

    // Entity Endpoint: updateEntity 
	@Step("Send a request of 'updateEntity'")
    public static Response updateEntity(String body) 
    {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.body(body)
					        		   .filter(new AllureRestAssured())
					                   .put("/api/entities");

        return response;
    }

    // Entity Endpoint: deleteEntity via entityId
	@Step("Send a request of 'deleteEntity'")
    public static Response deleteEntity(String entityId) 
    {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.queryParam("entityId", entityId) 
        							   .filter(new AllureRestAssured())
        							   .delete("/api/entities");

        return response;
    }

    // Entity Endpoint: getEntityById 
	@Step("Send a request of 'getEntityById'")
    public static Response getEntityById(String entityId) 
	{
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();     
        
        Response response = httpRequest.filter(new AllureRestAssured())
					                   .get("/api/entities/" + entityId);

        return response;
    }
	
    // Graph Endpoint: getGraph 
	@Step("Send a request of 'getGraph'")
    public static Response getGraph() 
	{
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();     
        
        Response response = httpRequest.filter(new AllureRestAssured())
					                   .get("/api/graphs");

        return response;
    }
	
	// Relation Endpoint: getRelations
	@Step("Send a request of 'getRelations' without labels")
    public static Response getAllRelations() 
	{
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();     
        
        Response response = httpRequest.filter(new AllureRestAssured())
					                   .get("/api/relations");

        return response;
    }

	@Step("Send a request of 'getRelations'")
    public static Response getRelations(String labels) 
	{
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();     
        
        Response response = httpRequest.queryParam("labels", labels)
        							   .filter(new AllureRestAssured())
					                   .get("/api/relations");

        return response;
    }
	
	@Step("Send a request of 'getRelationById'")
    public static Response getRelationById(String relationId) 
	{
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();     
        
        Response response = httpRequest.filter(new AllureRestAssured())
					                   .get("/api/relations/"+relationId);

        return response;
    }
	
}
