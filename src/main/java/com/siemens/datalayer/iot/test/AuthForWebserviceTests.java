package com.siemens.datalayer.iot.test;

import com.google.gson.Gson;
import com.siemens.datalayer.connector.test.ConnectorConfigureEndpoint;
import com.siemens.datalayer.connector.test.ConnectorEndpoint;
import com.siemens.datalayer.connector.test.ConnectorOtherInterfacesEndpoint;
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
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Epic("SDL Connector")
@Feature("auth for webservice")
public class AuthForWebserviceTests {

    @Parameters({"base_url", "port"})
    @BeforeClass(description = "Configure the host address and communication port of data-layer-connector")
    public void setConnectorEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("30850") String port) {
        ConnectorEndpoint.setBaseUrl(base_url);
        ConnectorEndpoint.setPort(port);
    }

    @Test(	priority = 0,
            description = "get webservice(data source) with auth Scenarios",
            dataProvider = "connector-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'getConceptModelDataByCondition' request with specified parameters and check the response message.")
    @Story("Webservice as data source,read (data source) by single entity with auth")
    public void authForWebserviceRead(Map<String, String> paramMaps){

        // 执行case之前，需调用connector（clearRedisCaches、clearAllCaches、clearRedisCache接口）
        // 和connector-configure（clear all cache接口）清除缓存
        ConnectorOtherInterfacesEndpoint.clearRedisCaches();
        ConnectorOtherInterfacesEndpoint.clearAllCaches();
        ConnectorOtherInterfacesEndpoint.clearRedisCache();

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
            description = "insert webservice(data source) with auth Scenarios",
            dataProvider = "connector-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'getConceptModelDataByCondition' request with specified parameters and check the response message.")
    @Story("Webservice as data source,insert (data source) by single entity with auth")
    public void authForWebserviceWrite(Map<String, String> paramMaps){
        // 执行case之前，需调用connector（clearRedisCaches、clearAllCaches、clearRedisCache接口）
        // 和connector-configure（clear all cache接口）清除缓存
        ConnectorOtherInterfacesEndpoint.clearRedisCaches();
        ConnectorOtherInterfacesEndpoint.clearAllCaches();
        ConnectorOtherInterfacesEndpoint.clearRedisCache();

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