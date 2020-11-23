package com.siemens.datalayer.connector.test;

import java.util.*;

import io.qameta.allure.*;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.siemens.datalayer.connector.model.*;
import com.siemens.datalayer.utils.AllureEnvironmentPropertiesWriter;

import org.testng.Assert;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;

@Epic("SDL Connector")
@Feature("Rest API")
public class InterfaceTests {
	
	@Parameters({"base_url", "port", "domain_name"})
	@BeforeClass (description = "Configure the host address and communication port of Connector Interface")
	public void setConnectorEndpoint(@Optional("http://localhost") String base_url, @Optional("9001") String port, String domain_name) {
	    Endpoint.setBaseUrl(base_url);
	    Endpoint.setPort(port);
	    Endpoint.setDomain(domain_name);
	    AllureEnvironmentPropertiesWriter.addEnvironmentItem("Connector Address", base_url + ":" + port);
	}
	 
	@Test (priority = 0, description = "Test connector interface: Get All Entities name.")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'Get All Entities name' request to read out all the available entity names.")
	@Story("Connctor Interface: Get all entities name")
	public void GetAllEntitiesName()
	{	
	  Response response = Endpoint.getAllEntitiesName();
	  
	  // This is an example of processing the response message via POJO  
	  GetAllEntitiesNameResponse rspBody = response.getBody().as(GetAllEntitiesNameResponse.class);
		
	  Assert.assertEquals("Operate success.", rspBody.getMessage());

	}
  
	@Test (priority = 0, description = "Test entity interface: Get concept model definition by model name.")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'searchModelSchemaByName' request to read out the model schema of the entity specified in the name parameter.")
	@Story("Entity Interface: Search model schema by name")
  	public void SearchModelSchemaByName()
  	{
	  Response response = Endpoint.getAllEntitiesName();

	  JsonPath jsonPathEvaluator = response.jsonPath();
	 
	  List<String> allEntities = jsonPathEvaluator.getList("data");
		  
	  response = Endpoint.searchModelSchemaByName("default", allEntities.get(0));
	
	  // This is an example of extracting information from the response message via Json path  
	  Assert.assertEquals("Operate success.", response.jsonPath().getString("message"));
		
  	}
  
	@Test (priority = 0, description = "Test connector interface: Get all entities name and then check its concept model.")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'Get All Entities name' request to get all the available entity names, then read out the model schema of every entity.")
	@Story("Entity Interface: Search model schema by name")
  	public void SearchModelSchemaForAllEntities()
  	{		
	  Response response = Endpoint.getAllEntitiesName();
	  
	  Assert.assertEquals(response.getStatusCode(), 200, "Correct status code returned");
	  
	  JsonPath jsonPathEvaluator = response.jsonPath();

	  List<String> allEntities = jsonPathEvaluator.getList("data");
	  
	  for(String entityItem : allEntities)
	  {
		  response = Endpoint.searchModelSchemaByName("default", entityItem);
		  
		  Assert.assertEquals(response.getStatusCode(), 200, "Correct status code returned");
	  }
  	}
  
	@Test (	priority = 0, 
			description = "Test connector interface: Get concept model data by condition.", 
			dataProvider = "connector-test-data-provider", 
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'getConceptModelDataByCondition' request with specified parameters and check the response message.")
	@Story("Connector Interface: Get concept model data by condition")
  	public void getConceptModelDataByCondition(Map<String, String> paramMaps)
  	{  
	  HashMap<String, String> queryParameters = new HashMap<>();
	  
	  if (paramMaps.containsKey("condition")) 		queryParameters.put("condition", paramMaps.get("condition"));  
	  if (paramMaps.containsKey("domainName")) 		queryParameters.put("domainName", paramMaps.get("domainName"));
	  if (paramMaps.containsKey("name")) 			queryParameters.put("name", paramMaps.get("name"));
	  if (paramMaps.containsKey("fields")) 			queryParameters.put("fields", paramMaps.get("fields"));
	  if (paramMaps.containsKey("order")) 			queryParameters.put("order", paramMaps.get("order"));
	  if (paramMaps.containsKey("pageIndex")) 		queryParameters.put("pageIndex", paramMaps.get("pageIndex"));
	  if (paramMaps.containsKey("pageSize")) 		queryParameters.put("pageSize", paramMaps.get("pageSize"));
	  if (paramMaps.containsKey("timeout")) 		queryParameters.put("pageSize", paramMaps.get("timeout")); 
	  
	  Response response = Endpoint.getConceptModelDataByCondition(queryParameters);
	  
	  checkResponseCode(paramMaps, response);  
	  
	  if (paramMaps.get("description").contains("data retrieved"))
	  {	  
		  if (paramMaps.containsKey("fields"))
		  {
			  // TBD: add fields check here
		  }
		  else
		  {
			  if (response.getBody().asString().contains("totalPages")) // If data returned in pagination format
			  {
				  Assert.assertTrue(response.jsonPath().getList("data.data").size() > 0);
				  assertThat(response.getBody().asString(), 
						  	 matchesJsonSchemaInClasspath(Endpoint.getResourcePath() + "JasonModelSchemaFor" + paramMaps.get("name") + "P.JSON"));
			  }
			  else
			  {
				  Assert.assertTrue(response.jsonPath().getList("data").size() > 0);			  
				  assertThat(response.getBody().asString(), 
						  	 matchesJsonSchemaInClasspath(Endpoint.getResourcePath() + "JasonModelSchemaFor" + paramMaps.get("name") + ".JSON"));
			  }
		  }
	  }
	  else 
	  {
		  Assert.assertNull(response.jsonPath().getList("data"));
	  }

  	}
	
	public void checkResponseCode(Map<String, String> paramMaps, Response response)
	{
		  int expStatusCode = 200;	// If not specified, the expected status code is set to 200 (OK)
		  if (paramMaps.containsKey("rspStatus")) expStatusCode = Integer.valueOf(paramMaps.get("rspStatus")).intValue();
		  Assert.assertEquals(response.getStatusCode(), expStatusCode, "The status code in response message matches the expected value.");
		  
		  if ((paramMaps.containsKey("rspCode")))
		  {
			  Assert.assertEquals(response.jsonPath().getString("code"), paramMaps.get("rspCode"));
		  }
		  else
		  {
			  if (paramMaps.get("description").contains("good request")) 
				  Assert.assertEquals(response.jsonPath().getString("code"), "0");
			  else
				  System.out.println("No error code is specified for test case： " + paramMaps.get("test-id"));
		  }		  
		  
		  if (paramMaps.containsKey("rspMessage"))
		  {
			  Assert.assertTrue(response.jsonPath().getString("message").contains(paramMaps.get("rspMessage")));
		  }
		  else
		  {
			  if (paramMaps.get("description").contains("good request")) 
				  Assert.assertEquals(response.jsonPath().getString("message"), "Operate success.");
			  else
				  System.out.println("No response message is specified for test case： " + paramMaps.get("test-id"));
		  }
	}
}
