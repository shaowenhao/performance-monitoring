package com.siemens.datalayer.apiengine.test;

import com.siemens.datalayer.apiengine.model.EntitiesApiResponse;
import com.siemens.datalayer.apiengine.model.GraphqlApiResponse;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Epic("Regression Tests")
@Feature("Ontology Rest API Tests")
public class OntologyInterfaceTests {

    @Parameters({"baseUrl", "port"})
    @BeforeClass
    public void setOntologyEndpoint(@Optional("http://140.231.89.85") String baseUrl, @Optional("31987") String port) {
        OntologyEndpoint.setBaseUrl(baseUrl);
        OntologyEndpoint.setPort(port);
    }

//    @Test(priority = 0, description = "Test ontology interface: Get all ontology domains without entities.")
//    @Severity(SeverityLevel.BLOCKER)
//    @Description("Send a request to SUT and verify if all domains return with entities null.")
//    @Story("Ontology Interface API design")
//    public void getOntologyDomainsWithoutEntities() {
//        Reporter.log("Send request to ontology api with withEntities false");
//
//        HashMap<String, String> queryParameters = new HashMap<>();
//        queryParameters.put("withEntities", "false");
//
//        Response response = OntologyEndpoint.getDomains(queryParameters);
//
//        Reporter.log("Response status is " + response.getStatusCode());
//
//        Reporter.log("Response Body is =>  " + response.getBody().asString());
//
//        EntitiesApiResponse rspBody = response.getBody().as(EntitiesApiResponse.class);
//
//        Assert.assertEquals("Successfully", rspBody.getMessage());
//        Assert.assertEquals(100000, rspBody.getCode());
//
//
//        JsonPath jsonPathEvaluator = response.jsonPath();
//
//        Assert.assertNotNull(jsonPathEvaluator.get("data"));
//
//    }



    @Test(priority = 0, description = "Test ontology interface: Get ontology domains by without entities.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if get domains by id without entities return with entities null.")
    @Story("Ontology Interface API design")
    public void getOntologyDomainsWithIdWithoutEntities() {
        Reporter.log("Send request to ontology api with withEntities false");

        HashMap<String, String> queryParameters = new HashMap<>();

        queryParameters.put("withEntities", "false");

        Response response = OntologyEndpoint.getDomains(queryParameters);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        EntitiesApiResponse rspBody = response.getBody().as(EntitiesApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());


        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));

        Assert.assertNull(jsonPathEvaluator.get("data[0].entities"));

        int id = jsonPathEvaluator.getInt("data[0].id");

        Reporter.log("Send request to ontology api with domain id and withEntities false");

        HashMap<String, String> queryParameters2 = new HashMap<>();

        queryParameters2.put("domainId", String.valueOf(id));
        queryParameters2.put("withEntities", "false");

        Response response2 = OntologyEndpoint.getDomains(queryParameters);

        Reporter.log("Response status is " + response2.getStatusCode());

        Reporter.log("Response Body is =>  " + response2.getBody().asString());

        EntitiesApiResponse rspBody2 = response2.getBody().as(EntitiesApiResponse.class);

        Assert.assertEquals("Successfully", rspBody2.getMessage());
        Assert.assertEquals(100000, rspBody2.getCode());


        JsonPath jsonPathEvaluator2 = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));


    }


    @Test(priority = 0, description = "Test ontology interface: Get ontology domains by with entities.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if get domains by id with entities return with entities.")
    @Story("Ontology Interface API design")
    public void getOntologyDomainsWithIdWithEntities() {
        Reporter.log("Send request to ontology api with withEntities true");

        HashMap<String, String> queryParameters = new HashMap<>();

        queryParameters.put("withEntities", "true");

        Response response = OntologyEndpoint.getDomains(queryParameters);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        EntitiesApiResponse rspBody = response.getBody().as(EntitiesApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());


        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));

        Assert.assertNotNull(jsonPathEvaluator.get("data[0].entities"));

        int id = jsonPathEvaluator.getInt("data[0].id");

        Reporter.log("Send request to ontology api with domain id and withEntities true");

        HashMap<String, String> queryParameters2 = new HashMap<>();

        queryParameters2.put("domainId", String.valueOf(id));
        queryParameters2.put("withEntities", "true");

        Response response2 = OntologyEndpoint.getDomains(queryParameters);

        Reporter.log("Response status is " + response2.getStatusCode());

        Reporter.log("Response Body is =>  " + response2.getBody().asString());

        EntitiesApiResponse rspBody2 = response2.getBody().as(EntitiesApiResponse.class);

        Assert.assertEquals("Successfully", rspBody2.getMessage());
        Assert.assertEquals(100000, rspBody2.getCode());


        JsonPath jsonPathEvaluator2 = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));
        Assert.assertNotNull(jsonPathEvaluator.get("data[0].entities"));


    }


//    @Test(priority = 0, description = "Test ontology interface: Get ontology domains by invalid id.")
//    @Severity(SeverityLevel.BLOCKER)
//    @Description("Send a request to SUT and verify if get domains by invalid id.")
//    @Story("Ontology Interface API design")
//    public void getOntologyDomainsWithInvalidId() {
//        Reporter.log("Send request to ontology api with invalid id");
//
//        HashMap<String, String> queryParameters = new HashMap<>();
//
//        queryParameters.put("domainId", "invalid");
//        queryParameters.put("withEntities", "true");
//
//        Response response = OntologyEndpoint.getDomains(queryParameters);
//
//        Reporter.log("Response status is " + response.getStatusCode());
//
//        Reporter.log("Response Body is =>  " + response.getBody().asString());
//
//        EntitiesApiResponse rspBody = response.getBody().as(EntitiesApiResponse.class);
//
//        Assert.assertEquals("Successfully", rspBody.getMessage());
//        Assert.assertEquals(100000, rspBody.getCode());
//
//    }

}
