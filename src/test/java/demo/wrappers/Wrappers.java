package demo.wrappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
// import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

import demo.HomePageTests;

public class Wrappers {
    // ChromeDriver driver;
    WebDriver driver;
    private WebDriverWait wait;

    public Wrappers(WebDriver driver, Duration timeoutInSeconds) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickElement(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            element.click();
            System.out.println("Clicked element: " + locator);
        } catch (Exception e) {
            System.out.println("Error clicking element: " + locator + " - " + e.getMessage());
        }
    }

     // ✅ Wrapper for Send Keys
     public void enterText(By locator, String text) {
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            element.clear();
            element.sendKeys(text);
            System.out.println("Entered text: " + text + " in element: " + locator);
        } catch (Exception e) {
            System.out.println("Error entering text in element: " + locator + " - " + e.getMessage());
        }
    }

    // ✅ Wrapper for Getting Text
    public String getText(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return element.getText();
        } catch (Exception e) {
            System.out.println("Error getting text from element: " + locator + " - " + e.getMessage());
            return "";
        }
    }

    // ✅ Wrapper for Checking if Element is Present
    public boolean isElementPresent(By locator) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

 
}
