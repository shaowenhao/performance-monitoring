package com.siemens.datalayer.apiengine.test;

import com.siemens.datalayer.apiengine.model.EntitiesApiResponse;
import com.siemens.datalayer.apiservice.model.ApiResponse;
import com.siemens.datalayer.apiservice.test.ApiServiceEndpoint;
import com.siemens.datalayer.utils.Utils;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.Reporter;

import java.util.*;
import java.util.stream.Collectors;

public class ApiEngineHelper {

    public static ArrayList<HashMap> getAnalogSiid(){
        Reporter.log("Send request to entities api with root=Analog filter=[Analog][type,Siid]");

        HashMap queryParameters = new HashMap<>();
        queryParameters.put("filter", "[Analog][type,Siid]");
        queryParameters.put("root", "Analog");

        Response response = ApiEngineEndpoint.getEntities(queryParameters);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        EntitiesApiResponse rspBody = response.getBody().as(EntitiesApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());
        JsonPath jsonPathEvaluator = response.jsonPath();
        List<String> l = new ArrayList<String>(
                Arrays.asList(
                        "Siid",
                        "type"
                )
        );
        ArrayList<HashMap> a = (ArrayList)jsonPathEvaluator.get("data.Analog");
        for(HashMap m : a){
            List<String> ll = new ArrayList<String>(m.keySet());
            ll.forEach( key-> Assert.assertTrue(l.contains(key)));
            Assert.assertEquals("Analog", m.get("type").toString());
        }
        return a;
    }
    public static int getOneAnalogSiid() {
        ArrayList<HashMap> list= ApiEngineHelper.getAnalogSiid();
        Random rand = new Random();
        HashMap random = list.get(rand.nextInt(list.size()));
        return Integer.parseInt(random.get("Siid").toString());
    }
}
