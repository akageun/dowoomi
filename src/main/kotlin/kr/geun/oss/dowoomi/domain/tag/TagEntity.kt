package kr.geun.oss.dowoomi.domain.tag

import java.time.LocalDateTime

/**
 * 태그 마스터 데이터 클래스
 */
data class TagEntity(
    val id: Long? = null,
    var name: String,
    var createdAt: LocalDateTime = LocalDateTime.now()
)
