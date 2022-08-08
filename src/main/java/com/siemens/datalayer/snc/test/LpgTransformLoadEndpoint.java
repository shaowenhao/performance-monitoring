package com.siemens.datalayer.snc.test;


import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;

public class LpgTransformLoadEndpoint {


    private static String BASE_URL = "";

    private static String port = "";

    public static void setBaseUrl(String base_url)
    {
        BASE_URL = base_url;
        RestAssured.basePath = "";
    }

    public static void setPort(String comm_port)
    {
        port = comm_port;
    }

    // Lpg-Transform-Load Endpoint: get graph
    @Step("Send a query request to get instance-kg grapph")
    public static Response getGraph() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.filter(new AllureRestAssured())
                                    .get("/api/graph/instances/graphs");

        return response;
    }

    // Lpg-Transform-Load Endpoint: list all graph names
    @Step("Send a query request to list all graph names")
    public static Response listAllGraphNames() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.filter(new AllureRestAssured())
                .get("/api/graph/instances/graph-names");

        return response;
    }
}
