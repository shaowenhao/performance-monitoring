package com.siemens.datalayer.connector.test;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


public class ConnectorCacheControllerEndpoint {

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
                .get("/api/cache/names");

        return response;
    }

    //Cache Controller: cacheStatistics
    @Step("Send a request of 'cacheStatistics'")
    public static Response getCacheStatistics(String name) {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.filter(new AllureRestAssured())
                .get("/api/cache/" + name + "/statistics");

        return response;
    }

    //Cache Controller: allCacheKeys
    @Step("Send a request of 'getallCacheKeys'")
    public static Response getAllCacheKeys(String name) {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.filter(new AllureRestAssured())
                .get("/api/cache/"+ name + "/keys");

        return response;
    }

    //Cache Controller: getCacheValue
    @Step("Send a request of 'getCacheValue'")
    public static Response getCacheValue(String key, String name) {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.filter(new AllureRestAssured())
                .get("/api/cache/"+ name + "/" +key);

        return response;
    }

    //Cache Controller: clean up all caches
    @Step("Send a request of 'deleteAllCaches'")
    public static Response deleteAllCaches() {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.filter(new AllureRestAssured())
                .delete("/cache/allCaches");
        return response;
    }

    //Cache Controller: delete cache by cache name
    @Step("Send a request of 'deleteCache'")
    public static Response deleteCache(String name) {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.filter(new AllureRestAssured())
                .delete("/api/cache/"+ name);
        return response;
    }
}
