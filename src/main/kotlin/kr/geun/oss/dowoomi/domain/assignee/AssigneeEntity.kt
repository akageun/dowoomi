package kr.geun.oss.dowoomi.domain.assignee

import java.time.LocalDateTime

/**
 * 담당자(Assignee) 데이터 클래스
 */
data class AssigneeEntity(
    val id: Long? = null,
    var name: String,
    var memo: String? = null,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
