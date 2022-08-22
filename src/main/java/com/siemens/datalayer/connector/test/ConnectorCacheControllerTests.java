package com.siemens.datalayer.connector.test;

import com.siemens.datalayer.iot.util.KafkaUtil;
import com.siemens.datalayer.utils.AllureEnvironmentPropertiesWriter;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

@Epic("SDL connector cache controller")
@Feature("connector cache controler")
public class ConnectorCacheControllerTests {

    @Parameters({"base_url", "port"})
    @BeforeClass(description = "Configure the host address and communication port of connector cache controller")
    public void setConnectorCacheControllerEndpoint(@Optional("http://140.231.89.106") String base_url, @Optional("31439") String port) {

        ConnectorCacheControllerEndpoint.setBaseUrl(base_url);
        ConnectorCacheControllerEndpoint.setPort(port);
    }

    @Test(priority = 0,
            description = "Test Connector   Cache Controller: allCacheNames",
            dataProvider = "connector-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post a 'getAllCacheNames' request to cache controller interface.")
    @Story("Cache Controller: cache interface")
    public void getAllCacheNames(Map<String, String> paramMaps) {

        Response response = ConnectorCacheControllerEndpoint.getAllCacheNames();
        response.prettyPrint();
        List<String> actualCacheNameList = response.jsonPath().getList("data");
        String dataList = paramMaps.get("dataList");
        String[] expectedCacheNames = dataList.split(",");
        assertThat(actualCacheNameList,is(hasItems(expectedCacheNames)));
    }

}
