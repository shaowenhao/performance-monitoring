package com.siemens.datalayer.apiengine.test;

import com.siemens.datalayer.apiengine.model.EntitiesApiResponse;
import com.siemens.datalayer.apiengine.model.GraphqlApiResponse;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Epic("Regression Tests")
@Feature("Ontology Rest API Tests")
public class OntologyInterfaceTests {

    @Parameters({"baseUrl", "port"})
    @BeforeClass
    public void setApiengineEndpoint(@Optional("http://140.231.89.85") String baseUrl, @Optional("30296") String port) {
        ApiEngineEndpoint.setBaseUrl(baseUrl);
        ApiEngineEndpoint.setPort(port);
    }

    @Test(priority = 0, description = "Test ontology interface: Get all ontology domains without entities.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify if all domains return with entities null.")
    @Story("Ontology Interface API design")
    public void getAllInstanceOfOneEntityByRestful() {
        Reporter.log("Send request to ontology api with withEntities false");

        HashMap<String, String> queryParameters = new HashMap<>();
        queryParameters.put("withEntities", "false");

        Response response = ApiEngineEndpoint.getEntities(queryParameters);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        EntitiesApiResponse rspBody = response.getBody().as(EntitiesApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());

    }


}
