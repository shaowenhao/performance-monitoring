package com.siemens.datalayer.subscriptionmanagement.test;
import com.siemens.datalayer.utils.AllureEnvironmentPropertiesWriter;
import com.siemens.datalayer.utils.CommonCheckFunctions;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.annotations.Optional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("SDL Subscription Management")
@Feature("Subscription Endpoint")
public class SubscriptionManagementTests {

	List<HashMap<String, String>> TestSubscriptionList;

    @Parameters({"base_url", "port"})
    @BeforeClass(description = "Configure the host address and communication port of iEMS Subscription management")
    public void setSubscriptionManagementEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("31111") String port) {

        TestSubscriptionList = new ArrayList<HashMap<String, String>>();
        SubscriptionManagementEndpoint.setBaseUrl(base_url);
        SubscriptionManagementEndpoint.setPort(port);
        AllureEnvironmentPropertiesWriter.addEnvironmentItem("iems-subscription-management", base_url + ":" + port);

        // Response responseOfGetSubscriptions = SubscriptionManagementEndpoint.getSubscriptions();
    }


    @AfterClass (description = "Clean up the subscriptions created for testing")
    public void removeTestSubscriptions()
    {
		for (HashMap<String, String> item : TestSubscriptionList)
		{	
			HashMap<String,String> queryParamters = new HashMap<String, String>();
            queryParamters.put("clientId", item.get("clientId"));
            
            Response response = SubscriptionManagementEndpoint.deleteSubscriptionById(queryParamters, item.get("id"));
            
            if (response.jsonPath().getString("message").contains("Successfully")==false)
            	System.out.println("Error: delete subscription failed (id=" + item.get("id") + "; clientId=" + item.get("clientId") + ")");
        };
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
        
        HashMap<String, String> queryParamters = new HashMap<String, String>();
        queryParamters.put("clientId", paramMaps.get("clientId"));
        
        Response response = SubscriptionManagementEndpoint.registerSubscriptions(queryParamters, subSentence);
        System.out.println("clients: "+response.jsonPath().getString("data.clients"));

        if (paramMaps.get("description").contains("good request")) 
        {
            checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().get("message"), response.jsonPath().getList("data.clients"));
            checkDataFollowsModelSchema(paramMaps.get("name"), response);
        } 
        else 
        {
            checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().get("message"), response.jsonPath().getString("data"));
        }
        
        if (response.jsonPath().getString("message").contains("Successfully"))
		{
        	HashMap<String, String> subscriptionItem = new HashMap<String, String>();;
        	subscriptionItem.put("id", response.jsonPath().getString("data.id"));
        	subscriptionItem.put("clientId", paramMaps.get("clientId"));
            TestSubscriptionList.add(subscriptionItem);
		}
    }

    @Test( dependsOnMethods = { "registerSubscriptions" }, alwaysRun = true,
            priority = 0,
            description = "Test subscription management interface: getSubscriptions.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'getSubscriptions' request to subscription endpoint interface.")
    @Story("Subscription End Point: getSubscriptions")
    public void getSubscriptions(){

        Response response = SubscriptionManagementEndpoint.getSubscriptions();
        
        Assert.assertEquals(response.getStatusCode(), 200, "The status code is 200.");
        
        List<HashMap<String, String>> subscriptionsList = response.jsonPath().getList("$");
        System.out.println("subscriptionsList.size(): "+ subscriptionsList.size());
        
        for (HashMap<String, String> rspDataItem: subscriptionsList)
        {
        	if (rspDataItem != null)
        	{
        		try
        		{
	        		ObjectMapper mapper = new ObjectMapper();
	        		String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rspDataItem);
	        		
		        	String schemaTemplateFile = SubscriptionManagementEndpoint.getResourcePath() + "JasonModelSchemaFor" + "GetSubMgmt.JSON";
		            CommonCheckFunctions.verifyIfDataMatchesJsonSchemaTemplate(schemaTemplateFile, jsonString);
        		}
    			catch (Exception e) 
    		    {
    				System.out.println("Error: failed to convert data item in Json format");
    				return;
    		    }
        	}
        	else
        	{
        		System.out.println("Warning: null item is found!");
        	}
        }
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

    	HashMap<String, String> queryParamters = new HashMap<String, String>();
        String subscriptionId = paramMaps.get("id");
        if(subscriptionId != null)
        {
            queryParamters.put("clientId", paramMaps.get("clientId"));
            Response response = SubscriptionManagementEndpoint.deleteSubscriptionById(queryParamters, subscriptionId);
            checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().get("message"), response.jsonPath().getString("data"));
        } 
        else
        {
        	for (HashMap<String, String> item : TestSubscriptionList)
        	{
        		if (item.get("clientId").equals(paramMaps.get("clientId")))
        		{
        			if (paramMaps.get("description").contains("bad request, valid id with invalid clientId"))
        				queryParamters.put("clientId", paramMaps.get("clientId")+"_incorrect");
        			else
        				queryParamters.put("clientId", paramMaps.get("clientId"));
        			
		            Response response = SubscriptionManagementEndpoint.deleteSubscriptionById(queryParamters, item.get("id"));
		            
		            checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().get("message"), response.jsonPath().getString("data"));
		            
		            if (paramMaps.get("name")!=null) checkDataFollowsModelSchema(paramMaps.get("name"), response);
		            
		            if(response.jsonPath().getString("message").contains("Successfully")) TestSubscriptionList.remove(item);
		            
		            break;
        		}
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
