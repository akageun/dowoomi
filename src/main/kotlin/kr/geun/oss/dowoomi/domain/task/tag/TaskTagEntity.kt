package kr.geun.oss.dowoomi.domain.task.tag

import jakarta.persistence.*
import kr.geun.oss.dowoomi.domain.tag.TagEntity
import kr.geun.oss.dowoomi.domain.task.TasksEntity
import java.io.Serializable

/**
 * Task ↔ Tag 매핑 엔티티 (N:N 관계)
 */
@Entity
@Table(name = "task_tags")
@IdClass(TaskTagId::class)
class TaskTagEntity(
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    var task: TasksEntity,

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    var tag: TagEntity
)

/**
 * 복합키 클래스
 */
data class TaskTagId(
    val task: Long? = null,
    val tag: Long? = null
) : Serializable
