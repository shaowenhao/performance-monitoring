package com.siemens.datalayer.iot.test;

import com.siemens.datalayer.connector.test.ConnectorEndpoint;
import com.siemens.datalayer.utils.CommonCheckFunctions;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.*;


@Epic("SDL Connector")
@Feature("clickhouse database as data source")
public class ClickhouseAsDataSourceTests {

    @Parameters({"base_url", "port"})
    @BeforeClass(description = "Configure the host address and communication port of data-layer-connector")
    public void setConnectorEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("30850") String port) {
        ConnectorEndpoint.setBaseUrl(base_url);
        ConnectorEndpoint.setPort(port);
    }


    @Test(priority = 0,
            description = "clickhouse as data source,query data source",
            dataProvider = "connector-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'searchData' request to connector interface.")
    @Story("clickhouse as data source,query data source")
    public void queryEntityMapToClickhouseTable(Map<String, String> paramMaps) {

        HashMap<String, String> queryParameters = new HashMap<>();

        if (paramMaps.containsKey("condition")) 		queryParameters.put("condition", paramMaps.get("condition"));
        if (paramMaps.containsKey("domainName")) 		queryParameters.put("domainName", paramMaps.get("domainName"));
        if (paramMaps.containsKey("name")) 			queryParameters.put("name", paramMaps.get("name"));
        if (paramMaps.containsKey("fields")) 			queryParameters.put("fields", paramMaps.get("fields"));
        if (paramMaps.containsKey("order")) 			queryParameters.put("order", paramMaps.get("order"));
        if (paramMaps.containsKey("pageIndex")) 		queryParameters.put("pageIndex", paramMaps.get("pageIndex"));
        if (paramMaps.containsKey("pageSize")) 		queryParameters.put("pageSize", paramMaps.get("pageSize"));
        if (paramMaps.containsKey("timeout")) 		queryParameters.put("timeout", paramMaps.get("timeout"));

        Response response = ConnectorEndpoint.getConceptModelDataByCondition(queryParameters);

        checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));

        List<HashMap<String, String>> rspDataList;
        String listPath = "data";
        if (paramMaps.get("description").contains("data retrieved"))
        {

            if (response.getBody().asString().contains("totalPages")) listPath += ".data"; // 只有分页时，路径才会变成data.data，其他时候为data

            rspDataList = response.jsonPath().getList(listPath); // 获取列表

            // Check if the returned data list is not empty
            Assert.assertNotNull(rspDataList,"return data is null");
            Assert.assertTrue(rspDataList.size() > 0,"return data is empty");

            if (paramMaps.containsKey("fields"))
            {
                    CommonCheckFunctions.checkDataContainsSpecifiedFields(listPath, paramMaps.get("fields"), rspDataList);
            }

            if (paramMaps.containsKey("order"))
            {
                Assert.assertTrue(CommonCheckFunctions.checkDataIsSorted(paramMaps.get("order"), rspDataList));
            }

            String pageIndex = "noInput", pageSize = "noInput";
            if (paramMaps.containsKey("pageIndex")) pageIndex = String.valueOf(paramMaps.get("pageIndex"));
            if (paramMaps.containsKey("pageSize")) pageSize = String.valueOf(paramMaps.get("pageSize"));

            // Check if the pagination format is correct
            checkPaginationFormat(pageIndex, pageSize, response);

            if(!paramMaps.get("description").contains("no condition check"))
            {
                // Check if the data satisfies the given condition
                checkSingleCondition(paramMaps, rspDataList);
            }
        }
        else
        {
            Assert.assertTrue(
                    response.jsonPath().getList("data") == null || response.jsonPath().getList("data").size() == 0);
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
                Assert.assertEquals(actualCode, "0", "The operation code in response message matches the expected value.");
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
                Assert.assertEquals(actualMessage, "Operate success.", "The message of 'operation success' is returned.");
            else
                System.out.println("Operation message is not specified for test case： " + requestParameters.get("test-id"));
        }
    }

    public void checkDataFollowsModelSchema(String schemaName, Response response)
    {
        String schemaTemplateFile = ConnectorEndpoint.getResourcePath() + "JasonModelSchemaFor" + schemaName;

        // If data is returned in pagination format
        if (response.getBody().asString().contains("totalPages")) schemaTemplateFile += "P";
        schemaTemplateFile += ".JSON";

        System.out.println(schemaTemplateFile);
        CommonCheckFunctions.verifyIfDataMatchesJsonSchemaTemplate(schemaTemplateFile, response.getBody().asString());

    }

    public static void checkPaginationFormat(String pageIndex, String pageSize, Response response)
    {
        if (((pageIndex.equals("noInput")) && (pageSize.equals("noInput")))	||
                ((pageIndex.equals("noInput")) && (pageSize.equals("0"))) 	 	||
                ((pageIndex.equals("0")) && (pageSize.equals("noInput")))	 	||
                ((pageIndex.equals("0")) && (pageSize.equals("0"))))
        {
            // Data is not in pagination format
            Assert.assertTrue((response.getBody().asString().contains("totalPages"))==false, "Data pagination is not used");
        }
        else
        {
            Assert.assertTrue(response.getBody().asString().contains("totalPages"), "Data is in pagination format");

            int pageIndexInput=0, pageSizeInput=0;

            if ((!pageIndex.equals("noInput")) && (CommonCheckFunctions.isIntegerStr(pageIndex)))
            {
                pageIndexInput = Integer.parseInt(pageIndex);
                if (pageIndexInput<=0) pageIndexInput = 1;
                if (pageSize.equals("noInput")) pageSizeInput = 20;
            }

            if ((!pageSize.equals("noInput")) && (CommonCheckFunctions.isIntegerStr(pageSize)))
            {
                pageSizeInput = Integer.parseInt(pageSize);
                if (pageSizeInput<=0) pageSizeInput = 20;
                if (pageIndex.equals("noInput")) pageIndexInput = 1;
            }

            checkPageIndexAndPageSize(pageIndexInput, pageSizeInput, response);
        }
    }

    @Step("Verify if the data pagination format is correct")
    public static void checkPageIndexAndPageSize(int expectPageIndex, int expectPageSize, Response response)
    {
        int actualPageIndex = response.jsonPath().get("data.pageIndex");
        int actualPageSize = response.jsonPath().get("data.pageSize");

        Assert.assertTrue(actualPageIndex==expectPageIndex, "The page index is correct.");
        Assert.assertTrue(actualPageSize==expectPageSize, "The page size is correct.");

        boolean isFirstPage = response.jsonPath().get("data.first");
        boolean isLastPage = response.jsonPath().get("data.last");

        if (expectPageIndex==1)
            Assert.assertTrue(isFirstPage, "The mark of first page is set.");
        else
            Assert.assertTrue(!isFirstPage, "The mark of first page is not set.");

        if (expectPageIndex==response.jsonPath().getShort("data.totalPages"))
            Assert.assertTrue(isLastPage, "The mark of last page is set.");
        else
            Assert.assertTrue(!isLastPage, "The mark of last page is not set.");
    }

    private void checkSingleCondition(Map<String, String> paramMaps, List<HashMap<String, String>> rspDataList) {
        if (paramMaps.get("description").contains("good request")) {
            // condition limitation: not support AND,OR
            if (paramMaps.containsKey("condition")) {
                String conditionStr = paramMaps.get("condition");
                String conditionStrLower = conditionStr.toLowerCase();
                if (!conditionStrLower.contains(" and ") && !conditionStrLower.contains(" or ")) {
                    ClickhouseAsDataSourceTests.Condition condition = new ClickhouseAsDataSourceTests.Condition(conditionStr);
                    if (StringUtils.isNumeric(condition.getField())) {
                        // no need to check this condition, such as condition is 1=2
                        System.out.println("No need to check this condition (" + conditionStr + ")");
                        return;
                    }
                    boolean result = CommonCheckFunctions.ifDataSatisfiesCondition("", condition.getField(),
                            condition.getCompareType(), condition.getValue(), rspDataList);
                    Assert.assertTrue(result,
                            "The data list do not satisfies the given condition (" + conditionStr + ")");
                } else {
                    System.out.println("Not support to check this condition (" + conditionStr
                            + ") since this condition contains \"AND\" or \"OR\"");
                }
            }
        }
    }

    private static class Condition {
        private String conditionStr;
        private String field;
        private String value;
        private String compareType;

        public Condition(String conditionStr) {
            this.conditionStr = conditionStr;
            parse();
        }

        private void parse() {
            String[] parts = {};
            if (this.conditionStr.contains(">=")) {
                this.compareType = "gte";
                parts = this.conditionStr.split(">=");
            } else if (this.conditionStr.contains(">")) {
                this.compareType = "gt";
                parts = this.conditionStr.split(">");
            } else if (this.conditionStr.contains("<=")) {
                this.compareType = "lte";
                parts = this.conditionStr.split("<=");
            } else if (this.conditionStr.contains("<")) {
                this.compareType = "lt";
                parts = this.conditionStr.split("<");
            } else if (this.conditionStr.contains("!=")) {
                this.compareType = "neq";
                parts = this.conditionStr.split("!=");
            } else if (this.conditionStr.contains("<>")) {
                this.compareType = "neq";
                parts = this.conditionStr.split("<>");
            } else if (this.conditionStr.contains("=")) {
                this.compareType = "eq";
                parts = this.conditionStr.split("=");
            }
            if (parts.length >= 2) {
                this.field = parts[0].trim();
                this.value = StringUtils.removeEnd(StringUtils.removeStart(parts[1].trim(), "'"), "'");
            } else {
                throw new IllegalArgumentException("Wrong condition: " + conditionStr);
            }
        }

        public String getField() {
            return field;
        }

        public String getValue() {
            return value;
        }

        public String getCompareType() {
            return compareType;
        }

    }
}
