package kr.geun.oss.dowoomi.domain.task

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 메인 Task 데이터 클래스
 */
data class TasksEntity(
  val id: Long? = null,
  var title: String,
  var description: String? = null,
  var categoryId: Long? = null, //category table 조인해야함.
  var statusProgress: String = TaskProgress.TODO.value,
  var statusLifecycle: String = TaskLifecycle.ACTIVE.value,
  var startDate: LocalDate? = null,
  var endDate: LocalDate? = null,
  var createdAt: LocalDateTime = LocalDateTime.now(),
  var updatedAt: LocalDateTime = LocalDateTime.now()
) {
  fun softDelete() {
    this.statusLifecycle = TaskLifecycle.DELETED.value
    this.updatedAt = LocalDateTime.now()
  }

  fun getProgressEnum(): TaskProgress = TaskProgress.fromValue(statusProgress)
  fun getLifecycleEnum(): TaskLifecycle = TaskLifecycle.fromValue(statusLifecycle)

  fun setProgress(progress: TaskProgress) {
    this.statusProgress = progress.value
    this.updatedAt = LocalDateTime.now()
  }

  fun setLifecycle(lifecycle: TaskLifecycle) {
    this.statusLifecycle = lifecycle.value
    this.updatedAt = LocalDateTime.now()
  }

  fun isDeleted(): Boolean = statusLifecycle == TaskLifecycle.DELETED.value
  fun isActive(): Boolean = statusLifecycle == TaskLifecycle.ACTIVE.value
  fun isDraft(): Boolean = statusLifecycle == TaskLifecycle.DRAFT.value
  fun isTodo(): Boolean = statusProgress == TaskProgress.TODO.value
  fun isInProgress(): Boolean = statusProgress == TaskProgress.IN_PROGRESS.value
  fun isDone(): Boolean = statusProgress == TaskProgress.DONE.value
}

  /**
   * 진행 상태: todo / in_progress / done
   */
  enum class TaskProgress(val value: String) {
    TODO("todo"),
    IN_PROGRESS("in_progress"),
    DONE("done");

    companion object {
      fun fromValue(value: String): TaskProgress =
        entries.find { it.value == value }
          ?: throw IllegalArgumentException("Unknown TaskProgress: $value")
    }
  }

  /**
   * 생명주기: active / draft / deleted
   */
  enum class TaskLifecycle(val value: String) {
    ACTIVE("active"),
    DRAFT("draft"),
    DELETED("deleted");

    companion object {
      fun fromValue(value: String): TaskLifecycle =
        entries.find { it.value == value }
          ?: throw IllegalArgumentException("Unknown TaskLifecycle: $value")
    }
  }
