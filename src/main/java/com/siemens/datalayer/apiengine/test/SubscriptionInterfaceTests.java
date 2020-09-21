package com.siemens.datalayer.apiengine.test;

import com.siemens.datalayer.apiengine.model.EntitiesApiResponse;
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

@Epic("Ontology Interface")
@Feature("Rest API")
public class SubscriptionInterfaceTests {

    @Parameters({"baseUrl", "port"})
    @BeforeClass
    public void setEndpoint(@Optional("http://140.231.89.85") String baseUrl, @Optional("31111") String port) {
        SubscriptionEndpoint.setBaseUrl(baseUrl);
        SubscriptionEndpoint.setPort(port);
    }


    @Test(priority = 0, description = "Test subscriptions interface: Create one subscription and get all subscriptions, at last, delete subscription.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if get all subscriptions return with entities null.")
    @Story("Get all subscriptions")
    public void getSubscriptions() {
        Reporter.log("Create one subscription first");

        HashMap<String, String> queryParameters = new HashMap<>();

        queryParameters.put("clientId", "automation");

        String body = "subscription {\n" +
                "\n" +
                "    Analog(cond:\"{Siid:{_eq:36341}}\")\n" +
                "        {\n" +
                "            localName\n" +
                "            mRID\n" +
                "            maxValue\n" +
                "            measurementType\n" +
                "            minValue\n" +
                "            name\n" +
                "            normalValue\n" +
                "            pathName\n" +
                "            positiveFlowIn\n" +
                "            generate_SensorData(cond:\"\"){\n" +
                "                Siid\n" +
                "                modelId\n" +
                "                updateTime\n" +
                "                value\n" +
                "            }\n" +
                "        }\n" +
                "    \n" +
                "}";

        Response response = SubscriptionEndpoint.register(queryParameters, body);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        EntitiesApiResponse rspBody = response.getBody().as(EntitiesApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());


        JsonPath jsonPathEvaluator = response.jsonPath();

        HashMap m = (HashMap) jsonPathEvaluator.get("data");

        Assert.assertNotNull(m);
        List<String> l = new ArrayList<String>(
                Arrays.asList(
                        "SensorData",
                        "Analog"
                )
        );
        List<String> ll = (List<String>) m.get("entities");
        ll.forEach(key -> Assert.assertTrue(l.contains(key)));
        List<String> l2 = (List<String>) m.get("clients");
        Assert.assertTrue(l2.contains("automation"));
        String id = m.get("id").toString();

        Response response2 = SubscriptionEndpoint.getSubscriptions();

        Reporter.log("Response status is " + response2.getStatusCode());

        Reporter.log("Response Body is =>  " + response2.getBody().asString());

        HashMap[] rspBody2 = response2.getBody().as(HashMap[].class);

        Assert.assertTrue(rspBody2.length >= 1);
        Assert.assertNotNull(Arrays.stream(rspBody2).filter(d -> id.equals(d.get("id").toString())).findAny().orElse(null));


        Response response3 = SubscriptionEndpoint.deleteSubscriptionsById(id);

        Reporter.log("Response status is " + response3.getStatusCode());

        Reporter.log("Response Body is =>  " + response3.getBody().asString());

        EntitiesApiResponse rspBody3 = response3.getBody().as(EntitiesApiResponse.class);


        Assert.assertEquals("Successfully", rspBody3.getMessage());
        Assert.assertEquals(100000, rspBody3.getCode());

    }


    @Test(priority = 0, description = "Test subscriptions interface: Create one subscription and delete all subscription.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if delete all subscriptions return correct.")
    @Story("Delete all subscriptions")
    public void deleteSubscriptions() {
        Reporter.log("Create one subscription first");

        HashMap<String, String> queryParameters = new HashMap<>();

        queryParameters.put("clientId", "automation");

        String body = "subscription {\n" +
                "\n" +
                "    Analog(cond:\"{Siid:{_eq:36341}}\")\n" +
                "        {\n" +
                "            localName\n" +
                "            mRID\n" +
                "            maxValue\n" +
                "            measurementType\n" +
                "            minValue\n" +
                "            name\n" +
                "            normalValue\n" +
                "            pathName\n" +
                "            positiveFlowIn\n" +
                "            generate_SensorData(cond:\"\"){\n" +
                "                Siid\n" +
                "                modelId\n" +
                "                updateTime\n" +
                "                value\n" +
                "            }\n" +
                "        }\n" +
                "    \n" +
                "}";

        Response response = SubscriptionEndpoint.register(queryParameters, body);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        EntitiesApiResponse rspBody = response.getBody().as(EntitiesApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());


        JsonPath jsonPathEvaluator = response.jsonPath();

        HashMap m = (HashMap) jsonPathEvaluator.get("data");

        Assert.assertNotNull(m);
        List<String> l = new ArrayList<String>(
                Arrays.asList(
                        "SensorData",
                        "Analog"
                )
        );
        List<String> ll = (List<String>) m.get("entities");
        ll.forEach(key -> Assert.assertTrue(l.contains(key)));
        List<String> l2 = (List<String>) m.get("clients");
        Assert.assertTrue(l2.contains("automation"));
        String id = m.get("id").toString();

        Response response2 = SubscriptionEndpoint.getSubscriptions();

        Reporter.log("Response status is " + response2.getStatusCode());

        Reporter.log("Response Body is =>  " + response2.getBody().asString());

        HashMap[] rspBody2 = response2.getBody().as(HashMap[].class);

        Assert.assertTrue(rspBody2.length >= 1);
        Assert.assertNotNull(Arrays.stream(rspBody2).filter(d -> id.equals(d.get("id").toString())).findAny().orElse(null));


        Response response3 = SubscriptionEndpoint.deleteAllSubscriptions();

        Reporter.log("Response status is " + response3.getStatusCode());

        Reporter.log("Response Body is =>  " + response3.getBody().asString());

        Assert.assertEquals(200, response3.getStatusCode());

        Response response4 = SubscriptionEndpoint.getSubscriptions();

        Reporter.log("Response status is " + response4.getStatusCode());

        Reporter.log("Response Body is =>  " + response4.getBody().asString());

        HashMap[] rspBody4 = response4.getBody().as(HashMap[].class);

        Assert.assertTrue(rspBody4.length == 0);
    }


    @Test(priority = 0, description = "Test subscriptions interface: delete subscription with invalid id.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if delete subscriptions by invalid id return correct.")
    @Story("Delete subscriptions by invalid id")
    public void deleteSubscriptionByInvalidId() {
        Reporter.log("Delete subscription by invalid id");

        Response response = SubscriptionEndpoint.deleteSubscriptionsById("invalid-id");

        EntitiesApiResponse rspBody = response.getBody().as(EntitiesApiResponse.class);

        Assert.assertEquals("Subscription invalid-id not exists!", rspBody.getMessage());
        Assert.assertEquals(107002, rspBody.getCode());
        Assert.assertNull(rspBody.getData());
    }


}
