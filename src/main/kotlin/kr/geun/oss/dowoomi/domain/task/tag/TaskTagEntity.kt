package kr.geun.oss.dowoomi.domain.task.tag

/**
 * Task ↔ Tag 매핑 데이터 클래스 (N:N 관계)
 */
data class TaskTagEntity(
    val taskId: Long,
    val tagId: Long
)
