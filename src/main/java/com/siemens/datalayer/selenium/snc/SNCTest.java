package com.siemens.datalayer.selenium.snc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.siemens.datalayer.utils.AllureEnvironmentPropertiesWriter;
import com.siemens.datalayer.utils.CustomizedTestListener;
import com.siemens.datalayer.utils.Utils;
import com.siemens.datalayer.utils.WebDriverBaseClass;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

//@Listeners({TestngListener.class})
@Listeners({CustomizedTestListener.class})
public class SNCTest extends WebDriverBaseClass {

    public WebDriver driver;
    private String baseUrl;
    private int apiPort;
    private String uiBaseUrl;

    @BeforeClass
    public void setup() {
        WebDriverBaseClass bs = new WebDriverBaseClass();
        driver = bs.initialize_driver();
        baseUrl = "http://140.231.89.85";
        uiBaseUrl = baseUrl + ":32189/";
        apiPort = 31918;

        driver.get(uiBaseUrl);
        driver.manage().window().maximize();

        Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
        AllureEnvironmentPropertiesWriter.addEnvironmentItem("Web Browser", caps.getBrowserName() + "driver " + caps.getVersion());
    }

    @Test(description = "Verify the user login function")
    @Description("Verify user login process")
    @Epic("EP001 Home Page Layout")
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
    @Epic("EP001 Home Page Layout")
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
    @Epic("EP001 Home Page Layout")
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
    }

    @Test(description = "Verify the apis used in page1 function")
    @Description("Verify the apis used inpage1 function")
    @Epic("EP001 Home Page Layout")
    @Feature("Feature2: Page1")
    @Step("Check apis used in page1")
    @Severity(SeverityLevel.CRITICAL)
    public void page1ApiTest() {
        this.verifySNCDemoBackendOfPage1();
    }

    private int countMatchSize(Matcher m){
        List<String> allMatches = new ArrayList<String>();
        while (m.find()) {
            allMatches.add(m.group());
        }
        return allMatches.size();
    }

    public Response sncDemoBackend(String body) {

        RestAssured.baseURI = baseUrl;
        RestAssured.port = apiPort;

        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.header("Content-Type", "text/plain")
                .body(body)
                .post("/graphqlController");

        return response;
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
        Response response = this.sncDemoBackend(body);

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
