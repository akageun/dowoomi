package kr.geun.oss.dowoomi.domain.task.dependency

/**
 * Task 선행 작업(의존성) 데이터 클래스 (self-reference N:N)
 */
data class TaskDependencyEntity(
    val taskId: Long,
    val dependencyTaskId: Long
)
