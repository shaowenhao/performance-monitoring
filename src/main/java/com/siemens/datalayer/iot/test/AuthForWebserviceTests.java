package com.siemens.datalayer.iot.test;

import com.google.gson.Gson;
import com.siemens.datalayer.connector.test.ConnectorConfigureEndpoint;
import com.siemens.datalayer.connector.test.ConnectorEndpoint;
import com.siemens.datalayer.connector.test.InterfaceTests;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Epic("SDL Connector")
@Feature("webservice as data source")
public class AuthForWebserviceTests {

    @Parameters({"base_url", "port"})
    @BeforeClass(description = "Configure the host address and communication port of data-layer-connector")
    public void setConnectorEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("30850") String port) {
        ConnectorEndpoint.setBaseUrl(base_url);
        ConnectorEndpoint.setPort(port);
    }

    @Test(	priority = 0,
            description = "\"webservice with auth\" as the data source, read (data source) by single entity",
            dataProvider = "connector-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'getConceptModelDataByCondition' request with specified parameters and check the response message.")
    @Story("auth for webservice read")
    public void authForWebserviceRead(Map<String, String> paramMaps){

        // 执行case之前，需调用connector（cleanUpAllCaches接口）
        // 和connector-configure（deleteCache接口）清除缓存
        ConnectorEndpoint.cleanUpAllCaches();

        List<String> nameList = Arrays.asList(
                "MongoDao",
                "appAuth",
                "conceptSchema",
                "connector",
                "dataSource",
                "engine",
                "entityConfig",
                "mapper",
                "mapperCondition",
                "mapperRule",
                "metaManagement",
                "plugin",
                "rule",
                "transaction");
        for (String name : nameList)
        {
            ConnectorConfigureEndpoint.deleteCache(name);
        }

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        HashMap<String,String> queryParameters = new HashMap<>();

        if (paramMaps.containsKey("authInfo")){
            try {
                String base64encodedAuthInfo = Base64.getEncoder().encodeToString(paramMaps.get("authInfo").getBytes("utf-8"));
                queryParameters.put("authInfo",base64encodedAuthInfo);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if (paramMaps.containsKey("name")) queryParameters.put("name",paramMaps.get("name"));

        Response response = ConnectorEndpoint.getConceptModelDataByCondition(queryParameters);
        System.out.println(response.jsonPath().getString(""));

        InterfaceTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));

        if (paramMaps.containsKey("rspData")){
            List<String> list = Arrays.asList(paramMaps.get("rspData").split("}"));
            List<Map<String,String>> expectedRspDataList = new ArrayList<>();
            for (int i=0;i<list.size();i++)
            {
                Gson gson = new Gson();
                Map<String,String> expectedRspDataListItem = new HashMap<>();
                expectedRspDataListItem = gson.fromJson(list.get(i)+"}",expectedRspDataListItem.getClass());
                expectedRspDataList.add(expectedRspDataListItem);
            }

            List<Map<String,String>> actualRspDataList = response.jsonPath().getList("data");

            Assert.assertEquals(expectedRspDataList,actualRspDataList);
        }
    }

    @Test(	priority = 0,
            description = "\"webservice with auth\" as the data source, write (data source) by single entity",
            dataProvider = "connector-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'getConceptModelDataByCondition' request with specified parameters and check the response message.")
    @Story("auth for webservice write")
    public void authForWebserviceWrite(Map<String, String> paramMaps){
        // 执行case之前，需调用connector（clearRedisCaches、clearAllCaches、clearRedisCache接口）
        // 和connector-configure（clear all cache接口）清除缓存
        ConnectorEndpoint.clearRedisCaches();
        ConnectorEndpoint.clearAllCaches();
        ConnectorEndpoint.clearRedisCache();

        ConnectorConfigureEndpoint.clearAllCache();

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        HashMap<String,String> queryParameters = new HashMap<>();
        if (paramMaps.containsKey("authInfo")){
            try {
                String base64encodedAuthInfo = Base64.getEncoder().encodeToString(paramMaps.get("authInfo").getBytes("utf-8"));
                queryParameters.put("authInfo",base64encodedAuthInfo);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        String insertInfo = null;
        if (paramMaps.containsKey("insertInfo")){
            insertInfo = paramMaps.get("insertInfo");
        }

        Response response = ConnectorEndpoint.dmlInsertOperator(queryParameters,insertInfo);
        System.out.println(response.jsonPath().getString(""));

        InterfaceTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));

        if (paramMaps.containsKey("rspData")){
            List<String> list = Arrays.asList(paramMaps.get("rspData").split("}"));
            List<Map<String,String>> expectedRspDataList = new ArrayList<>();
            for (int i=0;i<list.size();i++)
            {
                Gson gson = new Gson();
                Map<String,String> expectedRspDataListItem = new HashMap<>();
                expectedRspDataListItem = gson.fromJson(list.get(i)+"}",expectedRspDataListItem.getClass());
                expectedRspDataList.add(expectedRspDataListItem);
            }

            List<Map<String,String>> actualRspDataList = response.jsonPath().getList("data."+paramMaps.get("entityName")+"[0].data");

            Assert.assertEquals(expectedRspDataList,actualRspDataList);
        }
    }
}