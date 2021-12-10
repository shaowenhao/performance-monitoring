package com.siemens.datalayer.iot.test;

import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.Map;

@Epic("App client authentication")
@Feature("App client authentication for k8s")
public class AppClientAuthenticationForK8sTests {
    @Parameters({"baseUrlWithAuth", "portWithAuth"})
    @BeforeClass(description = "Configure the host address and communication port of data-layer-connector")
    public void setConnectorEndpoint(@Optional("https://demo.technology.siemens.cloud") String baseUrlWithAuth, @Optional("10443") String portWithAuth) {
        AppClientAuthenticationEndpoint.setBaseUrl(baseUrlWithAuth);
        AppClientAuthenticationEndpoint.setPort(portWithAuth);
    }

    @Test ( priority = 0,
            description = "need token for '/graphql/schema' with clientId 'aaa'",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'getSchema' request and check the response message.")
    @Story("need token for '/graphql/schema' with clientId 'aaa'")
    public void getSchemaWithAuth(Map<String, String> paramMaps)
    {
        Response response;
        if (paramMaps.containsKey("clientId") && paramMaps.containsKey("clientSecret") && paramMaps.containsKey("accessTokenUrl"))
            response = AppClientAuthenticationEndpoint.getSchema(paramMaps);
        else
            response = AppClientAuthenticationEndpoint.getSchema();

        Assert.assertEquals(Integer.valueOf(paramMaps.get("rspStatus")).intValue(),response.getStatusCode());
    }

    @Test ( priority = 0,
            description = "no token needed for '/cache/stats'",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'getAllCacheStats' request and check the response message.")
    @Story("no token needed for '/cache/stats'")
    public void getAllCacheStatsWithAuth(Map<String, String> paramMaps)
    {
        Response response;
        if (paramMaps.containsKey("clientId") && paramMaps.containsKey("clientSecret") && paramMaps.containsKey("accessTokenUrl"))
            response = AppClientAuthenticationEndpoint.getAllCacheStats(paramMaps);
        else
            response = AppClientAuthenticationEndpoint.getAllCacheStats();

        Assert.assertEquals(Integer.valueOf(paramMaps.get("rspStatus")).intValue(),response.getStatusCode());
    }
}
