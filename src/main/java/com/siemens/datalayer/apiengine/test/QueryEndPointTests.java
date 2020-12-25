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
		  
		if (paramMaps.containsKey("depth")) 	queryParameters.put("depth", paramMaps.get("depth"));  
		if (paramMaps.containsKey("filter")) 	queryParameters.put("filter", paramMaps.get("filter"));
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
						CommonCheckFunctions.checkDataContainsSpecifiedFields(entityListPath, filterParameters.get("field"), entityList);
					  
					if (filterParameters.containsKey("condition")) 
						checkCondition(entityListPath, filterParameters.get("condition"), entityList);
					  
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
						checkComplexCondition(queryParameters.get("condition"), queryParameters.get("entity"), response);
					else
						Assert.assertTrue(verifySingleCondition(entityListPath, queryParameters.get("condition"), entityList));
				}
				
				if (queryParameters.containsKey("field"))
				{				
					if (queryParameters.get("field").contains("{"))
					{
						String fieldStr = queryParameters.get("field");
						checkSubEntityFields(fieldStr, queryParameters.get("entity"), response);
					}
					else // all items are field names
					{
						String allFields = queryParameters.get("field");
						allFields = allFields.replaceAll("\\s+", ",");
						CommonCheckFunctions.checkDataContainsSpecifiedFields(entityListPath, allFields, entityList);
					}
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
	
	// Function to process complex condition string like "{status:{_eq:\"online\"},Lease_Group:{lease_type:{_eq:\"2\"}}}"
	public static void checkComplexCondition(String conditionStr, String rootEntity, Response response)
	{
		conditionStr = conditionStr.substring(conditionStr.indexOf("{")+1, conditionStr.lastIndexOf("}"));
		conditionStr = CommonCheckFunctions.removeBlankBeforeToken(conditionStr);
		
		if ((conditionStr.contains("_and:[")) || (conditionStr.contains("_or:[")))
		{
			Boolean result = false;
			String jasonPath = "data." + rootEntity;
			String jointConditionStr = conditionStr.substring(conditionStr.indexOf('[')+1, conditionStr.lastIndexOf(']'));
			
			if (conditionStr.contains("_and:[")) 
				result = verifySingleCondition(jasonPath, parseJointCondition(jointConditionStr, true), response.jsonPath().getList(jasonPath))
					  && verifySingleCondition(jasonPath, parseJointCondition(jointConditionStr, false), response.jsonPath().getList(jasonPath));
			else
				result = verifyJointCondition(parseJointCondition(jointConditionStr, true), 
				  	  						  parseJointCondition(jointConditionStr, false), response.jsonPath().getList(jasonPath));
			
			Assert.assertTrue(result, "The data list satisfies the given condition.");
		}
		else // handle sub-entity conditions
		{
			Scanner scanner = new Scanner(conditionStr);
			scanner.useDelimiter("},");
			
			while (scanner.hasNext())
			{
				String conditionItem = scanner.next();
				
				// Make sure the last '}' is not deleted when using scanner 
				long count1 = conditionItem.chars().filter(ch -> ch == '{').count();
				long count2 = conditionItem.chars().filter(ch -> ch == '}').count();
				if (count1>count2) conditionItem += "}";
				
				// Count the number of ':'
				long count3 = conditionItem.chars().filter(ch -> ch == ':').count();
				
				if (count3==2) // condition like {status:{_eq:\"online\"}
				{
					conditionItem = "{" + conditionItem + "}";
					String jasonPath = "data." + rootEntity;
					verifySingleCondition(jasonPath, conditionItem, response.jsonPath().getList(jasonPath));
				}
				else if (count3==3)
				{
					// If the first char is '{' ignore it
					if (conditionItem.indexOf("{")==0) conditionItem = conditionItem.substring(1);
					String subEntity = conditionItem.substring(0, conditionItem.indexOf(":{"));
					String subCondition = conditionItem.substring(conditionItem.indexOf(":{")+1);
					
					List<HashMap<String, String>> subEntityList = new ArrayList<HashMap<String, String>>();
					getSubEntityList(rootEntity, subEntity, subEntityList, response);
					
					if (subEntityList.size()>0) verifySingleCondition("data."+rootEntity+"."+subEntity, subCondition, subEntityList);
				}
				else
				{
					System.out.println("Error: Unknown condition pattern.");
				}
			}
			
			scanner.close();
		}
	}
	
	public static void getSubEntityList(String rootEntity, String subEntity, List<HashMap<String, String>> subEntityList, Response response)
	{
		String entityListPath = "data." + rootEntity;
		
		try
		{
			HashMap<String, String> entity = response.jsonPath().get(entityListPath+"[0]");
			
			for (String key : entity.keySet())
			{
				if (key.contains(subEntity)) // found the subEntity
				{
					List<HashMap<String, String>> entityList = response.jsonPath().getList(entityListPath);
					
					for (int i=0;i<entityList.size(); i++)
					{
						String subEntityItemPath = entityListPath + "[" + i + "]." + key;
						
						try
						{
							subEntityList.add(response.jsonPath().get(subEntityItemPath));
						}
						catch (Exception e) 
					    {
							subEntityItemPath += "[0]";
							subEntityList.add(response.jsonPath().get(subEntityItemPath));
					    }
					}
					
					break;
				}
			}
		}
	    catch(Exception e) 
	    {
	    	System.out.println("Error: null is returned when try to get data from jasonPath '" + entityListPath + "'");
	        return;
	    }
	}
	
	public static void checkSubEntityFields(String fieldStr, String rootEntity, Response response)
	{
		fieldStr = fieldStr.replaceAll("\\s+", " ");
		fieldStr = fieldStr.replaceAll(" \\{", "{");
		fieldStr = fieldStr.replaceAll(" }", "}");
		
		if (fieldStr.contains(" (cond:")) fieldStr = fieldStr.replaceAll(" \\(cond:", "\\(cond:"); 
		if (fieldStr.contains("\"){")) fieldStr = fieldStr.replaceAll("\"\\)\\{", "\"\\)\\{ ");
		
		String rootFields = "";
		String [] items = fieldStr.trim().split("\\p{Space}");
		
		String entityListPath = "data." + rootEntity;
		List<HashMap<String, String>> entityList = response.jsonPath().getList(entityListPath);
		
		if (entityList==null)
		{
			System.out.println("Error: null is returned when try to get data from jasonPath '" + entityListPath + "'");
			return;
		}
		
		for (int i=0; i<items.length; i++)
		{
			if (items[i].contains("{")) 
			{
				String subFieldStr = items[i];
				
				if (subFieldStr.contains("(cond")) 
					subFieldStr = subFieldStr.substring(0, subFieldStr.indexOf('(')) + "{";
				
				long subLevelCount = 1;
				
				while(subLevelCount>=1)
				{
					i++;					
					if (items[i].contains("{")) 
						subLevelCount += items[i].chars().filter(ch -> ch == '{').count();
					
					if (subLevelCount==1) // ignore sub-entities with depth higher than 1
					{
						subFieldStr += " ";
						subFieldStr += items[i];
					}
					
					if (items[i].contains("}")) 
					{
						long minus = items[i].chars().filter(ch -> ch == '}').count();
						if ((subLevelCount>1) && ((subLevelCount-minus)<=0)) subFieldStr += "}";
						subLevelCount -= minus;
					}
				}
				
				HashMap<String, String> subQueryParameters = new HashMap<>();
				parseSubQueryString(subFieldStr, subQueryParameters);
				
				List<HashMap<String, String>> subEntityList = new ArrayList<HashMap<String, String>>();
				
				for (int k=0;k<entityList.size(); k++)
				{
					String subEntityItemPath = entityListPath + "[" + k + "]." + subQueryParameters.get("entity");
					
					try
					{
						subEntityList.add(response.jsonPath().get(subEntityItemPath));
					}
					catch (Exception e1) 
				    {
						try
						{
							subEntityItemPath += "[0]";
							subEntityList.add(response.jsonPath().get(subEntityItemPath));
						}
						catch (Exception e2) 
						{
							System.out.println("Error: null is returned when try to get data from jasonPath '" + subEntityItemPath + "'");
							break;
						}
				    }
				}
				
				if (subEntityList.size()>0)
				{
					String allSubEntityFields = subQueryParameters.get("field");
					allSubEntityFields = allSubEntityFields.replaceAll("\\s+", ",");
					CommonCheckFunctions.checkDataContainsSpecifiedFields(
							entityListPath+"."+subQueryParameters.get("entity"), allSubEntityFields, subEntityList);
				}
			}
			else
			{
				if (!rootFields.isEmpty()) rootFields += ",";
				rootFields += items[i];
			}
		}
		
		CommonCheckFunctions.checkDataContainsSpecifiedFields(entityListPath, rootFields, entityList);
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
		String headStr = queryString.substring(0, queryString.indexOf(" {"));
		String fieldStr = queryString.substring(queryString.indexOf(" {"));
		String entityStr = headStr;
		
		if (headStr.contains("(")) 
		{
			entityStr = queryString.substring(0, queryString.indexOf('('));
			String conditionStr = queryString.substring(queryString.indexOf('('), queryString.indexOf(')')+1);
			fieldStr = queryString.substring(queryString.indexOf(')')+1);
			
			conditionStr = conditionStr.replace("(cond:", "");
			conditionStr = conditionStr.substring(0, conditionStr.lastIndexOf(')'));

			if ((conditionStr).contains(",order:"))
			{
				String orderStr = conditionStr.substring(conditionStr.indexOf(",order:"));
				orderStr = orderStr.replace(",order:", "");
				orderStr = orderStr.replace("\"", "");
				
				if (!orderStr.isEmpty()) queryParameters.put("order", orderStr.trim());
				
				conditionStr = conditionStr.substring(0, conditionStr.indexOf(",order:"));
			}
			
			conditionStr= conditionStr.substring(conditionStr.indexOf("\"")+1);
			conditionStr= conditionStr.substring(0, conditionStr.lastIndexOf("\""));
			queryParameters.put("condition", conditionStr.trim());
		}
		
		entityStr = entityStr.substring(entityStr.indexOf('{')+1);
		queryParameters.put("entity", entityStr.trim());
		
		fieldStr = fieldStr.substring(0, fieldStr.lastIndexOf('}'));
		
		fieldStr = fieldStr.substring(fieldStr.indexOf('{')+1);
		fieldStr = fieldStr.substring(0, fieldStr.lastIndexOf('}'));
		
		queryParameters.put("field", fieldStr.trim());
	}
	
	public static void parseSubQueryString(String subQueryString, HashMap<String, String> subQueryParameters)
	{
		String headStr = subQueryString.substring(0, subQueryString.indexOf("{"));
		String fieldStr = subQueryString.substring(subQueryString.indexOf("{"));
		String entityStr = headStr;
		
		if (headStr.contains("(")) 
		{
			entityStr = subQueryString.substring(0, subQueryString.indexOf('('));
			String conditionStr = subQueryString.substring(subQueryString.indexOf('('), subQueryString.indexOf(')')+1);
			fieldStr = subQueryString.substring(subQueryString.indexOf(')')+1);
			
			conditionStr = conditionStr.replace("(cond:", "");
			conditionStr = conditionStr.substring(0, conditionStr.lastIndexOf(')'));

			if ((conditionStr).contains(",order:"))
			{
				String orderStr = conditionStr.substring(conditionStr.indexOf(",order:"));
				orderStr = orderStr.replace(",order:", "");
				orderStr = orderStr.replace("\"", "");
				
				if (!orderStr.isEmpty()) subQueryParameters.put("order", orderStr.trim());
				
				conditionStr = conditionStr.substring(0, conditionStr.indexOf(",order:"));
			}
			
			conditionStr= conditionStr.substring(conditionStr.indexOf("\"")+1);
			conditionStr= conditionStr.substring(0, conditionStr.lastIndexOf("\""));
			if (!conditionStr.isEmpty()) subQueryParameters.put("condition", conditionStr.trim());
		}
		
		subQueryParameters.put("entity", entityStr.trim());
		
		fieldStr = fieldStr.substring(fieldStr.indexOf('{')+1);
		fieldStr = fieldStr.substring(0, fieldStr.lastIndexOf('}'));
		
		subQueryParameters.put("field", fieldStr.trim());
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
	
	public static void checkCondition(String jasonPath, String conditionFieldStr, List<HashMap<String, String>> dataList)
	{
		Boolean result = false;
		
		if (conditionFieldStr.contains("{_and:"))	// condition-1 AND condition-2
		{
			String conditionStr = conditionFieldStr.substring(conditionFieldStr.indexOf('[')+1, conditionFieldStr.indexOf(']'));
			
			result = verifySingleCondition(jasonPath, parseJointCondition(conditionStr, true), dataList) && 
					 verifySingleCondition(jasonPath, parseJointCondition(conditionStr, false), dataList);
		}
		else if (conditionFieldStr.contains("{_or:")) // condition-1 OR condition-2
		{
			String conditionStr = conditionFieldStr.substring(conditionFieldStr.indexOf('[')+1, conditionFieldStr.indexOf(']'));
			
			result = verifyJointCondition(parseJointCondition(conditionStr, true), parseJointCondition(conditionStr, false), dataList);
		}
		else	// Single condition
		{
			result = verifySingleCondition(jasonPath, conditionFieldStr, dataList);
		}
		
		Assert.assertTrue(result, "The data list satisfies the given condition.");
	}
	
	// Extract a single condition string from joint condition like "{updateTime: {_gt: "2020-09-27 04:00:00"}},{updateTime: {_lt: "2020-10-01 03:00:00"}}"
	public static String parseJointCondition(String jointConditionStr, boolean getFirstCondition)
	{
		String returnCondition = "";
		
		if (getFirstCondition) 
			returnCondition = jointConditionStr.substring(0, jointConditionStr.indexOf(",{"));
		else
			returnCondition = jointConditionStr.substring(jointConditionStr.indexOf(",{")+1);
		
		return returnCondition;
	}
	
	// Parse condition string like {updateTime: {_gt: "2020-09-27 04:00:00"}} and forward the results to check condition functions
	public static boolean verifySingleCondition(String jasonPath, String condition, List<HashMap<String, String>> dataList)
	{	
		condition = condition.replaceAll("\\s+", " ");
		String compareField = condition.substring(condition.indexOf('{')+1, condition.indexOf(':'));
		
		String equationStr = condition.substring(condition.indexOf(':')+1);	
		
		if (equationStr.contains(",order:"))
			equationStr = equationStr.substring(equationStr.indexOf('_'), equationStr.indexOf(",order:"));
		
		// remove the last char (condition should end with "\"")
		equationStr = equationStr.substring(equationStr.indexOf('_'), equationStr.length()-2);
		equationStr = equationStr.replace("}", "");
		
		String compareType = equationStr.substring(equationStr.indexOf('_')+1, equationStr.indexOf(':'));
		
		String compareValue = equationStr.substring(equationStr.indexOf(':')+1);		
		compareValue = CommonCheckFunctions.removeBlankBeforeToken(compareValue);
		
		Boolean result = CommonCheckFunctions.ifDataSatisfiesCondition(jasonPath, compareField, compareType, compareValue.trim(), dataList);
		
		return result;
	}
	
	public static boolean verifyJointCondition(String condition1, String condition2, List<HashMap<String, String>> dataList)
	{	
		String compareField1 = condition1.substring(condition1.indexOf('{')+1, condition1.indexOf(':'));
		
		String equationStr1 = condition1.substring(condition1.lastIndexOf('_'), condition1.length()-2);
		
		String compareType1 = equationStr1.substring(equationStr1.indexOf('_')+1, equationStr1.indexOf(':'));
		
		String compareValue1 = equationStr1.substring(equationStr1.lastIndexOf(':')+1).trim();
		
		String compareField2 = condition2.substring(condition2.indexOf('{')+1, condition2.indexOf(':'));
		
		String equationStr2 = condition2.substring(condition2.indexOf('_'), condition2.length()-2);
		
		String compareType2 = equationStr2.substring(equationStr2.indexOf('_')+1, equationStr2.indexOf(':'));
		
		String compareValue2 = equationStr2.substring(equationStr2.lastIndexOf(':')+1).trim();
		
		Boolean result = CommonCheckFunctions.ifDataSatisfiesJointCondition(compareField1, compareType1, compareValue1, 
																			compareField2, compareType2, compareValue2, dataList);
		
		return result;
	}
}
