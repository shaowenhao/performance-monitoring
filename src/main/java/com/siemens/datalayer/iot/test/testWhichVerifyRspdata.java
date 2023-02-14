package com.siemens.datalayer.iot.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.siemens.datalayer.apiengine.test.ApiEngineEndpoint;
import com.siemens.datalayer.apiengine.test.QueryEndPointTests;
import com.siemens.datalayer.iot.util.MapTypeAdapter;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.json.JSONException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.Map;

@Epic("SDL Api-engine")
@Feature("test which verify rspdata")
public class testWhichVerifyRspdata {
    @Parameters({"base_url", "port"})
    @BeforeClass(description = "Configure the host address and communication port of data-layer-api-engine;")
    public void setApiEngineEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("32552") String port) {
        ApiEngineEndpoint.setBaseUrl(base_url);
        ApiEngineEndpoint.setPort(port);
    }

    @Test(priority = 0,
            description = "test which verify rspdata",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'getData' request to graphql query interface.")
    @Story("test which verify rspdata")
    public void testWhichVerifyRspdata(Map<String, String> paramMaps) throws JSONException {
        if (paramMaps.containsKey("graphQLSentence")) {
            String query = paramMaps.get("graphQLSentence");
            Response response = ApiEngineEndpoint.postGraphql(query);

            // 校验返回的response的最外层的statusCode，code，message
            QueryEndPointTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));

            checkResponseData(paramMaps,response);
        }
    }

    @Test(priority = 0,
            description = "test which include multi data source",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'getData' request to graphql query interface.")
    @Story("test which include multi data source")
    public void testMultiDataSource(Map<String, String> paramMaps) throws JSONException {
        Response response = null;

        if (paramMaps.containsKey("pre-execution")) {
            String query = paramMaps.get("pre-execution");
            Response responseOfPreExecution = ApiEngineEndpoint.postGraphql(query);
        }

        if (paramMaps.containsKey("graphQLSentence")) {
            String query = paramMaps.get("graphQLSentence");
            response = ApiEngineEndpoint.postGraphql(query);
            System.out.println("response.jsonPath().prettify(): " + "\n" +response.jsonPath().prettify());
        }

        if (paramMaps.containsKey("after-execution")) {
            String query = paramMaps.get("after-execution");
            Response responseOfAfterExecution = ApiEngineEndpoint.postGraphql(query);
        }

        // 校验返回的response的最外层的statusCode，code，message
        QueryEndPointTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));

        checkResponseData(paramMaps,response);
    }

    @Step("校验接口返回的rspData")
    public static void checkResponseData(Map<String,String> requestParameters, Response response) throws JSONException {
        if (requestParameters.containsKey("rspData"))
        {
            // 转换成Map
            // System.out.println("response.jsonPath().prettify(): " + "\n" +response.jsonPath().prettify());
            Map<String,Object> actualResponseData = response.jsonPath().getMap("data");

            // 转换成Map，重写TypeAdapter方法为MapTypeAdapter，解决String转换成Map<String,Object>时，会将整数型数据自动添加小数点的问题
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(new TypeToken<Map<String, Object>>() {
                    }.getType(), new MapTypeAdapter()).create();

            System.out.println("requestParameters.get(\"rspData\"): " + "\n" + requestParameters.get("rspData"));
            Map<String, Object> expectedResponseData = gson.fromJson(requestParameters.get("rspData"), new TypeToken<Map<String, Object>>() {}.getType());

            Assert.assertEquals(actualResponseData.remove("score"),expectedResponseData.remove("score"));                 
            // Assert.assertEquals(actualResponseData,expectedResponseData);
        }
    }
}
