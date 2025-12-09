package kr.geun.oss.dowoomi.domain.category

import java.time.LocalDateTime

/**
 * 카테고리 데이터 클래스
 * - 예: 'GW 서비스', 'IDS', 'KW 서비스', '글쓰기' 등
 */
data class CategoryEntity(
    val id: Long? = null,
    var name: String,
    var description: String? = null,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
