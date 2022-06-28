package com.siemens.datalayer.iot.test;

import com.siemens.datalayer.connector.test.ConnectorEndpoint;
import com.siemens.datalayer.connector.test.InterfaceTests;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("SDL Connector")
@Feature("webservice as data source")
public class WebServiceAsDataSourcesTests {

    @Parameters({"base_url", "port"})
    @BeforeClass(description = "Configure the host address and communication port of data-layer-connector")
    public void setConnectorEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("30850") String port) {
        ConnectorEndpoint.setBaseUrl(base_url);
        ConnectorEndpoint.setPort(port);

    }


    @Test(priority = 0,
            description = "webservice as data source,query data source",
            dataProvider = "connector-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'dmlInsertOperator' request with insert info and check the response message.")
    @Story("webservice as data source,query data source")
    public void dmlDataInsert(Map<String, String> paramMaps) {

        // add clean connecotr cache to make it more stable
        ConnectorEndpoint.clearAllCaches();

        String insertinfo = paramMaps.get("insertinfo");
        Response response = ConnectorEndpoint.dmlInsertOperator(insertinfo);
        if (paramMaps.get("description").contains("data retrieved")) {
            InterfaceTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));
            List<HashMap<String, String>> rspDataList;
            String listPath = "data.warehousewrite";
            rspDataList = response.jsonPath().getList(listPath);
            assertThat(rspDataList, hasSize(1));
            String codePath = "data.warehousewrite[0].code";
            String responseCode = response.jsonPath().getString(codePath);
            assertThat(responseCode,is(equalTo("0")));
        }
        else{
            String dataPath = "data.warehousewrite[0].data";
            String data = response.jsonPath().get(dataPath);
            String expectData = paramMaps.get("rspMessage");
            assertThat(data,is(equalTo(expectData)));
        }
    }

}
