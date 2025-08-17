package com.example.basespringbootapikotlin.feature.report

import com.example.basespringbootapikotlin.feature.timeentry.TimeEntryRepository
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class ReportService(private val timeEntryRepository: TimeEntryRepository) {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    fun generateExcel(userSub: String, reporterName: String?, reporterEmail: String?, from: LocalDate?, to: LocalDate?, projectId: Long?): ByteArray {
        val entries = timeEntryRepository.findAllByUserSub(userSub, from, to)
            .filter { projectId == null || it.prosjektId == projectId }

        val wb = XSSFWorkbook()
        val sheet = wb.createSheet("Timeføringer")

        // Create some styles
        val headerStyle = wb.createCellStyle() as XSSFCellStyle
        val headerFont = wb.createFont()
        headerFont.bold = true
        headerFont.fontHeightInPoints = 12
        headerStyle.setFont(headerFont)
        headerStyle.alignment = HorizontalAlignment.CENTER
    headerStyle.fillForegroundColor = org.apache.poi.ss.usermodel.IndexedColors.LIGHT_CORNFLOWER_BLUE.index
        headerStyle.fillPattern = org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND

        val boldStyle = wb.createCellStyle() as XSSFCellStyle
        val boldFont = wb.createFont()
        boldFont.bold = true
        boldStyle.setFont(boldFont)

        val zebraStyle = wb.createCellStyle() as XSSFCellStyle
    zebraStyle.fillForegroundColor = org.apache.poi.ss.usermodel.IndexedColors.GREY_25_PERCENT.index
        zebraStyle.fillPattern = org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND

        var rowIdx = 0

        // Reporter information header (two rows)
        val reporterRow = sheet.createRow(rowIdx++)
        reporterRow.createCell(0).setCellValue("Rapportør: ${reporterName ?: userSub}")
        if (!reporterEmail.isNullOrEmpty()) reporterRow.createCell(2).setCellValue("E-post: $reporterEmail")

        // Empty spacer
        rowIdx++

        // Column headers
        val header = sheet.createRow(rowIdx++)
        header.createCell(0).setCellValue("Dato")
        header.createCell(1).setCellValue("Prosjekt")
        header.createCell(2).setCellValue("Timer")
        header.createCell(3).setCellValue("Kommentar")

        for (i in 0..3) {
            header.getCell(i)?.cellStyle = headerStyle
        }

        var totalHours = 0.0
        var dataRowIdx = rowIdx
        for ((index, e) in entries.withIndex()) {
            val r = sheet.createRow(rowIdx++)
            r.createCell(0).setCellValue(e.dato.format(dateFormatter))
            r.createCell(1).setCellValue(e.prosjektId.toString())
            r.createCell(2).setCellValue(e.timer)
            r.createCell(3).setCellValue(e.kommentar ?: "")
            totalHours += e.timer

            // zebra striping
            if (index % 2 == 1) {
                for (i in 0..3) r.getCell(i)?.cellStyle = zebraStyle
            }
        }

        // Summary row
        val summaryRow = sheet.createRow(rowIdx)
        summaryRow.createCell(1).setCellValue("Totalt")
        summaryRow.createCell(2).setCellValue(totalHours)
        summaryRow.getCell(1)?.cellStyle = boldStyle
        summaryRow.getCell(2)?.cellStyle = boldStyle

        // Autosize
        for (i in 0..3) sheet.autoSizeColumn(i)

        val baos = ByteArrayOutputStream()
        wb.use { it.write(baos) }
        return baos.toByteArray()
    }
}
