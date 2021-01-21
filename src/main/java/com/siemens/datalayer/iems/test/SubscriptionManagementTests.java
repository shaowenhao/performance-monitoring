package com.siemens.datalayer.iems.test;
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

@Epic("Subscription Management Interface")
@Feature("REST API")
public class SubscriptionManagementTests {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionManagementTests.class);
    private  String id;
    
    @Parameters({"base_url", "port", "domain_name"})
    @BeforeClass(description = "Configure the host address and communication port of iEMS Subscription management")
    public void setSubscriptionManagementEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("31111") String port, @Optional("iEMS") String domain_name) {
        SubscriptionManagementEndpoint.setBaseUrl(base_url);
        SubscriptionManagementEndpoint.setPort(port);
        SubscriptionManagementEndpoint.setDomain(domain_name);
        AllureEnvironmentPropertiesWriter.addEnvironmentItem("iems-subscription-management", base_url + ":" + port);
        //Before test run clean existed subscriptions
        SubscriptionManagementEndpoint.deleteAllSubscriptions();
    }

   @AfterClass
   public  void cleanExistedSubscriptions(){
        SubscriptionManagementEndpoint.deleteAllSubscriptions();
   }

    @Test(priority = 0,
            description = "Test subscription management interface: Manage subscription.",
            dataProvider = "subscription-management-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send different requests and check the response message.")
    @Story("Subscription management Interface: subscriptions management")
    public void manageSubscriptions(Map<String, String> paramMaps) {

        HashMap<String, String> queryParamters = new HashMap<>();
        String subSentence = paramMaps.get("subSentence");
        String clientId = paramMaps.get("clientId");
        String id = paramMaps.get("id");
        String numOfItems = paramMaps.get("numOfItems");

        if (clientId != null && subSentence != null) {   //根据条件进行case调用
            queryParamters.put("clientId", paramMaps.get("clientId"));
            Response response = SubscriptionManagementEndpoint.registerSubscriptions(queryParamters, subSentence);

            // check response
            if (paramMaps.get("description").contains("good request")) {
                checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().get("message"), response.jsonPath().getList("data.clients"));
            } else {
                checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().get("message"), response.jsonPath().getString("data"));
            }
            if (paramMaps.get("description").contains("data retrieved")) {
                // check with json schema
                checkDataFollowsModelSchema(paramMaps.get("name"), response);
            }
        } else if (subSentence == null && id != null) {
            queryParamters.put("clientId", paramMaps.get("clientId"));
            Response response = SubscriptionManagementEndpoint.deleteSubscriptionById(queryParamters, id);

            if (paramMaps.get("description").contains("good request") || paramMaps.get("description").contains("bad request")) {
                checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().get("message"), response.jsonPath().getString("data"));
            }
            if (paramMaps.get("description").contains("data retrieved")) {
                checkDataFollowsModelSchema(paramMaps.get("name"), response);
            }
        } else if(clientId == null && subSentence == null && id == null && numOfItems ==null){

            if(paramMaps.containsKey("fields")){
                Response response = SubscriptionManagementEndpoint.getSubscriptions();
                if (paramMaps.get("description").contains("good request")) {
                    checkStatusCode(paramMaps, response.getStatusCode());
                }
                if (paramMaps.get("description").contains("data retrieved")) {
                    List<HashMap<String, String>> rspDataList;
                    String listPath = "";
                    rspDataList = response.jsonPath().getList(listPath);
                    CommonCheckFunctions.checkDataContainsSpecifiedFields(listPath, paramMaps.get("fields"), rspDataList);
                }
                checkDataFollowsModelSchema(paramMaps.get("name"), response);
            }else{
                Response response = SubscriptionManagementEndpoint.deleteAllSubscriptions();
                if (paramMaps.get("description").contains("good request")) {
                    checkStatusCode(paramMaps, response.getStatusCode());
                }
            }

        }else if(clientId == null && subSentence == null && id == null && numOfItems != null){
            SubscriptionManagementEndpoint.deleteAllSubscriptions();
            Response response = SubscriptionManagementEndpoint.getSubscriptions();
            String listPath = "";
            List<HashMap<String, String>> rspDataList = response.jsonPath().getList(listPath);
            CommonCheckFunctions.checkDataContainsNumOfItems(listPath,paramMaps.get("numOfItems"),rspDataList);
        }
        logger.info(paramMaps.get("test-id") + " Tests Passed");
    }

    @Test(priority = 0,
            description = "Test subscription management interface: create subscription and delete by id",
            dataProvider = "subscription-management-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send different delete requests and check the response message.")
    @Story("Subscription management Interface: subscriptions management")
    public void createAndDelSubscriptionByID(Map<String, String> paramMaps){

          HashMap<String, String> queryParamters = new HashMap<>();
          String clientId = paramMaps.get("clientId");
          String subSentence = paramMaps.get("subSentence");
        if (clientId != null && subSentence != null) {
            SubscriptionManagementEndpoint.deleteAllSubscriptions();
            queryParamters.put("clientId", paramMaps.get("clientId"));
            Response response = SubscriptionManagementEndpoint.registerSubscriptions(queryParamters, subSentence);
            id = response.jsonPath().get("data.id");
            if (paramMaps.get("description").contains("good request")) {
                checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().get("message"), response.jsonPath().getList("data.clients"));
            }
            if (paramMaps.get("description").contains("data retrieved")) {
                checkDataFollowsModelSchema(paramMaps.get("name"), response);
            }
        }else if(clientId !=null && subSentence == null){
                queryParamters.put("clientId", paramMaps.get("clientId"));
                Response response = SubscriptionManagementEndpoint.deleteSubscriptionById(queryParamters, id);
                if (paramMaps.get("description").contains("good request")) {
                    checkStatusCode(paramMaps,response.getStatusCode());
                    checkResponseCodeAndMessage(paramMaps,response.jsonPath().getString("code"), response.jsonPath().get("message"));
                    String actualID = response.jsonPath().getString("data");
                    assertThat(id,is(equalTo(actualID)));
                }
                if (paramMaps.get("description").contains("data retrieved")) {
                    checkDataFollowsModelSchema(paramMaps.get("name"), response);
                }
        }
        logger.info(paramMaps.get("test-id") + " Tests Passed");
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


