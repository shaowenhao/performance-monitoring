package com.siemens.datalayer.snc.test;

import com.siemens.datalayer.iot.util.KafkaUtil;
import com.siemens.datalayer.snc.util.MessageSupplier;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.AllOf.allOf;

import org.hamcrest.core.AllOf;

@Epic("SDL Lpg-transform-load")
public class LpgTransformLoadTests {

    // data-layer-lpg-transform-load.yml defined the servers and topic
    String servers = "140.231.89.85:31092,140.231.89.85:31093,140.231.89.85:31094,140.231.89.85:31095,140.231.89.85:31096";
    String topic = "instance-kg-test";

    @Parameters({"base_url", "port"})
    @BeforeClass(description = "Configure the host address and communication port of data-layer-lpg-transform-load")
    public void setApiEngineEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("30467") String port)
    {
        LpgTransformLoadEndpoint.setBaseUrl(base_url);
        LpgTransformLoadEndpoint.setPort(port);
    }


    @Test(priority = 0, description = "check instance-kg generated")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Instance-kg-generation-service")
    @Story("check instance-kg")
    public void QueryCheckInatanceKg()
    {
        KafkaProducer<String, String> producer = KafkaUtil.createProducer(servers);
        //kafka cluster not stable yet, execute five times, at least generated one
        for (int i = 0; i <5 ; i++) {
            KafkaUtil.send(producer,topic, new MessageSupplier().get());
        }
       // check instance-kg num
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Response response = LpgTransformLoadEndpoint.searchGraph("release160","Alarm");
        int actualInstancekgNum = checkInstancekgNum(response);
        System.out.println("nums of gererated instance-kg:" + actualInstancekgNum);
        assertThat(actualInstancekgNum,allOf(greaterThanOrEqualTo(1),lessThanOrEqualTo(5)));
    }

    public static int checkInstancekgNum(Response response){

        // check Alarm emtity generated instance-kg, label named stated with Alarm_ and type is instance
        List<Map<String,Object>> entityList = response.jsonPath().getList("data.entities");
        List<Map<String, Object>> filterList = entityList.stream().filter(e -> {
            String value = (String) e.get("label");
            return value.startsWith("Alarm_");
        }).filter(e->{
            Map<String,String> properties = (Map<String,String>) e.get("properties");
            return  properties.get("metadata_node_type").equals("instance");
        }).collect(Collectors.toList());
        return filterList.size();
    }
}
