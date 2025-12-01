package kr.geun.oss.dowoomi.route

import kr.geun.oss.dowoomi.common.util.LoggerUtil
import kr.geun.oss.dowoomi.domain.category.CategoryEntity
import kr.geun.oss.dowoomi.domain.category.CategoryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 카테고리 API 컨트롤러
 */
@RestController
@RequestMapping("/api/categories")
class CategoryController(
    private val categoryService: CategoryService
) {
    companion object : LoggerUtil()

    /**
     * 카테고리 생성
     * POST /api/categories
     */
    @PostMapping
    fun createCategory(@RequestBody request: CreateCategoryRequest): ResponseEntity<CategoryResponse> {
        logger.info("Creating category: ${request.name}")
        val category = categoryService.createCategory(request.name, request.description)
        return ResponseEntity.status(HttpStatus.CREATED).body(category.toResponse())
    }

    /**
     * 카테고리 전체 조회
     * GET /api/categories
     */
    @GetMapping
    fun getAllCategories(): ResponseEntity<List<CategoryResponse>> {
        logger.info("Fetching all categories")
        val categories = categoryService.findAll().map { it.toResponse() }
        return ResponseEntity.ok(categories)
    }

    /**
     * 카테고리 단건 조회
     * GET /api/categories/{id}
     */
    @GetMapping("/{id}")
    fun getCategory(@PathVariable id: Long): ResponseEntity<CategoryResponse> {
        logger.info("Fetching category: $id")
        val category = categoryService.findById(id)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(category.toResponse())
    }

    /**
     * 카테고리 수정
     * PUT /api/categories/{id}
     */
    @PutMapping("/{id}")
    fun updateCategory(
        @PathVariable id: Long,
        @RequestBody request: UpdateCategoryRequest
    ): ResponseEntity<CategoryResponse> {
        logger.info("Updating category: $id")
        val category = categoryService.updateCategory(id, request.name, request.description)
        return ResponseEntity.ok(category.toResponse())
    }

    /**
     * 카테고리 삭제
     * DELETE /api/categories/{id}
     */
    @DeleteMapping("/{id}")
    fun deleteCategory(@PathVariable id: Long): ResponseEntity<Void> {
        logger.info("Deleting category: $id")
        categoryService.deleteCategory(id)
        return ResponseEntity.noContent().build()
    }
}

/**
 * 카테고리 생성 요청
 */
data class CreateCategoryRequest(
    val name: String,
    val description: String? = null
)

/**
 * 카테고리 수정 요청
 */
data class UpdateCategoryRequest(
    val name: String? = null,
    val description: String? = null
)

/**
 * 카테고리 응답
 */
data class CategoryResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val createdAt: String,
    val updatedAt: String
)

/**
 * Entity to Response 변환
 */
fun CategoryEntity.toResponse() = CategoryResponse(
    id = this.id!!,
    name = this.name,
    description = this.description,
    createdAt = this.createdAt.toString(),
    updatedAt = this.updatedAt.toString()
)
