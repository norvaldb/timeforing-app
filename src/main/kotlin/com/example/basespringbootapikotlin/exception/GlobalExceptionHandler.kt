package com.example.basespringbootapikotlin.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime

import org.slf4j.LoggerFactory
import org.slf4j.MDC

/**
 * Global exception handler for user-friendly error responses
 */
@ControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(
        ex: IllegalStateException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
    val correlationId = MDC.get("correlationId")
    val userSub = MDC.get("userSub")
    log.error("IllegalStateException: {} [correlationId={}, userSub={}]", ex.message, correlationId, userSub, ex)
        val status = when (ex.message) {
            "Kan ikke slette prosjekt med registrerte timer" -> HttpStatus.CONFLICT
            else -> HttpStatus.CONFLICT
        }
        val code = when (ex.message) {
            "Kan ikke slette prosjekt med registrerte timer" -> "PROJECT_HAS_TIME_ENTRIES"
            else -> "CONFLICT"
        }
        val errorResponse = ErrorResponse(
            message = ex.message ?: "Konflikt",
            code = code,
            status = status.value(),
            timestamp = LocalDateTime.now(),
            path = request.getDescription(false),
            correlationId = correlationId
        )
        return ResponseEntity.status(status).body(errorResponse)
    }
    
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
    val correlationId = MDC.get("correlationId")
    val userSub = MDC.get("userSub")
    log.error("Validation error: {} [correlationId={}, userSub={}]", ex.message, correlationId, userSub, ex)
        val errors = mutableMapOf<String, String>()
        
        ex.bindingResult.allErrors.forEach { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.defaultMessage ?: "Ugyldig verdi"
            errors[fieldName] = errorMessage
        }
        
        val errorResponse = ErrorResponse(
            message = "Valideringsfeil",
            code = "VALIDATION_ERROR",
            status = HttpStatus.BAD_REQUEST.value(),
            timestamp = LocalDateTime.now(),
            path = request.getDescription(false),
            errors = errors,
            correlationId = correlationId
        )
        
        return ResponseEntity.badRequest().body(errorResponse)
    }
    
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        ex: IllegalArgumentException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
    val correlationId = MDC.get("correlationId")
    val userSub = MDC.get("userSub")
    log.error("IllegalArgumentException: {} [correlationId={}, userSub={}]", ex.message, correlationId, userSub, ex)
        val status = when (ex.message) {
            "Epost addressen er allerede registrert" -> HttpStatus.CONFLICT
            "Bruker ikke funnet" -> HttpStatus.NOT_FOUND
            "Prosjekt ikke funnet eller ikke aktiv" -> HttpStatus.BAD_REQUEST
            "Timer må være positiv verdi" -> HttpStatus.BAD_REQUEST
            "Kun hele eller halve timer er tillatt" -> HttpStatus.BAD_REQUEST
            "Maks 24 timer per registrering" -> HttpStatus.CONFLICT
            "Kan ikke registrere mer enn 24 timer på samme dag" -> HttpStatus.CONFLICT
            "Timeføring ikke funnet" -> HttpStatus.NOT_FOUND
            else -> HttpStatus.BAD_REQUEST
        }
        val code = when (ex.message) {
            "Epost addressen er allerede registrert" -> "DUPLICATE_EMAIL"
            "Bruker ikke funnet" -> "USER_NOT_FOUND"
            "Prosjekt ikke funnet eller ikke aktiv" -> "PROJECT_NOT_FOUND_OR_INACTIVE"
            "Timer må være positiv verdi" -> "INVALID_TIMER_VALUE"
            "Kun hele eller halve timer er tillatt" -> "INVALID_TIMER_STEP"
            "Maks 24 timer per registrering" -> "MAX_HOURS_PER_ENTRY"
            "Kan ikke registrere mer enn 24 timer på samme dag" -> "MAX_HOURS_PER_DAY"
            "Timeføring ikke funnet" -> "TIME_ENTRY_NOT_FOUND"
            else -> "INVALID_REQUEST"
        }
        
        val errorResponse = ErrorResponse(
            message = ex.message ?: "Ugyldig forespørsel",
            code = code,
            status = status.value(),
            timestamp = LocalDateTime.now(),
            path = request.getDescription(false),
            correlationId = correlationId
        )
        
        return ResponseEntity.status(status).body(errorResponse)
    }
    
    @ExceptionHandler(Exception::class)
    fun handleGeneralException(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val correlationId = MDC.get("correlationId")
        val userSub = MDC.get("userSub")
        log.error("Unexpected error: {} [correlationId={}, userSub={}]", ex.message, correlationId, userSub, ex)
        val errorResponse = ErrorResponse(
            message = "Noe gikk galt, prøv igjen senere",
            code = "INTERNAL_ERROR",
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            timestamp = LocalDateTime.now(),
            path = request.getDescription(false),
            correlationId = correlationId
        )
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}

/**
 * Standardized error response structure
 */
data class ErrorResponse(
    val message: String,
    val code: String,
    val status: Int,
    val timestamp: LocalDateTime,
    val path: String,
    val errors: Map<String, String>? = null,
    val correlationId: String? = null
)
