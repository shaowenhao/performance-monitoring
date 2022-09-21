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
import org.apache.commons.collections4.MapUtils;
import org.json.JSONException;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.*;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Epic("SDL Api-engine")
@Feature("JDBC databases as data source")
public class JDBCDatabasesTests {
    // for Oracle
    static Connection connectionOfOracle;
    static Statement statementOfOracle;
    // for SQL Server
    static Connection connectionOfSqlServer;
    static Statement statementOfSqlServer;
    // for H2
    static Connection connectionOfH2;
    static Statement statementOfH2;

    static List<String> oracleTableList;
    static List<String> sqlserverTableList;
    static List<String> h2TableList;

    @Parameters({"base_url","port"})
    @BeforeClass(description = "Configure the host address,communication port of data-layer-api-engine;")
    public void setApiEngineEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("32552") String port)
    {
        ApiEngineEndpoint.setBaseUrl(base_url);
        ApiEngineEndpoint.setPort(port);

        /**
         连接数据库：Oracle
         */
        // 保持数据库连接不断开，最后在AfterClass(deleteDataForDatabases)中关闭数据库连接
        try
        {
            // 连接数据库
            connectionOfOracle = JdbcDatabaseUtil.getConnection("iot.test.oracle.db.properties");
            if(!connectionOfOracle.isClosed())
                System.out.println("Succeeded connecting to the Database!");

            // 操作数据库
            statementOfOracle = connectionOfOracle.createStatement();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        /**
         连接数据库：SQL Server
         */
        // 保持数据库连接不断开，最后在AfterClass(deleteDataForDatabases)中关闭数据库连接
        try
        {
            // 连接数据库
            connectionOfSqlServer = JdbcDatabaseUtil.getConnection("iot.test.sqlserver.db.properties");
            if(!connectionOfSqlServer.isClosed())
                System.out.println("Succeeded connecting to the Database!");

            // 操作数据库
            statementOfSqlServer = connectionOfSqlServer.createStatement();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        /**
         连接数据库：H2
         */
        // 保持数据库连接不断开，最后在AfterClass(deleteDataForDatabases)中关闭数据库连接
        try
        {
            // 连接数据库
            connectionOfH2 = JdbcDatabaseUtil.getConnection("iot.dev.h2.user.db.properties");
            if(!connectionOfH2.isClosed())
                System.out.println("Succeeded connecting to the Database!");

            // 操作数据库
            statementOfH2 = connectionOfH2.createStatement();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // 连接数据库的方式
        // 1、新建数据库配置文件(resources.iot.dev.mysql.db.properties)
        // 2、获取配置文件信息(com.siemens.datalayer.iot.util.JdbcUtil)
        // 3、注册数据库驱动

        oracleTableList = new ArrayList<>();
        sqlserverTableList = new ArrayList<>();
        h2TableList = new ArrayList<>();
    }

    @AfterClass(description = "Delete the data written in databases for the test")
    public void deleteDataForDatabases()
    {
        /**
         还原现场：Oracle
         */
        try
        {
            System.out.println(oracleTableList);
            // 需要执行的sql语句，删除testcase中写入的数据，还原现场
            if(oracleTableList.size() > 0)
            {
                for (String databaseTable : oracleTableList)
                {
                    // 需要执行的sql语句
                    String sql = "DELETE FROM " + databaseTable;
                    statementOfOracle.execute(sql);
                }
            }

            // 断开数据库的连接，释放资源
            statementOfOracle.close();
            connectionOfOracle.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        /**
         还原现场：SQL Server
         */
        try
        {
            System.out.println(sqlserverTableList);
            // 需要执行的sql语句，删除testcase中写入的数据，还原现场
            if(sqlserverTableList.size() > 0)
            {
                for (String databaseTable : sqlserverTableList)
                {
                    // 需要执行的sql语句
                    String sql = "DELETE FROM " + databaseTable;
                    statementOfSqlServer.execute(sql);
                }
            }

            // 断开数据库的连接，释放资源
            statementOfSqlServer.close();
            connectionOfSqlServer.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        /**
         还原现场：H2
         */
        try
        {
            System.out.println(h2TableList);
            // 需要执行的sql语句，删除testcase中写入的数据，还原现场
            if(h2TableList.size() > 0)
            {
                for (String databaseTable : h2TableList)
                {
                    // 需要执行的sql语句
                    String sql = "DELETE FROM " + databaseTable;
                    statementOfH2.execute(sql);
                }
            }

            // 断开数据库的连接，释放资源
            statementOfH2.close();
            connectionOfH2.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test(priority = 0,
            description = "query/insert/update/delete Oracle(data source)",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'query or mutation' request to graphql interface.")
    @Story("Oracle as data source,read/write data source")
    public void readWriteOracle(Map<String, String> paramMaps) throws JSONException {
        if (!oracleTableList.contains(paramMaps.get("database")))
            oracleTableList.add(paramMaps.get("database"));

        // 在每个testcase执行前，清空当前数据库
        // 这样做的原因在于：因为是比对整个当前数据库的数据，清空数据后，不会受之前case写入的脏数据的影响。
        clearDatabase(paramMaps);

        // 在testcase最先执行，
        // 作用为：比如query、update、delete数据库前，需要数据库中有数据
        preExecution(paramMaps);

        if (paramMaps.containsKey("graphQLSentence")) {
            String query = paramMaps.get("graphQLSentence");
            Response response = ApiEngineEndpoint.postGraphql(query);

            // 校验返回的response的最外层的statusCode，code，message
            QueryEndPointTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));

            checkResponseData(paramMaps,response);

            verityExpectedAndActualDataInDatabase(paramMaps);
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
        if (!sqlserverTableList.contains(paramMaps.get("database")))
            sqlserverTableList.add(paramMaps.get("database"));

        // 在每个testcase执行前，清空当前数据库
        // 这样做的原因在于：因为是比对整个当前数据库的数据，清空数据后，不会受之前case写入的脏数据的影响。
        clearDatabase(paramMaps);

        // 在testcase最先执行，
        // 作用为：比如query、update、delete数据库前，需要数据库中有数据
        preExecution(paramMaps);

        if (paramMaps.containsKey("graphQLSentence")) {
            String query = paramMaps.get("graphQLSentence");
            Response response = ApiEngineEndpoint.postGraphql(query);

            // 校验返回的response的最外层的statusCode，code，message
            QueryEndPointTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));

            checkResponseData(paramMaps,response);

            verityExpectedAndActualDataInDatabase(paramMaps);
        }
    }

    @Test(priority = 0,
            description = "query/insert/update/delete H2(data source)",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'query or mutation' request to graphql interface.")
    @Story("H2 as data source,read/write data source")
    public void readWriteH2(Map<String, String> paramMaps) throws JSONException {
        if (!h2TableList.contains(paramMaps.get("database")))
            h2TableList.add(paramMaps.get("database"));

        // 在每个testcase执行前，清空当前数据库
        // 这样做的原因在于：因为是比对整个当前数据库的数据，清空数据后，不会受之前case写入的脏数据的影响。
        clearDatabase(paramMaps);

        // 在testcase最先执行，
        // 作用为：比如query、update、delete数据库前，需要数据库中有数据
        preExecution(paramMaps);

        if (paramMaps.containsKey("graphQLSentence")) {
            String query = paramMaps.get("graphQLSentence");
            Response response = ApiEngineEndpoint.postGraphql(query);

            // 校验返回的response的最外层的statusCode，code，message
            QueryEndPointTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));

            checkResponseData(paramMaps,response);

            verityExpectedAndActualDataInDatabase(paramMaps);
        }
    }

    @Step("Clear database")
    public static void clearDatabase(Map<String,String> requestParameters){
        if (requestParameters.get("graphQLSentence").contains("Oracle"))
        {
            try {
                String sql = "DELETE FROM " + requestParameters.get("database");
                statementOfOracle.execute(sql);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        else if (requestParameters.get("graphQLSentence").contains("SqlServer"))
        {
            try {
                String sql = "DELETE FROM " + requestParameters.get("database");
                statementOfSqlServer.execute(sql);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        else if (requestParameters.get("graphQLSentence").contains("H2"))
        {
            try {
                String sql = "DELETE FROM " + requestParameters.get("database");
                statementOfH2.execute(sql);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
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

    @Step("Verify the data expected to be stored in the database and the data actually stored")
    public static void verityExpectedAndActualDataInDatabase(Map<String,String> requestParameters) {
        if (requestParameters.containsKey("expectedDataStoredInDatabase"))
        {
            List<Map<String,Object>> expectListFromExcel = new ArrayList<>();
            ResultSet rs = null;

            // 转换成Map，重写TypeAdapter方法为MapTypeAdapter，解决String转换成List<Map<String,Object>>时，会将整数型数据自动添加小数点的问题
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(new TypeToken<Map<String, Object>>() {
                    }.getType(), new MapTypeAdapter()).create();

            expectListFromExcel = gson.fromJson(requestParameters.get("expectedDataStoredInDatabase"), new TypeToken<List<Map<String, Object>>>() {}.getType());

            System.out.println("expectListFromExcel: ");
            System.out.println(expectListFromExcel);

            // actualListFromDatabase：数据库查询结果，为Map组成的List
            List<Map<String,Object>> actualListFromDatabase = new ArrayList<>();
            try {
                String sql = "SELECT * from " + requestParameters.get("database") + " order by \"ID\" ";

                if (requestParameters.get("graphQLSentence").contains("Oracle"))
                {
                    rs = statementOfOracle.executeQuery(sql);
                }
                else if (requestParameters.get("graphQLSentence").contains("SqlServer"))
                {
                    rs = statementOfSqlServer.executeQuery(sql);
                }
                else if (requestParameters.get("graphQLSentence").contains("H2"))
                {
                    rs = statementOfH2.executeQuery(sql);
                }
                ResultSetMetaData metaData = rs.getMetaData();

                while (rs.next()){
                    Map<String,Object> actualListFromDatabaseItem = new HashMap<>();

                    for (int i=1;i<=metaData.getColumnCount();i++){
                        String columnName = metaData.getColumnName(i);
                        Object value = rs.getObject(columnName);
                        actualListFromDatabaseItem.put(columnName,value);
                    }
                    actualListFromDatabase.add(actualListFromDatabaseItem);
                }

                rs.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            System.out.println("actualListFromDatabase: ");
            System.out.println(actualListFromDatabase);
            Assert.assertEquals(expectListFromExcel.size(),actualListFromDatabase.size());

            for (int i=0;i<actualListFromDatabase.size();i++)
            {
                for (Map.Entry<String,Object> actualMapFromDatabase : actualListFromDatabase.get(i).entrySet())
                {
                    String rowColumnOfDatabaseKey = actualMapFromDatabase.getKey();
                    Object rowColumnOfDatabaseValue = actualMapFromDatabase.getValue();
                    System.out.println(rowColumnOfDatabaseValue.getClass() + "," + expectListFromExcel.get(i).get(rowColumnOfDatabaseKey).getClass());

                    if (rowColumnOfDatabaseValue instanceof Integer)
                    {
                        Assert.assertEquals(rowColumnOfDatabaseValue,MapUtils.getInteger(expectListFromExcel.get(i),rowColumnOfDatabaseKey));
                    }

                    else if (rowColumnOfDatabaseValue instanceof BigDecimal)
                    {
                        Assert.assertEquals(rowColumnOfDatabaseValue.toString(),expectListFromExcel.get(i).get(rowColumnOfDatabaseKey).toString());
                    }

                    else if (rowColumnOfDatabaseValue instanceof Float)
                    {
                        Assert.assertEquals((Float) rowColumnOfDatabaseValue,MapUtils.getFloat(expectListFromExcel.get(i),rowColumnOfDatabaseKey), 0.001);
                    }

                    else if (rowColumnOfDatabaseValue instanceof Double)
                    {
                        Assert.assertEquals((Double) rowColumnOfDatabaseValue,MapUtils.getDouble(expectListFromExcel.get(i),rowColumnOfDatabaseKey), 0.001);
                    }

                    else if (rowColumnOfDatabaseValue instanceof LocalDateTime)
                    {
                        DateTimeFormatter dfWithT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"); // 格式一
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss"); // 格式二

                        // 将string格式转换成localdatetime格式，原来的格式为“格式一”
                        LocalDateTime ldt = LocalDateTime.parse(MapUtils.getString(expectListFromExcel.get(i),rowColumnOfDatabaseKey),dfWithT);

                        // 将localdatetime格式转换成string格式，使用“格式二”
                        String loginTime = df.format(ldt);
                        Assert.assertEquals(ldt,rowColumnOfDatabaseValue);
                    }

                    else if (rowColumnOfDatabaseValue instanceof Timestamp)
                    {
                        // Timestamp转String
                        String loginTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(rowColumnOfDatabaseValue);
                        Assert.assertEquals(loginTime,expectListFromExcel.get(i).get(rowColumnOfDatabaseKey));
                    }

                    else if (rowColumnOfDatabaseValue instanceof Date)
                    {
                        String loginTime = new SimpleDateFormat("yyyy-MM-dd").format(rowColumnOfDatabaseValue);
                        Assert.assertEquals(loginTime,expectListFromExcel.get(i).get(rowColumnOfDatabaseKey));
                    }

                    else
                    {
                        Assert.assertEquals(rowColumnOfDatabaseValue,expectListFromExcel.get(i).get(rowColumnOfDatabaseKey));
                    }
                }
            }
        }
    }
}