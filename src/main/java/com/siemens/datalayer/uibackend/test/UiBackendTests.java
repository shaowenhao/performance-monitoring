package com.siemens.datalayer.uibackend.test;

import com.siemens.datalayer.entitymanagement.test.EntityManagementEndpoint;
import com.siemens.datalayer.utils.AllureEnvironmentPropertiesWriter;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;

public class UiBackendTests {
    static String sucessfulRspCode = "100000";

    static Map<String, List<String>> testEntityMap;
    static Map<String,List<String>> testRelationMap;
    static Map<String,String> testGraphMap;

    @Parameters({"base_url", "port","entitymgt_port"})
    @BeforeClass(description = "Configure the host address and communication port of ui backend")
    public void setUiBackendEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("31280") String port,@Optional("31998") String entitymgt_port)
    {
        testEntityMap = new HashMap<>();
        testRelationMap = new HashMap<>();
        testGraphMap = new HashMap<>();

        UiBackendEndpoint.setBaseUrl(base_url);
        UiBackendEndpoint.setPort(port);
        EntityManagementEndpoint.setBaseUrl(base_url);
        EntityManagementEndpoint.setPort(entitymgt_port);
        AllureEnvironmentPropertiesWriter.addEnvironmentItem("data-layer-ui-backend", base_url + ":" + port);

        // delete the entities if the label of entity in List entityLabelToBeDeleteList
        List<String> entityIdToBeDeleteList = new ArrayList<>();

        List<String> entityLabelToBeDeleteList = Arrays.asList("Battery","Bus");

        for(String label : entityLabelToBeDeleteList){
            Response responseOfGetEntities = EntityManagementEndpoint.getEntities(label);
            if ((responseOfGetEntities.jsonPath().getList("data") !=null) && (!responseOfGetEntities.jsonPath().getList("data").isEmpty()))
            {
                List<Map<String,Object>> entityList = responseOfGetEntities.jsonPath().getList("data");
                for (int i=0;i<entityList.size();i++)
                {
                    if (!entityIdToBeDeleteList.contains(entityList.get(i).get("id")))
                        entityIdToBeDeleteList.add(String.valueOf(entityList.get(i).get("id")));
                }
            }
        }

        if (!entityIdToBeDeleteList.isEmpty())
            EntityManagementEndpoint.deleteEntities(String.join(",",entityIdToBeDeleteList));

        // delete the relations if the label of relation in List relationLabelToBeDeleteList
        List<String> relationIdToBeDeleteList = new ArrayList<>();

        List<String> relationLabelToBeDeleteList = Arrays.asList("gp_edge1");

        for(String label : relationLabelToBeDeleteList){
            Response responseOfGetRelations = EntityManagementEndpoint.getRelations(label);
            if ((responseOfGetRelations.jsonPath().getList("data") !=null) && (!responseOfGetRelations.jsonPath().getList("data").isEmpty()))
            {
                List<Map<String,Object>> entityList = responseOfGetRelations.jsonPath().getList("data");
                for (int i=0;i<entityList.size();i++)
                {
                    if (!relationIdToBeDeleteList.contains(entityList.get(i).get("id")))
                        relationIdToBeDeleteList.add(String.valueOf(entityList.get(i).get("id")));
                }
            }
        }

        if (!relationIdToBeDeleteList.isEmpty())
            EntityManagementEndpoint.deleteRelations(String.join(",",relationIdToBeDeleteList));
    }

    @Test( alwaysRun = true,
            priority = 0,
            description = "Test Ui-backend Endpoint: graphsCheck for entity",
            dataProvider = "ui-backend-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'graphsCheck' request to ui backend endpoint interface.")
    @Story("Ui-backend End Point: graphsCheck")
    public void graphsEntityCheck(Map<String, String> paramMaps){
        String bodyString = paramMaps.get("body");
        Response response = UiBackendEndpoint.graphsCheck(bodyString);
        response.prettyPrint();
        checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));
        if(paramMaps.containsKey("errorMessage")){
            List<Map<String,Object>> rspErrorList = response.jsonPath().getList("data.error");
            List<String> rspErrorMessageList = rspErrorList.stream().map(e -> e.get("message").toString()).collect(Collectors.toList());
            String errorMessage =  paramMaps.get("errorMessage");
            String[] errorMessages = StringUtils.split(errorMessage, ";");
            assertThat(rspErrorMessageList,hasItems(errorMessages));
        }
    }

    @Test ( alwaysRun = true,
            priority = 0,
            description = "Test Ui-backend Endpoint: graphsPublish",
            dataProvider = "ui-backend-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'graphsPublish' request to ui-backend endpoint interface.")
    @Story("Ui-backend End Point: graphsPublish")
    public void graphsPublish(Map<String, String> paramMaps){
        String bodyString = paramMaps.get("body");
        Response response = UiBackendEndpoint.graphsPublish(bodyString);
        if(response.jsonPath().getString("code").equals(sucessfulRspCode)){
            // 查询entityes 获取laebl和id
            Response getEntitiesResponse = UiBackendEndpoint.getEntities(paramMaps.get("entitylabels"));
            generateTestGrapMap(getEntitiesResponse);
            //查询relation 获取label和id
            Response getRelationsResponse = UiBackendEndpoint.getRelations(paramMaps.get("relationLabels"));
            generateTestGrapMap(getRelationsResponse);
        }
    }

    // generate testGrapMap to store label and id
    public static void generateTestGrapMap(Response Response) {
        List<Map<String, String>> dataList;
        dataList = Response.jsonPath().getList("data");
        for (Map<String, String> properties : dataList) {
            String label = properties.get("label");
            String id = properties.get("id");
            testGraphMap.put(label, id);
        }
    }


    @Test ( dependsOnMethods = { "graphsPublish" },
            alwaysRun = true,
            priority = 0,
            description = "test Ui-backend Endpoint: graphsCheck for relation",
            dataProvider = "ui-backend-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a 'graphsPublish' request to ui-backend endpoint interface.")
    @Story("Ui-backend End Point: graphsCheck")
    public void graphsRelationCheck(Map<String, String> paramMaps){

        String originalBody = paramMaps.get("body");
        // replace all placeholders of body
        String tempBody = StringUtils.replace(originalBody, "$entity_id1", testGraphMap.get("Battery"));
        String replacedEntityBody = StringUtils.replace(tempBody, "$entity_id2", testGraphMap.get("Bus"));
        String updateCheckBody = StringUtils.replace(replacedEntityBody, "$edge_id1", testGraphMap.get("gp_edge1"));
        System.out.println(updateCheckBody);
        Response response = UiBackendEndpoint.graphsCheck(updateCheckBody);
        checkResponseCode(paramMaps,response.getStatusCode(),response.jsonPath().getString("code"),response.jsonPath().getString("message"));
        Map<String,List<String>> data = response.jsonPath().get("data");
        //check valid publish action without warning and error
        if(paramMaps.get("description").contains("good request")){
            assertThat(data.get("warning"),is(empty()));
            assertThat(data.get("error"),is(empty()));
        }
        if(paramMaps.containsKey("errorMessage")){
            List<Map<String,Object>> rspErrorList = response.jsonPath().getList("data.error");
            List<String> rspErrorMessageList = rspErrorList.stream().map(e -> e.get("message").toString()).collect(Collectors.toList());
            String errorMessage =  paramMaps.get("errorMessage");
            String[] errorMessages = StringUtils.split(errorMessage, ";");
            assertThat(rspErrorMessageList,hasItems(errorMessages));
        }
    }

    @Step("Verify the status code, operation code, and message")
    public static void checkResponseCode(Map<String, String> requestParameters, int actualStatusCode, String actualCode, String actualMessage)
    {
        // 校验http返回状态码
        int expStatusCode = 200;	// If not specified, the expected status code is set to 200 (OK)
        if (requestParameters.containsKey("rspStatus")) expStatusCode = Integer.valueOf(requestParameters.get("rspStatus")).intValue();

        Assert.assertEquals(actualStatusCode, expStatusCode, "The status code in response message matches the expected value.");

        // 校验Response body-code
        if ((requestParameters.containsKey("rspCode")))
        {
            if (requestParameters.get("rspCode").contains("null"))
                Assert.assertNull(actualCode, "No operation code is found.");
            else
                Assert.assertEquals(actualCode, requestParameters.get("rspCode"), "The operation code in response message matches the expected value.");
        }

        // 校验Response body-message
        if (requestParameters.containsKey("rspMessage"))
        {
            if (requestParameters.get("rspMessage").contains("null"))
                Assert.assertNull(actualMessage, "The content of operation message is null.");
            else
                Assert.assertTrue(actualMessage.contains(requestParameters.get("rspMessage")), "The operation message contains the expected content.");
        }
    }
}
