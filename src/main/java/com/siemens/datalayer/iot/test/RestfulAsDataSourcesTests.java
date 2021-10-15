package com.siemens.datalayer.iot.test;

import com.google.gson.Gson;
import com.siemens.datalayer.apiengine.test.ApiEngineEndpoint;
import com.siemens.datalayer.apiengine.test.QueryEndPointTests;
import com.siemens.datalayer.iot.util.JdbcMysqlUtil;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.commons.collections4.MapUtils;
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
@Feature("Restful as data source,write (data source) by simple/complex single/multi entity")
public class RestfulAsDataSourcesTests {
    static Connection connection;
    static Statement statement;

    static Connection connectionOfUser;
    static Statement statementOfUser;

    static Connection connectionOfProduct;
    static Statement statementOfProduct;

    static Connection connectionOfPerson;
    static Statement statementOfPerson;

    static Map<String, List<String>> tableOfH2;

    @Parameters({"base_url","port"})
    @BeforeClass(description = "Configure the host address and communication port of data-layer-api-engine;")
    public void setApiEngineEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("32552") String port)
    {
        ApiEngineEndpoint.setBaseUrl(base_url);
        ApiEngineEndpoint.setPort(port);

        // 保持数据库连接不断开，最后在AfterClass(deleteDataForH2)中关闭数据库连接
        try
        {
            // 连接数据库
            connectionOfUser = JdbcMysqlUtil.getConnection("iot.dev.h2.user.db.properties");
            if(!connectionOfUser.isClosed())
                System.out.println("Succeeded connecting to the Database:User!");

            // 操作数据库
            statementOfUser = connectionOfUser.createStatement();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try
        {
            connectionOfProduct = JdbcMysqlUtil.getConnection("iot.dev.h2.product.db.properties");
            if (!connectionOfProduct.isClosed())
                System.out.println("Succeeded connecting to the Database:Product!");

            statementOfProduct = connectionOfProduct.createStatement();
        } catch (SQLException throwables){
            throwables.printStackTrace();
        }

        try
        {
            connectionOfPerson = JdbcMysqlUtil.getConnection("iot.dev.h2.person.db.properties");
            if (!connectionOfPerson.isClosed())
                System.out.println("Succeeded connecting to the Database:Person!");

            statementOfPerson = connectionOfPerson.createStatement();
        } catch (SQLException throwables){
            throwables.printStackTrace();
        }

        // 连接数据库的方式
        // 1、新建数据库配置文件(resources.iot.dev.mysql.db.properties)
        // 2、获取配置文件信息(com.siemens.datalayer.iot.util.JdbcUtil)
        // 3、注册数据库驱动

        tableOfH2 = new HashMap<>();
    }

    @AfterClass(description = "Delete the data written for testing in h2")
    public void deleteDataForH2()
    {
        System.out.println(tableOfH2);
        try
        {
            // 断开数据库的连接，释放资源
            statementOfUser.close();
            connectionOfUser.close();

            statementOfProduct.close();
            connectionOfProduct.close();

            statementOfPerson.close();
            connectionOfPerson.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test(priority = 0,
            description = "insert/update/delete restful(data source) Scenarios",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'getData' request to graphql query interface.")
    @Story("Restful as data source,write (data source) by single simple/complex entity")
    public void restfulTransactionSingleEntity(Map<String, String> paramMaps) throws JSONException {

        String entityName = paramMaps.get("entityName");
        List<String> tableList = Arrays.asList(paramMaps.get("tableOfH2").split(","));
        Statement statement = chooseStatement(paramMaps.get("entityName"));

        // 将用到的entityName和对应的table放入tableOfH2中
        if (!tableOfH2.containsKey(entityName)){
            tableOfH2.put(entityName,tableList);
        }
        else {
            for (String table : tableList){
                if (!tableOfH2.get(entityName).contains(table))
                    tableOfH2.get(entityName).add(table);
            }
        }

        // 在每个testcase执行前，清空当前数据库
        // 这样做的原因在于：因为是比对整个当前数据库的数据，清空数据后，不会受之前case写入的脏数据的影响。
        for (String table : tableList)
        {
            String deleteSql = "DELETE FROM " + table;

            try{
                statement.execute(deleteSql);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        // 这段代码，在testcase最先执行，
        // 作用为：比如验证事务处理，需要数据库中已有数据
        if (paramMaps.containsKey("pre-execution"))
        {
            List<String> preSqlList = Arrays.asList(paramMaps.get("pre-execution").split(";"));

            for (String preSql : preSqlList)
            {
                try {
                    statement.execute(preSql);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        if (paramMaps.containsKey("graphQLSentence")) {
            String query = paramMaps.get("graphQLSentence");
            Response response = ApiEngineEndpoint.postGraphql(query);

            // 校验返回的response的最外层的statusCode，code，message
            QueryEndPointTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));

            // 现在的testcase，每个都有参数database
            if (paramMaps.containsKey("entityName")){
                // 如外层返回code为101301、101303等，就不会返回内层code
                if (paramMaps.containsKey("rspCodeOfH2")){
                    String jsonPath = "data." + paramMaps.get("entityName") + "_" + paramMaps.get("operation") + ".json_value";

                    List<String> responseOfH2 = response.jsonPath().getList(jsonPath);

                    // 校验操作数据库的”每条“返回结果，路径类似于data.User_Insert.json_value
                    for (int i = 0; i < responseOfH2.size(); i++) {
                        String str = new Gson().toJson(responseOfH2.get(i));
                        JSONObject jo = new JSONObject(str);
                        System.out.println("h2返回code:" + String.valueOf(jo.get("code")) + " , h2返回data:" + String.valueOf(jo.get("data")));
                        checkResponseCodeOfH2(paramMaps, String.valueOf(jo.get("code")));
                    }

                    // 若h2数据insert、update、delete成功，则调用verityExpectedAndActualDataInH2方法校验各个h2数据表的预期结果和实际结果
                    if (paramMaps.get("rspCode").equals("100000") && paramMaps.get("rspMessage").equals("Successfully")) {
                        for (String table : tableList)
                        {
                            verityExpectedAndActualDataInH2(paramMaps.get("expectResultOf"+table),paramMaps.get("entityName"),table);
                        }
                    }
                }
            }
        }
    }

    @Test(priority = 0,
            description = "insert/update/delete restful(data source) Scenarios",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'getData' request to graphql query interface.")
    @Story("Restful as data source,write (data source) by multi simple/complex entity")
    public void restfulTransactionMultiEntity(Map<String, String> paramMaps) {

        List<String> entityList = Arrays.asList(paramMaps.get("entityName").split(","));
        for (String entity : entityList) {
            Statement preStatement = chooseStatement(entity);

            if (paramMaps.containsKey("tableOf" + entity)) {
                {
                    List<String> tableList = Arrays.asList(paramMaps.get("tableOf" + entity).split(","));

                    // 将用到的entityName和对应的table放入tableOfH2中
                    if (!tableOfH2.containsKey(entity)) {
                        tableOfH2.put(entity, tableList);
                    } else {
                        for (String table : tableList) {
                            if (!tableOfH2.get(entity).contains(table))
                                tableOfH2.get(entity).add(table);
                        }
                    }

                    // 在每个testcase执行前，清空当前数据库
                    // 这样做的原因在于：因为是比对整个当前数据库的数据，清空数据后，不会受之前case写入的脏数据的影响。
                    for (String table : tableList) {
                        String deleteSql = "DELETE FROM " + table;

                        try {
                            preStatement.execute(deleteSql);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                }
            }

            // 这段代码，在testcase最先执行，
            // 作用为：比如验证事务处理，需要数据库中已有数据
            if (paramMaps.containsKey("pre-executionOf" + entity)){
                List<String> preSqlList = Arrays.asList(paramMaps.get("pre-executionOf" + entity).split(";"));

                for (String preSql : preSqlList)
                {
                    try {
                        preStatement.execute(preSql);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        }

        if (paramMaps.containsKey("graphQLSentence")) {
            String query = paramMaps.get("graphQLSentence");
            Response response = ApiEngineEndpoint.postGraphql(query);

            // 校验返回的response的最外层的statusCode，code，message
            QueryEndPointTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));

            for (String entity : entityList) {
                Statement verityStatement = chooseStatement(entity);

                if (paramMaps.containsKey("tableOf" + entity)) {
                    {
                        List<String> tableList = Arrays.asList(paramMaps.get("tableOf" + entity).split(","));

                        // 若h2数据insert、update、delete成功，则调用verityExpectedAndActualDataInH2方法校验各个h2数据表的预期结果和实际结果
                        if (paramMaps.get("rspCode").equals("100000") && paramMaps.get("rspMessage").equals("Successfully")) {
                            for (String table : tableList)
                            {
                                verityExpectedAndActualDataInH2(paramMaps.get("expectResultOf"+table),entity,table);
                            }
                        }
                    }
                }
            }
        }
    }

    public static Statement chooseStatement(String entityName)
    {
        if (entityName.equals("User"))
            return statementOfUser;
        else if (entityName.equals("Product"))
            return statementOfProduct;
        else if (entityName.equals("Person"))
            return statementOfPerson;
        else
            return statement;
    }

    @Step("校验操作数据库的返回结果")
    public static void checkResponseCodeOfH2(Map<String,String> requestParameters, String actualCodeOfH2)
    {
        if(requestParameters.containsKey("rspCodeOfH2"))
        {
            Assert.assertEquals(actualCodeOfH2,requestParameters.get("rspCodeOfH2"),"The operation codeOfH2 in response message matches the expected value.");
        }
        else
        {
            if (requestParameters.get("description").contains("good request"))
                Assert.assertEquals(actualCodeOfH2,"0","The operation codeOfH2 in response message matches the expected value.");
            else
                System.out.println("Operation codeOfH2 is not specified for test case： " + requestParameters.get("test-id"));
        }
    }

    @Step("verify the expected results and the actual data stored in h2")
    public static void verityExpectedAndActualDataInH2(String expectResultsFromExcel,String entityName,String table)
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
        }
        System.out.println("expectListFromExcel for " + entityName + "-" + table + ": " + expectListFromExcel);

        Statement statement = chooseStatement(entityName);

        // 定义变量actualListFromH2：h2查询结果，为Map组成的List
        List<Map<String,Object>> actualListFromH2 = new ArrayList<>();
        try {
            String sql = "SELECT * from " + table;

            ResultSet rs = statement.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();

            while (rs.next()){
                Map<String,Object> actualListFromH2Item = new HashMap<>();

                for (int i=1;i<=metaData.getColumnCount();i++){
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(columnName);
                    actualListFromH2Item.put(columnName,value);
                }
                actualListFromH2.add(actualListFromH2Item);
            }

            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println("actualListFromH2 for " + entityName + "-" + table + ": " + actualListFromH2);

        Assert.assertEquals(expectListFromExcel.size(),actualListFromH2.size());

        for (int i=0;i<actualListFromH2.size();i++)
        {
            for (Map.Entry<String,Object> actualMapFromH2 : actualListFromH2.get(i).entrySet())
            {
                String rowColumnOfH2Key = actualMapFromH2.getKey();
                Object rowColumnOfH2Value = actualMapFromH2.getValue();

                if (rowColumnOfH2Value instanceof Integer)
                {
                    Assert.assertEquals(MapUtils.getInteger(expectListFromExcel.get(i),rowColumnOfH2Key),rowColumnOfH2Value);
                }

                else if (rowColumnOfH2Value instanceof Float)
                {
                    Assert.assertEquals(MapUtils.getFloat(expectListFromExcel.get(i),rowColumnOfH2Key), (Float) rowColumnOfH2Value,0.001);
                }

                else if (rowColumnOfH2Value instanceof LocalDateTime)
                {
                    DateTimeFormatter dfWithT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"); // 格式一
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss"); // 格式二

                    // 将string格式转换成localdatetime格式，原来的格式为“格式一”
                    LocalDateTime ldt = LocalDateTime.parse(MapUtils.getString(expectListFromExcel.get(i),rowColumnOfH2Key),dfWithT);

                    // 将localdatetime格式转换成string格式，使用“格式二”
                    String login_time = df.format(ldt);
                    Assert.assertEquals(ldt,rowColumnOfH2Value);
                }

                else
                {
                    Assert.assertEquals(expectListFromExcel.get(i).get(rowColumnOfH2Key),rowColumnOfH2Value);
                }
            }
        }
    }
}