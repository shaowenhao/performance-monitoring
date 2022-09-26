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

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Epic("SDL Api-engine")
@Feature("Restful as data source")
public class RestfulAsDataSourcesEnhanceTests {
    @Parameters({"base_url", "port"})
    @BeforeClass(description = "Configure the host address and communication port of data-layer-api-engine;")
    public void setApiEngineEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("32552") String port) {
        ApiEngineEndpoint.setBaseUrl(base_url);
        ApiEngineEndpoint.setPort(port);
    }

    @Test(priority = 0,
            description = "Transfer of flexible parameters such as \"in\",\"and\",\"or\" of restful driver",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'query or mutation' request to graphql interface.")
    @Story("Restful as data source,read/write data source,supports operations such as 'in','and','or'")
    public void transferOfFlexibleParams(Map<String, String> paramMaps) throws JSONException {
        if (paramMaps.containsKey("graphQLSentence")) {
            String query = paramMaps.get("graphQLSentence");
            Response response = ApiEngineEndpoint.postGraphql(query);
            System.out.println("response.jsonPath().prettify(): " + "\n" +response.jsonPath().prettify());

            // 校验返回的response的最外层的statusCode，code，message
            QueryEndPointTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));

            checkResponseData(paramMaps,response);
        }
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

            Assert.assertTrue(objectEquals(actualResponseData,expectedResponseData));
            // Assert.assertEquals(actualResponseData,expectedResponseData);
        }
    }

    /**
     * 该方法校验两个对象是否一致，对rspData返回多条数据，但是顺序不固定，使用Assert.assertEquals报错的问题，进行了重写
     */
    static boolean objectEquals(Object real, Object target) {
        if (real == null && target == null) {
            return true;
        }

        if (real instanceof Map && target instanceof Map) {
            Map<String, Object> realMap = (Map<String, Object>) real;
            Map<String, Object> targetMap = (Map<String, Object>) target;
            if (realMap.keySet().equals(targetMap.keySet())) {
                for(String key : realMap.keySet()) {
                    if (!objectEquals(realMap.get(key), targetMap.get(key))) {
                        return false;
                    }
                }
                return true;
            }
        }

        if (real instanceof Collection && target instanceof Collection) {
            Object[] realList = ((Collection<?>) real).toArray();
            Object[] targetList = ((Collection<?>) target).toArray();
            if (realList.length == targetList.length) {
                for (int i = 0; i < targetList.length; i++) {
                    if (!objectEquals(realList[i], targetList[i])) {
                        return false;
                    }
                }
                return true;
            }
        }

        if (real instanceof Number && target instanceof Number) {
            return Objects.equals(((Number)real).doubleValue(), ((Number)target).doubleValue());
        }

        if (real instanceof String && target instanceof String) {
            Pattern hitPattern = Pattern.compile(".*UserBasicInfo.*");
            if (hitPattern.matcher((String)real).find() && hitPattern.matcher((String)target).find()) {
                String[] realArray = ((String)real).split("\\|\\|");
                String[] targetArray = ((String)target).split("\\|\\|");
                return realArray.length == targetArray.length && Arrays.stream(realArray).collect(Collectors.toSet()).
                        equals(Arrays.stream(targetArray).collect(Collectors.toSet()));
            }
            else
                return real.equals(target);
        }

        return false;
    }
}