package kr.geun.oss.dowoomi.common

import com.fasterxml.jackson.annotation.JsonProperty
import kotlin.collections.find

/**
 * 에러 타입 열거형
 */
enum class ErrorType(
  @get:JsonProperty("code")
    val code: String,
  @get:JsonProperty("message")
    val message: String
) {
    // 일반적인 에러
    INVALID_REQUEST("E001", "잘못된 요청입니다."),
    UNAUTHORIZED("E002", "인증이 필요합니다."),
    FORBIDDEN("E003", "접근 권한이 없습니다."),
    NOT_FOUND("E004", "요청한 리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR("E005", "서버 내부 오류가 발생했습니다."),

    // 기타
    UNKNOWN_ERROR("U001", "알 수 없는 오류가 발생했습니다.");

    companion object {
        fun fromCode(code: String): ErrorType? {
            return ErrorType.entries.find { it.code == code }
        }
    }
}
