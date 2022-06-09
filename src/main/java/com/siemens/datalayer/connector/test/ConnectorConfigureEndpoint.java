package com.siemens.datalayer.connector.test;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import com.alibaba.fastjson.JSONObject;

public class ConnectorConfigureEndpoint {

    private static String baseUrl = "";

    private static String port = "";

    private static String domainName = "";

    public static void setBaseUrl(String base_url)
    {
        baseUrl = base_url;
        RestAssured.basePath = "";
    }

    public static void setPort(String comm_port)
    {
        port = comm_port;
    }

    public static void setDomainName(String domain_name)
    {
        domainName = domain_name;
    }

    // Test Developer Tools:clear all cache
    @Step("send a request of 'clear all cache'")
    public static Response clearAllCache()
    {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("content-type","application/json");

        Response response = httpRequest.filter(new AllureRestAssured())
                .get("/cache/clean");
        return response;
    }


   // connector-domain-controller:getAllConnectors
    @Step("Send a request of 'getAllConnectors'")
    public static Response getAllConnectors()
    {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("content-type","application/json");

        Response response = httpRequest.filter(new AllureRestAssured())
                                        .get("/connectors");

        return response;
    }

    // connector-domain-controller:save connector
    @Step("Send a request of 'save connector'")
    public static Response saveConnector(String body)
    {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("content-type","application/json");

        Response response = httpRequest.body(body)
                                       .filter(new AllureRestAssured())
                                       .post("/connectors");

        return response;
    }

    // connector-domain-controller:deleteConnector
    @Step("Send a request of 'deleteConnector'")
    public static Response deleteConnector(String connectorName)
    {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("content-type","application/json");

        Response response = httpRequest.filter(new AllureRestAssured())
                                       .delete("/connectors/" + connectorName);
        return response;
    }

    // connector-domain-controller:getConnector
    @Step("Send a request of 'getConnector'")
    public static Response getConnector(String connectorName)
    {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("content-type","application/json");

        Response response = httpRequest.filter(new AllureRestAssured())
                                       .get("/connectors/" + connectorName);

        return response;
    }

    // connector-domain-controller:updateConnector
    @Step("Send a request of 'updateConnector'")
    public static Response updateConnector(String connectorName,String body){
        JSONObject jsonObject = JSONObject.parseObject(body);

        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("content-type","application/json");

        Response response = httpRequest.body(jsonObject)
                                       .filter(new AllureRestAssured())
                                       .put("/connectors/" + connectorName);

        return response;
    }

    //Cache Config Controller:allCacheConfigs
    @Step("Send a request of 'getAllCacheConfigs'")
    public static Response getAllCacheConfigs()
    {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("content-type","application/json");

        Response response = httpRequest.filter(new AllureRestAssured())
                .get("/cache-config" );

        return response;
    }

    //Cache Config Controller:moduleCacheConfig
    @Step("Send a request of 'getModuleCacheConfig'")
    public static Response getModuleCaheConfig(String moduleName)
    {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("content-type","application/json");

        Response response = httpRequest.filter(new AllureRestAssured())
                .get("/cache-config/" + moduleName );

        return response;
    }
}
