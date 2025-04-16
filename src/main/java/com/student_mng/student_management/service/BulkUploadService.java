package com.student_mng.student_management.service;

import com.student_mng.student_management.dto.BulkUploadResponse;
import com.student_mng.student_management.dto.StudentDTO;
import com.student_mng.student_management.enums.BatchType;
import com.student_mng.student_management.enums.FileType;
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

    public BulkUploadResponse processFile(MultipartFile file, FileType fileType,
            Consumer<StudentDTO> studentProcessor) {
        try {
            return switch (fileType) {
                case CSV -> processCSV(file, studentProcessor);
                case XLSX -> processExcel(file, studentProcessor);
            };
        } catch (IOException e) {
//            log.error("Error processing file", e);
            return new BulkUploadResponse(0, 0, List.of(), 
                List.of("Error processing file: " + e.getMessage()));
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

            for (CSVRecord record : csvParser) {
                totalProcessed++;
                try {
                    StudentDTO studentDTO = extractStudentFromCSV(record);
                    studentProcessor.accept(studentDTO);
                    successCount++;
                    successfulEntries.add(studentDTO.rollNumber());
                } catch (Exception e) {
                    errors.add("Row " + record.getRecordNumber() + ": " + e.getMessage());
                }
            }
        }

        return new BulkUploadResponse(totalProcessed, successCount, 
            successfulEntries, errors);
    }

    private BulkUploadResponse processExcel(MultipartFile file, 
            Consumer<StudentDTO> studentProcessor) throws IOException {
        List<String> successfulEntries = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int totalProcessed = 0;
        int successCount = 0;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Skip header
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                totalProcessed++;
                try {
                    StudentDTO studentDTO = extractStudentFromExcel(row);
                    studentProcessor.accept(studentDTO);
                    successCount++;
                    successfulEntries.add(studentDTO.rollNumber());
                } catch (Exception e) {
                    errors.add("Row " + (row.getRowNum() + 1) + ": " + e.getMessage());
                }
            }
        }

        return new BulkUploadResponse(totalProcessed, successCount, 
            successfulEntries, errors);
    }

    private StudentDTO extractStudentFromCSV(CSVRecord record) {
        return new StudentDTO(
            record.get("Username"),
            record.get("Email"),
            generateDefaultPassword(record.get("Roll Number"), 
                record.get("First Name")),
            record.get("Roll Number"),
            record.get("First Name"),
            record.get("Last Name"),
            record.get("Contact"),
            record.get("Parent Contact"),
            record.get("Parent Email"),
            BatchType.valueOf(record.get("Batch"))
        );
    }

    private StudentDTO extractStudentFromExcel(Row row) {
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
    }

    private String generateDefaultPassword(String rollNumber, String firstName) {
        return rollNumber + "@" + firstName.toLowerCase();
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> "";
        };
    }

    public ByteArrayResource generateTemplate(FileType fileType) throws IOException {
        return switch (fileType) {
            case CSV -> generateCSVTemplate();
            case XLSX -> generateExcelTemplate();
        };
    }

    private ByteArrayResource generateCSVTemplate() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (CSVPrinter printer = new CSVPrinter(
                new OutputStreamWriter(out), CSVFormat.DEFAULT)) {
            printer.printRecord((Object[]) HEADERS);
        }
        return new ByteArrayResource(out.toByteArray());
    }

    private ByteArrayResource generateExcelTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Students");
            Row headerRow = sheet.createRow(0);
            
            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Create headers
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayResource(out.toByteArray());
        }
    }
}