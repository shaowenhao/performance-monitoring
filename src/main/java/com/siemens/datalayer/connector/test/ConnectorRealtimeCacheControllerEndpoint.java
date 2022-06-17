package com.siemens.datalayer.connector.test;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ConnectorRealtimeCacheControllerEndpoint {

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

    // Cache Controller: allCacheKeys
    @Step("Send a request of 'getallCacheKeys'")
    public static Response getAllCacheKeys(String name) {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.filter(new AllureRestAssured())
                .get("/api/cache/"+ name + "/keys");

        return response;
    }

    // Cache Controller: getCacheValue
    @Step("Send a request of 'getCacheValue'")
    public static Response getCacheValue(String name, String key) {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.filter(new AllureRestAssured())
                .get("/api/cache/"+ name + "/" + key);

        return response;
    }
}
