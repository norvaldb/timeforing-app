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

    fun generateExcel(userSub: String, from: LocalDate?, to: LocalDate?, projectId: Long?): ByteArray {
        val entries = timeEntryRepository.findAllByUserSub(userSub, from, to)
            .filter { projectId == null || it.prosjektId == projectId }

        val wb = XSSFWorkbook()
        val sheet = wb.createSheet("Timeføringer")

        // Header
        val header = sheet.createRow(0)
        header.createCell(0).setCellValue("Dato")
        header.createCell(1).setCellValue("Prosjekt")
        header.createCell(2).setCellValue("Timer")
        header.createCell(3).setCellValue("Kommentar")

        // Simple styles
    val headerStyle: XSSFCellStyle = wb.createCellStyle() as XSSFCellStyle
    headerStyle.alignment = HorizontalAlignment.CENTER

    for (i in 0..3) header.getCell(i)?.cellStyle = headerStyle

        var rowIdx = 1
        var totalHours = 0.0
        for (e in entries) {
            val r = sheet.createRow(rowIdx++)
            r.createCell(0).setCellValue(e.dato.format(dateFormatter))
            r.createCell(1).setCellValue(e.prosjektId.toString())
            r.createCell(2).setCellValue(e.timer)
            r.createCell(3).setCellValue(e.kommentar ?: "")
            totalHours += e.timer
        }

        // Summary row
        val summaryRow = sheet.createRow(rowIdx)
        summaryRow.createCell(1).setCellValue("Totalt")
        summaryRow.createCell(2).setCellValue(totalHours)

        // Autosize
        for (i in 0..3) sheet.autoSizeColumn(i)

        val baos = ByteArrayOutputStream()
        wb.use { it.write(baos) }
        return baos.toByteArray()
    }
}
