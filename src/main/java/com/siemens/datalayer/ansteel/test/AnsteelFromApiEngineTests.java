package com.siemens.datalayer.ansteel.test;

import cn.hutool.core.collection.CollectionUtil;
import com.siemens.datalayer.apiengine.test.ApiEngineEndpoint;
import com.siemens.datalayer.apiengine.test.QueryEndPointTests;
import com.siemens.datalayer.connector.test.ConnectorConfigureEndpoint;
import com.siemens.datalayer.connector.test.ConnectorEndpoint;
import com.siemens.datalayer.iot.util.JdbcMysqlUtil;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.commons.collections4.MapUtils;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.*;

import java.sql.*;
import java.util.*;

@Epic("SDL Api-engine")
@Feature("verify ansteel function")
public class AnsteelFromApiEngineTests {
    static Connection connection;
    static Statement statement;

    @Parameters({"base_url","port","baseUrlOfConnector","portOfConnector","baseUrlOfConnectorConfigure","portOfConnectorConfigure"})
    @BeforeClass(description = "Configure the host address,communication port and database properties file of data-layer-api-engine;")
    public void setApiEngineEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("32760") String port,
                                     @Optional("http://140.231.89.85")  String baseUrlOfConnector, @Optional("30747") String portOfConnector,
                                     @Optional("http://140.231.89.85")  String baseUrlOfConnectorConfigure, @Optional("30674") String portOfConnectorConfigure)
    {
        ApiEngineEndpoint.setBaseUrl(base_url);
        ApiEngineEndpoint.setPort(port);

        ConnectorEndpoint.setBaseUrl(baseUrlOfConnector);
        ConnectorEndpoint.setPort(portOfConnector);

        ConnectorConfigureEndpoint.setBaseUrl(baseUrlOfConnectorConfigure);
        ConnectorConfigureEndpoint.setPort(portOfConnectorConfigure);

        // 保持数据库连接不断开，最后在AfterClass(deleteDataForMysql)中关闭数据库连接
        try
        {
            // 连接数据库
            connection = JdbcMysqlUtil.getConnection("ansteel.test.clickhouse.db.properties");
            if(!connection.isClosed())
                System.out.println("Succeeded connecting to the Database!");

            // 操作数据库
            statement = connection.createStatement();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @AfterClass(description = "Delete the data written for testing in Mysql")
    public void disconnectDatabase()
    {
        try
        {
            // 断开数据库的连接，释放资源
            statement.close();
            connection.close();
            System.out.println("disconnect database...");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test(	priority = 0,
            description = "verify smart space function",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class
    )
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'getData' request to graphql query interface.")
    @Story("Verify smart space function with fixed graph,")
    public void verifyAnsteelWithfixedGraph(Map<String, String> paramMaps) {
        ConnectorEndpoint.clearAllCaches();
        ConnectorEndpoint.clearRedisCache();
        ConnectorEndpoint.clearRedisCaches();
        // 执行case之前，需调用connector-configure（clear all cache接口）清除缓存
        ConnectorConfigureEndpoint.clearAllCache();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (paramMaps.containsKey("query")) {
            String query = paramMaps.get("query");
            System.out.println(query);
            Response response = ApiEngineEndpoint.postGraphql(query);
            System.out.println(response.jsonPath().prettify());

            // 校验返回的response的最外层的statusCode，code，message
            QueryEndPointTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"),
                    response.jsonPath().getString("message"));

            // 如果是query语句，判断response中是否返回所有的层级结构
            if (paramMaps.containsKey("entities") && !paramMaps.containsKey("rspCodeOfDatasource")
                    && !paramMaps.containsKey("rspMessageOfDatasource"))
            {
                List<String> pathList = Arrays.asList(paramMaps.get("entities").trim().split("->"));
                Map<String,Object> responseMap = response.jsonPath().getMap("data");

                System.out.println(pathList);
                verifyResponse(responseMap,pathList);
            }

            if (paramMaps.containsKey("sqlStatement"))
            {
                verifyIfResponseMatchesClickhouse(response,paramMaps);
            }
        }
    }

    @Step("判断response中是否返回所有的层级结构")
    public static void verifyResponse(Map<String, Object> object, List<String> patternList)
    {
        Object myObject = matchCondition(object,patternList);

        Assert.assertTrue(myObject != null);
    }

    public static Object matchCondition(Map<String, Object> object, List<String> patternList) {
        if (CollectionUtil.isEmpty(patternList) || MapUtils.isEmpty(object)) {
            return null;
        }
        return getValue(object, patternList, 0);
    }

    public static Object getValue(Object object, List<String> patternList, int patternIndex) {
        if (object == null) {
            return null;
        }
        // String currentPattern = patternList.size() > patternIndex ? patternList.get(patternIndex) : null;
        if (patternList.size() == patternIndex) {
            return object;
        } else {
            String currentPattern = patternList.get(patternIndex);
            if (object instanceof Map) {
                return getValue(
                        MapUtils.getObject((Map<String, Object>)object, currentPattern),
                        patternList,
                        patternIndex + 1);
            } else if (object instanceof List) {
                return ((List)object)
                        .stream()
                        .map(item -> getValue(item, patternList, patternIndex))
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(null);
            } else {
                System.out.println("currentPattern: " + currentPattern);
                return null;
                //throw new RuntimeException(String.format("can't access key :% , because object is not a map %s, %s", currentPattern, o.getClass().getSimpleName(), o));
            }
        }
    }

    public static boolean isNotEmpty(Object object) {
        return object instanceof List ? CollectionUtil.isNotEmpty((List)object) : object != null;
    }

    @Step("判断response返回的数据跟clickhouse中查询的结果是否一致")
    public static void verifyIfResponseMatchesClickhouse(Response response, Map<String,String> requestParameters)
    {
        List<String> pathList = Arrays.asList(requestParameters.get("entities").trim().split("->"));
        Map<String,Object> responseMap = response.jsonPath().getMap("data");

        Object myObject = null;
        if (!pathList.contains("edges"))
        {
            // 获取hasTimeseries_Timeseries这个层级的数据
            myObject = matchCondition(responseMap,pathList);
        }
        else {
            // 获取hasTimeseries_TimeseriesConnection->edges->node层级的所有数据
            List<Map<String,Object>> nodeList = new ArrayList<>();
            Object edgesList = matchCondition(responseMap,pathList);
            if (edgesList instanceof ArrayList){
                for (int i=0;i<((ArrayList<?>) edgesList).size();i++){
                    if (((ArrayList<?>) edgesList).get(i) instanceof HashMap)
                    {
                        nodeList.add((Map<String, Object>) ((HashMap) ((ArrayList<?>) edgesList).get(i)).get("node"));
                    }
                }
            }
            myObject = nodeList;
        }

        List<Map<String,Object>> listFromClickhouse = new ArrayList<>();

        try {
            String sql = requestParameters.get("sqlStatement");

            ResultSet rs = statement.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();

            while (rs.next()){
                Map<String,Object> listFromClickhouseItem = new HashMap<>();

                for (int i=1;i<=metaData.getColumnCount();i++){
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(columnName);
                    listFromClickhouseItem.put(columnName,value);
                }
                listFromClickhouse.add(listFromClickhouseItem);
            }

            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // 如果myObject（接口返回的response中提取）为ArrayList，判断跟clickhouse中查询的结果是否一致
        if (myObject instanceof ArrayList)
        {
            System.out.println("myObject).size(): " + ((ArrayList<?>) myObject).size());
            System.out.println("listFromClickhouse.size(): " + listFromClickhouse.size());
            Assert.assertEquals(((ArrayList<?>) myObject).size(),listFromClickhouse.size());

            for (int i=0;i<((ArrayList<?>) myObject).size();i++)
            {
                if (((List<?>) myObject).get(i) instanceof HashMap)
                {
                    Map<String,Object> myObjectItem = (Map<String, Object>) ((ArrayList<?>) myObject).get(i);
                    for (Map.Entry<String,Object> entry : myObjectItem.entrySet())
                    {
                        String rowColumnOfMyObjectKey = entry.getKey();
                        Object rowColumnOfMyObjectValue = entry.getValue();

                        Object rowColumnOfClickValue = listFromClickhouse.get(i).get(rowColumnOfMyObjectKey);

                        if (listFromClickhouse.get(i).containsKey(rowColumnOfMyObjectKey))
                        {
                            // System.out.println(rowColumnOfMyObjectKey + ", " + rowColumnOfMyObjectValue + ", " + rowColumnOfClickValue);
                            if (rowColumnOfClickValue instanceof Integer)
                            {
                                Assert.assertEquals(rowColumnOfMyObjectValue,MapUtils.getInteger(listFromClickhouse.get(i),rowColumnOfMyObjectKey));
                            }

                            else if (rowColumnOfClickValue instanceof java.lang.Float)
                            {
                                Assert.assertEquals(Float.parseFloat(rowColumnOfMyObjectValue.toString()),MapUtils.getFloat(listFromClickhouse.get(i),rowColumnOfMyObjectKey),0.001);
                            }

                            else if (rowColumnOfClickValue == null || rowColumnOfClickValue.equals(""))
                            {
                                Assert.assertEquals(rowColumnOfMyObjectValue,null);
                            }

                            else
                                Assert.assertEquals(rowColumnOfMyObjectValue,listFromClickhouse.get(i).get(rowColumnOfMyObjectKey));
                        }
                    }
                }
            }
        }
    }
}
