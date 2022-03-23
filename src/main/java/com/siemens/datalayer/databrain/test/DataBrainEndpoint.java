package com.siemens.datalayer.databrain.test;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

public class DataBrainEndpoint {

    // desigoCC datasource Endpoint: getToken
    @Step("Send a request of 'getToken'")
    public static Response getToken(String httpsBaseUrl,String port,String granttype,String username,String password)
    {
        RestAssured.baseURI = httpsBaseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given().relaxedHTTPSValidation();

        String body = "grant_type=" + granttype + "&"
                    + "username=" + username + "&"
                    + "password=" + password;

        Response response = httpRequest.body(body)
                .contentType("application/x-www-form-urlencoded;charset=UTF-8")
                .filter(new AllureRestAssured())
                .post("/api/token");

        return response;
    }

    // desigoCC datasource Endpoint: getValue
    @Step("Send a request of 'getValue'")
    public static Response getValue(String httpsBaseUrl,String port,String accessToken,String id)
    {
        RestAssured.baseURI = httpsBaseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given().relaxedHTTPSValidation();
        httpRequest.header("Content-Type","application/json");
        httpRequest.header("Authorization","Bearer " + accessToken);

        Response response = httpRequest.filter(new AllureRestAssured())
                .get("/api/values/" + id);

        return response;
    }

    // desigoCC datasource Endpoint: /signalr/negotiate
    @Step("Send a request of '/signalr/negotiate'")
    public static Response signalrNegotiate(String httpsBaseUrl,String port,String accessToken,String clientProtocol,String connectionData)
    {
        RestAssured.baseURI = httpsBaseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given().relaxedHTTPSValidation();
        httpRequest.header("Content-Type","application/json");
        httpRequest.header("Authorization","Bearer " + accessToken);

        Map<String,String> queryParameters = new HashMap<>();
        queryParameters.put("clientProtocol",clientProtocol);
        queryParameters.put("connectionData",connectionData);

        Response response = httpRequest.queryParams(queryParameters)
                .filter(new AllureRestAssured())
                .post("/signalr/negotiate");

        return response;
    }

    // desigoCC datasource Endpoint: /api/sr/eventssubscriptions/channelize
    @Step("Send a request of '/api/sr/eventssubscriptions/channelize'")
    public static Response eventssubscriptions(String httpsBaseUrl, String port, String accessToken, String requestId, String connectionId)
    {
        RestAssured.baseURI = httpsBaseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given().relaxedHTTPSValidation();
        httpRequest.header("Content-Type","application/json");
        httpRequest.header("Authorization","Bearer " + accessToken);

        Response response = httpRequest.filter(new AllureRestAssured())
                .post("/api/sr/eventssubscriptions/channelize" + "/" + requestId + "/" + connectionId);

        return response;
    }

    // enlighted datasource Endpoint: /ems/api/org/sensor/v2/stats/floor
    @Step("Send a request of '/ems/api/org/sensor/v2/stats/floor'")
    public static Response getSensorDetailsbyFloor(String httpsBaseUrl,String port,
                                                   String apiKey,String authorization,String ts,Map<String, String> requestParameters)
    {
        RestAssured.baseURI = httpsBaseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given().relaxedHTTPSValidation();
        httpRequest.header("Content-Type","application/json");
        httpRequest.header("Accept","application/json");
        httpRequest.header("ApiKey",apiKey);
        httpRequest.header("Authorization",authorization);
        httpRequest.header("ts",ts);

        String floorId = requestParameters.get("floor_id");
        String fromDate = requestParameters.get("from_date");
        String toDate = requestParameters.get("to_date");

        Response response = httpRequest.filter(new AllureRestAssured())
                .get("/ems/api/org/sensor/v2/stats/floor" + "/" + floorId + "/" + fromDate + "/" + toDate);

        return response;
    }

    // enlighted datasource Endpoint: /ems/api/org/floor/list
    @Step("Send a request of '/ems/api/org/floor/list'")
    public static Response getllFloors(String httpsBaseUrl,String port,String apiKey,String authorization,String ts)
    {
        RestAssured.baseURI = httpsBaseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given().relaxedHTTPSValidation();
        httpRequest.header("Content-Type","application/json");
        httpRequest.header("ApiKey",apiKey);
        httpRequest.header("Authorization",authorization);
        httpRequest.header("ts",ts);

        Response response = httpRequest.filter(new AllureRestAssured())
                .get("/ems/api/org/floor/list");

        return response;
    }
}
