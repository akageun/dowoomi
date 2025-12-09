package kr.geun.oss.dowoomi.domain.task.assignee

/**
 * Task 담당자 데이터 클래스 (N:N 관계)
 */
data class TaskAssigneeEntity(
    val taskId: Long,
    val memberId: Long
)
