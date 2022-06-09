package com.siemens.datalayer.connector.test;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.siemens.datalayer.iot.util.JdbcMongodbUtil;
import com.siemens.datalayer.utils.AllureEnvironmentPropertiesWriter;

import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;

import org.bson.Document;
import org.testng.Assert;
import org.testng.annotations.*;

import io.restassured.response.Response;
import io.restassured.path.json.JsonPath;
import org.testng.annotations.Optional;

import java.util.*;

@Epic("SDL Connector configure")
@Feature("Rest api")
public class ConnectorConfigureTests {
    static List<String> connectorNamesList;
    private String mongodbHost;
    private String mongodbPort;
    private String mongodbUsername;
    private String mongodbPassword;
    private String mongodbDatabasename;

    @Parameters({"base_url", "port", "domain_name","mongodb_host","mongodb_port","mongodb_username","mongodb_password","mongodb_databasename"})
    @BeforeClass(description = "Configure the host address and communication port of data-layer-connector")
    public void setConnectorConfigureEndpoint(@Optional("http://localhost") String base_url, @Optional("9001") String port, String domain_name,
                                              @Optional("") String mongodb_host,@Optional("") String mongodb_port,
                                              @Optional("") String mongodb_username,@Optional("") String mongodb_password,
                                              @Optional("") String mongodb_databasename){
        connectorNamesList = new ArrayList<>();

        ConnectorConfigureEndpoint.setBaseUrl(base_url);
        ConnectorConfigureEndpoint.setPort(port);
        ConnectorConfigureEndpoint.setDomainName(domain_name);

        mongodbHost = mongodb_host;
        mongodbPort = mongodb_port;
        mongodbUsername = mongodb_username;
        mongodbPassword = mongodb_password;
        mongodbDatabasename =mongodb_databasename;

        AllureEnvironmentPropertiesWriter.addEnvironmentItem("data-layer-connector-configure", base_url + ":" + port);
        AllureEnvironmentPropertiesWriter.addEnvironmentItem("MongoDB_url", mongodbHost + ":" + mongodbPort );

        /* List<String> connectorNamesToBeCreatedList = Arrays.asList("TESTCONNECTORREFACTOR","TESTCONNECTORREFACTOR_1");
        for (String connectorName : connectorNamesToBeCreatedList)
        {
            Response response = ConnectorConfigureEndpoint.deleteConnector(connectorName);
        } */
    }


    @Test(  alwaysRun = true,
            priority = 0,
            description = "Test Cache Config Controller: allCacheConfigs")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'getAllCacheConfigs' request to Cache Config controller interface.")
    @Story("Cache Config Controller: allCacheConfigs")
     public void getAllCacheConfigs(){
        Response response = ConnectorConfigureEndpoint.getAllCacheConfigs();
        Map<String,String> mongodbParams = new HashMap<>();
        mongodbParams.put("mongodbHost",mongodbHost);
        mongodbParams.put("mongodbPort",mongodbPort);
        mongodbParams.put("mongodbUsername",mongodbUsername);
        mongodbParams.put("mongodbPassword",mongodbPassword);
        mongodbParams.put("mongodbDatabasename",mongodbDatabasename);
        checkNumberOfCacheConfig(response,mongodbParams);
    }

    @Step ("verify the number of Cache Config")
    private static void checkNumberOfCacheConfig(Response response, Map<String, String> mongodbParams) {

        MongoDatabase mongoDatabase = JdbcMongodbUtil.getConnect(mongodbParams.get("mongodbHost"),
                Integer.valueOf(mongodbParams.get("mongodbPort")).intValue(),
                mongodbParams.get("mongodbUsername"),
                mongodbParams.get("mongodbPassword"),
                mongodbParams.get("mongodbDatabasename"));
        MongoCollection<Document> cacheConfigCollection = mongoDatabase.getCollection("CacheConfig");
        FindIterable<Document> findIterable = cacheConfigCollection.find();
        MongoCursor<Document> iterator = findIterable.iterator();
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String,Object>> expectedCacheConfigList = new ArrayList<>();

        while (iterator.hasNext()){
            Document document = iterator.next();
            String documentJson = document.toJson();
            try {
                Map<String, Object> expectedCacheConfig = mapper.readValue(documentJson, new TypeReference<Map<String, Object>>() {
                });
                expectedCacheConfigList.add(expectedCacheConfig);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        }
        List<Map<String,String>> actualCacheConfigList = response.jsonPath().getList("data");
        Assert.assertEquals(actualCacheConfigList.size(),expectedCacheConfigList.size());
    }

    /* @AfterClass(description = "clean up test connectors,locators...")
    public void removeTestData()
    {
        if (connectorNamesList.size() > 0)
        {
            for (String connectorName : connectorNamesList){
                Response response = ConnectorConfigureEndpoint.deleteConnector(connectorName);
            }
        }
    } */

  /*  @Test(priority = 0, description = "Test Developer Tools:clear all cache.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'clear all cache' request")
    @Story("Test Developer Tools:clear all cache")
    public void clearRedisCaches()
    {
        Response response = ConnectorConfigureEndpoint.clearAllCache();
        JsonPath jsonPath = response.jsonPath();

        Assert.assertEquals("Operate success.",jsonPath.getString("message"));
    }
  */
    /* @Test(priority = 0, description = "Test connector-domain-controller:getAllConnectors.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'getAllConnectors' request to read out all the available connectors names.")
    @Story("connector-domain-controller:getAllConnectors")
    public void getAllConnectors()
    {
        Response response = ConnectorConfigureEndpoint.getAllConnectors();
        JsonPath jsonPath = response.jsonPath();

        Assert.assertEquals("Operate success.",jsonPath.getString("message"));

        String path = "data.types.ALL";
        System.out.println("len of 'data.types.ALL' is: " + jsonPath.getList(path).size());
        Assert.assertTrue(jsonPath.getList(path).size() > 0);
    }

    @Test (	priority = 0,
            description = "Test connector-domain-controller:save connector.",
            dataProvider = "connector-configure-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'save connector' request with specified parameters and check the response message.")
    @Story("Test connector-domain-controller:save connector")
    public void saveConnector(Map<String,String> paramMaps)
    {
        String bodyString = paramMaps.get("body");

        Response response = ConnectorConfigureEndpoint.saveConnector(bodyString);

        // check the response message
        checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));

        if (paramMaps.get("description").contains("good request"))
        {
            connectorNamesList.add(response.jsonPath().getString("data.connectorName"));
            System.out.println("connectors been created for test: " + connectorNamesList);

            // check the connector that been created above,can be found in "getAllConnectors"
            Response responseOfGetAllConnectors = ConnectorConfigureEndpoint.getAllConnectors();
            String pathOfGetAllConnectors = "data.types.ALL";
            List<Map<String,String>> actualConnectorsList = responseOfGetAllConnectors.jsonPath().getList(pathOfGetAllConnectors);

            List<String> actualConnectorNamesList = new ArrayList<>();
            for (Map<String,String> actualConnector : actualConnectorsList)
            {
                actualConnectorNamesList.add(actualConnector.get("connectorName"));
            }

            Gson gson = new Gson();
            Map<String,String> bodyMap = new HashMap<>();
            bodyMap = gson.fromJson(paramMaps.get("body"),bodyMap.getClass());
            String expConnectorName = bodyMap.get("connectorName");

            Assert.assertTrue(actualConnectorNamesList.contains(expConnectorName));
        }
    }


    @Test(  dependsOnMethods = { "saveConnector" }, alwaysRun = true,
            priority = 0,
            description = "Test connector-domain-controller:getConnector")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'getConnector' request to connector-domain-controller with connectorName that used in test 'saveConnector'")
    @Story("connector-domain-controller:getConnector")
    public void getConnector()
    {
        for (int i=0;i<connectorNamesList.size();i++)
        {
            Response response = ConnectorConfigureEndpoint.getConnector(connectorNamesList.get(i));

            Assert.assertEquals("Operate success.",response.jsonPath().getString("message"));

            System.out.println("connectorNamesList.get(" + i + "): "+connectorNamesList.get(i));
            Assert.assertEquals(connectorNamesList.get(i),response.jsonPath().getString("data.connectorName"));
        }
    }

    @Test (	dependsOnMethods = {"saveConnector"},alwaysRun = true,
            priority = 0,
            description = "Test connector-domain-controller:updateConnector.",
            dataProvider = "connector-configure-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'updateConnector' request with specified parameters and check the response message.")
    @Story("Test connector-domain-controller:updateConnector")
    public void updateConnector(Map<String,String> paramMaps){
        String connectorName = paramMaps.get("connectorName");
        String bodyString = paramMaps.get("body");
        Response response = ConnectorConfigureEndpoint.updateConnector(connectorName,bodyString);

        checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));
    }

    @Test (	dependsOnMethods = {"saveConnector","updateConnector","getConnector"},alwaysRun = true,
            priority = 0,
            description = "Test connector-domain-controller:deleteConnector.",
            dataProvider = "connector-configure-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'deleteConnector' request with specified parameters and check the response message.")
    @Story("Test connector-domain-controller:deleteConnector")
    public void deleteConnector(Map<String,String> paramMaps)
    {
        Response response = ConnectorConfigureEndpoint.deleteConnector(paramMaps.get("connectorName"));

        checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));

        if (paramMaps.get("description").contains("good request"))
        {
            Response responseOfGetAllConnectors = ConnectorConfigureEndpoint.getAllConnectors();
            String pathOfGetAllConnectors = "data.types.ALL";
            List<Map<String,String>> actualConnectorsList = responseOfGetAllConnectors.jsonPath().getList(pathOfGetAllConnectors);

            List<String> actualConnectorNamesList = new ArrayList<>();
            for (Map<String,String> actualConnector : actualConnectorsList)
            {
                actualConnectorNamesList.add(actualConnector.get("connectorName"));
            }

            Assert.assertFalse(actualConnectorNamesList.contains(paramMaps.get("connectorName")));
            System.out.println(paramMaps.get("connectorName") + " has been deleted");
        }
    } */

    @Step("Verify the status code, operation code, and message")
    public static void checkResponseCode(Map<String, String> requestParameters, int actualStatusCode, String actualCode, String actualMessage)
    {
        int expStatusCode = 200;	// If not specified, the expected status code is set to 200 (OK)
        if (requestParameters.containsKey("rspStatus")) expStatusCode = Integer.valueOf(requestParameters.get("rspStatus")).intValue();
        Assert.assertEquals(actualStatusCode, expStatusCode, "The status code in response message matches the expected value.");

        if ((requestParameters.containsKey("rspCode")))
        {
            Assert.assertEquals(actualCode, requestParameters.get("rspCode"), "The operation code in response message matches the expected value.");
        }
        else
        {
            if (requestParameters.get("description").contains("good request"))
                Assert.assertEquals(actualCode, "0", "The operation code in response message matches the expected value.");
            else
                System.out.println("Operation code is not specified for test case： " + requestParameters.get("test-id"));
        }

        if (requestParameters.containsKey("rspMessage"))
        {
            Assert.assertTrue(actualMessage.contains(requestParameters.get("rspMessage")), "The operation message contains the expected content.");
        }
        else
        {
            if (requestParameters.get("description").contains("good request"))
                Assert.assertEquals(actualMessage, "Operate success.", "The message of 'operation success' is returned.");
            else
                System.out.println("Operation message is not specified for test case： " + requestParameters.get("test-id"));
        }
    }
}
