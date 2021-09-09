package com.siemens.datalayer.iot.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siemens.datalayer.connector.test.ConnectorEndpoint;
import com.siemens.datalayer.connector.test.InterfaceTests;
import com.siemens.datalayer.utils.AllureEnvironmentPropertiesWriter;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class WebServiceAsDataSourcesTests {

    @Parameters({"base_url", "port"})
    @BeforeClass(description = "Configure the host address and communication port of data-layer-connector")
    public void setConnectorEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("30850") String port) {
        ConnectorEndpoint.setBaseUrl(base_url);
        ConnectorEndpoint.setPort(port);
    }


    @Test(priority = 0,
            description = "Test connector interface: DML insert operator.",
            dataProvider = "connector-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'dmlInsertOperator' request with insert info and check the response message.")
    @Story("Connector Interface: dml data insert operator")
    public void dmlDataInsert(Map<String, String> paramMaps) {

        String insertinfo = paramMaps.get("insertinfo");
        Response response = ConnectorEndpoint.dmlInsertOperator(insertinfo);
        InterfaceTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));
        if (paramMaps.get("description").contains("data retrieved")) {
            List<HashMap<String, String>> rspDataList;
            String listPath = "data.warehousewrite";
            rspDataList = response.jsonPath().getList(listPath);
            assertThat(rspDataList, hasSize(1));
        }
        else{
            Assert.assertNull(response.jsonPath().getList("data"));
        }
    }

}
