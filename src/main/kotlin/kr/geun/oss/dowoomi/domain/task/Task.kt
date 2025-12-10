package kr.geun.oss.dowoomi.domain.task

import kr.geun.oss.dowoomi.domain.assignee.Assignee
import kr.geun.oss.dowoomi.domain.category.Category
import kr.geun.oss.dowoomi.domain.tag.Tag
import kr.geun.oss.dowoomi.domain.task.link.TaskLink
import java.time.LocalDate
import java.time.LocalDateTime

data class Task(
  val taskId: Long,
  val title: String,
  val description: String? = null,
  val category: Category? = null,
  val statusProgress: String,
  val statusLifecycle: String,
  val startDate: LocalDate? = null,
  val endDate: LocalDate? = null,

  val assignees: List<Assignee>? = emptyList(),
  val tags : List<Tag>? = emptyList(),
  val links: List<TaskLink>? = emptyList(),

  val createdAt: LocalDateTime = LocalDateTime.now(),
  val updatedAt: LocalDateTime = LocalDateTime.now()
) {
}
