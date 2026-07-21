package dev.emrullah.ai_panel.tools;

import dev.emrullah.ai_panel.model.ExcelExportToolResponse;
import dev.emrullah.ai_panel.service.EntityManagerService;
import dev.emrullah.ai_panel.service.MinioService;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

@Component
@Transactional(readOnly = true)
public class ExcelExportTool {

    private final MinioService minioService;
    private final EntityManagerService entityManagerService;

    public ExcelExportTool(MinioService minioService, EntityManagerService entityManagerService) {
        this.minioService = minioService;
        this.entityManagerService = entityManagerService;
    }

    @Tool(
            name = "generate_bar_chart_excel_from_jpql",
            description = """
            Creates an Excel workbook with a bar chart from the result of a JPQL query.

            Use this tool whenever the user wants a chart/graph exported as Excel.

            Instructions:
            - The jpql parameter must return exactly two columns: a category (String) and a numeric value (Number).
              Example: SELECT u.username, SUM(o.paidAmount - ou.costPrice) FROM User u JOIN u.orderList o JOIN o.orderUsage ou GROUP BY u.username
            - Do not select the whole entity, only the two required fields.
            - The tool executes the query, builds the chart, and returns a downloadable URL.

            Returns an ExcelExportToolResponse JSON object with the following fields:
            - success (boolean): true if the Excel file was created and uploaded successfully, false if an error occurred.
            - message (string): "excel created successfully" on success, or the error message describing what went wrong on failure.
            - downloadableContentUrl (string, nullable): a pre-signed, time-limited URL to download the generated .xlsx file.
              This field is null when success is false. When present, share this URL with the user as the download link
              for their Excel file — do not attempt to fetch, parse, or re-upload its content.
            """
    )
    public ExcelExportToolResponse generateBarChartExcelFromJpql(
            @ToolParam(description = "Worksheet name.") String sheetName,
            @ToolParam(description = "JPQL query returning exactly [String category, Number value] pairs.") String jpql,
            @ToolParam(description = "Header for the category column.") String categoryHeader,
            @ToolParam(description = "Header for the value column.") String valueHeader,
            @ToolParam(description = "Chart title.") String chartTitle,
            @ToolParam(description = "X axis title.") String xAxisTitle,
            @ToolParam(description = "Y axis title.") String yAxisTitle,
            @ToolParam(description = "Series title.") String seriesTitle
    ) {
        try {
            List<?> result = (List<?>) entityManagerService.executeJpaQuery(jpql);

            List<String> categories = new ArrayList<>();
            List<Double> values = new ArrayList<>();

            for (Object rowObject : result) {
                if (rowObject instanceof Object[] row && row.length >= 2) {
                    categories.add(String.valueOf(row[0]));
                    values.add(((Number) row[1]).doubleValue());
                }
            }

            return buildBarChartExcel(sheetName, categoryHeader, valueHeader, categories, values,
                    chartTitle, xAxisTitle, yAxisTitle, seriesTitle);
        } catch (Exception e) {
            return new ExcelExportToolResponse(false, e.getMessage(), null);
        }
    }


    @Tool(name = "generate_tabular_excel_with_jpql",
            description = """
            Creates an Excel workbook from the result of a JPQL query.

            Use this tool whenever the user asks to export database records into Excel.

            Instructions:
            - The jpql parameter must be a valid read-only JPQL SELECT query.
            - The SELECT clause MUST list individual entity fields, NOT the whole entity.
              Correct:   SELECT u.id, u.username, u.email, u.createdDate FROM User u
              Incorrect: SELECT u FROM User u
            - The number and order of selected fields must exactly match the headers parameter.
            - Use JPA entity names and entity field names.
            - Do not generate native SQL.

            The tool executes the JPQL query, creates the Excel file and returns the download URL.

            Returns an ExcelExportToolResponse JSON object with the following fields:
            - success (boolean): true if the query executed and the Excel file was created and uploaded successfully,
              false if an error occurred (e.g. invalid JPQL, header/column count mismatch).
            - message (string): "excel created successfully" on success, or the error message describing what went wrong on failure.
            - downloadableContentUrl (string, nullable): a pre-signed, time-limited URL to download the generated .xlsx file.
              This field is null when success is false. When present, share this URL with the user as the download link
              for their Excel file — do not attempt to fetch, parse, or re-upload its content.
            """
    )
    public ExcelExportToolResponse generateTabularExcelWithJpql(

            @ToolParam(description = "Worksheet name.")
            String sheetName,

            @ToolParam(description = "Column headers in the same order as the JPQL select clause.")
            List<String> headers,

            @ToolParam(description = "JPQL query used to retrieve the exported data.")
            String jpql
    ) {

        List<?> result = (List<?>) entityManagerService.executeJpaQuery(jpql);

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XSSFSheet sheet = workbook.createSheet(sheetName);

            XSSFRow headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.size(); i++) {
                headerRow.createCell(i).setCellValue(headers.get(i));
            }

            for (int i = 0; i < result.size(); i++) {

                Object rowObject = result.get(i);

                XSSFRow row = sheet.createRow(i + 1);

                if (rowObject instanceof Object[] values) {

                    for (int j = 0; j < values.length; j++) {
                        row.createCell(j).setCellValue(
                                values[j] == null ? "" : values[j].toString()
                        );
                    }

                } else {

                    row.createCell(0).setCellValue(
                            rowObject == null ? "" : rowObject.toString()
                    );
                }
            }

            workbook.write(out);

            String url =  minioService.upload(
                    out.toByteArray(),
                    sheetName,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            );

            return new ExcelExportToolResponse(true, "excel created successfully", url);

        } catch (Exception e) {
            return new ExcelExportToolResponse(false, e.getMessage(), null);
        }
    }

    private ExcelExportToolResponse buildBarChartExcel(
            String sheetName,
            String categoryHeader,
            String valueHeader,
            List<String> categories,
            List<Double> values,
            String chartTitle,
            String xAxisTitle,
            String yAxisTitle,
            String seriesTitle
    ) {

        if (categories.size() != values.size()) {
            throw new IllegalArgumentException("Kategori ve Değer listelerinin boyutları aynı olmalıdır.");
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XSSFSheet sheet = workbook.createSheet(sheetName);

            XSSFRow headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue(categoryHeader);
            headerRow.createCell(1).setCellValue(valueHeader);

            int dataSize = categories.size();
            for (int i = 0; i < dataSize; i++) {
                XSSFRow row = sheet.createRow(i + 1); // 0. satır başlık olduğu için +1
                row.createCell(0).setCellValue(categories.get(i));
                row.createCell(1).setCellValue(values.get(i));
            }

            // Çizim Alanı ve Grafiğin Konumunu Ayarla
            XSSFDrawing drawing = sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor = drawing.createAnchor(
                    0, 0, 0, 0,
                    5, 1, 20, 30
            );

            // Grafiği ve Başlıklarını Oluştur
            XSSFChart chart = drawing.createChart(anchor);
            chart.setTitleText(chartTitle);
            chart.setTitleOverlay(false);

            // Eksenleri Tanımla
            XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            bottomAxis.setTitle(xAxisTitle);

            XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
            leftAxis.setTitle(yAxisTitle);
            leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

            // Veri Kaynaklarının Dinamik Aralıklarını (CellRangeAddress) Belirle
            XDDFDataSource<String> categoryData = XDDFDataSourcesFactory.fromStringCellRange(
                    sheet, new CellRangeAddress(1, dataSize, 0, 0)
            );
            XDDFNumericalDataSource<Double> valueData = XDDFDataSourcesFactory.fromNumericCellRange(
                    sheet, new CellRangeAddress(1, dataSize, 1, 1)
            );

            // Grafik Türünü ve Seriyi Ayarla
            XDDFBarChartData chartData = (XDDFBarChartData) chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
            chartData.setBarDirection(BarDirection.COL);

            XDDFBarChartData.Series series = (XDDFBarChartData.Series) chartData.addSeries(categoryData, valueData);
            series.setTitle(seriesTitle, null);

            // Grafiği Çiz ve Yükle
            chart.plot(chartData);
            workbook.write(out);

            String url =  minioService.upload(
                    out.toByteArray(),
                    sheetName,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            );
            return new ExcelExportToolResponse(true, "excel created successfully", url);
        } catch (Exception e) {
            return new ExcelExportToolResponse(false, e.getMessage(), null);
        }
    }
}
