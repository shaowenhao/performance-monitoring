package com.siemens.datalayer.jinzu.test;

import com.siemens.datalayer.iems.model.RestConstants;
import com.siemens.datalayer.jinzu.model.JinzuResultConstants;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;

public class JinzuEndpoint {

    private static String BASE_URL = "http://140.231.89.85/";

    private static String PORT = "31059";

    private static String PRE_ASSET = "";

    private static String PRE_DATA = "";

    public static void setBaseUrl(String base_url)
    {
        BASE_URL = base_url;
    }

    public static void setPort(String comm_port)
    {
        PORT = comm_port;
    }
    public static void setPreAsset(String pre_asset)
    {
        PRE_ASSET = pre_asset;
    }
    public static void setPreData(String pre_data)
    {
        PRE_DATA = pre_data;
    }

    private static Response getResponseByParameters(String api, HashMap<String, Object> parameters) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(PORT).intValue();
        RestAssured.basePath = api;

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = null;
        if(parameters == null) {
            response = httpRequest
                    .filter(new AllureRestAssured()).get();
        }else {
            response = httpRequest
                    .queryParams(parameters)
                    .filter(new AllureRestAssured()).get();
        }

        return response;
    }

    private static Response postResponseByBody(String api, String body) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(PORT).intValue();
        RestAssured.basePath = api;

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.body(body)
                .filter(new AllureRestAssured())
                .post();

        return response;
    }

    private static Response postResponseByParameters(String api, HashMap<String, Object> parameters) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(PORT).intValue();
        RestAssured.basePath = api;

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.queryParams(parameters)
                .filter(new AllureRestAssured())
                .post();

        return response;
    }

    public static Response graphQuery(String api) {
        return postResponseByBody(PRE_ASSET + JinzuResultConstants.GRAPH_QUERY, api);
    }


    public static Response leaseDetail(String api) {
        return postResponseByBody(PRE_ASSET + JinzuResultConstants.GRAPH_QUERY, api);
    }

    public static Response siteDetail(String api) {
        return postResponseByBody(PRE_ASSET + JinzuResultConstants.GRAPH_QUERY, api);
    }



}
