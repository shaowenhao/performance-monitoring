package com.siemens.datalayer.entitymanagement.test;

import java.util.HashMap;
import java.util.List;
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
import com.siemens.datalayer.utils.CommonCheckFunctions;
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
@Feature("Graph/ Entity/ Relation End Points")

public class EntityManagementTests {
	
	static Map<String, String> TestEntityList;
	
	@Parameters({"base_url", "port"})
	@BeforeClass (description = "Configure the host address and communication port of entity-management")
	public void setApiEngineEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("31706") String port) 
	{
		TestEntityList = new HashMap<String, String>();
		
		EntityManagementEndpoint.setBaseUrl(base_url);
		EntityManagementEndpoint.setPort(port);
	    AllureEnvironmentPropertiesWriter.addEnvironmentItem("data-layer-entity-management", base_url + ":" + port);
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
			description = "Test Entity-management Entity Endpoint: createEntity", 
			dataProvider = "entity-management-test-data-provider", 
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'createEntity' request to entity endpoint interface.")
	@Story("Entity End Point: createEntity")
	public void createEntity(Map<String, String> paramMaps)
	{
		if ((paramMaps.containsKey("location")) && (paramMaps.containsKey("label")))
		{
			updateEntityRequestBody requestBody = new updateEntityRequestBody();
			
			requestBody.setId("1234");
			requestBody.setLocation(paramMaps.get("location"));
			requestBody.setLabel(paramMaps.get("label"));
			
			requestBody.setNodeType("ENTITY");
			requestBody.setProperty("metadata_node_type", "entity");
			requestBody.setProperty("metadata_node_domain", paramMaps.get("location"));	
			requestBody.setProperty("additional_prop1", "value1");
			requestBody.setProperty("additional_prop2", "value2");
			
			try
			{
				ObjectMapper objectMapper = new ObjectMapper();	
				objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
				
				String bodyString = objectMapper.writeValueAsString(requestBody);
	
				Response response = EntityManagementEndpoint.createEntity(bodyString);
				checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message")); 
			
				if (paramMaps.get("description").contains("good request"))
				{
					TestEntityList.put(paramMaps.get("label"), response.jsonPath().get("data.id"));
					
					Assert.assertEquals(response.jsonPath().get("data.nodeType"), "ENTITY");
					Assert.assertEquals(response.jsonPath().get("data.label"), paramMaps.get("label"), "New entity's label is set correctly");
					Assert.assertEquals(response.jsonPath().get("data.location"), paramMaps.get("location"), "New entity's location is set correctly");
	
					Assert.assertEquals(response.jsonPath().get("data.properties.metadata_node_type"), "entity");
					Assert.assertEquals(response.jsonPath().get("data.properties.metadata_node_domain"), paramMaps.get("location"));
				}	
			}
			catch (Exception e) 
		    {
				System.out.println("Error: failed to assemble a jason format request body");
				return;
		    }
		}
	}
	
	@Test ( dependsOnMethods = { "createEntity" }, alwaysRun = true,
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
			(paramMaps.containsKey("location")) && (paramMaps.containsKey("label")))
			createEntityToBeTest(paramMaps.get("location"), paramMaps.get("label"));		

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
				if (TestEntityList.containsKey(paramMaps.get("label"))) requestBody.setId(TestEntityList.get(paramMaps.get("label")));
				
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
	
	@Test ( dependsOnMethods = { "createEntity" }, alwaysRun = true,
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
			(paramMaps.containsKey("location")) && (paramMaps.containsKey("label")))
			createEntityToBeTest(paramMaps.get("location"), paramMaps.get("label"));
		
		// Read the id from input parameter 
		String entityId = paramMaps.get("id");
		
		// If the entity to be test exists, use its real id
		if (TestEntityList.containsKey(paramMaps.get("label"))) 
			entityId = TestEntityList.get(paramMaps.get("label"));
		
		Response response = EntityManagementEndpoint.getEntityById(entityId);
		
		checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message")); 
		
		if (paramMaps.get("description").contains("good request"))
		{
			updateEntityRequestBody responseData = response.jsonPath().getObject("data", updateEntityRequestBody.class);
			
			Assert.assertTrue(responseData.getId().equals(entityId), "The entity id is correct");
			Assert.assertTrue(responseData.getNodeType().equals("ENTITY"), "The node type is correct");
			
			if ((paramMaps.containsKey("location")) && (paramMaps.containsKey("label")))
			{
				Assert.assertTrue(responseData.getLabel().equals(paramMaps.get("label")), "The entity name is correct");
				Assert.assertTrue(responseData.getLocation().equals(paramMaps.get("location")), "The domain is correct");
			}
		}
	}
	
	@Test ( dependsOnMethods = { "createEntity", "getEntityById", "updateEntity" }, alwaysRun = true,
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
			(paramMaps.containsKey("location")) && (paramMaps.containsKey("label")))
			createEntityToBeTest(paramMaps.get("location"), paramMaps.get("label"));
		
		// Read the id from input parameter 
		String entityToBeDelete = paramMaps.get("id");
		
		// If the entity to be delete exists, use its real id
		if (TestEntityList.containsKey(paramMaps.get("label"))) 
			entityToBeDelete = TestEntityList.get(paramMaps.get("label"));
		
		Response response = EntityManagementEndpoint.deleteEntity(entityToBeDelete);
		
		if ((TestEntityList.containsKey(paramMaps.get("label"))) && (response.jsonPath().getString("message").equals("OK"))) 
			TestEntityList.remove(paramMaps.get("label"));
		
		checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message")); 
	}
	
	@Test ( priority = 0, description = "Test Entity-management Graph Endpoint: getGraph")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'getGraph' request to graph endpoint interface.")
	@Story("Graph End Point: getGraph")
	public void getGraph()
	{
		Response response = EntityManagementEndpoint.getGraph();
		
		Map<String, String> paramMaps = new HashMap<String, String>();
		paramMaps.put("rspStatus", "200");
		paramMaps.put("rspCode", "200");
		paramMaps.put("message", "OK");
		
		checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message")); 	
	}
	
	@Test ( priority = 0, 
			description = "Test Entity-management Relation Endpoint: getRelations",
			dataProvider = "entity-management-test-data-provider", 
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'getRelations' request to relation endpoint interface.")
	@Story("Relation End Point: getRelations")
	public void getRelations(Map<String, String> paramMaps)
	{
		Response response;
		
		if (paramMaps.containsKey("labels")==false)
			response = EntityManagementEndpoint.getAllRelations();
		else
			response = EntityManagementEndpoint.getRelations(paramMaps.get("labels"));
		
		checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message")); 
		
		List<HashMap<String, String>> relationList = response.jsonPath().getList("data");
		
		if (paramMaps.get("description").contains("good request"))
		{
			Assert.assertTrue(relationList.size()>0, "Relation items are found in the response");
			
			if (paramMaps.containsKey("labels")) checkRelationLabels(relationList, paramMaps.get("labels"));
			
			String schemaTemplateFile = "json-model-schema/entity-mgmt/getAllRelationsResponse.JSON";	
			CommonCheckFunctions.verifyIfDataMatchesJsonSchemaTemplate(schemaTemplateFile, response.getBody().asString());
		}
		else
		{
			Assert.assertTrue(relationList.size()==0, "The response does not contain any relation items");
		}
	}
	
	@Test ( priority = 0, 
			description = "Test Entity-management Relation Endpoint: getRelationById",
			dataProvider = "entity-management-test-data-provider", 
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'getRelationById' request to relation endpoint interface.")
	@Story("Relation End Point: getRelationById")
	public void getRelationById(Map<String, String> paramMaps)
	{
		if (paramMaps.containsKey("label")) 
		{
			// Use getRelations(String labels) to find a list of relations
			Response response = EntityManagementEndpoint.getRelations(paramMaps.get("label"));
			
			List<HashMap<String, String>> relationList = response.jsonPath().getList("data");
			
			// Set the input parameter "relationId" to a value picked up from the above list
			if (relationList.size()>0) paramMaps.put("relationId", response.jsonPath().getString("data[0].id"));
		}
		
		Response response = EntityManagementEndpoint.getRelationById(paramMaps.get("relationId"));
		
		checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));
		
		if (paramMaps.get("description").contains("good request"))
		{
			HashMap<String, String> item = response.jsonPath().get("data");
			Assert.assertTrue(paramMaps.get("relationId").equals(item.get("id")), "The relation id is the same as specified in the request");
		}
	}
	
	// Check if the entity with the given location & label really exists, if not try to create it
	public static void createEntityToBeTest(String location, String label)
	{
		if (!TestEntityList.containsKey(label))
		{	
			String id = createEntity(location, label);
			if (id.equals("none")==false)
				TestEntityList.put(label, id);
			else
				System.out.println("Error: can not create test entity '" + label + "'");
		}
	}
	
	public static String createEntity(String location, String label)
	{
		String result = "none";
		
		updateEntityRequestBody requestBody = new updateEntityRequestBody();
		
		requestBody.setId("1234");
		requestBody.setLocation(location);
		requestBody.setLabel(label);		
		requestBody.setNodeType("ENTITY");
		requestBody.setProperty("metadata_node_type", "entity");
		requestBody.setProperty("metadata_node_domain", location);	
		requestBody.setProperty("additional_prop1", "value1");
		requestBody.setProperty("additional_prop2", "value2");
		
		try
		{
			ObjectMapper objectMapper = new ObjectMapper();	
			objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			
			String bodyString = objectMapper.writeValueAsString(requestBody);

			Response response = EntityManagementEndpoint.createEntity(bodyString);
			
			if (response.jsonPath().getString("message").contains("OK"))
				result = response.jsonPath().getString("data.id");
	
		}
		catch (Exception e) 
	    {
			System.out.println("Error: can not convert the input string 'body' to a jason request");
	    }
		
		return result;
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
	
	@Step("Verify if the relation labels in the response match the labels parameter of the request")
	public static void checkRelationLabels(List<HashMap<String, String>> itemList, String labels)
	{
		for (HashMap<String, String> item : itemList)
		{								
			Assert.assertTrue(labels.contains(item.get("label")), "Relation item's label matches the labels parameter of the request");
		}
	}

}
