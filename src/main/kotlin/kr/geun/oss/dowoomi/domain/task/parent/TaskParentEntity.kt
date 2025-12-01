package kr.geun.oss.dowoomi.domain.task.parent

import jakarta.persistence.*
import kr.geun.oss.dowoomi.domain.task.TasksEntity
import java.io.Serializable

/**
 * Task 상위 작업 관계 엔티티 (self-reference N:N)
 * parents: string[]
 */
@Entity
@Table(
    name = "task_parents",
    indexes = [
        Index(name = "idx_task_parents_parent", columnList = "parent_task_id")
    ]
)
@IdClass(TaskParentId::class)
class TaskParentEntity(
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    var task: TasksEntity,

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_task_id", nullable = false)
    var parentTask: TasksEntity
)

/**
 * 복합키 클래스
 */
data class TaskParentId(
    val task: Long? = null,
    val parentTask: Long? = null
) : Serializable
