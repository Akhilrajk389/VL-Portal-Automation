package demo;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import demo.LoginUtils;
import demo.wrappers.Wrappers;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.logging.Level;

public class AddSingleCandidates {
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


    public String generateRandomPhoneNumber() {
        Random random = new Random();
        int firstDigit = 6 + random.nextInt(4); // Generates 6, 7, 8, or 9
        long remainingDigits = 100000000L + random.nextInt(900000000); // 9 more digits
        return firstDigit + String.valueOf(remainingDigits);
    }

    public static class RandomTextGenerator {
        public static String generateRandomText(int length) {
            String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
            StringBuilder text = new StringBuilder();
            Random random = new Random();

            for (int i = 0; i < length; i++) {
                text.append(chars.charAt(random.nextInt(chars.length())));
            }
            return text.toString();
        }
    }

    @Test(priority = 1)
    public void testLogin() throws InterruptedException {
        loginUtils.login("7560868044", "8044");
    }

    @Test(priority = 2, enabled = true)
    public void TC05_Add_Single_Lead() throws InterruptedException {
        driver.get("https://mitra-leader.vahan.co");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        System.out.println("Adding Single Lead...");

        WebElement addSingleLeadButton = driver.findElement(By.xpath("//span[text() = 'Add Single Lead']"));
        addSingleLeadButton.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text() = 'Add Lead']")));

        WebElement name = driver.findElement(By.id("leadName"));
        name.sendKeys("Test User" + RandomTextGenerator.generateRandomText(5));

        String enteredName = name.getAttribute("value");
        System.out.println("Entered Name: " + enteredName);

        WebElement mobile = driver.findElement(By.id("leadPhoneNumber"));
        mobile.sendKeys(generateRandomPhoneNumber());

        String enteredNumber = mobile.getAttribute("value");
        System.out.println("Entered Number: " + enteredNumber);

        // Open the city dropdown
        WebElement cityDropdown = driver.findElement(By.id("cityPreference"));
        cityDropdown.click();

        // Wait for dropdown options to appear
        wait.until(ExpectedConditions
                .presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class, 'ant-select-item-option')]")));

        Set<String> allCities = new HashSet<>();
        int previousSize = 0;

        while (true) {
            List<WebElement> cityOptions = driver
                    .findElements(By.xpath("//div[contains(@class, 'ant-select-item-option')]"));

            for (WebElement city : cityOptions) {
                String cityName = city.getAttribute("title").trim();
                allCities.add(cityName);
            }

            // Scroll to the last city in the list
            WebElement lastCity = cityOptions.get(cityOptions.size() - 1);
            js.executeScript("arguments[0].scrollIntoView(true);", lastCity);

            // Wait for new cities to load
            Thread.sleep(1000);

            // If no new cities were loaded, exit loop
            if (allCities.size() == previousSize) {
                break;
            }
            previousSize = allCities.size();
        }

        System.out.println("All cities retrieved: " + allCities);
        softAssert.assertTrue(allCities.size() > 0, "No cities were retrieved from the dropdown.");
        softAssert.assertAll();

        // Select a random city from the fully loaded list
        List<String> cityList = new ArrayList<>(allCities);
        String randomCity = cityList.get(new Random().nextInt(cityList.size()));

        System.out.println("Random city selected: " + randomCity);

        // Type the random city name and press Enter
        WebElement cityInput = driver
                .findElement(By.xpath("//input[contains(@class, 'ant-select-selection-search-input')]"));
        cityInput.sendKeys(randomCity);

        // Wait for the matching option to appear
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@class, 'ant-select-item-option') and @title='" + randomCity + "']")));

        // Press Enter to select the city
        cityInput.sendKeys(Keys.ENTER);

        Thread.sleep(1000); // Just for stability, can be removed

        WebElement clientDropdown = driver.findElement(By.id("clientPreference"));
        clientDropdown.click();

        wait.until(ExpectedConditions
                .presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class, 'ant-select-item-option-content')]")));

        Set<String> allClients = new HashSet<>();
        previousSize = 0;

        while (true) {
            List<WebElement> clientOptions = driver
                    .findElements(By.xpath("//div[contains(@class, 'ant-select-item-option-content')]"));

            for (WebElement client : clientOptions) {
                String clientName = client.getAttribute("title").trim();
                allClients.add(clientName);
            }

            // Scroll to the last city in the list
            WebElement lastClient = clientOptions.get(clientOptions.size() - 1);
            js.executeScript("arguments[0].scrollIntoView(true);", lastClient);

            // Wait for new cities to load
            Thread.sleep(1000);

            // If no new cities were loaded, exit loop
            if (allClients.size() == previousSize) {
                break;
            }
            previousSize = allClients.size();
        }

        System.out.println("All clients retrieved: " + allClients);
        softAssert.assertTrue(allClients.size() > 0, "No clients were retrieved from the dropdown.");
        softAssert.assertAll();

        // Select a random city from the fully loaded list
        List<String> clientList = new ArrayList<>(allClients);
        String randomClient = clientList.get(new Random().nextInt(clientList.size()));

        System.out.println("Random client selected: " + randomClient);

        // Type the random client name and press Enter
        WebElement clientInput = driver.findElement(By.id("clientPreference"));
        clientInput.sendKeys(randomClient);

        // Press Enter to select the city
        clientInput.sendKeys(Keys.ENTER);

        Thread.sleep(1000); // Just for stability, can be removed

        WebElement localityDropdown = driver.findElement(By.id("locationPreference"));
        localityDropdown.click();

        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id("locationPreference_list")));

        Set<String> allLocality = new HashSet<>();
        previousSize = 0;

        while (true) {
            List<WebElement> localityOptions = driver.findElements(By.id("locationPreference_list"));

            for (WebElement locality : localityOptions) {
                String localityName = locality.getAttribute("title").trim();
                allLocality.add(localityName);
            }

            // Scroll to the last city in the list
            WebElement lastLocality = localityOptions.get(localityOptions.size() - 1);
            js.executeScript("arguments[0].scrollIntoView(true);", lastLocality);

            // Wait for new cities to load
            Thread.sleep(1000);

            // If no new cities were loaded, exit loop
            if (allLocality.size() == previousSize) {
                break;
            }
            previousSize = allLocality.size();
        }

        System.out.println("All clients retrieved: " + allLocality);
        softAssert.assertTrue(allLocality.size() > 0, "No clients were retrieved from the dropdown.");
        softAssert.assertAll();

        // Select a random city from the fully loaded list
        List<String> localityList = new ArrayList<>(allLocality);
        String randomLocality = localityList.get(new Random().nextInt(localityList.size()));

        System.out.println("Random client selected: " + randomLocality);

        // Type the random client name and press Enter
        WebElement localityInput = driver.findElement(By.id("locationPreference"));
        localityInput.sendKeys(randomLocality);

        // Press Enter to select the city
        localityInput.sendKeys(Keys.ENTER);

        Thread.sleep(1000); // Just for stability, can be removed

        // Select Next Button

        WebElement nextButton = driver.findElement(By.xpath("//button[text() = 'Next']"));
        nextButton.click();

        // Wait for the next page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[text() = 'All Candidates']")));

        WebElement nameElementInCandidateCard = driver.findElement(By.xpath("//div[contains(text(),'Test User')]"));
        String nameInCandidateCard = nameElementInCandidateCard.getText();
        System.out.println("Name in Candidate Card: " + nameInCandidateCard);

        softAssert.assertTrue(
                enteredName.equalsIgnoreCase(nameInCandidateCard),
                "Name in Candidate Card is not the same as the entered name (case insensitive check)");

        WebElement mobileNumberInCandidateCard = driver.findElement(
                By.xpath("//button[contains(@class, 'ant-btn-background-ghost')]/span[2][string-length(text()) = 10]"));
        String displayedNumber = mobileNumberInCandidateCard.getText().trim();
        System.out.println("Number in Candidate Card: " + displayedNumber);

        softAssert.assertEquals(displayedNumber, enteredNumber, "Mobile number in Candidate Card is incorrect!");

        if (nameInCandidateCard.equalsIgnoreCase(enteredName) && displayedNumber.equals(enteredNumber)) {
            System.out.println("✅ Candidate successfully added");
        } else {
            System.out.println("❌ Candidate not added/ Not found in Candidates page");
        }
    }

    @AfterTest
    public void endTest() {
        softAssert.assertAll();
        driver.close();
        driver.quit();
    }
}
