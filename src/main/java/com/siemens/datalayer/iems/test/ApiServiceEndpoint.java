package com.siemens.datalayer.iems.test;

import java.util.ArrayList;
import java.util.HashMap;

import org.testng.Assert;

import com.siemens.datalayer.apiservice.model.ApiResponse;
import com.siemens.datalayer.iems.model.RestConstants;
import com.siemens.datalayer.utils.Utils;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ApiServiceEndpoint {
	
	private static String BASE_URL = "";	
	private static String PORT = "";	
	private static String PRE_ASSET = "";	
	private static String PRE_DATA = "";
	
	public static void setBaseUrl(String base_url)
	{
		BASE_URL = base_url;
	}
	
	public static void setPort(String comm_port)
	{
		PORT = comm_port;
	}
	
	public static void setPreAsset(String pre_asset)
	{
		PRE_ASSET = pre_asset;
	}
	
	public static void setPreData(String pre_data)
	{
		PRE_DATA = pre_data;
	}
	
	private static Response getResponseByParameters(String api, HashMap<String, Object> parameters) 
	{
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(PORT).intValue();
		RestAssured.basePath = api;
		
        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");
        
        Response response = null;
        if(parameters == null) 
        {
        	response = httpRequest.filter(new AllureRestAssured())
        						  .get();
        }
        else 
        {
        	response = httpRequest.queryParams(parameters)
								  .filter(new AllureRestAssured())
								  .get();
        }
        
        return response;
	}
	
	private static Response postResponseByBody(String api, String body) 
	{
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(PORT).intValue();
		RestAssured.basePath = api;
		
        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");
        
        Response response = httpRequest.body(body)
									   .filter(new AllureRestAssured())
									   .post();
        
        return response;
	}
	
	private static Response postResponseByParameters(String api, HashMap<String, Object> parameters) 
	{
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(PORT).intValue();
		RestAssured.basePath = api;
		
        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");
        
        Response response = httpRequest.queryParams(parameters)
									   .filter(new AllureRestAssured())
									   .post();
        
        return response;
	}
	
	public static Response deleteResponseById(String api) {
		RestAssured.baseURI = BASE_URL;
		RestAssured.port = Integer.valueOf(PORT).intValue();
		RestAssured.basePath = api;

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest
        		.filter(new AllureRestAssured())
                .delete();

        return response;
    }
	
//	Asset Data
//	@Step("Device type: {parameters}")
	public static Response getDevicesByType(HashMap<String, Object> parameters) 
	{
        return getResponseByParameters(PRE_ASSET + RestConstants.GETDEVICESBYTYPE, parameters);
    }
	
	@Step("Send a request of 'Get all Device type'")
	public static Response listDeviceTypes() 
	{
		return getResponseByParameters(PRE_ASSET + RestConstants.LISTDEVICETYPES, null);
	}
	
	@Step("Send a request of 'Get all Device type contain id and type'")
	public static Response listAllDeviceTypes() 
	{
		return getResponseByParameters(PRE_ASSET + RestConstants.LISTALLDEVICETYPES, null);
	}
	
	@Step("Send a request of 'getDeviceInfo'")
	public static Response getDeviceInfo(HashMap<String, Object> parameters) 
	{
		return getResponseByParameters(PRE_ASSET + RestConstants.GETDEVICEINFO, parameters);
	}
	
	@Step("id: {parameters}")
	public static Response getSensorByDeviceId(HashMap<String, Object> parameters) 
	{
		return getResponseByParameters(PRE_ASSET + RestConstants.GETSENSORBYDEVICEID, parameters);
	}
	
	
//	Sensor Data
	@Step("Send a request of 'getSensorDataBySensorId'")
	public static Response getSensorDataBySensorId(String body) 
	{
		return postResponseByBody(PRE_DATA + RestConstants.GETSENSORDATABYSENSORID, body);
	}
	
	@Step("Send a request of 'getSensorDataByDeviceId'")
	public static Response getSensorDataByDeviceId(String body) 
	{
		return postResponseByBody(PRE_DATA + RestConstants.GETSENSORDATABYDEVICEID, body);
	}
	
	@Step("Send a request of 'getTopSensorDataByDeviceId'")
	public static Response getTopSensorDataByDeviceId(String body) 
	{
		return postResponseByBody(PRE_DATA + RestConstants.GETTOPSENSORDATABYDEVICEID, body);
	}
	
//	Kpi Data
	@Step("Send a request of 'getKpiDataByDeviceId'")
	public static Response getKpiDataByDeviceId(String body) 
	{
		return postResponseByBody(PRE_DATA + RestConstants.GETKPIDATABYDEVICEID, body);
	}
	
	@Step("Send a request of 'getTopKPIDataByDeviceId'")
	public static Response getTopKPIDataByDeviceId(String body) 
	{
		return postResponseByBody(PRE_DATA + RestConstants.GETTOPKPIDATABYDEVICEID, body);
	}
	
//	Subscription Data
	@Step("request: {parameters}")
	public static Response subscriptionsBySensorId(HashMap<String, Object> parameters) 
	{
		return postResponseByParameters(PRE_DATA + RestConstants.SUBSCRIPTIONSBYSENSORID, parameters);
	}
	
	@Step("request: {parameters}")
	public static Response subscriptionsByDeviceId(HashMap<String, Object> parameters) 
	{
		return postResponseByParameters(PRE_DATA + RestConstants.SUBSCRIPTIONBYDEVICEID, parameters);
	}
	
	@Step("request: {parameters}")
	public static Response subscriptionsWithKPIByDeviceId(HashMap<String, Object> parameters) 
	{
		return postResponseByParameters(PRE_DATA + RestConstants.SUBSCRIPTIONSWITHKPIBYDEVICEID, parameters);
	}
	
	@Step("id: {id}")
	public static Response deleteSubscriptions(String id) 
	{
		return deleteResponseById(PRE_DATA + RestConstants.DELETESUBSCRIPTIONS + "/" +id);
	}
	
	public static HashMap<String, String> getDeviceIdByName(ArrayList<String> deviceNames)
	{
        HashMap<String, String> results = new HashMap<>();
        HashMap<String, Object> queryParameters = new HashMap<>();
        queryParameters.put("device_type", "heatPumpDetail");

        Response response = getDevicesByType(queryParameters);
        ApiResponse rspBody = response.getBody().as(ApiResponse.class);
        Assert.assertEquals(200, rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();
        Assert.assertNotNull(jsonPathEvaluator.get("data"));
        
        ArrayList<HashMap<Object, Object>> data = jsonPathEvaluator.get("data");
        for (String name: deviceNames) 
        {
            HashMap<Object, Object> h = data.stream().filter(d -> name.equals(d.get("deviceName"))).findAny().orElse(null);
            Assert.assertFalse(Utils.isNullOrEmpty(h));
            results.put(name, h.get("id").toString());
        }

        return results;
    }

}
