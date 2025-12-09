package kr.geun.oss.dowoomi.route.tag

import kr.geun.oss.dowoomi.domain.tag.TagEntity

/**
 * 태그 응답
 */
data class TagResponse(
  val id: Long,
  val name: String,
  val createdAt: String
)

/**
 * 태그 목록 응답
 */
data class TagListResponse(
  val tags: List<TagResponse>
)

/**
 * Entity to Response 변환
 */
fun TagEntity.toResponse() = TagResponse(
  id = this.id!!,
  name = this.name,
  createdAt = this.createdAt.toString()
)
