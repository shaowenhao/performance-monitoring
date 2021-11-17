package com.siemens.datalayer.entitymanagement.test;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

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

    // Entity Endpoint: createEntities
    @Step("Send a request of 'createEntities'")
    public static Response createEntities(String body)
    {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.body(body)
                .filter(new AllureRestAssured())
                .post("/api/v2/graph/entities");
        return response;
    }

    // Entity Endpoint: updateEntities
    @Step("Send a request of 'updateEntities'")
    public static Response updateEntities(String body)
    {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.body(body)
                .filter(new AllureRestAssured())
                .put("/api/v2/graph/entities");

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
                .get("/api/v2/graph/entities/" + entityId);

        return response;
    }

    // Entity Endpoint: getEntities
    @Step("Send a request of 'getEntities'")
	public static Response getEntities(String labels){
	    RestAssured.baseURI = BASE_URL;
	    RestAssured.port = Integer.valueOf(port).intValue();

        Map<String,String> parameters = new HashMap<String,String>();
        parameters.put("labels",labels);

	    RequestSpecification httpRequest = RestAssured.given();
	    httpRequest.header("Content-Type", "application/json");

	    Response response = httpRequest.params(parameters)
                                       .filter(new AllureRestAssured())
                                       .get("/api/v2/graph/entities");
	    return response;
    }

    // Entity Endpoint: getEntitiesLabelLike
    @Step("Send a request of 'getEntitiesLabelLike'")
    public static Response getEntitiesLabelLike(String keyword)
    {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        Map<String,String> parameters = new HashMap<String,String>();
        parameters.put("keyword",keyword);

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.params(parameters)
                .filter(new AllureRestAssured())
                .get("/api/v2/graph/entities/search");
        return response;
    }

    // Entity Endpoint: deleteEntity via entityId
	@Step("Send a request of 'deleteEntity'")
    public static Response deleteEntity(String entityIds)
    {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.queryParam("entityIds", entityIds)
        							   .filter(new AllureRestAssured())
        							   .delete("/api/v2/graph/entities");

        return response;
    }

    // Relation Endpoint: createRelations
    @Step("Send a request of 'createRelations'")
    public static Response createRelations(String body)
    {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.body(body)
                .filter(new AllureRestAssured())
                .post("/api/v2/graph/relations");
        return response;
    }

    // Relation Endpoint: updateRelations
    @Step("Send a request of 'updateRelations'")
    public static Response updateRelations(String body)
    {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.body(body)
                .filter(new AllureRestAssured())
                .post("/api/v2/graph/relations");
        return response;
    }

    // Relation Endpoint: getRelationById
    @Step("Send a request of 'getRelationById'")
    public static Response getRelationById(String relationId)
    {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.filter(new AllureRestAssured())
                .get("/api/v2/graph/relations/"+relationId);

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
                .get("/api/v2/graph/relations");

        return response;
    }

    // Relation Endpoint: deleteRelation
    @Step("Send a request of 'deleteRelation'")
    public static Response deleteRelations(String relationIds)
    {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.queryParam("relationIds", relationIds)
                .filter(new AllureRestAssured())
                .delete("/api/v2/graph/relations");

        return response;
    }
}
