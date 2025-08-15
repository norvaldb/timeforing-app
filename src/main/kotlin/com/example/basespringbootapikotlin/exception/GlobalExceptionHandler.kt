package com.example.basespringbootapikotlin.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime

/**
 * Global exception handler for user-friendly error responses
 */
@ControllerAdvice
class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
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
            errors = errors
        )
        
        return ResponseEntity.badRequest().body(errorResponse)
    }
    
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        ex: IllegalArgumentException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val status = when (ex.message) {
            "Epost addressen er allerede registrert" -> HttpStatus.CONFLICT
            "Bruker ikke funnet" -> HttpStatus.NOT_FOUND
            else -> HttpStatus.BAD_REQUEST
        }
        
        val code = when (ex.message) {
            "Epost addressen er allerede registrert" -> "DUPLICATE_EMAIL"
            "Bruker ikke funnet" -> "USER_NOT_FOUND"
            else -> "INVALID_REQUEST"
        }
        
        val errorResponse = ErrorResponse(
            message = ex.message ?: "Ugyldig forespørsel",
            code = code,
            status = status.value(),
            timestamp = LocalDateTime.now(),
            path = request.getDescription(false)
        )
        
        return ResponseEntity.status(status).body(errorResponse)
    }
    
    @ExceptionHandler(Exception::class)
    fun handleGeneralException(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            message = "Noe gikk galt, prøv igjen senere",
            code = "INTERNAL_ERROR",
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            timestamp = LocalDateTime.now(),
            path = request.getDescription(false)
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
    val errors: Map<String, String>? = null
)
