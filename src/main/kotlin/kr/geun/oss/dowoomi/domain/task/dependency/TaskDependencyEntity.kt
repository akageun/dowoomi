package kr.geun.oss.dowoomi.domain.task.dependency

import jakarta.persistence.*
import kr.geun.oss.dowoomi.domain.task.TasksEntity
import java.io.Serializable

/**
 * Task 선행 작업(의존성) 엔티티 (self-reference N:N)
 * dependencies: string[]
 */
@Entity
@Table(
    name = "task_dependencies",
    indexes = [
        Index(name = "idx_task_deps_dependency", columnList = "dependency_task_id")
    ]
)
@IdClass(TaskDependencyId::class)
class TaskDependencyEntity(
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    var task: TasksEntity,

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dependency_task_id", nullable = false)
    var dependencyTask: TasksEntity
)

/**
 * 복합키 클래스
 */
data class TaskDependencyId(
    val task: Long? = null,
    val dependencyTask: Long? = null
) : Serializable
