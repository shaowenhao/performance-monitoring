package com.siemens.datalayer.jinzu.test;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.siemens.datalayer.connector.test.ConnectorEndpoint;
import com.siemens.datalayer.iot.util.JdbcDatabaseUtil;
import com.siemens.datalayer.iot.util.KafkaUtil;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.hc.client5.http.io.ConnectionEndpoint;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Epic("Realtime Persistence")
@Feature("Kafka message persistence on clickhouse")
public class RealtimePersistenceTests {

   private Connection clickhouseConnection;
   private Statement clickhouseStatement;

   private String kafkaServer = "140.231.89.106:30962";
   private String topic = "OUT-EVENT";
   @Parameters({"base_url", "port"})
   @BeforeClass(description = "Initialize jdbc connection for clickhouse")
   public void setup(@Optional("http://140.231.89.106") String base_url, @Optional("32710") String port){
        ConnectorEndpoint.setBaseUrl(base_url);
        ConnectorEndpoint.setPort(port);

        clickhouseConnection = JdbcDatabaseUtil.getConnection("jinzu.test.clickhouse.db.properties");

           if (clickhouseConnection != null){
               System.out.println("clickhouse connected successfully");
           }
       try {
           clickhouseStatement = clickhouseConnection.createStatement();
       } catch (SQLException e) {
           e.printStackTrace();
       }
   }

   @AfterClass(description = "close connection of clickhouse")
   public void clear(){
       try {
           if (!clickhouseConnection.isClosed()){
               clickhouseConnection.close();
               clickhouseStatement.close();
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
   }

    @Test(priority = 0, description = "Send kafka message to datalayer and verify the persistence results", dataProvider = "connector-test-data-provider", dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send kafka message to datalayer verify persistence results")
    @Story("realtime persistence")
   public void checkRealtimePersistence(Map<String, String> paramMaps){

       // clear clickhouse data in cluster
        String sql = paramMaps.get("sql");
        cleanDataSource(sql);
      // send kafka message to server
        KafkaProducer<String, String> producer = KafkaUtil.createProducer(kafkaServer);
        String message = "{\"process\":\"realtime persistence verify\",\"event\":\"success\",\"coreId\":\"1\",\"device\":\"device1\",\"timestamp\":\"2023-1-1\"} ";
        KafkaUtil.send(producer, topic,message);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HashMap<String, String> queryParameters = new HashMap<>();
        queryParameters.put("name",paramMaps.get("entityName"));
        Response response = ConnectorEndpoint.getConceptModelDataByCondition(queryParameters);
        response.prettyPrint();
        List<Map<String,String>> actualResonseData = response.jsonPath().getList("data");
        List<Map<String, String>> expectedResponseData = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            expectedResponseData = mapper.readValue(paramMaps.get("expectedResponseData"), new TypeReference<List<Map<String, String>>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // verify response though connector response
        Assert.assertEquals(actualResonseData,expectedResponseData);

    }

    @Step("Clear data of clickhouse database")
    private void cleanDataSource(String sql) {

        try {
            clickhouseStatement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

