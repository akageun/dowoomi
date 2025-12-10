package kr.geun.oss.dowoomi.domain.category

import java.time.LocalDateTime

/**
 * 카테고리 데이터 클래스
 */
data class CategoryEntity(
    val id: Long? = null,
    val name: String,
    val description: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
