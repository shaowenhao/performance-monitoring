package com.siemens.datalayer.apiservice.test;

import com.siemens.datalayer.apiservice.model.ApiResponse;
import com.siemens.datalayer.apiservice.model.BadRequestResponse;
import com.siemens.datalayer.utils.Utils;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.ObjectUtils;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.*;

@Epic("Api Service Interface")
@Feature("Rest API")
public class ApiServiceInterfaceTests {

    @Parameters({"baseUrl", "port"})
    @BeforeClass
    public void setApiserviceEndpoint(@Optional("http://140.231.89.85") String baseUrl, @Optional("9090") String port) {
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

        BadRequestResponse rspBody2 = response2.getBody().as(BadRequestResponse.class);

        Assert.assertEquals("Bad Request", rspBody2.getError());
        Assert.assertEquals(400, rspBody2.getStatus());


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

        BadRequestResponse rspBody = response.getBody().as(BadRequestResponse.class);

        Assert.assertEquals("Bad Request", rspBody.getError());
        Assert.assertEquals(400, rspBody.getStatus());


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

        BadRequestResponse rspBody = response.getBody().as(BadRequestResponse.class);

        Assert.assertEquals("Bad Request", rspBody.getError());
        Assert.assertEquals(400, rspBody.getStatus());


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

        Assert.assertEquals("Sensor not exist", rspBody.getMessage());
        Assert.assertEquals(102102, rspBody.getCode());
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

        BadRequestResponse rspBody = response.getBody().as(BadRequestResponse.class);

        Assert.assertEquals("Bad Request", rspBody.getError());
        Assert.assertEquals(400, rspBody.getStatus());


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

        BadRequestResponse rspBody = response.getBody().as(BadRequestResponse.class);

        Assert.assertEquals("Bad Request", rspBody.getError());
        Assert.assertEquals(400, rspBody.getStatus());


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

        Assert.assertTrue(Utils.isNullOrEmpty((Collection)jsonPathEvaluator.get("data")));

    }


    @Test(priority = 0, description = "Test api service interface: Get sensor data by device id.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if all sensor data belong given device id returned.")
    @Story("Get sensor data by device id")
    public void getSensorDataByDeviceId() {
        Reporter.log("Send request to getDeviceByType api with heatPumpDetail type");

        HashMap<String, String> queryParameters = new HashMap<>();
        queryParameters.put("device_type", "heatPumpDetail");

        Response response = ApiServiceEndpoint.getDevicesByType(queryParameters);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody = response.getBody().as(ApiResponse.class);

        Assert.assertEquals("OK", rspBody.getMessage());
        Assert.assertEquals(200, rspBody.getCode());


        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));

        ArrayList<HashMap> data = jsonPathEvaluator.get("data");
        Assert.assertEquals(data.size(), 6);

        HashMap h = data.stream().filter(d -> "1#制冷机".equals(d.get("deviceName"))).findAny().orElse(null);
        Assert.assertFalse(Utils.isNullOrEmpty(h));


        Reporter.log("Send request to getSensorDataByDeviceId api with device id");

        String q = String.format("{\n" +
                "  \"endTime\": 2594366300000,\n" +
                "  \"deviceId\": %s,\n" +
                "\"startTime\": 1594366100000\n" +
                "}", h.get("id"));

        Response response2 = ApiServiceEndpoint.getSensorDataByDeviceId(q);

        Reporter.log("Response status is " + response2.getStatusCode());

        Reporter.log("Response Body is =>  " + response2.getBody().asString());

        ApiResponse rspBody2 = response2.getBody().as(ApiResponse.class);

        Assert.assertEquals("OK", rspBody2.getMessage());
        Assert.assertEquals(200, rspBody2.getCode());

        JsonPath jsonPathEvaluator2 = response2.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator2.get("data"));

        ArrayList<HashMap> data2 = jsonPathEvaluator2.get("data");
        Assert.assertEquals(data2.size(), 3712);

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
        Assert.assertEquals(data.size(), 3720);

    }
}
