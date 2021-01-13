package com.siemens.datalayer.entitymanagement.test;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.siemens.datalayer.entitymanagement.model.updateEntityRequestBody;
import com.siemens.datalayer.utils.AllureEnvironmentPropertiesWriter;
import com.siemens.datalayer.utils.ExcelDataProviderClass;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.restassured.response.Response;

@Epic("SDL Entity-management")
@Feature("Entity End Point")

public class EntityManagementTests {
	
	static Map<String, String> TestEntityList;
	
	@Parameters({"base_url", "port"})
	@BeforeClass (description = "Configure the host address and communication port of entity-management")
	public void setApiEngineEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("31706") String port) 
	{
		TestEntityList = new HashMap<String, String>();
		
		EntityManagementEndpoint.setBaseUrl(base_url);
		EntityManagementEndpoint.setPort(port);
	    AllureEnvironmentPropertiesWriter.addEnvironmentItem("entity-management", base_url + ":" + port);
	}
	
	@AfterClass (description = "Clean up test entities")
	public void removeTestEntities()
	{
		if (TestEntityList.size() > 0)
		{
			for (Map.Entry<String, String> entry : TestEntityList.entrySet())
			{
				Response response = EntityManagementEndpoint.deleteEntity(entry.getValue());
				
				if (response.jsonPath().getString("code").equals("0")==false) 
					System.out.println("Error: can not remove the specified test entity (" + entry.getKey() + "/" + entry.getValue() + ")");
			}
		}
	}
	
	@Test ( priority = 0, 
			description = "Test Entity-management Entity Endpoint: createEntityInDomain", 
			dataProvider = "entity-management-test-data-provider", 
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'createEntityInDomain' request to entity endpoint interface.")
	@Story("Entity End Point: createEntityInDomain")
	public void createEntityInDomain(Map<String, String> paramMaps)
	{
		if ((paramMaps.containsKey("domain")) && (paramMaps.containsKey("entity")))
		{
			Response response = EntityManagementEndpoint.createEntityInDomain(paramMaps.get("domain"), paramMaps.get("entity"));
			checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message")); 
		
			if (paramMaps.get("description").contains("good request"))
			{
				TestEntityList.put(paramMaps.get("entity"), response.jsonPath().get("data.id"));
				
				Assert.assertEquals(response.jsonPath().get("data.nodeType"), "ENTITY");
				Assert.assertEquals(response.jsonPath().get("data.label"), paramMaps.get("entity"), "New entity's label is set correctly");
				Assert.assertEquals(response.jsonPath().get("data.location"), paramMaps.get("domain"), "New entity's location is set correctly");

				Assert.assertEquals(response.jsonPath().get("data.properties.metadata_node_type"), "entity");
				Assert.assertEquals(response.jsonPath().get("data.properties.metadata_node_domain"), paramMaps.get("domain"));
			}		
		}
	}
	
	@Test ( priority = 0, 
			description = "Test Entity-management Entity Endpoint: updateEntity", 
			dataProvider = "entity-management-test-data-provider", 
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send an 'updateEntity' request to entity endpoint interface.")
	@Story("Entity End Point: updateEntity")
	public void updateEntity(Map<String, String> paramMaps)
	{
		// Check if the entity to be update really exists, if not create it
		if (!paramMaps.get("description").contains("entity id not exist"))
		{
			if ((paramMaps.containsKey("domain")) && (paramMaps.containsKey("entity")))
			{
				if (!TestEntityList.containsKey(paramMaps.get("entity")))
				{
					Response response = EntityManagementEndpoint.createEntityInDomain(paramMaps.get("domain"), paramMaps.get("entity"));
				
					if (response.jsonPath().getString("code").equals("0"))
						TestEntityList.put(paramMaps.get("entity"), response.jsonPath().get("data.id"));
					else
						System.out.println("Error: can not create test entity '" + paramMaps.get("entity") + "'");
				}
			}
		}
		
		try
		{
			ObjectMapper objectMapper = new ObjectMapper();	
			objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			
			updateEntityRequestBody requestBody = objectMapper.readValue(paramMaps.get("body"), updateEntityRequestBody.class);
			requestBody.setId(TestEntityList.get(paramMaps.get("entity")));
			
			String valueAsString = objectMapper.writeValueAsString(requestBody);
//			System.out.println(valueAsString);
			
			Response response = EntityManagementEndpoint.updateEntity(valueAsString);
			
			checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message")); 
			
			if (paramMaps.get("description").contains("good request")) checkUpdateEntityResponse(requestBody, response);
		}
		catch (Exception e) 
	    {
			System.out.println("Error: can not convert the input field 'body' to a jason request");
	    }

	}
	
	@Step("Verify the status code, operation code, and message")
	public static void checkResponseCode(Map<String, String> requestParameters, int actualStatusCode, String actualCode, String actualMessage)
	{
		int expStatusCode = 200;	// If not specified, the expected status code is set to 200 (OK)
		if (requestParameters.containsKey("rspStatus")) expStatusCode = Integer.valueOf(requestParameters.get("rspStatus")).intValue();
		Assert.assertEquals(actualStatusCode, expStatusCode, "The status code in response message matches the expected value.");
		  
		if ((requestParameters.containsKey("rspCode")))
			Assert.assertEquals(actualCode, requestParameters.get("rspCode"), "The operation code in response message matches the expected value.");  
		  
		if (requestParameters.containsKey("rspMessage"))
			Assert.assertTrue(actualMessage.contains(requestParameters.get("rspMessage")), "The operation message contains the expected content.");		
	}
	
	@Step("Verify if the updateEntity response contains correct information")
	public static void checkUpdateEntityResponse(updateEntityRequestBody request, Response response)
	{
		updateEntityRequestBody responseData = response.jsonPath().getObject("data", updateEntityRequestBody.class);	
		Assert.assertTrue(request.equals(responseData), "The entity has been successfully updated");
	}

}
