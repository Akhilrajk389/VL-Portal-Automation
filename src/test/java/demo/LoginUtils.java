package demo;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import demo.wrappers.Wrappers;

import org.openqa.selenium.Keys;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;

public class LoginUtils {
    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions;
    Wrappers wrappers;
    LoginUtils loginUtils;
    SoftAssert softAssert = new SoftAssert();

    public LoginUtils(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.actions = new Actions(driver);
    }

    public void login(String mobileNumber, String otp) throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        Thread.sleep(2000);
        WebElement mobileNumberField = driver.findElement(By.xpath("//*[@id=\"enter-mobile\"]"));
        mobileNumberField.sendKeys(mobileNumber);

        WebElement getOtpButton = wait
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text() ='Get OTP']")));
        actions.moveToElement(getOtpButton).click().perform();
        Thread.sleep(2000);

        boolean otpTextPresent = driver.findElement(By.xpath("//h5[@class ='OtpMessage']")).isDisplayed();
        Assert.assertTrue(otpTextPresent, "OTP Text is not displayed");

        WebElement enterOTP = driver.findElement(By.id("enter-otp"));
        enterOTP.sendKeys(otp);

        WebElement loginButton = wait
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text() ='LOG IN']")));
        actions.moveToElement(loginButton).click().perform();

        Thread.sleep(500);
    }
}
