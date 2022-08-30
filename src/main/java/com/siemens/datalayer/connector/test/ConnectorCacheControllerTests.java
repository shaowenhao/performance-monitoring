package com.siemens.datalayer.connector.test;

import com.siemens.datalayer.iot.util.KafkaUtil;
import com.siemens.datalayer.utils.AllureEnvironmentPropertiesWriter;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.siemens.datalayer.connector.test.InterfaceTests.checkResponseCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

@Epic("SDL connector cache controller")
@Feature("connector cache controler")
public class ConnectorCacheControllerTests {

    @Parameters({"base_url", "port"})
    @BeforeClass(description = "Configure the host address and communication port of connector cache controller")
    public void setConnectorCacheControllerEndpoint(@Optional("http://140.231.89.106") String base_url, @Optional("31439") String port) {

        ConnectorCacheControllerEndpoint.setBaseUrl(base_url);
        ConnectorCacheControllerEndpoint.setPort(port);

        ConnectorEndpoint.setBaseUrl(base_url);
        ConnectorEndpoint.setPort(port);
    }

    @Test(priority = 0,
            description = "Test Connector   Cache Controller: allCacheNames",
            dataProvider = "connector-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'getAllCacheNames' request to cache controller interface.")
    @Story("Cache Controller: cache interface")
    public void getAllCacheNames(Map<String, String> paramMaps) {

        Response response = ConnectorCacheControllerEndpoint.getAllCacheNames();
        response.prettyPrint();
        List<String> actualCacheNameList = response.jsonPath().getList("data");
        String dataList = paramMaps.get("dataList");
        String[] expectedCacheNames = dataList.split(",");
        assertThat(actualCacheNameList,is(hasItems(expectedCacheNames)));
        checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));
    }

    @Test(priority = 0,
            description = "Test Connector Cache Controller: get cache key and value",
            dataProvider = "connector-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'getAllCacheKeys and getCacheValue' request to cache controller interface.")
    @Story("Cache Controller: cache interface")
    public void getCacheKeyAndValue(Map<String, String> paramMaps) {

          // 先清掉所有cache
          ConnectorCacheControllerEndpoint.deleteAllCaches();
          HashMap<String, String> queryParameters = new HashMap<>();
          queryParameters.put("name",paramMaps.get("entityName"));
          // 调用connector查询 触发cache
          Response response = ConnectorEndpoint.getConceptModelDataByCondition(queryParameters);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String cacheName = paramMaps.get("cacheName");
        //根据cacheKey查询statictics
        response = ConnectorCacheControllerEndpoint.getCacheStatistics(cacheName);
        response.prettyPrint();
        checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));
        // 根据cacheKey查询 cacheValue
        response = ConnectorCacheControllerEndpoint.getAllCacheKeys(cacheName);
        response.prettyPrint();
        List<String> dataList = response.jsonPath().getList("data");
        if (!paramMaps.get("description").contains("auth")){
            for (String cacheKeys : dataList) {
                String[] split = cacheKeys.split("::");
                String actualCacheKey = split[1];
                response = ConnectorCacheControllerEndpoint.getCacheValue(actualCacheKey, cacheName);
                response.prettyPrint();
                checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));
            }
        }else {
            // List结果即为cacheKey
            for (String cacheKey : dataList) {
                response = ConnectorCacheControllerEndpoint.getCacheValue(cacheKey, cacheName);
                response.prettyPrint();
                checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));
            }
        }
        //根据cacheName清cache
        response = ConnectorCacheControllerEndpoint.deleteCache(cacheName);
        response.prettyPrint();
        //查询cacheKey被清空
        response = ConnectorCacheControllerEndpoint.getAllCacheKeys(cacheName);
        response.prettyPrint();
        dataList = response.jsonPath().getList("data");
        Assert.assertTrue(dataList.size() == 0);
    }


}
