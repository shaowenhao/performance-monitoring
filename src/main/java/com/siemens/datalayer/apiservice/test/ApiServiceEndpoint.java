package com.siemens.datalayer.apiservice.test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;

public class ApiServiceEndpoint {

    private static String BASE_URL = "";

    private static String port = "";

    public static void setBaseUrl(String base_url) {
        BASE_URL = base_url;
    }

    public static void setPort(String comm_port) {
        port = comm_port;
    }

    public static Response getDevicesByType(HashMap<String, String> parameters) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.queryParams(parameters).get("/datalayer/api/v1/asset/getDevicesByType");

        return response;
    }

    public static Response getSensorByDeviceId(HashMap<String, String> parameters) {

        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.queryParams(parameters).get("/datalayer/api/v1/asset/getSensorByDeviceId");

        return response;
    }

    public static Response listAllDeviceTypes() {

        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.get("/datalayer/api/v1/asset/listAllDeviceTypes");

        return response;
    }

    public static Response listDeviceTypes() {

        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

        Response response = httpRequest.get("/datalayer/api/v1/asset/listDeviceTypes");

        return response;
    }

    public static Response deleteSubscriptions(String id) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest
                .delete("/datalayer/api/v1/data/deleteSubscriptions/" + id);

        return response;
    }

	public static Response getSensorDataByDeviceId(String body) {
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();

		RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

		Response response = httpRequest.body(body)
				.post("/datalayer/api/v1/data/getSensorDataByDeviceId");

		return response;
	}


	public static Response getSensorDataBySensorId(String body) {
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();

		RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

		Response response = httpRequest.body(body)
				.post("/datalayer/api/v1/data/getSensorDataBySensorId");

		return response;
	}

	public static Response subscriptionsByDeviceId(String body) {
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();

		RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

		Response response = httpRequest.body(body)
				.post("/datalayer/api/v1/data/subscriptionsByDeviceId");

		return response;
	}

	public static Response subscriptionsBySensorId(String body) {
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(port).intValue();

		RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

		Response response = httpRequest.body(body)
				.post("/datalayer/api/v1/data/subscriptionsBySensorId");

		return response;
	}

}