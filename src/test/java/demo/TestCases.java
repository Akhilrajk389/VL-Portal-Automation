package demo;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.*;
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

public class TestCases {
    ChromeDriver driver;
    // WebDriverWait wait;
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

    public String generateRandomPhoneNumber() {
        Random random = new Random();
        int firstDigit = 6 + random.nextInt(4); // Generates 6, 7, 8, or 9
        long remainingDigits = 100000000L + random.nextInt(900000000); // 9 more digits
        return firstDigit + String.valueOf(remainingDigits);
    }

    public class RandomTextGenerator {
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

    public class ExcelGenerator {
        public static String generateExcelFile() throws IOException {
            String filePath = System.getProperty("user.dir") + "/candidate_data.xlsx"; // Save in project root
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Candidates");

            Row headerRow = sheet.createRow(0); // First row
            headerRow.createCell(0).setCellValue("candidateName"); // Header for Name
            headerRow.createCell(1).setCellValue("candidatePhoneNumber");

            // Create header row
            String[] names = { "Ram", "Pushpendra", "Mahaveer", "Anil", "Kapil", "Lakshpat", "Himmat", "Pavan",
                    "Shahrukh", "Niranjan" };
            Random rand = new Random();

            for (int i = 1; i <= 30; i++) {
                Row row = sheet.createRow(i);
                row.createCell(0).setCellValue(names[rand.nextInt(names.length)]); // Random Name
                row.createCell(1).setCellValue("9" + (rand.nextInt(900000000) + 100000000)); // Random Phone
            }

            // 🔹 Write to File
            FileOutputStream fileOut = new FileOutputStream(filePath);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

            System.out.println("✅ Excel File Created: " + filePath);
            return filePath; // Return file path to use in Selenium upload
        }

    }

    @Test(enabled = true, priority = 1)
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

    @Test(enabled = true, priority = 2)
    public void TC02_Home_Page() throws InterruptedException {

        System.out.println("TestCase 02 Starting.. Verifying HomePage URL");
        // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Thread.sleep(200);
        System.out.println("Current Page URL: " + driver.getCurrentUrl());
        Assert.assertTrue(driver.getCurrentUrl().contains("/home"), "User is NOT on Home Page");

    }

    @Test(enabled = false)
    public void TC03_Verify_Home_UI() {
        System.out.println("Verifying UI Elements on Home Page...");

        // Check if all the main links in the Homepage is Displayed

        // softAssert.assertTrue(driver.findElement(By.xpath("//span[text() = 'Leads to
        // follow up']")).isDisplayed(), "Leads to follow up is missing");
        softAssert.assertTrue(driver.findElement(By.xpath("//span[text() = 'Job Posters']")).isDisplayed(),
                "Job Posters section is missing");
        softAssert.assertTrue(driver.findElement(By.xpath("//span[text() = 'Team Leaderboard']")).isDisplayed(),
                "Team Leaderboard section is missing");
        softAssert.assertTrue(driver.findElement(By.xpath("//span[text() = 'Help']")).isDisplayed(),
                "Help section is missing");
        softAssert.assertTrue(driver.findElement(By.xpath("//span[text() = 'Bulk Actions']")).isDisplayed(),
                "Bulk Actions is missing");
        softAssert.assertTrue(driver.findElement(By.xpath("//span[text() = 'Add Single Lead']")).isDisplayed(),
                "Add Single Lead");

        softAssert.assertTrue(driver.findElement(By.xpath("//div[text() = 'Quick Links']")).isDisplayed(),
                "Quick Links section is missing");
        softAssert.assertTrue(driver.findElement(By.xpath("//div[text()='Product Updates']")).isDisplayed(),
                "Product Updates section is missing");

        softAssert.assertAll();
    }

    @Test(enabled = false)
    public void TC04_Navigation_Tests() throws InterruptedException {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        System.out.println("Check if Add Single Leads BUtton is clickable");
        WebElement addSingleLeadButton = driver.findElement(By.xpath("//span[text() = 'Add Single Lead']"));
        addSingleLeadButton.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text() = 'Add Lead']")));
        String addSingleLeadspageSource = driver.getPageSource();
        System.out.println(driver.getTitle());

        softAssert.assertTrue(addSingleLeadspageSource.contains("Add Lead"), "Add Single Lead Page not opened");
        WebElement backButton = driver.findElement(By.xpath("//div[@role = 'button']"));
        backButton.click();

        Thread.sleep(1000);

        System.out.println("Check if BULK UPLOAD Leads Button is clickable");

        WebElement bulkActionsButton = driver.findElement(By.xpath("//span[text() = 'Bulk Actions']"));
        bulkActionsButton.click();

        String bulkActionsURL = driver.getCurrentUrl();
        System.out.println("Current URL is: " + bulkActionsURL);
        softAssert.assertTrue(bulkActionsURL.contains("bulk-actions"), "Bulk uploads page not opened");
        driver.navigate().back();

        System.out.println("check if Training Hub Button is clickable");

        WebElement goToTraining = driver.findElement(By.xpath("//span[text()='Go to Training']"));
        goToTraining.click();
        String trainingURL = driver.getCurrentUrl();
        System.out.println("Current URL is: " + trainingURL);
        Assert.assertTrue(driver.getCurrentUrl().contains("/training"), "Training Page not opened");
        driver.navigate().back();
    }

    @Test(enabled = false)
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

        // Wait for the matching option to appear
        // wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,
        // 'ant-select-item-option') and @title='" + randomClient + "']")));

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

        // Wait for the matching option to appear
        // wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,
        // 'ant-select-item-option') and @title='" + randomClient + "']")));

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

    public int getExcelRowCount(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);
        int rowCount = sheet.getPhysicalNumberOfRows() - 1; // Subtract 1 to exclude header row
        workbook.close();
        fis.close();
        return rowCount;
    }

    @Test(enabled = true, priority = 3)
    public void BulkUploadCandidates() throws IOException, InterruptedException {
        String filePath = ExcelGenerator.generateExcelFile();
        int expectedCandidateCount = getExcelRowCount(filePath);
        System.out.println("Number of candidates in Excel file: " + expectedCandidateCount);
        
        driver.get("https://mitra-leader.vahan.co/home");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until((ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text() = 'Bulk Actions']"))));
        driver.findElement(By.xpath("//span[text() = 'Bulk Actions']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text() = 'Bulk Upload & Referral']")));
        WebElement bulkUploadAndReferButton = driver.findElement(By.xpath("//div[text() = 'Bulk Upload & Referral']"));
        bulkUploadAndReferButton.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text() = 'Select File']")));
        WebElement selectFileButton = driver.findElement(By.xpath("//input[@type = 'file']"));

        WebElement uploadButton = driver.findElement(By.xpath("//div [text() = 'Upload']"));

        if (selectFileButton.getText().equals("Select File")) {
            softAssert.assertFalse(uploadButton.isEnabled(), "Upload button is enabled without selecting a file");
        } else if (selectFileButton.getText().equals("Select A Different File")) {
            softAssert.assertTrue(uploadButton.isEnabled(), "Upload button is disabled after selecting a file");
        }
        selectFileButton.sendKeys(filePath);
        System.out.println("✅ File Uploaded Successfully!");

        if (uploadButton.isEnabled()) {
            uploadButton.click();
        } else {
            System.out.println("❌ Upload button is disabled");
        }

        // Click the dropdown to open city list
        // Click to open the dropdown list
        WebElement cityDropdown = driver.findElement(By.xpath("//div[@class='ant-select-selector']"));
        cityDropdown.click();

        // Wait for dropdown options to appear
        wait.until(ExpectedConditions
                .presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class, 'ant-select-item-option-content')]")));

        // Get initial visible cities
        List<WebElement> initialCityOptions = driver
                .findElements(By.xpath("//div[contains(@class, 'ant-select-item-option-content')]"));
        List<String> cityList = new ArrayList<>();

        // Only get first 10 cities instead of scrolling through all
        int maxCities = Math.min(10, initialCityOptions.size());
        for (int i = 0; i < maxCities; i++) {
            cityList.add(initialCityOptions.get(i).getText().trim());
        }

        System.out.println("✅ Retrieved first " + maxCities + " cities: " + cityList);

        // Assertion - Ensure we have cities
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(!cityList.isEmpty(), "No cities were retrieved from the dropdown.");
        softAssert.assertAll();

        Random random = new Random();
        boolean validCityFound = false;
        List<String> triedCities = new ArrayList<>();

        while (!validCityFound && triedCities.size() < cityList.size()) {
            // Pick a random city we haven't tried yet
            String randomCity;
            do {
                randomCity = cityList.get(random.nextInt(cityList.size()));
            } while (triedCities.contains(randomCity));

            triedCities.add(randomCity);
            System.out.println("Trying city: " + randomCity);

            // Type the city name and select it
            WebElement cityInput = driver
                    .findElement(By.xpath("//input[contains(@class, 'ant-select-selection-search-input')]"));
            cityInput.clear();
            cityInput.sendKeys(randomCity);

            try {
                // Reduced wait time to 500ms
                wait.withTimeout(Duration.ofMillis(500))
                        .until(ExpectedConditions.presenceOfElementLocated(
                                By.xpath("//div[contains(@class, 'ant-select-item-option') and @title='" + randomCity
                                        + "']")));

                cityInput.sendKeys(Keys.ENTER);
                Thread.sleep(500); // Reduced wait time

                // Check if client dropdown is enabled
                WebElement clientDropdown = driver
                        .findElement(By.xpath("(//input[@class='ant-select-selection-search-input'])[2]"));
                validCityFound = clientDropdown.isEnabled();

                if (!validCityFound) {
                    System.out.println("❌ No clients available for city: " + randomCity + ". Trying another...");
                }
            } catch (Exception e) {
                System.out.println("❌ Failed to select city: " + randomCity);
            }
        }

        // Final check
        softAssert.assertTrue(validCityFound, "No valid city found with clients available. Test failed.");

        int previousSize = 0;
        // Select Client Dropdown

        WebElement clientDropdown = driver
                .findElement(By.xpath("(//input[@class='ant-select-selection-search-input'])[2]"));
        clientDropdown.click();

        wait.until(ExpectedConditions
                .presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class, 'ant-select-item-option-content')]")));

        Set<String> allClients = new HashSet<>();
        previousSize = 0;

        while (true) {

            WebElement parentDiv = driver.findElement(By.xpath("//div[@class='parent-client-div']"));
            // WebElement childElement =
            // parentDiv.findElement(By.xpath(".//div[@class='ant-select-item-option-content']"));

            List<WebElement> clientOptions = parentDiv
                    .findElements(By.xpath("//div[contains(@class, 'ant-select-item-option-content')]"));

            for (WebElement client : clientOptions) {
                String clientName = client.getText();
                allClients.add(clientName);
            }

            // Wait for new clients to load
            Thread.sleep(1000);

            // If no new clients were loaded, exit loop
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
        WebElement clientInput = driver.findElement(By.id("rc_select_1"));
        clientInput.sendKeys(randomClient);

        // Press Enter to select the client
        clientInput.sendKeys(Keys.ENTER);

        Thread.sleep(1000); // Just for stability, can be removed

        WebElement localityDropdown = driver.findElement(By.id("rc_select_2"));
        localityDropdown.click();

        // wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("")));

        Set<String> allLocality = new HashSet<>();
        previousSize = 0;

        while (true) {
            WebElement parentDiv = driver.findElement(By.xpath("//div[@class='parent-client-div']"));

            List<WebElement> localityOptions = parentDiv
                    .findElements(By.xpath("(//div[contains(@class, 'ant-select-item-option-content')])[2]"));

            for (WebElement locality : localityOptions) {
                String localityName = locality.getText();
                allLocality.add(localityName);
            }
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

        // Select a random loality from the fully loaded list
        List<String> localityList = new ArrayList<>(allLocality);
        String randomLocality = localityList.get(new Random().nextInt(localityList.size()));

        System.out.println("Random Locality selected: " + randomLocality);

        // Type the random locality name and press Enter
        WebElement localityInput = driver.findElement(By.id("rc_select_2"));
        localityInput.sendKeys(randomLocality);

        // Press Enter to select the city
        localityInput.sendKeys(Keys.ENTER);

        Thread.sleep(1000); // Just for stability, can be removed
        System.out.println("Verifying All required fields are selected and Next Button is enabled");

        Assert.assertTrue(driver.findElement(By.xpath("//span[text() = 'Next']")).isEnabled(),
                "Next Button is not enabled after selecting all required fields");
        System.out.println("✅ All required fields are selected and Next Button is enabled");

        WebElement nextButton = driver.findElement(By.xpath("//span[text() = 'Next']"));
        nextButton.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[text() = 'You can select team member(s) for assigning the candidates'] ")));

        System.out.println("Verify Next Button is enabled without selecting any fields in select Team Members page");

        Assert.assertTrue(driver.findElement(By.xpath("//span[text() = 'Next']")).isEnabled(),
                "Next Button is enabled without selecting any fields");
        System.out.println("✅ Next Button is enabled without selecting any fields in select Team Members page");

        WebElement nextButton2 = driver.findElement(By.xpath("//span[text() = 'Next']"));

        nextButton2.click();
        
        // Wait for the success message or uploaded candidates count
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div [text() = 'Your file has been processed']")));
        
        // Get the actual number of candidates uploaded
        WebElement numberOfCandidatesUploaded = driver.findElement(By.xpath("//div[@class='br-summary-processed-number br-summary-number-txt']"));
        String messageText = numberOfCandidatesUploaded.getText();
        int actualUploadedCount = Integer.parseInt(messageText.replaceAll("[^0-9]", ""));
        
        // Compare the counts
        Assert.assertEquals(actualUploadedCount, expectedCandidateCount, 
            "Number of uploaded candidates does not match the Excel file count");
        System.out.println("✅ Successfully verified " + actualUploadedCount + " candidates were uploaded");

        System.out.println("✅ Bulk Upload Candidates Test Case Passed.");
        System.out.println("Verify user is redirected to Bulk Referrals Page");

        WebElement goToBulkReferralsButton = driver.findElement(By.xpath("//div[text()='Go To Bulk Referrals']"));
        goToBulkReferralsButton.click();

        wait.until(ExpectedConditions.urlToBe("https://mitra-leader.vahan.co/bulk-actions"));
        System.out.println("✅ User is redirected to Bulk Referrals Page");
        
        
    }


    @AfterTest
    public void endTest() {
        softAssert.assertAll();
        driver.close();
        driver.quit();

    }
}
