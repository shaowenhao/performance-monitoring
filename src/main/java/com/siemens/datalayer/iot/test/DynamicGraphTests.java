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

import static com.siemens.datalayer.entitymanagement.test.EntityManagementTests.checkResponseCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItems;

@Epic("SDL Lpg-transform-load")
public class DynamicGraphTests {

    @Parameters({"base_url", "port"})
    @BeforeClass(description = "Configure the host address and communication port of data-layer-lpg-transform-load")
    public void setApiEngineEndpoint(@Optional("http://140.231.89.106") String base_url, @Optional("31141") String port)
    {
        LpgTransformLoadEndpoint.setBaseUrl(base_url);
        LpgTransformLoadEndpoint.setPort(port);

        LpgTransformLoadEndpoint.deleteInstanceGraph("test6761");
    }

    @Test(priority = 0,
            description = "check list Graph Names",
            dataProvider = "entity-management-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Instance-kg-generation-service")
    @Story("check dynamic graph")
    public void listGraphNames(Map<String, String> paramMaps){

        Response response = LpgTransformLoadEndpoint.listAllGraphNames();

        checkGraphNamesList(paramMaps, response);
        checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));
    }



    @Test(dependsOnMethods = { "listGraphNames"},
            alwaysRun = true,
            priority = 0,
            description = "create an instance Graph",
            dataProvider = "entity-management-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Instance-kg-generation-service")
    @Story("creat instance graph")
    public void createInstanceGraph(Map<String, String> paramMaps){

        String graphName = paramMaps.get("graphName");
        Response response = LpgTransformLoadEndpoint.createInstanceGraph(graphName);
        checkGraphNamesList(paramMaps,response);
        checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));
    }

    @Test(dependsOnMethods = { "createInstanceGraph"},
            alwaysRun = true,
            priority = 0,
            description = "create an Kg",
            dataProvider = "entity-management-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Instance-kg-generation-service")
    @Story("creat kg")
    public void generateKg(Map<String, String> paramMaps){

        String graphName = paramMaps.get("graphName");
        String entityLabels =  paramMaps.get("entityLabels");
        String graphql = paramMaps.get("graphql");
        Response response = LpgTransformLoadEndpoint.generateKg(entityLabels, graphName, graphql);
        response.prettyPrint();
        checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));
    }


    public void checkGraphNamesList(Map<String, String> paramMaps, Response response) {
        List<String> actualGraphList = response.jsonPath().getList("data");

        String[] expectContainsGraphNames = paramMaps.get("responseData").split(",");

//        try {
//           expectedList = objectMapper.readValue(paramMaps.get("responseData"), new TypeReference<List<String>>() {
//            });
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
        assertThat(actualGraphList,hasItems(expectContainsGraphNames));
    }

}
