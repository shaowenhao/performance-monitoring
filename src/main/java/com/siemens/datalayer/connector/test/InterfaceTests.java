package com.siemens.datalayer.connector.test;

import java.util.*;

import io.qameta.allure.*;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.siemens.datalayer.connector.model.GetAllEntitiesNameResponse;
import com.siemens.datalayer.connector.model.SearchModelSchemaByNameResponse;
import com.siemens.datalayer.utils.AllureEnvironmentPropertiesWriter;

import org.testng.Assert;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;

@Epic("Connector Interface")
@Feature("Rest API")
public class InterfaceTests {
	
	@Parameters({"base_url", "port"})
	@BeforeClass (description = "Configure host address and communication port for Connector service")
	public void setConnectorEndpoint(@Optional("http://localhost") String base_url, @Optional("9001") String port) {
	    Endpoint.setBaseUrl(base_url);
	    Endpoint.setPort(port);
	    AllureEnvironmentPropertiesWriter.addEnvironmentItem("Connector Address", base_url + ":" + port);
	}
	 
	@Test (priority = 0, description = "Test connector interface: Get All Entities name.")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT and verify if all the available entity names can be read out.")
	@Story("Get All Entities name")
	public void GetAllEntitiesName()
	{	
	  Response response = Endpoint.getAllEntitiesName();
		
	  GetAllEntitiesNameResponse rspBody = response.getBody().as(GetAllEntitiesNameResponse.class);
		
	  Assert.assertEquals("Operate success.", rspBody.getMessage());

	}
  
	@Test (priority = 0, description = "Test connector interface: Get concept model definition by model name.")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT to read out the model schema of entity 'Site'.")
	@Story("Search Model Schema by name")
  	public void SearchModelSchemaByName()
  	{
	  
	  Response response = Endpoint.getConceptModelDefinitionByModelName("Site");
		
	  SearchModelSchemaByNameResponse rspBody = response.getBody().as(SearchModelSchemaByNameResponse.class);
		
	  Assert.assertEquals("Operate success.", rspBody.getMessage());
		
  	}
  
	@Test (priority = 0, description = "Test connector interface: Get all entities name and then check its concept model.")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT to get all the available entity names, then read out the model schema of every entity.")
	@Story("Search Model Schema by name")
  	public void SearchModelSchemaForAllEntities()
  	{		
	  Response response = Endpoint.getAllEntitiesName();
	  
	  Assert.assertEquals(response.getStatusCode(), 200, "Correct status code returned");
	  
	  // First get the JsonPath object instance from the Response interface
	  JsonPath jsonPathEvaluator = response.jsonPath();
	 
	  // Read all the entities as a List of String. Each item in the list
	  // represent an entity in the REST service Response
	  List<String> allEntities = jsonPathEvaluator.getList("data");
	  
	  // Iterate over the entity list and query each entity's model schema
	  // via 'SearchModelSchemaByName' request 
	  for(String entityItem : allEntities)
	  {
		  response = Endpoint.getConceptModelDefinitionByModelName(entityItem);
		  
		  Assert.assertEquals(response.getStatusCode(), 200, "Correct status code returned");
	  }
  	}
  
	@Test (	priority = 0, 
			description = "Test connector interface: Get concept model data by condition.", 
			dataProvider = "connector-test-data-provider", 
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'SearchModelDataByCondition' request to SUT with specified parameters and check the response message.")
	@Story("Get concept model data by condition")
  	public void SearchModelDataByCondition(Map<String, String> paramMaps)
  	{  
	  HashMap<String, String> queryParameters = new HashMap<>();
	  
	  if (paramMaps.containsKey("name")) 
		  queryParameters.put("name", paramMaps.get("name"));
	  
	  if (paramMaps.containsKey("domainName")) 
		  queryParameters.put("domainName", paramMaps.get("domainName"));
	  
	  if (paramMaps.containsKey("pageIndex")) 
		  queryParameters.put("pageIndex", paramMaps.get("pageIndex"));
	  
	  if (paramMaps.containsKey("pageSize"))
		  queryParameters.put("pageSize", paramMaps.get("pageSize"));
	  
	  if (paramMaps.containsKey("condition")) 
		  queryParameters.put("condition", paramMaps.get("condition"));
	  
	  if (paramMaps.containsKey("fields")) 
		  queryParameters.put("fields", paramMaps.get("fields"));
	  
	  if (paramMaps.containsKey("order"))
		  queryParameters.put("order", paramMaps.get("order"));
		
	  Response response = Endpoint.getConceptModelDataByCondition(queryParameters);
	  
	  Assert.assertEquals(response.getStatusCode(), 200);
	  
	  if (paramMaps.containsKey("expectCode"))
		  Assert.assertEquals(response.jsonPath().getString("code"), paramMaps.get("expectCode"));
	  
	  if (paramMaps.containsKey("expectMessage"))
		  Assert.assertTrue(response.jsonPath().getString("message").contains(paramMaps.get("expectMessage")));
	  
	  if (paramMaps.get("description").contains("good request")) 
	  {	  
		  if (paramMaps.get("description").contains("data not found")) 
			  Assert.assertTrue(response.jsonPath().getList("data").isEmpty());
		  
		  if (paramMaps.get("description").contains("data retrieved")) 
		  {
			  Assert.assertTrue(response.jsonPath().getList("data").size() > 0);
		  
			  assertThat(response.getBody().asString(), 
					  	 matchesJsonSchemaInClasspath("JasonModelSchemaFor" + paramMaps.get("name") + ".JSON"));
		  }
	  }
	  else 
	  {
		  Assert.assertNull(response.jsonPath().getList("data"));
	  }

  	}
}
