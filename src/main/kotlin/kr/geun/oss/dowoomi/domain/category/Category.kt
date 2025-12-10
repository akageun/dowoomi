package kr.geun.oss.dowoomi.domain.category

import java.time.LocalDateTime

data class Category(
  val categoryId: Long,
  val name: String,
  val description: String? = null,
  val createdAt: LocalDateTime,
  val updatedAt: LocalDateTime
) {
}
