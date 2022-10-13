
package com.siemens.datalayer.jinzu.test;

        import com.siemens.datalayer.connector.test.ConnectorEndpoint;
        import com.siemens.datalayer.iot.util.KafkaUtil;
        import io.qameta.allure.*;
        import io.restassured.response.Response;

        import java.time.LocalDateTime;
        import java.util.Arrays;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

        import org.apache.kafka.clients.consumer.ConsumerRecord;
        import org.apache.kafka.clients.consumer.ConsumerRecords;
        import org.apache.kafka.clients.consumer.KafkaConsumer;
        import org.apache.log4j.Logger;
        import org.testng.Assert;
        import org.testng.annotations.BeforeClass;
        import org.testng.annotations.Optional;
        import org.testng.annotations.Parameters;
        import org.testng.annotations.Test;

        import com.siemens.datalayer.utils.ExcelDataProviderClass;

        import static com.siemens.datalayer.connector.test.InterfaceTests.checkResponseCode;
        import static org.hamcrest.MatcherAssert.assertThat;
        import static org.hamcrest.Matchers.*;

@Epic("SDL Modbus scenarios")
public class ModbusTests {

    String servers = "140.231.89.85:31092,140.231.89.85:31093,140.231.89.85:31094,140.231.89.85:31095,140.231.89.85:31096";
    // on jinzu-test api-engine create subscription to query on ModbusTestEnitity got the replyTo value was the Topic
    String topic = "ecd00e5a3b1cb604cff820c4359161fb";
    Logger logger = Logger.getLogger(ModbusTests.class);

    @Parameters({"base_url", "port"})
    @BeforeClass(description = "Configure the host address and communication port of data-layer-connector")
    public void setConnectorEndpoint(@Optional("http://140.231.89.106") String base_url, @Optional("32710") String port)
    {
        ConnectorEndpoint.setBaseUrl(base_url);
        ConnectorEndpoint.setPort(port);
    }

    @Test(priority = 0, description = "query modbus entity",
            dataProvider = "connector-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Query Modbus")
    @Story("Query Modbus entity")
    public void queryModbusEntity(Map<String, String> paramMaps)
    {
        HashMap<String,String> queryParameters = new HashMap<>();
        if(paramMaps.containsKey("name")){
            queryParameters.put("name",paramMaps.get("name"));
        }

        if(paramMaps.containsKey("condition")){
            LocalDateTime now = LocalDateTime.now();
            String startTime = now.toString();
            LocalDateTime end = now.plusSeconds(10);
            String endTime = end.toString();
            String condition = paramMaps.get("condition").replace("$start_time", startTime).replace("$end_time", endTime);
            queryParameters.put("condition",condition);
        }
        Response response = ConnectorEndpoint.getConceptModelDataByCondition(queryParameters);
        response.prettyPrint();
        checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));
        List<Map<String,String>> responseDataList = response.jsonPath().getList("data");
        //校验当前时间到之后的10秒内 得到的结果集至少有1条
        Assert.assertTrue(responseDataList.size() >= 1);
        //Modbus mock 服务配置 holdingValue 固定值为 010203040506
        for (Map<String, String> dataMap : responseDataList) {
            assertThat(dataMap,hasEntry("holdingValue","010203040506"));
        }
        
    }


    @Test(priority = 0, description = "subscription modbus entity")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Subscription Modbus, Kafka consume message")
    @Story("Subscription Modbus entity")
    public void subscriptionModbusEntity()
    {

        KafkaConsumer<String, String> consumer = KafkaUtil.createConsumer(servers);
        //Subscribing
        consumer.subscribe(Arrays.asList(topic));
        //polling
        int total = 0;
        while (total <= 3){

            ConsumerRecords<String, String> records = consumer.poll(1000);
            for (ConsumerRecord<String, String> record : records) {
                logger.info(record.toString());
                total += 1;
                String message = record.value();
                Assert.assertTrue(message.contains("ModbusTestEntity"));
            }
        }
    }
}
