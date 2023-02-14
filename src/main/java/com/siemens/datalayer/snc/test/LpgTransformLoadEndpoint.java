package com.siemens.datalayer.snc.test;


import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                .get("/instances/graph-names");

        return response;
    }


    @Step("Send a request to create instance graph")
    public static Response createInstanceGraph(String graphName)
    {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();
        RequestSpecification httpRequest = RestAssured.given();

        Map<String,String> parameters = new HashMap<String,String>();
        parameters.put("name",graphName);
        Response response = httpRequest.params(parameters)
                .filter(new AllureRestAssured())
                .post("/instances/graph");
        return response;
    }


    @Step("Send a request to delete instance graph")
    public static Response deleteInstanceGraph(String graphName)
    {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();
        RequestSpecification httpRequest = RestAssured.given();

        Map<String,String> parameters = new HashMap<String,String>();
        parameters.put("name",graphName);
        Response response = httpRequest.params(parameters)
                .filter(new AllureRestAssured())
                .delete("/instances/graph");
        System.out.println(response.prettyPrint());
        return response;
    }

    /**
     * lpg remove this interface comfrin with developer
     */
//    @Step("Send a request to generate kg")
//    public static Response generateKg(String entityLabels,String graphName,String graphql)
//    {
//        RestAssured.baseURI = BASE_URL;
//        RestAssured.port = Integer.valueOf(port).intValue();
//        RequestSpecification httpRequest = RestAssured.given();
//
//        Map<String,String> parameters = new HashMap<String,String>();
//        parameters.put("graphName",graphName);
//
//        String[] entityLabelsArr = entityLabels.split(",");
//        List<String> entityLabelList = Arrays.asList(entityLabelsArr);
//        //Specify a multi-value form parameter
//        Response response = httpRequest.queryParam("entityLabels",entityLabelList)
//                .queryParams(parameters)
//                .body(graphql)
//                .filter(new AllureRestAssured())
//                .post("/api/graph/instance/generate");
//        return response;
//    }

    // Lpg-Transform-Load Endpoint: search graph
    @Step("Send a query request to get instance-kg grapph")
    public static Response searchGraph(String graphName, String keyWord) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");
        Map<String,String> parameters = new HashMap<String,String>();
        parameters.put("graphName",graphName);
        parameters.put("keyword",keyWord);
        Response response = httpRequest.params(parameters)
                .filter(new AllureRestAssured())
                .get("/instances/search");

        return response;
    }

    /**
     * lpg remove this interface comfrin with developer
     * @param kafkaData
     * @return
     */
//    @Step("Send a request to generate instance kg though kafka")
//    public static Response generateInstanceKg(String kafkaData)
//    {
//        RestAssured.baseURI = BASE_URL;
//        RestAssured.port = Integer.valueOf(port).intValue();
//        RequestSpecification httpRequest = RestAssured.given();
//
//        //Specify a multi-value form parameter
//        Response response = httpRequest
//                .body(kafkaData)
//                .filter(new AllureRestAssured())
//                .post("/api/graph/instance/generate/kafka");
//        return response;
//    }
}
