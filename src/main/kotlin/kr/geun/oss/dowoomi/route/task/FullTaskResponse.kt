package kr.geun.oss.dowoomi.route.task

import kr.geun.oss.dowoomi.route.AssigneeResponse
import kr.geun.oss.dowoomi.route.DependencyResponse
import kr.geun.oss.dowoomi.route.LinkResponse
import kr.geun.oss.dowoomi.route.ParentResponse
import java.time.LocalDate

data class FullTaskResponse(
  val id: Long,
  val title: String,
  val description: String?,
  val categoryId: Long?,
  val categoryName: String?,
  val progress: String,
  val lifecycle: String,
  val startDate: LocalDate?,
  val endDate: LocalDate?,
  val tags: List<String>,
  val assignees: List<AssigneeResponse>,
  val links: List<LinkResponse>,
  val dependencies: List<DependencyResponse>,
  val parents: List<ParentResponse>,
  val createdAt: String,
  val updatedAt: String
)
