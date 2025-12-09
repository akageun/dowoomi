package kr.geun.oss.dowoomi.route.tag

/**
 * 태그 생성 요청
 */
data class CreateTagRequest(
  val name: String
)

/**
 * 태그 수정 요청
 */
data class UpdateTagRequest(
  val name: String
)
