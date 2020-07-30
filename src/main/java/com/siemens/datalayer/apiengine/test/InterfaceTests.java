package com.siemens.datalayer.apiengine.test;

import com.siemens.datalayer.apiengine.model.EntitiesApiResponse;

import com.siemens.datalayer.apiengine.model.GraphqlApiResponse;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.awt.*;
import java.util.HashMap;

@Epic("Regression Tests")
@Feature("Connector Rest API Tests")
public class InterfaceTests {

    @Parameters({"baseUrl", "port"})
    @BeforeTest
    public void beforeTest(@Optional("http://140.231.89.85") String baseUrl, @Optional("31101") String port) {
        Endpoint.setBaseUrl(baseUrl);
        Endpoint.setPort(port);
    }

    @Test(priority = 0, description = "Test api engine interface: Get Entities without filter.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if all the analog entities names can be read out.")
    @Story("Api engine Interface API design")
    public void getAllInstanceOfOneEntityByRestful() {
        Reporter.log("Send request to entities api with root=Analog depth=1");

        HashMap<String, String> queryParameters = new HashMap<>();
        queryParameters.put("depth", "1");
        queryParameters.put("root", "Analog");

        Response response = Endpoint.getEntities(queryParameters);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        EntitiesApiResponse rspBody = response.getBody().as(EntitiesApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());

    }


    @Test(priority = 0, description = "Test api engine interface: Query all instance of one entity by graphql.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if all the analog entities names can be read out.")
    @Story("Api engine Interface API design")
    public void getAllInstanceOfOneEntityByGraphQL() {
        Reporter.log("Send request to graphql api with graphql to get all instance of one entity");

        String query = "{\n" +
                "\tAnalog {\n" +
                "\t  Analog_id\n" +
                "\t  Siid\n" +
                "\t  aliasName\n" +
                "\t  description\n" +
                "\t  deviceId\n" +
                "\t}\n" +
                "}";

        Response response = Endpoint.postGraphql(query);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());

    }


    @Test(priority = 0, description = "Test api engine interface: Query all instance of one entity with multiple column type by graphql.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with multiple column type and verify if all the analog entities names can be read out.")
    @Story("Api engine Interface API design")
    public void getAllInstanceOfOneEntityWithMultipleColumnTypeByGraphQL() {
        Reporter.log("Send request to graphql api with graphql to get all instance of one entity");

        String query = "{\n" +
                "\tAnalog {\n" +
                "\t  Analog_id\n" +
                "\t  Siid\n" +
                "\t  aliasName\n" +
                "\t  description\n" +
                "\t  deviceId\n" +
                "\t  generate_SensorData {\n" +
                "\t\t  Siid\n" +
                "\t\t  modelId\n" +
                "\t\t  type\n" +
                "\t\t  updateTime\n" +
                "\t\t  value\n" +
                "\t  }\n" +
                "\t}\n" +
                "}";

        Response response = Endpoint.postGraphql(query);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());

    }


    @Test(priority = 0, description = "Test api engine interface: Query all instance of one entity not exist in kg by graphql.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with entity not exist in kg and verify if correct message return.")
    @Story("Api engine Interface API design")
    public void getAllInstanceOfOneEntityNotExistByGraphQL() {
        Reporter.log("Send request to graphql api with graphql to with one entity not exist");

        String query = "{\n" +
                "\tAnalogNotExist {\n" +
                "\t  Analog_id\n" +
                "\t  Siid\n" +
                "\t  aliasName\n" +
                "\t  description\n" +
                "\t  deviceId\n" +
                "\t  generate_SensorData {\n" +
                "\t\t  Siid\n" +
                "\t\t  modelId\n" +
                "\t\t  type\n" +
                "\t\t  updateTime\n" +
                "\t\t  value\n" +
                "\t  }\n" +
                "\t}\n" +
                "}";

        Response response = Endpoint.postGraphql(query);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals("Validation error of type FieldUndefined: Field 'AnalogNotExist' in type 'Query' is undefined @ 'AnalogNotExist'", rspBody.getMessage());
        Assert.assertEquals(101100, rspBody.getCode());

    }

}
