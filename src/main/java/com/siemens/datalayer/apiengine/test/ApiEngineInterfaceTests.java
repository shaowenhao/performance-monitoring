package com.siemens.datalayer.apiengine.test;

import com.siemens.datalayer.apiengine.model.GraphqlApiResponse;
import com.siemens.datalayer.apiengine.model.ResponseCode;
import com.siemens.datalayer.utils.CommonCheckFunctions;
import com.siemens.datalayer.utils.ExcelDataProviderClass;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.*;
import org.testng.annotations.Optional;

import java.util.*;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;

@Epic("Api Engine Interface")
@Feature("Rest API")
public class ApiEngineInterfaceTests {

    @Parameters({"baseUrl", "port"})
    @BeforeClass(alwaysRun = true)
    public void setApiengineEndpoint(@Optional("http://140.231.89.85") String baseUrl, @Optional("30169") String port) {
        ApiEngineEndpoint.setBaseUrl(baseUrl);
        ApiEngineEndpoint.setPort(port);
    }


    @Test ( priority = 0,
            description = "Test Api-engine Query Endpoint: GraphQL interface",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with graphql and verify if correct return.")
    @Story("Query all jinzu by graphql")
    public void queryJinzuByGraphQL(Map<String, String> paramMaps) {
        Reporter.log("Send request to graphql api with graphql");

        String query = paramMaps.get("query");

        Response response = ApiEngineEndpoint.postGraphql(query);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals(paramMaps.get("rspMessage"), rspBody.getMessage());
        Assert.assertEquals(Integer.parseInt(paramMaps.get("rspCode")), rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));

        ArrayList<HashMap> data = jsonPathEvaluator.get(paramMaps.get("jsonpath"));
        Assert.assertEquals(data.size(), Integer.parseInt(paramMaps.get("size")));

        checkDataFollowsModelSchema("json-model-schema/jinzu/" + paramMaps.get("schema"), response);
    }



    @Test ( priority = 0,
            description = "Test Api-engine Query Endpoint: GraphQL interface",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with graphql and verify if correct return.")
    @Story("Query all jinzu by graphql")
    public void queryJinzuAndCompareByGraphQL(Map<String, String> paramMaps) {
        Reporter.log("Send request to graphql api with graphql");

        String query = paramMaps.get("query");

        Response response = ApiEngineEndpoint.postGraphql(query);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals(paramMaps.get("rspMessage"), rspBody.getMessage());
        Assert.assertEquals(Integer.parseInt(paramMaps.get("rspCode")), rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));

        ArrayList<HashMap> data = jsonPathEvaluator.get(paramMaps.get("jsonpath"));
        Assert.assertEquals(data.size(), Integer.parseInt(paramMaps.get("size")));

        String expectJson = paramMaps.get("expectJson");
        try {
            JSONAssert.assertEquals(response.getBody().asString(), expectJson, false);
        } catch (JSONException e) {
            e.printStackTrace();
            Assert.fail("Failed to compare result with expect json ", e);
        }
    }


    @Test(groups = "iot", description = "Test api engine interface: Query all plant by graphql.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with graphql and verify if correct return.")
    @Story("Query all iot plant by graphql")
    public void queryAllIOTPlantByGraphQL() {
        Reporter.log("Send request to graphql api with graphql");

        String query = "{\n" +
                "      Plant_Owner(cond: \"\", order: \"\") {\n" +
                "        credit_card\n" +
                "        tax_register_org_name\n" +
                "        name\n" +
                "        cellphone\n" +
                "        identification_card\n" +
                "        id\n" +
                "        org_name\n" +
                "        tax_register_no\n" +
                "        org_no\n" +
                "        registered_capital\n" +
                "        email\n" +
                "        Owner_Plant(cond: \"\", order: \"\") {\n" +
                "          address\n" +
                "          plant_owner\n" +
                "          latitude\n" +
                "          name\n" +
                "          id\n" +
                "          longitude\n" +
                "          Has_Device(cond: \"\", order: \"\") {\n" +
                "            id\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }";

        Response response = ApiEngineEndpoint.postGraphql(query);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));

        ArrayList<HashMap> data = jsonPathEvaluator.get("data.Plant_Owner");
        Assert.assertEquals(data.size(), 6);

        assertThat(response.getBody().asString(),
                matchesJsonSchemaInClasspath("iot-all-plants.json"));

    }



    @Test(groups = "iot", description = "Test api engine interface: Query one plant by graphql.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with graphql and verify if correct return.")
    @Story("Query one iot plant by graphql")
    public void queryOneIOTPlantByGraphQL() {
        Reporter.log("Send request to graphql api with graphql");

        String query = "{\n" +
                "      Plant(cond: \"{id:{_eq: 3}}\", order: \"\") {\n" +
                "        address\n" +
                "        plant_owner\n" +
                "        latitude\n" +
                "        name\n" +
                "        id\n" +
                "        longitude\n" +
                "        invert_Plant_Owner(cond: \"\", order: \"\") {\n" +
                "          credit_card\n" +
                "          tax_register_org_name\n" +
                "          name\n" +
                "          cellphone\n" +
                "          identification_card\n" +
                "          id\n" +
                "          org_name\n" +
                "          tax_register_no\n" +
                "          org_no\n" +
                "          registered_capital\n" +
                "          email\n" +
                "        }\n" +
                "        Has_Device(cond: \"\", order: \"\") {\n" +
                "          hi\n" +
                "          amps\n" +
                "          bearings\n" +
                "          rating\n" +
                "          type\n" +
                "          eff_grade\n" +
                "          manufacturer\n" +
                "          output\n" +
                "          sf\n" +
                "          design\n" +
                "          model\n" +
                "          id\n" +
                "          sinamics_300\n" +
                "          volts\n" +
                "          eff\n" +
                "          poles\n" +
                "          electric_current\n" +
                "          ip\n" +
                "          weight\n" +
                "          rpm\n" +
                "          ins\n" +
                "          plant\n" +
                "          name\n" +
                "          sinamics_300_port\n" +
                "          serial_no\n" +
                "          frame\n" +
                "        }\n" +
                "      }\n" +
                "    }";

        Response response = ApiEngineEndpoint.postGraphql(query);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));

        ArrayList<HashMap> data = jsonPathEvaluator.get("data.Plant");
        Assert.assertEquals(data.size(), 1);


        String expectJson = "{\n" +
                "    \"code\":100000,\n" +
                "    \"message\":\"Successfully\",\n" +
                "    \"data\":{\n" +
                "        \"Plant\":[\n" +
                "            {\n" +
                "                \"address\":\"湖南长沙市雨花区环保科技园仙岭中路85号\",\n" +
                "                \"plant_owner\":\"3\",\n" +
                "                \"latitude\":28.07459,\n" +
                "                \"name\":\"新日域(湖南)机电制造厂\",\n" +
                "                \"id\":\"3\",\n" +
                "                \"longitude\":113.02856,\n" +
                "                \"invert_Plant_Owner\":{\n" +
                "                    \"credit_card\":\"91320214762418438A\",\n" +
                "                    \"tax_register_org_name\":\"上海市工商局\",\n" +
                "                    \"name\":\"薄建军\",\n" +
                "                    \"cellphone\":\"13232341234\",\n" +
                "                    \"identification_card\":\"110103123412341234\",\n" +
                "                    \"id\":\"3\",\n" +
                "                    \"org_name\":\"济南西门子有限公司\",\n" +
                "                    \"tax_register_no\":\"91320214762418438A\",\n" +
                "                    \"org_no\":\"76241843-8\",\n" +
                "                    \"registered_capital\":50000000,\n" +
                "                    \"email\":\"fujianjun@siemens.com\"\n" +
                "                },\n" +
                "                \"Has_Device\":[\n" +
                "                    {\n" +
                "                        \"hi\":72,\n" +
                "                        \"amps\":0.9,\n" +
                "                        \"bearings\":\"6306\",\n" +
                "                        \"rating\":\"DFBLDWTT\",\n" +
                "                        \"type\":\"三相异步电动机\",\n" +
                "                        \"eff_grade\":\"三级\",\n" +
                "                        \"manufacturer\":\"TOSHIBA\",\n" +
                "                        \"output\":80,\n" +
                "                        \"sf\":92,\n" +
                "                        \"design\":\"TOSHIBA\",\n" +
                "                        \"model\":\"QX3-20-0.55\",\n" +
                "                        \"id\":\"10\",\n" +
                "                        \"sinamics_300\":\"ddd19ed6-7fb5-4ab0-972a-75a8657a30de\",\n" +
                "                        \"volts\":64,\n" +
                "                        \"eff\":37.4,\n" +
                "                        \"poles\":38,\n" +
                "                        \"electric_current\":28.8,\n" +
                "                        \"ip\":50,\n" +
                "                        \"weight\":48.4,\n" +
                "                        \"rpm\":44,\n" +
                "                        \"ins\":\"A\",\n" +
                "                        \"plant\":\"3\",\n" +
                "                        \"name\":\"三相异步电动机\",\n" +
                "                        \"sinamics_300_port\":6,\n" +
                "                        \"serial_no\":\"1546629c-5d3f-4170-a56d-0e1a6843c335\",\n" +
                "                        \"frame\":\"200L\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"hi\":67,\n" +
                "                        \"amps\":96.7,\n" +
                "                        \"bearings\":\"6306\",\n" +
                "                        \"rating\":\"AJTKVCAC\",\n" +
                "                        \"type\":\"单相异步电动机\",\n" +
                "                        \"eff_grade\":\"二级\",\n" +
                "                        \"manufacturer\":\"ABB\",\n" +
                "                        \"output\":32,\n" +
                "                        \"sf\":77,\n" +
                "                        \"design\":\"ABB\",\n" +
                "                        \"model\":\"QX3-20-0.55\",\n" +
                "                        \"id\":\"16\",\n" +
                "                        \"sinamics_300\":\"56561233-f2e5-44cf-abe8-1284861b3eeb\",\n" +
                "                        \"volts\":91,\n" +
                "                        \"eff\":94.4,\n" +
                "                        \"poles\":62,\n" +
                "                        \"electric_current\":24,\n" +
                "                        \"ip\":66,\n" +
                "                        \"weight\":21,\n" +
                "                        \"rpm\":7,\n" +
                "                        \"ins\":\"H\",\n" +
                "                        \"plant\":\"3\",\n" +
                "                        \"name\":\"单相异步电动机\",\n" +
                "                        \"sinamics_300_port\":8,\n" +
                "                        \"serial_no\":\"0c5120a7-831e-4a48-9fac-2178a3576958\",\n" +
                "                        \"frame\":\"ABS-70\"\n" +
                "                    }\n" +
                "                ]\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
//        System.out.println(response2.getBody().prettyPrint());
//        assertThat(response.getBody().asString(),
//                matchesJsonSchemaInClasspath("jinzu-all-projects-graphql-schema.json"));
        try {
            JSONAssert.assertEquals(response.getBody().asString(), expectJson, false);
        } catch (JSONException e) {
            e.printStackTrace();
            Assert.fail("Failed to compare result with expect json ", e);
        }

    }



    @Test(groups = "iot", description = "Test api engine interface: Query one device by graphql.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with graphql and verify if correct return.")
    @Story("Query one iot device by graphql")
    public void queryOneIOTDeviceByGraphQL() {
        Reporter.log("Send request to graphql api with graphql");

        String query = "{\n" +
                "\tDevice(cond: \"{id:{_eq:10}}\", order: \"\") {\n" +
                "\t  hi\n" +
                "\t  amps\n" +
                "\t  bearings\n" +
                "\t  rating\n" +
                "\t  type\n" +
                "\t  eff_grade\n" +
                "\t  manufacturer\n" +
                "\t  output\n" +
                "\t  sf\n" +
                "\t  design\n" +
                "\t  model\n" +
                "\t  id\n" +
                "\t  sinamics_300\n" +
                "\t  volts\n" +
                "\t  eff\n" +
                "\t  poles\n" +
                "\t  electric_current\n" +
                "\t  ip\n" +
                "\t  weight\n" +
                "\t  rpm\n" +
                "\t  ins\n" +
                "\t  plant\n" +
                "\t  name\n" +
                "\t  sinamics_300_port\n" +
                "\t  serial_no\n" +
                "\t  frame\n" +
                "\t  Connect_To_SINAMICS_300_Info(cond: \"\", order: \"\") {\n" +
                "\t\tplant\n" +
                "\t\tcontract\n" +
                "\t\tname\n" +
                "\t\tserial_no\n" +
                "\t\tmanufacturer\n" +
                "\t  }\n" +
                "\t}\n" +
                " }";

        Response response = ApiEngineEndpoint.postGraphql(query);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));

        ArrayList<HashMap> data = jsonPathEvaluator.get("data.Device");
        Assert.assertEquals(data.size(), 1);


        String expectJson = "{\n" +
                "    \"code\":100000,\n" +
                "    \"message\":\"Successfully\",\n" +
                "    \"data\":{\n" +
                "        \"Device\":[\n" +
                "            {\n" +
                "                \"hi\":72,\n" +
                "                \"amps\":0.9,\n" +
                "                \"bearings\":\"6306\",\n" +
                "                \"rating\":\"DFBLDWTT\",\n" +
                "                \"type\":\"三相异步电动机\",\n" +
                "                \"eff_grade\":\"三级\",\n" +
                "                \"manufacturer\":\"TOSHIBA\",\n" +
                "                \"output\":80,\n" +
                "                \"sf\":92,\n" +
                "                \"design\":\"TOSHIBA\",\n" +
                "                \"model\":\"QX3-20-0.55\",\n" +
                "                \"id\":\"10\",\n" +
                "                \"sinamics_300\":\"ddd19ed6-7fb5-4ab0-972a-75a8657a30de\",\n" +
                "                \"volts\":64,\n" +
                "                \"eff\":37.4,\n" +
                "                \"poles\":38,\n" +
                "                \"electric_current\":28.8,\n" +
                "                \"ip\":50,\n" +
                "                \"weight\":48.4,\n" +
                "                \"rpm\":44,\n" +
                "                \"ins\":\"A\",\n" +
                "                \"plant\":\"3\",\n" +
                "                \"name\":\"三相异步电动机\",\n" +
                "                \"sinamics_300_port\":6,\n" +
                "                \"serial_no\":\"1546629c-5d3f-4170-a56d-0e1a6843c335\",\n" +
                "                \"frame\":\"200L\",\n" +
                "                \"Connect_To_SINAMICS_300_Info\":{\n" +
                "                    \"plant\":\"4\",\n" +
                "                    \"contract\":\"sx-1101-03\",\n" +
                "                    \"name\":\"SINAMICS CONNECT 300\",\n" +
                "                    \"serial_no\":\"ddd19ed6-7fb5-4ab0-972a-75a8657a30de\",\n" +
                "                    \"manufacturer\":\"SIEMENS\"\n" +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
//        System.out.println(response.getBody().prettyPrint());
//        assertThat(response.getBody().asString(),
//                matchesJsonSchemaInClasspath("jinzu-all-projects-graphql-schema.json"));
        try {
            JSONAssert.assertEquals(response.getBody().asString(), expectJson, false);
        } catch (JSONException e) {
            e.printStackTrace();
            Assert.fail("Failed to compare result with expect json ", e);
        }

    }




    @Test(groups = "iot", description = "Test api engine interface: Query one device error alarm by graphql.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with graphql and verify if correct return.")
    @Story("Query one iot device error alarm by graphql")
    public void queryOneIOTDeviceErrorByGraphQL() {
        Reporter.log("Send request to graphql api with graphql");

        String query = "{\n" +
                "\tSINAMICS_300_Log(cond:\"{update_time:{_gte:\\\"%s\\\", _lte:\\\"%s\\\"}, port:{_eq:\\\"6\\\"},sinamics_300:{_eq:\\\"ddd19ed6-7fb5-4ab0-972a-75a8657a30de\\\"} }\") {\n" +
                "\t  update_time\n" +
                "\t  Actual_fault_code\n" +
                "\t}\n" +
                "}";

        long startTime = System.currentTimeMillis() - 10*60*1000;
        long endTime = System.currentTimeMillis() - 5*60*1000;

//        List<String> l = new ArrayList<String>(
//                Arrays.asList(
//                        "Siid"
//                )
//        );

        Response response = ApiEngineEndpoint.postGraphql(String.format(query, startTime, endTime));

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));

        ArrayList<HashMap> data = jsonPathEvaluator.get("data.SINAMICS_300_Log");
        Assert.assertTrue(data.size() > 1);

        assertThat(response.getBody().asString(),
                matchesJsonSchemaInClasspath("iot-device-error-alarm.json"));


    }



    @Test(groups = "iot", description = "Test api engine interface: Query one device history data page by graphql.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with graphql and verify if correct return.")
    @Story("Query one iot device history data page by graphql")
    public void queryOneIOTDeviceHistoryDataPageByGraphQL() {
        Reporter.log("Send request to graphql api with graphql");

        String query = "{\n" +
                "\tSINAMICS_300_LogConnection(cond: \"{update_time:{_gte:\\\"%s\\\", _lte: \\\"%s\\\"}, port:{_eq:6}, sinamics_300:{_eq:\\\"ddd19ed6-7fb5-4ab0-972a-75a8657a30de\\\"}}\", order: \"update_time DESC\", after: \"0\", first: 5) {\n" +
                "\t  totalElements\n" +
                "\t  totalPages\n" +
                "\t  pageSize\n" +
                "\t  page\n" +
                "\t  numberOfElements\n" +
                "\t  edges {\n" +
                "\t\tnode {\n" +
                "\t\t  outputcurrent_actual_AI0\n" +
                "\t\t  Braking_status_word\n" +
                "\t\t  input_current_actual_AI0\n" +
                "\t\t  input_current_actual_AI1\n" +
                "\t\t  Act_filtered_DC_link_volt\n" +
                "\t\t  Safely_remove_mem_card_status\n" +
                "\t\t  Actual_speed_smoothed\n" +
                "\t\t  Motor_utilization_thermal\n" +
                "\t\t  Number_of_drive_objects\n" +
                "\t\t  id\n" +
                "\t\t  port1_CO_motor_temperature\n" +
                "\t\t  Fault_code_6\n" +
                "\t\t  sinamics_300\n" +
                "\t\t  Fault_code_5\n" +
                "\t\t  Fault_code_8\n" +
                "\t\t  Fault_code_7\n" +
                "\t\t  Status_word_1\n" +
                "\t\t  Pulse_frequency_Mod_min_value\n" +
                "\t\t  Status_word_2\n" +
                "\t\t  Digital_in_status_inverted\n" +
                "\t\t  CO_Act_filtered_current_Isq\n" +
                "\t\t  Status_word_faults_alarms_2\n" +
                "\t\t  Energy_consumpt_meter\n" +
                "\t\t  Status_word_faults_alarms_1\n" +
                "\t\t  Converter_state\n" +
                "\t\t  Device_type_identification\n" +
                "\t\t  Device_Firmware_version\n" +
                "\t\t  Analog_inputs_in_percent_AI1\n" +
                "\t\t  Missing_enable_sig\n" +
                "\t\t  unit_line_supply_voltage\n" +
                "\t\t  Speed_setpoint_smoothed\n" +
                "\t\t  port\n" +
                "\t\t  Analog_inputs_in_percent_AI0\n" +
                "\t\t  Firmware_date_year\n" +
                "\t\t  Firmware_patch_hot_fix\n" +
                "\t\t  terminal_actual_value\n" +
                "\t\t  CO_Act_motor_temperature\n" +
                "\t\t  S6_load_duty_cycle\n" +
                "\t\t  Company_IDDeviceidentification\n" +
                "\t\t  Rated_converter_power\n" +
                "\t\t  Rated_converter_current\n" +
                "\t\t  Firmware_date_day_month\n" +
                "\t\t  Abs_actual_current_smoothed\n" +
                "\t\t  ZSW_seq_ctrlStatuswordsequence\n" +
                "\t\t  S1_cont_duty_cycle\n" +
                "\t\t  update_time\n" +
                "\t\t  Energy_consumption_saved\n" +
                "\t\t  CU_Firmware_version\n" +
                "\t\t  Freq_Setpoint_before_RFG\n" +
                "\t\t  Digital_out_status\n" +
                "\t\t  Pulse_frequency_Actual\n" +
                "\t\t  Fault_code_2\n" +
                "\t\t  Fault_code_1\n" +
                "\t\t  Act_filtered_output_voltage\n" +
                "\t\t  Fault_code_4\n" +
                "\t\t  Fault_code_3\n" +
                "\t\t  Act_filtered_frequency\n" +
                "\t\t  Act_filtered_rotor_speed\n" +
                "\t\t  Digital_in_status\n" +
                "\t\t  Act_filtered_torque\n" +
                "\t\t  output_current_actual_AI1\n" +
                "\t\t  Actual_alarm_code\n" +
                "\t\t  Act_filtered_powerDisplays\n" +
                "\t\t  Rated_motor_current\n" +
                "\t\t  CO_Converter_temperature\n" +
                "\t\t  Control_word_faults_alarms\n" +
                "\t\t  Actual_fault_code\n" +
                "\t\t  Control_word_1\n" +
                "\t\t}\n" +
                "\t\tcursor\n" +
                "\t  }\n" +
                "\t  pageInfo {\n" +
                "\t\tendCursor\n" +
                "\t\thasNextPage\n" +
                "\t  }\n" +
                "\t}\n" +
                "}";

        long startTime = System.currentTimeMillis() - 60*60*1000;
        long endTime = System.currentTimeMillis() - 55*60*1000;

//        List<String> l = new ArrayList<String>(
//                Arrays.asList(
//                        "Siid"
//                )
//        );

        Response response = ApiEngineEndpoint.postGraphql(String.format(query, startTime, endTime));

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals("Successfully", rspBody.getMessage());
        Assert.assertEquals(100000, rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data.SINAMICS_300_LogConnection"));

        HashMap data = jsonPathEvaluator.get("data.SINAMICS_300_LogConnection");
        Assert.assertTrue(Integer.parseInt(data.get("totalElements").toString()) > 1);
        Assert.assertTrue(Integer.parseInt(data.get("totalPages").toString()) > 1);
        Assert.assertTrue(Integer.parseInt(data.get("pageSize").toString()) == 5);
        Assert.assertTrue(Integer.parseInt(data.get("numberOfElements").toString()) == 5);
        Assert.assertTrue(((List)(data.get("edges"))).size() == 5);

        assertThat(response.getBody().asString(),
                matchesJsonSchemaInClasspath("iot-device-history-data-page.json"));


    }



    @Test(groups = "iot", description = "Test api engine interface: Query one device one type one day history data page by graphql.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with graphql and verify if correct return.")
    @Story("Query one device one type one day history data page by graphql")
    public void queryOneIOTDeviceOneDayDataWithTypeByGraphQL() {
        Reporter.log("Send request to graphql api with graphql");

        String query = "{\n" +
                "  SINAMICS_300_LogConnection(cond: \"{update_time:{_gte:\\\"%s\\\", _lte: \\\"%s\\\"}, port:{_eq:6}, sinamics_300:{_eq:\\\"ddd19ed6-7fb5-4ab0-972a-75a8657a30de\\\"}}\", order: \"update_time DESC\", after: \"0\", first: 1000) {\n" +
                "\ttotalElements\n" +
                "\ttotalPages\n" +
                "\tpageSize\n" +
                "\tpage\n" +
                "\tnumberOfElements\n" +
                "\tedges {\n" +
                "\t  node {\n" +
                "\t\tupdate_time %s\n" +
                "\t  }\n" +
                "\t  cursor\n" +
                "\t}\n" +
                "\tpageInfo {\n" +
                "\t  endCursor\n" +
                "\t  hasNextPage\n" +
                "\t}\n" +
                "  }\n" +
                "}";

        long startTime = System.currentTimeMillis() - 24*60*60*1000;
        long endTime = System.currentTimeMillis();

        List<String> l = new ArrayList<String>(
                Arrays.asList(
                        "Act_filtered_rotor_speed Act_filtered_torque",
                        "Act_filtered_DC_link_volt Rated_motor_current",
                        "Act_filtered_DC_link_volt",
                        "Rated_converter_current",
                        "Rated_motor_current",
                        "Converter_state",
                        "CO_Converter_temperature",
                        "CO_Act_motor_temperature",
                        "Actual_speed_smoothed"
                )
        );

        for (String k:l
             ) {

            Response response = ApiEngineEndpoint.postGraphql(String.format(query, startTime, endTime, k));

            Reporter.log("Response status is " + response.getStatusCode());

            Reporter.log("Response Body is =>  " + response.getBody().asString());

            GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

            Assert.assertEquals("Successfully", rspBody.getMessage());
            Assert.assertEquals(100000, rspBody.getCode());

            JsonPath jsonPathEvaluator = response.jsonPath();

            Assert.assertNotNull(jsonPathEvaluator.get("data.SINAMICS_300_LogConnection"));

            HashMap data = jsonPathEvaluator.get("data.SINAMICS_300_LogConnection");
            Assert.assertTrue(Integer.parseInt(data.get("totalElements").toString()) > 1);
            Assert.assertTrue(Integer.parseInt(data.get("totalPages").toString()) >= 1);
            Assert.assertTrue(Integer.parseInt(data.get("pageSize").toString()) == 1000);
            Assert.assertTrue(Integer.parseInt(data.get("numberOfElements").toString()) > 1);
            Assert.assertTrue(((List)(data.get("edges"))).size() > 5);

            List<HashMap> values = jsonPathEvaluator.getList("data.SINAMICS_300_LogConnection.edges.node");
            for (HashMap m: values
                 ) {
                Assert.assertTrue(m.values()
                        .stream()
                        .allMatch(Objects::nonNull));
            }
        }

    }



    @Test ( priority = 0,
            description = "Test Api-engine Query Endpoint: GraphQL interface",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with graphql and verify if correct return.")
    @Story("Query product order by graphql")
    public void queryProductOrderByGraphQL(Map<String, String> paramMaps) {
        Reporter.log("Send request to graphql api with graphql");

        String query = paramMaps.get("query");


        Response response = ApiEngineEndpoint.postGraphql(query);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals(ResponseCode.SDL_SUCCESS.getMessage(), rspBody.getMessage());
        Assert.assertEquals(ResponseCode.SDL_SUCCESS.getCode(), rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get(paramMaps.get("jsonpath")));

        checkDataFollowsModelSchema("json-model-schema/snc/" + paramMaps.get("schema"), response);


    }

    public void checkDataFollowsModelSchema(String schemaName, Response response)
    {
        CommonCheckFunctions.verifyIfDataMatchesJsonSchemaTemplate(schemaName, response.getBody().asString());
    }


    @Test ( priority = 0,
            description = "Test Api-engine Query Endpoint: GraphQL interface",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with graphql and verify if correct return.")
    @Story("Query work center by graphql")
    public void queryWorkCenterByGraphQL(Map<String, String> paramMaps) {
        Reporter.log("Send request to graphql api with graphql");

        String query = paramMaps.get("query");

        Response response = ApiEngineEndpoint.postGraphql(query);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals(ResponseCode.SDL_SUCCESS.getMessage(), rspBody.getMessage());
        Assert.assertEquals(ResponseCode.SDL_SUCCESS.getCode(), rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get(paramMaps.get("jsonpath")));

        checkDataFollowsModelSchema("json-model-schema/snc/" + paramMaps.get("schema"), response);


    }


    @Test(groups = "snc", description = "Test api engine interface: Query product by graphql.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with graphql and verify if correct return.")
    @Story("Query product by graphql")
    public void queryProductByGraphQL() {
        Reporter.log("Send request to graphql api with graphql");

        String query = "{\n" +
                "    Product {\n" +
                "        product_no\n" +
                "    }\n" +
                "}";


        Response response = ApiEngineEndpoint.postGraphql(query);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals(ResponseCode.SDL_SUCCESS.getMessage(), rspBody.getMessage());
        Assert.assertEquals(ResponseCode.SDL_SUCCESS.getCode(), rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data.Product"));

        ArrayList data = jsonPathEvaluator.get("data.Product");
        Assert.assertTrue(data.size() > 1);

        assertThat(response.getBody().asString(),
                matchesJsonSchemaInClasspath("snc-product.json"));


    }


    @Test ( priority = 0,
            description = "Test Api-engine Query Endpoint: GraphQL interface",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with graphql and verify if correct return.")
    @Story("Query material by graphql")
    public void queryMaterialByGraphQL(Map<String, String> paramMaps) {
        Reporter.log("Send request to graphql api with graphql");

        String query = paramMaps.get("query");

        Response response = ApiEngineEndpoint.postGraphql(query);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals(ResponseCode.SDL_SUCCESS.getMessage(), rspBody.getMessage());
        Assert.assertEquals(ResponseCode.SDL_SUCCESS.getCode(), rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get(paramMaps.get("jsonpath")));

        checkDataFollowsModelSchema("json-model-schema/snc/" + paramMaps.get("schema"), response);

    }


    @Test ( priority = 0,
            description = "Test Api-engine Query Endpoint: GraphQL interface",
            dataProvider = "api-engine-test-data-provider",
            dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Send a request to SUT with graphql and verify if correct return.")
    @Story("Query preactor order by graphql")
    public void queryPreactorOrderByGraphQL(Map<String, String> paramMaps) {
        Reporter.log("Send request to graphql api with graphql");

        String query = paramMaps.get("query");

        Response response = ApiEngineEndpoint.postGraphql(query);

        Reporter.log("Response status is " + response.getStatusCode());

        Reporter.log("Response Body is =>  " + response.getBody().asString());

        GraphqlApiResponse rspBody = response.getBody().as(GraphqlApiResponse.class);

        Assert.assertEquals(ResponseCode.SDL_SUCCESS.getMessage(), rspBody.getMessage());
        Assert.assertEquals(ResponseCode.SDL_SUCCESS.getCode(), rspBody.getCode());

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get(paramMaps.get("jsonpath")));

        checkDataFollowsModelSchema("json-model-schema/snc/" + paramMaps.get("schema"), response);


    }



}
