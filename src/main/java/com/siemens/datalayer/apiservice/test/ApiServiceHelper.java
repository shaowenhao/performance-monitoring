package com.siemens.datalayer.apiservice.test;

import com.siemens.datalayer.apiservice.model.ApiResponse;
import com.siemens.datalayer.utils.Utils;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.Reporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ApiServiceHelper {
    public static HashMap<String, String> getDeviceIdByName(ArrayList<String> deviceNames){
        HashMap<String, String> results = new HashMap<>();
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

        for (String name: deviceNames) {
            HashMap h = data.stream().filter(d -> name.equals(d.get("deviceName"))).findAny().orElse(null);
            Assert.assertFalse(Utils.isNullOrEmpty(h));
            results.put(name, h.get("id").toString());
        }

        return results;
    }

    public static ArrayList<String> getSensorByDeviceId(String deviceId){
        HashMap<String, String> results = new HashMap<>();
        Reporter.log("Send request to getSensorByDeviceId api with id");

        HashMap<String, String> q = new HashMap<>();
        q.put("id", deviceId);

        Response response = ApiServiceEndpoint.getSensorByDeviceId(q);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        ApiResponse rspBody2 = response.getBody().as(ApiResponse.class);

        Assert.assertEquals("OK", rspBody2.getMessage());
        Assert.assertEquals(200, rspBody2.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));

        ArrayList<HashMap> data = jsonPathEvaluator.get("data");

        List<String> list = data.stream().filter(d -> !StringUtils.isEmpty(d.get("siid").toString())).map(d -> d.get("siid").toString()).collect(Collectors.toList());
        return new ArrayList<>(list);
    }
}
