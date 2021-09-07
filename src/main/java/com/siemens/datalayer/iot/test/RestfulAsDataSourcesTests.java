package com.siemens.datalayer.iot.test;

import com.siemens.datalayer.apiengine.test.ApiEngineEndpoint;
import com.siemens.datalayer.iot.util.JdbcMysqlUtil;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import org.json.JSONException;
import org.testng.annotations.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Epic("SDL Api-engine")
@Feature("Restful as data source,Insert/Update/Delete data source")
public class RestfulAsDataSourcesTests {

    static Connection connection;
    static Statement statement;

    @Parameters({"base_url","port"})
    @BeforeClass(description = "Configure the host address and communication port of data-layer-api-engine;")
    public void setApiEngineEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("32552") String port)
    {
        ApiEngineEndpoint.setBaseUrl(base_url);
        ApiEngineEndpoint.setPort(port);

        // 执行case之前，连接MySQL，并且清空表Mysql_Test、Mysql_Device
        // 保持数据库连接不断开，最后在AfterClass(deleteDataForMysql)中关闭数据库连接
        try
        {
            // 连接数据库
            connection = JdbcMysqlUtil.getConnection("iot.dev.h2.db.properties");
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
    }

    @Test(priority = 0,
            description = "insert/delete/update restful(data source)"
            /* dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class */)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'getData' request to graphql query interface.")
    @Story("insert/delete/update restful(data source) Scenarios")
    public void postGraphForRestful() throws JSONException {
        // 在每个testcase执行前，清空当前数据库
        // 这样做的原因在于：因为是比对整个当前数据库的数据，清空数据后，不会受之前case写入的脏数据的影响。
        try {
            String sql = "SELECT * FROM USER_BASIC_INFO";
            ResultSet rs = statement.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();

            while (rs.next())
            {
                Map<String,Object> mysqlQueryResultsItem = new HashMap<>();
                for (int i=1;i<=metaData.getColumnCount();i++){
                    String column = metaData.getColumnName(i);
                    Object value = rs.getObject(column);
                    mysqlQueryResultsItem.put(column,value);
                }
                System.out.println(mysqlQueryResultsItem);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @AfterClass(description = "Delete the data written for testing in Mysql")
    public void deleteDataForMysql()
    {
        try
        {
            // 断开数据库的连接，释放资源
            statement.close();
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
