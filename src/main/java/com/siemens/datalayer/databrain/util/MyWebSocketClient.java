package com.siemens.datalayer.databrain.util;

import cn.hutool.core.net.DefaultTrustManager;
import com.google.gson.Gson;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.JdkSslContext;
import io.qameta.allure.Step;
import org.asynchttpclient.*;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MyWebSocketClient {
    /**
     * expectedPayloadList存放一段时间（runningTime）内，websocket订阅到的非空数据;
     * 程序运行期间，expectedPayloadList中的数据会增加，取最后的数据即可
     */
    static List<Map<String,Object>> expectedPayloadList = new ArrayList<>();

    private static SSLContext unsecuredSslContext()
            throws KeyManagementException, NoSuchAlgorithmException
    {
        // Install the all-trusting trust manager
        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{new DefaultTrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }}, new java.security.SecureRandom());
        return sslContext;
    }

    protected static AsyncHttpClient createClient(AsyncHttpClientConfig config)
    {
        AsyncHttpClient client;
        if (config == null) {
            config = new DefaultAsyncHttpClientConfig.Builder().build();
            client = new DefaultAsyncHttpClient(config);
        } else {
            client = new DefaultAsyncHttpClient(config);
        }
        return client;
    }

    // desigoCC datasource Endpoint: /signalr/start
    @Step("Send a request of '/signalr/start'")
    public static Response signalrStart(String httpsBaseUrl, String port,
                                        String accessToken, String clientProtocol,
                                        String transport, String connectionData,
                                        String connectionId, String connectionToken)
            throws NoSuchAlgorithmException, KeyManagementException, ExecutionException, InterruptedException
    {

        DefaultAsyncHttpClientConfig.Builder builder = new DefaultAsyncHttpClientConfig.Builder();
        builder.setCookieStore(null);

        builder.setDisableHttpsEndpointIdentificationAlgorithm(true);

        SSLContext sslContext;
        sslContext = unsecuredSslContext();

        JdkSslContext ssl = new JdkSslContext(sslContext, true, ClientAuth.REQUIRE);
        builder.setSslContext(ssl);

        AsyncHttpClientConfig config = builder.setWebSocketMaxFrameSize(1024 * 1024).build();
        AsyncHttpClient client = createClient(config);

        String url = httpsBaseUrl + ":" + port + "/signalr/start" + "?"
                + "clientProtocol=" + clientProtocol + "&"
                + "transport=" + transport + "&"
                + "connectionData=" + connectionData + "&"
                + "ConnectionId=" + connectionId + "&"
                + "connectionToken=" + URLEncoder.encode(connectionToken);

        Future<Response> whenResponse = client.preparePost(url).execute();

        // Request request = post(url).build();
        // ListenableFuture<Response> whenResponse = client.executeRequest(request);

        Response response = whenResponse.get();

        return response;
    }

    public static List<Map<String,Object>> getMessageFromWebSocket(String wssBaseUrl, String port,
                                               String clientProtocol, String transport,
                                               String connectionData, String connectionToken)
            throws NoSuchAlgorithmException, KeyManagementException, ExecutionException, InterruptedException, UnsupportedEncodingException
    {
        DefaultAsyncHttpClientConfig.Builder builder = new DefaultAsyncHttpClientConfig.Builder();
//following codes is borrowed from super class
        /*
         * Not doing this will always create a cookie handler per endpoint, which is incompatible
         * to prior versions and interferes with the cookie handling in camel
         */
        builder.setCookieStore(null);
//disable host check
        builder.setDisableHttpsEndpointIdentificationAlgorithm(true);

        SSLContext sslContext;
        sslContext = unsecuredSslContext();

        JdkSslContext ssl = new JdkSslContext(sslContext, true, ClientAuth.REQUIRE);
        builder.setSslContext(ssl);

        AsyncHttpClientConfig config = builder.setWebSocketMaxFrameSize(1024 * 1024).build();
        AsyncHttpClient client = createClient(config);

        String url = wssBaseUrl + ":" + port + "/signalr/connect" + "?"
                                + "clientProtocol=" + clientProtocol + "&"
                                + "transport=" + transport + "&"
                                + "connectionData=" + URLEncoder.encode(connectionData,"UTF-8") + "&"
                                + "connectionToken=" + URLEncoder.encode(connectionToken,"UTF-8");

        WebSocket webSocket = client.prepareGet(url)
                .execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(
                        new WebSocketListener() {
                            @Override
                            public void onOpen(WebSocket webSocket) {
                                webSocket.sendTextFrame("...");
                            }

                            @Override
                            public void onClose(WebSocket webSocket, int i, String s) {

                            }

                            @Override
                            public void onError(Throwable throwable) {

                            }

                            @Override
                            public void onTextFrame(String payload, boolean finalFragment, int rsv) {
                                WebSocketListener.super.onTextFrame(payload, finalFragment, rsv);

                                // 以下code，重写onTextFrame方法
                                Gson gson = new Gson();
                                Map<String,Object> payloadMap = new HashMap<>();
                                payloadMap = gson.fromJson(payload,payloadMap.getClass());

                                /**
                                 * 以下代码根据MongoDB里的配置，只获取mapper里有的字段
                                 * */

                                if (payloadMap.containsKey("M"))
                                    if (payloadMap.get("M") instanceof List)
                                        if (!((List<?>) payloadMap.get("M")).isEmpty())
                                            if (((List<?>) payloadMap.get("M")).get(0) instanceof Map)
                                                if (((Map) ((List<?>) payloadMap.get("M")).get(0)).get("A") instanceof List)
                                                    if (((List<?>) ((Map) ((List<?>) payloadMap.get("M")).get(0)).get("A")).get(0) instanceof List)
                                                        if (((List) ((List<?>) ((Map) ((List<?>) payloadMap.get("M")).get(0)).get("A")).get(0)).get(0) instanceof Map)
                                                        {
                                                            Map<String,Object> expectedPayloadItem = new HashMap<>();
                                                            expectedPayloadItem.put("id",((Map<?, ?>) ((List) ((List<?>) ((Map) ((List<?>)
                                                                    payloadMap.get("M")).get(0)).get("A")).get(0)).get(0)).get("Id").toString());

                                                            expectedPayloadItem.put("EventId",((Map<?, ?>) ((List) ((List<?>) ((Map) ((List<?>)
                                                                    payloadMap.get("M")).get(0)).get("A")).get(0)).get(0)).get("EventId").toString());

                                                            expectedPayloadItem.put("Deleted",((Map<?, ?>) ((List) ((List<?>) ((Map) ((List<?>)
                                                                    payloadMap.get("M")).get(0)).get("A")).get(0)).get(0)).get("Deleted").toString());

                                                            expectedPayloadItem.put("CategoryId",((Map<?, ?>) ((List) ((List<?>) ((Map) ((List<?>)
                                                                    payloadMap.get("M")).get(0)).get("A")).get(0)).get(0)).get("CategoryId").toString());

                                                            expectedPayloadItem.put("State",((Map<?, ?>) ((List) ((List<?>) ((Map) ((List<?>)
                                                                    payloadMap.get("M")).get(0)).get("A")).get(0)).get(0)).get("State").toString());

                                                            expectedPayloadItem.put("Cause",((Map<?, ?>) ((List) ((List<?>) ((Map) ((List<?>)
                                                                    payloadMap.get("M")).get(0)).get("A")).get(0)).get(0)).get("Cause").toString());

                                                            expectedPayloadItem.put("CreationTime",((Map<?, ?>) ((List) ((List<?>) ((Map) ((List<?>)
                                                                    payloadMap.get("M")).get(0)).get("A")).get(0)).get(0)).get("CreationTime").toString());

                                                            expectedPayloadList.add(expectedPayloadItem);
                                                        }
                            }
                        }
                ).build()).get();
        // System.out.println(expectedPayloadList.size());
        return expectedPayloadList;
    }
}