package demo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.io.FileInputStream;
import demo.wrappers.Wrappers;
import demo.HomePageTests;
import org.testng.annotations.AfterTest;
import demo.LoginUtils;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;

import java.util.logging.Level;

public class JobsPage {
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

        wrappers = new Wrappers(driver, Duration.ofSeconds(10));
    }

    @Test (priority = 1)
    public void testLogin() throws InterruptedException {
        loginUtils.login("7560868044", "8044");
        Thread.sleep(1000);
        Assert.assertTrue(driver.getCurrentUrl().contains("/home"), "Login failed, not redirected to home page");
    }

    @Test (priority = 2)
    public void checkJobsPageUrl() throws InterruptedException {
        driver.get("https://mitra-leader.vahan.co/job-demands/job-requirements");
        Assert.assertTrue(driver.getCurrentUrl().equals("https://mitra-leader.vahan.co/job-demands/job-requirements"), "URL Not valid");
    }

    @AfterTest
    public void endTest() {
        softAssert.assertAll();
        driver.close();
        driver.quit();
    }
}
