package kr.geun.oss.dowoomi.route.task

import jakarta.validation.Valid
import kr.geun.oss.dowoomi.common.ApiResponse
import kr.geun.oss.dowoomi.common.util.LoggerUtil
import kr.geun.oss.dowoomi.domain.category.CategoryService
import kr.geun.oss.dowoomi.domain.tag.TagService
import kr.geun.oss.dowoomi.domain.task.TaskService
import kr.geun.oss.dowoomi.domain.task.TasksEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
@Validated
class TaskApi(
    private val taskService: TaskService,
    private val categoryService: CategoryService,
    private val tagService: TagService,
) {
    companion object : LoggerUtil()

    /**
     * Task 생성
     * POST /api/v1/task
     */
    @PostMapping("/task")
    fun createTask(
        @Valid @RequestBody request: CreateTaskRequest
    ): ResponseEntity<ApiResponse<TaskResponse>> {
        logger.info("Creating task: ${request.title}")

        // Progress와 Lifecycle 변환
        val progress = request.convertProgress()
        val lifecycle = request.convertLifecycle()

        //category 검증
        request.categoryId?.let {
            categoryService.findById(it) ?: throw IllegalArgumentException("유효하지 않은 categoryId 입니다: $it")
        }

        //tags 검증
        request.tags.let {
            if (it.size != it.toSet().size) {
                throw IllegalArgumentException("태그는 중복될 수 없습니다.")
            }

            val existsTags = tagService.findByIdsAsMap(request.tags)
            request.tags.forEach { tagId ->
                if (existsTags[tagId] == null) {
                    throw IllegalArgumentException("유효하지 않은 tagId 입니다: $tagId")
                }
            }
        }

        request.assignees.let{
            if (it.size != it.toSet().size) {
                throw IllegalArgumentException("담당자는 중복될 수 없습니다.")
            }


        }

        // Task 생성
        val task = taskService.createTask(
            title = request.title,
            description = request.description,
            categoryId = request.categoryId,
            progress = progress,
            lifecycle = lifecycle,
            startDate = request.startDate,
            endDate = request.endDate,
            tags = request.tags,
            assignees = request.assignees,
            links = request.links.map {
                TaskService.LinkInput(
                    url = it.url,
                    name = it.name,
                    description = it.description
                )
            }
        )

        return ApiResponse.ok(
            data = task.toResponse(taskService),
            message = "Task가 생성되었습니다."
        ).toResponseEntity(HttpStatus.CREATED)
    }
}

/**
 * Task Response DTO
 */
data class TaskResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val categoryId: Long?,
    val categoryName: String?,
    val progress: String,
    val lifecycle: String,
    val startDate: String?,
    val endDate: String?,
    val tags: List<String>,
    val assignees: List<String>,
    val links: List<TaskLinkResponse>,
    val dependencies: List<TaskRelationResponse>,
    val parents: List<TaskRelationResponse>,
    val createdAt: String,
    val updatedAt: String
)

data class TaskLinkResponse(
    val id: Long,
    val url: String,
    val name: String,
    val description: String?
)

data class TaskRelationResponse(
    val taskId: Long,
    val taskTitle: String
)

/**
 * Entity to Response 변환
 */
fun TasksEntity.toResponse(taskService: TaskService): TaskResponse {
    val taskId = this.id!!
    return TaskResponse(
        id = taskId,
        title = this.title,
        description = this.description,
        categoryId = this.categoryId,
        categoryName = taskService.getCategoryName(this.categoryId),
        progress = this.statusProgress,
        lifecycle = this.statusLifecycle,
        startDate = this.startDate?.toString(),
        endDate = this.endDate?.toString(),
        tags = taskService.getTaskTags(taskId),
        assignees = emptyList(), // TODO: assignee 구현 후 수정
        links = taskService.getTaskLinks(taskId).map {
            TaskLinkResponse(it.id!!, it.url, it.name, it.description)
        },
        dependencies = taskService.getTaskDependencies(taskId).map {
            TaskRelationResponse(it.first, it.second)
        },
        parents = taskService.getTaskParents(taskId).map {
            TaskRelationResponse(it.first, it.second)
        },
        createdAt = this.createdAt.toString(),
        updatedAt = this.updatedAt.toString()
    )
}
