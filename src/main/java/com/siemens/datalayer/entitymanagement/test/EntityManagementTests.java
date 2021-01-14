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
	
	@Test ( dependsOnMethods = { "createEntityInDomain" }, alwaysRun = true,
			priority = 0, 
			description = "Test Entity-management Entity Endpoint: updateEntity", 
			dataProvider = "entity-management-test-data-provider", 
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send an 'updateEntity' request to entity endpoint interface.")
	@Story("Entity End Point: updateEntity")
	public void updateEntity(Map<String, String> paramMaps)
	{
		// Check if the entity to be update really exists, if not create it
		if ((paramMaps.get("description").contains("entity id not exist")==false) &&
			(paramMaps.containsKey("domain")) && (paramMaps.containsKey("entity")))
			createEntityToBeTest(paramMaps.get("domain"), paramMaps.get("entity"));		

		String bodyString = paramMaps.get("body");
		
		// Check if the input string contains basic information
		if (bodyString.contains("id") && bodyString.contains("label") && bodyString.contains("properties"))
		{
			ObjectMapper objectMapper = new ObjectMapper();	
			objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			
			try
			{
				// Prepare request body object based on the input body string 
				updateEntityRequestBody requestBody = objectMapper.readValue(bodyString, updateEntityRequestBody.class);
				
				// If the entity to be test really exists, use its real id
				if (TestEntityList.containsKey(paramMaps.get("entity"))) requestBody.setId(TestEntityList.get(paramMaps.get("entity")));
				
				// Create a request body in Json format
				bodyString = objectMapper.writeValueAsString(requestBody);
	
				Response response = EntityManagementEndpoint.updateEntity(bodyString);
				
				checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message")); 
				
				if (paramMaps.get("description").contains("good request")) checkUpdateEntityResponse(requestBody, response);
			}
			catch (Exception e) 
		    {
				System.out.println("Error: can not convert the input string 'body' to a jason request");
				return;
		    }
		}
		else // test when input is not complete
		{
			Response response = EntityManagementEndpoint.updateEntity(bodyString);			
			checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message")); 	
		}
	}
	
	@Test ( dependsOnMethods = { "createEntityInDomain" }, alwaysRun = true,
			priority = 0, 
			description = "Test Entity-management Entity Endpoint: getEntityById", 
			dataProvider = "entity-management-test-data-provider", 
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'getEntityById' request to entity endpoint interface.")
	@Story("Entity End Point: getEntityById")
	public void getEntityById(Map<String, String> paramMaps)
	{
		// Check if the entity to be test really exists, if not create it
		if ((paramMaps.get("description").contains("entity id not exist")==false) &&
			(paramMaps.containsKey("domain")) && (paramMaps.containsKey("entity")))
			createEntityToBeTest(paramMaps.get("domain"), paramMaps.get("entity"));
		
		// Read the id from input parameter 
		String entityId = paramMaps.get("id");
		
		// If the entity to be test exists, use its real id
		if (TestEntityList.containsKey(paramMaps.get("entity"))) 
			entityId = TestEntityList.get(paramMaps.get("entity"));
		
		Response response = EntityManagementEndpoint.getEntityById(entityId);
		
		checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message")); 
		
		if (paramMaps.get("description").contains("good request"))
		{
			updateEntityRequestBody responseData = response.jsonPath().getObject("data", updateEntityRequestBody.class);
			
			Assert.assertTrue(responseData.getId().equals(entityId), "The entity is correct");
			Assert.assertTrue(responseData.getNodeType().equals("ENTITY"), "The entity is correct");
			
			if ((paramMaps.containsKey("domain")) && (paramMaps.containsKey("entity")))
			{
				Assert.assertTrue(responseData.getLabel().equals(paramMaps.get("entity")), "The entity name is correct");
				Assert.assertTrue(responseData.getLocation().equals(paramMaps.get("domain")), "The domain is correct");
			}
		}
	}
	
	@Test ( dependsOnMethods = { "createEntityInDomain", "getEntityById", "updateEntity" }, alwaysRun = true,
			priority = 0, 
			description = "Test Entity-management Entity Endpoint: deleteEntity", 
			dataProvider = "entity-management-test-data-provider", 
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'deleteEntity' request to entity endpoint interface.")
	@Story("Entity End Point: deleteEntity")
	public void deleteEntity(Map<String, String> paramMaps)
	{
		// Check if the entity to be delete really exists, if not create it
		if ((paramMaps.get("description").contains("entity id not exist")==false) &&
			(paramMaps.containsKey("domain")) && (paramMaps.containsKey("entity")))
			createEntityToBeTest(paramMaps.get("domain"), paramMaps.get("entity"));
		
		// Read the id from input parameter 
		String entityToBeDelete = paramMaps.get("id");
		
		// If the entity to be delete exists, use its real id
		if (TestEntityList.containsKey(paramMaps.get("entity"))) 
			entityToBeDelete = TestEntityList.get(paramMaps.get("entity"));
		
		Response response = EntityManagementEndpoint.deleteEntity(entityToBeDelete);
		
		if ((TestEntityList.containsKey(paramMaps.get("entity"))) && (response.jsonPath().getString("code").equals("0"))) 
			TestEntityList.remove(paramMaps.get("entity"));
		
		checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message")); 
	}
	
	// Check if the entity with the given domain & name really exists, if not try to create it
	public static void createEntityToBeTest(String domain, String name)
	{
		if (!TestEntityList.containsKey(name))
		{
			Response response = EntityManagementEndpoint.createEntityInDomain(domain, name);
		
			if (response.jsonPath().getString("message").contains("OK"))
				TestEntityList.put(name, response.jsonPath().get("data.id"));
			else
				System.out.println("Error: can not create test entity '" + name + "'");
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
			if (requestParameters.get("rspCode").contains("null"))
				Assert.assertNull(actualCode, "No operation code is found.");
			else
			Assert.assertEquals(actualCode, requestParameters.get("rspCode"), "The operation code in response message matches the expected value.");  
		}
		  
		if (requestParameters.containsKey("rspMessage"))
		{
			if (requestParameters.get("rspMessage").contains("null"))
				Assert.assertNull(actualMessage, "The content of operation message is null.");
			else
				Assert.assertTrue(actualMessage.contains(requestParameters.get("rspMessage")), "The operation message contains the expected content.");	
		}
	}
	
	@Step("Verify if the updateEntity response contains correct information")
	public static void checkUpdateEntityResponse(updateEntityRequestBody request, Response response)
	{
		updateEntityRequestBody responseData = response.jsonPath().getObject("data", updateEntityRequestBody.class);	
		Assert.assertTrue(request.equals(responseData), "The entity has been successfully updated");
	}

}
