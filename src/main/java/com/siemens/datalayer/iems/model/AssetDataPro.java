package com.siemens.datalayer.iems.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.testng.annotations.DataProvider;

import com.siemens.datalayer.apiservice.test.ApiServiceHelper;
import com.siemens.datalayer.iems.test.Endpoint;

public class AssetDataPro {

	@DataProvider(name = "dataForGetDevicesByType")
	Iterator<Object[]> dataForGetDevicesByType() {

		Collection<Object[]> queryParamCollection = new ArrayList<Object[]>();

		List<Map<String, String>> listOfQueryParams = new ArrayList<Map<String, String>>();

		Map<String, String> goodQuery01 = new HashMap<String, String>();
		goodQuery01.put("description", "good request, data retrieved");
		goodQuery01.put("deviceType", "heatPumpDetail");
		listOfQueryParams.add(goodQuery01);

		Map<String, String> badQuery01 = new HashMap<String, String>();
		badQuery01.put("description", "bad request, invalid type");
		badQuery01.put("deviceType", "invalid");
		badQuery01.put("expectCode", ResponseCode.SDL_KG_SVC_DEVICE_NOT_EXIST.getCode());
		badQuery01.put("expectMessage", ResponseCode.SDL_KG_SVC_DEVICE_NOT_EXIST.getMessage());
		listOfQueryParams.add(badQuery01);

		Map<String, String> badQuery02 = new HashMap<String, String>();
		badQuery02.put("description", "bad request, no device type");
		badQuery02.put("expectCode", ResponseCode.SDL_KG_SVC_DEVICE_NOT_EXIST.getCode());
		badQuery02.put("expectMessage", ResponseCode.SDL_KG_SVC_DEVICE_NOT_EXIST.getMessage());
		listOfQueryParams.add(badQuery02);

		for (Map<String, String> map : listOfQueryParams) {
			if (map.get("description").contains("good request")) {
				map.put("expectCode", ResponseCode.SDL_SUCCESS.getCode());
				map.put("expectMessage", ResponseCode.SDL_SUCCESS.getMessage());
			}

			queryParamCollection.add(new Object[] { map });
		}

		return queryParamCollection.iterator();
	}

	@DataProvider(name = "dataForDeviceId")
	Iterator<Object[]> dataForDeviceId() {

		Collection<Object[]> queryParamCollection = new ArrayList<Object[]>();

		List<Map<String, Object>> listOfQueryParams = new ArrayList<>();

		Map<String, Object> goodQuery01 = new HashMap<>();
		goodQuery01.put("description", "good request, data retrieved");
//		HashMap<String, String> deviceMap = Endpoint.getDeviceIdByName(new ArrayList<String>(){{
//            add("1#制冷机");
//            add("3#制冷机");
//        }});
		goodQuery01.put("deviceName", "1#制冷机");
		listOfQueryParams.add(goodQuery01);

		Map<String, Object> badQuery01 = new HashMap<>();
		badQuery01.put("description", "bad request, no id");
		badQuery01.put("expectCode", ResponseCode.SDL_PARAM_ERROR.getCode());
		badQuery01.put("expectMessage", ResponseCode.SDL_PARAM_ERROR.getMessage());
		listOfQueryParams.add(badQuery01);

		Map<String, Object> badQuery02 = new HashMap<>();
		badQuery02.put("description", "bad request, id not correct, should be number");
		badQuery02.put("id", "aaa");
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
