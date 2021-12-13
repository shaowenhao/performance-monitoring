package com.siemens.datalayer.connector.test;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.File;
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

    // Test Plugin Controller:pluginOperation
    @Step("send a request of 'pluginOperation'")
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

    // Test Plugin Controller:loadBundle
    @Step("send a request of 'loadBundle'")
    public static Response loadBundle(File file){
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("content-type","multipart/form-data");

        Response response = httpRequest.multiPart("file",file)
                                       .filter(new AllureRestAssured())
                                       .post("/api/plugin/bundle");
        return response;
    }

    @Step("send a request of 'loadBundle'")
    public static Response getPluginInformation(String pluginId){
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("content-type","application/json");

        Map<String,String> requestRarameters = new HashMap<>();
        requestRarameters.put("pluginId",pluginId);

        Response response = httpRequest.queryParams(requestRarameters)
                                       .filter(new AllureRestAssured())
                                       .get("/api/pluginToolkit/pluginInfo");
        return response;
    }

    public static Response pluginInstanceStart(String connectorName,String locatorName,String stopTime)
    {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("content-type","application/json");

        Map<String,String> requestRarameters = new HashMap<>();
        requestRarameters.put("connectorName",connectorName);
        requestRarameters.put("locatorName",locatorName);
        requestRarameters.put("stopTime",stopTime);

        Response response = httpRequest.queryParams(requestRarameters)
                .filter(new AllureRestAssured())
                .post("/api/pluginToolkit/pluginInstance");
        return response;
    }

    public static Response pluginRunningLogInformation(String connectorName,String locatorName)
    {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("content-type","application/json");

        Map<String,String> requestRarameters = new HashMap<>();
        requestRarameters.put("connectorName",connectorName);
        requestRarameters.put("locatorName",locatorName);

        Response response = httpRequest.queryParams(requestRarameters)
                .filter(new AllureRestAssured())
                .get("/api/pluginToolkit/pluginInstanceLog");
        return response;
    }
}