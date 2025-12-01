package kr.geun.oss.dowoomi.common.exception

import kr.geun.oss.dowoomi.common.ErrorType
import org.springframework.http.HttpStatus

/**
 * 비즈니스 로직 예외
 */
open class BusinessException(
    val errorType: ErrorType,
    override val message: String? = errorType.message,
    val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    cause: Throwable? = null
) : RuntimeException(message, cause)

/**
 * 리소스를 찾을 수 없을 때 발생하는 예외
 */
class ResourceNotFoundException(
    resourceName: String,
    identifier: Any? = null
) : BusinessException(
    errorType = ErrorType.NOT_FOUND,
    message = if (identifier != null) {
        "$resourceName(을)를 찾을 수 없습니다: $identifier"
    } else {
        "$resourceName(을)를 찾을 수 없습니다."
    },
    httpStatus = HttpStatus.NOT_FOUND
)

/**
 * 중복 리소스 예외
 */
class DuplicateResourceException(
    resourceName: String,
    identifier: Any? = null
) : BusinessException(
    errorType = ErrorType.INVALID_REQUEST,
    message = if (identifier != null) {
        "이미 존재하는 $resourceName 입니다: $identifier"
    } else {
        "이미 존재하는 $resourceName 입니다."
    },
    httpStatus = HttpStatus.CONFLICT
)

/**
 * 인증 실패 예외
 */
class UnauthorizedException(
    message: String = "인증이 필요합니다."
) : BusinessException(
    errorType = ErrorType.UNAUTHORIZED,
    message = message,
    httpStatus = HttpStatus.UNAUTHORIZED
)

/**
 * 권한 없음 예외
 */
class ForbiddenException(
    message: String = "접근 권한이 없습니다."
) : BusinessException(
    errorType = ErrorType.FORBIDDEN,
    message = message,
    httpStatus = HttpStatus.FORBIDDEN
)

/**
 * 잘못된 요청 예외
 */
class BadRequestException(
    message: String = "잘못된 요청입니다."
) : BusinessException(
    errorType = ErrorType.INVALID_REQUEST,
    message = message,
    httpStatus = HttpStatus.BAD_REQUEST
)
