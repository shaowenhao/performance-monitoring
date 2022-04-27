package com.siemens.datalayer.databrain.test;

import com.alibaba.fastjson.JSONObject;
import com.siemens.datalayer.apiengine.test.ApiEngineEndpoint;
import com.siemens.datalayer.apiengine.test.QueryEndPointTests;
import com.siemens.datalayer.connector.test.ConnectorConfigureEndpoint;
import com.siemens.datalayer.connector.test.ConnectorEndpoint;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Epic("SDL Api-engine")
@Feature("verify data brain function")
public class DataBrainFromApiEngineTests {
    static Connection connection;
    static Statement statement;

    @Parameters({"base_url","port","baseUrlOfConnector","portOfConnector","baseUrlOfConnectorConfigure","portOfConnectorConfigure"})
    @BeforeClass(description = "Configure the host address,communication port and database properties file of data-layer-api-engine;")
    public void setApiEngineEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("32510") String port,
                                     @Optional("http://140.231.89.85")  String baseUrlOfConnector,@Optional("32577") String portOfConnector,
                                     @Optional("http://140.231.89.85")  String baseUrlOfConnectorConfigure,@Optional("30674") String portOfConnectorConfigure)
    {
        ApiEngineEndpoint.setBaseUrl(base_url);
        ApiEngineEndpoint.setPort(port);

        ConnectorEndpoint.setBaseUrl(baseUrlOfConnector);
        ConnectorEndpoint.setPort(portOfConnector);

        ConnectorConfigureEndpoint.setBaseUrl(baseUrlOfConnectorConfigure);
        ConnectorConfigureEndpoint.setPort(portOfConnectorConfigure);
    }

    @Test(	priority = 0,
            description = "generate grapgQL to query/update DesigoCC/Enlighted history data",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'getData' request to graphql query interface.")
    @Story("generate grapgQL to query/update DesigoCC/Enlighted history data")
    public void queryUpdateFromApiEngine(Map<String, String> paramMaps)
    {
        if (paramMaps.containsKey("graphQLSentence")) {
            String query = paramMaps.get("graphQLSentence");
            Response response = ApiEngineEndpoint.postGraphql(query);

            // 校验返回的response的最外层的statusCode，code，message
            QueryEndPointTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"),
                    response.jsonPath().getString("message"));

            checkResponseData(paramMaps,response);
        }
    }

    @Test(	priority = 0,
            description = "verify smart space function",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class
            )
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'getData' request to graphql query interface.")
    @Story("verify smart space function")
    public void verifySmartSpaceFunction(Map<String, String> paramMaps)
    {
        // 执行case之前，需调用connector-configure（clear all cache接口）清除缓存
        ConnectorConfigureEndpoint.clearAllCache();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (paramMaps.containsKey("query")) {
            String query = paramMaps.get("query");
            System.out.println(query);
            Response response = ApiEngineEndpoint.postGraphql(query);
            System.out.println(response.jsonPath().prettify());

            // 校验返回的response的最外层的statusCode，code，message
            QueryEndPointTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"),
                    response.jsonPath().getString("message"));

            // 如果是query语句，检验返回的数据不少于一条
            if (paramMaps.containsKey("entity") && !paramMaps.containsKey("rspCodeOfDatasource")
                    && !paramMaps.containsKey("rspMessageOfDatasource"))
            {
                String path = "data." + paramMaps.get("entity");
                List<Map<String,Object>> responseDataList = response.jsonPath().getList(path);
                Assert.assertTrue(responseDataList.size() >= 1);
            }
            // 如果是mutation，校验返回的第二层的code/data
            else if (paramMaps.containsKey("entity") && paramMaps.containsKey("rspCodeOfDatasource") && paramMaps.containsKey("rspMessageOfDatasource"))
            {
                String pathOfRspCodeOfDatasource = null;
                String pathOfRspMessageOfDatasource = null;
                if (paramMaps.get("query").contains("Update"))
                {
                    pathOfRspCodeOfDatasource = "data." + paramMaps.get("entity") + "_Update.json_value[0].code";
                    pathOfRspMessageOfDatasource = "data." + paramMaps.get("entity") + "_Update.json_value[0].data";
                }
                String actualRspCodeOfDatasource = response.jsonPath().getString(pathOfRspCodeOfDatasource);
                String actualRspMessageOfDatasource = response.jsonPath().getString(pathOfRspMessageOfDatasource);

                Assert.assertEquals(actualRspCodeOfDatasource,paramMaps.get("rspCodeOfDatasource"));
                Assert.assertTrue(actualRspMessageOfDatasource.contains(paramMaps.get("rspMessageOfDatasource")));
            }
        }
    }

    @Step("校验/graphql接口返回的数据跟直接调用connector接口返回的数据是否一致")
    public static void checkResponseData(Map<String, String> requestParameters,Response actualResponse)
    {
        if (requestParameters.containsKey("convertToConnectorPath") && requestParameters.containsKey("convertToConnectorParameter")
                && requestParameters.containsKey("entity"))
        {
            if (requestParameters.get("convertToConnectorPath").equals("/api/connectors/searchData"))
            {
                HashMap<String, String> parameters = JSONObject.parseObject(requestParameters.get("convertToConnectorParameter"),HashMap.class);
                Response expectedResponse = ConnectorEndpoint.getConceptModelDataByCondition(parameters);

                List<Map<String,String>> actualDataList = actualResponse.jsonPath().getList("data." + requestParameters.get("entity"));
                List<Map<String,String>> expectedDataList = expectedResponse.jsonPath().getList("data");

                System.out.println(actualDataList);
                System.out.println(expectedDataList);
                Assert.assertEquals(actualDataList.size(),expectedDataList.size());
                Assert.assertEquals(actualDataList,expectedDataList);
            }
        }
        else if (requestParameters.containsKey("database") && requestParameters.containsKey("operation")
                && requestParameters.containsKey("rspCodeOfDatasource"))
        {
            String jsonPath = "data." + requestParameters.get("database") + "_" + requestParameters.get("operation") + "." + "json_value";
            List<Map<String,String>> actualDataList = actualResponse.jsonPath().getList(jsonPath);

            Iterator<Map<String,String>> iterator = actualDataList.iterator();
            if (iterator.hasNext())
            {
                Map<String,String> actualDataItem = iterator.next();

                System.out.println(actualDataItem);
                Assert.assertEquals(actualDataItem.get("code"),0);
            }
        }
    }
}
