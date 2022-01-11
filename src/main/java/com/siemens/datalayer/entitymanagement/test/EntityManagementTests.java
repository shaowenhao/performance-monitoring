package com.siemens.datalayer.entitymanagement.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siemens.datalayer.utils.AllureEnvironmentPropertiesWriter;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.Is.is;

@Epic("SDL Entity-management")
@Feature("Entity/ Relation/ Graph End Points")

public class EntityManagementTests {
	static String sucessfulRspCode = "100000";

	static Map<String, List<String>> testEntityMap;
	static Map<String,List<String>> testRelationMap;
	static Map<String,String> testGraphMap;

	@Parameters({"base_url", "port"})
	@BeforeClass (description = "Configure the host address and communication port of entity-management")
	public void setEntityManagementEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("31706") String port)
	{
		testEntityMap = new HashMap<>();
		testRelationMap = new HashMap<>();
		testGraphMap = new HashMap<>();

		EntityManagementEndpoint.setBaseUrl(base_url);
		EntityManagementEndpoint.setPort(port);
	    AllureEnvironmentPropertiesWriter.addEnvironmentItem("data-layer-entity-management", base_url + ":" + port);

		// delete the entities if the label of entity in List entityLabelToBeDeleteList
	    List<String> entityIdToBeDeleteList = new ArrayList<>();

		List<String> entityLabelToBeDeleteList = Arrays.asList("testEntity1","testEntity2","testEntity3",
				"testEntity4","entity1ForRelation","entity2ForRelation","pc_test1","pc_test2");

		for(String label : entityLabelToBeDeleteList){
			Response responseOfGetEntities = EntityManagementEndpoint.getEntities(label);
			if ((responseOfGetEntities.jsonPath().getList("data") !=null) && (!responseOfGetEntities.jsonPath().getList("data").isEmpty()))
			{
				List<Map<String,Object>> entityList = responseOfGetEntities.jsonPath().getList("data");
				for (int i=0;i<entityList.size();i++)
				{
					if (!entityIdToBeDeleteList.contains(entityList.get(i).get("id")))
						entityIdToBeDeleteList.add(String.valueOf(entityList.get(i).get("id")));
				}
			}
		}

		if (!entityIdToBeDeleteList.isEmpty())
			EntityManagementEndpoint.deleteEntities(String.join(",",entityIdToBeDeleteList));

		// delete the relations if the label of relation in List relationLabelToBeDeleteList
		List<String> relationIdToBeDeleteList = new ArrayList<>();

		List<String> relationLabelToBeDeleteList = Arrays.asList("testRelation1","testRelation2","testRelation3","testRelation4");

		for(String label : relationLabelToBeDeleteList){
			Response responseOfGetRelations = EntityManagementEndpoint.getRelations(label);
			if ((responseOfGetRelations.jsonPath().getList("data") !=null) && (!responseOfGetRelations.jsonPath().getList("data").isEmpty()))
			{
				List<Map<String,Object>> entityList = responseOfGetRelations.jsonPath().getList("data");
				for (int i=0;i<entityList.size();i++)
				{
					if (!relationIdToBeDeleteList.contains(entityList.get(i).get("id")))
						relationIdToBeDeleteList.add(String.valueOf(entityList.get(i).get("id")));
				}
			}
		}

		if (!relationIdToBeDeleteList.isEmpty())
			EntityManagementEndpoint.deleteRelations(String.join(",",relationIdToBeDeleteList));
	}

	@AfterClass (description = "Clean up test entities")
	public void clearTestEnvironment()
	{
		System.out.println("testEntityMap:" + testEntityMap);
		// System.out.println("testEntityMap:" + testEntityMap);
		if (testEntityMap.size() > 0)
		{
			for (Map.Entry<String, List<String>> entity : testEntityMap.entrySet())
			{
				for (String entityId : entity.getValue())
				{
					Response response = EntityManagementEndpoint.deleteEntities(entityId);
					// System.out.println(response.jsonPath().prettify());

					if (response.jsonPath().getString("code").equals(sucessfulRspCode)==false)
						System.out.println("Error: can not remove the specified test entity (" + entity.getKey() + "/" + entityId + ")");
				}
			}
		}

		System.out.println("testRelationMap:" + testRelationMap);
	}

	@Test ( alwaysRun = true,
			priority = 0,
			description = "Test Entity-management Entity Endpoint: createEntities",
			dataProvider = "entity-management-test-data-provider",
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'createEntities' request to entity endpoint interface.")
	@Story("Entity End Point: createEntities")
	// 此时的paramMaps为一个Map，比如{"test-id":"iEMS-Entity-mgmt-Test-1","description":"good request",
	// "location":"iEMS","label":"testEntity1","rspStatus":"200","rspCode":"200","rspMessage":"OK"}
	public void createEntities(Map<String, String> paramMaps)
	{
		String bodyString = paramMaps.get("body");
		Response response = EntityManagementEndpoint.createEntities(bodyString);

		// 如果接口创建entity成功，将其放入testEntityMap中
		if (response.jsonPath().getString("code").equals(sucessfulRspCode))
		{
			List<Map<String,String>> entityList = response.jsonPath().getList("data");
			for (Map<String,String> entity : entityList)
			{
				if (!testEntityMap.containsKey(entity.get("label"))) {
					testEntityMap.put(entity.get("label"), new ArrayList<String>() {
						{
							this.add(entity.get("id"));
						}
					});
				}
				else
					testEntityMap.get(entity.get("label")).add(entity.get("id"));
			}
		}

		checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));

		// check the response warning or error
		checkResponseWarningError(paramMaps,response);

		// check the response data whether matches the request
		if(paramMaps.get("description").contains("good request"))
		{
			List<Map<String,Object>> requestBodyList = null;
			List<Map<String,Object>> responseEntityList = null;

			try {
				requestBodyList = (new ObjectMapper()).readValue(paramMaps.get("body"), new TypeReference<List<Map<String, Object>>>() {});
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

			responseEntityList = response.jsonPath().getList("data");

			// check the response data size
			checkResponseDataSize(paramMaps,response.jsonPath().getList("data").size());

			// check the response data whether matches the request
			checkEntityResponseData(requestBodyList,responseEntityList);
		}
	}

	@Test ( dependsOnMethods = { "createEntities" },
			alwaysRun = true,
			priority = 0,
			description = "Test Entity-management Entity Endpoint: updateEntities",
			dataProvider = "entity-management-test-data-provider",
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send an 'updateEntities' request to entity endpoint interface.")
	@Story("Entity End Point: updateEntities")
	public void updateEntities(Map<String, String> paramMaps)
	{
		// Check if the entity to be update really exists, if not create it
		if (paramMaps.containsKey("pre-execution"))
		{
			createEntityToBeTest(paramMaps.get("pre-execution"));
		}

		// beforeReplacementBodyString：excel读取出的bodyString
		String beforeReplacementBodyString = paramMaps.get("body");

		if (paramMaps.get("description").contains("good request"))
		{
			List<Map<String,Object>> requestBodyList = null;

			try {
				requestBodyList = (new ObjectMapper()).readValue(beforeReplacementBodyString, new TypeReference<List<Map<String, Object>>>() {});
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

			// If the entity to be test really exists, use its real id
			for (Map<String,Object> requestBody : requestBodyList)
			{
				// 如果某个label对应多个id，取第一个
				requestBody.put("id",testEntityMap.get(requestBody.get("label")).get(0));
			}

			// bodyString：替换成真实id了的bodyString
			String bodyString = com.alibaba.fastjson.JSONObject.toJSONString(requestBodyList);
			System.out.println("bodyString: " + bodyString);

			Response response = EntityManagementEndpoint.updateEntities(bodyString);
			System.out.println(response.jsonPath().prettify());

			checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));

			checkResponseWarningError(paramMaps,response);

			// check the response data size
			checkResponseDataSize(paramMaps,response.jsonPath().getList("data").size());

			List<Map<String,Object>> responseEntityList = response.jsonPath().getList("data");

			// check the response data whether matches the request
			checkEntityResponseData(requestBodyList,responseEntityList);
		}
		else
		{
			Response response = EntityManagementEndpoint.updateEntities(beforeReplacementBodyString);
			System.out.println(response.jsonPath().prettify());

			checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));

			checkResponseWarningError(paramMaps,response);
		}
	}

	@Test ( dependsOnMethods = { "createEntities" },
			alwaysRun = true,
			priority = 0,
			description = "Test Entity-management Entity Endpoint: getEntityById",
			dataProvider = "entity-management-test-data-provider",
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'getEntityById' request to entity endpoint interface.")
	@Story("Entity End Point: getEntityById")
	public void getEntityById(Map<String, String> paramMaps)
	{
		// Check if the entity to be got really exists, if not create it
		if (paramMaps.containsKey("pre-execution"))
		{
			createEntityToBeTest(paramMaps.get("pre-execution"));
		}

		// Read the id from input parameter
		String entityId = paramMaps.get("id");

		// If the entity to be test exists, use its real id
		if (testEntityMap.containsKey(paramMaps.get("label")))
			entityId = testEntityMap.get(paramMaps.get("label")).get(0);

		Response response = EntityManagementEndpoint.getEntityById(entityId);

		checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));

		// Verity the id and label of the response
		if (paramMaps.get("description").contains("good request"))
		{
			Map<String,Object> responseEntityMap = response.jsonPath().getMap("data");

			Assert.assertEquals(responseEntityMap.get("id"),entityId);
			Assert.assertEquals(responseEntityMap.get("label"),paramMaps.get("label"));
		}
	}

	@Test(  dependsOnMethods = { "createEntities" },
			alwaysRun = true,
			priority = 0,
			description = "Test Entity-management Entity Endpoint: getEntities",
			dataProvider = "entity-management-test-data-provider",
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'getEntities' request to entity endpoint interface.")
	@Story("Entity End Point: getEntities")
	public void getEntities(Map<String, String> paramMaps)
	{
		Response response = EntityManagementEndpoint.getEntities(paramMaps.get("labels"));

		checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));

		List<Map<String,Object>> responseEntityList= response.jsonPath().getList("data");
		List<String> responseIdList = responseEntityList.stream().map(e->e.get("id").toString()).collect(Collectors.toList());

		if (paramMaps.get("description").contains("good request") && !paramMaps.get("description").contains("entities don't exist"))
		{
			// 如果存在labels，getEntities接口只返回对应的entity
			if (paramMaps.get("labels") != null)
			{
				List<Map<String,Object>> allEntityList = EntityManagementEndpoint.getEntities("").jsonPath().getList("data");
				checkLabels(paramMaps,allEntityList,responseEntityList);
			}
			// 如果不存在labels，getEntities接口返回所有entity
			else
			{
				for (Map.Entry<String,List<String>> entry : testEntityMap.entrySet())
				{
					for (String id : entry.getValue())
					{
						Assert.assertTrue(responseIdList.contains(id));
					}
				}
			}
		}
		else
		{
			Assert.assertTrue(responseIdList.isEmpty());
		}
	}

	@Test(  dependsOnMethods = { "createEntities" },
			alwaysRun = true,
			priority = 0,
			description = "Test Entity-management Entity Endpoint: getEntitiesLabelLike",
			dataProvider = "entity-management-test-data-provider",
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'getEntitiesLabelLike' request to entity endpoint interface.")
	@Story("Entity End Point: getEntitiesLabelLike")
	public void getEntitiesLabelLike(Map<String, String> paramMaps)
	{
		Response response = EntityManagementEndpoint.getEntitiesLabelLike(paramMaps.get("keyword"));

		checkResponseCode(paramMaps,response.statusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));

		if (paramMaps.get("description").contains("good request"))
		{
			Response responseOfGetEntities = EntityManagementEndpoint.getEntities("");

			List<Map<String,Object>> expAllEntityList= responseOfGetEntities.jsonPath().getList("data");

			// List<String> expAllIdList = expAllEntityList.stream().map(e->e.get("id").toString()).collect(Collectors.toList());
			// List<String> expIdList = expAllEntityList.stream().filter(e -> e != null && e.get("label") != null &&
					// e.get("label").toString().contains("test")).map(e->e.get("id").toString()).collect(Collectors.toList());

			List<Map<String,Object>> expEntityList = expAllEntityList.stream().filter(e -> e != null && e.get("label") != null &&
					e.get("label").toString().contains(paramMaps.get("keyword"))).collect(Collectors.toList());

			List<Map<String,Object>> actualEntityList = response.jsonPath().getList("data");

			// List<String> actualIdList = actualEntityList.stream().map(e->e.get("id").toString()).collect(Collectors.toList());

			System.out.println(actualEntityList);
			System.out.println(expEntityList);

			Assert.assertEquals(actualEntityList.size(),expEntityList.size());
			for (Map<String,Object> entity : actualEntityList)
				Assert.assertTrue(expEntityList.contains(entity));
		}
	}

	@Test ( dependsOnMethods = { "createEntities", "updateEntities", "getEntityById","getEntities","getEntitiesLabelLike"},
			alwaysRun = true,
			priority = 0,
			description = "Test Entity-management Entity Endpoint: deleteEntity",
			dataProvider = "entity-management-test-data-provider",
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'deleteEntity' request to entity endpoint interface.")
	@Story("Entity End Point: deleteEntity")
	public void deleteEntities(Map<String, String> paramMaps)
	{
		// Check if the entity to be deleted really exists, if not create it
		if (paramMaps.containsKey("pre-execution"))
		{
			createEntityToBeTest(paramMaps.get("pre-execution"));
		}

		// Read the id from input parameter
		List<String> entityIdToBeDeleteList = Arrays.asList(paramMaps.get("id").split(","));

		if (paramMaps.get("description").contains("good request"))
		{
			// Read the label from input parameter
			List<String> entityLabelToBeDeleteList = Arrays.asList(paramMaps.get("label").split(","));

			// If the entity to be delete exists, use its real id
			if (entityIdToBeDeleteList.size() == entityLabelToBeDeleteList.size())
			{
				for (int i=0;i<entityIdToBeDeleteList.size();i++)
				{
					if (testEntityMap.containsKey(entityLabelToBeDeleteList.get(i)))
					{
						entityIdToBeDeleteList.set(i,testEntityMap.get(entityLabelToBeDeleteList.get(i)).get(0));
					}
				}
				System.out.println(entityIdToBeDeleteList);

				// 此时的entityIdToBeDeleteList已经替换成了真实的entity id
				Response response = EntityManagementEndpoint.deleteEntities(String.join(",",entityIdToBeDeleteList));

				checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));

				// check if the entities are been deleted
				Response responseOfGetEntities = EntityManagementEndpoint.getEntities("");
				List<Map<String,Object>> allEntityList= responseOfGetEntities.jsonPath().getList("data");
				List<String> allIdList = allEntityList.stream().map(e->e.get("id").toString()).collect(Collectors.toList());

				for (String entityId : entityIdToBeDeleteList)
					Assert.assertFalse(allIdList.contains(entityId));
			}
		}
		else
		{
			Response response = EntityManagementEndpoint.deleteEntities(String.join(",",entityIdToBeDeleteList));
			checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));
		}
	}

	@Test ( alwaysRun = true,
			priority = 0,
			description = "Test Entity-management Relation Endpoint: createRelations",
			dataProvider = "entity-management-test-data-provider",
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'createRelations' request to Relation endpoint interface.")
	@Story("Relation Endpoint: createRelations")
	public void createRelations(Map<String,String> paramMaps)
	{
		// Check if the entity to be used in createRelations really exists, if not create it
		if (paramMaps.containsKey("pre-execution"))
		{
			createEntityToBeTest(paramMaps.get("pre-execution"));
		}

		// 此时的bodyString为excel中读取的数据
		String bodyString = paramMaps.get("body");

		// 将bodyString中的占位符表示的值替换成real entity id
		for (Map.Entry<String,List<String>> entity : testEntityMap.entrySet())
		{
			if (bodyString.contains("$" + entity.getKey()))
			{
				bodyString = bodyString.replaceAll("\\$" + entity.getKey(),entity.getValue().get(0));
			}
		}

		// 此时的bodyString不含占位符
		System.out.println("bodyString:" + bodyString);
		Response response = EntityManagementEndpoint.createRelations(bodyString);
		System.out.println(response.jsonPath().prettify());

		// // 如果接口创建relation成功，将其放入testRelationMap中
		if (response.jsonPath().getString("code").equals(sucessfulRspCode))
		{
			List<Map<String,String>> relationList = response.jsonPath().getList("data");
			for (Map<String,String> relation : relationList)
			{
				if (!testRelationMap.containsKey(relation.get("label"))) {
					testRelationMap.put(relation.get("label"), new ArrayList<String>() {
						{
							this.add(relation.get("id"));
						}
					});
				}
				else
					testRelationMap.get(relation.get("label")).add(relation.get("id"));
			}
		}

		checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));

		checkResponseWarningError(paramMaps,response);

		if(paramMaps.get("description").contains("good request"))
		{
			List<Map<String,Object>> requestBodyList = null;
			List<Map<String,Object>> responseEntityList = null;

			try {
				requestBodyList = (new ObjectMapper()).readValue(bodyString, new TypeReference<List<Map<String, Object>>>() {});
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

			responseEntityList = response.jsonPath().getList("data");

			// check the response data whether matches the request
			checkRelationResponseData(requestBodyList,responseEntityList);
		}
	}

	@Test ( dependsOnMethods = { "createRelations" },
			alwaysRun = true,
			priority = 0,
			description = "Test Entity-management Relation Endpoint: updateRelations",
			dataProvider = "entity-management-test-data-provider",
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'updateRelations' request to Relation endpoint interface.")
	@Story("Relation Endpoint: updateRelations")
	public void updateRelations(Map<String,String> paramMaps)
	{
		// Check if the entity to be used in createRelations really exists, if not create it
		if (paramMaps.containsKey("pre-executionOfCreateEntities"))
		{
			createEntityToBeTest(paramMaps.get("pre-executionOfCreateEntities"));
		}

		// Check if the relation to be used in updateRelations really exists, if not create it
		if (paramMaps.containsKey("pre-execution"))
		{
			// 此时的preBodyString为excel中读取的数据
			String preBodyString = paramMaps.get("pre-execution");

			// 将preBodyString中的占位符表示的值替换掉
			for (Map.Entry<String,List<String>> entity : testEntityMap.entrySet())
			{
				if (preBodyString.contains("$" + entity.getKey()))
				{
					preBodyString = preBodyString.replaceAll("\\$" + entity.getKey(),entity.getValue().get(0));
				}
			}
			// 此时的preBodyString不含占位符
			createRelationToBeTest(preBodyString);
		}

		// 此时的beforeReplacementBodyString为excel中读取的数据
		String beforeReplacementBodyString = paramMaps.get("body");

		// 将beforeReplacementBodyString中的占位符表示的值替换掉
		for (Map.Entry<String,List<String>> entity : testEntityMap.entrySet())
		{
			if (beforeReplacementBodyString.contains("$" + entity.getKey()))
			{
				beforeReplacementBodyString = beforeReplacementBodyString.replaceAll("\\$" + entity.getKey(),entity.getValue().get(0));
			}
		}
		System.out.println("beforeReplacementBodyString:" + beforeReplacementBodyString);

		if (paramMaps.get("description").contains("good request"))
		{
			List<Map<String, Object>> requestBodyList = null;

			try {
				requestBodyList = (new ObjectMapper()).readValue(beforeReplacementBodyString, new TypeReference<List<Map<String, Object>>>() {
				});
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

			// If the entity to be test really exists, use its real id
			for (Map<String, Object> requestBody : requestBodyList) {
				// 如果某个label对应多个id，取第一个
				requestBody.put("id", testRelationMap.get(requestBody.get("label")).get(0));
			}

			// bodyString：替换成真实id了的bodyString
			String bodyString = com.alibaba.fastjson.JSONObject.toJSONString(requestBodyList);
			System.out.println("bodyString:" + bodyString);

			Response response = EntityManagementEndpoint.updateRelations(bodyString);
			System.out.println(response.jsonPath().prettify());

			checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));

			checkResponseWarningError(paramMaps,response);

			// check the response data size
			checkResponseDataSize(paramMaps,response.jsonPath().getList("data").size());

			List<Map<String,Object>> responseEntityList = response.jsonPath().getList("data");

			// check the response data whether matches the request
			checkRelationResponseData(requestBodyList,responseEntityList);
		}
		else
		{
			Response response = EntityManagementEndpoint.updateRelations(beforeReplacementBodyString);
			System.out.println(response.jsonPath().prettify());

			checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));

			checkResponseWarningError(paramMaps,response);

			// checkResponseDataSize(paramMaps,response.jsonPath().getList("data").size());
		}
	}

	@Test ( dependsOnMethods = { "createRelations" },
			alwaysRun = true,
			priority = 0,
			description = "Test Entity-management Relation Endpoint: getRelationById",
			dataProvider = "entity-management-test-data-provider",
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'getRelationById' request to Relation endpoint interface.")
	@Story("Relation Endpoint: getRelationById")
	public void getRelationById(Map<String,String> paramMaps)
	{
		// Check if the entity to be used in createRelations really exists, if not create it
		if (paramMaps.containsKey("pre-executionOfCreateEntities"))
		{
			createEntityToBeTest(paramMaps.get("pre-executionOfCreateEntities"));
		}

		// Check if the relation to be used in getRelationById really exists, if not create it
		if (paramMaps.containsKey("pre-execution"))
		{
			// 此时的preBodyString为excel中读取的数据
			String preBodyString = paramMaps.get("pre-execution");

			// 将preBodyString中的占位符表示的值替换掉
			for (Map.Entry<String,List<String>> entity : testEntityMap.entrySet())
			{
				if (preBodyString.contains("$" + entity.getKey()))
				{
					preBodyString = preBodyString.replaceAll("\\$" + entity.getKey(),entity.getValue().get(0));
				}
			}
			// 此时的preBodyString不含占位符
			createRelationToBeTest(preBodyString);
		}

		// Read the id from input parameter
		String relationId = paramMaps.get("id");

		// If the entity to be test exists, use its real id
		if (testRelationMap.containsKey(paramMaps.get("label")))
			relationId = testRelationMap.get(paramMaps.get("label")).get(0);

		Response response = EntityManagementEndpoint.getRelationById(relationId);

		checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));

		// 校验返回的relation是否正确
		if (paramMaps.get("description").contains("good request"))
		{
			Response responseOfGetRelations = EntityManagementEndpoint.getRelations("");
			List<Map<String,Object>> allRelationList = responseOfGetRelations.jsonPath().getList("data");

			String finalEntityId = relationId;
			List<Map<String,Object>> expRelationList = allRelationList.stream().filter(e -> e != null && e.get("id") != null &&
					e.get("id").equals(finalEntityId)).collect(Collectors.toList());

			Assert.assertEquals(response.jsonPath().getMap("data"),expRelationList.get(0));
		}
	}

	@Test ( dependsOnMethods = { "createRelations" },
			alwaysRun = true,
			priority = 0,
			description = "Test Entity-management Relation Endpoint: getRelations",
			dataProvider = "entity-management-test-data-provider",
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'getRelations' request to Relation endpoint interface.")
	@Story("Relation Endpoint: getRelations")
	public void getRelations(Map<String,String> paramMaps)
	{
		Response response = EntityManagementEndpoint.getRelations(paramMaps.get("labels"));

		checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));

		List<Map<String,Object>> responseRelationList= response.jsonPath().getList("data");
		List<String> responseIdList = responseRelationList.stream().map(e -> e.get("id").toString()).collect(Collectors.toList());

		if (paramMaps.get("description").contains("good request") && !paramMaps.get("description").contains("entities don't exist"))
		{
			if (paramMaps.get("labels") != null)
			{
				List<Map<String,Object>> allRelationList = EntityManagementEndpoint.getRelations("").jsonPath().getList("data");
				checkLabels(paramMaps,allRelationList,responseRelationList);
			}
			else
			{
				for (Map.Entry<String,List<String>> entry : testRelationMap.entrySet())
				{
					for (String id : entry.getValue())
					{
						Assert.assertTrue(responseIdList.contains(id));
					}
				}
			}
		}
		else
		{
			Assert.assertTrue(responseRelationList.isEmpty());
		}
	}

	@Test ( dependsOnMethods = { "createRelations","updateRelations","getRelationById","getRelations" },
			alwaysRun = true,
			priority = 0,
			description = "Test Entity-management Relation Endpoint: deleteRelations",
			dataProvider = "entity-management-test-data-provider",
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'deleteRelations' request to Relation endpoint interface.")
	@Story("Relation Endpoint: deleteRelations")
	public void deleteRelations(Map<String,String> paramMaps)
	{
		// Check if the entity to be used in createRelations really exists, if not create it
		if (paramMaps.containsKey("pre-executionOfCreateEntities"))
		{
			createEntityToBeTest(paramMaps.get("pre-executionOfCreateEntities"));
		}

		// Check if the relation to be used in deleteRelations really exists, if not create it
		if (paramMaps.containsKey("pre-execution"))
		{
			// 此时的preBodyString为excel中读取的数据
			String preBodyString = paramMaps.get("pre-execution");

			// 将preBodyString中的占位符表示的值替换掉
			for (Map.Entry<String,List<String>> entity : testEntityMap.entrySet())
			{
				if (preBodyString.contains("$" + entity.getKey()))
				{
					preBodyString = preBodyString.replaceAll("\\$" + entity.getKey(),entity.getValue().get(0));
				}
			}
			// 此时的preBodyString不含占位符
			createRelationToBeTest(preBodyString);
		}

		// Read the id from input parameter
		List<String> relationIdToBeDeleteList = Arrays.asList(paramMaps.get("id").split(","));

		if (paramMaps.get("description").contains("good request"))
		{
			// Read the label from input parameter
			List<String> relationLabelToBeDeleteList = Arrays.asList(paramMaps.get("label").split(","));

			// If the entity to be delete exists, use its real id
			if (relationIdToBeDeleteList.size() == relationLabelToBeDeleteList.size())
			{
				for (int i=0;i<relationIdToBeDeleteList.size();i++)
				{
					if (testRelationMap.containsKey(relationLabelToBeDeleteList.get(i)))
					{
						relationIdToBeDeleteList.set(i,testRelationMap.get(relationLabelToBeDeleteList.get(i)).get(0));
					}
				}
				System.out.println(relationIdToBeDeleteList);

				Response response = EntityManagementEndpoint.deleteRelations(String.join(",",relationIdToBeDeleteList));

				checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));

				// check if the relations are been deleted
				Response responseOfGetRelations = EntityManagementEndpoint.getRelations("");
				List<Map<String,Object>> allRelationList= responseOfGetRelations.jsonPath().getList("data");
				List<String> allIdList = allRelationList.stream().map(e->e.get("id").toString()).collect(Collectors.toList());

				for (String entityId : relationIdToBeDeleteList)
					Assert.assertFalse(allIdList.contains(entityId));
			}
		}
		else
		{
			Response response = EntityManagementEndpoint.deleteRelations(String.join(",",relationIdToBeDeleteList));
			checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));
		}
	}


	@Test ( alwaysRun = true,
			priority = 0,
			description = "Test Entity-management Entity Endpoint: publishCheck",
			dataProvider = "entity-management-test-data-provider",
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'publishCheck' request to entity endpoint interface.")
	@Story("Entity End Point: publishCheck")
	public void publishCheck(Map<String, String> paramMaps){
		String bodyString = paramMaps.get("body");
		Response response = EntityManagementEndpoint.publishCheck(bodyString);

		checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));
		Map<String,List<String>> data = response.jsonPath().get("data");
		//check valid publish action without warning and error
		if(paramMaps.get("description").contains("good request")){
			assertThat(data.get("warning"),is(empty()));
			assertThat(data.get("error"),is(empty()));
		}

		if(paramMaps.containsKey("warningRule")){
			List<Map<String,Object>> rspWarningList = response.jsonPath().getList("data.warning");
			List<String> rspWarningRuleList = rspWarningList.stream().map(e -> e.get("rule").toString()).collect(Collectors.toList());
			assertThat(rspWarningRuleList,hasItem(paramMaps.get("warningRule")));
		}

		if(paramMaps.containsKey("errorRule")){
			List<Map<String,Object>> rspErrorList = response.jsonPath().getList("data.error");
			List<String> rspErrorRuleList = rspErrorList.stream().map(e -> e.get("rule").toString()).collect(Collectors.toList());
			assertThat(rspErrorRuleList,hasItem(paramMaps.get("errorRule")));
		}
	}

	@Test ( alwaysRun = true,
			priority = 0,
			description = "Test Entity-management Entity Endpoint: publishGraph",
			dataProvider = "entity-management-test-data-provider",
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'publishGraph' request to entity endpoint interface.")
	@Story("Entity End Point: publishGraph")
	public void publishGraph(Map<String, String> paramMaps){
		String bodyString = paramMaps.get("body");
		Response response = EntityManagementEndpoint.publish(bodyString);
		if(response.jsonPath().getString("code").equals(sucessfulRspCode)){
			// 查询entityes 获取laebl和id
			Response getEntitiesResponse = EntityManagementEndpoint.getEntities(paramMaps.get("entitylabels"));
			generateTestGrapMap(getEntitiesResponse);
			//查询relation 获取label和id
			Response getRelationsResponse = EntityManagementEndpoint.getRelations(paramMaps.get("relationLabels"));
			System.out.println("debug here:"+getRelationsResponse);
			generateTestGrapMap(getRelationsResponse);
		}
	}

	@Test ( dependsOnMethods = { "publishGraph" },
			alwaysRun = true,
			priority = 0,
			description = "Test Entity-management Entity Endpoint: updateCheck",
			dataProvider = "entity-management-test-data-provider",
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'updateCheck' request to entity endpoint interface.")
	@Story("Entity End Point: updateCheck")
	public void updateCheck(Map<String, String> paramMaps){

		String originalBody = paramMaps.get("body");
		// replace all placeholders of body
		String tempBody = StringUtils.replace(originalBody, "$entity_id1", testGraphMap.get("pc_test1"));
		String replacedEntityBody = StringUtils.replace(tempBody, "$entity_id2", testGraphMap.get("pc_test2"));
		String updateCheckBody = StringUtils.replace(replacedEntityBody, "$edge_id1", testGraphMap.get("pc_edge1"));
		System.out.println(updateCheckBody);
		Response response = EntityManagementEndpoint.publishCheck(updateCheckBody);
		checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));
		Map<String,List<String>> data = response.jsonPath().get("data");
		//check valid publish action without warning and error
		if(paramMaps.get("description").contains("good request")){
			assertThat(data.get("warning"),is(empty()));
			assertThat(data.get("error"),is(empty()));
		}

		if(paramMaps.containsKey("warningRule")){
			List<Map<String,Object>> rspWarningList = response.jsonPath().getList("data.warning");
			List<String> rspWarningRuleList = rspWarningList.stream().map(e -> e.get("rule").toString()).collect(Collectors.toList());
			assertThat(rspWarningRuleList,hasItem(paramMaps.get("warningRule")));
		}

		if(paramMaps.containsKey("errorRule")){
			List<Map<String,Object>> rspErrorList = response.jsonPath().getList("data.error");
			List<String> rspErrorRuleList = rspErrorList.stream().map(e -> e.get("rule").toString()).collect(Collectors.toList());
			assertThat(rspErrorRuleList,hasItem(paramMaps.get("errorRule")));
		}
	}

	// generate testGrapMap to store label and id
	public static void generateTestGrapMap(Response Response) {
		List<Map<String, String>> dataList;
		dataList = Response.jsonPath().getList("data");
		for (Map<String, String> properties : dataList) {
			String label = properties.get("label");
			String id = properties.get("id");
			testGraphMap.put(label, id);
		}
	}
	// Check if the entity with the given location & label really exists, if not try to create it
	public static void createEntityToBeTest(String preBodyString)
	{
		List<Map<String,Object>> preRequestBodyList = null;
		try {
			preRequestBodyList = (new ObjectMapper()).readValue(preBodyString, new TypeReference<List<Map<String, Object>>>() {});
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		for (Map<String,Object> preRequestBody : preRequestBodyList)
		{
			if (!testEntityMap.containsKey(preRequestBody.get("label")))
			{
				org.json.JSONObject jsonObject = new JSONObject(preRequestBody);
				String id = createEntity("[" + jsonObject + "]");

				testEntityMap.put(preRequestBody.get("label").toString(), new ArrayList<String>() {
					{
						this.add(id);
					}
				});
			}
		}
	}

	public static String createEntity(String bodyStringForSingleEntity)
	{
		String result = "none";

		Response response = EntityManagementEndpoint.createEntities(bodyStringForSingleEntity);

		if (response.jsonPath().getString("message").contains("OK"))
			result = response.jsonPath().getString("data[0].id");

		return result;
	}

	// Check if the relation with the given location & label really exists, if not try to create it
	public static void createRelationToBeTest(String preBodyString)
	{
		List<Map<String,Object>> preRequestBodyList = null;
		try {
			preRequestBodyList = (new ObjectMapper()).readValue(preBodyString, new TypeReference<List<Map<String, Object>>>() {});
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		for (Map<String,Object> preRequestBody : preRequestBodyList)
		{
			if (!testRelationMap.containsKey(preRequestBody.get("label")))
			{
				org.json.JSONObject jsonObject = new JSONObject(preRequestBody);
				String id = createRelation("[" + jsonObject + "]");

				testRelationMap.put(preRequestBody.get("label").toString(), new ArrayList<String>() {
					{
						this.add(id);
					}
				});
			}
		}
	}

	public static String createRelation(String bodyStringForSingleRelation)
	{
		String result = "none";

		Response response = EntityManagementEndpoint.createRelations(bodyStringForSingleRelation);

		if (response.jsonPath().getString("message").contains("OK"))
			result = response.jsonPath().getString("data[0].id");

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

	@Step("Verify the response warning or error")
	public static void checkResponseWarningError(Map<String, String> requestParameters, Response response)
	{
		if (requestParameters.get("description").contains("good request"))
		{
			Assert.assertTrue(response.jsonPath().get("data") instanceof List);

			List<Map<String,Object>> dataList = response.jsonPath().getList("data");
			for (Map<String,Object> dataItem : dataList)
			{
				Assert.assertFalse(dataItem.containsKey("warning"));
				Assert.assertFalse(dataItem.containsKey("error"));
			}
		}

		if (requestParameters.containsKey("warning"))
		{
			Assert.assertTrue(response.jsonPath().get("data") instanceof Map);

			List<String> expectedwarningList = Arrays.asList(requestParameters.get("warning").split(","));

			List<Map<String,Object>> responsewarningList = response.jsonPath().getList("data.warning");

			List<String> responseRuleList = responsewarningList.stream().map(e -> e.get("rule").toString()).collect(Collectors.toList());

			Assert.assertEquals(responseRuleList,expectedwarningList);

			for (String warningItem : expectedwarningList)
				Assert.assertTrue(responseRuleList.contains(warningItem));
		}

		if (requestParameters.containsKey("error"))
		{
			Assert.assertTrue(response.jsonPath().get("data") instanceof Map);

			List<String> expectedErrorList = Arrays.asList(requestParameters.get("error").split(","));

			List<Map<String,Object>> responseErrorList = response.jsonPath().getList("data.error");

			List<String> responseRuleList = responseErrorList.stream().map(e -> e.get("rule").toString()).collect(Collectors.toList());

			Assert.assertEquals(responseRuleList.size(),expectedErrorList.size());

			for (String errorItem : expectedErrorList)
				Assert.assertTrue(responseRuleList.contains(errorItem));
		}
	}

	@Step("Verify the response data size")
	public static void checkResponseDataSize(Map<String, String> requestParameters,int actualRspDataSize)
	{
		if (requestParameters.containsKey("rspDataSize"))
		{
			int expRspDataSize = Integer.valueOf(requestParameters.get("rspDataSize")).intValue();
			Assert.assertEquals(actualRspDataSize,expRspDataSize);
		}
	}

	@Step("Verify if the createEntities/updateEntities response contains correct information")
	public static void checkEntityResponseData(List<Map<String,Object>> requestBodyList, List<Map<String,Object>> responseEntityList)
	{
		Assert.assertEquals(requestBodyList.size(),responseEntityList.size());

		for (int i=0;i<requestBodyList.size();i++)
		{
			Assert.assertEquals(requestBodyList.get(i).get("label"),responseEntityList.get(i).get("label"));
			Assert.assertEquals(requestBodyList.get(i).get("properties"),responseEntityList.get(i).get("properties"));
		}
	}

	@Step("Verify if the createRelations/updateRelations response contains correct information")
	public static void checkRelationResponseData(List<Map<String,Object>> requestBodyList, List<Map<String,Object>> responseEntityList)
	{
		Assert.assertEquals(requestBodyList.size(),responseEntityList.size());

		for (int i=0;i<requestBodyList.size();i++)
		{
			Assert.assertEquals(requestBodyList.get(i).get("label"),responseEntityList.get(i).get("label"));
			Assert.assertEquals(requestBodyList.get(i).get("properties"),responseEntityList.get(i).get("properties"));
			Assert.assertEquals(requestBodyList.get(i).get("out"),responseEntityList.get(i).get("out"));
			Assert.assertEquals(requestBodyList.get(i).get("in"),responseEntityList.get(i).get("in"));
		}
	}

	@Step("Verify if the entity/relation labels in the response match the labels parameter of the request")
	public static void checkLabels(Map<String, String> requestParameters, List<Map<String,Object>> allDataList,List<Map<String,Object>> responseDataList)
	{
		if (requestParameters.containsKey("labels"))
		{
			List<String> labelList = Arrays.asList(requestParameters.get("labels").split(","));

			List<Map<String,Object>> expDataList = allDataList.stream().filter(e -> labelList.contains(e.get("label").toString())).collect(Collectors.toList());

			System.out.println(responseDataList);
			System.out.println(expDataList);
			Assert.assertEquals(responseDataList.size(),expDataList.size());

			Set<Map<String,Object>> responseDataSet = new HashSet<>(responseDataList);
			Set<Map<String,Object>> expDataSet = new HashSet<>(expDataList);

			Assert.assertEquals(responseDataSet,expDataSet);
		}
	}
}