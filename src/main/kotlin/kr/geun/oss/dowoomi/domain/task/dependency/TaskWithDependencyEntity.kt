package kr.geun.oss.dowoomi.domain.task.dependency

/**
 * Task와 Dependency Task 조인 결과를 담는 데이터 클래스
 */
data class TaskWithDependencyEntity(
    val taskId: Long,
    val dependencyTaskId: Long,
    val dependencyTitle: String
)
