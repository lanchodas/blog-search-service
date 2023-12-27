package org.blog.api.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(e: RuntimeException): ResponseEntity<ApiErrorResponse> {
        val apiErrorResponse = ApiErrorResponse(500, e.message ?: "알 수 없는 에러가 발생했습니다.")
        return ResponseEntity(apiErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}

data class ApiErrorResponse(
    val code: Int,
    val message: String
)
