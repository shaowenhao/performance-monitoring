package com.siemens.datalayer.selenium.snc;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.siemens.datalayer.utils.AllureEnvironmentPropertiesWriter;
import com.siemens.datalayer.utils.CustomizedTestListener;
import com.siemens.datalayer.utils.Utils;
import com.siemens.datalayer.utils.WebDriverBaseClass;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;

//@Listeners({TestngListener.class})
@Listeners({CustomizedTestListener.class})
public class SNCTest extends WebDriverBaseClass {

    public WebDriver driver;
    private String baseUrl;
    private int uiPort;
    private int apiPort;
    private String uiBaseUrl;

    @BeforeClass
    public void setup() {
        WebDriverBaseClass bs = new WebDriverBaseClass();
        driver = bs.initialize_driver();
        baseUrl = "http://140.231.89.85";
        uiPort = 32189;
        uiBaseUrl = baseUrl + String.format(":%s/", String.valueOf(uiPort));
        apiPort = 31918;

        driver.get(uiBaseUrl);
        driver.manage().window().maximize();

        Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
        AllureEnvironmentPropertiesWriter.addEnvironmentItem("Web Browser", caps.getBrowserName() + "driver " + caps.getVersion());
    }

    @Test(description = "Verify the user login function")
    @Description("Verify user login process")
    @Epic("SNC UI")
    @Feature("Feature2: Login")
    @Step("Check the Login function with correct password")
    @Severity(SeverityLevel.CRITICAL)
    public void loginTest() {
    	this.login();
        String text = driver.findElement(By.cssSelector(".title")).getText();

        Assert.assertEquals(text, "SNC MEtiS DASHBOARD");
    }

    @Test(description = "Verify the page1 function")
    @Description("Verify the page1 function")
    @Epic("SNC UI")
    @Feature("Feature2: Page1")
    @Step("Check page1 values")
    @Severity(SeverityLevel.CRITICAL)
    public void page1UITest() {
        this.loginAndCheckSuccess();
        this.switchToProductSummary();
        driver.switchTo().frame(0);
        String totalTheDay = driver.findElement(By.id("piecol2")).getText();
        String planTheDay = driver.findElement(By.id("piecol3")).getText();
        String pattern = "\\d+";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(totalTheDay);
        Assert.assertTrue(m.find());
        Matcher m2 = r.matcher(planTheDay);
        Assert.assertTrue(m2.find());

        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"uc\"]/div")));
        String text = driver.findElement(By.id("uc")).getText();
        Assert.assertTrue(text.length() > 10);
    }


    @Test(description = "Verify the page2 function")
    @Description("Verify the page2 function")
    @Epic("SNC UI")
    @Feature("Feature2: Page2")
    @Step("Check page2 values")
    @Severity(SeverityLevel.CRITICAL)
    public void page2UITest() {
        this.loginAndCheckSuccess();
        this.switchToProductPlan();
        driver.switchTo().frame(0);
        waitForAjaxToFinish();
        String actual = driver.findElement(By.id("opproduce")).getText();
        String plan = driver.findElement(By.id("opplan")).getText();
        String pattern = "\\d+";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(actual);
        Assert.assertTrue(countMatchSize(m) == 2);
        Matcher m2 = r.matcher(plan);
        Assert.assertTrue(countMatchSize(m2) == 2);

        String text = driver.findElement(By.id("pp")).getText();
        Assert.assertTrue(text.length() > 10);

//        WebElement canvas = driver.findElement(By.cssSelector("#pd canvas"));
//        Actions builder = new Actions(driver);
//        builder.moveToElement(canvas,652,250)  // start point
//                .click()
//                .perform();
//        System.out.println("finish");
    }


//    @Test(description = "Verify the page3 function")
//    @Description("Verify the page3 function")
//    @Epic("SNC UI")
//    @Feature("Feature2: Page3")
//    @Step("Check page3 values")
//    @Severity(SeverityLevel.CRITICAL)
//    public void page3UITest() {
//        this.loginAndCheckSuccess();
//        this.switchToProductDevice();
//        driver.switchTo().frame(0);
//        waitForAjaxToFinish();
//        String actual = driver.findElement(By.id("opproduce")).getText();
//        String plan = driver.findElement(By.id("opplan")).getText();
//        String pattern = "\\d+";
//
//        Pattern r = Pattern.compile(pattern);
//        Matcher m = r.matcher(actual);
//        Assert.assertTrue(countMatchSize(m) == 2);
//        Matcher m2 = r.matcher(plan);
//        Assert.assertTrue(countMatchSize(m2) == 2);
//
//        String text = driver.findElement(By.id("pp")).getText();
//        Assert.assertTrue(text.length() > 10);
//
//        WebElement canvas = driver.findElement(By.cssSelector("#pd canvas"));
//        Actions builder = new Actions(driver);
//        builder.moveToElement(canvas,652,250)  // start point
//                .click()
//                .perform();
//        System.out.println("finish");
//    }

    @Test(description = "Verify the apis used in page1 function")
    @Description("Verify the apis used inpage1 function")
    @Epic("EP001 Home Page Layout")
    @Feature("Feature2: Page1")
    @Step("Check apis used in page1")
    @Severity(SeverityLevel.CRITICAL)
    public void page1ApiTest() {
        this.verifySNCDemoBackendOfPage1();
    }


    @Test(description = "Verify the apis used in page3 function")
    @Description("Verify the apis used in page3 function")
    @Epic("EP001 Home Page Layout")
    @Feature("Feature2: Page3")
    @Step("Check apis used in page3")
    @Severity(SeverityLevel.CRITICAL)
    public void page3ApiTest() {
        this.verifySNCDemoBackendOfPage3();
    }


    @Test(description = "Verify the apis used in page3 function")
    @Description("Verify the apis used in page3 function")
    @Epic("EP001 Home Page Layout")
    @Feature("Feature2: Page3")
    @Step("Check apis used in page3")
    @Severity(SeverityLevel.CRITICAL)
    public void page4ApiTest() {
        this.verifySNCDemoBackendOfPage4();
    }

    private int countMatchSize(Matcher m){
        List<String> allMatches = new ArrayList<String>();
        while (m.find()) {
            allMatches.add(m.group());
        }
        return allMatches.size();
    }

    public Response sncDemoGraphQLControllerApi(String body) {

        RestAssured.baseURI = baseUrl;
        RestAssured.port = apiPort;

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.header("Content-Type", "text/plain")
                .body(body)
                .post("/graphqlController");

        return response;
    }

    public Response sncDemoGraphQLApi(String body) {

        RestAssured.baseURI = baseUrl;
        RestAssured.port = uiPort;

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.header("Content-Type", "text/plain")
                .body(body)
                .post("/graphql");

        return response;
    }

    public Response sncDemoStatisticApi() {

        RestAssured.baseURI = baseUrl;
        RestAssured.port = uiPort;

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.header("Content-Type", "application/json")
                .get("/snc/devices/statistic");

        return response;
    }

    public Response sncDemoAbnormalApi() {

        RestAssured.baseURI = baseUrl;
        RestAssured.port = uiPort;

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.header("Content-Type", "application/json")
                .get("/snc/devices/abnormal");

        return response;
    }


    public void verifySNCDemoBackendOfPage3(){
        this.verifySNCGraphQLApi();
        this.verifySNCStatisticApi();
        this.verifySNCAbnormalApi();
    }

    public void verifySNCDemoBackendOfPage4(){
        List<String> ids = this.getAllDeviceIds();
        for(String id:ids){
            this.verifySNCGraphQLApiWithDeviceRelation(id);
            this.verifySNCGraphQLApiWithDeviceDetail(id);
        }
    }


    public void verifySNCGraphQLApiWithDeviceRelation(String id){
        String body = "{\n" +
                "\tDevice(cond: \"{id:{_eq: %s}}\", order: \"\") {\n" +
                "\t\tid Connect_To_Work_Position(cond: \"\", order: \"\") {\n" +
                "\n" +
                "\t\t\tinvert_Work_Center(cond: \"\", order: \"\") {\n" +
                "\t\t\t\tname description location id Has_Preactor_OrderConnection(cond: \"\", order: \"start_time DESC\", after: \"0\", first: 1) {\n" +
                "\t\t\t\t\ttotalElements totalPages pageSize page numberOfElements edges {\n" +
                "\t\t\t\t\t\tnode {\n" +
                "\t\t\t\t\t\t\torder_no start_time product work_center comments end_time plan_quantity id Has_Product(cond: \"\", order: \"\") {\n" +
                "\t\t\t\t\t\t\t\tproduct_no name product_type\n" +
                "\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t\tinvert_Product_Order_Process(cond: \"\", order: \"\") {\n" +
                "\t\t\t\t\t\t\t\tproduct produce_quantity pass_quantity mach_type product_order event_time status\n" +
                "\t\t\t\t\t\t\t}\n" +
                "\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\tcursor\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t\tpageInfo {\n" +
                "\t\t\t\t\t\tendCursor hasNextPage\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t}\n" +
                "\n" +
                "\t\t\t}\n" +
                "\t\t\tinvert_Production_Procedure(cond: \"\", order: \"\") {\n" +
                "\t\t\t\tmach_type\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t}\n" +
                "\n" +
                "\t}\n" +
                "}";
        Response response = this.sncDemoGraphQLApi(String.format(body, id));
        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));
        ArrayList<HashMap> data = (ArrayList)jsonPathEvaluator.get("data.Device");

        Assert.assertEquals(1, data.size());
//        assertThat(response.getBody().asString(),
//                matchesJsonSchemaInClasspath("snc-graphql-device-relation-schema.json"));
    }


    public void verifySNCGraphQLApiWithDeviceDetail(String id){
        String body = "{\n" +
                "  Device(cond: \"{id:{_eq:%s}}\") {\n" +
                "\trating\n" +
                "\tweight\n" +
                "\tip\n" +
                "\tframe\n" +
                "\tserial_no\n" +
                "\teff_grade\n" +
                "\tins\n" +
                "\tdesign\n" +
                "\trpm\n" +
                "\tid\n" +
                "\tpoles\n" +
                "\tvolts\n" +
                "\telectric_current\n" +
                "\ttype\n" +
                "\teff\n" +
                "\tsinamics_300\n" +
                "\tplant\n" +
                "\thi\n" +
                "\tamps\n" +
                "\tmanufacturer\n" +
                "\tname\n" +
                "\tbearings\n" +
                "\tsinamics_300_port\n" +
                "\toutput\n" +
                "\tmodel\n" +
                "\tsf\n" +
                "  }\n" +
                "}";
        Response response = this.sncDemoGraphQLApi(String.format(body, id));
        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));
        ArrayList<HashMap> data = (ArrayList)jsonPathEvaluator.get("data.Device");

        Assert.assertEquals(1, data.size());
        assertThat(response.getBody().asString(),
                matchesJsonSchemaInClasspath("snc-graphql-device-schema.json"));
    }

    public void verifySNCGraphQLApi(){
        Response response = this.queryAllDevices();
        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));
        ArrayList<HashMap> data = (ArrayList)jsonPathEvaluator.get("data.Work_Center");

        Assert.assertEquals(3, data.size());
        assertThat(response.getBody().asString(),
                matchesJsonSchemaInClasspath("snc-graphql-schema.json"));
    }

    public ArrayList getAllDeviceIds(){
        Response response = this.queryAllDevices();
        JsonPath jsonPathEvaluator = response.jsonPath();

        ArrayList a = (ArrayList) jsonPathEvaluator.get("data.Work_Center.Contains_Work_Position.invert_Device.invert_KPI.device_id");
        return (ArrayList) Utils.flatten(a);
//        for(HashMap m : data){
//            m.get("Contains_Work_Position")
//        }
    }

    public Response queryAllDevices(){
        String body = "{\n" +
                "  Work_Center(cond: \"\", order: \"\") {\n" +
                "\tname\n" +
                "\tdescription\n" +
                "\tlocation\n" +
                "\tid\n" +
                "\tContains_Work_Position(cond: \"\", order: \"\") {\n" +
                "\t  device_id\n" +
                "\t  procedure_id\n" +
                "\t  name\n" +
                "\t  description\n" +
                "\t  work_center_id\n" +
                "\t  location\n" +
                "\t  invert_Device(cond: \"\", order: \"\") {\n" +
                "\t\tid\n" +
                "\t\tname\n" +
                "\t\tinvert_KPI {\n" +
                "\t\t  val\n" +
                "\t\t  type\n" +
                "\t\t  unit\n" +
                "\t\t  device_id\n" +
                "\t\t  start_time\n" +
                "\t\t  end_time\n" +
                "\t\t}\n" +
                "\t  }\n" +
                "\t}\n" +
                "  }\n" +
                "}";
        return this.sncDemoGraphQLApi(body);
    }

    public void verifySNCStatisticApi(){
        Response response = this.sncDemoStatisticApi();
        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));
        ArrayList<HashMap> data = (ArrayList)jsonPathEvaluator.get("data");

        Assert.assertTrue(data.size() > 0);
        assertThat(response.getBody().asString(),
                matchesJsonSchemaInClasspath("snc-statistic-schema.json"));
    }


    public void verifySNCAbnormalApi(){
        Response response = this.sncDemoAbnormalApi();
        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertNotNull(jsonPathEvaluator.get("data"));
        ArrayList<HashMap> data = (ArrayList)jsonPathEvaluator.get("data");

//        Assert.assertTrue(data.size() > 0);
        assertThat(response.getBody().asString(),
                matchesJsonSchemaInClasspath("snc-abnormal-schema.json"));
    }

    public void verifySNCDemoBackendOfPage1(){
        List<String> l = new ArrayList<>(
                Arrays.asList(
                        "{materialsUsed(DateTimeOverview:\"2018-03-29\"){ProduceQty PassQty MachType RequiredMaterials{RequiredPartNo RequiredQuantity Description ItemClass}}}",
                        "{productionOverview(DateTimeOverview:\"2018-03-29\"){ProductionOrder}}",
                        "{schedulingResult(DateTimeOverview:\"2018-03-29\"){WorkCenter}fmiResult(MachType:\"MPM******\",TestResult:\"F\",DateTimeOverview:\"2018-03-29\"){FID ProductionNo FmiTests{MachTime DateTimeOverview}}}",
                        "{materialsUsed(DateTimeOverview:\"2018-03-29\"){ProductionOrder DateTimeOverview ProduceQty PassQty  RequiredMaterials{RequiredPartNo Description ItemClass RequiredQuantity}}}",
                        "{productionOverview(DateTimeOverview:\"2018-03-29\"){ProductionNo}}",
                        "{fmiResult(MachType:\"MPM******\",TestResult:\"P\",DateTimeOverview:\"2018-03-29\"){FID ProductionNo ProductionOrder FmiTests{MachType ClockTime TestResult MachTime DateTimeOverview}}}",
                        "{productionOverview(DateTimeOverview:\"2018-03-29\", MachType:\"MPM******\"){PassQty PlanQty}}",
                        "{productionDetail(DateTimeOverview:\"2018-03-29\", WorkCenter:\"20015\") {ProductionOrder ProductionNo WorkCenter PlanQTY ProductionPassDetailResults{MachType MachTimeMin MachTimeMax PassQTY DateTimeOverview}}}",
                        "{productionOverview(DateTimeOverview:\"2018-03-29\", MachType:\"MPM******\"){PassQty ProduceQty}}",
                        "{schedulingResult(DateTimeOverview:\"2018-03-29\"){ProductionNo ProductionOrder WorkCenter StartSetupTime StartTime EndTime PlanQTY}}",
                        "{schedulingResult(DateTimeOverview:\"2018-03-29\"){WorkCenter}}",
                        "{schedulingResult(DateTimeOverview:\"2018-03-29\"){ProductionNo WorkCenter DateTimeOverview StartTime EndTime PlanQTY}}",
                        "{materialsUsed(DateTimeOverview:\"2018-03-29\", ProductionOrder:\"800014035899\"){ProduceQty PassQty}}"
                )
        );
        List<String> l2 = new ArrayList<>(
                Arrays.asList(
                        "materialsUsed",
                        "productionOverview",
                        "schedulingResult",
                        "materialsUsed",
                        "productionOverview",
                        "fmiResult",
                        "productionOverview",
                        "productionDetail",
                        "productionOverview",
                        "schedulingResult",
                        "schedulingResult",
                        "schedulingResult",
                        "materialsUsed"
                )
        );
        for (int i = 0; i < l.size(); i++) {
            verifySNCDemoBackendReturnValue(l.get(i), l2.get(i));
        }
    }

    public void verifySNCDemoBackendReturnValue(String body, String key){
        Response response = this.sncDemoGraphQLControllerApi(body);

        JsonPath jsonPathEvaluator = response.jsonPath();

        Assert.assertTrue(Utils.isNullOrEmpty(jsonPathEvaluator.getList("errors")));
        Assert.assertNull(jsonPathEvaluator.get("extensions"));
        Assert.assertNotNull(jsonPathEvaluator.get("data"));

        ArrayList<HashMap> data = jsonPathEvaluator.get(String.format("data.%s", key));
        Assert.assertTrue(data.size()>0);
    }

    private void loginAndCheckSuccess(){
        this.login();
        String text = driver.findElement(By.cssSelector(".title")).getText();
        Assert.assertEquals(text, "SNC MEtiS DASHBOARD");
    }

    private void login(){
        this.login("admin", "ng-alain.com");
    }

    private void login(String user, String pwd){
        driver.get(this.uiBaseUrl + "/#/passport/login");
        driver.findElement(By.xpath("//input")).sendKeys(user);
        driver.findElement(By.xpath("//input[@type='password']")).sendKeys(pwd);
        driver.findElement(By.cssSelector(".ant-btn")).click();
    }

    private void switchToProductSummary(){
        driver.switchTo().defaultContent();
        driver.findElement(By.id("product")).click();
    }

    private void switchToProductPlan(){
        driver.switchTo().defaultContent();
        driver.findElement(By.id("process")).click();
    }

    private void switchToProductDevice(){
        driver.switchTo().defaultContent();
        driver.findElement(By.id("product-device")).click();
    }

    public void waitForAjaxToFinish() {

        WebDriverWait wait = new WebDriverWait(driver, 5000);
        wait.until(new ExpectedCondition<Boolean>() {

            public Boolean apply(WebDriver wdriver) {
                return ((JavascriptExecutor) driver).executeScript("return jQuery.active == 0").equals(true);
            }

        });
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}
