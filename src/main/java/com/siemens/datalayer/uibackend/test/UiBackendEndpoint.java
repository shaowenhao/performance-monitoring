package com.siemens.datalayer.uibackend.test;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

public class UiBackendEndpoint {
    private static String BASE_URL = "";
    private static String port = "";
    private static String entitymgt_port = "";

    public static void setBaseUrl(String base_url)
    {
        BASE_URL = base_url;
        RestAssured.basePath = "";
    }

    public static void setPort(String comm_port)
    {
        port = comm_port;
    }

    public static void setEntitymgt_port(String entitymanagement_port) {
        entitymgt_port = entitymanagement_port;
    }

    // UI Backend Endpoint: graphsPublish
    @Step("Send a request of 'graphsPublish'")
    public static Response graphsPublish(String body)
    {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.body(body)
                .filter(new AllureRestAssured())
                .post("/api/kg/graphs/publish");
        return response;
    }

    // UI Backend Endpoint: graphsCheck
    @Step("Send a request of 'graphsCheck'")
    public static Response graphsCheck(String body)
    {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.body(body)
                .filter(new AllureRestAssured())
                .post("/api/kg/graphs/check");
        return response;
    }

    // UI Backend Endpoint: getEntities
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
                .get("/api/kg/entities");
        return response;
    }

    // UI Backend Endpoint: getRelations
    @Step("Send a request of 'getRelations'")
    public static Response getRelations(String labels)
    {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();

        Map<String,String> parameters = new HashMap<String,String>();
        parameters.put("labels",labels);
        parameters.put("order","asc");
        Response response = httpRequest.params(parameters)
                .filter(new AllureRestAssured())
                .get("/api/kg/relations");

        return response;
    }
}
