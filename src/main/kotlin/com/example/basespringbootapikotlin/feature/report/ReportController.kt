package com.example.basespringbootapikotlin.feature.report

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/reports")
class ReportController(private val reportService: ReportService) {
    @GetMapping("/excel")
    fun exportExcel(
        @org.springframework.security.core.annotation.AuthenticationPrincipal jwt: Jwt,
        @RequestParam(required = false) from: LocalDate?,
        @RequestParam(required = false) to: LocalDate?,
        @RequestParam(required = false, name = "project_id") projectId: Long?
    ): ResponseEntity<ByteArray> {
        val userSub = jwt.claims["sub"] as String
        // Attempt to read name/email from JWT claims; fall back to subject only
        val reporterName = jwt.claims["name"] as? String
        val reporterEmail = jwt.claims["email"] as? String

        val bytes = reportService.generateExcel(userSub, reporterName, reporterEmail, from, to, projectId)

        val filename = "time-report-${System.currentTimeMillis()}.xlsx"
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$filename\"")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(bytes)
    }
}
