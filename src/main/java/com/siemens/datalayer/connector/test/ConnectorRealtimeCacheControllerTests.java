package com.siemens.datalayer.connector.test;


import com.alibaba.fastjson.JSON;
import com.siemens.datalayer.apiengine.test.ApiEngineCacheControllerEndpoint;
import com.siemens.datalayer.entitymanagement.test.EntityManagementEndpoint;
import com.siemens.datalayer.utils.AllureEnvironmentPropertiesWriter;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

@Epic("SDL connector realtime cache controller")
@Feature("connector realtime cache controler")
public class ConnectorRealtimeCacheControllerTests {

    @Parameters({"base_url", "port"})
    @BeforeClass(description = "Configure the host address and communication port of connector realtime cache controller")
    public void setApiEngineCacheControllerEndpoint(@Optional("http://140.231.89.106") String base_url, @Optional("32563") String port) {

        ConnectorRealtimeCacheControllerEndpoint.setBaseUrl(base_url);
        ConnectorRealtimeCacheControllerEndpoint.setPort(port);
        AllureEnvironmentPropertiesWriter.addEnvironmentItem("data-layer-connector-realtime", base_url + ":" + port);
    }

    @Test(priority = 0,
            description = "Test Connector realtime Cache Controller: allCacheNames"
            )
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'getAllCacheNames' request to cache controller interface.")
    @Story("Cache Controller: cache interface")
    public void getAllCacheNames() {
        Response response = ConnectorRealtimeCacheControllerEndpoint.getAllCacheNames();
        response.prettyPrint();
        List<String> actualList = response.jsonPath().getList("data");
        assertThat(actualList,is(hasItems("mapperConfigCache","engineConfigCache")));
    }

}
