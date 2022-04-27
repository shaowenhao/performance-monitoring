package com.siemens.datalayer.iot.test;

import com.siemens.datalayer.connector.test.ConnectorConfigureEndpoint;
import com.siemens.datalayer.connector.test.ConnectorEndpoint;
import com.siemens.datalayer.connector.test.InterfaceTests;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Epic("SDL Connector")
@Feature("restful as datasource")
public class AuthForRestfulReadTests {

    @Parameters({"base_url", "port"})
    @BeforeClass(description = "Configure the host address and communication port of data-layer-connector")
    public void setConnectorEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("30850") String port) {
        ConnectorEndpoint.setBaseUrl(base_url);
        ConnectorEndpoint.setPort(port);
    }

    @Test(	priority = 0,
            description = "\"restful with auth\" as the data source, read (data source) by single entity",
            dataProvider = "connector-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'getConceptModelDataByCondition' request with specified parameters and check the response message.")
    @Story("auth for restful read")
    public void authForRestfulRead(Map<String, String> paramMaps){

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

        if (paramMaps.containsKey("name")) queryParameters.put("name",paramMaps.get("name"));

        Response response = ConnectorEndpoint.getConceptModelDataByCondition(queryParameters);
        System.out.println(response.jsonPath().getString(""));

        InterfaceTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));
    }
}