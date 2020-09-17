package com.siemens.datalayer.apiservice.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.siemens.datalayer.apiservice.model.ApiResponse;
import com.siemens.datalayer.apiservice.model.HeatPumpKpiData;
import com.siemens.datalayer.apiservice.model.SubscriptionKPIResult;
import com.siemens.datalayer.apiservice.model.SubscriptionSp5Result;
import com.siemens.datalayer.utils.RabbitMQ;
import com.siemens.datalayer.utils.Utils;
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
import java.util.*;

@Epic("Api Service Interface")
@Feature("Rest API")
public class ApiServiceInterfaceTests {

    @Parameters({"baseUrl", "port"})
    @BeforeClass
    public void setApiserviceEndpoint(@Optional("http://140.231.89.85") String baseUrl, @Optional("31332") String port) {
        ApiServiceEndpoint.setBaseUrl(baseUrl);
        ApiServiceEndpoint.setPort(port);
    }

    @Test(priority = 0, description = "Test api service interface: Get devices by type.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if all the device type can be read out.")
    @Story("Get devices by type")
    public void getDeviceByWaterPumpType() {
        Reporter.log("Send request to getDeviceByType api with waterPump type");

        HashMap<String, String> queryParameters = new HashMap<>();
        queryParameters.put("device_type", "waterPump");

        Response response = ApiServiceEndpoint.getDevicesByType(queryParameters);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

        Assert.assertEquals("OK", rspBody.getMessage());
        Assert.assertEquals(200, rspBody.getCode());


        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));

        ArrayList<HashMap> data = jsonPathEvaluator.get("data");
        Assert.assertEquals(data.size(), 20);
        data.forEach(x -> Assert.assertEquals(x.get("deviceType"), "waterPump"));

    }


    @Test(priority = 0, description = "Test api service interface: Get devices by invalid type.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if invalid type will return correct message.")
    @Story("Get devices by invalid type")
    public void getDeviceByInvalidType() {
        Reporter.log("Send request to getDeviceByType api with invalid type");

        HashMap<String, String> queryParameters = new HashMap<>();
        queryParameters.put("device_type", "invalid");

        Response response = ApiServiceEndpoint.getDevicesByType(queryParameters);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

        Assert.assertEquals("Device not exist", rspBody.getMessage());
        Assert.assertEquals(102101, rspBody.getCode());
        Assert.assertNull(rspBody.getData());

    }

    @Test(priority = 0, description = "Test api service interface: Get devices by device type empty.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if not input device type will return correct message.")
    @Story("Get devices by device type empty")
    public void getDeviceByNoDeviceType() {
        Reporter.log("Send request to getDeviceByType api without device type");

        HashMap<String, String> queryParameters = new HashMap<>();

        Response response = ApiServiceEndpoint.getDevicesByType(queryParameters);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

        Assert.assertEquals("Device not exist", rspBody.getMessage());
        Assert.assertEquals(102101, rspBody.getCode());
        Assert.assertNull(rspBody.getData());

    }


    @Test(priority = 0, description = "Test api service interface: List all device type.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if can list all device type.")
    @Story("List all device type")
    public void listAllDeviceTypes() {
        Reporter.log("Send request to listAllDeviceTypes api");

        Response response = ApiServiceEndpoint.listAllDeviceTypes();

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

        Assert.assertEquals("OK", rspBody.getMessage());
        Assert.assertEquals(200, rspBody.getCode());


        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));

        ArrayList<HashMap> data = jsonPathEvaluator.get("data");
        Assert.assertEquals(data.size(), 53);

    }


    @Test(priority = 0, description = "Test api service interface: List device type.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if can list device type.")
    @Story("List device type")
    public void listDeviceTypes() {
        Reporter.log("Send request to listDeviceTypes api");

        Response response = ApiServiceEndpoint.listDeviceTypes();

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

        Assert.assertEquals("OK", rspBody.getMessage());
        Assert.assertEquals(200, rspBody.getCode());


        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));

        ArrayList<String> data = jsonPathEvaluator.get("data");
        Assert.assertEquals(data.size(), 8);
        List<String> l = new ArrayList<String>(
                Arrays.asList(
                        "busLine",
                        "heatPumpDetail",
                        "load",
                        "converter",
                        "valve",
                        "waterPump",
                        "heatStorage",
                        "buyAndSale"
                )
        );
        Assert.assertTrue(Utils.equalLists(data, l));

    }


    @Test(priority = 0, description = "Test api service interface: get sensor by device id.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if can get sensor by device id.")
    @Story("Get sensor by device id")
    public void getSensorByDeviceId() {

        Reporter.log("Send request to getDevicesByType api with device_type heatPumpDetail");

        HashMap<String, String> queryParameters = new HashMap<>();
        queryParameters.put("device_type", "heatPumpDetail");

        Response response = ApiServiceEndpoint.getDevicesByType(queryParameters);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

        Assert.assertEquals("OK", rspBody.getMessage());
        Assert.assertEquals(200, rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();
        ArrayList<HashMap> data = jsonPathEvaluator.get("data");

        for (HashMap m : data) {
            Reporter.log("Send request to getSensorByDeviceId api with id");

            HashMap<String, String> q = new HashMap<>();
            q.put("id", (String) m.get("id"));

            Response response2 = ApiServiceEndpoint.getSensorByDeviceId(q);

            Reporter.log("Response status is " + response2.getStatusCode());

            Reporter.log("Response Body is =>  " + response2.getBody().asString());

            ApiResponse rspBody2 = response2.getBody().as(ApiResponse.class);

            Assert.assertEquals("OK", rspBody2.getMessage());
            Assert.assertEquals(200, rspBody2.getCode());
        }


    }


    @Test(priority = 0, description = "Test api service interface: get sensor by invalid device id.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if get sensor by invalid device id return correct.")
    @Story("Get sensor by invalid device id")
    public void getSensorByInvalidDeviceId() {

        Reporter.log("Send request to getSensorByDeviceId api with invalid id");

        HashMap<String, String> q = new HashMap<>();
        q.put("id", "999999");

        Response response2 = ApiServiceEndpoint.getSensorByDeviceId(q);

        Reporter.log("Response status is " + response2.getStatusCode());

        Reporter.log("Response Body is =>  " + response2.getBody().asString());

        ApiResponse rspBody2 = response2.getBody().as(ApiResponse.class);

        Assert.assertEquals("Sensor not exist", rspBody2.getMessage());
        Assert.assertEquals(102102, rspBody2.getCode());


    }


    @Test(priority = 0, description = "Test api service interface: get sensor by invalid format device id.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if get sensor by invalid format device id return correct.")
    @Story("Get sensor by invalid format device id")
    public void getSensorByInvalidFormatDeviceId() {

        Reporter.log("Send request to getSensorByDeviceId api with invalid format id");

        HashMap<String, String> q = new HashMap<>();
        q.put("id", "aaaaaaaaaaaaaaaaaaa");

        Response response2 = ApiServiceEndpoint.getSensorByDeviceId(q);

        Reporter.log("Response status is " + response2.getStatusCode());

        Reporter.log("Response Body is =>  " + response2.getBody().asString());

        ApiResponse rspBody2 = response2.getBody().as(ApiResponse.class);

        Assert.assertEquals("Parameter type is not correct!", rspBody2.getMessage());
        Assert.assertEquals(1001, rspBody2.getCode());


    }


    @Test(priority = 0, description = "Test api service interface: get sensor data by device id with wrong endtime format.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if get sensor data by device id with wrong endtime format return correct.")
    @Story("Get sensor data by device id with wrong endtime format")
    public void getSensorDataByDeviceIdWithWrongEndTimeFormat() {

        Reporter.log("Send request to getSensorDataByDeviceId api with wrong endtime format");

        String q = "{\n" +
                "  \"endTime\": 2594366300000L,\n" +
                "  \"deviceId\": 34133,\n" +
                "  \"startTime\": 1594366100000\n" +
                "}";

        Response response = ApiServiceEndpoint.getSensorDataByDeviceId(q);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

//        Assert.assertEquals("Bad Request", rspBody.getError());
        Assert.assertEquals(1001, rspBody.getCode());


    }


    @Test(priority = 0, description = "Test api service interface: get sensor data by device id with wrong startTime format.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if get sensor data by device id with wrong startTime format return correct.")
    @Story("Get sensor data by device id with wrong startTime format")
    public void getSensorDataByDeviceIdWithWrongStartTimeFormat() {

        Reporter.log("Send request to getSensorDataByDeviceId api with wrong startTime format");

        String q = "{\n" +
                "  \"endTime\": 2594366300000,\n" +
                "  \"deviceId\": 34133,\n" +
                "  \"startTime\": 1594366100000L\n" +
                "}";

        Response response = ApiServiceEndpoint.getSensorDataByDeviceId(q);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

//        Assert.assertEquals("Bad Request", rspBody.getError());
        Assert.assertEquals(1001, rspBody.getCode());


    }


    @Test(priority = 0, description = "Test api service interface: get sensor data by device id with invalid device id.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if get sensor data by invalid device id return correct.")
    @Story("Get sensor data by device id with invalid device id")
    public void getSensorDataByDeviceIdWithInvalidId() {

        Reporter.log("Send request to getSensorDataByDeviceId api with invalid device id");

        String q = "{\n" +
                "  \"endTime\": 2594366300000,\n" +
                "  \"deviceId\": 3413399,\n" +
                "\"startTime\": 2594366200000\n" +
                "}";

        Response response = ApiServiceEndpoint.getSensorDataByDeviceId(q);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

        Assert.assertEquals("Device not exist", rspBody.getMessage());
        Assert.assertEquals(102101, rspBody.getCode());
        Assert.assertNull(rspBody.getData());


    }


    @Test(priority = 0, description = "Test api service interface: get sensor data by device id with wrong endtime format.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if get sensor data by device id with wrong endtime format return correct.")
    @Story("Get sensor data by device id with wrong endtime format")
    public void getSensorDataBySensorIdWithWrongEndTimeFormat() {

        Reporter.log("Send request to getSensorDataByDeviceId api with wrong endtime format");

        String q = "{\n" +
                "  \"endTime\": 2594366300000L,\n" +
                "  \"sensor_list\": [\n" +
                "    {\n" +
                "      \"siid\": 12345\n" +
                "    }\n" +
                "  ],\n" +
                "  \"startTime\": 1594366100000\n" +
                "}";

        Response response = ApiServiceEndpoint.getSensorDataBySensorId(q);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

//        Assert.assertEquals("Bad Request", rspBody.getError());
        Assert.assertEquals(1001, rspBody.getCode());


    }


    @Test(priority = 0, description = "Test api service interface: get sensor data by device id with wrong startTime format.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if get sensor data by device id with wrong startTime format return correct.")
    @Story("Get sensor data by device id with wrong startTime format")
    public void getSensorDataBySensorIdWithWrongStartTimeFormat() {

        Reporter.log("Send request to getSensorDataByDeviceId api with wrong startTime format");

        String q = "{\n" +
                "  \"endTime\": 2594366300000,\n" +
                "  \"sensor_list\": [\n" +
                "    {\n" +
                "      \"siid\": 12345\n" +
                "    }\n" +
                "  ],\n" +
                "  \"startTime\": 1594366100000L\n" +
                "}";

        Response response = ApiServiceEndpoint.getSensorDataBySensorId(q);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

//        Assert.assertEquals("Bad Request", rspBody.getError());
        Assert.assertEquals(1001, rspBody.getCode());


    }


    @Test(priority = 0, description = "Test api service interface: get sensor data by device id with invalid device id.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if get sensor data by invalid device id return correct.")
    @Story("Get sensor data by device id with invalid device id")
    public void getSensorDataBySensorIdWithInvalidId() {

        Reporter.log("Send request to getSensorDataByDeviceId api with invalid device id");

        String q = "{\n" +
                "  \"endTime\": 2594366300000,\n" +
                "  \"sensor_list\": [\n" +
                "    {\n" +
                "      \"siid\": 999999\n" +
                "    }\n" +
                "  ],\n" +
                "\"startTime\": 2594366200000\n" +
                "}";

        Response response = ApiServiceEndpoint.getSensorDataBySensorId(q);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

        Assert.assertEquals("OK", rspBody.getMessage());
        Assert.assertEquals(200, rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertTrue(Utils.isNullOrEmpty((Collection) jsonPathEvaluator.get("data")));

    }


    @Test(priority = 0, description = "Test api service interface: Get sensor data by device id.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if all sensor data belong given device id returned.")
    @Story("Get sensor data by device id")
    public void getSensorDataByDeviceId() {
        HashMap<String, String> deviceMap = ApiServiceHelper.getDeviceIdByName(new ArrayList<String>(){{
            add("1#制冷机");
            add("3#制冷机");
        }});


        Reporter.log("Send request to getSensorDataByDeviceId api with device id");

        String q = String.format("{\n" +
                "  \"endTime\": 2594366300000,\n" +
                "  \"deviceId\": %s,\n" +
                "\"startTime\": 1594366100000\n" +
                "}", deviceMap.get("1#制冷机"));

        Response response2 = ApiServiceEndpoint.getSensorDataByDeviceId(q);

        Reporter.log("Response status is " + response2.getStatusCode());

        Reporter.log("Response Body is =>  " + response2.getBody().asString());

        ApiResponse rspBody2 = response2.getBody().as(ApiResponse.class);

        Assert.assertEquals("OK", rspBody2.getMessage());
        Assert.assertEquals(200, rspBody2.getCode());

        JsonPath jsonPathEvaluator2 = response2.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator2.get("data"));

        ArrayList<HashMap> data2 = jsonPathEvaluator2.get("data");
//        Assert.assertEquals(data2.size(), 3712);
        Assert.assertTrue(data2.size() > 0);

    }


    @Test(priority = 0, description = "Test api service interface: Get sensor data by sensor id.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if all sensor data belong given sensor id returned.")
    @Story("Get sensor data by sensor id")
    public void getSensorDataBySensorId() {
        Reporter.log("Send request to getSensorDataBySensorId api with sensor id");

        String q = String.format("{\n" +
                "  \"endTime\": 2594366300000,\n" +
                "  \"sensor_list\": [\n" +
                "    {\n" +
                "      \"siid\": %s\n" +
                "    }\n" +
                "  ],\n" +
                "  \"startTime\": 1594366100000\n" +
                "}", "5040");

        Response response = ApiServiceEndpoint.getSensorDataBySensorId(q);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

        Assert.assertEquals("OK", rspBody.getMessage());
        Assert.assertEquals(200, rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));

        ArrayList<HashMap> data = jsonPathEvaluator.get("data");
//        Assert.assertEquals(data.size(), 3720);
        Assert.assertTrue(data.size() > 0);

    }


    @Test(priority = 0, description = "Test api service interface: Get device info by invalid id.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if invalid id will return correct message.")
    @Story("Get devices info by invalid id")
    public void getDeviceInfoWithInvalidId() {
        Reporter.log("Send request to getDeviceInfo api with invalid id");

        HashMap<String, String> queryParameters = new HashMap<>();
        queryParameters.put("id", "99999");

        Response response = ApiServiceEndpoint.getDeviceInfo(queryParameters);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

        Assert.assertEquals("Device not exist", rspBody.getMessage());
        Assert.assertEquals(102101, rspBody.getCode());
        Assert.assertNull(rspBody.getData());

    }


    @Test(priority = 0, description = "Test api service interface: Get device info with valid id.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if valid id will return correct message.")
    @Story("Get devices info by invalid id")
    public void getDeviceInfo() {
        HashMap<String, String> deviceMap = ApiServiceHelper.getDeviceIdByName(new ArrayList<String>(){{
            add("1#制冷机");
            add("3#制冷机");
        }});


        Reporter.log("Send request to getDeviceInfo api with device id");

        HashMap<String, String> queryParameters2 = new HashMap<>();
        queryParameters2.put("id", deviceMap.get("1#制冷机"));

        Response response2 = ApiServiceEndpoint.getDeviceInfo(queryParameters2);

        Reporter.log("Response status is " + response2.getStatusCode());

        Reporter.log("Response Body is =>  " + response2.getBody().asString());

        ApiResponse rspBody2 = response2.getBody().as(ApiResponse.class);

        Assert.assertEquals("OK", rspBody2.getMessage());
        Assert.assertEquals(200, rspBody2.getCode());
        Assert.assertNotNull(rspBody2.getData());

        JsonPath jsonPathEvaluator2 = response2.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator2.get("data"));

        HashMap data2 = jsonPathEvaluator2.get("data");
//        Assert.assertEquals(data.size(), 3720);
        Assert.assertEquals(deviceMap.get("1#制冷机"), String.valueOf(data2.get("id")));
        Assert.assertEquals("1#制冷机", String.valueOf(data2.get("label")));
        HashMap p = jsonPathEvaluator2.get("data.properties");
        Assert.assertEquals(147, p.size());
    }

    @Test(priority = 0, description = "Test api service interface: Delete subscriptions by invalid id.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a delete request to SUT and verify if invalid id will return correct message.")
    @Story("Delete subscriptions by invalid id")
    public void deleteSubscriptionsWithInvalidId() {
        Reporter.log("Send request to deleteSubscriptions api with invalid id");

        Response response = ApiServiceEndpoint.deleteSubscriptions("99999");

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

        Assert.assertEquals("Subscriptions not exist", rspBody.getMessage());
        Assert.assertEquals(102105, rspBody.getCode());
        Assert.assertNull(rspBody.getData());
//        Assert.assertTrue(rspBody.getCode() < 500);

    }


    @Test(priority = 0, description = "Test api service interface: Get top sensor data by device id.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a post request to SUT and verify if getTopSensorDataByDeviceId will return correct message.")
    @Story("Get top sensor data by device id")
    public void getTopSensorDataByDeviceId() {

        HashMap<String, String> deviceMap = ApiServiceHelper.getDeviceIdByName(new ArrayList<String>(){{
            add("1#制冷机");
            add("3#制冷机");
        }});

        Reporter.log("Send request to getTopSensorDataByDeviceId api");

        String q = "{\n" +
                "  \"deviceId\": %s,\n" +
                "  \"limit\": 5\n" +
                "}";

        Response response2 = ApiServiceEndpoint.getTopSensorDataByDeviceId(String.format(q, deviceMap.get("1#制冷机")));

        Reporter.log("Response status is " + response2.getStatusCode());

        Reporter.log("Response Body is =>  " + response2.getBody().asString());

        ApiResponse rspBody2 = response2.getBody().as(ApiResponse.class);

        Assert.assertEquals("OK", rspBody2.getMessage());
        Assert.assertEquals(200, rspBody2.getCode());

        JsonPath jsonPathEvaluator2 = response2.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator2.get("data"));

        ArrayList<HashMap> data2 = jsonPathEvaluator2.get("data");
        int total = data2.stream().mapToInt(x -> ((ArrayList) x.get("SensorData")).size()).sum();
        Assert.assertEquals(5, total);
        Assert.assertTrue(this.isSortedByDateKey(data2, "updateTime"));
    }

    @Test(priority = 0, description = "Test api service interface: Get top sensor data by device id without limit.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a post request to SUT and verify if getTopSensorDataByDeviceId without limit will return correct message.")
    @Story("Get top sensor data by device id without limit")
    public void getTopSensorDataByDeviceIdWithoutLimit() {

        Reporter.log("Send request to getTopSensorDataByDeviceId api without limit");

        String q = "{\n" +
                "  \"deviceId\": 46690\n" +
                "}";

        Response response = ApiServiceEndpoint.getTopSensorDataByDeviceId(q);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

        Assert.assertEquals("[limit:limit is null]", rspBody.getMessage());
        Assert.assertEquals(1001, rspBody.getCode());
        Assert.assertNull(rspBody.getData());

    }


    @Test(priority = 0, description = "Test api service interface: Get top sensor data by invalid device id.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a post request to SUT and verify if getTopSensorDataByDeviceId with invalid device id will return correct message.")
    @Story("Get top sensor data by invalid device id")
    public void getTopSensorDataByDeviceIdWithInvalidId() {

        Reporter.log("Send request to getTopSensorDataByDeviceId api with invalid id");

        String q = "{\n" +
                "  \"deviceId\": 9999999,\n" +
                "  \"limit\": 100\n" +
                "}";

        Response response = ApiServiceEndpoint.getTopSensorDataByDeviceId(q);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

        Assert.assertEquals("Device not exist", rspBody.getMessage());
        Assert.assertEquals(102101, rspBody.getCode());
        Assert.assertNull(rspBody.getData());

    }


    @Test(priority = 0, description = "Test api service interface: Get top sensor data by limit large than 999.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a post request to SUT and verify if getTopSensorDataByDeviceId with limit large than 999 will return correct message.")
    @Story("Get top sensor data by limitlarge than 999")
    public void getTopSensorDataByDeviceIdWithLimitOutRange() {

        Reporter.log("Send request to getTopSensorDataByDeviceId api with limit 1000");

        String q = "{\n" +
                "  \"deviceId\": 44690,\n" +
                "  \"limit\": 1000\n" +
                "}";

        Response response = ApiServiceEndpoint.getTopSensorDataByDeviceId(q);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

        Assert.assertEquals("[limit:limit not correct, should be number < 1000]", rspBody.getMessage());
        Assert.assertEquals(1001, rspBody.getCode());
        Assert.assertNull(rspBody.getData());

    }


    @Test(priority = 0, description = "Test api service interface: Get top kpi data by device id.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a post request to SUT and verify if getTopKPIDataByDeviceId will return correct message.")
    @Story("Get top kpi data by device id")
    public void getTopKPIDataByDeviceId() {

        HashMap<String, String> deviceMap = ApiServiceHelper.getDeviceIdByName(new ArrayList<String>(){{
            add("1#制冷机");
            add("3#制冷机");
        }});

        Reporter.log("Send request to getTopKPIDataByDeviceId api");

        String q = "{\n" +
                "  \"deviceId\": %s,\n" +
                "  \"limit\": 100\n" +
                "}";

        Response response2 = ApiServiceEndpoint.getTopKPIDataByDeviceId(String.format(q, deviceMap.get("1#制冷机")));

        Reporter.log("Response status is " + response2.getStatusCode());

        Reporter.log("Response Body is =>  " + response2.getBody().asString());

        ApiResponse rspBody2 = response2.getBody().as(ApiResponse.class);

        Assert.assertEquals("OK", rspBody2.getMessage());
        Assert.assertEquals(200, rspBody2.getCode());

        JsonPath jsonPathEvaluator2 = response2.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator2.get("data"));

        ArrayList<HashMap> data2 = jsonPathEvaluator2.get("data");
        int total = data2.size();
        Assert.assertEquals(100, total);
        Assert.assertTrue(this.isSortedByDateStringKey(data2, "updateTime"));
    }

    @Test(priority = 0, description = "Test api service interface: Get top kpi data by device id without limit.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a post request to SUT and verify if getTopKPIDataByDeviceId without limit will return correct message.")
    @Story("Get top kpi data by device id without limit")
    public void getTopKPIDataByDeviceIdWithoutLimit() {

        Reporter.log("Send request to getTopKPIDataByDeviceId api without limit");

        String q = "{\n" +
                "  \"deviceId\": 46690\n" +
                "}";

        Response response = ApiServiceEndpoint.getTopKPIDataByDeviceId(q);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

        Assert.assertEquals("[limit:limit is null]", rspBody.getMessage());
        Assert.assertEquals(1001, rspBody.getCode());
        Assert.assertNull(rspBody.getData());

    }


    @Test(priority = 0, description = "Test api service interface: Get top kpi data by invalid device id.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a delete request to SUT and verify if getTopKPIDataByDeviceId with invalid device id will return correct message.")
    @Story("Get top kpi data by invalid device id")
    public void getTopKPIDataByDeviceIdWithInvalidId() {

        Reporter.log("Send request to getTopKPIDataByDeviceId api with invalid id");

        String q = "{\n" +
                "  \"deviceId\": 9999999,\n" +
                "  \"limit\": 100\n" +
                "}";

        Response response = ApiServiceEndpoint.getTopKPIDataByDeviceId(q);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

        Assert.assertEquals("Device not exist", rspBody.getMessage());
        Assert.assertEquals(102101, rspBody.getCode());
        Assert.assertNull(rspBody.getData());

    }


    @Test(priority = 0, description = "Test api service interface: Get top sensor data by limit large than 999.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a post request to SUT and verify if getTopKPIDataByDeviceId with limit large than 999 will return correct message.")
    @Story("Get top kpi data by limitlarge than 999")
    public void getTopKPIDataByDeviceIdWithLimitOutRange() {

        Reporter.log("Send request to getTopKPIDataByDeviceId api with limit 1000");

        String q = "{\n" +
                "  \"deviceId\": 44690,\n" +
                "  \"limit\": 1000\n" +
                "}";

        Response response = ApiServiceEndpoint.getTopKPIDataByDeviceId(q);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

        Assert.assertEquals("[limit:limit not correct, should be number < 1000]", rspBody.getMessage());
        Assert.assertEquals(1001, rspBody.getCode());
        Assert.assertNull(rspBody.getData());

    }

    public boolean isSortedByDateKey(ArrayList<HashMap> list, String key) {
        boolean sorted = true;
        for (int i = 1; i < list.size(); i++) {
            if ((long) list.get(i - 1).get(key) - (long) list.get(i).get(key) < 0) {
                sorted = false;
            }
        }

        return sorted;
    }

    public boolean isSortedByDateStringKey(ArrayList<HashMap> list, String key) {
        boolean sorted = true;
        for (int i = 1; i < list.size(); i++) {
            DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dt1 = LocalDateTime.parse(list.get(i - 1).get(key).toString(), inputFormat);
            LocalDateTime dt2 = LocalDateTime.parse(list.get(i).get(key).toString(), inputFormat);
            if (dt1.isBefore(dt2)) {
                sorted = false;
            }
        }

        return sorted;
    }

    @Test(priority = 0, description = "Test api service interface: subscriptionsByDeviceId by invalid id.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a delete request to SUT and verify if subscriptionsByDeviceId by invalid id will return correct message.")
    @Story("SubscriptionsByDeviceId by invalid id")
    public void subscriptionsByDeviceIdWithInvalidId() {
        Reporter.log("Send request to subscriptionsByDeviceId api with invalid id");

        HashMap<String, String> queryParameters = new HashMap<>();
        queryParameters.put("deviceId", "99999");
        Response response = ApiServiceEndpoint.subscriptionsByDeviceId(queryParameters);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

        Assert.assertEquals("Sensor not exist", rspBody.getMessage());
        Assert.assertEquals(102102, rspBody.getCode());
        Assert.assertNull(rspBody.getData());
//        Assert.assertTrue(rspBody.getCode() < 500);

    }


    @Test(priority = 0, description = "Test api service interface: get kpi data by device id.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if can get kpi data by device id.")
    @Story("Get kpi data by device id")
    public void getKpiDataByDeviceId() {

        HashMap<String, String> deviceMap = ApiServiceHelper.getDeviceIdByName(new ArrayList<String>(){{
            add("1#制冷机");
            add("3#制冷机");
        }});


        Reporter.log("Send request to getKpiDataByDeviceId api with device id");

        String q = String.format("{\n" +
                "  \"endTime\": 2594366300000,\n" +
                "  \"deviceId\": %s,\n" +
                "\"startTime\": 1594366100000\n" +
                "}", deviceMap.get("1#制冷机"));

        Response response2 = ApiServiceEndpoint.getKpiDataByDeviceId(q);

        Reporter.log("Response status is " + response2.getStatusCode());

        Reporter.log("Response Body is =>  " + response2.getBody().asString());

        ApiResponse rspBody2 = response2.getBody().as(ApiResponse.class);

        Assert.assertEquals("OK", rspBody2.getMessage());
        Assert.assertEquals(200, rspBody2.getCode());

        JsonPath jsonPathEvaluator2 = response2.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator2.get("data"));

        ArrayList<HashMap> data2 = jsonPathEvaluator2.get("data");
//        Assert.assertEquals(data2.size(), 3712);
        Assert.assertTrue(data2.size() > 0);


    }


    @Test(priority = 0, description = "Test api service interface: get kpi data by device id with wrong endtime format.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if get kpi data by device id with wrong endtime format return correct.")
    @Story("Get kpi data by device id with wrong endtime format")
    public void getKpiDataByDeviceIdWithWrongEndTimeFormat() {

        Reporter.log("Send request to getKpiDataByDeviceId api with wrong endtime format");

        String q = "{\n" +
                "  \"endTime\": 2594366300000L,\n" +
                "  \"deviceId\": 34133,\n" +
                "  \"startTime\": 1594366100000\n" +
                "}";

        Response response = ApiServiceEndpoint.getKpiDataByDeviceId(q);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

//        Assert.assertEquals("Bad Request", rspBody.getError());
        Assert.assertEquals(1001, rspBody.getCode());


    }


    @Test(priority = 0, description = "Test api service interface: get kpi data by device id with wrong startTime format.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if get kpi data by device id with wrong startTime format return correct.")
    @Story("Get kpi data by device id with wrong startTime format")
    public void getKpiDataByDeviceIdWithWrongStartTimeFormat() {

        Reporter.log("Send request to getKpiDataByDeviceId api with wrong startTime format");

        String q = "{\n" +
                "  \"endTime\": 2594366300000,\n" +
                "  \"deviceId\": 34133,\n" +
                "  \"startTime\": 1594366100000L\n" +
                "}";

        Response response = ApiServiceEndpoint.getKpiDataByDeviceId(q);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

//        Assert.assertEquals("Bad Request", rspBody.getError());
        Assert.assertEquals(1001, rspBody.getCode());

    }


    @Test(priority = 0, description = "Test api service interface: get kpi data by device id with invalid device id.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if get kpi data by invalid device id return correct.")
    @Story("Get kpi data by device id with invalid device id")
    public void getKpiDataByDeviceIdWithInvalidId() {

        Reporter.log("Send request to getKpiDataByDeviceId api with invalid device id");

        String q = "{\n" +
                "  \"endTime\": 2594366300000,\n" +
                "  \"deviceId\": 3413399,\n" +
                "\"startTime\": 2594366200000\n" +
                "}";

        Response response = ApiServiceEndpoint.getKpiDataByDeviceId(q);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

        Assert.assertEquals("Device not exist", rspBody.getMessage());
        Assert.assertEquals(102101, rspBody.getCode());
        Assert.assertNull(rspBody.getData());


    }


    @Test(priority = 0, description = "Test api service interface: get kpi data by device id without endTime.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if get kpi data without endTime return correct.")
    @Story("Get kpi data by device id without endTime")
    public void getKpiDataByDeviceIdWithoutEndTime() {

        Reporter.log("Send request to getKpiDataByDeviceId api without endTime");

        String q = "{\n" +
                "  \"deviceId\": 3413399,\n" +
                "\"startTime\": 2594366200000\n" +
                "}";

        Response response = ApiServiceEndpoint.getKpiDataByDeviceId(q);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

        Assert.assertEquals("[endTime:endTime is null]", rspBody.getMessage());
        Assert.assertEquals(1001, rspBody.getCode());
        Assert.assertNull(rspBody.getData());


    }

    @Test(priority = 0, description = "Test api service interface: get kpi data by device id without startTime.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if get kpi data without endTime return correct.")
    @Story("Get kpi data by device id without endTime")
    public void getKpiDataByDeviceIdWithoutStartTime() {

        Reporter.log("Send request to getKpiDataByDeviceId api without startTime");

        String q = "{\n" +
                "  \"endTime\": 2594366300000,\n" +
                "  \"deviceId\": 3413399\n" +
                "}";

        Response response = ApiServiceEndpoint.getKpiDataByDeviceId(q);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

        Assert.assertEquals("[startTime:startTime is null]", rspBody.getMessage());
        Assert.assertEquals(1001, rspBody.getCode());
        Assert.assertNull(rspBody.getData());


    }


    @Test(priority = 0, description = "Test api service interface: get kpi data by device id without startTime endTime.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if get kpi data without startTime endTime return correct.")
    @Story("Get kpi data by device id without endTime")
    public void getKpiDataByDeviceIdWithoutStartTimeEndTime() {

        Reporter.log("Send request to getKpiDataByDeviceId api without startTime endTime");

        String q = "{\n" +
                "  \"deviceId\": 3413399\n" +
                "}";

        Response response = ApiServiceEndpoint.getKpiDataByDeviceId(q);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

        Assert.assertTrue(rspBody.getMessage().contains("[endTime:endTime is null]"));
        Assert.assertTrue(rspBody.getMessage().contains("[startTime:startTime is null]"));
        Assert.assertEquals(1001, rspBody.getCode());
        Assert.assertNull(rspBody.getData());

    }


    @Test(priority = 0, description = "Test api service interface: get kpi data by device id without deviceId.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if get kpi data without deviceId return correct.")
    @Story("Get kpi data by device id without deviceId")
    public void getKpiDataByDeviceIdWithoutDeviceId() {

        Reporter.log("Send request to getKpiDataByDeviceId api without deviceId");

        String q = "{\n" +
                "  \"endTime\": 2594366300000,\n" +
                "  \"startTime\": 2594366200000\n" +
                "}";

        Response response = ApiServiceEndpoint.getKpiDataByDeviceId(q);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

        Assert.assertEquals("[deviceId:deviceId is null]", rspBody.getMessage());
        Assert.assertEquals(1001, rspBody.getCode());
        Assert.assertNull(rspBody.getData());


    }

    @Test(priority = 0, description = "Test api service interface: Subscription by device id.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if user can subscribe by device id.")
    @Story("Subscribe by device id")
    public void subscriptionsByDeviceId() {
        HashMap<String, String> deviceMap = ApiServiceHelper.getDeviceIdByName(new ArrayList<String>(){{
            add("1#制冷机");
            add("3#制冷机");
        }});

        Reporter.log("Send request to subscriptionsByDeviceId api with id");

        HashMap<String, String> queryParameters2 = new HashMap<>();
        queryParameters2.put("deviceId", deviceMap.get("1#制冷机"));
        Response response2 = ApiServiceEndpoint.subscriptionsByDeviceId(queryParameters2);

        Reporter.log("Response status is " + response2.getStatusCode());

        Reporter.log("Response Body is =>  " + response2.getBody().asString());

        ApiResponse rspBody2 = response2.getBody().as(ApiResponse.class);

        Assert.assertEquals("OK", rspBody2.getMessage());
        Assert.assertEquals(200, rspBody2.getCode());


        JsonPath jsonPathEvaluator2 = response2.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator2.get("data"));

        HashMap data2 = jsonPathEvaluator2.get("data");
        String replyTo = data2.get("replyTo").toString();

        RabbitMQ mq = new RabbitMQ();

        mq.simulateSp5Produce();
        String result = mq.simulateSp5Consume(replyTo);
        ObjectMapper objMapper = new ObjectMapper();
        SubscriptionSp5Result subscriptionSp5Result = null;
        try {
            subscriptionSp5Result = objMapper.readValue(result, SubscriptionSp5Result.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(subscriptionSp5Result, "Failed to get message in 300s");

        Assert.assertEquals(20, subscriptionSp5Result.getSensorData().size());


        Response response3 = ApiServiceEndpoint.subscriptionsByDeviceId(queryParameters2);

        Reporter.log("Response status is " + response3.getStatusCode());

        Reporter.log("Response Body is =>  " + response3.getBody().asString());

        ApiResponse rspBody3 = response3.getBody().as(ApiResponse.class);

        Assert.assertEquals("OK", rspBody3.getMessage());
        Assert.assertEquals(200, rspBody3.getCode());


        JsonPath jsonPathEvaluator3 = response3.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator3.get("data"));

        HashMap data3 = jsonPathEvaluator3.get("data");
        String replyTo2 = data3.get("replyTo").toString();
        Assert.assertEquals(replyTo, replyTo2);
    }


    @Test(priority = 0, description = "Test api service interface: Subscription by sensor id.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if user can subscribe by sensor id.")
    @Story("Subscribe by sensor id")
    public void subscriptionsBySensorId() {
        HashMap<String, String> deviceMap = ApiServiceHelper.getDeviceIdByName(new ArrayList<String>(){{
            add("1#制冷机");
        }});

        ArrayList<String> sensorList = ApiServiceHelper.getSensorByDeviceId(deviceMap.get("1#制冷机"));

        Random rand = new Random();
        String randomSensorId = sensorList.get(rand.nextInt(sensorList.size()));

        Reporter.log("Send request to subscriptionsByDeviceId api with id");

        HashMap<String, String> queryParameters2 = new HashMap<>();
        queryParameters2.put("request", randomSensorId);
        Response response2 = ApiServiceEndpoint.subscriptionsBySensorId(queryParameters2);

        Reporter.log("Response status is " + response2.getStatusCode());

        Reporter.log("Response Body is =>  " + response2.getBody().asString());

        ApiResponse rspBody2 = response2.getBody().as(ApiResponse.class);

        Assert.assertEquals("OK", rspBody2.getMessage());
        Assert.assertEquals(200, rspBody2.getCode());


        JsonPath jsonPathEvaluator2 = response2.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator2.get("data"));

        HashMap data2 = jsonPathEvaluator2.get("data");
        String replyTo = data2.get("replyTo").toString();

        RabbitMQ mq = new RabbitMQ();

        mq.simulateSp5Produce();
        String result = mq.simulateSp5Consume(replyTo);
        ObjectMapper objMapper = new ObjectMapper();
        SubscriptionSp5Result subscriptionSp5Result = null;
        try {
            subscriptionSp5Result = objMapper.readValue(result, SubscriptionSp5Result.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(subscriptionSp5Result, "Failed to get message in 300s");

        Assert.assertEquals(1, subscriptionSp5Result.getSensorData().size());


        Response response3 = ApiServiceEndpoint.subscriptionsBySensorId(queryParameters2);

        Reporter.log("Response status is " + response3.getStatusCode());

        Reporter.log("Response Body is =>  " + response3.getBody().asString());

        ApiResponse rspBody3 = response3.getBody().as(ApiResponse.class);

        Assert.assertEquals("OK", rspBody3.getMessage());
        Assert.assertEquals(200, rspBody3.getCode());


        JsonPath jsonPathEvaluator3 = response3.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator3.get("data"));

        HashMap data3 = jsonPathEvaluator3.get("data");
        String replyTo2 = data3.get("replyTo").toString();
        Assert.assertEquals(replyTo, replyTo2);

        Reporter.log("Send request to deleteSubscriptions api with id");

        Response response4 = ApiServiceEndpoint.deleteSubscriptions(replyTo);


        Reporter.log("Response status is " + response4.getStatusCode());

        Reporter.log("Response Body is =>  " + response4.getBody().asString());

        ApiResponse rspBody4 = response4.getBody().as(ApiResponse.class);

        Assert.assertEquals("OK", rspBody4.getMessage());
        Assert.assertEquals(200, rspBody4.getCode());

    }


    @Test(priority = 0, description = "Test api service interface: Subscription kpi data by device id.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if user can subscribe kpi data by device id.")
    @Story("Subscribe kpi data by device id")
    public void subscriptionsWithKPIByDeviceId() {
        HashMap<String, String> deviceMap = ApiServiceHelper.getDeviceIdByName(new ArrayList<String>(){{
            add("1#制冷机");
            add("3#制冷机");
        }});

        Reporter.log("Send request to subscriptionsByDeviceId api with id");

        HashMap<String, String> queryParameters2 = new HashMap<>();
        queryParameters2.put("request", deviceMap.get("1#制冷机"));
        Response response2 = ApiServiceEndpoint.subscriptionsWithKPIByDeviceId(queryParameters2);

        Reporter.log("Response status is " + response2.getStatusCode());

        Reporter.log("Response Body is =>  " + response2.getBody().asString());

        ApiResponse rspBody2 = response2.getBody().as(ApiResponse.class);

        Assert.assertEquals("OK", rspBody2.getMessage());
        Assert.assertEquals(200, rspBody2.getCode());


        JsonPath jsonPathEvaluator2 = response2.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator2.get("data"));

        HashMap data2 = jsonPathEvaluator2.get("data");
        String replyTo = data2.get("replyTo").toString();

        RabbitMQ mq = new RabbitMQ();
        mq.simulateKPIProduce();
        String result2 =mq.simulateKPIConsume(replyTo);
        ObjectMapper objMapper = new ObjectMapper();
        SubscriptionKPIResult result = null;
        try {
            result = objMapper.readValue(result2, SubscriptionKPIResult.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(result, "Failed to get message in 300s");

        Assert.assertEquals(1, result.getHeatPumpKpiData().size());

        String expectJson = "{\n" +
                "\t\"deviceName\":\"1#制冷机\",\n" +
                "\t\"updateTime\":\"2020-09-01 06:15:33\",\n" +
                "\t\"compressor_poly_efficiency\":null,\n" +
                "\t\"condensor_hot_outlet_pressure\":null,\n" +
                "\t\"condensor_hot_outlet_temperature\":null,\n" +
                "\t\"condensor_hot_sat_temperature\":null,\n" +
                "\t\"condensor_water_inlet_temperature\":null,\n" +
                "\t\"condensor_water_outlet_temperature\":null,\n" +
                "\t\"dt_condensor\":null,\n" +
                "\t\"dt_evaporator\":null,\n" +
                "\t\"evaporator_cold_inlet_pressure\":null,\n" +
                "\t\"evaporator_cold_inlet_temperature\":null,\n" +
                "\t\"evaporator_heat\":null,\n" +
                "\t\"evaporator_water_back_temperature\":null,\n" +
                "\t\"evaporator_water_leave_temperature\":null,\n" +
                "\t\"lubricate_press_diff\":0.0,\n" +
                "\t\"motor_current_percent\":0.0,\n" +
                "\t\"motor_work\":null,\n" +
                "\t\"oil_tank_press_high\":553.7,\n" +
                "\t\"oil_tank_press_low\":533.1,\n" +
                "\t\"run_time\":1712.0,\n" +
                "\t\"start_up_time\":216.0\n" +
                "}";
        try {
            objMapper.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));
            Assert.assertTrue(Utils.haveSamePropertyValues(HeatPumpKpiData.class, objMapper.readValue(expectJson, HeatPumpKpiData.class),result.getHeatPumpKpiData().get(0)));
        } catch (JsonProcessingException e) {
            Assert.fail("Failed to compare 2 json value", e);
        } catch (Exception e) {
            Assert.fail("Failed to compare 2 json value", e);
        }

        Response response3 = ApiServiceEndpoint.subscriptionsWithKPIByDeviceId(queryParameters2);

        Reporter.log("Response status is " + response3.getStatusCode());

        Reporter.log("Response Body is =>  " + response3.getBody().asString());

        ApiResponse rspBody3 = response3.getBody().as(ApiResponse.class);

        Assert.assertEquals("OK", rspBody3.getMessage());
        Assert.assertEquals(200, rspBody3.getCode());


        JsonPath jsonPathEvaluator3 = response3.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator3.get("data"));

        HashMap data3 = jsonPathEvaluator3.get("data");
        String replyTo3 = data3.get("replyTo").toString();
        Assert.assertEquals(replyTo, replyTo3);


        Reporter.log("Send request to subscriptionsByDeviceId api with multiple device id");

        HashMap<String, String> queryParameters4 = new HashMap<>();
        queryParameters4.put("request", deviceMap.get("1#制冷机")+","+deviceMap.get("3#制冷机"));
        Response response4 = ApiServiceEndpoint.subscriptionsWithKPIByDeviceId(queryParameters4);

        Reporter.log("Response status is " + response4.getStatusCode());

        Reporter.log("Response Body is =>  " + response4.getBody().asString());

        ApiResponse rspBody4 = response4.getBody().as(ApiResponse.class);

        Assert.assertEquals("OK", rspBody4.getMessage());
        Assert.assertEquals(200, rspBody4.getCode());


        JsonPath jsonPathEvaluator4 = response4.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator4.get("data"));

        HashMap data4 = jsonPathEvaluator4.get("data");
        String replyTo4 = data4.get("replyTo").toString();

//        mq.simulateKPIProduce();
//        String result4 =mq.simulateKPIConsume(replyTo4);
        RabbitMQ mq2 = new RabbitMQ();
        mq2.simulateKPIProduce();
        String result4 =mq2.simulateKPIConsume(replyTo4);
        try {
            result = objMapper.readValue(result4, SubscriptionKPIResult.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(result, "Failed to get message in 300s");

        Assert.assertEquals(2, result.getHeatPumpKpiData().size());

        String expectJson2 = "{\n" +
                "\t\"deviceName\":\"3#制冷机\",\n" +
                "\t\"updateTime\":\"2020-09-01 06:15:33\",\n" +
                "\t\"compressor_poly_efficiency\":null,\n" +
                "\t\"condensor_hot_outlet_pressure\":null,\n" +
                "\t\"condensor_hot_outlet_temperature\":null,\n" +
                "\t\"condensor_hot_sat_temperature\":null,\n" +
                "\t\"condensor_water_inlet_temperature\":null,\n" +
                "\t\"condensor_water_outlet_temperature\":null,\n" +
                "\t\"dt_condensor\":null,\n" +
                "\t\"dt_evaporator\":null,\n" +
                "\t\"evaporator_cold_inlet_pressure\":null,\n" +
                "\t\"evaporator_cold_inlet_temperature\":null,\n" +
                "\t\"evaporator_heat\":null,\n" +
                "\t\"evaporator_water_back_temperature\":null,\n" +
                "\t\"evaporator_water_leave_temperature\":null,\n" +
                "\t\"lubricate_press_diff\":0.0,\n" +
                "\t\"motor_current_percent\":0.0,\n" +
                "\t\"motor_work\":null,\n" +
                "\t\"oil_tank_press_high\":369.6,\n" +
                "\t\"oil_tank_press_low\":365.5,\n" +
                "\t\"run_time\":2036.0,\n" +
                "\t\"start_up_time\":282.0\n" +
                "}";
        try {
            objMapper.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));
            HeatPumpKpiData device1 = result.getHeatPumpKpiData().stream().filter(d -> "1#制冷机".equals(d.getDeviceName())).findAny().orElse(null);
            HeatPumpKpiData device3 = result.getHeatPumpKpiData().stream().filter(d -> "3#制冷机".equals(d.getDeviceName())).findAny().orElse(null);
            Reporter.log("Compare HeatPumpKpiData with expect for device 1");
            Assert.assertTrue(Utils.haveSamePropertyValues(HeatPumpKpiData.class, objMapper.readValue(expectJson, HeatPumpKpiData.class), device1));
            Reporter.log("Compare HeatPumpKpiData with expect for device 3");
            Assert.assertTrue(Utils.haveSamePropertyValues(HeatPumpKpiData.class, objMapper.readValue(expectJson2, HeatPumpKpiData.class), device3));
        } catch (JsonProcessingException e) {
            Assert.fail("Failed to compare 2 json value with 2 device", e);
        } catch (Exception e) {
            Assert.fail("Failed to compare 2 json value with 2 device", e);
        }
    }


//    @Test(priority = 0, description = "Test api service interface: Subscription kpi data by device id.")
//    @Severity(SeverityLevel.BLOCKER)
//    @Description("Send a request to SUT and verify if user can subscribe kpi data by device id.")
//    @Story("Subscribe kpi data by device id")
//    public void subscriptionsTest() {
//        RabbitMQ mq = new RabbitMQ();
//        mq.simulateKPIProduce();
//        String result2 =mq.simulateKPIConsume("2e1660898615bd6666b32e8dd6dda060");
//        ObjectMapper objMapper = new ObjectMapper();
//        SubscriptionKPIResult result = null;
//        try {
//            result = objMapper.readValue(result2, SubscriptionKPIResult.class);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        Assert.assertNotNull(result, "Failed to get message in 60s");
//        Assert.assertEquals("Successfully", result.getMessage());
//        Assert.assertEquals(100000, result.getCode());
//        Assert.assertEquals(1, result.getData().get("HeatPumpKpiData").size());
//
////        mq.simulateSp5Produce();
////        String result2 =mq.simulateSp5Consume("721b417b7731b5476c05202de8a9b346");
////        ObjectMapper objMapper = new ObjectMapper();
////        SubscriptionSp5Result result = null;
////        try {
////            result = objMapper.readValue(result2, SubscriptionSp5Result.class);
////        } catch (JsonProcessingException e) {
////            e.printStackTrace();
////        }
////        Assert.assertNotNull(result, "Failed to get message in 60s");
////        Assert.assertEquals("Successfully", result.getMessage());
////        Assert.assertEquals(100000, result.getCode());
////        Assert.assertEquals(20, result.getData().get("SensorData").size());
//        System.out.println("finish");
//    }



}
