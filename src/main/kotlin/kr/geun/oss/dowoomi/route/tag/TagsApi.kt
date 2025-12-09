package kr.geun.oss.dowoomi.route.tag

import kr.geun.oss.dowoomi.common.ApiResponse
import kr.geun.oss.dowoomi.common.exception.ResourceNotFoundException
import kr.geun.oss.dowoomi.common.util.LoggerUtil
import kr.geun.oss.dowoomi.domain.tag.TagService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 태그 API 컨트롤러
 */
@RestController
@RequestMapping("/api/tags")
class TagsApi(
  private val tagService: TagService
) {
  companion object : LoggerUtil()

  /**
   * 태그 생성
   * POST /api/tags
   */
  @PostMapping
  fun createTag(
    @RequestBody request: CreateTagRequest
  ): ResponseEntity<ApiResponse<TagResponse>> {
    logger.info("Creating tag: ${request.name}")
    val tag = tagService.create(request.name)
    return ApiResponse.ok(tag.toResponse(), "태그가 생성되었습니다.").toResponseEntity(HttpStatus.CREATED)
  }

  /**
   * 태그 전체 조회
   * GET /api/tags
   */
  @GetMapping
  fun getAllTags(): ResponseEntity<ApiResponse<TagListResponse>> {
    logger.info("Fetching all tags")
    val tags = tagService.findAll()
      .map { it.toResponse() }
    return ApiResponse.ok(
      TagListResponse(
        tags = tags
      )
    ).toResponseEntity()
  }

  /**
   * 태그 단건 조회
   * GET /api/tags/{id}
   */
  @GetMapping("/{id}")
  fun getTag(@PathVariable id: Long): ResponseEntity<ApiResponse<TagResponse>> {
    logger.info("Fetching tag: $id")
    val tag = tagService.findById(id)
      ?: throw ResourceNotFoundException("태그", id)
    return ApiResponse.ok(tag.toResponse()).toResponseEntity()
  }

  /**
   * 이름으로 태그 조회
   * GET /api/tags/name/{name}
   */
  @GetMapping("/name/{name}")
  fun getTagByName(@PathVariable name: String): ResponseEntity<ApiResponse<TagResponse>> {
    logger.info("Fetching tag by name: $name")
    val tag = tagService.findByName(name)
      ?: throw ResourceNotFoundException("태그", name)
    return ApiResponse.ok(tag.toResponse()).toResponseEntity()
  }

  /**
   * 태그 검색 (키워드 포함)
   * GET /api/tags/search?keyword={keyword}
   */
  @GetMapping("/search")
  fun searchTags(@RequestParam keyword: String): ResponseEntity<ApiResponse<List<TagResponse>>> {
    logger.info("Searching tags with keyword: $keyword")
    val tags = tagService.search(keyword).map { it.toResponse() }
    return ApiResponse.ok(tags).toResponseEntity()
  }

  /**
   * 태그 수정
   * PUT /api/tags/{id}
   */
  @PutMapping("/{id}")
  fun updateTag(
    @PathVariable id: Long,
    @RequestBody request: UpdateTagRequest
  ): ResponseEntity<ApiResponse<TagResponse>> {
    logger.info("Updating tag: $id")
    val tag = tagService.update(id, request.name)
      ?: throw ResourceNotFoundException("태그", id)
    return ApiResponse.ok(tag.toResponse(), "태그가 수정되었습니다.").toResponseEntity()
  }

  /**
   * 태그 삭제
   * DELETE /api/tags/{id}
   */
  @DeleteMapping("/{id}")
  fun deleteTag(@PathVariable id: Long): ResponseEntity<ApiResponse<Unit>> {
    logger.info("Deleting tag: $id")
    if (!tagService.delete(id)) {
      throw ResourceNotFoundException("태그", id)
    }
    return ApiResponse.ok("태그가 삭제되었습니다.").toResponseEntity()
  }

  /**
   * 태그 생성 또는 조회 (없으면 생성)
   * POST /api/tags/find-or-create
   */
  @PostMapping("/find-or-create")
  fun findOrCreateTag(@RequestBody request: CreateTagRequest): ResponseEntity<ApiResponse<TagResponse>> {
    logger.info("Find or create tag: ${request.name}")
    val tag = tagService.findOrCreate(request.name)
    return ApiResponse.ok(tag.toResponse()).toResponseEntity()
  }

  /**
   * 태그 존재 여부 확인
   * GET /api/tags/exists?name={name}
   */
  @GetMapping("/exists")
  fun existsByName(@RequestParam name: String): ResponseEntity<ApiResponse<Map<String, Boolean>>> {
    logger.info("Checking if tag exists: $name")
    val exists = tagService.existsByName(name)
    return ApiResponse.ok(mapOf("exists" to exists)).toResponseEntity()
  }
}
