package kr.geun.oss.dowoomi.domain.task

import jakarta.persistence.*
import kr.geun.oss.dowoomi.domain.category.CategoryEntity
import kr.geun.oss.dowoomi.domain.task.assignee.TaskAssigneeEntity
import kr.geun.oss.dowoomi.domain.task.dependency.TaskDependencyEntity
import kr.geun.oss.dowoomi.domain.task.link.TaskLinkEntity
import kr.geun.oss.dowoomi.domain.task.parent.TaskParentEntity
import kr.geun.oss.dowoomi.domain.task.tag.TaskTagEntity
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 메인 Task 엔티티
 * - SQLite TEXT 기반 스키마에 맞춤
 * - Enum은 TEXT(lowercase)로 저장
 * - 날짜는 YYYY-MM-DD / YYYY-MM-DD HH:mm:ss 형식 TEXT로 저장
 */
@Entity
@Table(name = "tasks")
class TasksEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var title: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var category: CategoryEntity? = null,

    @Column(name = "status_progress", nullable = false)
    var statusProgress: String = TaskProgress.TODO.value,

    @Column(name = "status_lifecycle", nullable = false)
    var statusLifecycle: String = TaskLifecycle.ACTIVE.value,

    @Column(name = "start_date")
    var startDate: LocalDate? = null,

    @Column(name = "end_date")
    var endDate: LocalDate? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    // 연관 관계들
    @OneToMany(mappedBy = "task", cascade = [CascadeType.ALL], orphanRemoval = true)
    var links: MutableList<TaskLinkEntity> = mutableListOf(),

    @OneToMany(mappedBy = "task", cascade = [CascadeType.ALL], orphanRemoval = true)
    var tags: MutableList<TaskTagEntity> = mutableListOf(),

    @OneToMany(mappedBy = "task", cascade = [CascadeType.ALL], orphanRemoval = true)
    var assignees: MutableList<TaskAssigneeEntity> = mutableListOf(),

    @OneToMany(mappedBy = "task", cascade = [CascadeType.ALL], orphanRemoval = true)
    var parents: MutableList<TaskParentEntity> = mutableListOf(),

    @OneToMany(mappedBy = "task", cascade = [CascadeType.ALL], orphanRemoval = true)
    var dependencies: MutableList<TaskDependencyEntity> = mutableListOf()
) {
    @PreUpdate
    fun onPreUpdate() {
        updatedAt = LocalDateTime.now()
    }

    fun softDelete() {
        this.statusLifecycle = TaskLifecycle.DELETED.value
    }

    fun getProgressEnum(): TaskProgress = TaskProgress.fromValue(statusProgress)
    fun getLifecycleEnum(): TaskLifecycle = TaskLifecycle.fromValue(statusLifecycle)

    fun setProgress(progress: TaskProgress) {
        this.statusProgress = progress.value
    }

    fun setLifecycle(lifecycle: TaskLifecycle) {
        this.statusLifecycle = lifecycle.value
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
