package kr.geun.oss.dowoomi.route

import kr.geun.oss.dowoomi.common.util.LoggerUtil
import kr.geun.oss.dowoomi.domain.member.MemberEntity
import kr.geun.oss.dowoomi.domain.member.MemberService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 멤버 API 컨트롤러
 */
@RestController
@RequestMapping("/api/members")
class MemberController(
    private val memberService: MemberService
) {
    companion object : LoggerUtil()

    /**
     * 멤버 생성
     * POST /api/members
     */
    @PostMapping
    fun createMember(@RequestBody request: CreateMemberRequest): ResponseEntity<MemberResponse> {
        logger.info("Creating member: ${request.name}")
        val member = memberService.createMember(request.name)
        return ResponseEntity.status(HttpStatus.CREATED).body(member.toResponse())
    }

    /**
     * 멤버 전체 조회
     * GET /api/members
     */
    @GetMapping
    fun getAllMembers(): ResponseEntity<List<MemberResponse>> {
        logger.info("Fetching all members")
        val members = memberService.findAll().map { it.toResponse() }
        return ResponseEntity.ok(members)
    }

    /**
     * 멤버 단건 조회
     * GET /api/members/{id}
     */
    @GetMapping("/{id}")
    fun getMember(@PathVariable id: Long): ResponseEntity<MemberResponse> {
        logger.info("Fetching member: $id")
        val member = memberService.findById(id)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(member.toResponse())
    }

    /**
     * 멤버 수정
     * PUT /api/members/{id}
     */
    @PutMapping("/{id}")
    fun updateMember(
        @PathVariable id: Long,
        @RequestBody request: UpdateMemberRequest
    ): ResponseEntity<MemberResponse> {
        logger.info("Updating member: $id")
        val member = memberService.updateMember(id, request.name)
        return ResponseEntity.ok(member.toResponse())
    }

    /**
     * 멤버 삭제
     * DELETE /api/members/{id}
     */
    @DeleteMapping("/{id}")
    fun deleteMember(@PathVariable id: Long): ResponseEntity<Void> {
        logger.info("Deleting member: $id")
        memberService.deleteMember(id)
        return ResponseEntity.noContent().build()
    }
}

/**
 * 멤버 생성 요청
 */
data class CreateMemberRequest(
    val name: String
)

/**
 * 멤버 수정 요청
 */
data class UpdateMemberRequest(
    val name: String
)

/**
 * 멤버 응답
 */
data class MemberResponse(
    val id: Long,
    val name: String,
    val createdAt: String,
    val updatedAt: String
)

/**
 * Entity to Response 변환
 */
fun MemberEntity.toResponse() = MemberResponse(
    id = this.id!!,
    name = this.name,
    createdAt = this.createdAt.toString(),
    updatedAt = this.updatedAt.toString()
)
