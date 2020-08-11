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

@Epic("Api Engine Interface")
@Feature("Rest API")
public class ApiEngineInterfaceTests {

    @Parameters({"baseUrl", "port"})
    @BeforeClass
    public void setApiengineEndpoint(@Optional("http://140.231.89.85") String baseUrl, @Optional("31101") String port) {
        ApiEngineEndpoint.setBaseUrl(baseUrl);
        ApiEngineEndpoint.setPort(port);
    }

    @Test(priority = 0, description = "Test api engine interface: Get Entities without filter.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if all the analog entities names can be read out.")
    @Story("Get Entities without filter")
    public void getAllInstanceOfOneEntityByRestful() {
        Reporter.log("Send request to entities api with root=Analog depth=1");

        HashMap<String, String> queryParameters = new HashMap<>();
        queryParameters.put("depth", "1");
        queryParameters.put("root", "Analog");

        Response response = ApiEngineEndpoint.getEntities(queryParameters);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        EntitiesApiResponse rspBody = response.getBody().as(EntitiesApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());

    }


    @Test(priority = 0, description = "Test api engine interface: Query all instance of one entity by graphql.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if all the analog entities names can be read out.")
    @Story("Query all instance of one entity by graphql")
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

        Response response = ApiEngineEndpoint.postGraphql(query);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());

    }


    @Test(priority = 0, description = "Test api engine interface: Query all instance of one entity with multiple column type by graphql.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with multiple column type and verify if all the analog entities names can be read out.")
    @Story("Query all instance of one entity with multiple column type by graphql")
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

        Response response = ApiEngineEndpoint.postGraphql(query);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());

    }


    @Test(priority = 0, description = "Test api engine interface: Query all instance of one entity not exist in kg by graphql.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with entity not exist in kg and verify if correct message return.")
    @Story("Query all instance of one entity not exist in kg by graphql")
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

        Response response = ApiEngineEndpoint.postGraphql(query);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals("Validation error of type FieldUndefined: Field 'AnalogNotExist' in type 'Query' is undefined @ 'AnalogNotExist'", rspBody.getMessage());
        Assert.assertEquals(101100, rspBody.getCode());

    }


    @Test(priority = 0, description = "Test api engine interface: Query all instance of one entity with eq condition by graphql.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with entity with eq condition and verify if correct return.")
    @Story("Query all instance of one entity with eq condition by graphql")
    public void getInstanceOfOneEntityWithEqByGraphQL() {
        Reporter.log("Send request to graphql api with graphql which siid eq 33473");

        String query = "{\n" +
                "\tAnalog(cond:\"{Siid: {_eq: 33473}}\") {\n" +
                "\t  Analog_id\n" +
                "\t  Siid\n" +
                "\t  aliasName\n" +
                "\t  description\n" +
                "\t  deviceId\n" +
                "\t}\n" +
                "}";

        Response response = ApiEngineEndpoint.postGraphql(query);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertEquals(33473, jsonPathEvaluator.getInt("data.Analog[0].Siid"));
        List<String> l = new ArrayList<String>(
                Arrays.asList(
                        "Siid",
                        "aliasName",
                        "Analog_id",
                        "description",
                        "deviceId"
                )
        );
        HashMap m = (HashMap)jsonPathEvaluator.get("data.Analog[0]");
        List<String> ll = new ArrayList<String>(m.keySet());
        ll.forEach( key-> Assert.assertTrue(l.contains(key)));

    }


    @Test(priority = 0, description = "Test api engine interface: Query all instance of one entity with eq condition no result by graphql.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with entity with eq condition no result and verify if correct return.")
    @Story("Query all instance of one entity with eq condition no result by graphql")
    public void getInstanceOfOneEntityWithEqNoResultByGraphQL() {
        Reporter.log("Send request to graphql api with graphql which siid eq 999999");

        String query = "{\n" +
                "\tAnalog(cond:\"{Siid: {_eq: 999999}}\") {\n" +
                "\t  Analog_id\n" +
                "\t  Siid\n" +
                "\t  aliasName\n" +
                "\t  description\n" +
                "\t  deviceId\n" +
                "\t}\n" +
                "}";

        Response response = ApiEngineEndpoint.postGraphql(query);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNull(jsonPathEvaluator.get("data.Analog"));

    }



    @Test(priority = 0, description = "Test api engine interface: Query all instance of one entity with invalid condition by graphql.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with entity with invalid condition and verify if correct return.")
    @Story("Query all instance of one entity with invalid condition by graphql")
    public void getInstanceOfOneEntityWithInvalidFilterGraphQL() {
        Reporter.log("Send request to graphql api with graphql which invalid condition query");

        String query = "{\n" +
                "\tAnalog(cond:\"{Siid: {_invalid: 999999}}\") {\n" +
                "\t  Analog_id\n" +
                "\t  Siid\n" +
                "\t  aliasName\n" +
                "\t  description\n" +
                "\t  deviceId\n" +
                "\t}\n" +
                "}";

        Response response = ApiEngineEndpoint.postGraphql(query);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals("Exception while fetching data (/Analog) : can not parse _invalid: 999999", rspBody.getMessage());
        Assert.assertEquals(101100, rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNull(jsonPathEvaluator.get("data"));

    }



//    @Test(priority = 0, description = "Test api engine interface: Query all instance of one entity with eq condition in relation entity by graphql.")
//    @Severity(SeverityLevel.BLOCKER)
//    @Description("Send a request to SUT with entity with eq condition in relation entity and verify if correct return.")
//    @Story("Api engine Interface API design")
//    public void getInstanceOfOneEntityWithEqInRelationEntityByGraphQL() {
//        Reporter.log("Send request to graphql api with graphql which siid eq 34159 in relation entity");
//
//        String query = "{\n" +
//                "\tAnalog {\n" +
//                "\t  Analog_id\n" +
//                "\t  Siid\n" +
//                "\t  aliasName\n" +
//                "\t  description\n" +
//                "\t  deviceId\n" +
//                "\t  generate_SensorData(cond:\"{Siid: {_eq: 34159}}\"){\n" +
//                "\t\t  Siid\n" +
//                "\t\t  modelId\n" +
//                "\t\t  type\n" +
//                "\t\t  updateTime\n" +
//                "\t\t  value\n" +
//                "\t  }\n" +
//                "\t}\n" +
//                "}";
//
//        Response response = Endpoint.postGraphql(query);
//
//        Reporter.log("Response status is " + response.getStatusCode());
//
//        Reporter.log("Response Body is =>  " + response.getBody().asString());
//
//        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);
//
//        Assert.assertEquals("Successfully", rspBody.getMessage());
//        Assert.assertEquals(100000, rspBody.getCode());
//
//        JsonPath jsonPathEvaluator = response.jsonPath();
//
//        Assert.assertNull(jsonPathEvaluator.get("data.Analog"));
//
//    }


//    @Test(priority = 0, description = "Test api engine interface: Query all instance of one entity with invalid condition in relation entity by graphql.")
//    @Severity(SeverityLevel.BLOCKER)
//    @Description("Send a request to SUT with entity with invalid condition in relation entity and verify if correct return.")
//    @Story("Api engine Interface API design")
//    public void getInstanceOfOneEntityWithInvalidFilterInRelationEntityGraphQL() {
//        Reporter.log("Send request to graphql api with graphql which invalid condition query in relation entity");
//
//        String query = "{\n" +
//                "\tAnalog(cond:\"{Siid: {_eq: 34159}}\") {\n" +
//                "\t  Analog_id\n" +
//                "\t  Siid\n" +
//                "\t  aliasName\n" +
//                "\t  description\n" +
//                "\t  deviceId\n" +
//                "\t  generate_SensorData(cond:\"{Siid: {_invalid: 34159}}\"){\n" +
//                "\t\t  Siid\n" +
//                "\t\t  modelId\n" +
//                "\t\t  type\n" +
//                "\t\t  updateTime\n" +
//                "\t\t  value\n" +
//                "\t  }\n" +
//                "\t}\n" +
//                "}";
//
//        Response response = Endpoint.postGraphql(query);
//
//        Reporter.log("Response status is " + response.getStatusCode());
//
//        Reporter.log("Response Body is =>  " + response.getBody().asString());
//
//        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);
//
//        Assert.assertEquals("Exception while fetching data (/Analog) : can not parse _invalid: 999999", rspBody.getMessage());
//        Assert.assertEquals(101100, rspBody.getCode());
//
//        JsonPath jsonPathEvaluator = response.jsonPath();
//
//        Assert.assertNull(jsonPathEvaluator.get("data"));
//
//    }

//
//
//    @Test(priority = 0, description = "Test api engine interface: Query all instance of one entity with select column in relation entity by graphql.")
//    @Severity(SeverityLevel.BLOCKER)
//    @Description("Send a request to SUT with entity with  select column in relation entity and verify if correct return.")
//    @Story("Api engine Interface API design")
//    public void getInstanceOfOneEntityWithSelectColumnInRelationEntityGraphQL() {
//        Reporter.log("Send request to graphql api with graphql which query with  select column in relation entity");
//
//        String query = "{\n" +
//                "\tAnalog(cond:\"{Siid: {_eq: 34159}}\") {\n" +
//                "\t  Analog_id\n" +
//                "\t  Siid\n" +
//                "\t  aliasName\n" +
//                "\t  description\n" +
//                "\t  deviceId\n" +
//                "\t  generate_SensorData(cond:\"{Siid: {_invalid: 34159}}\"){\n" +
//                "\t\t  Siid\n" +
//                "\t\t  value\n" +
//                "\t  }\n" +
//                "\t}\n" +
//                "}";
//
//        Response response = Endpoint.postGraphql(query);
//
//        Reporter.log("Response status is " + response.getStatusCode());
//
//        Reporter.log("Response Body is =>  " + response.getBody().asString());
//
//        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);
//
//        Assert.assertEquals("Exception while fetching data (/Analog) : can not parse _invalid: 999999", rspBody.getMessage());
//        Assert.assertEquals(101100, rspBody.getCode());
//
//        JsonPath jsonPathEvaluator = response.jsonPath();
//
//        Assert.assertNull(jsonPathEvaluator.get("data"));
//
//    }




}
