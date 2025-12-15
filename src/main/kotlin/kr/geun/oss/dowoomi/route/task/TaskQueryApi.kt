package kr.geun.oss.dowoomi.route.task

import kr.geun.oss.dowoomi.common.ApiResponse
import kr.geun.oss.dowoomi.common.util.LoggerUtil
import kr.geun.oss.dowoomi.domain.category.Category
import kr.geun.oss.dowoomi.domain.task.TaskFindAllParam
import kr.geun.oss.dowoomi.domain.task.TaskProjection
import kr.geun.oss.dowoomi.domain.task.TaskRetriever
import kr.geun.oss.dowoomi.domain.task.tag.TaskTagService
import kr.geun.oss.dowoomi.route.tag.toResponse
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

/**
 * Task 조회 API
 */
@RestController
@RequestMapping("/api/v1/tasks/query")
class TaskQueryApi(
    private val taskRetriever: TaskRetriever,
    private val taskTagService: TaskTagService,
    private val taskParentService: kr.geun.oss.dowoomi.domain.task.parent.TaskParentService,
    private val taskDependencyService: kr.geun.oss.dowoomi.domain.task.dependency.TaskDependencyService,
    private val taskAssigneeService: kr.geun.oss.dowoomi.domain.task.assignee.TaskAssigneeService
) {
    companion object : LoggerUtil()

    /**
     * ID로 TaskProjection 단건 조회
     * GET /api/v1/tasks/query/{id}
     */
    @GetMapping("/{id}")
    fun getTaskProjectionById(@PathVariable id: Long): ResponseEntity<ApiResponse<TaskProjectionResponse>> {
        logger.info("Fetching TaskProjection by id: $id")
        val projection = taskRetriever.findProjectionById(id)
            ?: throw IllegalArgumentException("Task를 찾을 수 없습니다: $id")

        val tags = taskTagService.findTagsByTaskId(id)
        val assignees = taskAssigneeService.findAssigneesByTaskId(id)
        val dependencies = taskDependencyService.findDependenciesByTaskId(id)
        val parents = taskParentService.findParentsByTaskId(id)

        return ApiResponse.ok(
            data = projection.toResponse(
                tags = tags.map { it.toResponse() },
                assignees = assignees,
                dependencies = dependencies,
                parents = parents
            ),
            message = "Task 조회에 성공했습니다."
        ).toResponseEntity()
    }

    /**
     * ID 목록으로 TaskProjection 조회
     * GET /api/v1/tasks/query/by-ids?ids=1,2,3
     */
    @GetMapping("/by-ids")
    fun getTaskProjectionsByIds(
        @RequestParam ids: List<Long>
    ): ResponseEntity<ApiResponse<List<TaskProjectionResponse>>> {
        logger.info("Fetching TaskProjections by ids: ${ids.size} items")
        val projections = taskRetriever.findProjectionsByIds(ids)

        val taskIds = projections.map { it.taskId }
        val tagsMap = taskTagService.findTagsByTaskIds(taskIds)
        val assigneesMap = taskAssigneeService.findAssigneesByTaskIds(taskIds)
        val dependenciesMap = taskDependencyService.findDependenciesByTaskIds(taskIds)
        val parentsMap = taskParentService.findParentsByTaskIds(taskIds)

        return ApiResponse.ok(
            data = projections.map { projection ->
                projection.toResponse(
                    tags = tagsMap[projection.taskId]?.map { it.toResponse() } ?: emptyList(),
                    assignees = assigneesMap[projection.taskId] ?: emptyList(),
                    dependencies = dependenciesMap[projection.taskId] ?: emptyList(),
                    parents = parentsMap[projection.taskId] ?: emptyList()
                )
            },
            message = "${projections.size}개의 Task를 조회했습니다."
        ).toResponseEntity()
    }

    /**
     * 조건별 TaskProjection 조회
     * GET /api/v1/tasks/query?categoryId=1&statusProgress=in_progress&keyword=버그
     */
    @GetMapping
    fun getAllTaskProjections(
        request: TaskQueryAllRequest
    ): ResponseEntity<ApiResponse<List<TaskProjectionResponse>>> {
        logger.info("Fetching TaskProjections with filters - categoryId: ${request.categoryId}, statusProgress: ${request.statusProgress}, keyword: ${request.keyword}")

        val projections = taskRetriever.findAllProjections(request.toParam())

        val taskIds = projections.map { it.taskId }
        val tagsMap = taskTagService.findTagsByTaskIds(taskIds)
        val assigneesMap = taskAssigneeService.findAssigneesByTaskIds(taskIds)
        val dependenciesMap = taskDependencyService.findDependenciesByTaskIds(taskIds)
        val parentsMap = taskParentService.findParentsByTaskIds(taskIds)

        return ApiResponse.ok(
            data = projections.map { projection ->
                projection.toResponse(
                    tags = tagsMap[projection.taskId]?.map { it.toResponse() } ?: emptyList(),
                    assignees = assigneesMap[projection.taskId] ?: emptyList(),
                    dependencies = dependenciesMap[projection.taskId] ?: emptyList(),
                    parents = parentsMap[projection.taskId] ?: emptyList()
                )
            },
            message = "${projections.size}개의 Task를 조회했습니다."
        ).toResponseEntity()
    }

    /**
     * Active TaskProjection 조회
     * GET /api/v1/tasks/query/active
     */
    @GetMapping("/active")
    fun getActiveTaskProjections(): ResponseEntity<ApiResponse<List<TaskProjectionResponse>>> {
        logger.info("Fetching all active TaskProjections")
        val projections = taskRetriever.findAllActiveProjections()

        val taskIds = projections.map { it.taskId }
        val tagsMap = taskTagService.findTagsByTaskIds(taskIds)
        val assigneesMap = taskAssigneeService.findAssigneesByTaskIds(taskIds)
        val dependenciesMap = taskDependencyService.findDependenciesByTaskIds(taskIds)
        val parentsMap = taskParentService.findParentsByTaskIds(taskIds)

        return ApiResponse.ok(
            data = projections.map { projection ->
                projection.toResponse(
                    tags = tagsMap[projection.taskId]?.map { it.toResponse() } ?: emptyList(),
                    assignees = assigneesMap[projection.taskId] ?: emptyList(),
                    dependencies = dependenciesMap[projection.taskId] ?: emptyList(),
                    parents = parentsMap[projection.taskId] ?: emptyList()
                )
            },
            message = "${projections.size}개의 Active Task를 조회했습니다."
        ).toResponseEntity()
    }
}



/**
 * Category 응답 DTO
 */
data class CategoryResponse(
    val categoryId: Long,
    val name: String,
    val description: String?,
    val createdAt: String,
    val updatedAt: String
)

/**
 * TaskProjection to Response 변환
 */
fun TaskProjection.toResponse(
    tags: List<kr.geun.oss.dowoomi.route.tag.TagResponse> = emptyList(),
    assignees: List<AssigneeResponse> = emptyList(),
    dependencies: List<SimpleTaskResponse> = emptyList(),
    parents: List<SimpleTaskResponse> = emptyList()
) = TaskProjectionResponse(
    taskId = this.taskId,
    title = this.title,
    description = this.description,
    category = this.category?.let {
        CategoryResponse(
            categoryId = it.categoryId,
            name = it.name,
            description = it.description,
            createdAt = it.createdAt.toString(),
            updatedAt = it.updatedAt.toString()
        )
    },
    statusProgress = this.statusProgress,
    statusLifecycle = this.statusLifecycle,
    startDate = this.startDate?.toString(),
    endDate = this.endDate?.toString(),
    createdAt = this.createdAt.toString(),
    updatedAt = this.updatedAt.toString(),
    tags = tags,
    assignees = assignees,
    dependencies = dependencies,
    parents = parents
)
