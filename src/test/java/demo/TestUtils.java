package demo;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import io.github.bonigarcia.wdm.WebDriverManager;

public class TestUtils {

    public static String generateRandomPhoneNumber() {
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