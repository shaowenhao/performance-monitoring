package com.siemens.datalayer.connector.test;


import com.alibaba.fastjson.JSON;
import com.siemens.datalayer.apiengine.test.ApiEngineCacheControllerEndpoint;
import com.siemens.datalayer.entitymanagement.test.EntityManagementEndpoint;
import com.siemens.datalayer.iot.util.KafkaUtil;
import com.siemens.datalayer.snc.util.MessageSupplier;
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
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

@Epic("SDL connector realtime cache controller")
@Feature("connector realtime cache controler")
public class ConnectorRealtimeCacheControllerTests<d> {

    // server 是 monitor-test 下建的 测试搭建的 kafka
    String servers = "140.231.89.106:30962";
    String topic = "OUT-EVENT";
    @Parameters({"base_url", "port"})
    @BeforeClass(description = "Configure the host address and communication port of connector realtime cache controller")
    public void setApiEngineCacheControllerEndpoint(@Optional("http://140.231.89.106") String base_url, @Optional("32563") String port) {

        ConnectorRealtimeCacheControllerEndpoint.setBaseUrl(base_url);
        ConnectorRealtimeCacheControllerEndpoint.setPort(port);
        AllureEnvironmentPropertiesWriter.addEnvironmentItem("data-layer-connector-realtime", base_url + ":" + port);
    }

    @Test(priority = 0,
            description = "Test Connector realtime Cache Controller: allCacheNames"
            )
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'getAllCacheNames' request to cache controller interface.")
    @Story("Cache Controller: cache interface")
    public void getAllCacheNames() {
        Response response = ConnectorRealtimeCacheControllerEndpoint.getAllCacheNames();
        response.prettyPrint();
        List<String> actualList = response.jsonPath().getList("data");
        assertThat(actualList,is(hasItems("mapperConfigCache","engineConfigCache")));
    }


    @Test(priority = 0,
            description = "Test connector realtime Cache Controller: check mapper cache key and value"
           )
    @Severity(SeverityLevel.BLOCKER)
    @Description("check cache key and value though getCacheValue and allCacheKeys")
    @Story("Cache Controller: check mapper cache key and value")
    public void checkMapperCache() {

        // Kafka发送消息 持久化
        KafkaProducer<String, String> producer = KafkaUtil.createProducer(servers);
        LocalDateTime dateTime = LocalDateTime.now();
        String dayOfMonth = String.valueOf(dateTime.getDayOfMonth());
        String message = "{\"process\":\"FCE0164\",\"event\":\"in\",\"coreId\":\"N1234567890\",\"device\":\"test" + dayOfMonth + "\",\"timestamp\":\"\"} ";
        KafkaUtil.send(producer,topic, message);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        checkKeyandValue();
    }

    @Step("check key and value")
    public void checkKeyandValue() {
        // Check Cache key and value
        Response keyResponse = ConnectorRealtimeCacheControllerEndpoint.getAllCacheKeys("mapperConfigCache");
        keyResponse.prettyPrint();
        String keyContent = keyResponse.jsonPath().getString("data[0]");
        System.out.println(keyContent);
        String[] splitArray = keyContent.split("::");
        String key = splitArray[1];
        Response valueResponse = ConnectorRealtimeCacheControllerEndpoint.getCacheValue("mapperConfigCache",key);
        valueResponse.prettyPrint();
        Assert.assertEquals(valueResponse.jsonPath().getString("message"),"Operate success.");
        //value接口 data 返回的是字符串
        Assert.assertTrue(!valueResponse.jsonPath().getString("data").isEmpty());
    }
}
