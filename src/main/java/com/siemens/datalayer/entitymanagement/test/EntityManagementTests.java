package com.siemens.datalayer.entitymanagement.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jdk.nashorn.internal.objects.annotations.Constructor;
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

import javax.swing.*;

import static io.restassured.RestAssured.given;

@Epic("SDL Entity-management")
@Feature("Graph/ Entity/ Relation End Points")

public class EntityManagementTests {
	
	static Map<String, String> TestEntityList;
	static Map<String,Map> paramMapsOfcreateEntity;
	
	@Parameters({"base_url", "port"})
	@BeforeClass (description = "Configure the host address and communication port of entity-management")
	public void setEntityManagementEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("31706") String port)
	{
		TestEntityList = new HashMap<String, String>();
		paramMapsOfcreateEntity = new HashMap<String,Map>();
		
		EntityManagementEndpoint.setBaseUrl(base_url);
		EntityManagementEndpoint.setPort(port);
	    AllureEnvironmentPropertiesWriter.addEnvironmentItem("data-layer-entity-management", base_url + ":" + port);
	}
	
	@AfterClass (description = "Clean up test entities")
	public void removeTestEntitiesAndparamMapsOfcreateEntity()
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

		if(paramMapsOfcreateEntity.size() > 0){
			for (String key : paramMapsOfcreateEntity.keySet())
			{
				paramMapsOfcreateEntity.remove(key);
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
	// 此时的paramMaps为一个Map，比如{"test-id":"iEMS-Entity-mgmt-Test-1","description":"good request",
	// "location":"iEMS","label":"testEntity1","rspStatus":"200","rspCode":"200","rspMessage":"OK"}
	public void createEntity(Map<String, String> paramMaps)
	{
		if(paramMapsOfcreateEntity.get("paramMapsOfcreateEntity") == null)
			paramMapsOfcreateEntity.put("paramMapsOfcreateEntity",paramMaps);
		/*for(Map.Entry<String,Map> entry : paramMapsOfcreateEntity.entrySet())
			System.out.println(entry.getKey() + " = " +entry.getValue());*/
		if (true)
		{
			// 创建类updateEntityRequestBody的一个实例，updateEntityRequestBody类主要是构造传入接口的参数
			updateEntityRequestBody requestBody = new updateEntityRequestBody();

			requestBody.setId(paramMaps.get("id"));
			requestBody.setLabel(paramMaps.get("label"));

			requestBody.setProperty("metadata_node_type", paramMaps.get("metadata_node_type"));
			requestBody.setProperty("metadata_node_domain", paramMaps.get("metadata_node_domain"));
			requestBody.setProperty("additional_prop1", paramMaps.get("additional_prop1"));
			requestBody.setProperty("additional_prop2", paramMaps.get("additional_prop2"));

			requestBody.setNodeType(paramMaps.get("nodeType"));
			requestBody.setConnectedRelationNumber(paramMaps.get("connectedRelationNumber"));

			try
			{
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

				String bodyString = objectMapper.writeValueAsString(requestBody);


				// 通过EntityManagementEndpoint-createEntity方法，发送请求至http://140.231.89.85:31818/api/entities接口，并获取返回response
				Response response = EntityManagementEndpoint.createEntity(bodyString);
				checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));

				// System.out.println("data.id:"+response.jsonPath().get("data.id").toString());

				if (paramMaps.get("description").contains("good request"))
				{
					TestEntityList.put(paramMaps.get("label"), response.jsonPath().get("data.id"));

					Assert.assertNotNull(response.jsonPath().get("data.id"));
					Assert.assertEquals(response.jsonPath().get("data.label"), paramMaps.get("label"), "New entity's label is set correctly");

					Assert.assertEquals(response.jsonPath().get("data.properties.metadata_node_type"), paramMaps.get("metadata_node_type"));
					Assert.assertEquals(response.jsonPath().get("data.properties.metadata_node_domain"), paramMaps.get("metadata_node_domain"));
					Assert.assertEquals(response.jsonPath().get("data.properties.additional_prop1"), paramMaps.get("additional_prop1"));
					Assert.assertEquals(response.jsonPath().get("data.properties.additional_prop2"), paramMaps.get("additional_prop2"));

					Assert.assertEquals(response.jsonPath().get("data.nodeType"), paramMaps.get("nodeType"));
					Assert.assertEquals(response.jsonPath().get("data.connectedRelationNumber"),paramMaps.get("connectedRelationNumber"));
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
		if((paramMaps.get("description").contains("entity id not exist")==false)
				&&(paramMaps.containsKey("label")))
			createEntityToBeTest(paramMaps.get("label"));

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
				// System.out.println(response.jsonPath().get("data.properties").toString());

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
		if((paramMaps.get("description").contains("entity id not exist")==false)
				&&(paramMaps.containsKey("label")))
			createEntityToBeTest(paramMaps.get("label"));
		
		// Read the id from input parameter 
		String entityId = paramMaps.get("id");
		
		// If the entity to be test exists, use its real id
		if (TestEntityList.containsKey(paramMaps.get("label"))) 
			entityId = TestEntityList.get(paramMaps.get("label"));
		
		Response response = EntityManagementEndpoint.getEntityById(entityId);
		// System.out.println("message:"+response.jsonPath().get("message").toString());
		
		checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message")); 
		
		if (paramMaps.get("description").contains("good request"))
		{
			updateEntityRequestBody responseData = response.jsonPath().getObject("data", updateEntityRequestBody.class);
			
			Assert.assertTrue(responseData.getId().equals(entityId), "The entity id is correct");
			Assert.assertTrue(responseData.getNodeType().equals("ENTITY"), "The node type is correct");
			
			if (paramMaps.containsKey("label"))
			{
				Assert.assertTrue(responseData.getLabel().equals(paramMaps.get("label")), "The entity name is correct");
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
		if((paramMaps.get("description").contains("entity id not exist")==false)
				&&(paramMaps.containsKey("label")))
			createEntityToBeTest(paramMaps.get("label"));
		
		// Read the id from input parameter 
		String entityToBeDelete = paramMaps.get("id");
		
		// If the entity to be delete exists, use its real id
		if (TestEntityList.containsKey(paramMaps.get("label"))) 
			entityToBeDelete = TestEntityList.get(paramMaps.get("label"));

		// System.out.println("data.id:"+TestEntityList.get(paramMaps.get("label")));
		Response response = EntityManagementEndpoint.deleteEntity(entityToBeDelete);
		
		if ((TestEntityList.containsKey(paramMaps.get("label"))) && (response.jsonPath().getString("message").equals("OK"))) 
			TestEntityList.remove(paramMaps.get("label"));
		// System.out.println("data.id:"+TestEntityList.get(paramMaps.get("label")));
		
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
		paramMaps.put("rspCode", "100000");
		paramMaps.put("message", "success");
		
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
		// System.out.println("size of data:"+response.jsonPath().getList("data").size());
		
		checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message")); 
		
		List<HashMap<String, String>> relationList = response.jsonPath().getList("data");
		
		if (paramMaps.get("description").contains("good request"))
		{
			Assert.assertTrue(relationList.size()>0, "Relation items are found in the response");
			
			if (paramMaps.containsKey("labels")) checkRelationLabels(relationList, paramMaps.get("labels"));
			
			String schemaTemplateFile = "json-model-schema/entity-mgmt/getAllRelationsResponse.JSON";	
			//CommonCheckFunctions.verifyIfDataMatchesJsonSchemaTemplate(schemaTemplateFile, response.getBody().asString());
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
	public static void createEntityToBeTest(String label)
	{
		if (!TestEntityList.containsKey(label))
		{	
			String id = createEntity(label);
			if (id.equals("none")==false)
				TestEntityList.put(label, id);
			else
				System.out.println("Error: can not create test entity '" + label + "'");
		}
	}
	
	public static String createEntity(String label)
	{
		String result = "none";
		
		updateEntityRequestBody requestBody = new updateEntityRequestBody();
		
		requestBody.setId(paramMapsOfcreateEntity.get("paramMapsOfcreateEntity").get("id").toString());
		requestBody.setLabel(paramMapsOfcreateEntity.get("paramMapsOfcreateEntity").get("label").toString());
		requestBody.setNodeType(paramMapsOfcreateEntity.get("paramMapsOfcreateEntity").get("nodeType").toString());
		requestBody.setProperty("metadata_node_type", paramMapsOfcreateEntity.get("paramMapsOfcreateEntity").get("metadata_node_type").toString());
		requestBody.setProperty("metadata_node_domain", paramMapsOfcreateEntity.get("paramMapsOfcreateEntity").get("metadata_node_domain").toString());
		requestBody.setProperty("additional_prop1", paramMapsOfcreateEntity.get("paramMapsOfcreateEntity").get("additional_prop1").toString());
		requestBody.setProperty("additional_prop2", paramMapsOfcreateEntity.get("paramMapsOfcreateEntity").get("additional_prop2").toString());
		
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
			System.out.println("Error: can not convert the input string 'body' to a json request");
	    }
		
		return result;
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
