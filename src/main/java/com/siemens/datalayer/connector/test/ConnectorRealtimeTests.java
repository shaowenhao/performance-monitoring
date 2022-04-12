package com.siemens.datalayer.connector.test;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.siemens.datalayer.iot.util.JdbcMongodbUtil;
import com.siemens.datalayer.iot.util.KafkaUtil;
import com.siemens.datalayer.utils.AllureEnvironmentPropertiesWriter;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.bson.Document;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 1、测试接口范围包括connector-realtime-"Plugin Controller"/"Plugin Toolkit Controller"两部分
 * 2、覆盖功能：
 * 1）real-time plugin
 * 2）数据源->clickhouse的实时数据通路
 * */

@Epic("SDL Connector Realtime")
@Feature("'CORE-Data connector realtime")
public class ConnectorRealtimeTests {

    private String mongodbHost;
    private String mongodbPort;
    private String mongodbUsername;
    private String mongodbPassword;
    private String mongodbDatabasename;

    private String kafkaServers = "140.231.89.85:30962";
    private String kafkaTopic = "topicForRealtimePlugin";
    // private String messageSentToKafka;

    @Parameters({"base_url", "port",
                 "mongodb_host","mongodb_port","mongodb_username","mongodb_password","mongodb_databasename"})
    @BeforeClass(description = "Configure the host address and communication port of data-layer-connector")
    public void setConnectorOtherInterfacesEndpoint(
            @Optional("http://localhost") String base_url,@Optional("9001") String port,
            @Optional("") String mongodb_host,@Optional("") String mongodb_port,
            @Optional("") String mongodb_username,@Optional("") String mongodb_password,
            @Optional("") String mongodb_databasename
    ) {
        ConnectorRealtimeEndpoint.setBaseUrl(base_url);
        ConnectorRealtimeEndpoint.setPort(port);

        mongodbHost = mongodb_host;
        mongodbPort = mongodb_port;
        mongodbUsername = mongodb_username;
        mongodbPassword = mongodb_password;
        mongodbDatabasename = mongodb_databasename;

        AllureEnvironmentPropertiesWriter.addEnvironmentItem("data-layer-connector-realtime", base_url + ":" + port);
        AllureEnvironmentPropertiesWriter.addEnvironmentItem("MongoDB_url", mongodbHost + ":" + mongodbPort );
    }

    @Test(  alwaysRun = true,
            priority = 0,
            description = "Test Connector-realtime Plugin Controller: findAllPlugins")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'findAllPlugins' request to plugin controller interface.")
    @Story("Plugin Controller: findAllPlugins")
    public void findAllPlugins()
    {
        Response response = ConnectorRealtimeEndpoint.findAllPlugins();

        Map<String,String> mongodbParams = new HashMap<>();
        mongodbParams.put("mongodbHost",mongodbHost);
        mongodbParams.put("mongodbPort",mongodbPort);
        mongodbParams.put("mongodbUsername",mongodbUsername);
        mongodbParams.put("mongodbPassword",mongodbPassword);
        mongodbParams.put("mongodbDatabasename",mongodbDatabasename);

        checkNumberOfPlugins(mongodbParams,response);
    }

    @Test(  alwaysRun = true,
            priority = 0,
            description = "Test Connector-realtime Plugin Controller: loadBundle",
            dataProvider = "connector-realtime-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'loadBundle' request to plugin controller interface.")
    @Story("Plugin Controller: loadBundle")
    public void loadBundle(Map<String, String> paramMaps)
    {
        // If the same plugin is present,unload it first by call at /plugin/{pluginId}
        // If there is any driver running with the same driver type,stop it first by call api at plugin/{pluginId}
        prepareEnvironment(paramMaps);

        if (paramMaps.containsKey("pre-executionOfLoadBundle"))
        {
            List<Map<String,String>> preRequestParamsList = null;
            try {
                preRequestParamsList = (new ObjectMapper()).readValue(paramMaps.get("pre-executionOfLoadBundle"), new TypeReference<List<Map<String, String>>>() {});
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            for (Map<String,String> preRequestParams : preRequestParamsList)
            {
                File file = null;
                if (preRequestParams.containsKey("file"))
                    file = new File("src/main/resources/plugin/" + preRequestParams.get("file"));
                Response responseOfLoadBundle = ConnectorRealtimeEndpoint.loadBundle(file);
            }
        }

        File file = null;
        if (paramMaps.containsKey("file"))
            file = new File("src/main/resources/plugin/" + paramMaps.get("file"));

        Response response = ConnectorRealtimeEndpoint.loadBundle(file);
        System.out.println(response.jsonPath().prettify());

        checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));

        if (paramMaps.get("description").contains("good request"))
        {
            Map<String,String> mongodbParams = new HashMap<>();
            mongodbParams.put("mongodbHost",mongodbHost);
            mongodbParams.put("mongodbPort",mongodbPort);
            mongodbParams.put("mongodbUsername",mongodbUsername);
            mongodbParams.put("mongodbPassword",mongodbPassword);
            mongodbParams.put("mongodbDatabasename",mongodbDatabasename);

            // 校验load的plugin在mongodb中的存储情况
            checkPluginState(mongodbParams,paramMaps);
        }
    }

    @Test(  alwaysRun = true,
            priority = 0,
            description = "Test Connector-realtime Plugin Controller: pluginOperation",
            dataProvider = "connector-realtime-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'pluginOperation' request to plugin controller interface.")
    @Story("Plugin Controller: pluginOperation")
    public void pluginOperation(Map<String, String> paramMaps)
    {
        // If the same plugin is present,unload it first by call at /plugin/{pluginId}
        // If there is any driver running with the same driver type,stop it first by call api at plugin/{pluginId}
        prepareEnvironment(paramMaps);

        if (paramMaps.containsKey("pre-executionOfLoadBundle"))
        {
            List<Map<String,String>> preRequestParamsList = null;
            try {
                preRequestParamsList = (new ObjectMapper()).readValue(paramMaps.get("pre-executionOfLoadBundle"), new TypeReference<List<Map<String, String>>>() {});
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            
            for (Map<String,String> preRequestParams : preRequestParamsList)
            {
                File file = null;
                if (preRequestParams.containsKey("file"))
                    file = new File("src/main/resources/plugin/" + preRequestParams.get("file"));
                Response responseOfLoadBundle = ConnectorRealtimeEndpoint.loadBundle(file);
                System.out.println("responseOfLoadBundle: " + responseOfLoadBundle.jsonPath().prettify());
            }
        }

        if (paramMaps.containsKey("pre-executionOfPluginOperation"))
        {
            List<Map<String,String>> preRequestParamsList = null;
            try {
                preRequestParamsList = (new ObjectMapper()).readValue(paramMaps.get("pre-executionOfPluginOperation"), new TypeReference<List<Map<String, String>>>() {});
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            for (Map<String,String> preRequestParams : preRequestParamsList)
            {
                Response responseOfPluginOperation = ConnectorRealtimeEndpoint.pluginOperation(preRequestParams.get("operator"),preRequestParams.get("pluginId"));
                System.out.println("responseOfPluginOperation: "+ responseOfPluginOperation);
            }
        }

        Response response = ConnectorRealtimeEndpoint.pluginOperation(paramMaps.get("operator"),paramMaps.get("pluginId"));
        System.out.println(response.jsonPath().prettify());

        checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));

        if (paramMaps.get("description").contains("good request"))
        {
            Map<String,String> mongodbParams = new HashMap<>();
            mongodbParams.put("mongodbHost",mongodbHost);
            mongodbParams.put("mongodbPort",mongodbPort);
            mongodbParams.put("mongodbUsername",mongodbUsername);
            mongodbParams.put("mongodbPassword",mongodbPassword);
            mongodbParams.put("mongodbDatabasename",mongodbDatabasename);

            // 校验load的plugin在mongodb中的存储情况
            checkPluginState(mongodbParams,paramMaps);
        }
    }

    @Test(  alwaysRun = true,
            priority = 0,
            description = "Test Connector-realtime Plugin Toolkit Controller: get Plugin Information",
            dataProvider = "connector-realtime-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'get Plugin Information' request to Plugin Toolkit Controller interface.")
    @Story("Plugin Toolkit Controller: get Plugin Information")
    public void getPluginInformation(Map<String, String> paramMaps)
    {
        // If the same plugin is present,unload it first by call at /plugin/{pluginId}
        // If there is any driver running with the same driver type,stop it first by call api at plugin/{pluginId}
        prepareEnvironment(paramMaps);

        if (paramMaps.containsKey("pre-executionOfLoadBundle"))
        {
            List<Map<String,String>> preRequestParamsList = null;
            try {
                preRequestParamsList = (new ObjectMapper()).readValue(paramMaps.get("pre-executionOfLoadBundle"), new TypeReference<List<Map<String, String>>>() {});
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            for (Map<String,String> preRequestParams : preRequestParamsList)
            {
                File file = null;
                if (preRequestParams.containsKey("file"))
                    file = new File("src/main/resources/plugin/" + preRequestParams.get("file"));
                Response responseOfLoadBundle = ConnectorRealtimeEndpoint.loadBundle(file);
                System.out.println("responseOfLoadBundle: " + responseOfLoadBundle.jsonPath().prettify());
            }
        }

        if (paramMaps.containsKey("pre-executionOfPluginOperation"))
        {
            List<Map<String,String>> preRequestParamsList = null;
            try {
                preRequestParamsList = (new ObjectMapper()).readValue(paramMaps.get("pre-executionOfPluginOperation"), new TypeReference<List<Map<String, String>>>() {});
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            for (Map<String,String> preRequestParams : preRequestParamsList)
            {
                Response responseOfPluginOperation = ConnectorRealtimeEndpoint.pluginOperation(preRequestParams.get("operator"),preRequestParams.get("pluginId"));
                System.out.println("responseOfPluginOperation: " + responseOfPluginOperation.jsonPath().prettify());
            }
        }

        Response response = ConnectorRealtimeEndpoint.getPluginInformation(paramMaps.get("pluginId"));
        System.out.println(response.jsonPath().prettify());

        checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));

        if (paramMaps.get("description").contains("good request"))
        {
            Map<String,String> mongodbParams = new HashMap<>();
            mongodbParams.put("mongodbHost",mongodbHost);
            mongodbParams.put("mongodbPort",mongodbPort);
            mongodbParams.put("mongodbUsername",mongodbUsername);
            mongodbParams.put("mongodbPassword",mongodbPassword);
            mongodbParams.put("mongodbDatabasename",mongodbDatabasename);

            checkPluginInformation(mongodbParams,paramMaps,response);
        }
    }

    @Test(  alwaysRun = true,
            priority = 0,
            description = "Test Connector-realtime Plugin Toolkit Controller: Plugin Instance Start",
            dataProvider = "connector-realtime-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'Plugin Instance Start' request to Plugin Toolkit Controller interface.")
    @Story("Plugin Toolkit Controller: Plugin Instance Start")
    public void pluginInstanceStart(Map<String, String> paramMaps)
    {
        // If the same plugin is present,unload it first by call at /plugin/{pluginId}
        // If there is any driver running with the same driver type,stop it first by call api at plugin/{pluginId}
        prepareEnvironment(paramMaps);

        if (paramMaps.containsKey("pre-executionOfLoadBundle"))
        {
            List<Map<String,String>> preRequestParamsList = null;
            try {
                preRequestParamsList = (new ObjectMapper()).readValue(paramMaps.get("pre-executionOfLoadBundle"), new TypeReference<List<Map<String, String>>>() {});
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            for (Map<String,String> preRequestParams : preRequestParamsList)
            {
                File file = null;
                if (preRequestParams.containsKey("file"))
                    file = new File("src/main/resources/plugin/" + preRequestParams.get("file"));
                Response responseOfLoadBundle = ConnectorRealtimeEndpoint.loadBundle(file);
            }
        }

        if (paramMaps.containsKey("pre-executionOfPluginOperation"))
        {
            List<Map<String,String>> preRequestParamsList = null;
            try {
                preRequestParamsList = (new ObjectMapper()).readValue(paramMaps.get("pre-executionOfPluginOperation"), new TypeReference<List<Map<String, String>>>() {});
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            for (Map<String,String> preRequestParams : preRequestParamsList)
            {
                Response responseOfPluginOperation = ConnectorRealtimeEndpoint.pluginOperation(preRequestParams.get("operator"),preRequestParams.get("pluginId"));
            }
        }

        Response response = ConnectorRealtimeEndpoint.pluginInstanceStart(paramMaps.get("connectorName"),paramMaps.get("locatorName"),paramMaps.get("stopTime"));
        System.out.println(response.jsonPath().prettify());

        checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));
    }

    @Test(  alwaysRun = true,
            priority = 0,
            description = "Test Connector-realtime Plugin Toolkit Controller: plugin Running Log Information",
            dataProvider = "connector-realtime-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'plugin Running Log Information' request to Plugin Toolkit Controller interface.")
    @Story("Plugin Toolkit Controller: plugin Running Log Information")
    public void pluginRunningLogInformation(Map<String, String> paramMaps)
    {
        // If the same plugin is present,unload it first by call at /plugin/{pluginId}
        // If there is any driver running with the same driver type,stop it first by call api at plugin/{pluginId}
        prepareEnvironment(paramMaps);

        if (paramMaps.containsKey("pre-executionOfLoadBundle"))
        {
            List<Map<String,String>> preRequestParamsList = null;
            try {
                preRequestParamsList = (new ObjectMapper()).readValue(paramMaps.get("pre-executionOfLoadBundle"), new TypeReference<List<Map<String, String>>>() {});
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            for (Map<String,String> preRequestParams : preRequestParamsList)
            {
                File file = null;
                if (preRequestParams.containsKey("file"))
                    file = new File("src/main/resources/plugin/" + preRequestParams.get("file"));
                Response responseOfLoadBundle = ConnectorRealtimeEndpoint.loadBundle(file);
            }
        }

        if (paramMaps.containsKey("pre-executionOfPluginOperation"))
        {
            List<Map<String,String>> preRequestParamsList = null;
            try {
                preRequestParamsList = (new ObjectMapper()).readValue(paramMaps.get("pre-executionOfPluginOperation"), new TypeReference<List<Map<String, String>>>() {});
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            for (Map<String,String> preRequestParams : preRequestParamsList)
            {
                Response responseOfPluginOperation = ConnectorRealtimeEndpoint.pluginOperation(preRequestParams.get("operator"),preRequestParams.get("pluginId"));
            }
        }

        if (paramMaps.containsKey("pre-executionOfPluginInstanceStart"))
        {
            List<Map<String,String>> preRequestParamsList = null;
            try {
                preRequestParamsList = (new ObjectMapper()).readValue(paramMaps.get("pre-executionOfPluginInstanceStart"), new TypeReference<List<Map<String, String>>>() {});
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            for (Map<String,String> preRequestParams : preRequestParamsList)
            {
                Response responseOfPluginInstanceStart = ConnectorRealtimeEndpoint.pluginInstanceStart(paramMaps.get("connectorName"),
                        paramMaps.get("locatorName"),paramMaps.get("stopTime"));
            }
        }

        Response response = ConnectorRealtimeEndpoint.pluginRunningLogInformation(paramMaps.get("connectorName"), paramMaps.get("locatorName"));
        System.out.println(response.jsonPath().prettify());

        sendMessageToKafka(kafkaServers,kafkaTopic);
    }

    @Step("prepare test environment")
    public static void prepareEnvironment(Map<String, String> requestParameter)
    {
        Response responseOfFindAllPlugins = ConnectorRealtimeEndpoint.findAllPlugins();

        List<Map<String,String>> allPluginsList = responseOfFindAllPlugins.jsonPath().getList("data");
        // List<String> allPluginIdList = allPluginsList.stream().map(e -> e.get("pluginId")).collect(Collectors.toList());
        // List<String> allDriverTypeList = allPluginsList.stream().map(e -> e.get("driverType")).collect(Collectors.toList());

        String requestPluginId = null;
        String requestDriverType = null;

        if (requestParameter.containsKey("pluginId"))
            requestPluginId = requestParameter.get("pluginId");

        if (requestParameter.containsKey("driverType"))
            requestDriverType = requestParameter.get("driverType");

        Response responseOfPluginUnload;

        Response responseOfPluginStop;

        for (Iterator<Map<String,String>> iter = allPluginsList.iterator(); iter.hasNext();)
        {
            Map<String,String> pluginItem = iter.next();

            // If the same plugin is present,unload it first by call at /plugin/{pluginId}
            if (pluginItem.get("pluginId") != null && pluginItem.get("pluginId").equals(requestPluginId))
            {
                responseOfPluginUnload = ConnectorRealtimeEndpoint.pluginOperation("UNLOAD",requestPluginId);
                System.out.println("unload the same plugin: \n" + responseOfPluginUnload.jsonPath().prettify());
            }

            // If there is any driver running with the same driver type,stop it first by call api at plugin/{pluginId}
            if (pluginItem.get("driverType") != null && pluginItem.get("driverType").equals(requestDriverType))
            {
                if (!pluginItem.get("pluginId").equals(requestPluginId))
                {
                    responseOfPluginStop = ConnectorRealtimeEndpoint.pluginOperation("STOP",pluginItem.get("pluginId"));
                    System.out.println("stop the same driver type: \n" + responseOfPluginStop.jsonPath().prettify());
                }
            }
        }
    }

    @Step("Verify the status code, operation code, and message")
    public static void checkResponseCode(Map<String, String> requestParameters, int actualStatusCode, String actualCode, String actualMessage)
    {
        // 校验http返回状态码
        int expStatusCode = 200;	// If not specified, the expected status code is set to 200 (OK)
        if (requestParameters.containsKey("rspStatus")) expStatusCode = Integer.valueOf(requestParameters.get("rspStatus")).intValue();

        Assert.assertEquals(actualStatusCode, expStatusCode, "The status code in response message matches the expected value.");

        // 校验Response body-code
        if ((requestParameters.containsKey("rspCode")))
        {
            if (requestParameters.get("rspCode").contains("null"))
                Assert.assertNull(actualCode, "No operation code is found.");
            else
                Assert.assertEquals(actualCode, requestParameters.get("rspCode"), "The operation code in response message matches the expected value.");
        }

        // 校验Response body-message
        if (requestParameters.containsKey("rspMessage"))
        {
            if (requestParameters.get("rspMessage").contains("null"))
                Assert.assertNull(actualMessage, "The content of operation message is null.");
            else
                Assert.assertTrue(actualMessage.contains(requestParameters.get("rspMessage")), "The operation message contains the expected content.");
        }
    }

    @Step("Verify the plugin pluginState in mongodb")
    public static void checkPluginState(Map<String, String> mongodbParams,Map<String, String> requestParameters)
    {
        // 调用JdbcMongodbUtil.getConnect方法返回连接数据库对象
        MongoDatabase mongoDatabase = JdbcMongodbUtil.getConnect(mongodbParams.get("mongodbHost"),
                                                                 Integer.valueOf(mongodbParams.get("mongodbPort")).intValue(),
                                                                 mongodbParams.get("mongodbUsername"),
                                                                 mongodbParams.get("mongodbPassword"),
                                                                 mongodbParams.get("mongodbDatabasename"));

        // 获取集合Plugin（pluginsCollection）
        MongoCollection<Document> pluginsCollection = mongoDatabase.getCollection("Plugin");

        // 获取目标plugin
        Document pluginFromMongo = pluginsCollection.find(Filters.eq("pluginId",requestParameters.get("pluginId"))).first();

        if (requestParameters.containsKey("pluginState"))
        {
            Assert.assertNotNull(pluginFromMongo);
            Assert.assertEquals(pluginFromMongo.get("pluginState"),requestParameters.get("pluginState"));
        }
        else
            Assert.assertNull(pluginFromMongo);
    }

    @Step("Verify the number of plugins")
    private static void checkNumberOfPlugins(Map<String, String> mongodbParams, Response response)
    {
        // 调用JdbcMongodbUtil.getConnect方法返回连接数据库对象
        MongoDatabase mongoDatabase = JdbcMongodbUtil.getConnect(mongodbParams.get("mongodbHost"),
                                                                 Integer.valueOf(mongodbParams.get("mongodbPort")).intValue(),
                                                                 mongodbParams.get("mongodbUsername"),
                                                                 mongodbParams.get("mongodbPassword"),
                                                                 mongodbParams.get("mongodbDatabasename"));

        // 获取集合Plugin（pluginsCollection）
        MongoCollection<Document> pluginsCollection = mongoDatabase.getCollection("Plugin");

        // 查找集合中的所有文档,并对其进行遍历，存放至expectedPluginsList中
        FindIterable<Document> findIterable = pluginsCollection.find();
        MongoCursor<Document> mongoCursor = findIterable.iterator();

        List<Map<String,String>> expectedPluginsList = new ArrayList<>();

        while (mongoCursor.hasNext())
        {
            Document document = mongoCursor.next();
            // document.toJson() 将document转换成json
            // JSON.parseObject(document.toJson(),Map.class) 将json转换成Map
            Map<String,String> expectedPlugin = JSON.parseObject(document.toJson(),Map.class);
            expectedPluginsList.add(expectedPlugin);
        }

        List<Map<String,String>> actualPluginsList = response.jsonPath().getList("data");

        System.out.println("actualPluginsList.size(): " + actualPluginsList.size());
        System.out.println("expectedPluginsList.size(): " + expectedPluginsList.size());
        Assert.assertEquals(actualPluginsList.size(),expectedPluginsList.size());
    }

    @Step("Verify the plugin information")
    private static void checkPluginInformation(Map<String, String> mongodbParams,Map<String, String> requestParameters,Response response)
    {
        // 调用JdbcMongodbUtil.getConnect方法返回连接数据库对象
        MongoDatabase mongoDatabase = JdbcMongodbUtil.getConnect(mongodbParams.get("mongodbHost"),
                                                                 Integer.valueOf(mongodbParams.get("mongodbPort")).intValue(),
                                                                 mongodbParams.get("mongodbUsername"),
                                                                 mongodbParams.get("mongodbPassword"),
                                                                 mongodbParams.get("mongodbDatabasename"));

        // 获取集合Plugin（pluginsCollection）
        MongoCollection<Document> pluginsCollection = mongoDatabase.getCollection("Plugin");

        // 获取目标plugin
        Document pluginFromMongo = pluginsCollection.find(Filters.eq("pluginId",requestParameters.get("pluginId"))).first();

        Map<String,String> actualPlugin = response.jsonPath().getMap("data");
        for (Map.Entry<String,String> entry : actualPlugin.entrySet())
        {
            // 判断接口返回的类型，跟mongodb中存储的类型是否相等
            if (pluginFromMongo.containsKey(entry.getKey()) && entry.getValue().getClass() == pluginFromMongo.get(entry.getKey()).getClass()){
                System.out.println(entry.getKey() + ": " + entry.getValue());
                Assert.assertEquals(entry.getValue(),pluginFromMongo.get(entry.getKey()));}
        }
    }

    private static void sendMessageToKafka(String servers,String topic)
    {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        String message = dateTimeFormatter.format(localDateTime);

        KafkaProducer<String,String> producer = KafkaUtil.createProducer(servers);
        KafkaUtil.send(producer,topic, message);
    }
}
