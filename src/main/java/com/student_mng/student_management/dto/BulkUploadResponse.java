package com.student_mng.student_management.dto;

import java.util.List;

public record BulkUploadResponse(
    int totalProcessed,
    int successCount,
    List<String> successfulEntries,
    List<String> errors
) {
    public static BulkUploadResponse empty() {
        return new BulkUploadResponse(0, 0, List.of(), List.of());
    }
}