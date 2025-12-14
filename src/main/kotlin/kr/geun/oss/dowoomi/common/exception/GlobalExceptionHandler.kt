package kr.geun.oss.dowoomi.common.exception

import jakarta.servlet.http.HttpServletRequest
import kr.geun.oss.dowoomi.common.ApiResponse
import kr.geun.oss.dowoomi.common.ErrorType
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import java.io.FileNotFoundException

/**
 * 전역 예외 처리 핸들러
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * 비즈니스 예외 처리
     */
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ApiResponse<Unit>> {
        log.warn("BusinessException [{}]: {}", e.errorType.code, e.message)
        return ApiResponse.error<Unit>(e.errorType, e.message ?: e.errorType.message)
            .toResponseEntity(e.httpStatus)
    }

    /**
     * 리소스를 찾을 수 없는 경우
     */
    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(e: ResourceNotFoundException): ResponseEntity<ApiResponse<Unit>> {
        log.warn("ResourceNotFoundException: {}", e.message)
        return ApiResponse.error<Unit>(ErrorType.NOT_FOUND, e.message ?: "리소스를 찾을 수 없습니다.")
            .toResponseEntity(HttpStatus.NOT_FOUND)
    }

    /**
     * 중복 리소스 예외 처리
     */
    @ExceptionHandler(DuplicateResourceException::class)
    fun handleDuplicateResourceException(e: DuplicateResourceException): ResponseEntity<ApiResponse<Unit>> {
        log.warn("DuplicateResourceException: {}", e.message)
        return ApiResponse.error<Unit>(e.errorType, e.message ?: "이미 존재하는 리소스입니다.")
            .toResponseEntity(HttpStatus.CONFLICT)
    }

    /**
     * 인증 실패 예외 처리
     */
    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(e: UnauthorizedException): ResponseEntity<ApiResponse<Unit>> {
        log.warn("UnauthorizedException: {}", e.message)
        return ApiResponse.error<Unit>(ErrorType.UNAUTHORIZED, e.message ?: "인증이 필요합니다.")
            .toResponseEntity(HttpStatus.UNAUTHORIZED)
    }

    /**
     * 권한 없음 예외 처리
     */
    @ExceptionHandler(ForbiddenException::class)
    fun handleForbiddenException(e: ForbiddenException): ResponseEntity<ApiResponse<Unit>> {
        log.warn("ForbiddenException: {}", e.message)
        return ApiResponse.error<Unit>(ErrorType.FORBIDDEN, e.message ?: "접근 권한이 없습니다.")
            .toResponseEntity(HttpStatus.FORBIDDEN)
    }

    /**
     * 잘못된 요청 예외 처리
     */
    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(e: BadRequestException): ResponseEntity<ApiResponse<Unit>> {
        log.warn("BadRequestException: {}", e.message)
        return ApiResponse.error<Unit>(ErrorType.INVALID_REQUEST, e.message ?: "잘못된 요청입니다.")
            .toResponseEntity(HttpStatus.BAD_REQUEST)
    }

    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ApiResponse<Unit>> {
        log.warn("IllegalArgumentException: {}", e.message)
        return ApiResponse.error<Unit>(ErrorType.INVALID_REQUEST, e.message ?: "잘못된 요청입니다.")
            .toResponseEntity(HttpStatus.BAD_REQUEST)
    }

    /**
     * Validation 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Unit>> {
        val errorMessages = e.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        log.warn("Validation failed: {}", errorMessages)
        return ApiResponse.error<Unit>(ErrorType.INVALID_REQUEST, errorMessages)
            .toResponseEntity(HttpStatus.BAD_REQUEST)
    }

    /**
     * 요청 파라미터 누락
     */
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameterException(e: MissingServletRequestParameterException): ResponseEntity<ApiResponse<Unit>> {
        log.warn("Missing parameter: {}", e.parameterName)
        return ApiResponse.error<Unit>(ErrorType.INVALID_REQUEST, "필수 파라미터가 누락되었습니다: ${e.parameterName}")
            .toResponseEntity(HttpStatus.BAD_REQUEST)
    }

    /**
     * 파라미터 타입 불일치
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(e: MethodArgumentTypeMismatchException): ResponseEntity<ApiResponse<Unit>> {
        log.warn("Type mismatch: {} = {}", e.name, e.value)
        return ApiResponse.error<Unit>(ErrorType.INVALID_REQUEST, "파라미터 타입이 올바르지 않습니다: ${e.name}")
            .toResponseEntity(HttpStatus.BAD_REQUEST)
    }

    /**
     * 요청 본문 파싱 실패
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): ResponseEntity<ApiResponse<Unit>> {
        log.warn("Message not readable: {}", e.message)
        return ApiResponse.error<Unit>(ErrorType.INVALID_REQUEST, "요청 본문을 읽을 수 없습니다.")
            .toResponseEntity(HttpStatus.BAD_REQUEST)
    }

    /**
     * 지원하지 않는 HTTP 메서드
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupportedException(e: HttpRequestMethodNotSupportedException): ResponseEntity<ApiResponse<Unit>> {
        log.warn("Method not supported: {}", e.method)
        return ApiResponse.error<Unit>(ErrorType.INVALID_REQUEST, "지원하지 않는 HTTP 메서드입니다: ${e.method}")
            .toResponseEntity(HttpStatus.METHOD_NOT_ALLOWED)
    }

    /**
     * 지원하지 않는 미디어 타입
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleHttpMediaTypeNotSupportedException(e: HttpMediaTypeNotSupportedException): ResponseEntity<ApiResponse<Unit>> {
        log.warn("Media type not supported: {}", e.contentType)
        return ApiResponse.error<Unit>(ErrorType.INVALID_REQUEST, "지원하지 않는 미디어 타입입니다: ${e.contentType}")
            .toResponseEntity(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    }

    /**
     * 핸들러를 찾을 수 없음 (404)
     */
    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(e: NoHandlerFoundException): ResponseEntity<ApiResponse<Unit>> {
        log.warn("No handler found: {} {}", e.httpMethod, e.requestURL)
        return ApiResponse.error<Unit>(ErrorType.NOT_FOUND, "요청 경로를 찾을 수 없습니다: ${e.requestURL}")
            .toResponseEntity(HttpStatus.NOT_FOUND)
    }

    /**
     * 파일을 찾을 수 없음 (static 리소스 등)
     */
    @ExceptionHandler(FileNotFoundException::class)
    fun handleFileNotFoundException(e: FileNotFoundException, request: HttpServletRequest): ResponseEntity<ApiResponse<Unit>>? {
        val requestUri = request.requestURI
        
        // API 요청인 경우에만 404 JSON 응답 반환
        if (requestUri.startsWith("/api/")) {
            log.warn("API resource not found: {}", requestUri)
            return ApiResponse.error<Unit>(ErrorType.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다: $requestUri")
                .toResponseEntity(HttpStatus.NOT_FOUND)
        }
        
        // static 리소스 요청은 무시 (로그만 남기고 예외를 다시 던지지 않음)
        log.debug("Static resource not found (ignored): {}", e.message)
        return null
    }

    /**
     * 기타 모든 예외 처리
     */
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception, request: HttpServletRequest): ResponseEntity<ApiResponse<Unit>>? {
        val requestUri = request.requestURI
        
        // FileNotFoundException이 Exception으로 잡힌 경우 처리
        if (e is FileNotFoundException) {
            return handleFileNotFoundException(e, request)
        }
        
        // API 요청인 경우에만 500 에러 응답
        if (requestUri.startsWith("/api/")) {
            log.error("Unexpected error occurred in API: {}", e.message, e)
            return ApiResponse.error<Unit>(ErrorType.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.")
                .toResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
        
        // static 리소스 관련 에러는 로그만 남기고 무시
        log.debug("Non-API error (ignored): {} - {}", requestUri, e.message)
        return null
    }
}
