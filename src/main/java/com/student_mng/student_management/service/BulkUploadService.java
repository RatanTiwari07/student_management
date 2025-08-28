package com.student_mng.student_management.service;

import com.student_mng.student_management.dto.BulkUploadResponse;
import com.student_mng.student_management.dto.StudentDTO;
import com.student_mng.student_management.enums.BatchType;
import com.student_mng.student_management.enums.FileType;
import com.student_mng.student_management.exception.FileProcessingException;
import com.student_mng.student_management.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

@Service
@Slf4j
public class BulkUploadService {

    private static final String[] HEADERS = {
        "Username", "Email", "Roll Number", "First Name", "Last Name",
        "Contact", "Parent Contact", "Parent Email", "Batch"
    };

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final int MAX_RECORDS = 1000;

    public BulkUploadResponse processFile(MultipartFile file, FileType fileType,
            Consumer<StudentDTO> studentProcessor) {
        try {
            validateFile(file);

            return switch (fileType) {
                case CSV -> processCSV(file, studentProcessor);
                case XLSX -> processExcel(file, studentProcessor);
            };
        } catch (IOException e) {
            log.error("Error processing file: {}", file.getOriginalFilename(), e);
            throw new FileProcessingException("Error processing file: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error processing file: {}", file.getOriginalFilename(), e);
            throw new FileProcessingException("Unexpected error processing file: " + e.getMessage(), e);
        }
    }

    private BulkUploadResponse processCSV(MultipartFile file, 
            Consumer<StudentDTO> studentProcessor) throws IOException {
        List<String> successfulEntries = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int totalProcessed = 0;
        int successCount = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withTrim())) {

            validateCSVHeaders(csvParser.getHeaderNames());

            for (CSVRecord record : csvParser) {
                if (totalProcessed >= MAX_RECORDS) {
                    errors.add("Maximum number of records (" + MAX_RECORDS + ") exceeded");
                    break;
                }

                totalProcessed++;
                try {
                    StudentDTO studentDTO = extractStudentFromCSV(record);
                    validateStudentDTO(studentDTO, record.getRecordNumber());
                    studentProcessor.accept(studentDTO);
                    successCount++;
                    successfulEntries.add(studentDTO.rollNumber());
                } catch (Exception e) {
                    String errorMsg = "Row " + record.getRecordNumber() + ": " + e.getMessage();
                    errors.add(errorMsg);
                    log.warn("Error processing CSV row {}: {}", record.getRecordNumber(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error parsing CSV file: {}", file.getOriginalFilename(), e);
            throw new FileProcessingException("Error parsing CSV file: " + e.getMessage(), e);
        }

        log.info("CSV processing completed: {} total, {} successful, {} errors",
                totalProcessed, successCount, errors.size());
        return new BulkUploadResponse(totalProcessed, successCount, successfulEntries, errors);
    }

    private BulkUploadResponse processExcel(MultipartFile file, 
            Consumer<StudentDTO> studentProcessor) throws IOException {
        List<String> successfulEntries = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int totalProcessed = 0;
        int successCount = 0;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            if (workbook.getNumberOfSheets() == 0) {
                throw new FileProcessingException("Excel file contains no sheets");
            }

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Validate and skip header
            if (rowIterator.hasNext()) {
                Row headerRow = rowIterator.next();
                validateExcelHeaders(headerRow);
            } else {
                throw new FileProcessingException("Excel file is empty");
            }

            while (rowIterator.hasNext()) {
                if (totalProcessed >= MAX_RECORDS) {
                    errors.add("Maximum number of records (" + MAX_RECORDS + ") exceeded");
                    break;
                }

                Row row = rowIterator.next();
                totalProcessed++;

                try {
                    // Skip empty rows
                    if (isRowEmpty(row)) {
                        continue;
                    }

                    StudentDTO studentDTO = extractStudentFromExcel(row);
                    validateStudentDTO(studentDTO, row.getRowNum() + 1);
                    studentProcessor.accept(studentDTO);
                    successCount++;
                    successfulEntries.add(studentDTO.rollNumber());
                } catch (Exception e) {
                    String errorMsg = "Row " + (row.getRowNum() + 1) + ": " + e.getMessage();
                    errors.add(errorMsg);
                    log.warn("Error processing Excel row {}: {}", row.getRowNum() + 1, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error parsing Excel file: {}", file.getOriginalFilename(), e);
            throw new FileProcessingException("Error parsing Excel file: " + e.getMessage(), e);
        }

        log.info("Excel processing completed: {} total, {} successful, {} errors",
                totalProcessed, successCount, errors.size());
        return new BulkUploadResponse(totalProcessed, successCount, successfulEntries, errors);
    }

    private StudentDTO extractStudentFromCSV(CSVRecord record) {
        try {
            return new StudentDTO(
                getCSVValue(record, "Username"),
                getCSVValue(record, "Email"),
                generateDefaultPassword(getCSVValue(record, "Roll Number"),
                    getCSVValue(record, "First Name")),
                getCSVValue(record, "Roll Number"),
                getCSVValue(record, "First Name"),
                getCSVValue(record, "Last Name"),
                getCSVValue(record, "Contact"),
                getCSVValue(record, "Parent Contact"),
                getCSVValue(record, "Parent Email"),
                BatchType.valueOf(getCSVValue(record, "Batch"))
            );
        } catch (Exception e) {
            throw new FileProcessingException("Error extracting student data from CSV record: " + e.getMessage(), e);
        }
    }

    private StudentDTO extractStudentFromExcel(Row row) {
        try {
            return new StudentDTO(
                getCellValue(row.getCell(0)),
                getCellValue(row.getCell(1)),
                generateDefaultPassword(getCellValue(row.getCell(2)),
                    getCellValue(row.getCell(3))),
                getCellValue(row.getCell(2)),
                getCellValue(row.getCell(3)),
                getCellValue(row.getCell(4)),
                getCellValue(row.getCell(5)),
                getCellValue(row.getCell(6)),
                getCellValue(row.getCell(7)),
                BatchType.valueOf(getCellValue(row.getCell(8)))
            );
        } catch (Exception e) {
            throw new FileProcessingException("Error extracting student data from Excel row: " + e.getMessage(), e);
        }
    }

    private String generateDefaultPassword(String rollNumber, String firstName) {
        if (rollNumber == null || firstName == null) {
            throw new ValidationException("Roll number and first name are required for password generation");
        }
        return rollNumber + "@" + firstName.toLowerCase();
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        try {
            return switch (cell.getCellType()) {
                case STRING -> cell.getStringCellValue().trim();
                case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
                case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
                default -> "";
            };
        } catch (Exception e) {
            log.warn("Error reading cell value: {}", e.getMessage());
            return "";
        }
    }

    private String getCSVValue(CSVRecord record, String columnName) {
        try {
            return record.get(columnName).trim();
        } catch (Exception e) {
            throw new FileProcessingException("Missing or invalid column: " + columnName);
        }
    }

    public ByteArrayResource generateTemplate(FileType fileType) throws IOException {
        try {
            return switch (fileType) {
                case CSV -> generateCSVTemplate();
                case XLSX -> generateExcelTemplate();
            };
        } catch (Exception e) {
            log.error("Error generating template for file type: {}", fileType, e);
            throw new FileProcessingException("Error generating template: " + e.getMessage(), e);
        }
    }

    private ByteArrayResource generateCSVTemplate() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (CSVPrinter printer = new CSVPrinter(
                new OutputStreamWriter(out), CSVFormat.DEFAULT)) {
            printer.printRecord((Object[]) HEADERS);
            // Add sample row
            printer.printRecord("john_doe", "john@example.com", "2023001", "John", "Doe",
                              "1234567890", "9876543210", "parent@example.com", "B1");
        }
        return new ByteArrayResource(out.toByteArray());
    }

    private ByteArrayResource generateExcelTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Students");

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Create headers
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }

            // Add sample row
            Row sampleRow = sheet.createRow(1);
            String[] sampleData = {"john_doe", "john@example.com", "2023001", "John", "Doe",
                                 "1234567890", "9876543210", "parent@example.com", "B1"};
            for (int i = 0; i < sampleData.length; i++) {
                sampleRow.createCell(i).setCellValue(sampleData[i]);
            }

            // Auto-size columns after adding sample data
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayResource(out.toByteArray());
        }
    }

    // Validation methods
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("File cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ValidationException("File size exceeds maximum limit of " + (MAX_FILE_SIZE / 1024 / 1024) + "MB");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || filename.trim().isEmpty()) {
            throw new ValidationException("Invalid filename");
        }
    }

    private void validateCSVHeaders(List<String> actualHeaders) {
        if (actualHeaders.size() != HEADERS.length) {
            throw new ValidationException("CSV file must have exactly " + HEADERS.length + " columns");
        }

        for (String expectedHeader : HEADERS) {
            if (!actualHeaders.contains(expectedHeader)) {
                throw new ValidationException("Missing required column: " + expectedHeader);
            }
        }
    }

    private void validateExcelHeaders(Row headerRow) {
        if (headerRow.getLastCellNum() != HEADERS.length) {
            throw new ValidationException("Excel file must have exactly " + HEADERS.length + " columns");
        }

        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = headerRow.getCell(i);
            String actualHeader = getCellValue(cell);
            if (!HEADERS[i].equals(actualHeader)) {
                throw new ValidationException("Column " + (i + 1) + " should be '" + HEADERS[i] + "' but found '" + actualHeader + "'");
            }
        }
    }

    private boolean isRowEmpty(Row row) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && !getCellValue(cell).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void validateStudentDTO(StudentDTO studentDTO, long rowNumber) {
        List<String> errors = new ArrayList<>();

        if (studentDTO.username() == null || studentDTO.username().trim().isEmpty()) {
            errors.add("Username is required");
        }

        if (studentDTO.email() == null || studentDTO.email().trim().isEmpty()) {
            errors.add("Email is required");
        } else if (!isValidEmail(studentDTO.email())) {
            errors.add("Invalid email format");
        }

        if (studentDTO.rollNumber() == null || studentDTO.rollNumber().trim().isEmpty()) {
            errors.add("Roll number is required");
        }

        if (studentDTO.firstName() == null || studentDTO.firstName().trim().isEmpty()) {
            errors.add("First name is required");
        }

        if (studentDTO.lastName() == null || studentDTO.lastName().trim().isEmpty()) {
            errors.add("Last name is required");
        }

        if (studentDTO.batch() == null) {
            errors.add("Batch is required");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Validation errors: " + String.join(", ", errors));
        }
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}