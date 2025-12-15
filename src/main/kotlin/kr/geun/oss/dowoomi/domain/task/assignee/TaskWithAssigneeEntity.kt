package kr.geun.oss.dowoomi.domain.task.assignee

/**
 * Task와 Assignee 조인 결과를 담는 데이터 클래스
 */
data class TaskWithAssigneeEntity(
    val taskId: Long,
    val memberId: Long,
    val memberName: String,
    val memberMemo: String?
)
