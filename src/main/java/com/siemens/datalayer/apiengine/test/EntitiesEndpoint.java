package com.siemens.datalayer.apiengine.test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.File;
import java.util.HashMap;

public class EntitiesEndpoint {

    private static String BASE_URL = "";

    private static String port = "";

    public static void setBaseUrl(String base_url) {
        BASE_URL = base_url;
    }

    public static void setPort(String comm_port) {
        port = comm_port;
    }

    // graph-endpoint

    public static Response getGraphs() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.get("/api/graphs");

        return response;
    }

    public static Response getGraphByDomain(String domain) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.get(String.format("/api/%s/graphs", domain));

        return response;
    }

    public static Response loadGraph(String filePath) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.body(new File(filePath)).post("/api/graphs");

        return response;
    }


    public static Response deleteGraph() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.delete("/api/graphs");

        return response;
    }

    public static Response addEntitiesByDomain(String domain) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.get(String.format("/api/graphs/%s/entities", domain));

        return response;
    }

    public static Response archiveGraph(String type) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.get(String.format("/api/graphs/%s", type));

        return response;
    }

    public static Response addEntitiestToKg(String entities) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.body(entities)
                .post("/api/graphs/entities");

        return response;
    }

    public static Response searchNodes(HashMap<String, String> parameters) {

        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.queryParams(parameters)
                .get("/api/graphs/search");

        return response;
    }

    //domain-endpoint

    public static Response filterDomain(String domain, HashMap<String, String> parameters) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.queryParams(parameters)
                .get(String.format("/api/%s/filter", domain));

        return response;
    }

    public static Response getDomains(HashMap<String, String> parameters) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.queryParams(parameters)
                .get("/api/domains");

        return response;
    }

    public static Response getDomainsByLabel(String domain, HashMap<String, String> parameters) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.queryParams(parameters)
                .get(String.format("/api/domains/%s", domain));

        return response;
    }

    public static Response filterEntity(String entity) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.get(String.format("/api/filter/%s", entity));

        return response;
    }

    // entity-endpoint

    public static Response getEntitiesByDomain(String domain, HashMap<String, String> parameters) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.queryParams(parameters)
                .get(String.format("/api/%s/domain", domain));

        return response;
    }

    public static Response createEntityInDomain(HashMap<String, String> parameters) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.queryParams(parameters)
                .post("/api/entities");

        return response;
    }

    public static Response updateEntity(String entity) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.body(entity)
                .post("/api/entities");

        return response;
    }

    public static Response deleteEntity(HashMap<String, String> parameters) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.queryParams(parameters)
                .delete("/api/entities");

        return response;
    }

    public static Response getEntityById(String entityId) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest
                .get(String.format("/api/entities/%s", entityId));

        return response;
    }

    public static Response filterEntityByProperty(String entity, String property) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest
                .get(String.format("/api/filter/%s/%s", entity, property));

        return response;
    }

    // k-generator-endpoint

    public static Response generateIEMSGraph(String sp5CimFilePath, String sp5RdfFilePath, String topologyFolderPath) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "multipart/form-data");

        Response response = httpRequest.multiPart("sp5CimFile", new File(sp5CimFilePath), "text/xml")
                .multiPart("sp5RdfFile", new File(sp5RdfFilePath), "text/xml")
//                .m
                .post("/api/iems/graph");

        return response;
    }


}
