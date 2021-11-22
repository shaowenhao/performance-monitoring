package com.siemens.datalayer.connector.test;

import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Epic("SDL Connector Realtime")
@Feature("'CORE-Data connector realtime")
public class ConnectorRealtimeTests {

    @Parameters({"base_url", "port"})
    @BeforeClass(description = "Configure the host address and communication port of data-layer-connector")
    public void setConnectorOtherInterfacesEndpoint(@Optional("http://localhost") String base_url,@Optional("9001") String port){
        ConnectorRealtimeEndpoint.setBaseUrl(base_url);
        ConnectorRealtimeEndpoint.setPort(port);
    }

    @Test(  alwaysRun = true,
            priority = 0,
            description = "Test Connector-realtime Plugin Controller: loadBundle",
            dataProvider = "connector-realtime-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'loadBundle' request to plugin controller interface.")
    @Story("Plugin Controller: loadBundle")
    public void loadBundle(Map<String, String> paramMaps)
    {
        Response responseOfFindAllPlugins = ConnectorRealtimeEndpoint.findAllPlugins();

        List<Map<String,String>> allPluginsList = responseOfFindAllPlugins.jsonPath().getList("data");
        List<String> allPluginIdList = allPluginsList.stream().map(e -> e.get("pluginId")).collect(Collectors.toList());
        List<String> allDriverTypeList = allPluginsList.stream().map(e -> e.get("driverType")).collect(Collectors.toList());

        String pluginId = null;
        String driverType = null;

        if (paramMaps.containsKey("pluginId"))
            pluginId = paramMaps.get("pluginId");

        if (paramMaps.containsKey("driverType"))
            paramMaps.get("driverType");

        Response responseOfPluginUnload;
        if (allPluginIdList.contains(pluginId))
        {
            responseOfPluginUnload = ConnectorRealtimeEndpoint.pluginOperation("UNLOAD", pluginId);
        }
    }
}
