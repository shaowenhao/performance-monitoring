package com.siemens.datalayer.apiengine.test;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;

public class ApiEngineEndpoint {

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

    // API Engine Query Endpoint: getData (restful interface)
    @Step("Send a restful query request 'getData' to data-layer-api-engine")
    public static Response getEntities(HashMap<String, String> parameters) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");

		Response response = httpRequest.queryParams(parameters)
				   					   .filter(new AllureRestAssured())
				   					   .get("/entities");

        return response;
    }
    
    // API Engine Query Endpoint: getData (graphql interface)
    @Step("Send a graphql query request 'getData' to data-layer-api-engine")
    public static Response postGraphql(String query) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.body(query)
        							   .contentType("application/json")
					        		   .filter(new AllureRestAssured())
					                   .post("/graphql");

        return response;
    }

    // API Engine Query Endpoint with HTTPS: getData (graphql interface)
    @Step("Send a graphql https query request 'getData' to data-layer-api-engine")
    public static Response postHttpsGraphql(String query,String accessToken) {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given().relaxedHTTPSValidation();
        httpRequest.header("Authorization","Bearer "+accessToken);
        Response response = httpRequest.body(query)
                .contentType("application/json")
                .filter(new AllureRestAssured())
                .post("jinzu-test/api-engine/graphql");

        return response;
    }
    public static Response getGraphqlApiSchema() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.filter(new AllureRestAssured())
                					   .get("/graphql-api/schema");

        return response;
    }

}
