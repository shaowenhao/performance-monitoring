package com.siemens.datalayer.databrain.test;

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
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

enum PropertyEnum{

    CO2_Sensor("System1:GmsDevice_4_7130_121634835.RAQual_Present_Value",
            "5d11b970dfdb4b4fb68c20af04fa87af","CO2_Sensor","Desigocc"),

    GreenLeaf_Mode_Command("System1:GmsDevice_4_7130_16777322.Present_Value",
            "095a804d31584d4bb7762ade3d1b3047","GreenLeaf_Mode_Command","Desigocc"),

    Humidity_Sensor("System1:GmsDevice_4_7130_121634835.RHuRel_Present_Value",
            "9e4ab0cc4fb24f40ad8b80facdeb017b","Humidity_Sensor","Desigocc"),

    Luminance_Setpoint("System1:GmsDevice_4_7130_1090519068.Present_Value",
            "73b97708f1404419a1e10b31baba725a","Luminance_Setpoint","Desigocc"),

    Mode_Command("System1:GmsDevice_4_7130_121634870.PltOpMod_Relinquish_Default",
            "67ac53b928a947fc9afcb29f30cce93f","Mode_Command","Desigocc"),

    Mode_Command1("System1:GmsDevice_1_7105_29360128.Present_Value",
            "22a30930ce1d4007a59e24bef925d17d","Mode_Command","Desigocc"),

    Mode_Command2("System1:GmsDevice_4_7130_79692330.Present_Value",
            "858e767d499343bb9198ff4603db79fb","Mode_Command","Desigocc"),

    Position_Command("System1:GmsDevice_4_7130_1082130448.Present_Value.Height",
            "0db848bd91874e8aac289a03b5e18c6b","Position_Command","Desigocc"),

    Speed_Setpoint("System1:GmsDevice_4_7130_121634914.FanMultiSpd_Present_Value",
            "e248aeb1cdb74be2b3f0b9957ea3d6f5","Speed_Setpoint","Desigocc"),

    Temperature_Sensor("System1:GmsDevice_4_7130_121634835.RTemp_Present_Value",
            "ec2510822ef64bebac25f028bd3e147c","Temperature_Sensor","Desigocc"),

    Temperature_Setpoint("System1:GmsDevice_4_7130_121634849.SpTR_Present_Value",
            "c873ae9f77a54bf2af2b91d924038cfd","Temperature_Setpoint","Desigocc"),

    Luminance_Sensor("2",
            "e5f0eb45ff8f47dd800214e56a2b6eef","Luminance_Sensor","Enlighted"),

    Occupancy_Status("1",
            "6bcccd43f94246098510735605bd0cf7","Occupancy_Status","Enlighted"),

    Power_Sensor("2",
            "c39df126e6b54469adf4b4a00b703ef4","Power_Sensor","Enlighted"),

    Temperature_Sensor1("2",
            "b20b01a19aa8455f8d9275d32a1450dd","Temperature_Sensor","Enlighted"),

    CO2_Sensor1("24e124128a486491",
            "abd4823004544adab6b2a8ed92ca6b33","CO2_Sensor","iotgateway"),

    Humidity_Sensor1("24e124128a486491",
            "63fc159fb0b3419ca1bd14d86eaf43e9","Humidity_Sensor","iotgateway"),

    Luminance_Sensor1("24e124128a486491",
            "394edf421c4a43da941b9002bc942f60","Luminance_Sensor","iotgateway"),

    Occupancy_Sensor("24e124600b251195",
            "ad776315e61d42a58c30e6f9ebd19d52","Occupancy_Sensor","iotgateway"),

    Occupancy_Status1("24e124538b441301",
            "9bcccd24f94246098510735605bd9db6","Occupancy_Status","iotgateway"),

    Pressure_Sensor("24e124128a486491",
            "2d182a01a6bb4bef920fc08debac2f69","Pressure_Sensor","iotgateway"),

    Temperature_Sensor2("24e124128a486491",
            "5540e6f7753941908379ddc8ecb43ffd","Temperature_Sensor","iotgateway"),

    TVOC_Sensor("24e124128a486491",
            "87cebf00987844849ad130af5fe39971","TVOC_Sensor","iotgateway"),
    ;

    String sid;
    String id;
    String type;
    String sourcesystem;

    PropertyEnum(String sid, String id,String type,String sourcesystem) {
        this.sid = sid;
        this.id = id;
        this.type = type;
        this.sourcesystem = sourcesystem;
    }

    public String getSid(){
        return sid;
    }

    public String getId(){
        return id;
    }

    public String getType() {return type;}

    public String getSourcesystem(){
        return sourcesystem;
    }

    public static PropertyEnum getBySid(String sid) {
        return Arrays.stream(PropertyEnum.values()).filter(item -> Objects.equals(sid, item.getSid())).findFirst().orElse(null);
    }

    public static PropertyEnum getById(String id){
        return Arrays.stream(PropertyEnum.values()).filter(item -> Objects.equals(id,item.getId())).findFirst().orElse(null);
    }
}

@Epic("SDL Api-engine")
@Feature("Verify data brain function")
public class DataBrainFromApiEngineTests {
    static Connection connection;
    static Statement statement;

    @Parameters({"base_url","port","baseUrlOfConnector","portOfConnector","baseUrlOfConnectorConfigure","portOfConnectorConfigure"})
    @BeforeClass(description = "Configure the host address,communication port and database properties file of data-layer-api-engine;")
    public void setApiEngineEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("32510") String port,
                                     @Optional("http://140.231.89.85")  String baseUrlOfConnector,@Optional("32577") String portOfConnector,
                                     @Optional("http://140.231.89.85")  String baseUrlOfConnectorConfigure,@Optional("30674") String portOfConnectorConfigure)
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
            connection = JdbcMysqlUtil.getConnection("iems.dev.clickhouse.db.properties");
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
            description = "Verify smart space function(except realtime function)",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class
            )
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'getData' request to graphql query interface.")
    @Story("Verify smart space function(except realtime function)")
    public void verifySmartSpaceOtherFunc(Map<String, String> paramMaps)
    {
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

                //
            }
            // 如果是mutation，校验返回的第二层的code/data
            else if (paramMaps.containsKey("entities") && paramMaps.containsKey("rspCodeOfDatasource") && paramMaps.containsKey("rspMessageOfDatasource"))
            {
                String pathOfRspCodeOfDatasource = null;
                String pathOfRspMessageOfDatasource = null;
                if (paramMaps.get("query").contains("Update"))
                {
                    pathOfRspCodeOfDatasource = "data." + paramMaps.get("entities") + "_Update.json_value[0].code";
                    pathOfRspMessageOfDatasource = "data." + paramMaps.get("entities") + "_Update.json_value[0].data";
                }
                String actualRspCodeOfDatasource = response.jsonPath().getString(pathOfRspCodeOfDatasource);
                String actualRspMessageOfDatasource = response.jsonPath().getString(pathOfRspMessageOfDatasource);

                Assert.assertEquals(actualRspCodeOfDatasource,paramMaps.get("rspCodeOfDatasource"));
                Assert.assertTrue(actualRspMessageOfDatasource.contains(paramMaps.get("rspMessageOfDatasource")));
            }
        }
    }

    @Test(	priority = 0,
            description = "Verify smart space realtime function(including 23 points)",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class
    )
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'getData' request to graphql query interface.")
    @Story("Verify smart space realtime function(including 23 points)")
    public void verifySmartSpaceRealtimeFunc(Map<String, String> paramMaps)
    {
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
            // 动态生成graphql语句，包括替换开始时间（如当前时间前5个小时）、结束时间（当前时间）

            String endTime = String.valueOf(System.currentTimeMillis());
            String startTime = String.valueOf(System.currentTimeMillis()-3600000);

                    String query = paramMaps.get("query");
            if (query.contains("$startTime"))
            {
                query = query.replace("$startTime", startTime);
            }
            if (query.contains("endTime"))
            {
                query = query.replace("$endTime",endTime);
            }
            System.out.println(query);

            Response response = ApiEngineEndpoint.postGraphql(query);
            System.out.println(response.jsonPath().prettify());

            // 校验返回的response的最外层的statusCode，code，message
            QueryEndPointTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"),
                    response.jsonPath().getString("message"));

            if (paramMaps.containsKey("sqlStatement"))
            {
                verifyIfResponseMatchesClickhouse(response,paramMaps,startTime,endTime);
            }

            List<String> pathList = Arrays.asList(paramMaps.get("entities").trim().split("->"));
            Map<String,Object> responseMap = response.jsonPath().getMap("data");

            System.out.println(pathList);
            verifyResponse(responseMap,pathList);
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
    public static void verifyIfResponseMatchesClickhouse(Response response,Map<String,String> requestParameters,String startTime,String endTime)
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
            // 动态生成sql语句，包括替换开始时间（当前时间前5个小时）、结束时间（当前时间）
            DateTimeFormatter dfWithT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

            String sql = requestParameters.get("sqlStatement");
            if (sql.contains("$startTime"))
            {
                sql = sql.replace("$startTime",String.valueOf(Long.parseLong(startTime)/1000));
            }
            if (sql.contains("endTime"))
            {
                sql = sql.replace("$endTime",String.valueOf(Long.parseLong(endTime)/1000));
            }
            System.out.println(sql);

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
            System.out.println("myObject).size(): " + ((List<?>) myObject).size());
            System.out.println("listFromClickhouse.size(): " + listFromClickhouse.size());
            Assert.assertEquals(((List<?>) myObject).size(),listFromClickhouse.size());

            for (int i=0;i<((List<?>) myObject).size();i++)
            {
                if (((List<?>) myObject).get(i) instanceof HashMap)
                {
                    /**
                     * 校验接口返回的captureTime字段
                     * */
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss.S"); // 格式一
                    DateTimeFormatter dfWithT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"); // 格式二

                    if (listFromClickhouse.get(i).get("dataLayerTime") instanceof Timestamp)
                    {
                        // 将string格式转换成localdatetime格式，原来的格式为“格式一”
                        LocalDateTime ld = LocalDateTime.parse((CharSequence) listFromClickhouse.get(i).get("dataLayerTime").toString(),df);

                        // 将localdatetime格式转换成string格式，使用“格式二”
                        String dataLayerTimeWithT = dfWithT.format(ld);

                        OffsetDateTime ts = ((Timestamp)listFromClickhouse.get(i).get("dataLayerTime")).toInstant().atOffset(ZoneOffset.ofHours(8));
                        String clickHouseDateTimeString = ts.format(dfWithT);
                        clickHouseDateTimeString = clickHouseDateTimeString.replace("\\+08:00", "");

                        System.out.println(((Map) ((List<?>) myObject).get(i)).get("captureTime") + ", " + clickHouseDateTimeString);
                        Assert.assertEquals(((Map) ((List<?>) myObject).get(i)).get("captureTime"),clickHouseDateTimeString);
                    }

                    /**
                     * 校验接口返回的id字段
                     * */
                    /* Assert.assertEquals(((Map) ((List<?>) myObject).get(i)).get("id"),
                            PropertyEnum.getBySid(requestParameters.get("PropertyId")).getId()); */

                    /**
                     * 校验接口返回的value字段
                     * */
                    if (!(listFromClickhouse.get(i).get("Value")).equals(""))
                    {
                        // System.out.println(((Map) ((List<?>) myObject).get(i)).get("value").getClass() + "," + listFromClickhouse.get(i).get("Value").getClass());
                        Assert.assertEquals(((Map) ((List<?>) myObject).get(i)).get("value").toString(),listFromClickhouse.get(i).get("Value").toString());
                    }
                    else
                        Assert.assertTrue(((Map) ((List<?>) myObject).get(i)).get("value") == null);

                    /**
                     * 校验接口返回的type字段
                     * */
                    Assert.assertEquals(((Map) ((List<?>) myObject).get(i)).get("type"),
                            PropertyEnum.getById(requestParameters.get("id")).getType());
                }
            }
        }
        // 如果myObject（接口返回的response中提取）为null，判断clickhouse中查询的结果是否也为null
        else
        {
            Assert.assertTrue(myObject == null);
            Assert.assertTrue(listFromClickhouse.isEmpty());
        }
    }
}