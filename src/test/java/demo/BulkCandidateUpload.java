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
import demo.wrappers.Wrappers;
import io.github.bonigarcia.wdm.WebDriverManager;
public class BulkCandidateUpload {
    ChromeDriver driver;
    SoftAssert softAssert = new SoftAssert();
    Wrappers wrappers;
    LoginUtils loginUtils;


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
    public int getExcelRowCount(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);
        int rowCount = sheet.getPhysicalNumberOfRows() - 1; // Subtract 1 to exclude header row
        workbook.close();
        fis.close();
        return rowCount;
    }

    @Test(priority = 1)
    public void testLogin() throws InterruptedException {
        loginUtils.login("7560868044", "8044");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/home"));    
        Assert.assertTrue(driver.getCurrentUrl().contains("/home"), "Login failed, not redirected to home page");
    }

    @Test(enabled = false, priority = 2)
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
        System.out.println("✅ Staging Branch");

        wrappers.clickElement(By.xpath("//span[text() = 'Download All']"));

    }

    @Test(enabled = true, priority = 3)
    public void verifyBulkUploadCandidatesPageElements() throws InterruptedException {

        System.out.println("Verify Bulk Upload Candidates Page Elements");

        driver.get("https://mitra-leader.vahan.co/bulk-actions");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/bulk-actions"));

        System.out.println("Checking presence of Bulk Upload & Referral button");
        softAssert.assertTrue(wrappers.isElementPresent(By.xpath("//div[text() = 'Bulk Upload & Referral']")), "Bulk Upload & Referral element is not present");
        System.out.println("Checking presence of Upload and refer candidates in bulk for a job button");
        softAssert.assertTrue(wrappers.isElementPresent(By.xpath("//div[text() = 'Upload and refer candidates in bulk for a job']")), "Upload and refer candidates in bulk for a job element is not present");
        System.out.println("Checking presence of Bulk Referral button");
        softAssert.assertTrue(wrappers.isElementPresent(By.xpath("//span[text() = 'Bulk Referral']")), "Bulk Referral element is not present");
        System.out.println("Checking presence of Bulk Uploads button");
        softAssert.assertTrue(wrappers.isElementPresent(By.xpath("//span[text() = 'Bulk Uploads']")), "Bulk Uploads element is not present");
        System.out.println("Checking presence of Bulk WhatsApp Messages button");
        softAssert.assertTrue(wrappers.isElementPresent(By.xpath("//span[text() = 'Bulk WhatsApp Messages']")), "Bulk WhatsApp Messages element is not present");
        System.out.println("Checking presence of Refresh button");
        softAssert.assertTrue(wrappers.isElementPresent(By.xpath("//span[text() = 'Refresh']")), "Refresh element is not present");
        System.out.println("Checking presence of Download All button");
        softAssert.assertTrue(wrappers.isElementPresent(By.xpath("//span[text() = 'Download All']")), "Download All element is not present");
        System.out.println("Checking presence of Pagination");
        softAssert.assertTrue(wrappers.isElementPresent(By.xpath("//li[@class = 'ant-pagination-total-text']")), "Pagination total text element is not present");
        System.out.println("Checking presence of Apply filter button");
        softAssert.assertTrue(wrappers.isElementPresent(By.xpath("//button[@class = 'ant-btn ant-btn-default apply-filter-btn']")), "Apply filter button is not present");
        System.out.println("Checking presence of Date Picker");
        softAssert.assertTrue(wrappers.isElementPresent(By.xpath("//div[@class = 'ant-picker ant-picker-range']")), "Date range picker is not present");
        softAssert.assertAll();

        System.out.println("Verify Apply filter button is working");

        WebElement filterButton = driver.findElement(By.xpath("//button[@class = 'ant-btn ant-btn-default apply-filter-btn']"));
        filterButton.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'sidername']")));

        System.out.println("Verify Filter components");

        System.out.println("Checking presence of Clients filter component");
        softAssert.assertTrue(wrappers.isElementPresent(By.xpath("//div[text() = 'Clients']")), "Clients filter component is not present");
        System.out.println("Checking presence of Process Status filter component");
        softAssert.assertTrue(wrappers.isElementPresent(By.xpath("//div[text() = 'Process Status']")), "Process Status filter component is not present");
        System.out.println("Checking presence of Clear All button");
        softAssert.assertTrue(wrappers.isElementPresent(By.xpath("//span[text() = 'Clear All']")), "Clear All button is not present");
        System.out.println("Checking presence of Submit button");
        softAssert.assertTrue(wrappers.isElementPresent(By.xpath("//span[text() = 'Submit']")), "Submit button is not present");







        
        softAssert.assertAll();
    }

    @AfterTest
    public void endTest() {
        softAssert.assertAll();
        driver.close();
        driver.quit();
    }
}
