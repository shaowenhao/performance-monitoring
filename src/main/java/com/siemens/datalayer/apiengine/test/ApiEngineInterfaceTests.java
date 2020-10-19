package com.siemens.datalayer.apiengine.test;

import com.siemens.datalayer.apiengine.model.EntitiesApiResponse;

import com.siemens.datalayer.apiengine.model.GraphqlApiResponse;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.qameta.allure.*;
import io.restassured.internal.path.json.JSONAssertion;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.*;
import org.testng.annotations.Optional;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;

@Epic("Api Engine Interface")
@Feature("Rest API")
public class ApiEngineInterfaceTests {

    @Parameters({"baseUrl", "port"})
    @BeforeClass(alwaysRun = true)
    public void setApiengineEndpoint(@Optional("http://140.231.89.85") String baseUrl, @Optional("31059") String port) {
        ApiEngineEndpoint.setBaseUrl(baseUrl);
        ApiEngineEndpoint.setPort(port);
    }

    @Test(groups = "iems", description = "Test api engine interface: Get Entities without filter.")
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

    @Test(groups = "iems", description = "Test api engine interface: Get Entities with filter of given column.")
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
        ArrayList<HashMap> a = (ArrayList) jsonPathEvaluator.get("data.Analog");
        for (HashMap m : a) {
            List<String> ll = new ArrayList<String>(m.keySet());
            ll.forEach(key -> Assert.assertTrue(l.contains(key)));
            Assert.assertEquals("Analog", m.get("type").toString());
        }

    }

    @Test(groups = "iems", description = "Test api engine interface: Get Entities with filter of eq condition.")
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
        ArrayList<HashMap> a = (ArrayList) jsonPathEvaluator.get("data.Analog");
        Assert.assertEquals(1, a.size());
        Assert.assertEquals(a.get(0), new HashMap() {{
            put("Siid", siid);
            put("type", "Analog");
        }});

    }


    @Test(groups = "iems", description = "Test api engine interface: Get Entities with filter of Neq condition.")
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
        ArrayList<HashMap> a = (ArrayList) jsonPathEvaluator.get("data.Analog");
        Assert.assertNull(a.stream().filter(d -> Integer.parseInt(d.get("Siid").toString()) == siid).findAny().orElse(null));

    }


    @Test(groups = "iems", description = "Test api engine interface: Get Entities with filter of in condition.")
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
        ArrayList<HashMap> a2 = (ArrayList) jsonPathEvaluator.get("data.Analog");
        HashMap<String, Integer> count = new HashMap<>();
        for (HashMap m : a2) {
            String id = m.get("Siid").toString();
            Assert.assertTrue(a.contains(id));
            if (count.containsKey(id)) {
                count.put(id, count.get(id) + 1);
            } else {
                count.put(id, 1);
            }
        }
        for (String s : a) {
            Assert.assertTrue(count.containsKey(s));
            Assert.assertTrue(count.get(s) >= 1);
        }

    }


    @Test(groups = "iems", description = "Test api engine interface: Get Entities with filter of nin condition.")
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
        ArrayList<HashMap> a2 = (ArrayList) jsonPathEvaluator.get("data.Analog");

        for (HashMap m : a2) {
            String id = m.get("Siid").toString();
            Assert.assertFalse(a.contains(id));
        }


    }

    @Test(groups = "iems", description = "Test api engine interface: Get Entities with filter of date range condition.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if date range condition work.")
    @Story("Test filter of date range")
    public void filterWithDateRangeConditionByRestful() {
        int siid = ApiEngineHelper.getOneAnalogSiid();

        Reporter.log("Send request to entities api with root=SensorData filter=[SensorData][][{_and: [{updateTime: {_lt: \"2020-07-24 03:00:00\"}},{updateTime: {_gt: \"2019-07-24 03:00:00\"}}]}]");

        HashMap queryParameters = new HashMap<>();
        queryParameters.put("filter", "[SensorData][][{_and: [{updateTime: {_lt: \"2020-07-24 03:00:00\"}},{updateTime: {_gt: \"2019-07-24 03:00:00\"}}]}]");
        queryParameters.put("root", "SensorData");
        queryParameters.put("depth", "1");

        Response response = ApiEngineEndpoint.getEntities(queryParameters);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        EntitiesApiResponse rspBody = response.getBody().as(EntitiesApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());
        JsonPath jsonPathEvaluator = response.jsonPath();
        ArrayList<HashMap> a = (ArrayList) jsonPathEvaluator.get("data.SensorData");
        DateTimeFormatter inputFormat1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter inputFormat2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime dt1 = LocalDateTime.parse("2019-07-24 03:00:00", inputFormat1);
        LocalDateTime dt2 = LocalDateTime.parse("2020-07-24 03:00:00", inputFormat1);
        for (HashMap m : a) {
            LocalDateTime dt3 = LocalDateTime.parse(m.get("updateTime").toString(), inputFormat2);
            Assert.assertTrue(dt3.isBefore(dt2) && dt3.isAfter(dt1));
        }

    }

    @Test(groups = "iems", description = "Test api engine interface: Get Entities with filter and pagination.")
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
        HashMap h = (HashMap) jsonPathEvaluator.get("data.SensorData");
        Assert.assertEquals(1, Integer.parseInt(h.get("page").toString()));
        Assert.assertEquals(10, Integer.parseInt(h.get("pageSize").toString()));
        ArrayList<HashMap> c = (ArrayList) h.get("content");
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
        HashMap h2 = (HashMap) jsonPathEvaluator2.get("data.SensorData");
        Assert.assertEquals(1, Integer.parseInt(h2.get("page").toString()));
        Assert.assertEquals(10, Integer.parseInt(h2.get("pageSize").toString()));
        ArrayList<HashMap> c2 = (ArrayList) h2.get("content");
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
                    }
                    ;
                    break;
                case "desc":
                    if (dt1.isBefore(dt2)) {
                        sorted = false;
                    }
                    ;
                    break;
            }
        }

        return sorted;
    }

    @Test(groups = "iems", description = "Test api engine interface: Get Entities with filter of eq bool condition.")
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
        ArrayList<HashMap> a = (ArrayList) jsonPathEvaluator.get("data.Analog");
        for (HashMap m : a) {
            Assert.assertEquals(3, m.size());
            Assert.assertEquals(true, (boolean) m.get("positiveFlowIn"));
        }

    }

    @Test(groups = "iems", description = "Test api engine interface: Query all instance of one entity by graphql.")
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


    @Test(groups = "iems", description = "Test api engine interface: Query all instance of one entity with multiple column type by graphql.")
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


    @Test(groups = "iems", description = "Test api engine interface: Query all instance of one entity not exist in kg by graphql.")
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


    @Test(groups = "iems", description = "Test api engine interface: Query all instance of one entity with eq condition by graphql.")
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
        HashMap m = (HashMap) jsonPathEvaluator.get("data.Analog[0]");
        List<String> ll = new ArrayList<String>(m.keySet());
        ll.forEach(key -> Assert.assertTrue(l.contains(key)));

    }


    @Test(groups = "iems", priority = 0, description = "Test api engine interface: Query all instance of one entity with eq condition no result by graphql.")
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


    @Test(groups = "iems", priority = 0, description = "Test api engine interface: Query all instance of one entity with invalid condition by graphql.")
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


    @Test(groups = "jinzu", description = "Test api engine interface: Query all jinzu power station project by graphql.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with graphql and verify if correct return.")
    @Story("Query all jinzu power station project by graphql")
    public void queryAllJinzuPowerStationProjectsByGraphQL() {
        Reporter.log("Send request to graphql api with graphql");

        String query = "{\n" +
                "\tProject(cond:\"{status:{_eq:\\\"online\\\"},Lease_Group:{lease_type:{_eq:\\\"2\\\"}}}\",order:\"\")\n" +
                "\t{\n" +
                "\t  business_mgr\n" +
                "\t  business_unit\n" +
                "\t  charge_frequency\n" +
                "\t  city\n" +
                "\t  province\n" +
                "\t  district\n" +
                "\t  class_level\n" +
                "\t  classification_level\n" +
                "\t  credit_amount\n" +
                "\t  detail_address\n" +
                "\t  discount_ratio\n" +
                "\t  expire_date\n" +
                "\t  guarantee_type\n" +
                "\t  id\n" +
                "\t  is_manufacture_buy_back\n" +
                "\t  is_manufacture_leasing\n" +
                "\t  manufacture\n" +
                "\t  no\n" +
                "\t  status\n" +
                "\t  name\n" +
                "\t  risk_mgr\n" +
                "\t  rent_type\n" +
                "\t  invert_Customer(cond:\"\",order:\"\")\n" +
                "\t  {\n" +
                "\t\tactual_controller\n" +
                "\t\tcategory\n" +
                "\t\tcid\n" +
                "\t\tcity\n" +
                "\t\tcname\n" +
                "\t\tcontact\n" +
                "\t\tcontact_detail\n" +
                "\t\tctype\n" +
                "\t\tdistrict\n" +
                "\t\tenterprise_size\n" +
                "\t\tgroup\n" +
                "\t\tholding_type\n" +
                "\t\tid\n" +
                "\t\tis_connected_tx\n" +
                "\t\tis_gov_fin_customer\n" +
                "\t\tis_group_customer\n" +
                "\t\tlegal_person_id\n" +
                "\t\tlegal_person\n" +
                "\t\tmajor_class\n" +
                "\t\tmiddle_class\n" +
                "\t\toffice_address\n" +
                "\t\tproject\n" +
                "\t\tprovince\n" +
                "\t\tregistered_address\n" +
                "\t\tsmall_class\n" +
                "\t  }\n" +
                "\tRestricted_By_Contract(cond:\"\",order:\"\")\n" +
                "\t{\n" +
                "\t  accumulated_amount\n" +
                "\t  charge_frequency\n" +
                "\t  contract_amount\n" +
                "\t  customer\n" +
                "\t  grant_loan_frequency\n" +
                "\t  id\n" +
                "\t  lease_balance\n" +
                "\t  lease_end_time\n" +
                "\t  lease_num\n" +
                "\t  lease_start_time\n" +
                "\t  lease_unit\n" +
                "\t  leasing_principal\n" +
                "\t  make_loan_day\n" +
                "\t  overdue_amount\n" +
                "\t  overdue_days\n" +
                "\t  overdue_interest\n" +
                "\t  overdue_principal\n" +
                "\t  payment_method\n" +
                "\t  project\n" +
                "\t}\n" +
                "\tRefer_To_Lease_Group(cond:\"\",order:\"\")\n" +
                "\t{\n" +
                "\t  asset_type\n" +
                "\t  count\n" +
                "\t  discount_ratio\n" +
                "\t  id\n" +
                "\t  lease_net_val\n" +
                "\t  lease_type\n" +
                "\t  lease_type_gb\n" +
                "\t  lease_type_yj\n" +
                "\t  nominal_cost\n" +
                "\t  project\n" +
                "\t  transfer_price\n" +
                "\t  unit_price\n" +
                "\t}\n" +
                "\t} \n" +
                "  }";

        Response response = ApiEngineEndpoint.postGraphql(query);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));

        ArrayList<HashMap> data = jsonPathEvaluator.get("data.Project");
        Assert.assertEquals(data.size(), 31);

        assertThat(response.getBody().asString(),
                matchesJsonSchemaInClasspath("jinzu-all-projects-graphql-schema.json"));

    }



    @Test(groups = "jinzu", description = "Test api engine interface: Query jinzu mix source by graphql.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with graphql and verify if correct return.")
    @Story("Query all jinzu jinzu mix source by graphql")
    public void queryAllJinzuMixSourceByGraphQL() {
        Reporter.log("Send request to graphql api with graphql");

        String query = "{\n" +
                "\tSite(cond:\"{id:{_eq:\\\"P000000666\\\"}}\",order:\"\"){ \n" +
                "\t\tid\n" +
                "\t\tlocation\n" +
                "\t\tcommissioning_date\n" +
                "\t\tstate\n" +
                "\t\tpower_station\n" +
                "\t\tHas_Device_Inverter{\n" +
                "\t\tsite\n" +
                "\t\tpr\n" +
                "\t\tproduction\n" +
                "\t\tname\n" +
                "\t\ttype\n" +
                "\t\tfull_generation_hours}\n" +
                "\t}\n" +
                "}\n";

        Response response = ApiEngineEndpoint.postGraphql(query);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));

        ArrayList<HashMap> data = jsonPathEvaluator.get("data.Site");
        Assert.assertEquals(data.size(), 1);


        assertThat(response.getBody().asString(),
                matchesJsonSchemaInClasspath("jinzu-mix-source-schema.json"));

    }

    @Test(groups = "jinzu", description = "Test api engine interface: Query one jinzu power station project by graphql.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with graphql and verify if correct return.")
    @Story("Query one jinzu power station project by graphql")
    public void queryOneJinzuPowerStationProjectByGraphQL() {
        Reporter.log("Send request to graphql api with graphql");

        String query = "{\n" +
                "  Contract(cond:\"{project:{_eq:33}}\",order:\"\")\n" +
                "   {\n" +
                "   accumulated_amount\n" +
                "   charge_frequency\n" +
                "   contract_amount\n" +
                "   customer\n" +
                "   grant_loan_frequency\n" +
                "   id\n" +
                "   lease_balance\n" +
                "   lease_end_time\n" +
                "   lease_num\n" +
                "   lease_start_time\n" +
                "   lease_unit\n" +
                "   leasing_principal\n" +
                "   make_loan_day\n" +
                "   overdue_amount\n" +
                "   overdue_days\n" +
                "   overdue_interest\n" +
                "   overdue_principal\n" +
                "   payment_method\n" +
                "   project\n" +
                "   invert_Project(cond:\"\",order:\"\")\n" +
                "\t{\n" +
                "\tbusiness_mgr\n" +
                "\tbusiness_unit\n" +
                "\tcharge_frequency\n" +
                "\tcity\n" +
                "\tprovince\n" +
                "\tdistrict\n" +
                "\tclass_level\n" +
                "\tclassification_level\n" +
                "\tcredit_amount\n" +
                "\tdetail_address\n" +
                "\tdiscount_ratio\n" +
                "\texpire_date\n" +
                "\tguarantee_type\n" +
                "\tid\n" +
                "\tis_manufacture_buy_back\n" +
                "\tis_manufacture_leasing\n" +
                "\tmanufacture\n" +
                "\tno\n" +
                "\tstatus\n" +
                "\tname\n" +
                "\trisk_mgr\n" +
                "\trent_type\n" +
                "\tRefer_To_Lease_Group(cond:\"\",order:\"\")\n" +
                "\t {\n" +
                "\t asset_type\n" +
                "\t count\n" +
                "\t discount_ratio\n" +
                "\t id\n" +
                "\t lease_net_val\n" +
                "\t lease_type\n" +
                "\t lease_type_gb\n" +
                "\t lease_type_yj\n" +
                "\t nominal_cost\n" +
                "\t project\n" +
                "\t transfer_price\n" +
                "\t unit_price\n" +
                "\t Refer_To_Power_Station_Properties(cond:\"\",order:\"\")\n" +
                "\t  {\n" +
                "\t  ps_type\n" +
                "\t  structure\n" +
                "\t  avg_annual_eq_hours\n" +
                "\t  capacity\n" +
                "\t }\n" +
                "\t} \n" +
                "   }\n" +
                "   invert_Customer(cond:\"\",order:\"\")\n" +
                "\t{\n" +
                "\tactual_controller\n" +
                "\tcategory\n" +
                "\tcid\n" +
                "\tcity\n" +
                "\tcname\n" +
                "\tcontact\n" +
                "\tcontact_detail\n" +
                "\tctype\n" +
                "\tdistrict\n" +
                "\tenterprise_size\n" +
                "\tgroup\n" +
                "\tholding_type\n" +
                "\tid\n" +
                "\tis_connected_tx\n" +
                "\tis_gov_fin_customer\n" +
                "\tis_group_customer\n" +
                "\tlegal_person_id\n" +
                "\tlegal_person\n" +
                "\tmajor_class\n" +
                "\tmiddle_class\n" +
                "\toffice_address\n" +
                "\tproject\n" +
                "\tprovince\n" +
                "\tregistered_address\n" +
                "\tsmall_class\n" +
                "   }\n" +
                "  } \n" +
                "}";

        Response response = ApiEngineEndpoint.postGraphql(query);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));

        ArrayList<HashMap> data = jsonPathEvaluator.get("data.Contract");
        Assert.assertEquals(data.size(), 1);

        String expectJson = "{\n" +
                "    \"code\": 100000,\n" +
                "    \"message\": \"Successfully\",\n" +
                "    \"data\": {\n" +
                "        \"Contract\": [\n" +
                "            {\n" +
                "                \"accumulated_amount\": 5.0E7,\n" +
                "                \"charge_frequency\": 3,\n" +
                "                \"contract_amount\": 5.0E7,\n" +
                "                \"customer\": \"28\",\n" +
                "                \"grant_loan_frequency\": \"一次投放\",\n" +
                "                \"id\": \"苏州租赁(2018)回字第1810099号\",\n" +
                "                \"lease_balance\": 4.4168744E7,\n" +
                "                \"lease_end_time\": \"2026-07-15 00:00:00\",\n" +
                "                \"lease_num\": 96.0,\n" +
                "                \"lease_start_time\": \"2018-07-25 00:00:00\",\n" +
                "                \"lease_unit\": null,\n" +
                "                \"leasing_principal\": 5.0E7,\n" +
                "                \"make_loan_day\": \"2018-07-25 00:00:00\",\n" +
                "                \"overdue_amount\": 0.0,\n" +
                "                \"overdue_days\": 0,\n" +
                "                \"overdue_interest\": 0.0,\n" +
                "                \"overdue_principal\": 0.0,\n" +
                "                \"payment_method\": \"等额本息\",\n" +
                "                \"project\": \"33\",\n" +
                "                \"invert_Project\": {\n" +
                "                    \"business_mgr\": \"沈忱宜\",\n" +
                "                    \"business_unit\": \"业务三部\",\n" +
                "                    \"charge_frequency\": 3,\n" +
                "                    \"city\": \"安阳市\",\n" +
                "                    \"province\": \"河南省\",\n" +
                "                    \"district\": \"安阳县\",\n" +
                "                    \"class_level\": \"正常\",\n" +
                "                    \"classification_level\": \"正常\",\n" +
                "                    \"credit_amount\": 50000000,\n" +
                "                    \"detail_address\": null,\n" +
                "                    \"discount_ratio\": 0.5131579,\n" +
                "                    \"expire_date\": null,\n" +
                "                    \"guarantee_type\": \"质押\",\n" +
                "                    \"id\": \"33\",\n" +
                "                    \"is_manufacture_buy_back\": false,\n" +
                "                    \"is_manufacture_leasing\": false,\n" +
                "                    \"manufacture\": \"瑞龙电气厂\",\n" +
                "                    \"no\": \"20180651\",\n" +
                "                    \"status\": \"online\",\n" +
                "                    \"name\": \"安阳县中晖光伏发电有限公司集中式光伏电站回租1期\",\n" +
                "                    \"risk_mgr\": \"朱金龙\",\n" +
                "                    \"rent_type\": \"融资租赁/回租\",\n" +
                "                    \"Refer_To_Lease_Group\": {\n" +
                "                        \"asset_type\": \"动产\",\n" +
                "                        \"count\": 1,\n" +
                "                        \"discount_ratio\": 0.5131579,\n" +
                "                        \"id\": \"33\",\n" +
                "                        \"lease_net_val\": 9.7435896E7,\n" +
                "                        \"lease_type\": \"2\",\n" +
                "                        \"lease_type_gb\": \"其他电力工业专用设备\",\n" +
                "                        \"lease_type_yj\": \"电力设备\",\n" +
                "                        \"nominal_cost\": 1000.0,\n" +
                "                        \"project\": \"33\",\n" +
                "                        \"transfer_price\": 5.0E7,\n" +
                "                        \"unit_price\": 1.02564104E8,\n" +
                "                        \"Refer_To_Power_Station_Properties\": {\n" +
                "                            \"ps_type\": \"光伏电站\",\n" +
                "                            \"structure\": \"集中式\",\n" +
                "                            \"avg_annual_eq_hours\": 1115.0,\n" +
                "                            \"capacity\": 20.0\n" +
                "                        }\n" +
                "                    }\n" +
                "                },\n" +
                "                \"invert_Customer\": {\n" +
                "                    \"actual_controller\": \"王柏兴\",\n" +
                "                    \"category\": \"电力、热力、燃气及水的生产和供应业\",\n" +
                "                    \"cid\": \"91410522MA3X5R1H97\",\n" +
                "                    \"city\": \"安阳市\",\n" +
                "                    \"cname\": \"安阳县中晖光伏发电有限公司\",\n" +
                "                    \"contact\": \"陶珂珂\",\n" +
                "                    \"contact_detail\": \"15851585618\",\n" +
                "                    \"ctype\": \"企业法人\",\n" +
                "                    \"district\": \"安阳县\",\n" +
                "                    \"enterprise_size\": \"微型\",\n" +
                "                    \"group\": \"江苏中利集团股份有限公司\",\n" +
                "                    \"holding_type\": \"私人控股企业\",\n" +
                "                    \"id\": \"28\",\n" +
                "                    \"is_connected_tx\": true,\n" +
                "                    \"is_gov_fin_customer\": false,\n" +
                "                    \"is_group_customer\": true,\n" +
                "                    \"legal_person_id\": \"412328198306074537\",\n" +
                "                    \"legal_person\": \"王庆杰\",\n" +
                "                    \"major_class\": \"电力、热力生产和供应业\",\n" +
                "                    \"middle_class\": \"电力生产\",\n" +
                "                    \"office_address\": \"河南省安阳市安阳县马家乡北齐村\",\n" +
                "                    \"project\": \"33\",\n" +
                "                    \"province\": \"河南省\",\n" +
                "                    \"registered_address\": \"河南省安阳市安阳县马家乡北齐村\",\n" +
                "                    \"small_class\": \"太阳能发电\"\n" +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
//        System.out.println(response.getBody().prettyPrint());
//        assertThat(response.getBody().asString(),
//                matchesJsonSchemaInClasspath("jinzu-all-projects-graphql-schema.json"));
        try {
            JSONAssert.assertEquals(response.getBody().asString(), expectJson, false);
        } catch (JSONException e) {
            e.printStackTrace();
            Assert.fail("Failed to compare result with expect json ", e);
        }
    }


    @Test(groups = "jinzu", description = "Test api engine interface: Query one jinzu project lease by graphql.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with graphql and verify if correct return.")
    @Story("Query one jinzu project lease by graphql")
    public void queryOneJinzuProjectLeaseByGraphQL() {
        Reporter.log("Send request to graphql api with graphql");

        String query = "{Lease(\n" +
                "      cond:\"{project:{_eq:33}}\",\n" +
                "      order:\"\")\n" +
                "      {\n" +
                "      lease_type_gb\n" +
                "      lending\n" +
                "      lposition\n" +
                "      invoice_no\n" +
                "      project\n" +
                "      lease_type_yj\n" +
                "      brand_name\n" +
                "      unit_price\n" +
                "      serial_num\n" +
                "      discount_ratio\n" +
                "      transfer_price\n" +
                "      lease_net_val\n" +
                "      nominal_cost\n" +
                "      supplier\n" +
                "      asset_type\n" +
                "      name\n" +
                "      lease_type\n" +
                "      position\n" +
                "      id\n" +
                "      purchase_time\n" +
                "      status\n" +
                "      lease_group\n" +
                "      product_model\n" +
                "      Refer_To_Power_Station(\n" +
                "      cond:\"\",\n" +
                "      order:\"\")\n" +
                "      {\n" +
                "      id\n" +
                "      Compose_Of_Site(\n" +
                "      cond:\"\",\n" +
                "      order:\"\")\n" +
                "      {\n" +
                "      avg_annual_eq_hours\n" +
                "      avg_irradiance\n" +
                "      capacity\n" +
                "      city\n" +
                "      commissioning_date\n" +
                "      grid_inject_production\n" +
                "      id\n" +
                "      irradiance\n" +
                "      location\n" +
                "      name\n" +
                "      power_station\n" +
                "      state\n" +
                "      sys_engaged_date\n" +
                "      type\n" +
                "      voltage_degree\n" +
                "      }\n" +
                "      }\n" +
                "      }}";

        Response response = ApiEngineEndpoint.postGraphql(query);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));

        ArrayList<HashMap> data = jsonPathEvaluator.get("data.Lease");
        Assert.assertEquals(data.size(), 1);

        String expectJson = "{\n" +
                "    \"code\": 100000,\n" +
                "    \"message\": \"Successfully\",\n" +
                "    \"data\": {\n" +
                "        \"Lease\": [\n" +
                "            {\n" +
                "                \"lease_type_gb\": \"其他电力工业专用设备\",\n" +
                "                \"lending\": \"P000000666\",\n" +
                "                \"lposition\": \"河南省安阳市安阳县马家乡北齐村\",\n" +
                "                \"invoice_no\": \"12345678\",\n" +
                "                \"project\": \"33\",\n" +
                "                \"lease_type_yj\": \"电力设备\",\n" +
                "                \"brand_name\": \"中利新能源\",\n" +
                "                \"unit_price\": 0.0,\n" +
                "                \"serial_num\": null,\n" +
                "                \"discount_ratio\": 0.5131579,\n" +
                "                \"transfer_price\": 5.0E7,\n" +
                "                \"lease_net_val\": 9.7435896E7,\n" +
                "                \"nominal_cost\": 1.02564104E8,\n" +
                "                \"supplier\": \"瑞龙电气厂\",\n" +
                "                \"asset_type\": \"动产\",\n" +
                "                \"name\": \"河南安阳中晖\",\n" +
                "                \"lease_type\": \"2\",\n" +
                "                \"position\": \"河南省安阳市安阳县马家乡北齐村\",\n" +
                "                \"id\": \"666\",\n" +
                "                \"purchase_time\": \"2018-07-13T12:00:00\",\n" +
                "                \"status\": \"online\",\n" +
                "                \"lease_group\": \"33\",\n" +
                "                \"product_model\": null,\n" +
                "                \"Refer_To_Power_Station\": {\n" +
                "                    \"id\": \"P000000666\",\n" +
                "                    \"Compose_Of_Site\": [\n" +
                "                        {\n" +
                "                            \"avg_annual_eq_hours\": null,\n" +
                "                            \"avg_irradiance\": null,\n" +
                "                            \"capacity\": 20.0,\n" +
                "                            \"city\": null,\n" +
                "                            \"commissioning_date\": 1582175527907,\n" +
                "                            \"grid_inject_production\": null,\n" +
                "                            \"id\": \"P000000666\",\n" +
                "                            \"irradiance\": null,\n" +
                "                            \"location\": \"河南省安阳市安阳县马家乡北齐村\",\n" +
                "                            \"name\": null,\n" +
                "                            \"power_station\": \"P000000666\",\n" +
                "                            \"state\": \"online\",\n" +
                "                            \"sys_engaged_date\": null,\n" +
                "                            \"type\": null,\n" +
                "                            \"voltage_degree\": null\n" +
                "                        }\n" +
                "                    ]\n" +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
//        System.out.println(response.getBody().prettyPrint());
//        assertThat(response.getBody().asString(),
//                matchesJsonSchemaInClasspath("jinzu-all-projects-graphql-schema.json"));
        try {
            JSONAssert.assertEquals(response.getBody().asString(), expectJson, false);
        } catch (JSONException e) {
            e.printStackTrace();
            Assert.fail("Failed to compare result with expect json ", e);
        }

        String query2 = "{Lease(\n" +
                "      cond:\"{id:{_eq:666}}\",\n" +
                "      order:\"\")\n" +
                "       {\n" +
                "       lease_type_gb\n" +
                "       lending\n" +
                "       lposition\n" +
                "       invoice_no\n" +
                "       project\n" +
                "       lease_type_yj\n" +
                "       brand_name\n" +
                "       unit_price\n" +
                "       serial_num\n" +
                "       discount_ratio\n" +
                "       transfer_price\n" +
                "       lease_net_val\n" +
                "       nominal_cost\n" +
                "       supplier\n" +
                "       asset_type\n" +
                "       name\n" +
                "       lease_type\n" +
                "       position\n" +
                "       id\n" +
                "       purchase_time\n" +
                "       status\n" +
                "       lease_group\n" +
                "       product_model\n" +
                "       invert_Lease_Group(\n" +
                "       cond:\"\",\n" +
                "       order:\"\")\n" +
                "        {\n" +
                "        lease_net_val\n" +
                "        transfer_price\n" +
                "        lease_type_gb\n" +
                "        nominal_cost\n" +
                "        count\n" +
                "        asset_type\n" +
                "        project\n" +
                "        lease_type_yj\n" +
                "        id\n" +
                "        lease_type\n" +
                "        unit_price\n" +
                "        discount_ratio\n" +
                "        invert_Project(\n" +
                "        cond:\"\",\n" +
                "        order:\"\")\n" +
                "         {\n" +
                "         no\n" +
                "         rent_type\n" +
                "         is_manufacture_buy_back\n" +
                "         classification_level\n" +
                "         city\n" +
                "         detail_address\n" +
                "         charge_frequency\n" +
                "         credit_amount\n" +
                "         risk_mgr\n" +
                "         is_manufacture_leasing\n" +
                "         business_unit\n" +
                "         discount_ratio\n" +
                "         class_level\n" +
                "         manufacture\n" +
                "         business_mgr\n" +
                "         expire_date\n" +
                "         province\n" +
                "         district\n" +
                "         name\n" +
                "         guarantee_type\n" +
                "         id\n" +
                "         status\n" +
                "         invert_Customer(\n" +
                "         cond:\"\",\n" +
                "         order:\"\")\n" +
                "          {\n" +
                "          registered_address\n" +
                "          city\n" +
                "          major_class\n" +
                "          cname\n" +
                "          enterprise_size\n" +
                "          project\n" +
                "          is_connected_tx\n" +
                "          legal_person_id\n" +
                "          small_class\n" +
                "          province\n" +
                "          is_gov_fin_customer\n" +
                "          contact\n" +
                "          id\n" +
                "          group\n" +
                "          legal_person\n" +
                "          contact_detail\n" +
                "          actual_controller\n" +
                "          office_address\n" +
                "          ctype\n" +
                "          district\n" +
                "          is_group_customer\n" +
                "          middle_class\n" +
                "          category\n" +
                "          cid\n" +
                "          holding_type\n" +
                "         }\n" +
                "        }\n" +
                "        Refer_To_Power_Station_Properties(\n" +
                "        cond:\"\",\n" +
                "        order:\"\")\n" +
                "         {\n" +
                "         avg_annual_eq_hours\n" +
                "         lease_group\n" +
                "         ps_type\n" +
                "         structure\n" +
                "         capacity\n" +
                "        }\n" +
                "       }\n" +
                "       Refer_To_Power_Station(\n" +
                "       cond:\"\",\n" +
                "       order:\"\")\n" +
                "        {\n" +
                "        id\n" +
                "        Compose_Of_Site(\n" +
                "        cond:\"\",\n" +
                "        order:\"\")\n" +
                "         {\n" +
                "         avg_annual_eq_hours\n" +
                "         avg_irradiance\n" +
                "         capacity\n" +
                "         city\n" +
                "         commissioning_date\n" +
                "         grid_inject_production\n" +
                "         id\n" +
                "         irradiance\n" +
                "         location\n" +
                "         name\n" +
                "         power_station\n" +
                "         state\n" +
                "         sys_engaged_date\n" +
                "         type\n" +
                "         voltage_degree\n" +
                "         Generate_Site_Report(\n" +
                "         cond:\"{report_time:{_eq:\\\"1582128000000\\\"}}\",\n" +
                "         order:\"\")\n" +
                "          {\n" +
                "          energy_loss\n" +
                "          pr\n" +
                "          peak_power_moment\n" +
                "          production\n" +
                "          purchasing_energy\n" +
                "          sunshin_duration\n" +
                "          full_generation_hours\n" +
                "          comprehensive_plant_power_consumption\n" +
                "          peak_power\n" +
                "          self_consumed_percent\n" +
                "          site\n" +
                "          revenue\n" +
                "          report_time\n" +
                "          comprehensive_plant_power_consumption_rate\n" +
                "          self_consumed_production\n" +
                "          energy_loss_rate\n" +
                "          equivalent_hours\n" +
                "          pr_validity\n" +
                "         }\n" +
                "         invert_Weather(\n" +
                "         cond:\"\",\n" +
                "         order:\"\")\n" +
                "          {\n" +
                "          max_temperature\n" +
                "          update_time\n" +
                "          site\n" +
                "          rainfall\n" +
                "          main_weather_station\n" +
                "          avg_temperature\n" +
                "          description\n" +
                "          min_temperature\n" +
                "         }\n" +
                "         Involve_Device_Alarm_Event(\n" +
                "         cond:\"\",\n" +
                "         order:\"\")\n" +
                "          {\n" +
                "          alarm_location\n" +
                "          update_time\n" +
                "          site\n" +
                "          alarm_handle_status\n" +
                "          alarm_template\n" +
                "          occurring_time\n" +
                "          event_name\n" +
                "          id\n" +
                "          update_by\n" +
                "          device\n" +
                "          confirm_time\n" +
                "         }\n" +
                "        }\n" +
                "       }\n" +
                "      }}";

        Response response2 = ApiEngineEndpoint.postGraphql(query2);

        Reporter.log("Response status is " + response2.getStatusCode());

        Reporter.log("Response Body is =>  " + response2.getBody().asString());

        GraphqlApiResponse rspBody2 = response2.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals("Successfully", rspBody2.getMessage());
        Assert.assertEquals(100000, rspBody2.getCode());

        JsonPath jsonPathEvaluator2 = response2.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator2.get("data"));

        ArrayList<HashMap> data2 = jsonPathEvaluator2.get("data.Lease");
        Assert.assertEquals(data2.size(), 1);

        String expectJson2 = "{\n" +
                "    \"code\": 100000,\n" +
                "    \"message\": \"Successfully\",\n" +
                "    \"data\": {\n" +
                "        \"Lease\": [\n" +
                "            {\n" +
                "                \"lease_type_gb\": \"其他电力工业专用设备\",\n" +
                "                \"lending\": \"P000000666\",\n" +
                "                \"lposition\": \"河南省安阳市安阳县马家乡北齐村\",\n" +
                "                \"invoice_no\": \"12345678\",\n" +
                "                \"project\": \"33\",\n" +
                "                \"lease_type_yj\": \"电力设备\",\n" +
                "                \"brand_name\": \"中利新能源\",\n" +
                "                \"unit_price\": 0.0,\n" +
                "                \"serial_num\": null,\n" +
                "                \"discount_ratio\": 0.5131579,\n" +
                "                \"transfer_price\": 5.0E7,\n" +
                "                \"lease_net_val\": 9.7435896E7,\n" +
                "                \"nominal_cost\": 1.02564104E8,\n" +
                "                \"supplier\": \"瑞龙电气厂\",\n" +
                "                \"asset_type\": \"动产\",\n" +
                "                \"name\": \"河南安阳中晖\",\n" +
                "                \"lease_type\": \"2\",\n" +
                "                \"position\": \"河南省安阳市安阳县马家乡北齐村\",\n" +
                "                \"id\": \"666\",\n" +
                "                \"purchase_time\": \"2018-07-13T12:00:00\",\n" +
                "                \"status\": \"online\",\n" +
                "                \"lease_group\": \"33\",\n" +
                "                \"product_model\": null,\n" +
                "                \"invert_Lease_Group\": {\n" +
                "                    \"lease_net_val\": 9.7435896E7,\n" +
                "                    \"transfer_price\": 5.0E7,\n" +
                "                    \"lease_type_gb\": \"其他电力工业专用设备\",\n" +
                "                    \"nominal_cost\": 1000.0,\n" +
                "                    \"count\": 1,\n" +
                "                    \"asset_type\": \"动产\",\n" +
                "                    \"project\": \"33\",\n" +
                "                    \"lease_type_yj\": \"电力设备\",\n" +
                "                    \"id\": \"33\",\n" +
                "                    \"lease_type\": \"2\",\n" +
                "                    \"unit_price\": 1.02564104E8,\n" +
                "                    \"discount_ratio\": 0.5131579,\n" +
                "                    \"invert_Project\": {\n" +
                "                        \"no\": \"20180651\",\n" +
                "                        \"rent_type\": \"融资租赁/回租\",\n" +
                "                        \"is_manufacture_buy_back\": false,\n" +
                "                        \"classification_level\": \"正常\",\n" +
                "                        \"city\": \"安阳市\",\n" +
                "                        \"detail_address\": null,\n" +
                "                        \"charge_frequency\": 3,\n" +
                "                        \"credit_amount\": 50000000,\n" +
                "                        \"risk_mgr\": \"朱金龙\",\n" +
                "                        \"is_manufacture_leasing\": false,\n" +
                "                        \"business_unit\": \"业务三部\",\n" +
                "                        \"discount_ratio\": 0.5131579,\n" +
                "                        \"class_level\": \"正常\",\n" +
                "                        \"manufacture\": \"瑞龙电气厂\",\n" +
                "                        \"business_mgr\": \"沈忱宜\",\n" +
                "                        \"expire_date\": null,\n" +
                "                        \"province\": \"河南省\",\n" +
                "                        \"district\": \"安阳县\",\n" +
                "                        \"name\": \"安阳县中晖光伏发电有限公司集中式光伏电站回租1期\",\n" +
                "                        \"guarantee_type\": \"质押\",\n" +
                "                        \"id\": \"33\",\n" +
                "                        \"status\": \"online\",\n" +
                "                        \"invert_Customer\": {\n" +
                "                            \"registered_address\": \"河南省安阳市安阳县马家乡北齐村\",\n" +
                "                            \"city\": \"安阳市\",\n" +
                "                            \"major_class\": \"电力、热力生产和供应业\",\n" +
                "                            \"cname\": \"安阳县中晖光伏发电有限公司\",\n" +
                "                            \"enterprise_size\": \"微型\",\n" +
                "                            \"project\": \"33\",\n" +
                "                            \"is_connected_tx\": true,\n" +
                "                            \"legal_person_id\": \"412328198306074537\",\n" +
                "                            \"small_class\": \"太阳能发电\",\n" +
                "                            \"province\": \"河南省\",\n" +
                "                            \"is_gov_fin_customer\": false,\n" +
                "                            \"contact\": \"陶珂珂\",\n" +
                "                            \"id\": \"28\",\n" +
                "                            \"group\": \"江苏中利集团股份有限公司\",\n" +
                "                            \"legal_person\": \"王庆杰\",\n" +
                "                            \"contact_detail\": \"15851585618\",\n" +
                "                            \"actual_controller\": \"王柏兴\",\n" +
                "                            \"office_address\": \"河南省安阳市安阳县马家乡北齐村\",\n" +
                "                            \"ctype\": \"企业法人\",\n" +
                "                            \"district\": \"安阳县\",\n" +
                "                            \"is_group_customer\": true,\n" +
                "                            \"middle_class\": \"电力生产\",\n" +
                "                            \"category\": \"电力、热力、燃气及水的生产和供应业\",\n" +
                "                            \"cid\": \"91410522MA3X5R1H97\",\n" +
                "                            \"holding_type\": \"私人控股企业\"\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"Refer_To_Power_Station_Properties\": {\n" +
                "                        \"avg_annual_eq_hours\": 1115.0,\n" +
                "                        \"lease_group\": \"33\",\n" +
                "                        \"ps_type\": \"光伏电站\",\n" +
                "                        \"structure\": \"集中式\",\n" +
                "                        \"capacity\": 20.0\n" +
                "                    }\n" +
                "                },\n" +
                "                \"Refer_To_Power_Station\": {\n" +
                "                    \"id\": \"P000000666\",\n" +
                "                    \"Compose_Of_Site\": [\n" +
                "                        {\n" +
                "                            \"avg_annual_eq_hours\": null,\n" +
                "                            \"avg_irradiance\": null,\n" +
                "                            \"capacity\": 20.0,\n" +
                "                            \"city\": null,\n" +
                "                            \"commissioning_date\": 1582175527907,\n" +
                "                            \"grid_inject_production\": null,\n" +
                "                            \"id\": \"P000000666\",\n" +
                "                            \"irradiance\": null,\n" +
                "                            \"location\": \"河南省安阳市安阳县马家乡北齐村\",\n" +
                "                            \"name\": null,\n" +
                "                            \"power_station\": \"P000000666\",\n" +
                "                            \"state\": \"online\",\n" +
                "                            \"sys_engaged_date\": null,\n" +
                "                            \"type\": null,\n" +
                "                            \"voltage_degree\": null,\n" +
                "                            \"Generate_Site_Report\": [\n" +
                "                                {\n" +
                "                                    \"energy_loss\": null,\n" +
                "                                    \"pr\": 0.63,\n" +
                "                                    \"peak_power_moment\": null,\n" +
                "                                    \"production\": 46968.65,\n" +
                "                                    \"purchasing_energy\": null,\n" +
                "                                    \"sunshin_duration\": null,\n" +
                "                                    \"full_generation_hours\": null,\n" +
                "                                    \"comprehensive_plant_power_consumption\": null,\n" +
                "                                    \"peak_power\": null,\n" +
                "                                    \"self_consumed_percent\": null,\n" +
                "                                    \"site\": \"P000000666\",\n" +
                "                                    \"revenue\": null,\n" +
                "                                    \"report_time\": 1582128000000,\n" +
                "                                    \"comprehensive_plant_power_consumption_rate\": null,\n" +
                "                                    \"self_consumed_production\": null,\n" +
                "                                    \"energy_loss_rate\": null,\n" +
                "                                    \"equivalent_hours\": 2.43,\n" +
                "                                    \"pr_validity\": null\n" +
                "                                }\n" +
                "                            ],\n" +
                "                            \"invert_Weather\": [\n" +
                "                                {\n" +
                "                                    \"max_temperature\": null,\n" +
                "                                    \"update_time\": 1582128000000,\n" +
                "                                    \"site\": \"P000000666\",\n" +
                "                                    \"rainfall\": null,\n" +
                "                                    \"main_weather_station\": null,\n" +
                "                                    \"avg_temperature\": null,\n" +
                "                                    \"description\": \"Sunny\",\n" +
                "                                    \"min_temperature\": null\n" +
                "                                },\n" +
                "                                {\n" +
                "                                    \"max_temperature\": null,\n" +
                "                                    \"update_time\": 1582387200000,\n" +
                "                                    \"site\": \"P000000666\",\n" +
                "                                    \"rainfall\": null,\n" +
                "                                    \"main_weather_station\": null,\n" +
                "                                    \"avg_temperature\": null,\n" +
                "                                    \"description\": \"Sunny\",\n" +
                "                                    \"min_temperature\": null\n" +
                "                                },\n" +
                "                                {\n" +
                "                                    \"max_temperature\": null,\n" +
                "                                    \"update_time\": 1582214400000,\n" +
                "                                    \"site\": \"P000000666\",\n" +
                "                                    \"rainfall\": null,\n" +
                "                                    \"main_weather_station\": null,\n" +
                "                                    \"avg_temperature\": null,\n" +
                "                                    \"description\": \"Sunny\",\n" +
                "                                    \"min_temperature\": null\n" +
                "                                },\n" +
                "                                {\n" +
                "                                    \"max_temperature\": null,\n" +
                "                                    \"update_time\": 1582300800000,\n" +
                "                                    \"site\": \"P000000666\",\n" +
                "                                    \"rainfall\": null,\n" +
                "                                    \"main_weather_station\": null,\n" +
                "                                    \"avg_temperature\": null,\n" +
                "                                    \"description\": \"Sunny\",\n" +
                "                                    \"min_temperature\": null\n" +
                "                                }\n" +
                "                            ],\n" +
                "                            \"Involve_Device_Alarm_Event\": [\n" +
                "                                {\n" +
                "                                    \"alarm_location\": null,\n" +
                "                                    \"update_time\": 1568869200000,\n" +
                "                                    \"site\": \"P000000666\",\n" +
                "                                    \"alarm_handle_status\": \"1\",\n" +
                "                                    \"alarm_template\": \"1\",\n" +
                "                                    \"occurring_time\": 1568822400000,\n" +
                "                                    \"event_name\": null,\n" +
                "                                    \"id\": \"21\",\n" +
                "                                    \"update_by\": \"钱志良\",\n" +
                "                                    \"device\": null,\n" +
                "                                    \"confirm_time\": null\n" +
                "                                }\n" +
                "                            ]\n" +
                "                        }\n" +
                "                    ]\n" +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
//        System.out.println(response2.getBody().prettyPrint());
//        assertThat(response.getBody().asString(),
//                matchesJsonSchemaInClasspath("jinzu-all-projects-graphql-schema.json"));
        try {
            JSONAssert.assertEquals(response2.getBody().asString(), expectJson2, false);
        } catch (JSONException e) {
            e.printStackTrace();
            Assert.fail("Failed to compare result with expect json 2 ", e);
        }

    }



    @Test(groups = "iot", description = "Test api engine interface: Query all plant by graphql.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with graphql and verify if correct return.")
    @Story("Query all iot plant by graphql")
    public void queryAllIOTPlantByGraphQL() {
        Reporter.log("Send request to graphql api with graphql");

        String query = "{\n" +
                "      Plant_Owner(cond: \"\", order: \"\") {\n" +
                "        credit_card\n" +
                "        tax_register_org_name\n" +
                "        name\n" +
                "        cellphone\n" +
                "        identification_card\n" +
                "        id\n" +
                "        org_name\n" +
                "        tax_register_no\n" +
                "        org_no\n" +
                "        registered_capital\n" +
                "        email\n" +
                "        Owner_Plant(cond: \"\", order: \"\") {\n" +
                "          address\n" +
                "          plant_owner\n" +
                "          latitude\n" +
                "          name\n" +
                "          id\n" +
                "          longitude\n" +
                "          Has_Device(cond: \"\", order: \"\") {\n" +
                "            id\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }";

        Response response = ApiEngineEndpoint.postGraphql(query);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));

        ArrayList<HashMap> data = jsonPathEvaluator.get("data.Plant_Owner");
        Assert.assertEquals(data.size(), 6);

        assertThat(response.getBody().asString(),
                matchesJsonSchemaInClasspath("iot-all-plants.json"));

    }



    @Test(groups = "iot", description = "Test api engine interface: Query one plant by graphql.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with graphql and verify if correct return.")
    @Story("Query one iot plant by graphql")
    public void queryOneIOTPlantByGraphQL() {
        Reporter.log("Send request to graphql api with graphql");

        String query = "{\n" +
                "      Plant(cond: \"{id:{_eq: 3}}\", order: \"\") {\n" +
                "        address\n" +
                "        plant_owner\n" +
                "        latitude\n" +
                "        name\n" +
                "        id\n" +
                "        longitude\n" +
                "        invert_Plant_Owner(cond: \"\", order: \"\") {\n" +
                "          credit_card\n" +
                "          tax_register_org_name\n" +
                "          name\n" +
                "          cellphone\n" +
                "          identification_card\n" +
                "          id\n" +
                "          org_name\n" +
                "          tax_register_no\n" +
                "          org_no\n" +
                "          registered_capital\n" +
                "          email\n" +
                "        }\n" +
                "        Has_Device(cond: \"\", order: \"\") {\n" +
                "          hi\n" +
                "          amps\n" +
                "          bearings\n" +
                "          rating\n" +
                "          type\n" +
                "          eff_grade\n" +
                "          manufacturer\n" +
                "          output\n" +
                "          sf\n" +
                "          design\n" +
                "          model\n" +
                "          id\n" +
                "          sinamics_300\n" +
                "          volts\n" +
                "          eff\n" +
                "          poles\n" +
                "          electric_current\n" +
                "          ip\n" +
                "          weight\n" +
                "          rpm\n" +
                "          ins\n" +
                "          plant\n" +
                "          name\n" +
                "          sinamics_300_port\n" +
                "          serial_no\n" +
                "          frame\n" +
                "        }\n" +
                "      }\n" +
                "    }";

        Response response = ApiEngineEndpoint.postGraphql(query);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));

        ArrayList<HashMap> data = jsonPathEvaluator.get("data.Plant");
        Assert.assertEquals(data.size(), 1);


        String expectJson = "{\n" +
                "    \"code\":100000,\n" +
                "    \"message\":\"Successfully\",\n" +
                "    \"data\":{\n" +
                "        \"Plant\":[\n" +
                "            {\n" +
                "                \"address\":\"湖南长沙市雨花区环保科技园仙岭中路85号\",\n" +
                "                \"plant_owner\":\"3\",\n" +
                "                \"latitude\":28.07459,\n" +
                "                \"name\":\"新日域(湖南)机电制造厂\",\n" +
                "                \"id\":\"3\",\n" +
                "                \"longitude\":113.02856,\n" +
                "                \"invert_Plant_Owner\":{\n" +
                "                    \"credit_card\":\"91320214762418438A\",\n" +
                "                    \"tax_register_org_name\":\"上海市工商局\",\n" +
                "                    \"name\":\"薄建军\",\n" +
                "                    \"cellphone\":\"13232341234\",\n" +
                "                    \"identification_card\":\"110103123412341234\",\n" +
                "                    \"id\":\"3\",\n" +
                "                    \"org_name\":\"济南西门子有限公司\",\n" +
                "                    \"tax_register_no\":\"91320214762418438A\",\n" +
                "                    \"org_no\":\"76241843-8\",\n" +
                "                    \"registered_capital\":50000000,\n" +
                "                    \"email\":\"fujianjun@siemens.com\"\n" +
                "                },\n" +
                "                \"Has_Device\":[\n" +
                "                    {\n" +
                "                        \"hi\":72,\n" +
                "                        \"amps\":0.9,\n" +
                "                        \"bearings\":\"6306\",\n" +
                "                        \"rating\":\"DFBLDWTT\",\n" +
                "                        \"type\":\"三相异步电动机\",\n" +
                "                        \"eff_grade\":\"三级\",\n" +
                "                        \"manufacturer\":\"TOSHIBA\",\n" +
                "                        \"output\":80,\n" +
                "                        \"sf\":92,\n" +
                "                        \"design\":\"TOSHIBA\",\n" +
                "                        \"model\":\"QX3-20-0.55\",\n" +
                "                        \"id\":\"10\",\n" +
                "                        \"sinamics_300\":\"ddd19ed6-7fb5-4ab0-972a-75a8657a30de\",\n" +
                "                        \"volts\":64,\n" +
                "                        \"eff\":37.4,\n" +
                "                        \"poles\":38,\n" +
                "                        \"electric_current\":28.8,\n" +
                "                        \"ip\":50,\n" +
                "                        \"weight\":48.4,\n" +
                "                        \"rpm\":44,\n" +
                "                        \"ins\":\"A\",\n" +
                "                        \"plant\":\"3\",\n" +
                "                        \"name\":\"三相异步电动机\",\n" +
                "                        \"sinamics_300_port\":6,\n" +
                "                        \"serial_no\":\"1546629c-5d3f-4170-a56d-0e1a6843c335\",\n" +
                "                        \"frame\":\"200L\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"hi\":67,\n" +
                "                        \"amps\":96.7,\n" +
                "                        \"bearings\":\"6306\",\n" +
                "                        \"rating\":\"AJTKVCAC\",\n" +
                "                        \"type\":\"单相异步电动机\",\n" +
                "                        \"eff_grade\":\"二级\",\n" +
                "                        \"manufacturer\":\"ABB\",\n" +
                "                        \"output\":32,\n" +
                "                        \"sf\":77,\n" +
                "                        \"design\":\"ABB\",\n" +
                "                        \"model\":\"QX3-20-0.55\",\n" +
                "                        \"id\":\"16\",\n" +
                "                        \"sinamics_300\":\"56561233-f2e5-44cf-abe8-1284861b3eeb\",\n" +
                "                        \"volts\":91,\n" +
                "                        \"eff\":94.4,\n" +
                "                        \"poles\":62,\n" +
                "                        \"electric_current\":24,\n" +
                "                        \"ip\":66,\n" +
                "                        \"weight\":21,\n" +
                "                        \"rpm\":7,\n" +
                "                        \"ins\":\"H\",\n" +
                "                        \"plant\":\"3\",\n" +
                "                        \"name\":\"单相异步电动机\",\n" +
                "                        \"sinamics_300_port\":8,\n" +
                "                        \"serial_no\":\"0c5120a7-831e-4a48-9fac-2178a3576958\",\n" +
                "                        \"frame\":\"ABS-70\"\n" +
                "                    }\n" +
                "                ]\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
//        System.out.println(response2.getBody().prettyPrint());
//        assertThat(response.getBody().asString(),
//                matchesJsonSchemaInClasspath("jinzu-all-projects-graphql-schema.json"));
        try {
            JSONAssert.assertEquals(response.getBody().asString(), expectJson, false);
        } catch (JSONException e) {
            e.printStackTrace();
            Assert.fail("Failed to compare result with expect json ", e);
        }

    }



    @Test(groups = "iot", description = "Test api engine interface: Query one device by graphql.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with graphql and verify if correct return.")
    @Story("Query one iot device by graphql")
    public void queryOneIOTDeviceByGraphQL() {
        Reporter.log("Send request to graphql api with graphql");

        String query = "{\n" +
                "\tDevice(cond: \"{id:{_eq:10}}\", order: \"\") {\n" +
                "\t  hi\n" +
                "\t  amps\n" +
                "\t  bearings\n" +
                "\t  rating\n" +
                "\t  type\n" +
                "\t  eff_grade\n" +
                "\t  manufacturer\n" +
                "\t  output\n" +
                "\t  sf\n" +
                "\t  design\n" +
                "\t  model\n" +
                "\t  id\n" +
                "\t  sinamics_300\n" +
                "\t  volts\n" +
                "\t  eff\n" +
                "\t  poles\n" +
                "\t  electric_current\n" +
                "\t  ip\n" +
                "\t  weight\n" +
                "\t  rpm\n" +
                "\t  ins\n" +
                "\t  plant\n" +
                "\t  name\n" +
                "\t  sinamics_300_port\n" +
                "\t  serial_no\n" +
                "\t  frame\n" +
                "\t  Connect_To_SINAMICS_300_Info(cond: \"\", order: \"\") {\n" +
                "\t\tplant\n" +
                "\t\tcontract\n" +
                "\t\tname\n" +
                "\t\tserial_no\n" +
                "\t\tmanufacturer\n" +
                "\t  }\n" +
                "\t}\n" +
                " }";

        Response response = ApiEngineEndpoint.postGraphql(query);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));

        ArrayList<HashMap> data = jsonPathEvaluator.get("data.Device");
        Assert.assertEquals(data.size(), 1);


        String expectJson = "{\n" +
                "    \"code\":100000,\n" +
                "    \"message\":\"Successfully\",\n" +
                "    \"data\":{\n" +
                "        \"Device\":[\n" +
                "            {\n" +
                "                \"hi\":72,\n" +
                "                \"amps\":0.9,\n" +
                "                \"bearings\":\"6306\",\n" +
                "                \"rating\":\"DFBLDWTT\",\n" +
                "                \"type\":\"三相异步电动机\",\n" +
                "                \"eff_grade\":\"三级\",\n" +
                "                \"manufacturer\":\"TOSHIBA\",\n" +
                "                \"output\":80,\n" +
                "                \"sf\":92,\n" +
                "                \"design\":\"TOSHIBA\",\n" +
                "                \"model\":\"QX3-20-0.55\",\n" +
                "                \"id\":\"10\",\n" +
                "                \"sinamics_300\":\"ddd19ed6-7fb5-4ab0-972a-75a8657a30de\",\n" +
                "                \"volts\":64,\n" +
                "                \"eff\":37.4,\n" +
                "                \"poles\":38,\n" +
                "                \"electric_current\":28.8,\n" +
                "                \"ip\":50,\n" +
                "                \"weight\":48.4,\n" +
                "                \"rpm\":44,\n" +
                "                \"ins\":\"A\",\n" +
                "                \"plant\":\"3\",\n" +
                "                \"name\":\"三相异步电动机\",\n" +
                "                \"sinamics_300_port\":6,\n" +
                "                \"serial_no\":\"1546629c-5d3f-4170-a56d-0e1a6843c335\",\n" +
                "                \"frame\":\"200L\",\n" +
                "                \"Connect_To_SINAMICS_300_Info\":{\n" +
                "                    \"plant\":\"4\",\n" +
                "                    \"contract\":\"sx-1101-03\",\n" +
                "                    \"name\":\"SINAMICS CONNECT 300\",\n" +
                "                    \"serial_no\":\"ddd19ed6-7fb5-4ab0-972a-75a8657a30de\",\n" +
                "                    \"manufacturer\":\"SIEMENS\"\n" +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
//        System.out.println(response.getBody().prettyPrint());
//        assertThat(response.getBody().asString(),
//                matchesJsonSchemaInClasspath("jinzu-all-projects-graphql-schema.json"));
        try {
            JSONAssert.assertEquals(response.getBody().asString(), expectJson, false);
        } catch (JSONException e) {
            e.printStackTrace();
            Assert.fail("Failed to compare result with expect json ", e);
        }

    }




    @Test(groups = "iot", description = "Test api engine interface: Query one device error alarm by graphql.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with graphql and verify if correct return.")
    @Story("Query one iot device error alarm by graphql")
    public void queryOneIOTDeviceErrorByGraphQL() {
        Reporter.log("Send request to graphql api with graphql");

        String query = "{\n" +
                "\tSINAMICS_300_Log(cond:\"{update_time:{_gte:\\\"%s\\\", _lte:\\\"%s\\\"}, port:{_eq:\\\"6\\\"},sinamics_300:{_eq:\\\"ddd19ed6-7fb5-4ab0-972a-75a8657a30de\\\"} }\") {\n" +
                "\t  update_time\n" +
                "\t  Actual_fault_code\n" +
                "\t}\n" +
                "}";

        long startTime = System.currentTimeMillis() - 10*60*1000;
        long endTime = System.currentTimeMillis() - 5*60*1000;

//        List<String> l = new ArrayList<String>(
//                Arrays.asList(
//                        "Siid"
//                )
//        );

        Response response = ApiEngineEndpoint.postGraphql(String.format(query, startTime, endTime));

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));

        ArrayList<HashMap> data = jsonPathEvaluator.get("data.SINAMICS_300_Log");
        Assert.assertTrue(data.size() > 1);

        assertThat(response.getBody().asString(),
                matchesJsonSchemaInClasspath("iot-device-error-alarm.json"));


    }



    @Test(groups = "iot", description = "Test api engine interface: Query one device history data page by graphql.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with graphql and verify if correct return.")
    @Story("Query one iot device history data page by graphql")
    public void queryOneIOTDeviceHistoryDataPageByGraphQL() {
        Reporter.log("Send request to graphql api with graphql");

        String query = "{\n" +
                "\tSINAMICS_300_LogConnection(cond: \"{update_time:{_gte:\\\"%s\\\", _lte: \\\"%s\\\"}, port:{_eq:6}, sinamics_300:{_eq:\\\"ddd19ed6-7fb5-4ab0-972a-75a8657a30de\\\"}}\", order: \"update_time DESC\", after: \"0\", first: 5) {\n" +
                "\t  totalElements\n" +
                "\t  totalPages\n" +
                "\t  pageSize\n" +
                "\t  page\n" +
                "\t  numberOfElements\n" +
                "\t  edges {\n" +
                "\t\tnode {\n" +
                "\t\t  outputcurrent_actual_AI0\n" +
                "\t\t  Braking_status_word\n" +
                "\t\t  input_current_actual_AI0\n" +
                "\t\t  input_current_actual_AI1\n" +
                "\t\t  Act_filtered_DC_link_volt\n" +
                "\t\t  Safely_remove_mem_card_status\n" +
                "\t\t  Actual_speed_smoothed\n" +
                "\t\t  Motor_utilization_thermal\n" +
                "\t\t  Number_of_drive_objects\n" +
                "\t\t  id\n" +
                "\t\t  port1_CO_motor_temperature\n" +
                "\t\t  Fault_code_6\n" +
                "\t\t  sinamics_300\n" +
                "\t\t  Fault_code_5\n" +
                "\t\t  Fault_code_8\n" +
                "\t\t  Fault_code_7\n" +
                "\t\t  Status_word_1\n" +
                "\t\t  Pulse_frequency_Mod_min_value\n" +
                "\t\t  Status_word_2\n" +
                "\t\t  Digital_in_status_inverted\n" +
                "\t\t  CO_Act_filtered_current_Isq\n" +
                "\t\t  Status_word_faults_alarms_2\n" +
                "\t\t  Energy_consumpt_meter\n" +
                "\t\t  Status_word_faults_alarms_1\n" +
                "\t\t  Converter_state\n" +
                "\t\t  Device_type_identification\n" +
                "\t\t  Device_Firmware_version\n" +
                "\t\t  Analog_inputs_in_percent_AI1\n" +
                "\t\t  Missing_enable_sig\n" +
                "\t\t  unit_line_supply_voltage\n" +
                "\t\t  Speed_setpoint_smoothed\n" +
                "\t\t  port\n" +
                "\t\t  Analog_inputs_in_percent_AI0\n" +
                "\t\t  Firmware_date_year\n" +
                "\t\t  Firmware_patch_hot_fix\n" +
                "\t\t  terminal_actual_value\n" +
                "\t\t  CO_Act_motor_temperature\n" +
                "\t\t  S6_load_duty_cycle\n" +
                "\t\t  Company_IDDeviceidentification\n" +
                "\t\t  Rated_converter_power\n" +
                "\t\t  Rated_converter_current\n" +
                "\t\t  Firmware_date_day_month\n" +
                "\t\t  Abs_actual_current_smoothed\n" +
                "\t\t  ZSW_seq_ctrlStatuswordsequence\n" +
                "\t\t  S1_cont_duty_cycle\n" +
                "\t\t  update_time\n" +
                "\t\t  Energy_consumption_saved\n" +
                "\t\t  CU_Firmware_version\n" +
                "\t\t  Freq_Setpoint_before_RFG\n" +
                "\t\t  Digital_out_status\n" +
                "\t\t  Pulse_frequency_Actual\n" +
                "\t\t  Fault_code_2\n" +
                "\t\t  Fault_code_1\n" +
                "\t\t  Act_filtered_output_voltage\n" +
                "\t\t  Fault_code_4\n" +
                "\t\t  Fault_code_3\n" +
                "\t\t  Act_filtered_frequency\n" +
                "\t\t  Act_filtered_rotor_speed\n" +
                "\t\t  Digital_in_status\n" +
                "\t\t  Act_filtered_torque\n" +
                "\t\t  output_current_actual_AI1\n" +
                "\t\t  Actual_alarm_code\n" +
                "\t\t  Act_filtered_powerDisplays\n" +
                "\t\t  Rated_motor_current\n" +
                "\t\t  CO_Converter_temperature\n" +
                "\t\t  Control_word_faults_alarms\n" +
                "\t\t  Actual_fault_code\n" +
                "\t\t  Control_word_1\n" +
                "\t\t}\n" +
                "\t\tcursor\n" +
                "\t  }\n" +
                "\t  pageInfo {\n" +
                "\t\tendCursor\n" +
                "\t\thasNextPage\n" +
                "\t  }\n" +
                "\t}\n" +
                "}";

        long startTime = System.currentTimeMillis() - 60*60*1000;
        long endTime = System.currentTimeMillis() - 55*60*1000;

//        List<String> l = new ArrayList<String>(
//                Arrays.asList(
//                        "Siid"
//                )
//        );

        Response response = ApiEngineEndpoint.postGraphql(String.format(query, startTime, endTime));

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data.SINAMICS_300_LogConnection"));

        HashMap data = jsonPathEvaluator.get("data.SINAMICS_300_LogConnection");
        Assert.assertTrue(Integer.parseInt(data.get("totalElements").toString()) > 1);
        Assert.assertTrue(Integer.parseInt(data.get("totalPages").toString()) > 1);
        Assert.assertTrue(Integer.parseInt(data.get("pageSize").toString()) == 5);
        Assert.assertTrue(Integer.parseInt(data.get("numberOfElements").toString()) == 5);
        Assert.assertTrue(((List)(data.get("edges"))).size() == 5);

        assertThat(response.getBody().asString(),
                matchesJsonSchemaInClasspath("iot-device-history-data-page.json"));


    }



    @Test(groups = "iot", description = "Test api engine interface: Query one device one type one day history data page by graphql.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with graphql and verify if correct return.")
    @Story("Query one device one type one day history data page by graphql")
    public void queryOneIOTDeviceOneDayDataWithTypeByGraphQL() {
        Reporter.log("Send request to graphql api with graphql");

        String query = "{\n" +
                "  SINAMICS_300_LogConnection(cond: \"{update_time:{_gte:\\\"%s\\\", _lte: \\\"%s\\\"}, port:{_eq:6}, sinamics_300:{_eq:\\\"ddd19ed6-7fb5-4ab0-972a-75a8657a30de\\\"}}\", order: \"update_time DESC\", after: \"0\", first: 1000) {\n" +
                "\ttotalElements\n" +
                "\ttotalPages\n" +
                "\tpageSize\n" +
                "\tpage\n" +
                "\tnumberOfElements\n" +
                "\tedges {\n" +
                "\t  node {\n" +
                "\t\tupdate_time %s\n" +
                "\t  }\n" +
                "\t  cursor\n" +
                "\t}\n" +
                "\tpageInfo {\n" +
                "\t  endCursor\n" +
                "\t  hasNextPage\n" +
                "\t}\n" +
                "  }\n" +
                "}";

        long startTime = System.currentTimeMillis() - 24*60*60*1000;
        long endTime = System.currentTimeMillis();

        List<String> l = new ArrayList<String>(
                Arrays.asList(
                        "Act_filtered_rotor_speed Act_filtered_torque",
                        "Act_filtered_DC_link_volt Rated_motor_current",
                        "Act_filtered_DC_link_volt",
                        "Rated_converter_current",
                        "Rated_motor_current",
                        "Converter_state",
                        "CO_Converter_temperature",
                        "CO_Act_motor_temperature",
                        "Actual_speed_smoothed"
                )
        );

        for (String k:l
             ) {

            Response response = ApiEngineEndpoint.postGraphql(String.format(query, startTime, endTime, k));

            Reporter.log("Response status is " + response.getStatusCode());

            Reporter.log("Response Body is =>  " + response.getBody().asString());

            GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

            Assert.assertEquals("Successfully", rspBody.getMessage());
            Assert.assertEquals(100000, rspBody.getCode());

            JsonPath jsonPathEvaluator = response.jsonPath();

            Assert.assertNotNull(jsonPathEvaluator.get("data.SINAMICS_300_LogConnection"));

            HashMap data = jsonPathEvaluator.get("data.SINAMICS_300_LogConnection");
            Assert.assertTrue(Integer.parseInt(data.get("totalElements").toString()) > 1);
            Assert.assertTrue(Integer.parseInt(data.get("totalPages").toString()) >= 1);
            Assert.assertTrue(Integer.parseInt(data.get("pageSize").toString()) == 1000);
            Assert.assertTrue(Integer.parseInt(data.get("numberOfElements").toString()) > 1);
            Assert.assertTrue(((List)(data.get("edges"))).size() > 5);

            List<HashMap> values = jsonPathEvaluator.getList("data.SINAMICS_300_LogConnection.edges.node");
            for (HashMap m: values
                 ) {
                Assert.assertTrue(m.values()
                        .stream()
                        .allMatch(Objects::nonNull));
            }
        }

    }

//    @Test(groups = "iems",  description = "Test api engine interface: Query all instance of one entity with eq condition in relation entity by graphql.")
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


//    @Test(groups = "iems",  description = "Test api engine interface: Query all instance of one entity with invalid condition in relation entity by graphql.")
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
//    @Test(groups = "iems",  description = "Test api engine interface: Query all instance of one entity with select column in relation entity by graphql.")
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
