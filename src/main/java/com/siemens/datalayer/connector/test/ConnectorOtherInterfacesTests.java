package com.siemens.datalayer.connector.test;

import com.siemens.datalayer.utils.ExcelDataProviderClass;

import io.qameta.allure.*;
import io.qameta.allure.restassured.AllureRestAssured;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Epic("SDL Connector")
@Feature("'CORE-Data connector for Restful API' or 'Developer Tools' etc.")
public class ConnectorOtherInterfacesTests {

    @Parameters({"base_url", "port", "domain_name"})
    @BeforeClass(description = "Configure the host address and communication port of data-layer-connector")
    public void setConnectorOtherInterfacesEndpoint(@Optional("http://localhost") String base_url,@Optional("9001") String port,String domain_name){
        ConnectorOtherInterfacesEndpoint.setBaseUrl(base_url);
        ConnectorOtherInterfacesEndpoint.setPort(port);
        ConnectorOtherInterfacesEndpoint.setDomainName(domain_name);
    }

    @Test (	priority = 0,
            description = "复杂结构的Restful做为数据源，connector get此数据源",
            dataProvider = "connector-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'getConceptModelDataByCondition' request with specified 'name' and check the response message.")
    @Story("Support for Complex Structure in Restful Get API as Data Sources")
    public void getDataByConditionForRestfulGet(Map<String,String> paramMaps)
    {
        HashMap<String,String> queryParameters = new HashMap<>();

        if (paramMaps.containsKey("name")) queryParameters.put("name",paramMaps.get("name"));

        Response response = ConnectorOtherInterfacesEndpoint.getConceptModelDataByCondition(queryParameters);

        InterfaceTests.checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));

        checkEntityFields(paramMaps.get("entityFields"),response);
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

    @Step("Verity entityFields return from getConceptModelDataByCondition and data source")
    public static void checkEntityFields(String entityFields,Response response)
    {
        List<String> exceptedEntityFieldsList = Arrays.asList(entityFields.split(","));

        Map<String,String> actualEntityFieldsMap = response.jsonPath().getMap("data[0]");
        Assert.assertEquals(exceptedEntityFieldsList.size(),actualEntityFieldsMap.size());

        Response exceptedResponseFromDataSource = getSensorByDeviceId("http://140.231.89.107","3000");

        for (Map.Entry<String,String> entityField : actualEntityFieldsMap.entrySet())
        {
            if (entityField.getKey() == "deviceType")
                Assert.assertEquals(exceptedResponseFromDataSource.jsonPath().getString("data[0].deviceType"),entityField.getValue());

            if (entityField.getKey() == "MSDistribute")
                Assert.assertEquals(exceptedResponseFromDataSource.jsonPath().getString("data[0].properties.MSDistribute"),entityField.getValue());

            if (entityField.getKey() == "Siid2")
                Assert.assertEquals(exceptedResponseFromDataSource.jsonPath()
                        .getString("data[0].properties.metadata_node_pk.metadata_node_pk1.metadata_node_pk2.Siid2"),entityField.getValue());

            if (entityField.getKey() == "Siid1")
                Assert.assertEquals(exceptedResponseFromDataSource.jsonPath()
                        .getString("data[0].properties.metadata_node_pk.metadata_node_pk1.Siid1"),entityField.getValue());

            if (entityField.getKey() == "Siid")
                Assert.assertEquals(exceptedResponseFromDataSource.jsonPath().getString("data[0].properties.metadata_node_pk.Siid"),entityField.getValue());

            if (entityField.getKey() == "SoeEnabled")
                Assert.assertEquals(exceptedResponseFromDataSource.jsonPath().getString("data[0].properties.SoeEnabled"),entityField.getValue());
        }
    }

    @Step("Send a request of 'getSensorByDeviceId'(data source)")
    public static Response getSensorByDeviceId(String baseUrl,String port)
    {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = Integer.valueOf(port).intValue();

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("content-type","application/json");

        Response response = httpRequest.filter(new AllureRestAssured())
                .get("/getSensorByDeviceId");
        return response;
    }
}
