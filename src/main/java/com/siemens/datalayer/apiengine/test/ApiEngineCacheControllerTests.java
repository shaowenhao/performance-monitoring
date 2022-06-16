package com.siemens.datalayer.apiengine.test;


import com.alibaba.fastjson.JSON;
import com.siemens.datalayer.connector.test.ConnectorConfigureEndpoint;
import com.siemens.datalayer.connector.test.ConnectorConfigureTests;
import com.siemens.datalayer.entitymanagement.test.EntityManagementEndpoint;
import com.siemens.datalayer.utils.AllureEnvironmentPropertiesWriter;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Epic("SDL Api-engine cache controller")
@Feature("api-engine cache controler")
public class ApiEngineCacheControllerTests {

    static String sucessfulRspCode = "100000";

    static Map<String, String> testEntityMap;

    @Parameters({"base_url", "port", "baseUrlOfEntityManagement", "portOfEntityManagement"})
    @BeforeClass(description = "Configure the host address and communication port of api-engine cache controller")
    public void setApiEngineCacheControllerEndpoint(@Optional("http://140.231.89.106") String base_url, @Optional("31950") String port,
                                                    @Optional("http://140.231.89.106") String baseUrlOfEntityManagement, @Optional("31954") String portOfEntityManagement) {
        testEntityMap = new HashMap<>();
        ApiEngineCacheControllerEndpoint.setBaseUrl(base_url);
        ApiEngineCacheControllerEndpoint.setPort(port);
        EntityManagementEndpoint.setBaseUrl(baseUrlOfEntityManagement);
        EntityManagementEndpoint.setPort(portOfEntityManagement);
        AllureEnvironmentPropertiesWriter.addEnvironmentItem("data-layer-api-engine", base_url + ":" + port);
        // clean all cache
        ApiEngineCacheControllerEndpoint.evictAllCache();
    }


    @AfterClass(description = "Clean up test entity")
    public void clearTestEnvironment()
    {
        String entityId = null;
        if (testEntityMap.size() > 0)
        {
            for (Map.Entry<String, String> entity : testEntityMap.entrySet())
            {
                entityId = entity.getValue();
                Response response = EntityManagementEndpoint.deleteEntities(entityId);

                    if (response.jsonPath().getString("code").equals(sucessfulRspCode)==false)
                        System.out.println("Error: can not remove the specified test entity (" + entity.getKey() + "/" + entityId + ")");
                }
            }
        //删除entity后 需要清掉cache
        ApiEngineCacheControllerEndpoint.evictAllCache();
    }

    @Test(priority = 0,
            description = "Test Api-engine Cache Controller: allCacheNames",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'getAllCacheNames' request to cache controller interface.")
    @Story("Cache Controller: cache interface")
    public void getAllCacheNames(Map<String, String> paramMaps) {
        Response response = ApiEngineCacheControllerEndpoint.getAllCacheNames();

        ConnectorConfigureTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));

        List<String> actualList = response.jsonPath().getList("data");

        List<String> expectedList = JSON.parseArray(paramMaps.get("rspData"), String.class);
        Assert.assertEquals(actualList, expectedList);

    }

    @Test(priority = 0,
            description = "Test Api-engine Cache Controller: check kgCache",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("create entity and check kgCache though getCacheValue and allCacheKeys")
    @Story("Cache Controller: check kgCache")
    public void kgCache(Map<String, String> paramMaps) {
        // 先创建Entity
        Response response = EntityManagementEndpoint.createEntities(paramMaps.get("createEntityBody"));
        if (response.jsonPath().getString("code").equals(sucessfulRspCode)) {
            List<Map<String, String>> entityList = response.jsonPath().getList("data");
            for (Map<String, String> entity : entityList) {
                if (!testEntityMap.containsKey(entity.get("label"))) {
                    testEntityMap.put(entity.get("label"), entity.get("id"));
                }

            }
        }
        //查询 包含"entity"的个数
        response = EntityManagementEndpoint.getEntities("");
        List<Map<String,Object>> dataList = response.jsonPath().getList("data");
        long entityCount = dataList.stream().filter(e -> {
            return ((Map<String, String>) e.get("properties")).containsKey("metadata_node_type") &&
                    ((Map<String, String>) e.get("properties")).containsValue("entity");
        }).count();
        System.out.println("entityCount:" + entityCount);

        //避免查kgCache为空 做一些延时
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //查询name为kgCache的allCacheKeys
        String name = paramMaps.get("name");
        response = ApiEngineCacheControllerEndpoint.getAllCacheKeys(name);
        List<String> keyList = response.jsonPath().getList("data");
        //获取包含 getNodesByLabel信息的key
        String actualKey = null;
        if(!keyList.isEmpty()){
            for (String key : keyList) {
                if(key.contains("getNodesByLabel")){
                    actualKey = key;
                }
            }
        }
        System.out.println("actualKey:" + actualKey);
       // 查询key为 actualKey的 cacheValue,统计kgNode的个数
        response = ApiEngineCacheControllerEndpoint.getCacheValue(name,actualKey);
        String valueResult = response.jsonPath().getString("data");
       //返回的结果 是个String不是List
        String[] kgNodeArray = valueResult.split(",");
        long kgNodeCount = kgNodeArray.length;
        System.out.println("kgNodeCount" + kgNodeCount);

      //判断entity count 与 kgNode cout 期望相同
        Assert.assertEquals(kgNodeCount,entityCount);
    }
}