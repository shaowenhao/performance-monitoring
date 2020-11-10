package com.siemens.datalayer.jinzu.test;

import com.siemens.datalayer.iems.test.ConnectorEndpoints;
import com.siemens.datalayer.utils.AllureEnvironmentPropertiesWriter;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.annotations.Optional;

import java.util.*;

@Epic("JinZu Connector Interface")
public class JinzuConnectorTest {

    @Parameters({"base_url", "port"})
    @BeforeClass(description = "Configure host address and communication port for Connector service")
    public void setConnectorEndpoint(@Optional("http://140.231.89.85") String base_url,
                                     @Optional("32710") String port) {
        ConnectorEndpoints.setBaseUrl(base_url);
        ConnectorEndpoints.setPort(port);
        AllureEnvironmentPropertiesWriter
                .addEnvironmentItem("Connector Address", base_url + ":" + port);
    }


    @DataProvider(name = "JinzuDataSets")
    Iterator<Object[]> jinzuDataSets() {
        Collection<Object[]> queryParamCollection = new ArrayList<>();
        List<Map<String, Object>> listOfQueryParams = new ArrayList<>();

        //SensorData
        Map<String, Object> goodquery01 = new HashMap<>();
        goodquery01.put("description", "good request, data is restreived");
        goodquery01.put("name", "Inverter");

        listOfQueryParams.add(goodquery01);

        Map<String, Object> goodquery02 = new HashMap<>();
        goodquery02.put("description", "good request, data is restreived");
        goodquery02.put("name", "Site");
        //goodquery02.put("condition", "Siid = 5400");

        listOfQueryParams.add(goodquery02);

        Map<String, Object> goodquery03 = new HashMap<>();
        goodquery03.put("description", "good request, data is restreived");
        goodquery03.put("name", "Lease");
        listOfQueryParams.add(goodquery03);

        Map<String, Object> goodquery04 = new HashMap<>();
        goodquery04.put("description", "good request, data is restreived");
        goodquery04.put("name", "Power_Station_Properties");
        listOfQueryParams.add(goodquery04);

        Map<String, Object> goodquery05 = new HashMap<>();
        goodquery05.put("description", "good request, data is restreived");
        goodquery05.put("name", "Customer");
        listOfQueryParams.add(goodquery05);

        Map<String, Object> goodquery06 = new HashMap<>();
        goodquery06.put("description", "good request, data is restreived");
        goodquery06.put("name", "Device_Alarm_Event");
        listOfQueryParams.add(goodquery06);

        Map<String, Object> goodquery07 = new HashMap<>();
        goodquery07.put("description", "good request, data is restreived");
        goodquery07.put("name", "Device_Model");
        listOfQueryParams.add(goodquery07);

        Map<String, Object> goodquery08 = new HashMap<>();
        goodquery08.put("description", "good request, data is restreived");
        goodquery08.put("name", "Device_Alarm_Template");
        listOfQueryParams.add(goodquery08);

        Map<String, Object> goodquery09 = new HashMap<>();
        goodquery09.put("description", "good request, data is restreived");
        goodquery09.put("name", "Weather");
        listOfQueryParams.add(goodquery09);

        Map<String, Object> goodquery10 = new HashMap<>();
        goodquery10.put("description", "good request, data is restreived");
        goodquery10.put("name", "Project");
        listOfQueryParams.add(goodquery10);


        Map<String, Object> goodquery11 = new HashMap<>();
        goodquery11.put("description", "good request, data is restreived");
        goodquery11.put("name", "Site_Report");
        listOfQueryParams.add(goodquery11);

        Map<String, Object> goodquery12 = new HashMap<>();
        goodquery12.put("description", "good request, data is restreived");
        goodquery12.put("name", "Device_Detail_Info");
        listOfQueryParams.add(goodquery12);

        Map<String, Object> goodquery13 = new HashMap<>();
        goodquery13.put("description", "good request, data is restreived");
        goodquery13.put("name", "Power_Station");
        listOfQueryParams.add(goodquery13);

        Map<String, Object> goodquery14 = new HashMap<>();
        goodquery14.put("description", "good request, data is restreived");
        goodquery14.put("name", "Lease_Group");
        listOfQueryParams.add(goodquery14);

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
            dataProvider = "JinzuDataSets")
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
