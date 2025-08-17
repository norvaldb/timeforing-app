package com.example.basespringbootapikotlin.config

import org.slf4j.MDC
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class CorrelationIdTestController {
    @GetMapping("/test/correlation-id")
    fun getCorrelationId(@RequestHeader("X-Correlation-Id", required = false) header: String?): Map<String, String?> {
        // Return both the header and the MDC value for verification
        return mapOf(
            "header" to header,
            "mdc" to MDC.get("correlationId")
        )
    }
}
