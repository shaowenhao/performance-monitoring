package com.siemens.datalayer.connector.test;

import java.util.HashMap;
import java.util.List;

import io.qameta.allure.*;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.siemens.datalayer.connector.model.GetAllEntitiesNameResponse;
import com.siemens.datalayer.connector.model.SearchModelSchemaByNameResponse;

import org.testng.Assert;
import org.testng.Reporter;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;

@Epic("Regression Tests")
@Feature("Connector Rest API Tests")
public class InterfaceTests {
	
	@Parameters({"base_url", "port"})
	@BeforeClass
	public void setConnectorEndpoint(@Optional("http://localhost") String base_url, @Optional("9001") String port) {
	    Endpoint.setBaseUrl(base_url);
	    Endpoint.setPort(port);
	}
    
	@Test (priority = 0, description="Test connector interface: Get All Entities name.")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT and verify if all the available entity names can be read out.")
	@Story("Connector Interface API design")
	public void GetAllEntitiesName()
	{
	  Reporter.log("Send a 'GetAllEntitiesName' request");
		
	  Response response = Endpoint.getAllEntitiesName();

	  Reporter.log("Response status is " + response.getStatusCode());
		
	  Reporter.log("Response Body is =>  " + response.getBody().asString());
		
	  GetAllEntitiesNameResponse rspBody = response.getBody().as(GetAllEntitiesNameResponse.class);
		
	  Assert.assertEquals("Operate success.", rspBody.getMessage());

	}
  
	@Test (priority = 0, description="Test connector interface: Get concept model definition by model name.")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT to read out the model schema of entity 'Site'.")
	@Story("Connector Interface API design")
  	public void SearchModelSchemaByName()
  	{
	  Reporter.log("Send a 'SearchModelSchemaByName' request for entity 'Site'");	
	  
	  Response response = Endpoint.getConceptModelDefinitionByModelName("Site");
		
	  Reporter.log("Response status is " + response.getStatusCode());
		  
	  Reporter.log("Response Body is =>  " + response.asString());
		
	  SearchModelSchemaByNameResponse rspBody = response.getBody().as(SearchModelSchemaByNameResponse.class);
		
	  Assert.assertEquals("Operate success.", rspBody.getMessage());
		
  	}
  
	@Test (priority = 0, description="Test connector interface: Get all entities name and then check its concept model.")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT to get all the available entity names, then read out the model schema of every entity.")
	@Story("Connector Interface API design")
  	public void SearchModelSchemaForAllEntities()
  	{
	  Reporter.log("Send a 'GetAllEntitiesName' request");
		
	  Response response = Endpoint.getAllEntitiesName();
	  
	  Reporter.log("Response status is " + response.getStatusCode());
	  
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
		  Reporter.log("Send a 'SearchModelSchemaByName' request for entity '" + entityItem + "'");
		  
		  response = Endpoint.getConceptModelDefinitionByModelName(entityItem);
		  
		  Reporter.log("Response status is " + response.getStatusCode());
		  
		  Assert.assertEquals(response.getStatusCode(), 200, "Correct status code returned");
	  }
  	}
  
	@Test (priority = 0, description="Test connector interface: Get concept model data by condition.")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT to read out 10 entities of 'Customer' type.")
	@Story("Connector Interface API design")
  	public void SearchModelDataByCondition()
  	{
	  Reporter.log("Send a 'SearchModelDataByCondition' request");
	  
	  HashMap<String, String> queryParameters = new HashMap<>();
	  queryParameters.put("name", "Customer");
	  queryParameters.put("pageIndex", "1");
	  queryParameters.put("pageSize", "10");
		
	  Response response = Endpoint.getConceptModelDataByCondition(queryParameters);
	  
	  Reporter.log("Response status is " + response.getStatusCode());
	  
	  Reporter.log("Response Body is =>  " + response.asString());
	  
	  Assert.assertEquals(response.getStatusCode(), 200, "Correct status code returned");
	  
	  assertThat(response.getBody().asString(), matchesJsonSchemaInClasspath("JasonModelShemaForCustomer.JSON"));
  	}
}
