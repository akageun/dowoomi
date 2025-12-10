package kr.geun.oss.dowoomi.domain.assignee

import java.time.LocalDateTime

data class Assignee(
  val categoryId: Long,
  val name: String,
  val memo: String? = null,
  val createdAt: LocalDateTime,
  val updatedAt: LocalDateTime
) {
}
