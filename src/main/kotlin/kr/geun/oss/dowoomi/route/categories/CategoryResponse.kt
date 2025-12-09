package kr.geun.oss.dowoomi.route.categories

import kr.geun.oss.dowoomi.domain.category.CategoryEntity

/**
 * 카테고리 응답
 */
data class CategoryResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val label: CategoryLabel?,
    val createdAt: String,
    val updatedAt: String
)

data class CategoryListResponse(
    val categories: List<CategoryResponse>,

)

/**
 * Entity to Response 변환
 */
fun CategoryEntity.toResponse(label: CategoryLabel? = null) = CategoryResponse(
    id = this.id!!,
    name = this.name,
    description = this.description,
    label = label,
    createdAt = this.createdAt.toString(),
    updatedAt = this.updatedAt.toString()
)
