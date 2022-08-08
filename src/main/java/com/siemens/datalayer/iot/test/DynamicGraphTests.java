package com.siemens.datalayer.iot.test;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siemens.datalayer.snc.test.LpgTransformLoadEndpoint;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

@Epic("SDL Lpg-transform-load")
public class DynamicGraphTests {

    @Parameters({"base_url", "port"})
    @BeforeClass(description = "Configure the host address and communication port of data-layer-lpg-transform-load")
    public void setApiEngineEndpoint(@Optional("http://140.231.89.106") String base_url, @Optional("31141") String port)
    {
        LpgTransformLoadEndpoint.setBaseUrl(base_url);
        LpgTransformLoadEndpoint.setPort(port);
    }

    @Test(priority = 0,
            description = "",
            dataProvider = "entity-management-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Instance-kg-generation-service")
    @Story("check dynamic graph")
    public void listGraphNames(Map<String, String> paramMaps){
       // System.out.println(paramMaps.get("response"));
        Response response = LpgTransformLoadEndpoint.listAllGraphNames();
        checkGraphNamesList(paramMaps, response);
    }

    public void checkGraphNamesList(Map<String, String> paramMaps, Response response) {
        List<String> actualGraphList = response.jsonPath().getList("data");
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> expectedList = null;
        try {
           expectedList = objectMapper.readValue(paramMaps.get("response"), new TypeReference<List<String>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(actualGraphList,expectedList);
    }

}
