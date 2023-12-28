package org.blog.api.exception

import org.springframework.http.HttpStatus

enum class ErrorType(
    val httpStatus: HttpStatus,
    val code: ErrorCode,
    val message: String?
) {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, ErrorCode.E400, null),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "알 수 없는 에러가 발생했습니다."),
    ILLEGAL_ARGUMENT(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E501, "서버 설정 오류가 있습니다."),
    BLOG_CLIENT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E502, null),
}

enum class ErrorCode {
    E400,
    E500,
    E501,
    E502
}
