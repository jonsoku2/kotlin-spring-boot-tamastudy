package com.tamastudy.tama.handler

import com.tamastudy.tama.controller.BoardApiController
import com.tamastudy.tama.dto.Error.ErrorResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest

@ControllerAdvice(basePackageClasses = [BoardApiController::class])
class BoardErrorHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val errors = mutableListOf<String>()
        e.bindingResult.allErrors.forEach {
            errors.add(it.defaultMessage!!)
        }
        return ResponseEntity.badRequest().body(
                ErrorResponse().apply {
                    this.message = errors.joinToString()
                    this.timestamp = LocalDateTime.now()
                }
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        return ResponseEntity.badRequest().body(
                ErrorResponse().apply {
                    this.message = e.localizedMessage
                    this.timestamp = LocalDateTime.now()
                }
        )
    }
}