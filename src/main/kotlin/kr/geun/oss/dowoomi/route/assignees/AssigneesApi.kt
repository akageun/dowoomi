package kr.geun.oss.dowoomi.route.assignees

import kr.geun.oss.dowoomi.common.ApiResponse
import kr.geun.oss.dowoomi.common.exception.ResourceNotFoundException
import kr.geun.oss.dowoomi.common.util.LoggerUtil
import kr.geun.oss.dowoomi.domain.assignee.AssigneeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 담당자 API 컨트롤러
 */
@RestController
@RequestMapping("/api/assignees")
class AssigneesApi(
  private val assigneeService: AssigneeService
) {
  companion object : LoggerUtil()

  /**
   * 담당자 생성
   * POST /api/assignees
   */
  @PostMapping
  fun createAssignee(
    @RequestBody request: CreateAssigneeRequest
  ): ResponseEntity<ApiResponse<AssigneeResponse>> {
    logger.info("Creating assignee: ${request.name}")
    val assignee = assigneeService.createAssignee(request.name, request.memo)
    return ApiResponse.ok(assignee.toResponse(), "담당자가 생성되었습니다.").toResponseEntity(HttpStatus.CREATED)
  }

  /**
   * 담당자 전체 조회
   * GET /api/assignees
   */
  @GetMapping
  fun getAllAssignees(): ResponseEntity<ApiResponse<AssigneeListResponse>> {
    logger.info("Fetching all assignees")
    val assignees = assigneeService.findAll()
      .map { it.toResponse() }
    return ApiResponse.ok(
      AssigneeListResponse(
        assignees = assignees
      )
    ).toResponseEntity()
  }

  /**
   * 담당자 단건 조회
   * GET /api/assignees/{id}
   */
  @GetMapping("/{id}")
  fun getAssignee(@PathVariable id: Long): ResponseEntity<ApiResponse<AssigneeResponse>> {
    logger.info("Fetching assignee: $id")
    val assignee = assigneeService.findById(id)
      ?: throw ResourceNotFoundException("담당자", id)
    return ApiResponse.ok(assignee.toResponse()).toResponseEntity()
  }

  /**
   * 담당자 수정
   * PUT /api/assignees/{id}
   */
  @PutMapping("/{id}")
  fun updateAssignee(
    @PathVariable id: Long,
    @RequestBody request: UpdateAssigneeRequest
  ): ResponseEntity<ApiResponse<AssigneeResponse>> {
    logger.info("Updating assignee: $id")
    val assignee = assigneeService.updateAssignee(id, request.name, request.memo)
    return ApiResponse.ok(assignee.toResponse(), "담당자가 수정되었습니다.").toResponseEntity()
  }

  /**
   * 담당자 삭제
   * DELETE /api/assignees/{id}
   */
  @DeleteMapping("/{id}")
  fun deleteAssignee(@PathVariable id: Long): ResponseEntity<ApiResponse<Unit>> {
    logger.info("Deleting assignee: $id")
    assigneeService.deleteAssignee(id)
    return ApiResponse.ok("담당자가 삭제되었습니다.").toResponseEntity()
  }

  /**
   * 담당자 검색 (이름으로)
   * GET /api/assignees/search?name={name}
   */
  @GetMapping("/search")
  fun searchAssignees(@RequestParam name: String): ResponseEntity<ApiResponse<List<AssigneeResponse>>> {
    logger.info("Searching assignees by name: $name")
    val assignees = assigneeService.searchByName(name).map { it.toResponse() }
    return ApiResponse.ok(assignees).toResponseEntity()
  }
}
