package com.siemens.datalayer.subscriptionmanagement.test;
import com.siemens.datalayer.entitymanagement.test.EntityManagementEndpoint;
import com.siemens.datalayer.utils.AllureEnvironmentPropertiesWriter;
import com.siemens.datalayer.utils.CommonCheckFunctions;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.annotations.Optional;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("SDL Subscription Management")
@Feature("Subscription Endpoint")
public class SubscriptionManagementTests {

    static HashMap<String, String> queryParamters;
    static Map<String, String> TestSubscriptionIdList;
    private static String id;
    @Parameters({"base_url", "port"})
    @BeforeClass(description = "Configure the host address and communication port of iEMS Subscription management")
    public void setSubscriptionManagementEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("31111") String port) {

        queryParamters = new HashMap<>();
        TestSubscriptionIdList = new HashMap<>();
        SubscriptionManagementEndpoint.setBaseUrl(base_url);
        SubscriptionManagementEndpoint.setPort(port);
        AllureEnvironmentPropertiesWriter.addEnvironmentItem("iems-subscription-management", base_url + ":" + port);
    }


    @AfterClass (description = "Clean up test subscription")
    public void removeTestSubscriptions()
    {
        TestSubscriptionIdList.forEach((key,value)->{
            queryParamters.put("clientId",key);
            SubscriptionManagementEndpoint.deleteSubscriptionById(queryParamters,id);
        });

    }

    @Test(priority = 0,
            description = "Test subscription management interface: registerSubscription.",
            dataProvider = "subscription-management-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'registerSubscriptions' request to subscription endpoint interface.")
    @Story("Subscription End Point: registerSubscriptions")
    public void registerSubscriptions(Map<String, String> paramMaps){
        String subSentence = paramMaps.get("subSentence");
        queryParamters.put("clientId", paramMaps.get("clientId"));
        Response response = SubscriptionManagementEndpoint.registerSubscriptions(queryParamters, subSentence);

        if (paramMaps.get("description").contains("good request")) {
            id = response.jsonPath().getString("data.id");
            TestSubscriptionIdList.put(queryParamters.get("clientId"),id);
            checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().get("message"), response.jsonPath().getList("data.clients"));
            checkDataFollowsModelSchema(paramMaps.get("name"), response);
        } else {
            checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().get("message"), response.jsonPath().getString("data"));
        }
    }


    @Test( dependsOnMethods = { "registerSubscriptions" }, alwaysRun = true,
            priority = 0,
            description = "Test subscription management interface: getSubscriptions.",
            dataProvider = "subscription-management-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'getSubscriptions' request to subscription endpoint interface.")
    @Story("Subscription End Point: getSubscriptions")
    public void getSubscriptions(Map<String, String> paramMaps){

        Response response = SubscriptionManagementEndpoint.getSubscriptions();
        checkStatusCode(paramMaps, response.getStatusCode());
        List<HashMap<String, String>> rspDataList;
        String listPath = "";
        rspDataList = response.jsonPath().getList(listPath);
        CommonCheckFunctions.checkDataContainsSpecifiedFields(listPath, paramMaps.get("fields"), rspDataList);
    }

    @Test( dependsOnMethods = { "registerSubscriptions","getSubscriptions" }, alwaysRun = true,
            priority = 0,
            description = "Test subscription management interface: delSubscriptionById.",
            dataProvider = "subscription-management-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'delSubscriptionById' request to subscription endpoint interface.")
    @Story("Subscription End Point: delSubscriptionById")
    public void delSubscriptionById(Map<String, String> paramMaps){

        String subscriptionId;
        subscriptionId = paramMaps.get("id");
        if(subscriptionId != null){
            queryParamters.put("clientId", paramMaps.get("clientId"));
            Response response = SubscriptionManagementEndpoint.deleteSubscriptionById(queryParamters, subscriptionId);
            checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().get("message"), response.jsonPath().getString("data"));
        } else{
            queryParamters.put("clientId", paramMaps.get("clientId"));
            Response response = SubscriptionManagementEndpoint.deleteSubscriptionById(queryParamters, id);
            checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().get("message"), response.jsonPath().getString("data"));
            checkDataFollowsModelSchema(paramMaps.get("name"), response);
            if(response.jsonPath().getString("message").contains("Successfully")){
                TestSubscriptionIdList.remove(paramMaps.get("clientId"));
            }
        }

    }

    @Step("verify status code, operation code, message and data clients")
    public static void checkResponseCode(Map<String, String> requestParemeters, int actualStatusCode, String actualCode, String actualMessage, List<String> actualData) {
        checkStatusCode(requestParemeters,actualStatusCode);
        checkResponseCodeAndMessage(requestParemeters,actualCode,actualMessage);
        if (requestParemeters.containsKey("rspData")) {
            String[] expDataClients = requestParemeters.get("rspData").split(",");
            assertThat(actualData, hasItems(expDataClients));
        }
    }

    @Step("verify status code, operation code, message and data")
    public static void checkResponseCode(Map<String, String> requestParemeters, int actualStatusCode, String actualCode, String actualMessage, Object actualData) {
        checkStatusCode(requestParemeters,actualStatusCode);
        checkResponseCodeAndMessage(requestParemeters,actualCode,actualMessage);
        if (requestParemeters.containsKey("rspData")) {
            String expData = requestParemeters.get("rspData");
            assertThat(String.valueOf(actualData), equalTo(expData));
        }
    }

    public static void checkStatusCode(Map<String, String> requestParemeters, int actualStatusCode){
        int expStatusCode = 200;
        if (requestParemeters.containsKey("rspStatus"))
            expStatusCode = Integer.valueOf(requestParemeters.get("rspStatus")).intValue();
        Assert.assertEquals(actualStatusCode, expStatusCode, "The status code in response message matches the expected value.");
    }

    public static void checkResponseCodeAndMessage(Map<String, String> requestParemeters, String actualCode, String actualMessage){
        if (requestParemeters.containsKey("rspCode")) {
            Assert.assertEquals(actualCode, requestParemeters.get("rspCode"), "The operation code in response message matches the expected value.");
        }
        if (requestParemeters.containsKey("rspMessage")) {
            Assert.assertTrue(actualMessage.contains(requestParemeters.get("rspMessage")), "The operation message contains the expected content.");
        }
    }

    @Step("check response data match JsonSchema template")
    public void checkDataFollowsModelSchema(String schemaName, Response response) {
        String schemaTemplateFile = SubscriptionManagementEndpoint.getResourcePath() + "JasonModelSchemaFor" + schemaName + ".JSON";
        CommonCheckFunctions.verifyIfDataMatchesJsonSchemaTemplate(schemaTemplateFile, response.getBody().asString());
    }

}


