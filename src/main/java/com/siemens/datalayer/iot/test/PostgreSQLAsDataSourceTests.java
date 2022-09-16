package com.siemens.datalayer.iot.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.siemens.datalayer.apiengine.test.ApiEngineEndpoint;
import com.siemens.datalayer.apiengine.test.QueryEndPointTests;
import com.siemens.datalayer.iot.util.JdbcDatabaseUtil;
import com.siemens.datalayer.iot.util.MapTypeAdapter;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.json.JSONException;
import org.testng.Assert;
import org.testng.annotations.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Epic("SDL Api-engine")
@Feature("JDBC databases as data source")
public class PostgreSQLAsDataSourceTests {

    static Connection connection;
    static Statement statement;

    static List<String> databaseTableList;

    @Parameters({"base_url","port"})
    @BeforeClass(description = "Configure the host address,communication port of data-layer-api-engine;")
    public void setApiEngineEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("32552") String port)
    {
        ApiEngineEndpoint.setBaseUrl(base_url);
        ApiEngineEndpoint.setPort(port);

        // 保持数据库连接不断开，最后在AfterClass(deleteDataForMysql)中关闭数据库连接
        try
        {
            // 连接数据库
            connection = JdbcDatabaseUtil.getConnection("iot.test.postgresql.db.properties");
            if(!connection.isClosed())
                System.out.println("Succeeded connecting to the Database!");

            // 操作数据库
            statement = connection.createStatement();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // 连接数据库的方式
        // 1、新建数据库配置文件(resources.iot.test.postgresql.db.properties)
        // 2、获取配置文件信息(com.siemens.datalayer.iot.util.JdbcMysqlUtil)
        // 3、注册数据库驱动

        databaseTableList = new ArrayList<>();
    }

    @AfterClass(description = "Delete the data written in PostgreSQL for the test")
    public void deleteDataForPostgreSQL()
    {
        try
        {
            System.out.println(databaseTableList);
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
            System.out.println("disconnect database...");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test(priority = 0,
            description = "query PostgresSQL(data source)",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'query' request to graphql interface.")
    @Story("PostgreSQL as data source,read data source")
    public void readPostgreSQL(Map<String, String> paramMaps) throws JSONException {
        // 在每个testcase执行前，清空当前数据库
        // 这样做的原因在于：因为是比对整个当前数据库的数据，清空数据后，不会受之前case写入的脏数据的影响。
        clearDatabase(paramMaps);

        // 在testcase最先执行，
        // 作用为：比如query、update、delete数据库前，需要数据库中有数据
        preExecution(paramMaps);

        if (paramMaps.containsKey("query")) {
            String query = paramMaps.get("query");
            Response response = ApiEngineEndpoint.postGraphql(query);
            System.out.println("response.jsonPath().prettify(): " + "\n" + response.jsonPath().prettify());

            // 校验返回的response的最外层的statusCode，code，message
            QueryEndPointTests.checkResponseCode(paramMaps, response.getStatusCode(),
                    response.jsonPath().getString("code"), response.jsonPath().getString("message"));

            checkResponseData(paramMaps, response);
        }
    }

    @Test(  dependsOnMethods = { "readPostgreSQL"},
            priority = 0,
            description = "insert/update/delete Oracle(data source)",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'mutation' request to graphql interface.")
    @Story("PostgreSQL as data source,write data source")
    public void writePostgreSQL(Map<String, String> paramMaps) throws JSONException {
        // 在每个testcase执行前，清空当前数据库
        // 这样做的原因在于：因为是比对整个当前数据库的数据，清空数据后，不会受之前case写入的脏数据的影响。
        try{
            String sql = "DELETE FROM " + paramMaps.get("database");
            statement.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // 这两行代码，在testcase最先执行，
        // 作用为：比如update、delete数据库前，需要数据库中有数据
        if (paramMaps.containsKey("pre-execution"))
            ApiEngineEndpoint.postGraphql(paramMaps.get("pre-execution"));

        if (paramMaps.containsKey("graphQLSentence")) {
            String query = paramMaps.get("graphQLSentence");
            Response response = ApiEngineEndpoint.postGraphql(query);
            System.out.println("response.jsonPath().prettify(): " + "\n" +response.jsonPath().prettify());

            // 校验返回的response的最外层的statusCode，code，message
            QueryEndPointTests.checkResponseCode(paramMaps, response.getStatusCode(),
                    response.jsonPath().getString("code"), response.jsonPath().getString("message"));

            // 校验接口返回的rspData
            checkResponseData(paramMaps,response);

            // 现在的testcase，每个都有参数database
            if (paramMaps.containsKey("database")) {
                // 若mysql数据insert、update、delete成功，则调用verityExpectedAndActualDataInMysql方法校验预期结果和实际结果
                if (paramMaps.get("description").contains("good request") && paramMaps.get("description").contains("data retrieved"))
                {
                    verityExpectedAndActualDataInPostgreSQL(paramMaps.get("database"),paramMaps.get("expectedDataStoredInPostgreSQL"));
                    if (!databaseTableList.contains(paramMaps.get("database"))) {
                        databaseTableList.add(paramMaps.get("database"));
                    }
                }
            }
        }
    }

    @Step("Clear database")
    public static void clearDatabase(Map<String,String> requestParameters){
        try {
            String sql = "DELETE FROM " + requestParameters.get("database");
            statement.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
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
            Map<String,Object> actualResponseData = response.jsonPath().getMap("data");

            // 转换成Map，重写TypeAdapter方法为MapTypeAdapter，解决返回的map含有小数点的问题
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(new TypeToken<Map<String, Object>>() {
                    }.getType(), new MapTypeAdapter()).create();

            System.out.println("requestParameters.get(\"rspData\"): " + "\n" + requestParameters.get("rspData"));
            Map<String, Object> expectedResponseData = gson.fromJson(requestParameters.get("rspData"), new TypeToken<Map<String, Object>>() {
            }.getType());

            Assert.assertEquals(actualResponseData,expectedResponseData);
        }
    }

    @Step("Verify the data expected to be stored in the PostgreSQL and the data actually stored")
    public static void verityExpectedAndActualDataInPostgreSQL(String database, String expectResultsFromExcel)
    {
        List<Map<String,Object>> expectListFromExcel = new ArrayList<>();
        if (expectResultsFromExcel != null)
        {
            // 转换成Map，重写TypeAdapter方法为MapTypeAdapter，解决String转换成List<Map<String,Object>>时，会将整数型数据自动添加小数点的问题
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(new TypeToken<Map<String, Object>>() {
                    }.getType(), new MapTypeAdapter()).create();

            expectListFromExcel = gson.fromJson(expectResultsFromExcel, new TypeToken<List<Map<String, Object>>>() {}.getType());

            System.out.println("expectListFromExcel: ");
            System.out.println(expectListFromExcel);
        }

        // 定义变量actualListFromMysql：MySQL查询结果，为Map组成的List
        List<Map<String,Object>> actualListFromMysql = new ArrayList<>();
        try {
            String sql = "SELECT * from " + database + " order by \"index\" ";

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

        System.out.println("actualListFromMysql: ");
        System.out.println(actualListFromMysql);
        Assert.assertEquals(expectListFromExcel.size(),actualListFromMysql.size());

        for (int i=0;i<actualListFromMysql.size();i++)
        {
            for (Map.Entry<String,Object> actualMapFromMysql : actualListFromMysql.get(i).entrySet())
            {
                String rowColumnOfMysqlKey = actualMapFromMysql.getKey();
                Object rowColumnOfMysqlValue = actualMapFromMysql.getValue();

                Assert.assertEquals(expectListFromExcel.get(i).get(rowColumnOfMysqlKey),rowColumnOfMysqlValue);
            }
        }
    }
}
