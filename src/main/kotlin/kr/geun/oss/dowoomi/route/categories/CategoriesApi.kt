package kr.geun.oss.dowoomi.route.categories

import kr.geun.oss.dowoomi.common.ApiResponse
import kr.geun.oss.dowoomi.common.exception.ResourceNotFoundException
import kr.geun.oss.dowoomi.common.util.LoggerUtil
import kr.geun.oss.dowoomi.domain.category.CategoryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 카테고리 API 컨트롤러
 */
@RestController
@RequestMapping("/api/categories")
class CategoriesApi(
  private val categoryService: CategoryService
) {

  companion object : LoggerUtil()

  /**
   * 카테고리 생성
   * POST /api/categories
   */
  @PostMapping
  fun createCategory(
    @RequestBody request: CreateCategoryRequest
  ): ResponseEntity<ApiResponse<CategoryResponse>> {
    logger.info("Creating category: ${request.name}")
    val category = categoryService.createCategory(request.name, request.description)
    return ApiResponse.ok(
      data = category.toResponse(request.label),
      message = "카테고리가 생성되었습니다."
    ).toResponseEntity(HttpStatus.CREATED)
  }

  /**
   * 카테고리 전체 조회
   * GET /api/categories
   */
  @GetMapping
  fun getAllCategories(): ResponseEntity<ApiResponse<CategoryListResponse>> {
    logger.info("Fetching all categories")

    return ApiResponse.ok(
      data = CategoryListResponse(
        categories = categoryService.findAll().map { it.toResponse() }
      )
    ).toResponseEntity()
  }

  /**
   * 카테고리 단건 조회
   * GET /api/categories/{id}
   */
  @GetMapping("/{id}")
  fun getCategory(@PathVariable id: Long): ResponseEntity<ApiResponse<CategoryResponse>> {
    logger.info("Fetching category: $id")
    val category = categoryService.findById(id)
      ?: throw ResourceNotFoundException("카테고리", id)
    return ApiResponse.ok(category.toResponse()).toResponseEntity()
  }

  /**
   * 카테고리 수정
   * PUT /api/categories/{id}
   */
  @PutMapping("/{id}")
  fun updateCategory(
    @PathVariable id: Long,
    @RequestBody request: UpdateCategoryRequest
  ): ResponseEntity<ApiResponse<CategoryResponse>> {
    logger.info("Updating category: $id")
    val category = categoryService.updateCategory(id, request.name, request.description)
    return ApiResponse.ok(category.toResponse(request.label), "카테고리가 수정되었습니다.").toResponseEntity()
  }

  /**
   * 카테고리 삭제
   * DELETE /api/categories/{id}
   */
  @DeleteMapping("/{id}")
  fun deleteCategory(@PathVariable id: Long): ResponseEntity<ApiResponse<Unit>> {
    logger.info("Deleting category: $id")
    categoryService.deleteCategory(id)
    return ApiResponse.ok("카테고리가 삭제되었습니다.").toResponseEntity()
  }

}
