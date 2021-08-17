package com.siemens.datalayer.connector.test;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.response.Response;

public class ConnectorOtherInterfacesEndpoint {
    private static String baseUrl = "";

    private static String port = "";

    private static String domainName = "";

    public static void setBaseUrl(String base_url)
    {
        baseUrl = base_url;
        RestAssured.basePath = "";
    }

    public static void setPort(String comm_port){port = comm_port;}

    public static void setDomainName(String domain_name){domainName = domain_name;}

    // Test Developer Tools:clearRedisCaches
    @Step("send a request of 'clearRedisCaches'")
    public static Response clearRedisCaches()
    {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("content-type","application/json");

        Response response = httpRequest.filter(new AllureRestAssured())
                                       .get("/api/cache-tools/redis-clearance");
        return response;
    }
}
