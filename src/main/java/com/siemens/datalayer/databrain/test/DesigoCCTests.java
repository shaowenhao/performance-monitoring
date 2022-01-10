package com.siemens.datalayer.databrain.test;

import com.siemens.datalayer.connector.test.ConnectorEndpoint;
import com.siemens.datalayer.connector.test.InterfaceTests;
import com.siemens.datalayer.databrain.util.MyWebSocketClient;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Epic("SDL Connector")
@Feature("read desigoCC history/realtime data")
public class DesigoCCTests {
    private static final String desigoHttpsBaseUrl = "https://md3ktpmc.ad001.siemens.net";
    private static final String desigoWssBaseUrl = "wss://md3ktpmc.ad001.siemens.net";

    private static final String desigoPort = "61022";
    private static final String desigoGranttype = "password";
    private static final String desigoUsername = "defaultadmin";
    private static final String desigoPassword = "cc";

    private static final String desigoClientProtocol = "1.4";
    private static final String desigoConnectionData = "[{\"Name\":\"norisHub\"}]";

    private static final String desigoTransport = "webSockets";

    private static final String desigoRequestId = "9565ca41-8556-4dbb-94cc-1b89451a5db5";
    // private static final String desigoPostData = "[\"System1:GmsDevice_1_7210_0.Present_Value\",\"System1:GmsDevice_1_7210_6.Present_Value\"]";

    @Parameters({"base_url", "port"})
    @BeforeClass(description = "Configure the host address and communication port of data-layer-connector")
    public void setConnectorEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("30417") String port)
    {
        ConnectorEndpoint.setBaseUrl(base_url);
        ConnectorEndpoint.setPort(port);
    }

    @Test(	priority = 0,
            description = "read desigoCC history data",
            dataProvider = "connector-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'getConceptModelDataByCondition' request with specified parameters and check the response message.")
    @Story("read desigoCC history data")
    public void desigoCCHistoryData(Map<String, String> paramMaps)
    {
        // 检查desigoCC历史数据数据源
        checkDesigoCCHistoryDatasource(paramMaps);

        HashMap<String, String> queryParameters = new HashMap<>();
        if (paramMaps.containsKey("OriginalObjectOrPropertyId"))
            queryParameters.put("condition","id='" + paramMaps.get("OriginalObjectOrPropertyId") +"'");
        if (paramMaps.containsKey("name"))
            queryParameters.put("name",paramMaps.get("name"));

        Response response = ConnectorEndpoint.getConceptModelDataByCondition(queryParameters);
        System.out.println(response.jsonPath().getString(""));

        InterfaceTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));
    }

    @Test(	priority = 0,
            description = "read desigoCC realtime data",
            dataProvider = "connector-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'getConceptModelDataByCondition' request with specified parameters and check the response message.")
    @Story("read desigoCC realtime data")
    public void desigoCCRealtimeData(Map<String, String> paramMaps)
            throws IOException, NoSuchAlgorithmException, ExecutionException, InterruptedException, KeyManagementException
    {
        // 检查desigoCC实时数据数据源，程序运行requestParameters.get("runningTime")秒，收集实时数据
        checkDesigoCCRealtimeDatasource(paramMaps);
    }

    @Step("check desigoCC history datasource")
    public static void checkDesigoCCHistoryDatasource(Map<String, String> requestParameters)
    {
        Response responseOfGetToken = DesigoCCEndpoint.getToken(desigoHttpsBaseUrl,desigoPort,desigoGranttype,desigoUsername,desigoPassword);

        // check interface:/api/token
        Assert.assertEquals(responseOfGetToken.getStatusCode(),200);

        String accessToken = responseOfGetToken.jsonPath().getString("access_token");
        Response responseOfGetValue = DesigoCCEndpoint.getValue(desigoHttpsBaseUrl,desigoPort,accessToken,requestParameters.get("OriginalObjectOrPropertyId"));

        // check interface:/api/values/System1:GmsDevice_1_7210_4194307.Present_Value
        Assert.assertEquals(responseOfGetValue.getStatusCode(),200);
        Assert.assertEquals(responseOfGetValue.jsonPath().getString("[0].ErrorCode"),"0");
    }

    @Step("check desigoCC realtime datasource")
    public static void checkDesigoCCRealtimeDatasource(Map<String, String> requestParameters)
            throws IOException, NoSuchAlgorithmException, ExecutionException, InterruptedException, KeyManagementException
    {

        /**
         * 实时数据订阅步骤：
         * step1：调用/api/token接口获取accessToken
         * step2：利用step1获取到的accessToken，调用/signalr/negotiate接口，获取connectionToken、connectionId
         * step3：利用step2获取到的connectionToken、connectionId，调用/signalr/start接口，启动signalr
         * step4：利用step1获取到的accessToken、step2获取到的、connectionId，调用/api/sr/valuessubscriptions/channelize接口，创建订阅channel
         * step5：利用step1获取到的accessToken、step2获取到的、connectionToken，调用/signalr/connect接口，订阅实时数据
         */
        Response responseOfGetToken = DesigoCCEndpoint.getToken(desigoHttpsBaseUrl,desigoPort,desigoGranttype,desigoUsername,desigoPassword);

        // check interface:/api/token
        Assert.assertEquals(responseOfGetToken.getStatusCode(),200);

        String accessToken = responseOfGetToken.jsonPath().getString("access_token");
        Response responseOfSignalrNegotiate = DesigoCCEndpoint.signalrNegotiate(desigoHttpsBaseUrl,desigoPort,accessToken,desigoClientProtocol,desigoConnectionData);

        // check interface:/signalr/negotiate
        Assert.assertEquals(responseOfSignalrNegotiate.getStatusCode(),200);

        String connectionToken = responseOfSignalrNegotiate.jsonPath().getString("ConnectionToken");
        String connectionId = responseOfSignalrNegotiate.jsonPath().getString("ConnectionId");
        org.asynchttpclient.Response responseOfSignalrStart = MyWebSocketClient.signalrStart(desigoHttpsBaseUrl,desigoPort,
                                                                               accessToken,desigoClientProtocol,
                                                                               desigoTransport,desigoConnectionData,
                                                                               connectionId,connectionToken);

        // check interface:/signalr/start
        Assert.assertEquals(responseOfSignalrStart.getStatusCode(),200);

        Response responseOfValuessubscriptions = DesigoCCEndpoint.eventssubscriptions(desigoHttpsBaseUrl,desigoPort,accessToken,desigoRequestId,connectionId);

        // check interface:/api/sr/eventssubscriptions/channelize
        Assert.assertEquals(responseOfValuessubscriptions.getStatusCode(),200);

        int runningTime = Integer.valueOf(requestParameters.get("runningTime")).intValue();
        long start = System.currentTimeMillis();
        long end = start + (runningTime)*1000;

        while (true)
        {
            try {
                MyWebSocketClient.checkMessageFromWebSocket(desigoWssBaseUrl,desigoPort,
                        desigoClientProtocol,desigoTransport,
                        desigoConnectionData,connectionToken);
            }catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
            if (System.currentTimeMillis() >= end)
            {
                break;
            }
        }
    }
}
