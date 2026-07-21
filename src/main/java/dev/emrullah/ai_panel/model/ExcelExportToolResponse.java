package dev.emrullah.ai_panel.model;

public record ExcelExportToolResponse(
        boolean success,
        String message,
        String downloadableContentUrl
) {
}
