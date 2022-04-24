package com.siemens.datalayer.iot.test;

import com.google.gson.Gson;
import com.siemens.datalayer.apiengine.test.ApiEngineEndpoint;
import com.siemens.datalayer.apiengine.test.QueryEndPointTests;
import com.siemens.datalayer.utils.ExcelDataProviderClass;

import io.qameta.allure.*;
import io.restassured.response.Response;

import org.apache.commons.collections4.MapUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Epic("SDL Api-engine")
@Feature("restful as datasource")
public class AuthForRestfulWriteTests {
    @Parameters({"base_url","port"})
    @BeforeClass(description = "Configure the host address and communication port of data-layer-api-engine;")
    public void setApiEngineEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("32552") String port)
    {
        ApiEngineEndpoint.setBaseUrl(base_url);
        ApiEngineEndpoint.setPort(port);
    }

    @Test(priority = 0,
            description = "\"restful with auth\" as the data source, write (data source) by single entity",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'getData' request to graphql query interface.")
    @Story("auth for restful write")
    public void authForRestfulWrite(Map<String, String> paramMaps){

        if (paramMaps.containsKey("graphQLSentence") && paramMaps.get("graphQLSentence").contains("$authInfo")){

            if (paramMaps.containsKey("authList")){
                try {
                    // 对paramMaps.get("authList")进行base64编码，生成authInfo
                    String base64encodedAuthInfo = Base64.getEncoder().encodeToString(paramMaps.get("authList").getBytes("utf-8"));

                    String query = paramMaps.get("graphQLSentence").replaceAll("\\$authInfo",base64encodedAuthInfo);
                    Response response = ApiEngineEndpoint.postGraphql(query);

                    // 校验返回的response的最外层的statusCode，code，message
                    QueryEndPointTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));

                    String jsonPath = "data." + paramMaps.get("entity") + "_" + paramMaps.get("operation") + "." + "json_value";
                    List<String> responseOfRestful = response.jsonPath().getList(jsonPath);

                    // 校验操作restful数据源的”每条“返回结果，路径类似于data.UserWithAuth_Insert.json_value
                    for (int i = 0; i < responseOfRestful.size(); i++) {
                        String str = new Gson().toJson(responseOfRestful.get(i));
                        JSONObject jo = new JSONObject(str);
                        System.out.println("restful返回code:" + String.valueOf(jo.get("code")) + " , restful返回data:" + String.valueOf(jo.get("data")));
                        checkResponseCodeOfRestful(paramMaps, String.valueOf(jo.get("code")));
                        checkResponseDataOfRestful(paramMaps, String.valueOf(jo.get("data")));
                    }

                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void checkResponseCodeOfRestful(Map<String, String> requestParameters, String actualCodeOfRestful)
    {
        Assert.assertEquals(requestParameters.get("rspCodeOfRestful"),actualCodeOfRestful);
    }

    public void checkResponseDataOfRestful(Map<String, String> requestParameters, String actualDataOfRestful)
    {
        // 创建Pattern对象，按指定模式在字符串查找
        Pattern pattern = Pattern.compile("(.*)=(.*)");

        List<String> actualDataList = Arrays.asList(actualDataOfRestful.split("\\|\\|"));

        Map<String,Integer> actualDataMap = new HashMap<>();
        for (String actualData : actualDataList)
        {
            Integer lengthOfActualData = actualData.length();
            // 创建matcher对象
            Matcher matcher = pattern.matcher(actualData.substring(1,lengthOfActualData-1));
            if (matcher.find()){

                try {
                    // 字符串转json
                    JSONObject jo = new JSONObject(matcher.group(2));
                    if (jo.has("code"))
                        actualDataMap.put(matcher.group(1),jo.getInt("code"));
                    else
                        actualDataMap.put(matcher.group(1),jo.getInt("statusCode"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                System.out.println("NO MATCH");
            }
        }

        // 字符串转map
        Gson gson = new Gson();
        Map<String,Integer> expectDataMapFromExcel = new HashMap<>();
        expectDataMapFromExcel = gson.fromJson(requestParameters.get("rspDataOfRestful"),expectDataMapFromExcel.getClass());

        Assert.assertEquals(expectDataMapFromExcel.size(),actualDataMap.size());

        for (Map.Entry<String,Integer> actualDataMapItem : actualDataMap.entrySet()){

            if (actualDataMapItem.getValue() instanceof Integer)
            {
                Assert.assertEquals(MapUtils.getInteger(expectDataMapFromExcel,actualDataMapItem.getKey()),actualDataMapItem.getValue());
            }
        }
    }
}
