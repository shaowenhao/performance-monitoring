package com.siemens.datalayer.iems.test;

import com.siemens.datalayer.connector.model.GetAllEntitiesNameResponse;
import com.siemens.datalayer.utils.AllureEnvironmentPropertiesWriter;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author z003vffr
 * @date 11/6/2020 1:11 PM
 */
@Epic("IEMS Connector Interface")
@Feature("Rest API")
public class ConnectorInterfaceTests {

    @Parameters({"base_url", "port"})
    @BeforeClass(description = "Configure host address and communication port for Connector service")
    public void setConnectorEndpoint(@Optional("http://localhost") String base_url,
        @Optional("9001") String port) {
        ConnectorEndpoints.setBaseUrl(base_url);
        ConnectorEndpoints.setPort(port);
        AllureEnvironmentPropertiesWriter
            .addEnvironmentItem("Connector Address", base_url + ":" + port);
    }

    @Test(priority = 0, description = "Test connector interface: Get All Entities name.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if all the available entity names can be read out.")
    @Story("Get All Entities name")
    public void GetAllEntitiesName() {
        Response response = ConnectorEndpoints.getAllEntitiesName();

        GetAllEntitiesNameResponse rspBody = response.getBody()
            .as(GetAllEntitiesNameResponse.class);

        Assert.assertEquals("Operate success.", rspBody.getMessage());

    }

    //Test Data Set
    @DataProvider(name = "iemsDataSearchSensorDataSet")
    Iterator<Object[]> iemsDataSearchSensorDataSet() {
        Collection<Object[]> queryParamCollection = new ArrayList<>();
        List<Map<String, Object>> listOfQueryParams = new ArrayList<>();

        //SensorData
        Map<String, Object> goodquery01 = new HashMap<>();
        goodquery01.put("description", "good request, data is restreived");
        goodquery01.put("name", "SensorData");

        listOfQueryParams.add(goodquery01);

        Map<String, Object> goodquery02 = new HashMap<>();
        goodquery02.put("description", "good request, data is restreived");
        goodquery02.put("name", "SensorData");
        goodquery02.put("condition", "Siid = 5400");

        listOfQueryParams.add(goodquery02);

        Map<String, Object> goodquery03 = new HashMap<>();
        goodquery03.put("description", "good request, data is restreived");
        goodquery03.put("name", "SensorData");
        goodquery03.put("conditio0in", "1=1");
        listOfQueryParams.add(goodquery03);

        Map<String, Object> goodquery04 = new HashMap<>();
        goodquery04.put("description", "good request, data is restreived");
        goodquery04.put("name", "SensorData");
        goodquery04.put("condition", "Siid > 5400");
        listOfQueryParams.add(goodquery04);

        Map<String, Object> goodquery05 = new HashMap<>();
        goodquery05.put("description", "good request, data is restreived");
        goodquery05.put("name", "SensorData");
        goodquery05.put("condition", "Siid > 5400");
        goodquery05.put("domainName", "iEMS");
        listOfQueryParams.add(goodquery05);

        Map<String, Object> goodquery06 = new HashMap<>();
        goodquery06.put("description", "good request, data is restreived");
        goodquery06.put("name", "SensorData");
        goodquery06.put("condition", "Siid = 5400");
        goodquery06.put("domainName", "iEMS");
        goodquery06.put("fields", "updateTime");
        listOfQueryParams.add(goodquery06);

        Map<String, Object> goodquery07 = new HashMap<>();
        goodquery07.put("description", "good request, data is restreived");
        goodquery07.put("name", "SensorData");
        goodquery07.put("condition", "Siid = 5400");
        goodquery07.put("domainName", "iEMS");
        goodquery07.put("fields", "updateTime, Siid");
        listOfQueryParams.add(goodquery07);

        Map<String, Object> goodquery08 = new HashMap<>();
        goodquery08.put("description", "good request, data is restreived");
        goodquery08.put("name", "SensorData");
        goodquery08.put("condition", "Siid = 5400");
        goodquery08.put("domainName", "iEMS");
        goodquery08.put("fields", "*");
        listOfQueryParams.add(goodquery08);

        Map<String, Object> goodquery09 = new HashMap<>();
        goodquery09.put("description", "good request, data is restreived");
        goodquery09.put("name", "SensorData");
        goodquery09.put("order", "siid");
        listOfQueryParams.add(goodquery09);

        Map<String, Object> goodquery10 = new HashMap<>();
        goodquery10.put("description", "good request, data is restreived");
        goodquery10.put("name", "SensorData");
        goodquery10.put("order", "+siid");
        listOfQueryParams.add(goodquery10);

        Map<String, Object> goodquery11 = new HashMap<>();
        goodquery11.put("description", "good request, data is restreived");
        goodquery11.put("name", "SensorData");
        goodquery11.put("order", "-siid");
        listOfQueryParams.add(goodquery11);

        Map<String, Object> goodquery12 = new HashMap<>();
        goodquery12.put("description", "good request, data is restreived");
        goodquery12.put("name", "SensorData");
        goodquery12.put("order", "siid, updateTime");
        listOfQueryParams.add(goodquery12);

        Map<String, Object> goodquery13 = new HashMap<>();
        goodquery13.put("description", "good request, data is restreived");
        goodquery13.put("name", "SensorData");
        goodquery13.put("pageIndex", "0");
        listOfQueryParams.add(goodquery13);

        Map<String, Object> goodquery14 = new HashMap<>();
        goodquery14.put("description", "good request, data is restreived");
        goodquery14.put("name", "SensorData");
        goodquery14.put("pageIndex", "2");
        listOfQueryParams.add(goodquery14);

        Map<String, Object> goodquery15 = new HashMap<>();
        goodquery15.put("description", "good request, data is restreived");
        goodquery15.put("name", "SensorData");
        goodquery15.put("pageSize", "0");
        listOfQueryParams.add(goodquery15);

        Map<String, Object> goodquery16 = new HashMap<>();
        goodquery16.put("description", "good request, data is restreived");
        goodquery16.put("name", "SensorData");
        goodquery16.put("pageSize", "2");
        listOfQueryParams.add(goodquery16);

        Map<String, Object> badquery01 = new HashMap<>();

        badquery01.put("description", "bad request, data not found");
        badquery01.put("name", "");
        badquery01.put("expectCode", "106601");
        badquery01.put("expectMessage", "searchData.name is not valid,reason: must not be blank");

        listOfQueryParams.add(badquery01);

        Map<String, Object> badquery02 = new HashMap<>();
        badquery02.put("description", "bad request, data not found");
        badquery02.put("name", "1321");
        badquery02.put("expectCode", "108001");
        badquery02.put("expectMessage",
            "The m2 service unavailable: (request M2 failed : no found entity ).");
        listOfQueryParams.add(badquery02);

        Map<String, Object> badquery03 = new HashMap<>();
        badquery03.put("description", "bad request, data not found");
        badquery03.put("name", "SensorData");
        badquery03.put("condition", "Siid = 5400");
        badquery03.put("domainName", "123213321");
        badquery03.put("expectCode", "106602");
        badquery03.put("expectMessage", "");
        listOfQueryParams.add(badquery03);

        Map<String, Object> badquery04 = new HashMap<>();
        badquery04.put("description", "bad request, data not found");
        badquery04.put("name", "SensorData");
        badquery04.put("condition", "Siid = 5400");
        badquery04.put("domainName", "iEMS");
        badquery04.put("fields", "321321");
        badquery04.put("expectCode", "106107");
        badquery04.put("expectMessage", "fields false not exist in entity!");
        listOfQueryParams.add(badquery04);

        Map<String, Object> badquery05 = new HashMap<>();
        badquery05.put("description", "bad request, data not found");
        badquery05.put("name", "SensorData");
        badquery05.put("order", "12");
        badquery05.put("expectCode", "106601");
        badquery05.put("expectMessage", "bad request:error order: 12");
        listOfQueryParams.add(badquery05);

        //HeadPumpKpiData
        Map<String, Object> goodquery001 = new HashMap<>();
        goodquery001.put("description", "good request, data is restreived");
        goodquery001.put("name", "HeatPumpKpiData");
        listOfQueryParams.add(goodquery001);

        Map<String, Object> goodquery002 = new HashMap<>();
        goodquery002.put("description", "good request, data is restreived");
        goodquery002.put("name", "HeatPumpKpiData");
        goodquery002.put("condition", "motor_current_percent=0");
        listOfQueryParams.add(goodquery002);

        Map<String, Object> goodquery003 = new HashMap<>();
        goodquery003.put("description", "good request, data is restreived");
        goodquery003.put("name", "HeatPumpKpiData");
        goodquery003.put("condition", "motor_current_percent=0 and condensor_water_inlet_temperature=21.7");
        listOfQueryParams.add(goodquery003);

        Map<String, Object> goodquery004 = new HashMap<>();
        goodquery004.put("description", "good request, data is restreived");
        goodquery004.put("name", "HeatPumpKpiData");
        goodquery004.put("condition", "1=1");
        listOfQueryParams.add(goodquery004);

        Map<String, Object> goodquery005 = new HashMap<>();
        goodquery005.put("description", "good request, data is restreived");
        goodquery005.put("name", "HeatPumpKpiData");
        goodquery005.put("condition", "motor_current_percent>0 and condensor_water_inlet_temperature>21.7");
        listOfQueryParams.add(goodquery005);


        Map<String, Object> goodquery006 = new HashMap<>();
        goodquery006.put("description", "good request, data is restreived");
        goodquery006.put("name", "HeatPumpKpiData");
        goodquery006.put("condition", "motor_current_percent>0");
        goodquery006.put("domainName", "iEMS");
        listOfQueryParams.add(goodquery006);

        Map<String, Object> goodquery007 = new HashMap<>();
        goodquery007.put("description", "good request, data is restreived");
        goodquery007.put("name", "HeatPumpKpiData");
        goodquery007.put("condition", "motor_current_percent>0");
        goodquery007.put("domainName", "iEMS");
        goodquery007.put("fields", "deviceName,start_up_time");
        listOfQueryParams.add(goodquery007);

        Map<String, Object> goodquery008 = new HashMap<>();
        goodquery008.put("description", "good request, data is restreived");
        goodquery008.put("name", "HeatPumpKpiData");
        goodquery008.put("condition", "motor_current_percent>0");
        goodquery008.put("domainName", "iEMS");
        goodquery008.put("fields", "*");
        listOfQueryParams.add(goodquery008);

        Map<String, Object> goodquery009 = new HashMap<>();
        goodquery009.put("description", "good request, data is restreived");
        goodquery009.put("name", "HeatPumpKpiData");
        goodquery009.put("order", "run_time");
        listOfQueryParams.add(goodquery009);

        Map<String, Object> goodquery010 = new HashMap<>();
        goodquery010.put("description", "good request, data is restreived");
        goodquery010.put("name", "HeatPumpKpiData");
        goodquery010.put("order", "+run_time");
        listOfQueryParams.add(goodquery010);

        Map<String, Object> goodquery011 = new HashMap<>();
        goodquery011.put("description", "good request, data is restreived");
        goodquery011.put("name", "HeatPumpKpiData");
        goodquery011.put("order", "-run_time");
        listOfQueryParams.add(goodquery011);

        Map<String, Object> goodquery012 = new HashMap<>();
        goodquery012.put("description", "good request, data is restreived");
        goodquery012.put("name", "HeatPumpKpiData");
        goodquery012.put("order", "run_time, updateTime");
        listOfQueryParams.add(goodquery012);

        Map<String, Object> goodquery013 = new HashMap<>();
        goodquery013.put("description", "good request, data is restreived");
        goodquery013.put("name", "HeatPumpKpiData");
        goodquery013.put("pageIndex", "0");
        listOfQueryParams.add(goodquery013);


        Map<String, Object> goodquery014 = new HashMap<>();
        goodquery014.put("description", "good request, data is restreived");
        goodquery014.put("name", "HeatPumpKpiData");
        goodquery014.put("pageIndex", "2");
        listOfQueryParams.add(goodquery014);

        Map<String, Object> goodquery015 = new HashMap<>();
        goodquery015.put("description", "good request, data is restreived");
        goodquery015.put("name", "HeatPumpKpiData");
        goodquery015.put("pageSize", "0");
        listOfQueryParams.add(goodquery015);

        Map<String, Object> goodquery016 = new HashMap<>();
        goodquery016.put("description", "good request, data is restreived");
        goodquery016.put("name", "HeatPumpKpiData");
        goodquery016.put("pageIndex", "0");
        goodquery016.put("pageSize", "0");
        listOfQueryParams.add(goodquery016);



        Map<String, Object> goodquery017 = new HashMap<>();
        goodquery017.put("description", "good request, data is restreived");
        goodquery017.put("name", "HeatPumpKpiData");
        goodquery017.put("condition", "motor_current_percent>0");
        goodquery017.put("domainName", "iEMS");
        goodquery017.put("fields", "deviceName");
        listOfQueryParams.add(goodquery017);




        for (Map<String, Object> map : listOfQueryParams) {
            if (map.get("description").toString().contains("good request")) {
                map.put("expectMessage", "Operate success.");
            }

            queryParamCollection.add(new Object[]{map});
        }

        return queryParamCollection.iterator();
    }

    @Test(priority = 0,
        description = "Test connector interface: Get concept model data by condition.",
        dataProvider = "iemsDataSearchSensorDataSet")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'SearchModelDataByCondition' request to SUT with specified parameters and check the response message.")
    @Story("Get concept model data by condition")
    public void SearchModelDataByCondition(Map<String, String> paramMaps) {
        HashMap<String, String> queryParameters = new HashMap<>();

        if (paramMaps.containsKey("name")) {
            queryParameters.put("name", paramMaps.get("name"));
        }
        if (paramMaps.containsKey("domainName")) {
            queryParameters.put("domainName", paramMaps.get("domainName"));
        }

        if (paramMaps.containsKey("pageIndex")) {
            queryParameters.put("pageIndex", paramMaps.get("pageIndex"));
        }

        if (paramMaps.containsKey("pageSize")) {
            queryParameters.put("pageSize", paramMaps.get("pageSize"));
        }

        if (paramMaps.containsKey("condition")) {
            queryParameters.put("condition", paramMaps.get("condition"));
        }

        if (paramMaps.containsKey("fields")) {
            queryParameters.put("fields", paramMaps.get("fields"));
        }

        if (paramMaps.containsKey("order")) {
            queryParameters.put("order", paramMaps.get("order"));
        }

        Response response = ConnectorEndpoints.getConceptModelDataByCondition(queryParameters);

        if (paramMaps.get("description").contains("good request")) {
            Assert.assertEquals(response.getStatusCode(), 200);
        }

        if (paramMaps.get("description").contains("bad request")) {
//            Assert.assertEquals(response.getStatusCode(), paramMaps.get("expectCode"));

            if (paramMaps.containsKey("expectCode")) {
                Assert.assertEquals(response.jsonPath().getString("code"),
                    paramMaps.get("expectCode"));
            }
            if (paramMaps.containsKey("expectMessage")) {
                Assert.assertEquals(response.jsonPath().getString("message"),
                    (paramMaps.get("expectMessage")));
            }
        }


    }

}
