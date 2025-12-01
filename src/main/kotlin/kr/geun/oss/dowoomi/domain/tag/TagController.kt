package kr.geun.oss.dowoomi.domain.tag

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tags")
class TagController(
    private val tagService: TagService
) {

    /**
     * 모든 태그 조회
     */
    @GetMapping
    fun getAllTags(): ResponseEntity<List<TagResponse>> {
        val tags = tagService.findAll()
        return ResponseEntity.ok(tags.map { it.toResponse() })
    }

    /**
     * ID로 태그 조회
     */
    @GetMapping("/{id}")
    fun getTagById(@PathVariable id: Long): ResponseEntity<TagResponse> {
        val tag = tagService.findById(id)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(tag.toResponse())
    }

    /**
     * 이름으로 태그 조회
     */
    @GetMapping("/name/{name}")
    fun getTagByName(@PathVariable name: String): ResponseEntity<TagResponse> {
        val tag = tagService.findByName(name)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(tag.toResponse())
    }

    /**
     * 태그 검색 (키워드 포함)
     */
    @GetMapping("/search")
    fun searchTags(@RequestParam keyword: String): ResponseEntity<List<TagResponse>> {
        val tags = tagService.search(keyword)
        return ResponseEntity.ok(tags.map { it.toResponse() })
    }

    /**
     * 태그 생성
     */
    @PostMapping
    fun createTag(@RequestBody request: CreateTagRequest): ResponseEntity<TagResponse> {
        val tag = tagService.create(request.name)
        return ResponseEntity.ok(tag.toResponse())
    }

    /**
     * 태그 생성 또는 조회 (없으면 생성)
     */
    @PostMapping("/find-or-create")
    fun findOrCreateTag(@RequestBody request: CreateTagRequest): ResponseEntity<TagResponse> {
        val tag = tagService.findOrCreate(request.name)
        return ResponseEntity.ok(tag.toResponse())
    }

    /**
     * 태그 수정
     */
    @PutMapping("/{id}")
    fun updateTag(
        @PathVariable id: Long,
        @RequestBody request: UpdateTagRequest
    ): ResponseEntity<TagResponse> {
        val tag = tagService.update(id, request.name)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(tag.toResponse())
    }

    /**
     * 태그 삭제
     */
    @DeleteMapping("/{id}")
    fun deleteTag(@PathVariable id: Long): ResponseEntity<Void> {
        return if (tagService.delete(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * 태그 존재 여부 확인
     */
    @GetMapping("/exists")
    fun existsByName(@RequestParam name: String): ResponseEntity<Map<String, Boolean>> {
        val exists = tagService.existsByName(name)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }
}

// ========== Request DTOs ==========

data class CreateTagRequest(
    val name: String
)

data class UpdateTagRequest(
    val name: String
)

// ========== Response DTO ==========

data class TagResponse(
    val id: Long,
    val name: String,
    val createdAt: String
)

// ========== Extension Function ==========

fun TagEntity.toResponse() = TagResponse(
    id = this.id!!,
    name = this.name,
    createdAt = this.createdAt.toString()
)
