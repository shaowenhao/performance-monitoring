/***
 * 由于现在mysql的host、port不能暴露出来，regression case跑在gitlab上面，无法连接对应的mysql，所以将连接MySQL，进入MySQL内部比对数据的相关代码都删除。
 */
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
import org.testng.annotations.*;
import org.testng.annotations.Optional;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Epic("SDL Api-engine")
@Feature("JDBC databases as data source")
public class OtherJDBCDatabaseTests {

    @Parameters({"base_url","port","db_properties"})
    @BeforeClass(description = "Configure the host address,communication port and database properties file of data-layer-api-engine;")
    public void setApiEngineEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("32552") String port,String db_properties)
    {
        ApiEngineEndpoint.setBaseUrl(base_url);
        ApiEngineEndpoint.setPort(port);
    }

    @Test(priority = 0,
          description = "query/insert/update/delete MySQL(data source)",
          dataProvider = "api-engine-test-data-provider",
          dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'query or mutation' request to graphql interface.")
    @Story("MySQL as data source,read/write data source")
    public void readWriteMySQL(Map<String, String> paramMaps) throws JSONException {
        // 在每个testcase执行前，通过graph语句清空数据
        // 这样做的原因在于：清空数据后，不会受之前case写入的脏数据的影响。
        clearDataThroughGraph(paramMaps);

        // 在testcase最先执行，
        // 作用为：比如update、delete数据库前，需要数据库中有数据
        preExecution(paramMaps);

        if (paramMaps.containsKey("graphQLSentence")) {
            String query = paramMaps.get("graphQLSentence");
            Response response = ApiEngineEndpoint.postGraphql(query);

            // 校验返回的response的最外层的statusCode，code，message
            QueryEndPointTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));

            checkResponseData(paramMaps,response);
        }
    }

    @Test(priority = 0,
             description = "query/insert/update/delete SQL Server(data source)",
             dataProvider = "api-engine-test-data-provider",
             dataProviderClass = ExcelDataProviderClass.class)
     @Severity(SeverityLevel.BLOCKER)
     @Description("Post a 'query or mutation' request to graphql interface.")
     @Story("SQL Server as data source,read/write data source")
     public void readWriteSqlServer(Map<String, String> paramMaps) throws JSONException {
        // 在每个testcase执行前，通过graph语句清空数据
        // 这样做的原因在于：清空数据后，不会受之前case写入的脏数据的影响。
        clearDataThroughGraph(paramMaps);

        // 在testcase最先执行，
        // 作用为：比如update、delete数据库前，需要数据库中有数据
        preExecution(paramMaps);

        if (paramMaps.containsKey("graphQLSentence")) {
            String query = paramMaps.get("graphQLSentence");
            Response response = ApiEngineEndpoint.postGraphql(query);

            // 校验返回的response的最外层的statusCode，code，message
            QueryEndPointTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));

            checkResponseData(paramMaps,response);
        }
    }

     @Step("Clear data through graph")
     public static void clearDataThroughGraph(Map<String,String> requestParameters){
         // generate graphQL sentence for delete
         String graphQLSentenceForDelete ="mutation mutationName{\n" +
                 requestParameters.get("database") +
                 "_Delete_By_Condition(cond: \"{id: {_gte: 1}}\")\n" +
                 "    {\n" +
                 "        json_value\n" +
                 "        reserved_field_1\n" +
                 "        reserved_field_2\n" +
                 "    }\n" +
                 "}";

         ApiEngineEndpoint.postGraphql(graphQLSentenceForDelete);
     }

     @Step("Prepare test data for query/update/delete")
     public static void preExecution(Map<String,String> requestParameters) {
         if (requestParameters.containsKey("pre-execution"))
             ApiEngineEndpoint.postGraphql(requestParameters.get("pre-execution"));
     }

     @Step("Verify the data returned by the interface")
     public static void checkResponseData(Map<String,String> requestParameters, Response response) throws JSONException {
         if (requestParameters.containsKey("rspData"))
         {
             // 转换成Map
             System.out.println("response.jsonPath().prettify(): " + "\n" +response.jsonPath().prettify());
             Map<String,Object> actualResponseData = response.jsonPath().getMap("data");

             // 转换成Map，重写TypeAdapter方法为MapTypeAdapter，解决String转换成Map<String,Object>时，会将整数型数据自动添加小数点的问题
             Gson gson = new GsonBuilder()
                     .registerTypeAdapter(new TypeToken<Map<String, Object>>() {
                     }.getType(), new MapTypeAdapter()).create();

             System.out.println("requestParameters.get(\"rspData\"): " + "\n" + requestParameters.get("rspData"));
             Map<String, Object> expectedResponseData = gson.fromJson(requestParameters.get("rspData"), new TypeToken<Map<String, Object>>() {}.getType());

             if (requestParameters.get("description").contains("query operation")){
                 Assert.assertTrue(objectEquals(actualResponseData,expectedResponseData));
             }
             else {
                 Assert.assertEquals(actualResponseData,expectedResponseData);
             }
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