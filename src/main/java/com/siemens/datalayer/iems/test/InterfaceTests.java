package com.siemens.datalayer.iems.test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.*;
import io.qameta.allure.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.siemens.datalayer.iems.model.SensorDataPro;
import com.siemens.datalayer.iems.model.SubscriptionsRequestDataPro;
import com.siemens.datalayer.iems.model.AssetDataPro;
import com.siemens.datalayer.iems.model.ResponseCode;
import com.siemens.datalayer.iems.model.RestConstants;

import org.testng.Assert;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

@Epic("iEMS Interface")
public class InterfaceTests {

	@Parameters({ "base_url", "port", "pre_asset", "pre_data" })
	@BeforeClass(description = "Configure host address and communication port for Connector service")
	public void setConnectorEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("30684") String port,
			@Optional("/datalayer/api/v1/asset/") String pre_asset, @Optional("/datalayer/api/v1/data/") String pre_data) {
		Endpoint.setBaseUrl(base_url);
		Endpoint.setPort(port);
		Endpoint.setPreAsset(pre_asset);
		Endpoint.setPreData(pre_data);
//	    AllureEnvironmentPropertiesWriter.addEnvironmentItem("Connector Address", base_url + ":" + port);
	}

	@Test(priority = 0, description = RestConstants.LISTDEVICETYPES)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT and verify if all the available device type can be read out.")
	@Feature("Get asset data API")
	@Story("Get All device type")
	public void listDeviceTypes() {
		Response response = Endpoint.listDeviceTypes();
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.jsonPath().getList("data").size() > 0);
		assertThat(response.getBody().asString(),
				matchesJsonSchemaInClasspath("iems/" + RestConstants.LISTDEVICETYPES + ".JSON"));
	}

	@Test(priority = 0, description = RestConstants.LISTALLDEVICETYPES)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT and verify if all the available device id and type can be read out.")
	@Feature("Get asset data API")
	@Story("Get All device type contain id and type")
	public void listAllDeviceTypes() {
		Response response = Endpoint.listAllDeviceTypes();
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.jsonPath().getList("data").size() > 0);
		assertThat(response.getBody().asString(),
				matchesJsonSchemaInClasspath("iems/" + RestConstants.LISTALLDEVICETYPES + ".JSON"));
	}

	@Test(priority = 0, description = RestConstants.GETDEVICESBYTYPE, dataProviderClass = AssetDataPro.class, dataProvider = "dataForGetDevicesByType")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT with specified parameters and check the response message.")
	@Feature("Get asset data API")
	@Story("Get devices by type")
	public void getDevicesByType(Map<String, String> paramMaps) {
		HashMap<String, Object> queryParameters = new HashMap<>();

		if (paramMaps.containsKey("deviceType"))
			queryParameters.put("device_type", paramMaps.get("deviceType"));

		Response response = Endpoint.getDevicesByType(queryParameters);

		Assert.assertEquals(response.getStatusCode(), 200);

		if (paramMaps.containsKey("expectCode"))
			Assert.assertEquals(response.jsonPath().getString("code"), paramMaps.get("expectCode"));

		if (paramMaps.containsKey("expectMessage"))
			Assert.assertTrue(response.jsonPath().getString("message").contains(paramMaps.get("expectMessage")));

		if (paramMaps.get("description").contains("good request")) {
			if (paramMaps.get("description").contains("data not found"))
				Assert.assertTrue(response.jsonPath().getList("data").isEmpty());

			if (paramMaps.get("description").contains("data retrieved")) {
				Assert.assertTrue(response.jsonPath().getList("data").size() > 0);
				assertThat(response.getBody().asString(),
						matchesJsonSchemaInClasspath("iems/" + RestConstants.LISTALLDEVICETYPES + ".JSON"));
			}
		} else {
			Assert.assertNull(response.jsonPath().getList("data"));
		}

	}

	@Test(priority = 0, description = RestConstants.GETDEVICEINFO, dataProviderClass = AssetDataPro.class, dataProvider = "dataForDeviceId")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT with specified parameters and check the response message.")
	@Feature("Get asset data API")
	@Story("Get device infomation")
	public void getDeviceInfo(Map<String, String> paramMaps) {
		HashMap<String, Object> queryParameters = new HashMap<>();
		if (paramMaps.containsKey("id"))
			queryParameters.put("id", paramMaps.get("id"));
		Response response = Endpoint.getDeviceInfo(queryParameters);
		if (paramMaps.get("description").contains("good request")) {
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertNotNull(response.jsonPath().getString("data"));
			assertThat(response.getBody().asString(),
					matchesJsonSchemaInClasspath("iems/" + RestConstants.GETDEVICEINFO + ".JSON"));
		} else {
			Assert.assertNull(response.jsonPath().getString("data"));
		}
	}
	
	@Test(priority = 0, description = RestConstants.GETSENSORBYDEVICEID, dataProviderClass = AssetDataPro.class, dataProvider = "dataForDeviceId")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT with specified parameters and check the response message.")
	@Feature("Get asset data API")
	@Story("Get sensors of device")
	public void getSensorByDeviceId(Map<String, String> paramMaps) {
		HashMap<String, Object> queryParameters = new HashMap<>();
		if (paramMaps.containsKey("id"))
			queryParameters.put("id", paramMaps.get("id"));
		Response response = Endpoint.getSensorByDeviceId(queryParameters);
		if (paramMaps.get("description").contains("good request")) {
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertTrue(response.jsonPath().getList("data").size() > 0);
		} else {
			Assert.assertNull(response.jsonPath().getList("data"));
		}
	}
	
//	Get Sensor Data API
	@Test(priority = 0, description = RestConstants.GETSENSORDATABYSENSORID, dataProviderClass = SensorDataPro.class, dataProvider = "dataForGetSensorDataBySensorId")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT with json and check the response message.")
	@Feature("Get sensor data API")
	@Story("Get sensor data by sensor ids")
	public void getSensorDataBySensorId(Map<String, Object> paramMaps) {
		Response response = Endpoint.getSensorDataBySensorId( paramMaps.get("body").toString());
		if (paramMaps.get("description").toString().contains("good request")) {
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertTrue(response.jsonPath().getList("data").size() > 0);
			assertThat(response.getBody().asString(),
					matchesJsonSchemaInClasspath("iems/" + RestConstants.SENSORDATA + ".JSON"));
		} else {
			Assert.assertNull(response.jsonPath().getList("data"));
		}
	}
	
	@Test(priority = 0, description = RestConstants.GETSENSORDATABYDEVICEID, dataProviderClass = SensorDataPro.class, dataProvider = "dataForGetSensorDataByDeviceId")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT with json and check the response message.")
	@Feature("Get sensor data API")
	@Story("Get sensor data by device id")
	public void getSensorDataByDeviceId(Map<String, Object> paramMaps) {
		Response response = Endpoint.getSensorDataByDeviceId( paramMaps.get("body").toString());
		if (paramMaps.get("description").toString().contains("good request")) {
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertTrue(response.jsonPath().getList("data").size() > 0);
			assertThat(response.getBody().asString(),
					matchesJsonSchemaInClasspath("iems/" + RestConstants.SENSORDATA + ".JSON"));
		} else {
			Assert.assertNull(response.jsonPath().getList("data"));
		}
	}
	
	@Test(priority = 0, description = RestConstants.GETTOPSENSORDATABYDEVICEID, dataProviderClass = SensorDataPro.class, dataProvider = "getTopSensorDataByDeviceId")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT with json and check the response message.")
	@Feature("Get sensor data API")
	@Story("Get top n sensor data by device id")
	public void getTopSensorDataByDeviceId(Map<String, Object> paramMaps) {
		Response response = Endpoint.getTopSensorDataByDeviceId( paramMaps.get("body").toString());
		if (paramMaps.get("description").toString().contains("good request")) {
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertTrue(response.jsonPath().getList("data").size() > 0);
			assertThat(response.getBody().asString(),
					matchesJsonSchemaInClasspath("iems/" + RestConstants.SENSORDATA + ".JSON"));
		} else {
			Assert.assertNull(response.jsonPath().getList("data"));
		}
	}
	
	
	@Test(priority = 0, description = RestConstants.GETKPIDATABYDEVICEID, dataProviderClass = SensorDataPro.class, dataProvider = "dataForGetSensorDataByDeviceId")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT with json and check the response message.")
	@Feature("Get kpi data API")
	@Story("Get kpi data by device id")
	public void getKpiDataByDeviceId(Map<String, Object> paramMaps) {
		Response response = Endpoint.getKpiDataByDeviceId( paramMaps.get("body").toString());
		if (paramMaps.get("description").toString().contains("good request")) {
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertTrue(response.jsonPath().getList("data").size() > 0);
			assertThat(response.getBody().asString(),
					matchesJsonSchemaInClasspath("iems/" + RestConstants.KPIDATA + ".JSON"));
		} else {
			Assert.assertNull(response.jsonPath().getList("data"));
		}
	}
	
	@Test(priority = 0, description = RestConstants.GETTOPKPIDATABYDEVICEID, dataProviderClass = SensorDataPro.class, dataProvider = "getTopSensorDataByDeviceId")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT with json and check the response message.")
	@Feature("Get kpi data API")
	@Story("Get top n kpi data by device id")
	public void getTopKPIDataByDeviceId(Map<String, Object> paramMaps) {
		Response response = Endpoint.getTopKPIDataByDeviceId( paramMaps.get("body").toString());
		if (paramMaps.get("description").toString().contains("good request")) {
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertTrue(response.jsonPath().getList("data").size() > 0);
			assertThat(response.getBody().asString(),
					matchesJsonSchemaInClasspath("iems/" + RestConstants.KPIDATA + ".JSON"));
		} else {
			Assert.assertNull(response.jsonPath().getList("data"));
		}
	}
	
//	Subscription Data
	@Test(priority = 0, description = RestConstants.SUBSCRIPTIONSBYSENSORID, dataProviderClass = SubscriptionsRequestDataPro.class, dataProvider = "dataForSubscriptionBySensorId")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT with sensor ids and subscription the response message.")
	@Feature("Get subscription data API")
	@Story("Get subscription sensor data by sensor ids")
	public void subscriptionsBySensorId(Map<String, String> paramMaps) {
		HashMap<String, Object> queryParameters = new HashMap<>();
		if (paramMaps.containsKey("request"))
			queryParameters.put("request", paramMaps.get("request").toString());
		Response response = Endpoint.subscriptionsBySensorId(queryParameters);
		if (paramMaps.get("description").contains("good request")) {
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertNotNull(response.jsonPath().getString("data"));
			assertThat(response.getBody().asString(),
					matchesJsonSchemaInClasspath("iems/" + RestConstants.SUBSCRIPTIONDATA + ".JSON"));
		} else {
			Assert.assertNull(response.jsonPath().getString("data"));
		}
	}
	
	@Test(priority = 0, description = RestConstants.SUBSCRIPTIONBYDEVICEID, dataProviderClass = SubscriptionsRequestDataPro.class, dataProvider = "dataForSubscriptionsByDeviceId")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT with device id and subscription the response message.")
	@Feature("Get subscription data API")
	@Story("Get subscription sensor data by device id")
	public void subscriptionsByDeviceId(Map<String, String> paramMaps) {
		HashMap<String, Object> queryParameters = new HashMap<>();
		if (paramMaps.containsKey("deviceId"))
			queryParameters.put("deviceId", paramMaps.get("deviceId"));
		Response response = Endpoint.subscriptionsByDeviceId(queryParameters);
		if (paramMaps.get("description").contains("good request")) {
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertNotNull(response.jsonPath().getString("data"));
			assertThat(response.getBody().asString(),
					matchesJsonSchemaInClasspath("iems/" + RestConstants.SUBSCRIPTIONDATA + ".JSON"));
		} else {
			Assert.assertNull(response.jsonPath().getString("data"));
		}
	}
	
	@Test(priority = 0, description = RestConstants.SUBSCRIPTIONSWITHKPIBYDEVICEID, dataProviderClass = SubscriptionsRequestDataPro.class, dataProvider = "dataForSubscriptionsWithKPIByDeviceId")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT with device ids and subscription the response message.")
	@Feature("Get subscription data API")
	@Story("Get subscription kpi data by device ids")
	public void subscriptionsWithKPIByDeviceId(Map<String, String> paramMaps) {
		HashMap<String, Object> queryParameters = new HashMap<>();
		if (paramMaps.containsKey("request"))
			queryParameters.put("request", paramMaps.get("request").toString());
		Response response = Endpoint.subscriptionsWithKPIByDeviceId(queryParameters);
		if (paramMaps.get("description").contains("good request")) {
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertNotNull(response.jsonPath().getString("data"));
			assertThat(response.getBody().asString(),
					matchesJsonSchemaInClasspath("iems/" + RestConstants.SUBSCRIPTIONDATA + ".JSON"));
		} else {
			Assert.assertNull(response.jsonPath().getString("data"));
		}
	}
	
	@Test(priority = 0, description = RestConstants.DELETESUBSCRIPTIONS)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT with id and delete subscription.")
	@Feature("Get subscription data API")
	@Story("Delete subscription by id")
	public void deleteSubscriptionsSuccess() {
		HashMap<String, String> deviceMap = Endpoint.getDeviceIdByName(new ArrayList<String>(){{
            add("1#制冷机");
            add("3#制冷机");
        }});
		HashMap<String, Object> queryParameters = new HashMap<>();
		queryParameters.put("request", Integer.parseInt(deviceMap.get("1#制冷机")));
		Response responsePro = Endpoint.subscriptionsWithKPIByDeviceId(queryParameters);
		Assert.assertEquals(responsePro.getStatusCode(), 200);
		JsonPath jsonPathEvaluator = responsePro.jsonPath();
		Assert.assertNotNull(jsonPathEvaluator.get("data"));
		HashMap data = jsonPathEvaluator.get("data");
		String replyTo = data.get("replyTo").toString();
		Response response = Endpoint.deleteSubscriptions(replyTo);
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertNotNull(response.jsonPath().getString("data"));
		assertThat(response.getBody().asString(),
				matchesJsonSchemaInClasspath("iems/" + RestConstants.DELETESUBSCRIPTION + ".JSON"));
	}
	
	@Test(priority = 0, description = RestConstants.DELETESUBSCRIPTIONS, dataProviderClass = SubscriptionsRequestDataPro.class, dataProvider = "dataForDeleteSubscriptions")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT with id and delete subscription.")
	@Feature("Get subscription data API")
	@Story("Delete subscription by id")
	public void deleteSubscriptions(Map<String, String> paramMaps) {
		Response response = Endpoint.deleteSubscriptions(paramMaps.get("id"));
		if (paramMaps.get("description").contains("good request")) {
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertNotNull(response.jsonPath().getString("data"));
			assertThat(response.getBody().asString(),
					matchesJsonSchemaInClasspath("iems/" + RestConstants.DELETESUBSCRIPTION + ".JSON"));
		} else {
			Assert.assertNull(response.jsonPath().getString("data"));
		}
	}

}
