package org.blog.api.exception

import org.blog.client.BlogClientException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(ApiException::class)
    fun handleRuntimeException(e: ApiException): ResponseEntity<ApiErrorResponse> {
        return ResponseEntity(
            ApiErrorResponse(e.errorType.code, e.errorType.message),
            e.errorType.httpStatus
        )
    }

    @ExceptionHandler(BlogClientException::class)
    fun handleRuntimeException(e: BlogClientException): ResponseEntity<ApiErrorResponse> {
        return ResponseEntity(
            ApiErrorResponse(ErrorType.BLOG_CLIENT_ERROR.code, e.message),
            ErrorType.BLOG_CLIENT_ERROR.httpStatus
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleRuntimeException(e: MethodArgumentNotValidException): ResponseEntity<ApiErrorResponse> {
        return ResponseEntity(
            ApiErrorResponse(ErrorType.BAD_REQUEST.code, e.message),
            ErrorType.BAD_REQUEST.httpStatus
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleRuntimeException(e: Exception): ResponseEntity<ApiErrorResponse> {
        val apiErrorResponse = ApiErrorResponse(
            ErrorType.INTERNAL_SERVER_ERROR.code,
            e.message ?: ErrorType.INTERNAL_SERVER_ERROR.message ?: "알 수 없는 에러가 발생했습니다."
        )
        return ResponseEntity(apiErrorResponse, ErrorType.INTERNAL_SERVER_ERROR.httpStatus)
    }
}

data class ApiErrorResponse(
    val code: ErrorCode,
    val message: String?
)
