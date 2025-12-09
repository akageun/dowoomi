package kr.geun.oss.dowoomi.domain.task.parent

/**
 * Task 상위 작업 관계 데이터 클래스 (self-reference N:N)
 */
data class TaskParentEntity(
    val taskId: Long,
    val parentTaskId: Long
)
