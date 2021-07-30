package com.siemens.datalayer.iot.test;

import com.google.gson.Gson;
import com.siemens.datalayer.apiengine.test.ApiEngineEndpoint;
import com.siemens.datalayer.apiengine.test.QueryEndPointTests;
import com.siemens.datalayer.iot.util.JdbcUtil;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.commons.collections4.MapUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.annotations.Optional;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Epic("User Query Scenarios")
public class UserQueryTests {

    static List<String> idList;

    @Parameters({"base_url","port"})
    @BeforeClass(description = "1.Configure the host address and communication port of data-layer-api-engine;")
    public void setApiEnigineEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("32552") String port)
    {
        // 执行case之前，连接MySQL，并且清空表Mysql_Test
        try
        {
            // 连接数据库
            Connection connection = JdbcUtil.getConnection();
            if(!connection.isClosed())
                System.out.println("Succeeded connecting to the Database!");

            // 操作数据库
            Statement statement = connection.createStatement();
            // 需要执行的sql语句
            String sql = "DELETE FROM Mysql_Test";
            statement.execute(sql);

            statement.close();
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        ApiEngineEndpoint.setBaseUrl(base_url);
        ApiEngineEndpoint.setPort(port);

        // 连接数据库的方式
        // 1、新建数据库配置文件(resources.db.properties)
        // 2、获取配置文件信息(com.siemens.datalayer.iot.util.JdbcUtil)
        // 3、注册数据库驱动

        idList = new ArrayList<>();
    }

   @AfterClass(description = "Delete the data written for testing in Mysql")
    public void deleteDataForMysql()
    {
        try
        {
            // 连接数据库
            Connection connection = JdbcUtil.getConnection();
            if(!connection.isClosed())
                System.out.println("Succeeded connecting to the Database!");

            // 操作数据库
            Statement statement = connection.createStatement();
            // 需要执行的sql语句
            if(idList.size() > 0){
                String sql = "DELETE FROM Mysql_Test WHERE ID in " + "(" + String.join(",",idList) + ")";
                statement.execute(sql);
            }

            statement.close();
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test(priority = 0,
          description = "mysql 增/删/改/查",
          dataProvider = "api-engine-test-data-provider",
          dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Feature("mysql insert/delete/update/get Scenarios")
    @Story("mysql 增/删/改/查")
    public void postGraphForMysql(Map<String, String> paramMaps) throws JSONException {
        if(paramMaps.containsKey("queryWithoutData") && paramMaps.containsKey("data"))
        {
            if (paramMaps.get("queryWithoutData").contains("$data"))
            {
                // 将"queryWithoutData"列中的占位符"$data"，替换成"data"列中的值，生成完整的apiengine-graphql请求语句"query"
                String query = paramMaps.get("queryWithoutData").replace("$data",paramMaps.get("data"));
                Response response = ApiEngineEndpoint.postGraphql(query);

                // 校验返回的response的最外层的statusCode，code，message
               QueryEndPointTests.checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));

                if (paramMaps.containsKey("database")  && paramMaps.containsKey("operation"))
                    if (paramMaps.containsKey("rspCodeOfMysql") && paramMaps.containsKey("rspDataOfMysql"))
                    {
                        String jsonPath = "data." + paramMaps.get("operation") + "." + "json_value" + "." + paramMaps.get("database");
                        List<String> responseOfMysql = response.jsonPath().getList(jsonPath);

                        // 校验操作数据库的”每条“返回结果，路径类似于data.Insert.json_value.Mysql_Test[0]
                        for (int i=0;i<responseOfMysql.size();i++)
                        {
                            String str = new Gson().toJson(responseOfMysql.get(i));
                            JSONObject jo = new JSONObject(str);
                            System.out.println("mysql返回code:"+String.valueOf(jo.get("code")) + " , mysql返回data:" + String.valueOf(jo.get("data")));
                            checkResponseCodeOfMysql(paramMaps,String.valueOf(jo.get("code")),String.valueOf(jo.get("data")));
                        }
                    }

                if (paramMaps.get("description").contains("data retrieved"))
                {
                    // 这5行代码，将excel-postGraphForMysql-data中的数据写入List ”sentenceForMysql“，以便获取里面字段的值
                    String requestOfGraphQL = paramMaps.get("data");
                    List<String> list = Arrays.asList(requestOfGraphQL.split("}"));

                    List<String> sentenceForMysql = new ArrayList<>();

                    for (int i=0;i<list.size();i++)
                        sentenceForMysql.add(list.get(i) + "}");

                    // 如果是Insert、Update MySQL，调用checkInsertUpdateMysql方法，校验在数据库中存储的数据是否正确。
                    if (paramMaps.get("queryWithoutData").contains("Insert") || paramMaps.get("queryWithoutData").contains("Update"))
                    {
                        for (int i=0;i<sentenceForMysql.size();i++)
                        {
                            Gson gson = new Gson();
                            Map<String,String> sentenceForMysqlItem = new HashMap<>();
                            sentenceForMysqlItem = gson.fromJson(sentenceForMysql.get(i),sentenceForMysqlItem.getClass());
                            idList.add(MapUtils.getDouble(sentenceForMysqlItem,"ID").toString());
                            checkInsertUpdateMysql(sentenceForMysqlItem);
                        }
                    }
                    else if (paramMaps.get("queryWithoutData").contains("Delete"))
                    {
                        for (int i=0;i<sentenceForMysql.size();i++)
                        {
                            Gson gson = new Gson();
                            Map<String,String> sentenceForMysqlItem = new HashMap<>();
                            sentenceForMysqlItem = gson.fromJson(sentenceForMysql.get(i),sentenceForMysqlItem.getClass());
                            checkDeleteMysql(sentenceForMysqlItem);
                        }
                    }
                }
            }
        }
        else
        {
            System.out.println("操作数据库的grapgql语句不符合规范");
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

    @Step("校验Insert Update MySQL数据是否存储正确")
    public static void checkInsertUpdateMysql(Map<String,String> data){
        try
        {
            // 连接数据库
            Connection connection = JdbcUtil.getConnection();
            if(!connection.isClosed())
                System.out.println("Succeeded connecting to the Database!");

            // 操作数据库
            Statement statement = connection.createStatement();
            // 需要执行的sql语句
            Double id = MapUtils.getDouble(data, "ID");
            String sql = "SELECT * from Mysql_Test WHERE ID = " + id;

            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()){
                System.out.println(rs.getString(1) + "--" +
                                   rs.getInt(2) + "--" +
                                   rs.getString(3) + "--" +
                                   rs.getString(4) + "--" +
                                   rs.getString(5) + "--" +
                                   rs.getFloat(6));
                DateTimeFormatter dfWithT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"); // 格式一
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss"); // 格式二

                // 将string格式转换成localdatetime格式，原来的格式为“格式一”
                LocalDateTime ldt = LocalDateTime.parse(MapUtils.getString(data,"login_time"),dfWithT);

                // 将localdatetime格式转换成string格式，使用“格式二”
                String login_time = df.format(ldt);

                Assert.assertEquals(rs.getString(1),MapUtils.getString(data,"deviceName"));
                Assert.assertEquals(rs.getInt(2),MapUtils.getDouble(data,"ID"),0.001);
                Assert.assertEquals(rs.getString(3),MapUtils.getString(data,"supplier"));
                Assert.assertEquals(rs.getString(4),MapUtils.getString(data,"operator"));
                Assert.assertEquals(rs.getString(5),login_time);
                Assert.assertEquals(rs.getFloat(6),MapUtils.getFloat(data,"score"),0.001);
            }

            rs.close();
            statement.close();
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Step("校验Delete MySQL数据是否删除正确")
    public static void checkDeleteMysql(Map<String,String> data){
        try
        {
            // 连接数据库
            Connection connection = JdbcUtil.getConnection();
            if(!connection.isClosed())
                System.out.println("Succeeded connecting to the Database!");

            // 操作数据库
            Statement statement = connection.createStatement();
            // 需要执行的sql语句
            Double id = MapUtils.getDouble(data, "ID");
            String sql = "SELECT * from Mysql_Test WHERE ID = " + id;

            ResultSet rs = statement.executeQuery(sql);

            System.out.println("rs.next():" + rs.next());
            Assert.assertFalse(rs.next());

            rs.close();
            statement.close();
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}