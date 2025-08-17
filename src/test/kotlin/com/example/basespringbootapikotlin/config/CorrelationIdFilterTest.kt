package com.example.basespringbootapikotlin.config

import io.mockk.mockk
import io.mockk.verify
import io.mockk.every
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals

class CorrelationIdFilterTest {
    @Test
    fun `should add correlationId to MDC if not present in request`() {
        val filter = CorrelationIdFilter()
        val request = mockk<HttpServletRequest>(relaxed = true) {
            every { getHeader("X-Correlation-Id") } returns null
        }
        val response = mockk<HttpServletResponse>(relaxed = true)
        val chain = mockk<FilterChain>(relaxed = true)

        filter.doFilter(request, response, chain)

        val correlationId = MDC.get("correlationId")
        assertNotNull(correlationId)
        verify { chain.doFilter(request, response) }
    }

    @Test
    fun `should use existing correlationId from request header`() {
        val filter = CorrelationIdFilter()
        val request = mockk<HttpServletRequest>(relaxed = true) {
            every { getHeader("X-Correlation-Id") } returns "test-corr-id"
        }
        val response = mockk<HttpServletResponse>(relaxed = true)
        val chain = mockk<FilterChain>(relaxed = true)

        filter.doFilter(request, response, chain)

        val correlationId = MDC.get("correlationId")
        assertEquals("test-corr-id", correlationId)
        verify { chain.doFilter(request, response) }
    }
}
package com.example.basespringbootapikotlin.config

import io.mockk.mockk
import io.mockk.verify
import io.mockk.every
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals

class CorrelationIdFilterTest {
    @Test
    fun `should add correlationId to MDC if not present in request`() {
        val filter = CorrelationIdFilter()
        val request = mockk<HttpServletRequest>(relaxed = true) {
            every { getHeader("X-Correlation-Id") } returns null
        }
        val response = mockk<HttpServletResponse>(relaxed = true)
        val chain = mockk<FilterChain>(relaxed = true)

        filter.doFilter(request, response, chain)

        val correlationId = MDC.get("correlationId")
        assertNotNull(correlationId)
        verify { chain.doFilter(request, response) }
    }

    @Test
    fun `should use existing correlationId from request header`() {
        val filter = CorrelationIdFilter()
        val request = mockk<HttpServletRequest>(relaxed = true) {
            every { getHeader("X-Correlation-Id") } returns "test-corr-id"
        }
        val response = mockk<HttpServletResponse>(relaxed = true)
        val chain = mockk<FilterChain>(relaxed = true)

        filter.doFilter(request, response, chain)

        val correlationId = MDC.get("correlationId")
        assertEquals("test-corr-id", correlationId)
        verify { chain.doFilter(request, response) }
    }
}
