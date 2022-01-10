package com.siemens.datalayer.databrain.util;

import cn.hutool.core.net.DefaultTrustManager;
import com.alibaba.fastjson.JSONObject;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MyWebSocketClient {

    private static final String desigoHttpsBaseUrl = "https://md3ktpmc.ad001.siemens.net";
    private static final String desigoWssBaseUrl = "wss://md3ktpmc.ad001.siemens.net";

    private static final String desigoPort = "61022";
    private static final String desigoGranttype = "password";
    private static final String desigoUsername = "defaultadmin";
    private static final String desigoPassword = "cc";

    private static final String desigoClientProtocol = "1.4";
    private static final String desigoConnectionData = "[{\"Name\":\"norisHub\"}]";

    private static final String desigoTransport = "webSockets";

    private static final String desigoRequestId = "5f8a3542-b406-41cb-83c2-7f667fbabf52";
    private static final String desigoPostData = "[\"System1:GmsDevice_1_7210_0.Present_Value\",\"System1:GmsDevice_1_7210_6.Present_Value\"]";

    private static final String connectionToken = "AQAAANCMnd8BFdERjHoAwE/Cl+sBAAAATXKgfEYKGkOPiymLX+wD+wAAAAACAAAAAAAQZgAAAAEAACAAAAD7v9qOIdDdB95lLCpCIrDDBFb82hvPELWIIRfcIonznAAAAAAOgAAAAAIAACAAAABvWAzeNzQNezDrgEv7gK6oru+HutxEg99m5LCRnRB+RTAAAADOWsO66erqhGbX3+KqKTckviqykMRkql+scy7E4p8mHSq0qimMd7sQzMRm9SXIlZhAAAAAdo0vT4/Mf6CTjYhCoACcW45exin4LA0hw2PKmNEixMBjWvg2PDJPfwPhVtXyLuqeOUDLmfT8O3hffZmq6QsfNw==";

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

    public static void checkMessageFromWebSocket(String wssBaseUrl, String port,
                                                 String clientProtocol,String transport,
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

                                JSONObject jsonPayload = JSONObject.parseObject(payload);

                                System.out.println(jsonPayload);
                                System.out.println(jsonPayload.get("M") != null);
                            }
                        }
                ).build()).get();
    }
}