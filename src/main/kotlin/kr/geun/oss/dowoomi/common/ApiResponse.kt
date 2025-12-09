package kr.geun.oss.dowoomi.common

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

/**
 * API 응답 공통 모델
 *
 * @param T 응답 데이터 타입
 * @author akageun
 * @since 2025-07-11
 */
data class ApiResponse<T>(
  @get:JsonProperty("success")
  val success: Boolean = true,

  @get:JsonProperty("message")
  val message: String,

  @field:JsonInclude(JsonInclude.Include.NON_NULL)
  @get:JsonProperty("data")
  val data: T? = null,

  @field:JsonInclude(JsonInclude.Include.NON_NULL)
  @get:JsonProperty("error")
  val error: ErrorType? = null,

  @get:JsonProperty("timestamp")
  val timestamp: Long = System.currentTimeMillis()
) {
  companion object {
    /**
     * 성공 응답 생성
     */
    fun <T> ok(data: T): ApiResponse<T> {
      return ApiResponse(
        success = true,
        message = "Success",
        data = data
      )
    }

    /**
     * 성공 응답 생성 (메시지 포함)
     */
    fun <T> ok(data: T, message: String): ApiResponse<T> {
      return ApiResponse(
        success = true,
        message = message,
        data = data
      )
    }

    /**
     * 성공 응답 생성 (데이터 없음)
     */
    fun ok(): ApiResponse<Unit> {
      return ApiResponse(
        success = true,
        message = "Success"
      )
    }

    /**
     * 성공 응답 생성 (메시지만)
     */
    fun ok(message: String): ApiResponse<Unit> {
      return ApiResponse(
        success = true,
        message = message
      )
    }

    /**
     * 에러 응답 생성
     */
    fun <T> error(errorType: ErrorType): ApiResponse<T> {
      return ApiResponse(
        success = false,
        message = errorType.message,
        error = errorType
      )
    }

    /**
     * 에러 응답 생성 (커스텀 메시지)
     */
    fun <T> error(errorType: ErrorType, customMessage: String): ApiResponse<T> {
      return ApiResponse(
        success = false,
        message = customMessage,
        error = errorType
      )
    }

    /**
     * 에러 응답 생성 (메시지만)
     */
    fun <T> error(message: String): ApiResponse<T> {
      return ApiResponse(
        success = false,
        message = message,
        error = ErrorType.UNKNOWN_ERROR
      )
    }
  }

  /**
   * ResponseEntity로 변환 (200 OK)
   */
  fun toResponseEntity(): ResponseEntity<ApiResponse<T>> {
    return ResponseEntity.ok(this)
  }

  /**
   * ResponseEntity로 변환 (상태 코드 지정)
   */
  fun toResponseEntity(status: HttpStatus): ResponseEntity<ApiResponse<T>> {
    return ResponseEntity.status(status).body(this)
  }

  /**
   * 성공 여부 확인
   */
  fun isSuccess(): Boolean {
    return success
  }

  /**
   * 실패 여부 확인
   */
  fun isError(): Boolean {
    return !success
  }
}
