package demo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
import org.testng.asserts.SoftAssert;
import demo.wrappers.Wrappers;
import io.github.bonigarcia.wdm.WebDriverManager;
import demo.LoginUtils;
import java.time.Duration;
import java.util.logging.Level;

public class BaseTest {
    ChromeDriver driver;
    Wrappers wrappers;
    LoginUtils loginUtils;
    SoftAssert softAssert = new SoftAssert();

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
        loginUtils = new LoginUtils(driver);

    }

    @AfterTest
    public void endTest() {
        softAssert.assertAll();
        driver.close();
        driver.quit();
    }
} 