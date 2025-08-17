package com.example.basespringbootapikotlin.config

import org.slf4j.MDC
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.*
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@Component
class CorrelationIdFilter : HttpFilter() {
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        try {
            val correlationId = request.getHeader("X-Correlation-Id") ?: UUID.randomUUID().toString()
            MDC.put("correlationId", correlationId)
            val userSub = request.userPrincipal?.name
            if (userSub != null) {
                MDC.put("userSub", userSub)
            }
            chain.doFilter(request, response)
        } finally {
            MDC.remove("correlationId")
            MDC.remove("userSub")
        }
    }
}
