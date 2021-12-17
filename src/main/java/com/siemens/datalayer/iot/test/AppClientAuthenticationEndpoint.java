package com.siemens.datalayer.iot.test;

import com.siemens.datalayer.iot.util.OltuJavaClient;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class AppClientAuthenticationEndpoint {
    private static String baseUrl = "";

    private static String port = "";

    public static void setBaseUrl(String base_url)
    {
        baseUrl = base_url;
        RestAssured.basePath = "";
    }

    public static void setPort(String comm_port) { port = comm_port; }

    @Step("send a request of 'getSchema' without authentication")
    public static Response getSchema()
    {

        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = given().relaxedHTTPSValidation();
        httpRequest.header("content-type","application/json");

        Response response = httpRequest.filter(new AllureRestAssured())
                .get("/jinzu-test/api-engine/graphql/schema");

        return response;
    }

    @Step("send a request of 'getSchema' with authentication")
    public static Response getSchema(Map<String,String> parameters)
    {
        String accessToken = OltuJavaClient.getAccessTokenUseClientCredentials(parameters.get("accessTokenUrl"),
                parameters.get("clientId"),parameters.get("clientSecret"));

        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = given().relaxedHTTPSValidation();
        httpRequest.header("Authorization","Bearer "+accessToken);
        httpRequest.header("content-type","application/json");

        Response response = httpRequest.filter(new AllureRestAssured())
                .get("/jinzu-test/api-engine/graphql/schema");

        return response;
    }

    @Step("send a request of 'getAllCacheStats' without authentication")
    public static Response getAllCacheStats()
    {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = given();
        httpRequest.header("content-type","application/json");

        Response response = httpRequest.filter(new AllureRestAssured())
                .get("/jinzu-test/api-engine/cache/stats");

        return response;
    }

    @Step("send a request of 'getAllCacheStats' with authentication")
    public static Response getAllCacheStats(Map<String,String> parameters)
    {
        String accessToken = OltuJavaClient.getAccessTokenUseClientCredentials(parameters.get("accessTokenUrl"),
            parameters.get("clientId"),parameters.get("clientSecret"));

        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = given().relaxedHTTPSValidation();
        httpRequest.header("Authorization","Bearer "+accessToken);
        httpRequest.header("content-type","application/json");

        Response response = httpRequest.filter(new AllureRestAssured())
                .get("/jinzu-test/api-engine/cache/stats");

        return response;
    }
}
