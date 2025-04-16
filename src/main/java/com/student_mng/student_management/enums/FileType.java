package com.student_mng.student_management.enums;

public enum FileType {
    CSV("text/csv", "csv"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");

    private final String contentType;
    private final String extension;

    FileType(String contentType, String extension) {
        this.contentType = contentType;
        this.extension = extension;
    }

    public String getContentType() {
        return contentType;
    }

    public String getExtension() {
        return extension;
    }

    public static FileType fromExtension(String extension) {
        return switch (extension.toLowerCase()) {
            case "csv" -> CSV;
            case "xlsx" -> XLSX;
            default -> throw new IllegalArgumentException("Unsupported file type: " + extension);
        };
    }
}