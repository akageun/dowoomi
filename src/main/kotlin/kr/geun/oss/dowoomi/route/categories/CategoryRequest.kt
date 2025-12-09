package kr.geun.oss.dowoomi.route.categories

/**
 * 카테고리 생성 요청
 */
data class CreateCategoryRequest(
    val name: String,
    val description: String? = null,
    val label: CategoryLabel? = null
)

/**
 * 카테고리 수정 요청
 */
data class UpdateCategoryRequest(
    val name: String? = null,
    val description: String? = null,
    val label: CategoryLabel? = null
)
