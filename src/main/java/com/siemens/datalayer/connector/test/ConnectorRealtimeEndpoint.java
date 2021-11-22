package com.siemens.datalayer.connector.test;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

public class ConnectorRealtimeEndpoint {
    private static String baseUrl = "";

    private static String port = "";

    public static void setBaseUrl(String base_url)
    {
        baseUrl = base_url;
        RestAssured.basePath = "";
    }

    public static void setPort(String comm_port){port = comm_port;}

    // Test Plugin Controller:findAllPlugins
    @Step("send a request of 'findAllPlugins'")
    public static Response findAllPlugins()
    {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("content-type","application/json");

        Response response = httpRequest.filter(new AllureRestAssured())
                .get("/api/plugin");
        return response;
    }

    public static Response pluginOperation(String operator,String pluginId) {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("content-type","application/json");

        Map<String,String> requestRarameters = new HashMap<>();
        requestRarameters.put("operator",operator);

        Response response = httpRequest.queryParams(requestRarameters)
                                       .filter(new AllureRestAssured())
                                       .post("/api/plugin/" + pluginId);
        return response;
    }
}
