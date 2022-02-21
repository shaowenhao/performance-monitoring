package com.siemens.datalayer.databrain.test;

import com.alibaba.fastjson.JSONObject;
import com.siemens.datalayer.apiengine.test.ApiEngineEndpoint;
import com.siemens.datalayer.apiengine.test.QueryEndPointTests;
import com.siemens.datalayer.connector.test.ConnectorEndpoint;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Epic("SDL Api-engine")
@Feature("read/write DesigoCC/Enlighted history/realtime data from api-engine")
public class DataBrainFromApiEngineTests {
    @Parameters({"base_url","port","baseUrlOfConnector","portOfConnector"})
    @BeforeClass(description = "Configure the host address,communication port and database properties file of data-layer-api-engine;")
    public void setApiEngineEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("32084") String port,
                                     @Optional("http://140.231.89.85")  String baseUrlOfConnector,@Optional("30694") String portOfConnector)
    {
        ApiEngineEndpoint.setBaseUrl(base_url);
        ApiEngineEndpoint.setPort(port);

        ConnectorEndpoint.setBaseUrl(baseUrlOfConnector);
        ConnectorEndpoint.setPort(portOfConnector);
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