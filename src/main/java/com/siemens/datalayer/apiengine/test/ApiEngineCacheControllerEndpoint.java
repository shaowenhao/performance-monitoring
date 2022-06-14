package com.siemens.datalayer.apiengine.test;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;

public class ApiEngineCacheControllerEndpoint {
    private static String baseUrl = "";

    private static String port = "";

    public static void setBaseUrl(String base_url)
    {
        baseUrl = base_url;
        RestAssured.basePath = "";
    }

    public static void setPort(String comm_port)
    {
        port = comm_port;
    }

    //Cache Controller: allCacheNames
    @Step("Send a request of 'getAllCacheNames'")
    public static Response getAllCacheNames() {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.filter(new AllureRestAssured())
                .get("/cache/names");

        return response;
    }

    // Cache Controller: evictAllCache
    @Step("Send a request of 'evictAllCache'")
    public static Response evictAllCache()
    {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("content-type","application/json");

        Response response = httpRequest.filter(new AllureRestAssured())
                .delete("/cache");
        return response;
    }

    // Cache Controller: allCacheKeys
    @Step("Send a request of 'getallCacheKeys'")
    public static Response getAllCacheKeys(String name) {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.filter(new AllureRestAssured())
                .get("/cache/"+ name + "/keys");

        return response;
    }

    // Cache Controller: getCacheValue
    @Step("Send a request of 'getCacheValue'")
    public static Response getCacheValue(String name, String key) {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.filter(new AllureRestAssured())
                .get("/cache/"+ name + "/" + key);

        return response;
    }
}
