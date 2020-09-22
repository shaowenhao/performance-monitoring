package com.siemens.datalayer.apiengine.test;

import com.siemens.datalayer.apiengine.model.EntitiesApiResponse;

import com.siemens.datalayer.apiengine.model.GraphqlApiResponse;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Epic("Api Engine Interface")
@Feature("Rest API")
public class ApiEngineInterfaceTests {

    @Parameters({"baseUrl", "port"})
    @BeforeClass
    public void setApiengineEndpoint(@Optional("http://140.231.89.85") String baseUrl, @Optional("32223") String port) {
        ApiEngineEndpoint.setBaseUrl(baseUrl);
        ApiEngineEndpoint.setPort(port);
    }

    @Test(priority = 0, description = "Test api engine interface: Get Entities without filter.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if all the analog entities names can be read out.")
    @Story("Get Entities without filter")
    public void getAllInstanceOfOneEntityByRestful() {
        Reporter.log("Send request to entities api with root=Analog depth=1");

        HashMap queryParameters = new HashMap<>();
        queryParameters.put("depth", "1");
        queryParameters.put("root", "Analog");

        Response response = ApiEngineEndpoint.getEntities(queryParameters);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        EntitiesApiResponse rspBody = response.getBody().as(EntitiesApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());

    }

    @Test(priority = 0, description = "Test api engine interface: Get Entities with filter of given column.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if all the analog entities names can be read out with given column.")
    @Story("Get Entities with filter of given column")
    public void getAllInstanceOfOneEntityWithGivenColumnByRestful() {
        Reporter.log("Send request to entities api with root=Analog filter=[Analog][type,Siid]");

        HashMap queryParameters = new HashMap<>();
        queryParameters.put("filter", "[Analog][type,Siid]");
        queryParameters.put("root", "Analog");

        Response response = ApiEngineEndpoint.getEntities(queryParameters);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        EntitiesApiResponse rspBody = response.getBody().as(EntitiesApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());
        JsonPath jsonPathEvaluator = response.jsonPath();
        List<String> l = new ArrayList<String>(
                Arrays.asList(
                        "Siid",
                        "type"
                )
        );
        ArrayList<HashMap> a = (ArrayList)jsonPathEvaluator.get("data.Analog");
        for(HashMap m : a){
            List<String> ll = new ArrayList<String>(m.keySet());
            ll.forEach( key-> Assert.assertTrue(l.contains(key)));
            Assert.assertEquals("Analog", m.get("type").toString());
        }

    }

    @Test(priority = 0, description = "Test api engine interface: Get Entities with filter of eq condition.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if eq condition work.")
    @Story("Test filter of eq")
    public void filterWithEqNumberConditionByRestful() {
        int siid = ApiEngineHelper.getOneAnalogSiid();

        Reporter.log("Send request to entities api with root=Analog filter=[Analog][type,Siid][{Siid: {_eq: 36435}}]");

        HashMap queryParameters = new HashMap<>();
        queryParameters.put("filter", String.format("[Analog][type,Siid][{Siid: {_eq: %d}}]", siid));
        queryParameters.put("root", "Analog");

        Response response = ApiEngineEndpoint.getEntities(queryParameters);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        EntitiesApiResponse rspBody = response.getBody().as(EntitiesApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());
        JsonPath jsonPathEvaluator = response.jsonPath();
        ArrayList<HashMap> a = (ArrayList)jsonPathEvaluator.get("data.Analog");
        Assert.assertEquals(1, a.size());
        Assert.assertEquals(a.get(0), new HashMap() {{
            put("Siid", siid);
            put("type", "Analog");
        }});

    }


    @Test(priority = 0, description = "Test api engine interface: Get Entities with filter of Neq condition.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if Neq condition work.")
    @Story("Test filter of eq")
    public void filterWithNeqNumberConditionByRestful() {
        int siid = ApiEngineHelper.getOneAnalogSiid();

        Reporter.log("Send request to entities api with root=Analog filter=[Analog][type,Siid][{Siid: {_neq: 36435}}]");

        HashMap queryParameters = new HashMap<>();
        queryParameters.put("filter", String.format("[Analog][type,Siid][{Siid: {_neq: %d}}]", siid));
        queryParameters.put("root", "Analog");

        Response response = ApiEngineEndpoint.getEntities(queryParameters);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        EntitiesApiResponse rspBody = response.getBody().as(EntitiesApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());
        JsonPath jsonPathEvaluator = response.jsonPath();
        ArrayList<HashMap> a = (ArrayList)jsonPathEvaluator.get("data.Analog");
        Assert.assertNull(a.stream().filter(d -> Integer.parseInt(d.get("Siid").toString()) == siid).findAny().orElse(null));

    }



    @Test(priority = 0, description = "Test api engine interface: Get Entities with filter of in condition.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if in condition work.")
    @Story("Test filter of in")
    public void filterWithInConditionByRestful() {
        ArrayList<String> a = ApiEngineHelper.getNAnalogSiid(5);

        Reporter.log("Send request to entities api with root=Analog filter=[Analog][type,Siid][{Siid: {_in: [123,234,345]}}][][]");

        HashMap queryParameters = new HashMap<>();
        queryParameters.put("filter", String.format("[Analog][type,Siid][{Siid: {_in: [%s]}}][][]", String.join(",", a)));
        queryParameters.put("root", "Analog");
        queryParameters.put("depth", "1");

        Response response = ApiEngineEndpoint.getEntities(queryParameters);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        EntitiesApiResponse rspBody = response.getBody().as(EntitiesApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());
        JsonPath jsonPathEvaluator = response.jsonPath();
        ArrayList<HashMap> a2 = (ArrayList)jsonPathEvaluator.get("data.Analog");
        HashMap<String, Integer> count = new HashMap<>();
        for(HashMap m: a2){
            String id = m.get("Siid").toString();
            Assert.assertTrue(a.contains(id));
            if(count.containsKey(id)){
                count.put(id, count.get(id) + 1);
            } else {
                count.put(id, 1);
            }
        }
        for(String s: a){
            Assert.assertTrue(count.containsKey(s));
            Assert.assertTrue(count.get(s) >= 1);
        }

    }


    @Test(priority = 0, description = "Test api engine interface: Get Entities with filter of nin condition.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if nin condition work.")
    @Story("Test filter of nin")
    public void filterWithNinConditionByRestful() {
        ArrayList<String> a = ApiEngineHelper.getNAnalogSiid(5);

        Reporter.log("Send request to entities api with root=Analog filter=[Analog][type,Siid][{Siid: {_nin: [123,234,345]}}][][]");

        HashMap queryParameters = new HashMap<>();
        queryParameters.put("filter", String.format("[Analog][type,Siid][{Siid: {_nin: [%s]}}][][]", String.join(",", a)));
        queryParameters.put("root", "Analog");
        queryParameters.put("depth", "1");

        Response response = ApiEngineEndpoint.getEntities(queryParameters);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        EntitiesApiResponse rspBody = response.getBody().as(EntitiesApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());
        JsonPath jsonPathEvaluator = response.jsonPath();
        ArrayList<HashMap> a2 = (ArrayList)jsonPathEvaluator.get("data.Analog");

        for(HashMap m: a2){
            String id = m.get("Siid").toString();
            Assert.assertFalse(a.contains(id));
        }


    }

    @Test(priority = 0, description = "Test api engine interface: Get Entities with filter of date range condition.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if date range condition work.")
    @Story("Test filter of date range")
    public void filterWithDateRangeConditionByRestful() {
        int siid = ApiEngineHelper.getOneAnalogSiid();

        Reporter.log("Send request to entities api with root=SensorData filter=[SensorData][][{_and: [{updateTime: {_lt: \"2020-07-24 03:00:00\"}},{updateTime: {_gt: \"2019-07-24 03:00:00\"}}]}]");

        HashMap queryParameters = new HashMap<>();
        queryParameters.put("filter","[SensorData][][{_and: [{updateTime: {_lt: \"2020-07-24 03:00:00\"}},{updateTime: {_gt: \"2019-07-24 03:00:00\"}}]}]");
        queryParameters.put("root", "SensorData");
        queryParameters.put("depth", "1");

        Response response = ApiEngineEndpoint.getEntities(queryParameters);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        EntitiesApiResponse rspBody = response.getBody().as(EntitiesApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());
        JsonPath jsonPathEvaluator = response.jsonPath();
        ArrayList<HashMap> a = (ArrayList)jsonPathEvaluator.get("data.SensorData");
        DateTimeFormatter inputFormat1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter inputFormat2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime dt1 = LocalDateTime.parse("2019-07-24 03:00:00", inputFormat1);
        LocalDateTime dt2 = LocalDateTime.parse("2020-07-24 03:00:00", inputFormat1);
        for(HashMap m: a){
            LocalDateTime dt3 = LocalDateTime.parse(m.get("updateTime").toString(), inputFormat2);
            Assert.assertTrue(dt3.isBefore(dt2) && dt3.isAfter(dt1));
        }

    }

    @Test(priority = 0, description = "Test api engine interface: Get Entities with filter and pagination.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if pagination work.")
    @Story("Test filter of pagination")
    public void filterWithPaginationByRestful() {
        Reporter.log("Send request to entities api with depth=1 root=SensorData filter=[SensorData][][][updateTime asc][1,10]");

        HashMap queryParameters = new HashMap<>();
        queryParameters.put("filter", "[SensorData][][][updateTime asc][1,10]");
        queryParameters.put("root", "SensorData");
        queryParameters.put("depth", "1");

        Response response = ApiEngineEndpoint.getEntities(queryParameters);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        EntitiesApiResponse rspBody = response.getBody().as(EntitiesApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());
        JsonPath jsonPathEvaluator = response.jsonPath();
        HashMap h = (HashMap)jsonPathEvaluator.get("data.SensorData");
        Assert.assertEquals(1, Integer.parseInt(h.get("page").toString()));
        Assert.assertEquals(10, Integer.parseInt(h.get("pageSize").toString()));
        ArrayList<HashMap> c = (ArrayList)h.get("content");
        Assert.assertEquals(10, c.size());
        Assert.assertTrue(this.isSortedByDateStringKey(c, "updateTime", "asc"));

        Reporter.log("Send request to entities api with depth=1 root=SensorData filter=[SensorData][][][updateTime desc][1,10]");

        HashMap queryParameters2 = new HashMap<>();
        queryParameters2.put("filter", "[SensorData][][][updateTime desc][1,10]");
        queryParameters2.put("root", "SensorData");
        queryParameters2.put("depth", "1");

        Response response2 = ApiEngineEndpoint.getEntities(queryParameters2);

        Reporter.log("Response status is " + response2.getStatusCode());

        Reporter.log("Response Body is =>  " + response2.getBody().asString());

        EntitiesApiResponse rspBody2 = response2.getBody().as(EntitiesApiResponse.class);

        Assert.assertEquals("Successfully", rspBody2.getMessage());
        Assert.assertEquals(100000, rspBody2.getCode());
        JsonPath jsonPathEvaluator2 = response2.jsonPath();
        HashMap h2 = (HashMap)jsonPathEvaluator2.get("data.SensorData");
        Assert.assertEquals(1, Integer.parseInt(h2.get("page").toString()));
        Assert.assertEquals(10, Integer.parseInt(h2.get("pageSize").toString()));
        ArrayList<HashMap> c2 = (ArrayList)h2.get("content");
        Assert.assertEquals(10, c2.size());
        Assert.assertTrue(this.isSortedByDateStringKey(c2, "updateTime", "desc"));
    }

    public boolean isSortedByDateStringKey(ArrayList<HashMap> list, String key, String order) {
        boolean sorted = true;
        for (int i = 1; i < list.size(); i++) {
            DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime dt1 = LocalDateTime.parse(list.get(i - 1).get(key).toString(), inputFormat);
            LocalDateTime dt2 = LocalDateTime.parse(list.get(i).get(key).toString(), inputFormat);
            switch (order) {
                case "asc":
                    if (dt1.isAfter(dt2)) {
                        sorted = false;
                    };
                    break;
                case "desc":
                    if (dt1.isBefore(dt2)) {
                        sorted = false;
                    };
                    break;
            }
        }

        return sorted;
    }

    @Test(priority = 0, description = "Test api engine interface: Get Entities with filter of eq bool condition.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if eq bool condition work.")
    @Story("Test filter of eq")
    public void filterWithEqBoolConditionByRestful() {
        int siid = ApiEngineHelper.getOneAnalogSiid();

        Reporter.log("Send request to entities api with root=Analog filter=[Analog][type,Siid,positiveFlowIn][{positiveFlowIn: {_eq: true}}][][]");

        HashMap queryParameters = new HashMap<>();
        queryParameters.put("filter", "[Analog][type,Siid,positiveFlowIn][{positiveFlowIn: {_eq: true}}][][]");
        queryParameters.put("root", "Analog");

        Response response = ApiEngineEndpoint.getEntities(queryParameters);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        EntitiesApiResponse rspBody = response.getBody().as(EntitiesApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());
        JsonPath jsonPathEvaluator = response.jsonPath();
        ArrayList<HashMap> a = (ArrayList)jsonPathEvaluator.get("data.Analog");
        for(HashMap m: a){
            Assert.assertEquals(3, m.size());
            Assert.assertEquals(true, (boolean)m.get("positiveFlowIn"));
        }

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
