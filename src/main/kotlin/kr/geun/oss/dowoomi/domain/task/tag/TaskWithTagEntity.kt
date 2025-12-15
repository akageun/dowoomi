package kr.geun.oss.dowoomi.domain.task.tag

import java.time.LocalDateTime

/**
 * Task와 Tag 조인 결과를 담는 데이터 클래스
 */
data class TaskWithTagEntity(
    val id: Long,
    val name: String,
    val createdAt: LocalDateTime,
    val taskId: Long
)
