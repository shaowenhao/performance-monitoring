package com.siemens.datalayer.iems.test;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.testng.annotations.DataProvider;

/**
 * @author z003vffr
 * @date 11/6/2020 1:12 PM
 */
public class ConnectorEndpoints {

    private static String BASE_URL = "";

    private static String port = "";

    public static void setBaseUrl(String base_url)
    {
        BASE_URL = base_url;
    }

    public static void setPort(String comm_port)
    {
        port = comm_port;
    }

    // Connector Interface: Get All Entities name
    @Step("Send a request of 'Get All Entities Name'")
    public static Response getAllEntitiesName() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();
        RestAssured.basePath = "api/connector/entities";

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.filter(new AllureRestAssured())
            .get();

        return response;
    }

    // Connector Interface: Get concept model data by condition
    @Step("Send a request of 'Get concept model data by condition'")
    public static Response getConceptModelDataByCondition(HashMap<String, String> parameters)
    {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();
        RestAssured.basePath = "api/connector/searchData";

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.queryParams(parameters)
            .filter(new AllureRestAssured())
            .get();

        return response;
    }


}
