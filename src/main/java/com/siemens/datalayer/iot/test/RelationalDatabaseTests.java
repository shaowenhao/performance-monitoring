package com.siemens.datalayer.iot.test;
import com.siemens.datalayer.apiengine.test.ApiEngineEndpoint;
import com.siemens.datalayer.apiengine.test.QueryEndPointTests;
import com.siemens.datalayer.iot.util.JdbcMysqlUtil;
import com.siemens.datalayer.utils.ExcelDataProviderClass;

import com.google.gson.Gson;

import io.qameta.allure.*;
import io.restassured.response.Response;

import org.apache.commons.collections4.MapUtils;
import org.apache.velocity.servlet.VelocityServlet;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.annotations.Optional;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Epic("SDL Api-engine")
@Feature("Relational databases as data source,Insert/Update/Delete data source")
public class RelationalDatabaseTests {

    static Connection connection;
    static Statement statement;

    // static List<String> databaseToBeClearInBeforeClassList;

    static List<String> databaseTableList;

    @Parameters({"base_url","port","db_properties"})
    @BeforeClass(description = "Configure the host address,communication port and database properties file of data-layer-api-engine;")
    public void setApiEngineEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("32552") String port,String db_properties)
    {
        ApiEngineEndpoint.setBaseUrl(base_url);
        ApiEngineEndpoint.setPort(port);

        // 执行case之前，连接MySQL，并且清空表Mysql_Test、Mysql_Device
        // 保持数据库连接不断开，最后在AfterClass(deleteDataForMysql)中关闭数据库连接
        try
        {
            // 连接数据库
            connection = JdbcMysqlUtil.getConnection(db_properties);
            if(!connection.isClosed())
                System.out.println("Succeeded connecting to the Database!");

            // 操作数据库
            statement = connection.createStatement();

            // 需要在测试之前清空的数据库列表
            /* databaseToBeClearInBeforeClassList = Arrays.asList("Mysql_Test","Mysql_Device");
            for (String database : databaseToBeClearInBeforeClassList)
            {
                // 需要执行的sql语句
                String sql = "DELETE FROM " + database;
                statement.execute(sql);
            } */
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // 连接数据库的方式
        // 1、新建数据库配置文件(resources.iot.dev.mysql.db.properties)
        // 2、获取配置文件信息(com.siemens.datalayer.iot.util.JdbcUtil)
        // 3、注册数据库驱动

        databaseTableList = new ArrayList<>();
    }

   @AfterClass(description = "Delete the data written for testing in Mysql")
    public void deleteDataForMysql()
    {
        try
        {
            // 需要执行的sql语句，删除testcase中insert的数据，还原现场
            if(databaseTableList.size() > 0)
            {
                for (String databaseTable : databaseTableList)
                {
                    // 需要执行的sql语句
                    String sql = "DELETE FROM " + databaseTable;
                    statement.execute(sql);
                }
            }

            // 断开数据库的连接，释放资源
            statement.close();
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test(priority = 0,
          description = "insert/delete/update mysql(data source)",
          dataProvider = "api-engine-test-data-provider",
          dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'getData' request to graphql query interface.")
    @Story("insert/delete/update mysql(data source) Scenarios")
    public void postGraphForMysql(Map<String, String> paramMaps) throws JSONException {
        // 在每个testcase执行前，清空当前数据库
        // 这样做的原因在于：因为是比对整个当前数据库的数据，清空数据后，不会受之前case写入的脏数据的影响。
        try{
            String sql = "DELETE FROM " + paramMaps.get("database");
            statement.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // 这两行代码，在testcase执行之前执行，
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

                    // 若mysql数据insert、update、delete成功，则调用verityExpectedAndActualDataInMysql方法校验预期结果和实际结果
                    if (paramMaps.get("description").contains("data retrieved")) {
                        verityExpectedAndActualDataInMysql(paramMaps.get("database"),paramMaps.get("expectResults"));

                        databaseTableList.add(paramMaps.get("database"));
                    }
                }
            }
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

    @Step("verify the expected results and the actual data stored in MySQL")
    public static void verityExpectedAndActualDataInMysql(String database,String expectResultsFromExcel)
    {
        List<Map<String,Object>> expectListFromExcel = new ArrayList<>();
        if (expectResultsFromExcel != null)
        {
            List<String> list = Arrays.asList(expectResultsFromExcel.split("}"));

            List<String> listWithString = new ArrayList<>();
            for (int i=0;i<list.size();i++)
                listWithString.add(list.get(i) + "}");

            // 将listWithString中的String格式转换成Map
            for (int i=0;i<listWithString.size();i++)
            {
                Gson gson = new Gson();
                Map<String,Object> expectListFromExcelItem = new HashMap<>();
                expectListFromExcelItem = gson.fromJson(listWithString.get(i),expectListFromExcelItem.getClass());
                expectListFromExcel.add(expectListFromExcelItem);
            }
            System.out.println(expectListFromExcel);
        }

        // 定义变量actualListFromMysql：MySQL查询结果，为Map组成的List
        List<Map<String,Object>> actualListFromMysql = new ArrayList<>();
        try {
            String sql = "SELECT * from " + database;

            ResultSet rs = statement.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();

            while (rs.next()){
                Map<String,Object> actualListFromMysqlItem = new HashMap<>();

                for (int i=1;i<=metaData.getColumnCount();i++){
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(columnName);
                    actualListFromMysqlItem.put(columnName,value);
                }
                actualListFromMysql.add(actualListFromMysqlItem);
            }

            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        System.out.println(actualListFromMysql);
        Assert.assertEquals(expectListFromExcel.size(),actualListFromMysql.size());

        for (int i=0;i<actualListFromMysql.size();i++)
        {
            for (Map.Entry<String,Object> actualMapFromMysql : actualListFromMysql.get(i).entrySet())
            {
                String rowColumnOfMysqlKey = actualMapFromMysql.getKey();
                Object rowColumnOfMysqlValue = actualMapFromMysql.getValue();

                if (rowColumnOfMysqlValue instanceof Integer)
                {
                    Assert.assertEquals(MapUtils.getInteger(expectListFromExcel.get(i),rowColumnOfMysqlKey),rowColumnOfMysqlValue);
                }

                else if (rowColumnOfMysqlValue instanceof Float)
                {
                    Assert.assertEquals(MapUtils.getFloat(expectListFromExcel.get(i),rowColumnOfMysqlKey), (Float) rowColumnOfMysqlValue,0.001);
                }

                else if (rowColumnOfMysqlValue instanceof LocalDateTime)
                {
                    DateTimeFormatter dfWithT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"); // 格式一
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss"); // 格式二

                    // 将string格式转换成localdatetime格式，原来的格式为“格式一”
                    LocalDateTime ldt = LocalDateTime.parse(MapUtils.getString(expectListFromExcel.get(i),rowColumnOfMysqlKey),dfWithT);

                    // 将localdatetime格式转换成string格式，使用“格式二”
                    String login_time = df.format(ldt);
                    Assert.assertEquals(ldt,rowColumnOfMysqlValue);
                }

                else
                {
                    Assert.assertEquals(expectListFromExcel.get(i).get(rowColumnOfMysqlKey),rowColumnOfMysqlValue);
                }
            }
        }
    }
}