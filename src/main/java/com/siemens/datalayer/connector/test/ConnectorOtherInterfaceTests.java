package com.siemens.datalayer.connector.test;

import com.google.gson.Gson;
import com.siemens.datalayer.utils.AllureEnvironmentPropertiesWriter;

import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;

import org.json.JSONException;
import org.testng.Assert;
import org.testng.annotations.*;

import io.restassured.response.Response;
import io.restassured.path.json.JsonPath;
import org.testng.annotations.Optional;

import java.util.*;

@Epic("SDL Connector")
@Feature("Rest api except 'Connector Interface'")
public class ConnectorOtherInterfaceTests {
    static List<String> connectorNamesList;

    @Parameters({"base_url", "port", "domain_name"})
    @BeforeClass(description = "Configure the host address and communication port of data-layer-connector")
    public void setConnectorOtherInterfaceEndpoint(@Optional("http://localhost") String base_url,@Optional("9001") String port,String domain_name){
        connectorNamesList = new ArrayList<>();

        ConnectorOtherInterfaceEndpoint.setBaseUrl(base_url);
        ConnectorOtherInterfaceEndpoint.setPort(port);
        ConnectorOtherInterfaceEndpoint.setDomainName(domain_name);
        AllureEnvironmentPropertiesWriter.addEnvironmentItem("data-layer-connector", base_url + ":" + port);

        List<String> connectorNamesToBeCreatedList = Arrays.asList("TESTCONNECTORREFACTOR","TESTCONNECTORREFACTOR_1");
        for (String connectorName : connectorNamesToBeCreatedList)
        {
            Response response = ConnectorOtherInterfaceEndpoint.deleteConnector(connectorName);
        }
    }

    @AfterClass(description = "clean up test connectors,locators...")
    public void removeTestData()
    {
        if (connectorNamesList.size() > 0)
        {
            for (String connectorName : connectorNamesList){
                Response response = ConnectorOtherInterfaceEndpoint.deleteConnector(connectorName);
            }
        }
    }

    @Test(priority = 0, description = "Test connector-domain-controller:getAllConnectors.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'getAllConnectors' request to read out all the available connectors names.")
    @Story("connector-domain-controller:getAllConnectors")
    public void getAllConnectors()
    {
        Response response = ConnectorOtherInterfaceEndpoint.getAllConnectors();
        JsonPath jsonPath = response.jsonPath();

        Assert.assertEquals("Operate success.",jsonPath.getString("message"));

        String path = "data.types.ALL";
        System.out.println("len of 'data.types.ALL' is: " + jsonPath.getList(path).size());
        Assert.assertTrue(jsonPath.getList(path).size() > 0);
    }

    @Test (	priority = 0,
            description = "Test connector-domain-controller:save connector.",
            dataProvider = "connector-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'save connector' request with specified parameters and check the response message.")
    @Story("Test connector-domain-controller:save connector")
    public void saveConnector(Map<String,String> paramMaps)
    {
        String bodyString = paramMaps.get("body");

        Response response = ConnectorOtherInterfaceEndpoint.saveConnector(bodyString);

        // check the response message
        checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));

        if (paramMaps.get("description").contains("good request"))
        {
            connectorNamesList.add(response.jsonPath().getString("data.connectorName"));
            System.out.println("connectors been created for test: " + connectorNamesList);

            // check the connector that been created above,can be found in "getAllConnectors"
            Response responseOfGetAllConnectors = ConnectorOtherInterfaceEndpoint.getAllConnectors();
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
            Response response = ConnectorOtherInterfaceEndpoint.getConnector(connectorNamesList.get(i));

            Assert.assertEquals("Operate success.",response.jsonPath().getString("message"));

            System.out.println("connectorNamesList.get(" + i + "): "+connectorNamesList.get(i));
            Assert.assertEquals(connectorNamesList.get(i),response.jsonPath().getString("data.connectorName"));
        }
    }

    @Test (	dependsOnMethods = {"saveConnector"},alwaysRun = true,
            priority = 0,
            description = "Test connector-domain-controller:updateConnector.",
            dataProvider = "connector-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'updateConnector' request with specified parameters and check the response message.")
    @Story("Test connector-domain-controller:updateConnector")
    public void updateConnector(Map<String,String> paramMaps){
        String connectorName = paramMaps.get("connectorName");
        String bodyString = paramMaps.get("body");
        Response response = ConnectorOtherInterfaceEndpoint.updateConnector(connectorName,bodyString);

        checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));
    }

    @Test (	dependsOnMethods = {"saveConnector","updateConnector","getConnector"},alwaysRun = true,
            priority = 0,
            description = "Test connector-domain-controller:deleteConnector.",
            dataProvider = "connector-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'deleteConnector' request with specified parameters and check the response message.")
    @Story("Test connector-domain-controller:deleteConnector")
    public void deleteConnector(Map<String,String> paramMaps)
    {
        Response response = ConnectorOtherInterfaceEndpoint.deleteConnector(paramMaps.get("connectorName"));

        checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));

        if (paramMaps.get("description").contains("good request"))
        {
            Response responseOfGetAllConnectors = ConnectorOtherInterfaceEndpoint.getAllConnectors();
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
    }

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
