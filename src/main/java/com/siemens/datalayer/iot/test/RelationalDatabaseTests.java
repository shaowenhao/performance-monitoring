/***
 * 由于现在mysql的host、port不能暴露出来，regression case跑在gitlab上面，无法连接对应的mysql，所以将连接MySQL，进入MySQL内部比对数据的相关代码都删除。
 */
package com.siemens.datalayer.iot.test;
import com.google.gson.Gson;
import com.siemens.datalayer.apiengine.test.ApiEngineEndpoint;
import com.siemens.datalayer.apiengine.test.QueryEndPointTests;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Epic("SDL Api-engine")
@Feature("relational databases as data source")
public class RelationalDatabaseTests {

    @Parameters({"base_url","port","db_properties"})
    @BeforeClass(description = "Configure the host address,communication port and database properties file of data-layer-api-engine;")
    public void setApiEngineEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("32552") String port,String db_properties)
    {
        String database = "Mysql_Test";
        List<String> IDList = Arrays.asList("1","2","3","4","5","6");

        ApiEngineEndpoint.setBaseUrl(base_url);
        ApiEngineEndpoint.setPort(port);

        deleteDataFromMysqlBeforeTest(database,IDList);
    }

    @Test(priority = 0,
          description = "mysql as data source,Insert/Update/Delete data source",
          dataProvider = "api-engine-test-data-provider",
          dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'getData' request to graphql query interface.")
    @Story("mysql as data source,write data source")
    public void postGraphForMysql(Map<String, String> paramMaps) throws JSONException {

        // 这两行代码，在testcase最先执行，
        // 作用为：比如update、delete数据库前，需要数据库中有数据
        if (paramMaps.containsKey("pre-execution"))
            ApiEngineEndpoint.postGraphql(paramMaps.get("pre-execution"));

        if (paramMaps.containsKey("graphQLSentence")) {
            String query = paramMaps.get("graphQLSentence");
            Response response = ApiEngineEndpoint.postGraphql(query);

            // 校验返回的response的最外层的statusCode，code，message
            QueryEndPointTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));

            // 现在的testcase，每个都有参数database
            if (paramMaps.containsKey("database")) {
                // 如外层返回code为101301、101303等，就不会返回内层code
                if (paramMaps.containsKey("rspCodeOfMysql") && paramMaps.containsKey("rspDataOfMysql")) {
                    String jsonPath = null;
                    if (!paramMaps.get("graphQLSentence").contains("_By_Condition"))
                        jsonPath = "data." + paramMaps.get("database") + "_" + paramMaps.get("operation") + "." + "json_value";
                    else
                        jsonPath = "data." + paramMaps.get("database") + "_" + paramMaps.get("operation") + "_By_Condition" + "." + "json_value";
                    List<String> responseOfMysql = response.jsonPath().getList(jsonPath);

                    // 校验操作数据库的”每条“返回结果，路径类似于data.Mysql_Test_Insert.json_value
                    for (int i = 0; i < responseOfMysql.size(); i++) {
                        String str = new Gson().toJson(responseOfMysql.get(i));
                        JSONObject jo = new JSONObject(str);
                        System.out.println("mysql返回code:" + String.valueOf(jo.get("code")) + " , mysql返回data:" + String.valueOf(jo.get("data")));
                        checkResponseCodeOfMysql(paramMaps, String.valueOf(jo.get("code")), String.valueOf(jo.get("data")));
                    }
                }
            }
        }

        // 如果有在mysql里写入数据，则调用deleteDataFromMysql方法清除数据，还原测试环境。
        if (paramMaps.containsKey("expectedRemainingIdInMysql")) {
            deleteDataFromMysql(paramMaps);
        }
    }

    @Step("校验操作数据库的返回结果")
    public static void checkResponseCodeOfMysql(Map<String,String> requestParameters,String actualCodeOfMysql,String actualDataOfMysql)
    {
        if(requestParameters.containsKey("rspCodeOfMysql"))
        {
            Assert.assertEquals(actualCodeOfMysql,requestParameters.get("rspCodeOfMysql"),"The operation codeOfMysql in response message matches the expected value.");
        }
        else
        {
            if (requestParameters.get("description").contains("good request"))
                Assert.assertEquals(actualCodeOfMysql,"0","The operation codeOfMysql in response message matches the expected value.");
            else
                System.out.println("Operation codeOfMysql is not specified for test case： " + requestParameters.get("test-id"));
        }

        if(requestParameters.containsKey("rspDataOfMysql"))
        {
            //Assert.assertEquals(actualDataOfMysql,requestParameters.get("rspDataOfMysql"),"The operation dataOfMysql in response message matches the expected value.");
            Assert.assertTrue(actualDataOfMysql.contains(requestParameters.get("rspDataOfMysql")),"The operation dataOfMysql in response message contains the expected content.");
        }
        else
        {
            if(requestParameters.get("description").contains("good request"))
                //Assert.assertEquals(actualDataOfMysql,"true","The operation dataOfMysql in response message matches the expected value.");
                Assert.assertTrue(actualDataOfMysql.contains("true"),"The operation dataOfMysql in response message contains the expected content.");
            else
                System.out.println("Operation dataOfMysql is not specified for test case： "+requestParameters.get("test-id"));
        }
    }

    @Step("从MySQL清除数据，还原测试环境")
    public static void deleteDataFromMysql(Map<String,String> requestParameters)
    {
        // generate graphQL sentence for delete
        String graphQLSentenceForDelete = null;

        String IDSentence = "";
        List<String> IDList = Arrays.asList(requestParameters.get("expectedRemainingIdInMysql").split(","));

        for (String ID : IDList)
        {
            IDSentence += "\t\t" + "{" + "\n" + "\t\t\t" + "ID:" + ID + "\n" + "\t\t" + "}" + "\n";
        }

        graphQLSentenceForDelete = "mutation mutationName{"
                + "\n\t" + requestParameters.get("database") + "_" + "Delete" + "(input:"
                + "\n\t" + "["
                + "\n" + IDSentence
                + "\t" + "]"
                + "\n\t\t" + ")"
                + "\n\t\t" + "{"
                + "\n\t\t\t" + "json_value"
                + "\n\t\t\t" + "reserved_field_1"
                + "\n\t\t\t" + "reserved_field_2"
                + "\n\t\t" + "}"
                + "\n" + "}";

        ApiEngineEndpoint.postGraphql(graphQLSentenceForDelete);
    }

    public static void deleteDataFromMysqlBeforeTest(String database,List<String> IDList)
    {
        // generate graphQL sentence for delete
        String graphQLSentenceForDelete = null;

        String IDSentence = "";

        for (String ID : IDList)
        {
            IDSentence += "\t\t" + "{" + "\n" + "\t\t\t" + "ID:" + ID + "\n" + "\t\t" + "}" + "\n";
        }

        graphQLSentenceForDelete = "mutation mutationName{"
                + "\n\t" + database + "_" + "Delete" + "(input:"
                + "\n\t" + "["
                + "\n" + IDSentence
                + "\t" + "]"
                + "\n\t\t" + ")"
                + "\n\t\t" + "{"
                + "\n\t\t\t" + "json_value"
                + "\n\t\t\t" + "reserved_field_1"
                + "\n\t\t\t" + "reserved_field_2"
                + "\n\t\t" + "}"
                + "\n" + "}";

        ApiEngineEndpoint.postGraphql(graphQLSentenceForDelete);
    }
}