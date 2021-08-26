package com.siemens.datalayer.connector.test;

import com.siemens.datalayer.utils.AllureEnvironmentPropertiesWriter;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

@Epic("SDL Connector")
@Feature("Rest api except 'Connector Interface'")
public class ConnectorOtherInterfacesTests {

    @Parameters({"base_url", "port", "domain_name"})
    @BeforeClass(description = "Configure the host address and communication port of data-layer-connector")
    public void setConnectorOtherInterfacesEndpoint(@Optional("http://localhost") String base_url,@Optional("9001") String port,String domain_name){
        ConnectorOtherInterfacesEndpoint.setBaseUrl(base_url);
        ConnectorOtherInterfacesEndpoint.setPort(port);
        ConnectorOtherInterfacesEndpoint.setDomainName(domain_name);
    }

    @Test(priority = 0, description = "Test Developer Tools:clearRedisCaches.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'clearRedisCaches' request")
    @Story("Test Developer Tools:clearRedisCaches")
    public void clearRedisCaches()
    {
        Response response = ConnectorOtherInterfacesEndpoint.clearRedisCaches();
        JsonPath jsonPath = response.jsonPath();

        Assert.assertEquals("Operate success.",jsonPath.getString("message"));
    }
}
