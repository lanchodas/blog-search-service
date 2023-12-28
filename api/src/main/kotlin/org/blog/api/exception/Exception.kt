package org.blog.api.exception

// API 서버 내부의 설정 오류 등에 사용합니다.
class ApiException(val errorType: ErrorType) : RuntimeException(errorType.message)
