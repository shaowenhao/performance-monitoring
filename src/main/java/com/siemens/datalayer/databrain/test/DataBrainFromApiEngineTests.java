package com.siemens.datalayer.databrain.test;

import cn.hutool.core.collection.CollectionUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.siemens.datalayer.apiengine.test.ApiEngineEndpoint;
import com.siemens.datalayer.apiengine.test.QueryEndPointTests;
import com.siemens.datalayer.connector.test.ConnectorConfigureEndpoint;
import com.siemens.datalayer.connector.test.ConnectorEndpoint;
import com.siemens.datalayer.iot.util.JdbcMysqlUtil;
import com.siemens.datalayer.iot.util.MapTypeAdapter;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.commons.collections4.MapUtils;
import org.json.JSONException;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.*;

import java.sql.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

enum PropertyEnum{

    CO2_Sensor("System1:GmsDevice_4_7130_121634835.RAQual_Present_Value",
            "5d11b970dfdb4b4fb68c20af04fa87af","CO2_Sensor","Desigocc"),

    CO2_Sensor1("mock_room4_CO2_sensor_1",
            "b1951f0279d046568289342c624eea8f","CO2_Sensor","Desigocc"),

    CO2_Sensor2("mock_room3_CO2_sensor_1",
            "ea0de84bd76b4146be6ce1237b5bb14f","CO2_Sensor","Desigocc"),

    Humidity_Sensor("mock_room3_humidity_sensor_1",
            "9e0e90dbea284bfab43f7f05bec47352","Humidity_Sensor","Desigocc"),

    Humidity_Sensor1("System1:GmsDevice_4_7130_121634835.RHuRel_Present_Value",
            "9e4ab0cc4fb24f40ad8b80facdeb017b","Humidity_Sensor","Desigocc"),

    Humidity_Sensor2("mock_room4_humidity_sensor_1",
            "d46f466bda9d40c5914bf74862343b63","Humidity_Sensor","Desigocc"),

    Luminance_Setpoint("System1:GmsDevice_4_7130_1090519068.Present_Value",
            "73b97708f1404419a1e10b31baba725a","Luminance_Setpoint","Desigocc"),

    Mode_Command("System1:GmsDevice_1_7105_29360128.Present_Value",
            "17ecd6103fbd43769d5fef0d09e776bd","Mode_Command","Desigocc"),

    Mode_Command1("System1:GmsDevice_4_7130_79692331.Present_Value",
            "262aaa32de6448a78ffaa58a6775b301","Mode_Command","Desigocc"),

    Mode_Command2("System1:GmsDevice_4_7130_29360128.Present_Value",
            "67ac53b928a947fc9afcb29f30cce93f","Mode_Command","Desigocc"),

    Occupancy_Sensor("mock_Floor1_occupancy_status_3",
            "080b2b6a79c84cd49017dbf18cb0b1f8","Occupancy_Sensor","Desigocc"),

    Occupancy_Sensor1("mock_room6_occupancy_sensor_1",
            "173fffbef6cb4a608abbb002a41b694f","Occupancy_Sensor","Desigocc"),

    Occupancy_Sensor2("mock_room5_occupancy_sensor_1",
            "c4d4ba6710b3429d8865728890687cd4","Occupancy_Sensor","Desigocc"),

    Occupancy_Status("mock_room4_occupancy_status_1",
            "512cd053901742a3a41889f54a126dd6","Occupancy_Status","Desigocc"),

    Occupancy_Status1("mock_Floor1_occupancy_sensor_3",
            "b21d534e067143cd978b997463acb606","Occupancy_Status","Desigocc"),

    Occupancy_Status2("mock_room3_occupancy_status_1",
            "e4cb716fa31d4138905ad8bdb30e8485","Occupancy_Status","Desigocc"),

    Position_Command("System1:GmsDevice_4_7130_1082130448.Present_Value.Height",
            "0db848bd91874e8aac289a03b5e18c6b","Position_Command","Desigocc"),

    Power_Sensor("mock_room3_power_sensor_1",
            "19a54042c65a40a58fd5761908a8e735","Power_Sensor","Desigocc"),

    Power_Sensor1("mock_room4_power_sensor_1",
            "dcfb6e452da64f7aa23e07077faf725e","Power_Sensor","Desigocc"),

    Speed_Setpoint("System1:GmsDevice_4_7130_121634914.FanMultiSpd_Present_Value",
            "e248aeb1cdb74be2b3f0b9957ea3d6f5","Speed_Setpoint","Desigocc"),

    Temperature_Sensor("mock_room3_temperature_sensor_1",
            "7a4110475690416aad33cb845cccf6f0","Temperature_Sensor","Desigocc"),

    Temperature_Sensor1("mock_room4_temperature_sensor_1",
            "eae7e8266e0242ffb9b8a6f9bb1e0927","Temperature_Sensor","Desigocc"),

    Temperature_Sensor2("System1:GmsDevice_4_7130_121634835.RTemp_Present_Value",
            "ec2510822ef64bebac25f028bd3e147c","Temperature_Sensor","Desigocc"),

    Temperature_Setpoint("System1:GmsDevice_4_7130_121634849.SpTR_Present_Value",
            "c873ae9f77a54bf2af2b91d924038cfd","Temperature_Setpoint","Desigocc"),

    TVOC_Sensor("mock_room4_TVOC_sensor_1",
            "04967aeef6bd4600bad7019be2b74a4e","TVOC_Sensor","Desigocc"),

    TVOC_Sensor1("mock_room3_TVOC_sensor_1",
            "b102b5f3b1294011ac212d641eff7eb9","TVOC_Sensor","Desigocc"),

    Luminance_Sensor("2",
            "e5f0eb45ff8f47dd800214e56a2b6eef","Luminance_Sensor","Enlighted"),

    Occupancy_Status3("1",
            "6bcccd43f94246098510735605bd0cf7","Occupancy_Status","Enlighted"),

    Power_Sensor2("2",
            "c39df126e6b54469adf4b4a00b703ef4","Power_Sensor","Enlighted"),

    Temperature_Sensor3("2",
            "b20b01a19aa8455f8d9275d32a1450dd","Temperature_Sensor","Enlighted"),

    CO2_Sensor3("24e124710b514794",
            "625232f47b3b4369a595f708886bc54d","CO2_Sensor","iotgateway"),

    CO2_Sensor4("24e124128a486491",
            "abd4823004544adab6b2a8ed92ca6b33","CO2_Sensor","iotgateway"),

    Humidity_Sensor3("24e124710b514794",
            "1efe90e970a640b6b1d840c3ccf02cdc","Humidity_Sensor","iotgateway"),

    Humidity_Sensor4("24e124128a486491",
            "63fc159fb0b3419ca1bd14d86eaf43e9","Humidity_Sensor","iotgateway"),

    Luminance_Sensor1("24e124128a486491",
            "394edf421c4a43da941b9002bc942f60","Luminance_Sensor","iotgateway"),

    Luminance_Sensor2("24e124710b514794",
            "b8a6aa3cde244e3a9a25d7af1bdde406","Luminance_Sensor","iotgateway"),

    Occupancy_Sensor3("24e124600b251195",
            "ad776315e61d42a58c30e6f9ebd19d52","Occupancy_Sensor","iotgateway"),

    Occupancy_Status4("24e124538b441301",
            "9bcccd24f94246098510735605bd9db6","Occupancy_Status","iotgateway"),

    Pressure_Sensor("24e124128a486491",
            "2d182a01a6bb4bef920fc08debac2f69","Pressure_Sensor","iotgateway"),

    Pressure_Sensor1("24e124710b514794",
            "7619cfef340c49f08e475fb1bd0d534f","Pressure_Sensor","iotgateway"),

    Temperature_Sensor4("24e124128a486491",
            "5540e6f7753941908379ddc8ecb43ffd","Temperature_Sensor","iotgateway"),

    Temperature_Sensor5("24e124710b514794",
            "6a00e9a5db4a465091eb0062ad2963c9","Temperature_Sensor","iotgateway"),

    TVOC_Sensor2("24e124710b514794",
            "7d08b8c7001340de8843257ebc3bcba2","TVOC_Sensor","iotgateway"),

    TVOC_Sensor3("24e124128a486491",
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
            description = "Verify smart space other query function(except realtime function/property function)",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class
    )
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'getData' request to graphql query interface.")
    @Story("Verify smart space other query function(except realtime function/property function)")
    public void verifySmartSpaceWenSiQueryFunc(Map<String, String> paramMaps) throws JSONException
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
            System.out.println("query:");
            System.out.println(query);
            Response response = ApiEngineEndpoint.postGraphql(query);
            System.out.println("response.jsonPath().prettify():");
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
                verifyResponssIncludingAllHierarchical(responseMap,pathList);
            }
        }
    }

    @Test(	priority = 0,
            description = "Verify smart space property function",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class
            )
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'getData' request to graphql query interface.")
    @Story("Verify smart space property function")
    public void verifySmartSpacePropertyFunc(Map<String, String> paramMaps) throws JSONException
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
            System.out.println("query:");
            System.out.println(query);
            Response response = ApiEngineEndpoint.postGraphql(query);
            System.out.println("response.jsonPath().prettify():");
            System.out.println(response.jsonPath().prettify());

            // 校验返回的response的最外层的statusCode，code，message
            QueryEndPointTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"),
                    response.jsonPath().getString("message"));

            // 如果是query语句，判断response中是否返回所有的层级结构
            /* if (paramMaps.containsKey("entities") && !paramMaps.containsKey("rspCodeOfDatasource")
                    && !paramMaps.containsKey("rspMessageOfDatasource"))
            {
                List<String> pathList = Arrays.asList(paramMaps.get("entities").trim().split("->"));
                Map<String,Object> responseMap = response.jsonPath().getMap("data");

                System.out.println(pathList);
                verifyResponssIncludingAllHierarchical(responseMap,pathList);
            } */

            checkResponseData(paramMaps,response);
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
            String startTime = String.valueOf(System.currentTimeMillis()-3600000);
            String endTime = String.valueOf(System.currentTimeMillis());

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

            // 判断response返回的数据跟clickhouse中查询的结果是否一致
            if (paramMaps.containsKey("sqlStatement"))
            {
                verifyIfResponseMatchesClickhouse(response,paramMaps,startTime,endTime);
            }

            List<String> pathList = Arrays.asList(paramMaps.get("entities").trim().split("->"));
            Map<String,Object> responseMap = response.jsonPath().getMap("data");

            // 判断response中是否返回所有的层级结构
            // System.out.println(pathList);
            // verifyResponssIncludingAllHierarchical(responseMap,pathList);
        }
    }

    @Test(	priority = 0,
            description = "Verify smart space mutation function",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class
    )
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'getData' request to graphql query interface.")
    @Story("Verify smart space mutation function")
    public void verifySmartSpaceMutationFunc(Map<String, String> paramMaps)
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

            // 如果是mutation，校验返回的第二层的code/data
            if (paramMaps.containsKey("entities") && paramMaps.containsKey("rspCodeOfDatasource") && paramMaps.containsKey("rspMessageOfDatasource"))
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

    @Step("校验接口返回的rspData")
    public static void checkResponseData(Map<String,String> requestParameters, Response response) throws JSONException {
        if (requestParameters.containsKey("rspData"))
        {
            // 转换成Map
            Map<String,Object> actualResponseData = response.jsonPath().getMap("data");

            // 转换成Map，重写TypeAdapter方法为MapTypeAdapter，解决String转换成Map<String,Object>时，会将整数型数据自动添加小数点的问题
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(new TypeToken<Map<String, Object>>() {
                    }.getType(), new MapTypeAdapter()).create();

            System.out.println("requestParameters.get(\"rspData\"): " + "\n" + requestParameters.get("rspData"));
            Map<String, Object> expectedResponseData = gson.fromJson(requestParameters.get("rspData"), new TypeToken<Map<String, Object>>() {}.getType());

            System.out.println("actualResponseData.size(): " + actualResponseData.size());
            System.out.println("expectedResponseData.size(): " + expectedResponseData.size());
            Assert.assertEquals(actualResponseData.size(),expectedResponseData.size());
            Assert.assertEquals(actualResponseData,expectedResponseData);
        }
    }

    @Step("判断response中是否返回所有的层级结构")
    public static void verifyResponssIncludingAllHierarchical(Map<String, Object> object, List<String> patternList)
    {
        Object myObject = matchCondition(object,patternList);
        System.out.println(myObject);

        Assert.assertTrue(myObject != null);
        if (myObject instanceof ArrayList)
        {
            Assert.assertFalse(((ArrayList<?>) myObject).isEmpty());
        }
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
            System.out.println("myObject.size(): " + ((List<?>) myObject).size());
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