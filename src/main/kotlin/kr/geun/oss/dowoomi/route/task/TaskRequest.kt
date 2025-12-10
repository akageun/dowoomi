package kr.geun.oss.dowoomi.route.task

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kr.geun.oss.dowoomi.domain.task.TaskLifecycle
import kr.geun.oss.dowoomi.domain.task.TaskProgress
import java.time.LocalDate

data class CreateTaskRequest(
  @field:NotBlank(message = "제목은 필수입니다")
  @field:Size(min = 1, max = 200, message = "제목은 1자 이상 200자 이하여야 합니다")
  val title: String,

  @field:Size(max = 5000, message = "설명은 5000자 이하여야 합니다")
  val description: String? = null,

  val categoryId: Long? = null,
  val parentTaskIds: List<Long>? = null,
  val dependencyTaskIds: List<Long>? = null,
  val progress: String? = null, // todo, in_progress, done
  val lifecycle: String? = null, // active, draft, deleted
  val startDate: LocalDate? = null,
  val endDate: LocalDate? = null,
  val tags: List<Long> = emptyList(),
  val assignees: List<Long> = emptyList(),

  @field:Valid
  val links: List<CreateTaskLinkRequest> = emptyList()
) {
  fun convertProgress(): TaskProgress {
    return this.progress?.let {
      try {
        TaskProgress.fromValue(it)
      } catch (e: IllegalArgumentException) {
        TaskProgress.TODO
      }
    } ?: TaskProgress.TODO
  }

  fun convertLifecycle(): TaskLifecycle {
    return this.progress?.let {
      try {
        TaskLifecycle.fromValue(it)
      } catch (e: IllegalArgumentException) {
        TaskLifecycle.ACTIVE
      }
    } ?: TaskLifecycle.ACTIVE
  }
}

data class CreateTaskLinkRequest(
  @field:NotBlank(message = "링크 URL은 필수입니다")
  val url: String,

  @field:NotBlank(message = "링크 이름은 필수입니다")
  @field:Size(min = 1, max = 100, message = "링크 이름은 1자 이상 100자 이하여야 합니다")
  val name: String,

  @field:Size(max = 500, message = "링크 설명은 500자 이하여야 합니다")
  val description: String? = null
)
