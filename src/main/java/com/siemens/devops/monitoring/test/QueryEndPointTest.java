package com.siemens.devops.monitoring.test;

import com.siemens.devops.monitoring.EnvironmentConstants;
import com.siemens.devops.monitoring.test.endpoint.ApiEngineEndpoint;
import com.siemens.devops.monitoring.test.endpoint.ConnectorConfigureEndpoint;
import com.siemens.devops.monitoring.utils.AllureEnvironmentPropertiesWriter;
import com.siemens.devops.monitoring.utils.CommonCheckFunctions;
import com.siemens.devops.monitoring.utils.ExcelDataProvider;
import com.siemens.devops.monitoring.utils.CasePropertyUtils;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.*;

@Epic("SDL Api-engine")
@Feature("Query End Point")

public class QueryEndPointTest {

	@Parameters({ "apiEngineBaseUrl", "apiEnginePort", "connectorConfigureBaseUrl", "connectorConfigurePort" })
	@BeforeClass(description = "Configure the host address and communication port of data-layer-api-engine")
	public void setApiEngineEndpoint(
			@Optional(EnvironmentConstants.IOT_TEST_APIENGINE_BASE_URL) String apiEngineBaseUrl,
			@Optional(EnvironmentConstants.IOT_TEST_APIENGINE_PORT) String apiEnginePort,
			@Optional(EnvironmentConstants.IOT_TEST_CONNECTOR_CONFIGURE_BASE_URL) String connectorConfigureBaseUrl,
			@Optional(EnvironmentConstants.IOT_TEST_CONNECTOR_CONFIGURE_PORT) String connectorConfigurePort) {
		ApiEngineEndpoint.setBaseUrlAndPort(apiEngineBaseUrl, apiEnginePort);

		ConnectorConfigureEndpoint.setBaseUrlAndPort(connectorConfigureBaseUrl, connectorConfigurePort);

		AllureEnvironmentPropertiesWriter.addEnvironmentItem("data-layer-api-engine",
				apiEngineBaseUrl + ":" + apiEnginePort);
		AllureEnvironmentPropertiesWriter.addEnvironmentItem("data-layer-connetor-configure",
				connectorConfigureBaseUrl + ":" + connectorConfigurePort);
	}

	@Test(priority = 0, description = "Test Api-engine Query Endpoint: GraphQL interface", dataProvider = "api-engine-test-data-provider", dataProviderClass = ExcelDataProvider.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Post a 'getData' request to graphql query interface.")
	@Story("Query End Point: GraphQL Interface")
	public void getDataByGraphQL(Map<String, String> paramMaps) {
		// Try to add clean cache step on the beginning
		ConnectorConfigureEndpoint.clearModuleCaches("data-layer-connector-configure");
		ConnectorConfigureEndpoint.clearModuleCaches("data-layer-connector");
		ConnectorConfigureEndpoint.clearModuleCaches("data-layer-api-engine");

		Response response = ApiEngineEndpoint.postGraphql(paramMaps.get("query"));

		checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"),
				response.jsonPath().getString("message"));

		if (CasePropertyUtils.getTypeInDescription(paramMaps).contains("good request")) {
			HashMap<String, String> queryParameters = new HashMap<>();
			parseQueryString(paramMaps.get("query"), queryParameters);

			Assert.assertTrue(response.getBody().asString().contains(queryParameters.get("entity")));

			String entityListPath = "data." + queryParameters.get("entity");

			if (CasePropertyUtils.getTypeInDescription(paramMaps).contains("data retrieved")) {
				List<HashMap<String, String>> entityList = response.jsonPath().getList(entityListPath);

				if (queryParameters.containsKey("condition")) {
					if (queryParameters.get("condition").contains("},"))
						checkComplexCondition(queryParameters.get("condition"), queryParameters.get("entity"),
								response);
					else
						Assert.assertTrue(
								verifySingleCondition(entityListPath, queryParameters.get("condition"), entityList));
				}

				if (queryParameters.containsKey("field")) {
					if (queryParameters.get("field").contains("{")) {
						String fieldStr = queryParameters.get("field");
						checkSubEntityFields(fieldStr, queryParameters.get("entity"), response);
					} else // all items are field names
					{
						String allFields = queryParameters.get("field");
						allFields = allFields.replaceAll("\\s+", ",");
						CommonCheckFunctions.checkDataContainsSpecifiedFields(entityListPath, allFields, entityList);
					}
				}
			} else {
				Assert.assertTrue(response.jsonPath().getList(entityListPath).isEmpty(),
						"The response message does not contain any data.");
			}
		}

	}

	@Step("Verify the status code, operation code, and message")
	public void checkResponseCode(Map<String, String> requestParameters, int actualStatusCode, String actualCode,
			String actualMessage) {
		int expStatusCode = 200; // If not specified, the expected status code is set to 200 (OK)
		if (requestParameters.containsKey("rspStatus"))
			expStatusCode = Integer.valueOf(requestParameters.get("rspStatus")).intValue();
		Assert.assertEquals(actualStatusCode, expStatusCode,
				"The status code in response message matches the expected value.");

		if ((requestParameters.containsKey("rspCode"))) {
			Assert.assertEquals(actualCode, requestParameters.get("rspCode"),
					"The operation code in response message matches the expected value.");
		} else {
			if (requestParameters.get("description").contains("good request"))
				Assert.assertEquals(actualCode, "100000",
						"The operation code in response message matches the expected value.");
			else
				System.out
						.println("Operation code is not specified for test case： " + requestParameters.get("test-id"));
		}

		if (requestParameters.containsKey("rspMessage")) {
			Assert.assertTrue(actualMessage.contains(requestParameters.get("rspMessage")),
					"The operation message contains the expected content.");
		} else {
			if (requestParameters.get("description").contains("good request"))
				Assert.assertEquals(actualMessage, "Successfully", "The message of 'operation success' is returned.");
			else
				System.out.println(
						"Operation message is not specified for test case： " + requestParameters.get("test-id"));
		}
	}

	// Function to process complex condition string like
	// "{status:{_eq:\"online\"},Lease_Group:{lease_type:{_eq:\"2\"}}}"
	private void checkComplexCondition(String conditionStr, String rootEntity, Response response) {
		conditionStr = conditionStr.substring(conditionStr.indexOf("{") + 1, conditionStr.lastIndexOf("}"));
		conditionStr = CommonCheckFunctions.removeBlankBeforeToken(conditionStr);

		if ((conditionStr.contains("_and:[")) || (conditionStr.contains("_or:["))) {
			Boolean result = false;
			String jasonPath = "data." + rootEntity;
			String jointConditionStr = conditionStr.substring(conditionStr.indexOf('[') + 1,
					conditionStr.lastIndexOf(']'));

			if (conditionStr.contains("_and:["))
				result = verifySingleCondition(jasonPath, parseJointCondition(jointConditionStr, true),
						response.jsonPath().getList(jasonPath))
						&& verifySingleCondition(jasonPath, parseJointCondition(jointConditionStr, false),
								response.jsonPath().getList(jasonPath));
			else
				result = verifyJointCondition(parseJointCondition(jointConditionStr, true),
						parseJointCondition(jointConditionStr, false), response.jsonPath().getList(jasonPath));

			Assert.assertTrue(result, "The data list satisfies the given condition.");
		} else // handle sub-entity conditions
		{
			Scanner scanner = new Scanner(conditionStr);
			scanner.useDelimiter("},");

			while (scanner.hasNext()) {
				String conditionItem = scanner.next();

				// Make sure the last '}' is not deleted when using scanner
				long count1 = conditionItem.chars().filter(ch -> ch == '{').count();
				long count2 = conditionItem.chars().filter(ch -> ch == '}').count();
				if (count1 > count2)
					conditionItem += "}";

				// Count the number of ':'
				long count3 = conditionItem.chars().filter(ch -> ch == ':').count();

				if (count3 == 2) // condition like {status:{_eq:\"online\"}
				{
					conditionItem = "{" + conditionItem + "}";
					String jasonPath = "data." + rootEntity;
					verifySingleCondition(jasonPath, conditionItem, response.jsonPath().getList(jasonPath));
				} else if (count3 == 3) {
					// If the first char is '{' ignore it
					if (conditionItem.indexOf("{") == 0)
						conditionItem = conditionItem.substring(1);
					String subEntity = conditionItem.substring(0, conditionItem.indexOf(":{"));
					String subCondition = conditionItem.substring(conditionItem.indexOf(":{") + 1);

					List<HashMap<String, String>> subEntityList = new ArrayList<HashMap<String, String>>();
					getSubEntityList(rootEntity, subEntity, subEntityList, response);

					if (subEntityList.size() > 0)
						verifySingleCondition("data." + rootEntity + "." + subEntity, subCondition, subEntityList);
				} else {
					System.out.println("Error: Unknown condition pattern.");
				}
			}

			scanner.close();
		}
	}

	private void getSubEntityList(String rootEntity, String subEntity, List<HashMap<String, String>> subEntityList,
			Response response) {
		String entityListPath = "data." + rootEntity;

		try {
			HashMap<String, String> entity = response.jsonPath().get(entityListPath + "[0]");

			for (String key : entity.keySet()) {
				if (key.contains(subEntity)) // found the subEntity
				{
					List<HashMap<String, String>> entityList = response.jsonPath().getList(entityListPath);

					for (int i = 0; i < entityList.size(); i++) {
						String subEntityItemPath = entityListPath + "[" + i + "]." + key;

						try {
							subEntityList.add(response.jsonPath().get(subEntityItemPath));
						} catch (Exception e) {
							subEntityItemPath += "[0]";
							subEntityList.add(response.jsonPath().get(subEntityItemPath));
						}
					}

					break;
				}
			}
		} catch (Exception e) {
			System.out.println("Error: null is returned when try to get data from jasonPath '" + entityListPath + "'");
			return;
		}
	}

	// Verify if the given data list contains the required information fields. Here
	// field string can contains sub-entity fields, e.g.:
	// business_mgr business_unit charge_frequency city province district
	// invert_Customer(cond:"",order:"") { actual_controller category city
	// contact_detail Restricted_By_Contract(cond:"",order:"") {contract_amount
	// customer lease_end_time lease_start_time payment_method project}
	// Refer_To_Lease_Group(cond:"",order:"") {asset_type count discount_ratio id
	// unit_price}
	private void checkSubEntityFields(String fieldStr, String rootEntity, Response response) {
		fieldStr = fieldStr.replaceAll("\\s+", " ");
		fieldStr = fieldStr.replaceAll(" \\{", "{");
		fieldStr = fieldStr.replaceAll(" }", "}");

		if (fieldStr.contains(" (cond:"))
			fieldStr = fieldStr.replaceAll(" \\(cond:", "\\(cond:");
		if (fieldStr.contains("\"){"))
			fieldStr = fieldStr.replaceAll("\"\\)\\{", "\"\\)\\{ ");

		String rootFields = "";
		String[] items = fieldStr.trim().split("\\p{Space}");

		String entityListPath = "data." + rootEntity;
		List<HashMap<String, String>> entityList = response.jsonPath().getList(entityListPath);

		if (entityList == null) {
			System.out.println("Error: null is returned when try to get data from jasonPath '" + entityListPath + "'");
			return;
		}

		// Start to extract sub-entity fields when first "{" is detected and continue
		// until the corresponding "}" is found
		for (int i = 0; i < items.length; i++) {
			if (items[i].contains("{")) {
				String subFieldStr = items[i];

				if (subFieldStr.contains("(cond"))
					subFieldStr = subFieldStr.substring(0, subFieldStr.indexOf('(')) + "{";

				long subLevelCount = 1;

				while (subLevelCount >= 1) {
					i++;
					if (items[i].contains("{"))
						subLevelCount += items[i].chars().filter(ch -> ch == '{').count();

					if (subLevelCount == 1) // ignore sub-entities with depth higher than 1
					{
						subFieldStr += " ";
						subFieldStr += items[i];
					}

					if (items[i].contains("}")) {
						long minus = items[i].chars().filter(ch -> ch == '}').count();
						if ((subLevelCount > 1) && ((subLevelCount - minus) <= 0))
							subFieldStr += "}";
						subLevelCount -= minus;
					}
				}

				HashMap<String, String> subQueryParameters = new HashMap<>();
				parseSubQueryString(subFieldStr, subQueryParameters);

				// Try to extract sub-entity data items from the given data list
				List<HashMap<String, String>> subEntityList = new ArrayList<HashMap<String, String>>();

				for (int k = 0; k < entityList.size(); k++) {
					String subEntityItemPath = entityListPath + "[" + k + "]." + subQueryParameters.get("entity");

					try {
						subEntityList.add(response.jsonPath().get(subEntityItemPath));
					} catch (Exception e1) {
						try {
							subEntityItemPath += "[0]";
							subEntityList.add(response.jsonPath().get(subEntityItemPath));
						} catch (Exception e2) {
							System.out.println("Error: null is returned when try to get data from jasonPath '"
									+ subEntityItemPath + "'");
							break;
						}
					}
				}

				// Verify if the required information fields are found in all the sub-entity
				// data items
				if (subEntityList.size() > 0) {
					String allSubEntityFields = subQueryParameters.get("field");
					allSubEntityFields = allSubEntityFields.replaceAll("\\s+", ",");
					CommonCheckFunctions.checkDataContainsSpecifiedFields(
							entityListPath + "." + subQueryParameters.get("entity"), allSubEntityFields, subEntityList);
				}
			} else {
				if (!rootFields.isEmpty())
					rootFields += ",";
				rootFields += items[i];
			}
		}

		CommonCheckFunctions.checkDataContainsSpecifiedFields(entityListPath, rootFields, entityList);
	}

	// Read the information from a top level query string, which can contains
	// sub-entity string.
	// Sample:
	// {Project(cond:"{status:{_eq:\"online\"},Lease_Group:{lease_type:{_eq:\"2\"}}}",order:"")
	// {business_mgr charge_frequency invert_Customer(cond:"",order:"") {
	// actual_controller city contact_detail} Refer_To_Lease_Group(cond:"",order:"")
	// {asset_type count discount_ratio id}} }
	private void parseQueryString(String queryString, HashMap<String, String> queryParameters) {
		String headStr = queryString.substring(0, queryString.indexOf(" {"));
		String fieldStr = queryString.substring(queryString.indexOf(" {"));
		String entityStr = headStr;

		if (headStr.contains("(")) {
			entityStr = queryString.substring(0, queryString.indexOf('('));
			String conditionStr = queryString.substring(queryString.indexOf('('), queryString.indexOf(')') + 1);
			fieldStr = queryString.substring(queryString.indexOf(')') + 1);

			conditionStr = conditionStr.replace("(cond:", "");
			conditionStr = conditionStr.substring(0, conditionStr.lastIndexOf(')'));
			// System.out.println("conditionStr: " + conditionStr);

			if ((conditionStr).contains(",order:")) {
				String orderStr = conditionStr.substring(conditionStr.indexOf(",order:"));
				orderStr = orderStr.replace(",order:", "");
				orderStr = orderStr.replace("\"", "");

				if (!orderStr.isEmpty())
					queryParameters.put("order", orderStr.trim());
				// System.out.println("order: "+orderStr.trim());

				conditionStr = conditionStr.substring(0, conditionStr.indexOf(",order:"));
			}

			// process the case: conditionStr=""
			if (conditionStr.length() > 2) {
				conditionStr = conditionStr.substring(conditionStr.indexOf("\"") + 1);
				conditionStr = conditionStr.substring(0, conditionStr.lastIndexOf("\""));
				queryParameters.put("condition", conditionStr.trim());
				// System.out.println("condition: "+conditionStr.trim());
			}
		}

		entityStr = entityStr.substring(entityStr.indexOf('{') + 1);
		queryParameters.put("entity", entityStr.trim());

		fieldStr = fieldStr.substring(0, fieldStr.lastIndexOf('}'));

		fieldStr = fieldStr.substring(fieldStr.indexOf('{') + 1);
		fieldStr = fieldStr.substring(0, fieldStr.lastIndexOf('}'));

		queryParameters.put("field", fieldStr.trim());
		// System.out.println("entity: "+entityStr.trim());
		// System.out.println("field: "+fieldStr.trim());
	}

	// Read the information from a sub-entity string, e.g.
	// invert_Customer(cond:"",order:"") { actual_controller category ... district}
	private void parseSubQueryString(String subQueryString, HashMap<String, String> subQueryParameters) {
		String headStr = subQueryString.substring(0, subQueryString.indexOf("{"));
		String fieldStr = subQueryString.substring(subQueryString.indexOf("{"));
		String entityStr = headStr;

		if (headStr.contains("(")) {
			entityStr = subQueryString.substring(0, subQueryString.indexOf('('));
			String conditionStr = subQueryString.substring(subQueryString.indexOf('('),
					subQueryString.indexOf(')') + 1);
			fieldStr = subQueryString.substring(subQueryString.indexOf(')') + 1);

			conditionStr = conditionStr.replace("(cond:", "");
			conditionStr = conditionStr.substring(0, conditionStr.lastIndexOf(')'));

			if ((conditionStr).contains(",order:")) {
				String orderStr = conditionStr.substring(conditionStr.indexOf(",order:"));
				orderStr = orderStr.replace(",order:", "");
				orderStr = orderStr.replace("\"", "");

				if (!orderStr.isEmpty())
					subQueryParameters.put("order", orderStr.trim());

				conditionStr = conditionStr.substring(0, conditionStr.indexOf(",order:"));
			}

			conditionStr = conditionStr.substring(conditionStr.indexOf("\"") + 1);
			conditionStr = conditionStr.substring(0, conditionStr.lastIndexOf("\""));
			if (!conditionStr.isEmpty())
				subQueryParameters.put("condition", conditionStr.trim());
		}

		subQueryParameters.put("entity", entityStr.trim());

		fieldStr = fieldStr.substring(fieldStr.indexOf('{') + 1);
		fieldStr = fieldStr.substring(0, fieldStr.lastIndexOf('}'));

		subQueryParameters.put("field", fieldStr.trim());
	}

	// ---abandoned---
	@Step("Verify if the data pagination format is correct")
	private void checkPaginationFormat(String format, int actualPageIndex, int actualPageSize) {
		Scanner scanner = new Scanner(format);
		scanner.useDelimiter(",");

		String pageIndex = scanner.next();
		String pageSize = scanner.next();

		Assert.assertEquals(Integer.parseInt(pageIndex), actualPageIndex);
		Assert.assertEquals(Integer.parseInt(pageSize), actualPageSize);

		scanner.close();
	}

	// Extract a single condition string from joint condition like "{updateTime:
	// {_gt: "2020-09-27 04:00:00"}},{updateTime: {_lt: "2020-10-01 03:00:00"}}"
	private String parseJointCondition(String jointConditionStr, boolean getFirstCondition) {
		String returnCondition = "";

		if (getFirstCondition)
			returnCondition = jointConditionStr.substring(0, jointConditionStr.indexOf(",{"));
		else
			returnCondition = jointConditionStr.substring(jointConditionStr.indexOf(",{") + 1);

		return returnCondition;
	}

	// Parse condition string like {updateTime: {_gt: "2020-09-27 04:00:00"}} and
	// forward the results to check condition functions
	private boolean verifySingleCondition(String jasonPath, String condition, List<HashMap<String, String>> dataList) {
		condition = condition.replaceAll("\\s+", " ");
		String compareField = condition.substring(condition.indexOf('{') + 1, condition.indexOf(':'));

		String equationStr = condition.substring(condition.indexOf(':') + 1);

		if (equationStr.contains(",order:"))
			equationStr = equationStr.substring(equationStr.indexOf('_'), equationStr.indexOf(",order:"));

		// remove the last char (condition should end with "\"")
		equationStr = equationStr.substring(equationStr.indexOf('_'), equationStr.length() - 2);
		equationStr = equationStr.replace("}", "");

		String compareType = equationStr.substring(equationStr.indexOf('_') + 1, equationStr.indexOf(':'));

		String compareValue = equationStr.substring(equationStr.indexOf(':') + 1);
		compareValue = CommonCheckFunctions.removeBlankBeforeToken(compareValue);

		Boolean result = CommonCheckFunctions.ifDataSatisfiesCondition(jasonPath, compareField, compareType,
				compareValue.trim(), dataList);

		return result;
	}

	// Check if two conditions can be both satisfied by a data list.
	// Condition is represented by strings like "{business_mgr:{_in:[潘云晖,臧佳宝]}}",
	// "{status:{_in:[archived]}}", etc.
	private boolean verifyJointCondition(String condition1, String condition2, List<HashMap<String, String>> dataList) {
		String compareField1 = condition1.substring(condition1.indexOf('{') + 1, condition1.indexOf(':'));

		String equationStr1 = condition1.substring(condition1.lastIndexOf('_'), condition1.length() - 2);

		String compareType1 = equationStr1.substring(equationStr1.indexOf('_') + 1, equationStr1.indexOf(':'));

		String compareValue1 = equationStr1.substring(equationStr1.lastIndexOf(':') + 1).trim();

		String compareField2 = condition2.substring(condition2.indexOf('{') + 1, condition2.indexOf(':'));

		String equationStr2 = condition2.substring(condition2.indexOf('_'), condition2.length() - 2);

		String compareType2 = equationStr2.substring(equationStr2.indexOf('_') + 1, equationStr2.indexOf(':'));

		String compareValue2 = equationStr2.substring(equationStr2.lastIndexOf(':') + 1).trim();

		Boolean result = CommonCheckFunctions.ifDataSatisfiesJointCondition(compareField1, compareType1, compareValue1,
				compareField2, compareType2, compareValue2, dataList);

		return result;
	}
}
