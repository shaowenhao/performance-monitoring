package com.siemens.datalayer.apiengine.test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.File;
import java.util.HashMap;

public class SubscriptionEndpoint {

    private static String BASE_URL = "";

    private static String port = "";

    public static void setBaseUrl(String base_url) {
        BASE_URL = base_url;
    }

    public static void setPort(String comm_port) {
        port = comm_port;
    }

    // graph-endpoint

    public static Response getSubscriptions() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port);

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        return httpRequest.get("/subscriptions");
    }

    public static Response register(HashMap<String, String> parameters, String body) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.parseInt(port);

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        return httpRequest.queryParams(parameters).body(body)
                .post("/subscriptions");
    }

    public static Response deleteAllSubscriptions() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.parseInt(port);

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        return httpRequest.delete("/subscriptions");
    }


    public static Response deleteSubscriptionsById(String id) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.parseInt(port);

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        return httpRequest.delete("/subscriptions/%s", id);
    }


}
