package kr.geun.oss.dowoomi.domain.task.assignee

import jakarta.persistence.*
import kr.geun.oss.dowoomi.domain.member.MemberEntity
import kr.geun.oss.dowoomi.domain.task.TasksEntity
import java.io.Serializable

/**
 * Task 담당자 엔티티 (N:N 관계)
 */
@Entity
@Table(
    name = "task_assignees",
    indexes = [
        Index(name = "idx_task_assignees_member_id", columnList = "member_id")
    ]
)
@IdClass(TaskAssigneeId::class)
class TaskAssigneeEntity(
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    var task: TasksEntity,

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: MemberEntity
)

/**
 * 복합키 클래스
 */
data class TaskAssigneeId(
    val task: Long? = null,
    val member: Long? = null
) : Serializable
