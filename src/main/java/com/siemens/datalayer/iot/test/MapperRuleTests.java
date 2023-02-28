package com.siemens.datalayer.iot.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.siemens.datalayer.apiengine.test.ApiEngineEndpoint;
import com.siemens.datalayer.apiengine.test.QueryEndPointTests;
import com.siemens.datalayer.iot.util.MapTypeAdapter;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * <p>Create Time: 2023年02月28日 10:18          </p>
 **/

@Epic("SDL Api-engine")
@Feature("Mapper rule")
public class MapperRuleTests {

    @Parameters({"base_url", "port"})
    @BeforeClass(description = "Configure the host address and communication port of data-layer-api-engine;")
    public void setApiEngineEndpoint(@Optional("http://140.231.89.106") String base_url, @Optional("30346") String port) {
        ApiEngineEndpoint.setBaseUrl(base_url);
        ApiEngineEndpoint.setPort(port);
    }


    @Test(priority = 0,
            description = "verify mapper rule",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("check mapper rule though graphql with differnet conditions")
    @Story("verify mapper rule")
    public void testMapperRule(Map<String, String> paramMaps){
        Response response = ApiEngineEndpoint.postGraphql(paramMaps.get("graphQLSentence"));
        // 校验返回的response的最外层的statusCode，code，message
        QueryEndPointTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));

        checkResponseData(paramMaps,response);
    }

    @Step("verify rspData")
    private void checkResponseData(Map<String, String> paramMaps, Response response) {
        // 转换成Map
        Map<String,Object> actualResponseData = response.jsonPath().getMap("data");

        // 转换成Map，重写TypeAdapter方法为MapTypeAdapter，解决String转换成Map<String,Object>时，会将整数型数据自动添加小数点的问题
//        Gson gson = new GsonBuilder()
//                .registerTypeAdapter(new TypeToken<Map<String, Object>>() {
//                }.getType(), new MapTypeAdapter()).create();
//
//        Map<String, Object> expectedResponseData = gson.fromJson(paramMaps.get("rspData"), new TypeToken<Map<String, Object>>() {}.getType());

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> expectedResponseData = null;
        try {
              expectedResponseData = mapper.readValue(paramMaps.get("rspData"), new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Assert.assertEquals(actualResponseData,expectedResponseData);
    }
}
