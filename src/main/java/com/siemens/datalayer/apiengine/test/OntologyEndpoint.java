package com.siemens.datalayer.apiengine.test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;

public class OntologyEndpoint {

    private static String BASE_URL = "";

    private static String port = "";

    public static void setBaseUrl(String base_url) {
        BASE_URL = base_url;
    }

    public static void setPort(String comm_port) {
        port = comm_port;
    }

    public static Response getDomains(HashMap<String, String> parameters) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.queryParams(parameters).get("/api/ontology/domains");

        return response;
    }

    public static Response getEntitiesByLabel(HashMap<String, String> parameters) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.queryParams(parameters).get("/api/ontology/entities");

        return response;
    }

//	public static Response loadGraph(String query)
//	{
//		RestAssured.baseURI = BASE_URL;
//		RestAssured.port = Integer.valueOf(port).intValue();
//
//		RequestSpecification httpRequest = RestAssured.given();
//
//		Response response = httpRequest.body(query)
//		                   .post("/api/ontology/graphs");
//
//		return response;
//	}

    public static Response deleteGraph() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest
                .delete("/api/ontology/graphs");

        return response;
    }


    public static Response getRelationBetweenEntities(HashMap<String, String> parameters) {

        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.queryParams(parameters)
                .get("/api/ontology/relations");

        return response;
    }


	public static Response getRelationByEntitiesIds(HashMap<String, String> parameters) {

		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();

		RequestSpecification httpRequest = RestAssured.given();

		Response response = httpRequest.queryParams(parameters)
				.post("/api/ontology/relations");

		return response;
	}

}
