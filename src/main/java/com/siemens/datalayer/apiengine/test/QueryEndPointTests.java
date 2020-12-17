package com.siemens.datalayer.apiengine.test;

import java.util.*;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

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

@Epic("SDL Api-engine")
@Feature("Query End Point")

public class QueryEndPointTests {
	
	@Parameters({"base_url", "port"})
	@BeforeClass (description = "Configure the host address and communication port of data-layer-api-engine")
	public void setApiEngineEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("30035") String port) 
	{
        ApiEngineEndpoint.setBaseUrl(base_url);
        ApiEngineEndpoint.setPort(port);
	    AllureEnvironmentPropertiesWriter.addEnvironmentItem("data-layer-api-engine", base_url + ":" + port);
	}
	
	@Test ( priority = 0, 
			description = "Test Api-engine Query Endpoint: Restful interface", 
			dataProvider = "api-engine-test-data-provider", 
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'getData' request to restful query interface.")
	@Story("Query End Point: Restful Interface")
	public void getDataEntities(Map<String, String> paramMaps)
	{
		HashMap<String, String> queryParameters = new HashMap<>();
		  
		if (paramMaps.containsKey("depth")) 		queryParameters.put("depth", paramMaps.get("depth"));  
		if (paramMaps.containsKey("filter")) 		queryParameters.put("filter", paramMaps.get("filter"));
		if (paramMaps.containsKey("root")) 		queryParameters.put("root", paramMaps.get("root"));
		  
		Response response = ApiEngineEndpoint.getEntities(queryParameters);
		  
		checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message")); 
		  
		if (paramMaps.containsKey("filter"))
		{
			HashMap<String, String> filterParameters = new HashMap<>();
			parseFilterParameters(paramMaps.get("filter"), filterParameters);
		  
			if (filterParameters.containsKey("name")) 
			{
				String entityName = filterParameters.get("name");
				  
				Assert.assertTrue(response.getBody().asString().contains(entityName));
				  
				String entityListPath = "data." + entityName;
				if (filterParameters.containsKey("pagination")) entityListPath+= ".content";
				  
				if (response.body().asString().contains("\"" + entityName + "\":null"))
				{
					Assert.assertFalse(paramMaps.get("description").contains("data retrieved"), "The response message does not contain any data entries.");
				}
				else
				{
					List<HashMap<String, String>> entityList= response.jsonPath().getList(entityListPath);
					  
					// Check if the returned data list is not empty
					Assert.assertTrue(entityList.size() > 0);
					  
					// Check if the data in response message contains the specified fields
					if (filterParameters.containsKey("field"))
						CommonCheckFunctions.checkDataContainsSpecifiedFields(filterParameters.get("field"), entityList);
					  
					if (filterParameters.containsKey("condition")) 
						checkCondition(filterParameters.get("condition"), entityList);
					  
					// Check if the data in response message is sorted by the specified order parameters
					if (filterParameters.containsKey("order")) 
						checkOrder(filterParameters.get("order"), entityList);
					  
					// Check if the data is returned in correct pagination format 
					if (filterParameters.containsKey("pagination"))
						checkPaginationFormat(filterParameters.get("pagination"), 
											  response.jsonPath().get("data." + entityName + ".page"), 
											  response.jsonPath().get("data." + entityName + ".pageSize"));		
				}
			}
		}
	}
	
	@Test ( priority = 0, 
			description = "Test Api-engine Query Endpoint: GraphQL interface", 
			dataProvider = "api-engine-test-data-provider", 
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Post a 'getData' request to graphql query interface.")
	@Story("Query End Point: GraphQL Interface")
	public void getDataGraphQL(Map<String, String> paramMaps)
	{
		Response response = ApiEngineEndpoint.postGraphql(paramMaps.get("query"));
		
		checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));
		
		if (paramMaps.get("description").contains("good request"))
		{
			HashMap<String, String> queryParameters = new HashMap<>();
			parseQueryString(paramMaps.get("query"), queryParameters);
			
			Assert.assertTrue(response.getBody().asString().contains(queryParameters.get("entity")));
			
			String entityListPath = "data." + queryParameters.get("entity");
			
			if (paramMaps.get("description").contains("data retrieved"))
			{
				List<HashMap<String, String>> entityList = response.jsonPath().getList(entityListPath);

				if (queryParameters.containsKey("condition"))
				{
					if (queryParameters.get("condition").contains("},"))
						System.out.println("Multiple conditions found: " + queryParameters.get("condition"));
					else
						Assert.assertTrue(verifySingleCondition(queryParameters.get("condition"), entityList));
				}
				
				if (queryParameters.containsKey("field"))
				{				
					Scanner scanner = new Scanner(queryParameters.get("field"));
					
					while (scanner.hasNext())
					{
						String fieldStr = scanner.next();	
						if (fieldStr.contains("{"))
						{
							System.out.println("sub-entity found:" + fieldStr);
							
							while (scanner.hasNext())
							{
								String subFieldStr = scanner.next();
								if (subFieldStr.contains("}")) break;
							}
						}
						else
							CommonCheckFunctions.checkDataContainsSpecifiedFields(fieldStr, entityList);
					}	
					  
					scanner.close();
				}
			}
			else
			{
				Assert.assertNull(response.jsonPath().get(entityListPath), "The response message does not contain any data.");
			}
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
			Assert.assertEquals(actualCode, requestParameters.get("rspCode"), "The operation code in response message matches the expected value.");
		}
		else
		{
			if (requestParameters.get("description").contains("good request")) 
				Assert.assertEquals(actualCode, "100000", "The operation code in response message matches the expected value.");
			else
				System.out.println("Operation code is not specified for test case： " + requestParameters.get("test-id"));
		}		  
		  
		if (requestParameters.containsKey("rspMessage"))
		{
			Assert.assertTrue(actualMessage.contains(requestParameters.get("rspMessage")), "The operation message contains the expected content.");
		}
		else
		{
			if (requestParameters.get("description").contains("good request")) 
				Assert.assertEquals(actualMessage, "Successfully", "The message of 'operation success' is returned.");
			else
				System.out.println("Operation message is not specified for test case： " + requestParameters.get("test-id"));
		}
	}
	
	public static void parseFilterParameters(String filterStr, HashMap<String, String> filterParameters)
	{	  
		Scanner scanner = new Scanner(filterStr);
		scanner.useDelimiter("]");
		
		int paramCount = 0;
		while (scanner.hasNext())
		{
			paramCount++;
			
			String parameterStr = scanner.next();
			parameterStr = parameterStr.replaceFirst("\\[", "");
			if (parameterStr.contains("[")) parameterStr += "]" + scanner.next();
			
			if (!parameterStr.isEmpty()) 
			{
				switch(paramCount)
				{
				case 1:
					filterParameters.put("name", parameterStr);
					break;
					
				case 2:
					filterParameters.put("field", parameterStr);
					break;
					
				case 3:
					filterParameters.put("condition", parameterStr);
					break;
					
				case 4:
					filterParameters.put("order", parameterStr);
					break;
					
				case 5:
					filterParameters.put("pagination", parameterStr);
	
				default:
					break;
				}
			}
		}	
		  
		scanner.close();
	}
	
	public static void parseQueryString(String queryString, HashMap<String, String> queryParameters)
	{
		Scanner scanner = new Scanner(queryString);
		
		String pattern = "\s\\{";
		if (queryString.contains("(cond:")) pattern = "\\(cond:";

		scanner.useDelimiter(pattern);
		String entityStr = scanner.next();
		entityStr = entityStr.replaceFirst("\\{", "");
		queryParameters.put("entity", entityStr.trim());
		
		if (queryString.contains("(cond:"))
		{
			scanner.reset();
			pattern = "\\)";
			scanner.useDelimiter(pattern);
			
			String conditionStr = scanner.next();
			conditionStr = conditionStr.replace("(cond:", "");
			
			if ((conditionStr).contains(",order:"))
			{
				String orderStr = conditionStr.substring(conditionStr.indexOf(",order:"));
				orderStr = orderStr.replace(",order:", "");
				orderStr = orderStr.replace("\"", "");
				
				if (!orderStr.isEmpty()) queryParameters.put("order", orderStr.trim());
				
				conditionStr = conditionStr.substring(0, conditionStr.indexOf(",order:")-1);
			}
			
			queryParameters.put("condition", conditionStr.trim());
		}
		
		String fieldStr = scanner.next();
		fieldStr = fieldStr.substring(fieldStr.indexOf('{')+1);
		fieldStr = fieldStr.substring(0, fieldStr.lastIndexOf('}')-1);
//		fieldStr = fieldStr.replace("}", "");
		queryParameters.put("field", fieldStr.trim());
		
		scanner.close();
	}
	
	public static void checkOrder(String orderStr, List<HashMap<String, String>> dataList)
	{
		Scanner scanner = new Scanner(orderStr);
		scanner.useDelimiter(" ");
		
		String keyForOrder = scanner.next();
		
		if (orderStr.contains("asc")) keyForOrder = "+" + keyForOrder;
		if (orderStr.contains("dsc")) keyForOrder = "-" + keyForOrder;
		
		Assert.assertTrue(CommonCheckFunctions.checkDataIsSorted(keyForOrder, dataList), "The data list is sorted by the given order.");
		
		scanner.close();
	}
	
	@Step("Verify if the data pagination format is correct")
	public static void checkPaginationFormat(String format, int actualPageIndex, int actualPageSize)
	{
		Scanner scanner = new Scanner(format);
		scanner.useDelimiter(",");
		
		String pageIndex = scanner.next();
		String pageSize = scanner.next();
		
		Assert.assertEquals(Integer.parseInt(pageIndex), actualPageIndex);
		Assert.assertEquals(Integer.parseInt(pageSize), actualPageSize);
		
		scanner.close();
	}
	
	public static void checkCondition(String conditionFieldStr, List<HashMap<String, String>> dataList)
	{
		Boolean result = false;
		
		if (conditionFieldStr.contains("{_and:"))	// condition-1 AND condition-2
		{
			String conditionStr = conditionFieldStr.substring(conditionFieldStr.indexOf('[')+1, conditionFieldStr.indexOf(']'));
			
			result = verifySingleCondition(parseJointCondition(conditionStr, true), dataList) && 
					 verifySingleCondition(parseJointCondition(conditionStr, false), dataList);
		}
		else if (conditionFieldStr.contains("{_or:")) // condition-1 OR condition-2
		{
			String conditionStr = conditionFieldStr.substring(conditionFieldStr.indexOf('[')+1, conditionFieldStr.indexOf(']'));
			
			result = verifyJointCondition(parseJointCondition(conditionStr, true), parseJointCondition(conditionStr, false), dataList);
		}
		else	// Single condition
		{
			result = verifySingleCondition(conditionFieldStr, dataList);
		}
		
		Assert.assertTrue(result, "The data list satisfies the given condition.");
	}
	
	// Extract a single condition string from joint condition like "{updateTime: {_gt: "2020-09-27 04:00:00"}},{updateTime: {_lt: "2020-10-01 03:00:00"}}"
	public static String parseJointCondition(String jointConditionStr, boolean getFirstCondition)
	{
		String returnCondition = "";
		
		if (getFirstCondition) 
			returnCondition = jointConditionStr.substring(0, jointConditionStr.indexOf(','));
		else
			returnCondition = jointConditionStr.substring(jointConditionStr.indexOf(',')+1);
		
		return returnCondition;
	}
	
	// Parse condition string like {updateTime: {_gt: "2020-09-27 04:00:00"}} and forward the results to check condition functions
	public static boolean verifySingleCondition(String condition, List<HashMap<String, String>> dataList)
	{	
		String compareField = condition.substring(condition.indexOf('{')+1, condition.indexOf(':'));
		
		String equationStr = condition.substring(condition.indexOf('_'), condition.length()-2);
		equationStr = equationStr.replace("}", "");
		
		String compareType = equationStr.substring(equationStr.indexOf('_')+1, equationStr.indexOf(':'));
		
		String compareValue = equationStr.substring(equationStr.indexOf(' ')+1);
		
		if (compareValue.contains("\\\"")) compareValue = compareValue.replace("\\\"", "");
		
		Boolean result = CommonCheckFunctions.ifDataSatisfiesCondition(compareField, compareType, compareValue, dataList);
		
		return result;
	}
	
	public static boolean verifyJointCondition(String condition1, String condition2, List<HashMap<String, String>> dataList)
	{	
		String compareField1 = condition1.substring(condition1.indexOf('{')+1, condition1.indexOf(':'));
		
		String equationStr1 = condition1.substring(condition1.indexOf('_'), condition1.length()-2);
		
		String compareType1 = equationStr1.substring(equationStr1.indexOf('_')+1, equationStr1.indexOf(':'));
		
		String compareValue1 = equationStr1.substring(equationStr1.indexOf(' ')+1);
		
		String compareField2 = condition2.substring(condition2.indexOf('{')+1, condition2.indexOf(':'));
		
		String equationStr2 = condition2.substring(condition2.indexOf('_'), condition2.length()-2);
		
		String compareType2 = equationStr2.substring(equationStr2.indexOf('_')+1, equationStr2.indexOf(':'));
		
		String compareValue2 = equationStr2.substring(equationStr2.indexOf(' ')+1);
		
		Boolean result = CommonCheckFunctions.ifDataSatisfiesJointCondition(compareField1, compareType1, compareValue1, 
																			compareField2, compareType2, compareValue2, dataList);
		
		return result;
	}
}
