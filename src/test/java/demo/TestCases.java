package demo;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import java.time.Duration;
import java.util.logging.Level;

public class TestCases {
    ChromeDriver driver;

    /*
     * TODO: Write your tests here with testng @Test annotation.
     * Follow testCase01 testCase02... format or what is provided in
     * instructions
     */
    SoftAssert softAssert = new SoftAssert();

    /*
     * Do not change the provided methods unless necessary, they will help in
     * automation and assessment
     */
    @BeforeTest
    public void startBrowser() {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        // NOT NEEDED FOR SELENIUM MANAGER
        // WebDriverManager.chromedriver().timeout(30).setup();

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log");

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();

        driver.get("https://mitra-leader.vahan.co");
        // SoftAssert softAssert = new SoftAssert();
    }

    @Test
    public void TC01_Login() throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Actions actions = new Actions(driver);

        System.out.println("Current URL is : " + driver.getCurrentUrl());
        Assert.assertTrue(driver.getCurrentUrl().equals("https://mitra-leader.vahan.co/"), "URL Not valid");
        Thread.sleep(2000);
        WebElement mobileNumber = driver.findElement(By.xpath("//*[@id=\"enter-mobile\"]"));
        mobileNumber.sendKeys("1234");

        WebElement getOtpButton = wait
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text() ='Get OTP']")));
        actions.moveToElement(getOtpButton).click().perform();
        Thread.sleep(2000);
        WebElement errMsg = driver.findElement(By.xpath("//h5[text() ='This number is not registered']"));

        if (errMsg.isDisplayed()) {
            System.out.println("✅ Error message displayed: 1234 " + errMsg.getText());
        } else {
            System.out.println("❌ Error message NOT displayed.");
        }
        softAssert.assertTrue(errMsg.isDisplayed(), "Element is not displayed!");

        // driver.navigate().refresh();
        Thread.sleep(500);

        mobileNumber.sendKeys(" 11");
        for (int i = 0; i < 7; i++) { // Assuming max length of 20
            mobileNumber.sendKeys(Keys.BACK_SPACE);
        }

        mobileNumber.sendKeys("7560868044");

        actions.moveToElement(getOtpButton).click().perform();

        boolean otpTextPresent = driver.findElement(By.xpath("//h5[@class ='OtpMessage']")).isDisplayed();

        softAssert.assertTrue(otpTextPresent, "OTP Text is not displayed");

        WebElement enterOTP = driver.findElement(By.id("enter-otp"));
        enterOTP.sendKeys("1234");

        WebElement loginButton = wait
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text() ='LOG IN']")));
        actions.moveToElement(loginButton).click().perform();

        Thread.sleep(500);
        boolean errMsgWrngOTP = driver.findElement(By.xpath("//h5[@class ='errorMsg']")).isDisplayed();
        System.out.println(errMsgWrngOTP);

        softAssert.assertTrue(errMsgWrngOTP, "Error msg not present");

        Thread.sleep(500);
        enterOTP.sendKeys("11");
        for (int i = 0; i < 7; i++) {
            enterOTP.sendKeys(Keys.BACK_SPACE);
        }
        enterOTP.sendKeys("8044");
        actions.moveToElement(loginButton).click().perform();

        Thread.sleep(500);
        System.out.println("Current url is: " + driver.getCurrentUrl());

        Assert.assertTrue(driver.getCurrentUrl().contains("/home"), "User has not redirected to the Home page");

    }

    @Test
    public void TC02_Home_Page() throws InterruptedException {

        System.out.println("TestCase 01 Starting..");
        // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Thread.sleep(200);
        System.out.println("Current Page URL: " + driver.getCurrentUrl());
        Assert.assertTrue(driver.getCurrentUrl().contains("/home"), "User is NOT on Home Page");
        
    }



    @AfterTest
    public void endTest() {
        softAssert.assertAll();
        // driver.close();
        // driver.quit();

    }
}

