package com.siemens.datalayer.iems.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.testng.annotations.DataProvider;

public class SensorDataPro {

	@DataProvider(name = "dataForGetSensorDataBySensorId")
	Iterator<Object[]> dataForGetSensorDataBySensorId() {
		Collection<Object[]> queryParamCollection = new ArrayList<Object[]>();
		List<Map<String, Object>> listOfQueryParams = new ArrayList<>();
		Map<String, Object> goodQuery01 = new HashMap<>();
		String good1 = String.format("{\r\n" + 
				"  \"endTime\": 1694666300000,\r\n" + 
				"  \"sensor_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"siid\": 35698\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"siid\": 34159\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"siid\": 34155\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"siid\": 34133\r\n" + 
				"    }\r\n" + 
				"  ],\r\n" + 
				"  \"startTime\": 1594366100000\r\n" + 
				"}");
		goodQuery01.put("description", "good request, data retrieved");
		goodQuery01.put("body", good1);
		listOfQueryParams.add(goodQuery01);

		Map<String, Object> badQuery01 = new HashMap<>();
		String bad1 = String.format("{\r\n" + 
				"  \"endTime\": 1694666300000,\r\n" + 
				"  \"sensor_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"siid\": 34133\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}");
		badQuery01.put("description", "bad request, startTime is null");
		badQuery01.put("body", bad1);
		badQuery01.put("expectCode", ResponseCode.SDL_PARAM_ERROR.getCode());
		badQuery01.put("expectMessage", ResponseCode.SDL_PARAM_ERROR.getMessage());
		listOfQueryParams.add(badQuery01);
		
		Map<String, Object> badQuery02 = new HashMap<>();
		String bad2 = String.format("{\r\n" + 
				"  \"startTime\": 1694666300000,\r\n" + 
				"  \"sensor_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"siid\": 34133\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}");
		badQuery02.put("description", "bad request, endTime is null");
		badQuery02.put("body", bad2);
		badQuery02.put("expectCode", ResponseCode.SDL_PARAM_ERROR.getCode());
		badQuery02.put("expectMessage", ResponseCode.SDL_PARAM_ERROR.getMessage());
		listOfQueryParams.add(badQuery02);
		
		Map<String, Object> badQuery03 = new HashMap<>();
		String bad3 = String.format("{\r\n" + 
				"  \"endTime\": 1694666300000,\r\n" + 
				"  \"sensor_list\": [\r\n" + 
				"  ],\r\n" + 
				"  \"startTime\": 1594366100000\r\n" + 
				"}");
		badQuery03.put("description", "bad request, sensor_list is null");
		badQuery03.put("body", bad3);
		badQuery03.put("expectCode", ResponseCode.SDL_PARAM_ERROR.getCode());
		badQuery03.put("expectMessage", ResponseCode.SDL_PARAM_ERROR.getMessage());
		listOfQueryParams.add(badQuery03);


		for (Map<String, Object> map : listOfQueryParams) {
			if (map.get("description").toString().contains("good request")) {
				map.put("expectCode", ResponseCode.SDL_SUCCESS.getCode());
				map.put("expectMessage", ResponseCode.SDL_SUCCESS.getMessage());
			}

			queryParamCollection.add(new Object[] { map });
		}

		return queryParamCollection.iterator();
	}
	
	
	
	
	@DataProvider(name = "dataForGetSensorDataByDeviceId")
	Iterator<Object[]> dataForGetSensorDataByDeviceId() {
		Collection<Object[]> queryParamCollection = new ArrayList<Object[]>();
		List<Map<String, Object>> listOfQueryParams = new ArrayList<>();
		Map<String, Object> goodQuery01 = new HashMap<>();
		String good1 = String.format("{\r\n" + 
				"  \"deviceId\": 1744,\r\n" + 
				"  \"endTime\": 1601519153000,\r\n" + 
				"  \"startTime\": 1593570353000\r\n" + 
				"}");
		goodQuery01.put("description", "good request, data retrieved");
		goodQuery01.put("body", good1);
		listOfQueryParams.add(goodQuery01);

		Map<String, Object> badQuery01 = new HashMap<>();
		String bad1 = String.format("{\r\n" + 
				"  \"deviceId\": 1744,\r\n" + 
				"  \"startTime\": 1593570353000\r\n" + 
				"}");
		badQuery01.put("description", "bad request, endTime is null");
		badQuery01.put("body", bad1);
		badQuery01.put("expectCode", ResponseCode.SDL_PARAM_ERROR.getCode());
		badQuery01.put("expectMessage", ResponseCode.SDL_PARAM_ERROR.getMessage());
		listOfQueryParams.add(badQuery01);
		
		Map<String, Object> badQuery02 = new HashMap<>();
		String bad2 = String.format("{\r\n" + 
				"  \"deviceId\": 1744,\r\n" + 
				"  \"endTime\": 1601519153000\r\n" + 
				"}");
		badQuery02.put("description", "bad request, startTime is null");
		badQuery02.put("body", bad2);
		badQuery02.put("expectCode", ResponseCode.SDL_PARAM_ERROR.getCode());
		badQuery02.put("expectMessage", ResponseCode.SDL_PARAM_ERROR.getMessage());
		listOfQueryParams.add(badQuery02);
		
		Map<String, Object> badQuery03 = new HashMap<>();
		String bad3 = String.format("{\r\n" + 
				"  \"endTime\": 1601519153000,\r\n" + 
				"  \"startTime\": 1593570353000\r\n" + 
				"}");
		badQuery03.put("description", "bad request, deviceId is null");
		badQuery03.put("body", bad3);
		badQuery03.put("expectCode", ResponseCode.SDL_PARAM_ERROR.getCode());
		badQuery03.put("expectMessage", ResponseCode.SDL_PARAM_ERROR.getMessage());
		listOfQueryParams.add(badQuery03);


		for (Map<String, Object> map : listOfQueryParams) {
			if (map.get("description").toString().contains("good request")) {
				map.put("expectCode", ResponseCode.SDL_SUCCESS.getCode());
				map.put("expectMessage", ResponseCode.SDL_SUCCESS.getMessage());
			}

			queryParamCollection.add(new Object[] { map });
		}

		return queryParamCollection.iterator();
	}
	
	
	@DataProvider(name = "getTopSensorDataByDeviceId")
	Iterator<Object[]> getTopSensorDataByDeviceId() {
		Collection<Object[]> queryParamCollection = new ArrayList<Object[]>();
		List<Map<String, Object>> listOfQueryParams = new ArrayList<>();
		Map<String, Object> goodQuery01 = new HashMap<>();
		String good1 = String.format("{\r\n" + 
				"  \"limit\": 2,\r\n" + 
				"  \"deviceId\": 1744\r\n" + 
				"}");
		goodQuery01.put("description", "good request, data retrieved");
		goodQuery01.put("body", good1);
		listOfQueryParams.add(goodQuery01);

		Map<String, Object> badQuery01 = new HashMap<>();
		String bad1 = String.format("{\r\n" + 
				"  \"deviceId\": 1744\r\n" + 
				"}");
		badQuery01.put("description", "bad request, limit is null");
		badQuery01.put("body", bad1);
		badQuery01.put("expectCode", ResponseCode.SDL_PARAM_ERROR.getCode());
		badQuery01.put("expectMessage", ResponseCode.SDL_PARAM_ERROR.getMessage());
		listOfQueryParams.add(badQuery01);
		
		Map<String, Object> badQuery02 = new HashMap<>();
		String bad2 = String.format("{\r\n" + 
				"  \"limit\": 2\r\n" + 
				"}");
		badQuery02.put("description", "bad request, deviceId is null");
		badQuery02.put("body", bad2);
		badQuery02.put("expectCode", ResponseCode.SDL_PARAM_ERROR.getCode());
		badQuery02.put("expectMessage", ResponseCode.SDL_PARAM_ERROR.getMessage());
		listOfQueryParams.add(badQuery02);
		
		Map<String, Object> badQuery03 = new HashMap<>();
		String bad3 = String.format("{\r\n" + 
				"  \"limit\": 1000,\r\n" + 
				"  \"deviceId\": 1744\r\n" + 
				"}");
		badQuery03.put("description", "bad request, limit not correct, should be number < 1000");
		badQuery03.put("body", bad3);
		badQuery03.put("expectCode", ResponseCode.SDL_PARAM_ERROR.getCode());
		badQuery03.put("expectMessage", ResponseCode.SDL_PARAM_ERROR.getMessage());
		listOfQueryParams.add(badQuery03);
		

		for (Map<String, Object> map : listOfQueryParams) {
			if (map.get("description").toString().contains("good request")) {
				map.put("expectCode", ResponseCode.SDL_SUCCESS.getCode());
				map.put("expectMessage", ResponseCode.SDL_SUCCESS.getMessage());
			}

			queryParamCollection.add(new Object[] { map });
		}

		return queryParamCollection.iterator();
	}
}
