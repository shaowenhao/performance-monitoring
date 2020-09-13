package com.siemens.datalayer.apiservice.test;

import com.siemens.datalayer.apiservice.model.ApiResponse;
import com.siemens.datalayer.utils.Utils;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.Reporter;

import java.util.ArrayList;
import java.util.HashMap;

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
}
