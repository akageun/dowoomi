package kr.geun.oss.dowoomi.route.task.tag

import kr.geun.oss.dowoomi.common.ApiResponse
import kr.geun.oss.dowoomi.common.util.LoggerUtil
import kr.geun.oss.dowoomi.domain.task.tag.TaskTagService
import kr.geun.oss.dowoomi.route.tag.TagResponse
import kr.geun.oss.dowoomi.route.tag.toResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Task-Tag 조회 API
 */
@RestController
@RequestMapping("/api/v1/tasks/tags")
class TaskTagQueryApi(
    private val taskTagService: TaskTagService
) {
    companion object : LoggerUtil()

    /**
     * taskIds로 각 Task의 Tag 정보 조회
     * GET /api/v1/tasks/tags/by-task-ids?taskIds=1,2,3
     */
    @GetMapping("/by-task-ids")
    fun getTagsByTaskIds(
        @RequestParam taskIds: List<Long>
    ): ResponseEntity<ApiResponse<TaskTagsResponse>> {
        logger.info("Fetching tags for taskIds: $taskIds")

        val tagsGroupByTaskId = taskTagService.findTagsByTaskIds(taskIds)

        val taskTagsList = taskIds.map { taskId ->
            TaskTagsData(
                taskId = taskId,
                tags = tagsGroupByTaskId[taskId]?.map { it.toResponse() } ?: emptyList()
            )
        }

        return ApiResponse.ok(
            data = TaskTagsResponse(tasks = taskTagsList),
            message = "${taskIds.size}개 Task의 Tag를 조회했습니다."
        ).toResponseEntity()
    }

    /**
     * 단일 taskId로 Tag 정보 조회
     * GET /api/v1/tasks/tags/{taskId}
     */
    @GetMapping("/{taskId}")
    fun getTagsByTaskId(
        @PathVariable taskId: Long
    ): ResponseEntity<ApiResponse<List<TagResponse>>> {
        logger.info("Fetching tags for taskId: $taskId")

        val tags = taskTagService.findTagsByTaskId(taskId)

        return ApiResponse.ok(
            data = tags.map { it.toResponse() },
            message = "${tags.size}개의 Tag를 조회했습니다."
        ).toResponseEntity()
    }
}

/**
 * Task별 Tag 목록 응답
 */
data class TaskTagsResponse(
    val tasks: List<TaskTagsData>
)

/**
 * Task와 해당 Task의 Tag 목록
 */
data class TaskTagsData(
    val taskId: Long,
    val tags: List<TagResponse>
)
