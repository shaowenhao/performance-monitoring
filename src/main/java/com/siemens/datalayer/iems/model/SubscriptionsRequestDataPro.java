package com.siemens.datalayer.iems.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.testng.annotations.DataProvider;

import com.siemens.datalayer.iems.test.Endpoint;


public class SubscriptionsRequestDataPro {
	@DataProvider(name = "dataForSubscriptionBySensorId")
	Iterator<Object[]> dataForSubscriptionBySensorId() {
		Collection<Object[]> queryParamCollection = new ArrayList<Object[]>();
		List<Map<String, Object>> listOfQueryParams = new ArrayList<>();

		Map<String, Object> goodQuery01 = new HashMap<>();
		goodQuery01.put("description", "good request, data retrieved");
		goodQuery01.put("request", "34155,34159,34155");
		listOfQueryParams.add(goodQuery01);

		Map<String, Object> badQuery01 = new HashMap<>();
		badQuery01.put("description", "bad request, request for sensor id");
		badQuery01.put("expectCode", ResponseCode.SDL_PARAM_ERROR.getCode());
		badQuery01.put("expectMessage", ResponseCode.SDL_PARAM_ERROR.getMessage());
		listOfQueryParams.add(badQuery01);

		Map<String, Object> badQuery02 = new HashMap<>();
		badQuery02.put("description", "bad request, request is Integer");
		badQuery02.put("request", "aaa");
		badQuery02.put("expectCode", ResponseCode.SDL_PARAM_ERROR.getCode());
		badQuery02.put("expectMessage", ResponseCode.SDL_PARAM_ERROR.getMessage());
		listOfQueryParams.add(badQuery02);
		
		Map<String, Object> badQuery03 = new HashMap<>();
		badQuery03.put("description", "bad request, request need to be separated by commas");
		badQuery03.put("request", "34155;34159;34155");
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
	
	
	@DataProvider(name = "dataForSubscriptionsByDeviceId")
	Iterator<Object[]> dataForSubscriptionsByDeviceId() {
		HashMap<String, String> deviceMap = Endpoint.getDeviceIdByName(new ArrayList<String>(){{
            add("1#制冷机");
            add("3#制冷机");
        }});
		Collection<Object[]> queryParamCollection = new ArrayList<Object[]>();
		List<Map<String, Object>> listOfQueryParams = new ArrayList<>();

		Map<String, Object> goodQuery01 = new HashMap<>();
		goodQuery01.put("description", "good request, data retrieved");
		goodQuery01.put("deviceId", Integer.parseInt(deviceMap.get("1#制冷机")));
		listOfQueryParams.add(goodQuery01);

		Map<String, Object> badQuery01 = new HashMap<>();
		badQuery01.put("description", "bad request, Required Integer parameter 'deviceId' is not present");
		badQuery01.put("expectCode", ResponseCode.SDL_PARAM_ERROR.getCode());
		badQuery01.put("expectMessage", ResponseCode.SDL_PARAM_ERROR.getMessage());
		listOfQueryParams.add(badQuery01);

		Map<String, Object> badQuery02 = new HashMap<>();
		badQuery02.put("description", "bad request, deviceId not correct, should be number");
		badQuery02.put("deviceId", "aaa");
		badQuery02.put("expectCode", ResponseCode.SDL_PARAM_ERROR.getCode());
		badQuery02.put("expectMessage", ResponseCode.SDL_PARAM_ERROR.getMessage());
		listOfQueryParams.add(badQuery02);

		for (Map<String, Object> map : listOfQueryParams) {
			if (map.get("description").toString().contains("good request")) {
				map.put("expectCode", ResponseCode.SDL_SUCCESS.getCode());
				map.put("expectMessage", ResponseCode.SDL_SUCCESS.getMessage());
			}

			queryParamCollection.add(new Object[] { map });
		}

		return queryParamCollection.iterator();
	}
	
	
	@DataProvider(name = "dataForSubscriptionsWithKPIByDeviceId")
	Iterator<Object[]> dataForSubscriptionsWithKPIByDeviceId() {
		HashMap<String, String> deviceMap = Endpoint.getDeviceIdByName(new ArrayList<String>(){{
            add("1#制冷机");
            add("3#制冷机");
        }});
		Collection<Object[]> queryParamCollection = new ArrayList<Object[]>();
		List<Map<String, Object>> listOfQueryParams = new ArrayList<>();

		Map<String, Object> goodQuery01 = new HashMap<>();
		goodQuery01.put("description", "good request, data retrieved");
		goodQuery01.put("request", deviceMap.get("1#制冷机")+","+deviceMap.get("3#制冷机"));
		listOfQueryParams.add(goodQuery01);

		Map<String, Object> badQuery01 = new HashMap<>();
		badQuery01.put("description", "bad request, request for device id");
		badQuery01.put("expectCode", ResponseCode.SDL_PARAM_ERROR.getCode());
		badQuery01.put("expectMessage", ResponseCode.SDL_PARAM_ERROR.getMessage());
		listOfQueryParams.add(badQuery01);

		Map<String, Object> badQuery02 = new HashMap<>();
		badQuery02.put("description", "bad request, request is Integer");
		badQuery02.put("request", "aaa");
		badQuery02.put("expectCode", ResponseCode.SDL_PARAM_ERROR.getCode());
		badQuery02.put("expectMessage", ResponseCode.SDL_PARAM_ERROR.getMessage());
		listOfQueryParams.add(badQuery02);
		
		Map<String, Object> badQuery03 = new HashMap<>();
		badQuery03.put("description", "bad request, request need to be separated by commas");
		badQuery03.put("request", deviceMap.get("1#制冷机")+";"+deviceMap.get("3#制冷机"));
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
	
	
	
	@DataProvider(name = "dataForDeleteSubscriptions")
	Iterator<Object[]> dataForDeleteSubscriptions() {
		Collection<Object[]> queryParamCollection = new ArrayList<Object[]>();
		List<Map<String, Object>> listOfQueryParams = new ArrayList<>();

//		Map<String, Object> goodQuery01 = new HashMap<>();
//		goodQuery01.put("description", "good request, data retrieved");
//		goodQuery01.put("id", "5adce67b8f41888700812fee58d6cf8e");
//		listOfQueryParams.add(goodQuery01);

		Map<String, Object> badQuery01 = new HashMap<>();
		badQuery01.put("description", "bad request, request for id");
		badQuery01.put("expectCode", ResponseCode.SDL_PARAM_ERROR.getCode());
		badQuery01.put("expectMessage", ResponseCode.SDL_PARAM_ERROR.getMessage());
		listOfQueryParams.add(badQuery01);

		Map<String, Object> badQuery02 = new HashMap<>();
		badQuery02.put("description", "bad request, Subscriptions not exist");
		badQuery02.put("id", "115adce67b8f41888700812fee58d6cf8e");
		badQuery02.put("expectCode", ResponseCode.SDL_PARAM_ERROR.getCode());
		badQuery02.put("expectMessage", ResponseCode.SDL_PARAM_ERROR.getMessage());
		listOfQueryParams.add(badQuery02);
		
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
