package com.app.softlancer.web.controller
import com.app.softlancer.SecurityException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

data class ErrorResponse(
    val timestamp: Long,
    val status: HttpStatus,
    val error: String?,
    val path: String,
)


@RestControllerAdvice
class ControllerAdvise {

    @ExceptionHandler(SecurityException::class)
    fun securityDomainExceptionHandler(
        exception: SecurityException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            System.currentTimeMillis(),
            HttpStatus.UNAUTHORIZED,
            exception.message,
            request.requestURI
        )
        return ResponseEntity(errorResponse, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidExceptionHandler(
        exception: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            System.currentTimeMillis(),
            HttpStatus.BAD_REQUEST,
            exception.bindingResult.allErrors.joinToString(", ") { it.defaultMessage ?: "" },
            request.requestURI
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun globalExceptionHandler(
        exception: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {

        val errorResponse = ErrorResponse(
            System.currentTimeMillis(),
            HttpStatus.INTERNAL_SERVER_ERROR,
            exception.message,
            request.requestURI

        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

}