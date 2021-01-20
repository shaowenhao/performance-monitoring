package com.siemens.datalayer.iems.test;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class SubscriptionManagementEndpoint {
    private static String BASE_URL = "";
    private static String port = "";
    private static String domain_name = "";

    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
    }

    public static void setPort(String comm_port) {
        port = comm_port;
    }

    public static void setDomain(String domainName) {
        domain_name = domainName;
    }

    public static String getResourcePath()
    {
        String resourcePath;

        switch (domain_name)
        {
            case "iEMS":
                resourcePath = "json-model-schema/iems/";
                break;

            default:
                resourcePath = "";
        }

        return resourcePath;
    }

    public static Response deleteAllSubscriptions(){
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();
        Response response = given().when().contentType("application/json")
                .filter(new AllureRestAssured())
                .delete("/subscriptions");
        return response;
    }

    public static Response deleteSubscriptionById(HashMap<String,String> parameters, String id){
         RestAssured.baseURI = BASE_URL;
         RestAssured.port = Integer.valueOf(port).intValue();
         Response response = given().when().contentType("application/json")
                .pathParam("id", id)
                .queryParams(parameters)
                 .filter(new AllureRestAssured())
                .delete("/subscriptions/{id}");
        return response;

    }

    public static Response getSubscriptions(){
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();
        Response response = given().when().contentType("application/json")
                .filter(new AllureRestAssured())
                .get("/subscriptions");
        return response;
    }

    public static Response registerSubscriptions(HashMap<String,String> parameters, String subSentence){
         RestAssured.baseURI = BASE_URL;
         RestAssured.port = Integer.valueOf(port).intValue();
         Response response = given().when().contentType("application/json")
                .queryParams(parameters)
                .body(subSentence)
                 .filter(new AllureRestAssured())
                .post("/subscriptions");
        return response;
    }
}
