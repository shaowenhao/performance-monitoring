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

@Epic("Api Engine Interface")
@Feature("Rest API")
public class EntitiesInterfaceTests {

    @Parameters({"baseUrl", "port"})
    @BeforeClass
    public void setEndpoint(@Optional("http://140.231.89.85") String baseUrl, @Optional("30035") String port) {
        EntitiesEndpoint.setBaseUrl(baseUrl);
        EntitiesEndpoint.setPort(port);
    }

    @Test(priority = 0, description = "Test entities interface: .")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT and verify .")
    @Story("Get Entities ")
    public void getAllInstanceOfOneEntityByRestful() {


    }






}
