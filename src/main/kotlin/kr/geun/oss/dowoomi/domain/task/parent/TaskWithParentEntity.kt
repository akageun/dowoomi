package kr.geun.oss.dowoomi.domain.task.parent

/**
 * Task와 Parent Task 조인 결과를 담는 데이터 클래스
 */
data class TaskWithParentEntity(
    val taskId: Long,
    val parentTaskId: Long,
    val parentTitle: String
)
