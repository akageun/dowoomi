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
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/tasks")
@Validated
class TaskApi(
    private val taskService: TaskService,
    private val categoryService: CategoryService,
    private val tagService: TagService,
) {
    companion object : LoggerUtil()

    // ========== 조회 API ==========

    /**
     * 모든 Active Task 조회
     * GET /api/v1/tasks
     */
    @GetMapping
    fun getAllTasks(): ResponseEntity<ApiResponse<List<TaskResponse>>> {
        logger.info("Fetching all active tasks")
        val tasks = taskService.findAllActiveTasks()
        return ApiResponse.ok(
            data = tasks.map { it.toResponse(taskService) },
            message = "Task 목록을 조회했습니다."
        ).toResponseEntity()
    }

    /**
     * ID로 Task 조회
     * GET /api/v1/tasks/{id}
     */
    @GetMapping("/{id}")
    fun getTaskById(@PathVariable id: Long): ResponseEntity<ApiResponse<TaskResponse>> {
        logger.info("Fetching task: $id")
        val task = taskService.findByIdNotDeleted(id)
            ?: throw IllegalArgumentException("Task를 찾을 수 없습니다: $id")
        
        return ApiResponse.ok(
            data = task.toResponse(taskService),
            message = "Task를 조회했습니다."
        ).toResponseEntity()
    }

    /**
     * 카테고리별 Task 조회
     * GET /api/v1/tasks/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    fun getTasksByCategoryId(@PathVariable categoryId: Long): ResponseEntity<ApiResponse<List<TaskResponse>>> {
        logger.info("Fetching tasks by category: $categoryId")
        val tasks = taskService.findByCategoryId(categoryId)
        return ApiResponse.ok(
            data = tasks.map { it.toResponse(taskService) },
            message = "카테고리별 Task 목록을 조회했습니다."
        ).toResponseEntity()
    }

    /**
     * 진행 상태별 Task 조회
     * GET /api/v1/tasks/progress/{progress}
     */
    @GetMapping("/progress/{progress}")
    fun getTasksByProgress(@PathVariable progress: String): ResponseEntity<ApiResponse<List<TaskResponse>>> {
        logger.info("Fetching tasks by progress: $progress")
        val taskProgress = kr.geun.oss.dowoomi.domain.task.TaskProgress.fromValue(progress)
        val tasks = taskService.findByProgress(taskProgress)
        return ApiResponse.ok(
            data = tasks.map { it.toResponse(taskService) },
            message = "진행 상태별 Task 목록을 조회했습니다."
        ).toResponseEntity()
    }

    /**
     * 특정 월의 Task 조회
     * GET /api/v1/tasks/month/{yearMonth}
     */
    @GetMapping("/month/{yearMonth}")
    fun getTasksByMonth(@PathVariable yearMonth: String): ResponseEntity<ApiResponse<List<TaskResponse>>> {
        logger.info("Fetching tasks by month: $yearMonth")
        val tasks = taskService.findByYearMonth(yearMonth)
        return ApiResponse.ok(
            data = tasks.map { it.toResponse(taskService) },
            message = "월별 Task 목록을 조회했습니다."
        ).toResponseEntity()
    }

    /**
     * 마감일이 가까운 Task 조회
     * GET /api/v1/tasks/deadlines?days=7
     */
    @GetMapping("/deadlines")
    fun getUpcomingDeadlines(
        @RequestParam(defaultValue = "7") days: Int
    ): ResponseEntity<ApiResponse<List<TaskResponse>>> {
        logger.info("Fetching upcoming deadlines: $days days")
        val tasks = taskService.findUpcomingDeadlines(days)
        return ApiResponse.ok(
            data = tasks.map { it.toResponse(taskService) },
            message = "마감일이 가까운 Task 목록을 조회했습니다."
        ).toResponseEntity()
    }

    /**
     * 기한 초과 Task 조회
     * GET /api/v1/tasks/overdue
     */
    @GetMapping("/overdue")
    fun getOverdueTasks(): ResponseEntity<ApiResponse<List<TaskResponse>>> {
        logger.info("Fetching overdue tasks")
        val tasks = taskService.findOverdueTasks()
        return ApiResponse.ok(
            data = tasks.map { it.toResponse(taskService) },
            message = "기한 초과 Task 목록을 조회했습니다."
        ).toResponseEntity()
    }

    /**
     * 오늘 집중해야 할 Task
     * GET /api/v1/tasks/focus
     */
    @GetMapping("/focus")
    fun getFocusTasks(): ResponseEntity<ApiResponse<List<TaskResponse>>> {
        logger.info("Fetching focus tasks")
        val tasks = taskService.findTodayFocusTasks()
        return ApiResponse.ok(
            data = tasks.map { it.toResponse(taskService) },
            message = "오늘 집중해야 할 Task 목록을 조회했습니다."
        ).toResponseEntity()
    }

    /**
     * 바로 시작 가능한 Task
     * GET /api/v1/tasks/ready
     */
    @GetMapping("/ready")
    fun getReadyTasks(): ResponseEntity<ApiResponse<List<TaskResponse>>> {
        logger.info("Fetching ready to start tasks")
        val tasks = taskService.findReadyToStartTasks()
        return ApiResponse.ok(
            data = tasks.map { it.toResponse(taskService) },
            message = "바로 시작 가능한 Task 목록을 조회했습니다."
        ).toResponseEntity()
    }

    /**
     * 이번 주 완료한 Task
     * GET /api/v1/tasks/completed-this-week
     */
    @GetMapping("/completed-this-week")
    fun getCompletedThisWeek(): ResponseEntity<ApiResponse<List<TaskResponse>>> {
        logger.info("Fetching completed tasks this week")
        val tasks = taskService.findCompletedThisWeek()
        return ApiResponse.ok(
            data = tasks.map { it.toResponse(taskService) },
            message = "이번 주 완료한 Task 목록을 조회했습니다."
        ).toResponseEntity()
    }

    /**
     * 제목으로 Task 검색
     * GET /api/v1/tasks/search?keyword=검색어&limit=20
     */
    @GetMapping("/search")
    fun searchTasks(
        @RequestParam keyword: String,
        @RequestParam(defaultValue = "20") limit: Int
    ): ResponseEntity<ApiResponse<List<TaskResponse>>> {
        logger.info("Searching tasks: keyword=$keyword, limit=$limit")
        val tasks = taskService.searchByTitle(keyword, limit)
        return ApiResponse.ok(
            data = tasks.map { it.toResponse(taskService) },
            message = "Task 검색 결과입니다."
        ).toResponseEntity()
    }

    // ========== 생성/수정/삭제 API ==========

    /**
     * Task 생성
     * POST /api/v1/tasks
     */
    @PostMapping
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

    /**
     * Task 수정
     * PUT /api/v1/tasks/{id}
     */
    @PutMapping("/{id}")
    fun updateTask(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateTaskRequest
    ): ResponseEntity<ApiResponse<TaskResponse>> {
        logger.info("Updating task: $id")

        val progress = request.progress?.let { kr.geun.oss.dowoomi.domain.task.TaskProgress.fromValue(it) }
        val lifecycle = request.lifecycle?.let { kr.geun.oss.dowoomi.domain.task.TaskLifecycle.fromValue(it) }

        val task = taskService.updateTask(
            id = id,
            title = request.title,
            description = request.description,
            categoryId = request.categoryId,
            progress = progress,
            lifecycle = lifecycle,
            startDate = request.startDate,
            endDate = request.endDate,
            clearCategory = request.clearCategory ?: false
        ) ?: throw IllegalArgumentException("Task를 찾을 수 없습니다: $id")

        return ApiResponse.ok(
            data = task.toResponse(taskService),
            message = "Task가 수정되었습니다."
        ).toResponseEntity()
    }

    /**
     * 진행 상태 변경
     * PATCH /api/v1/tasks/{id}/progress
     */
    @PatchMapping("/{id}/progress")
    fun changeProgress(
        @PathVariable id: Long,
        @Valid @RequestBody request: ChangeProgressRequest
    ): ResponseEntity<ApiResponse<TaskResponse>> {
        logger.info("Changing progress of task $id to ${request.progress}")

        val progress = kr.geun.oss.dowoomi.domain.task.TaskProgress.fromValue(request.progress)
        val task = taskService.changeProgress(id, progress)
            ?: throw IllegalArgumentException("Task를 찾을 수 없습니다: $id")

        return ApiResponse.ok(
            data = task.toResponse(taskService),
            message = "Task 진행 상태가 변경되었습니다."
        ).toResponseEntity()
    }

    /**
     * Task 소프트 삭제
     * DELETE /api/v1/tasks/{id}
     */
    @DeleteMapping("/{id}")
    fun deleteTask(@PathVariable id: Long): ResponseEntity<ApiResponse<Unit>> {
        logger.info("Soft deleting task: $id")
        
        if (!taskService.softDelete(id)) {
            throw IllegalArgumentException("Task를 찾을 수 없습니다: $id")
        }

        return ApiResponse.ok(
            message = "Task가 삭제되었습니다."
        ).toResponseEntity()
    }

    /**
     * Task 하드 삭제
     * DELETE /api/v1/tasks/{id}/hard
     */
    @DeleteMapping("/{id}/hard")
    fun hardDeleteTask(@PathVariable id: Long): ResponseEntity<ApiResponse<Unit>> {
        logger.info("Hard deleting task: $id")
        
        if (!taskService.hardDelete(id)) {
            throw IllegalArgumentException("Task를 찾을 수 없습니다: $id")
        }

        return ApiResponse.ok(
            message = "Task가 영구 삭제되었습니다."
        ).toResponseEntity()
    }

    // ========== 태그 관리 API ==========

    /**
     * 태그 추가
     * POST /api/v1/tasks/{id}/tags
     */
    @PostMapping("/{id}/tags")
    fun addTag(
        @PathVariable id: Long,
        @Valid @RequestBody request: AddTagRequest
    ): ResponseEntity<ApiResponse<TaskResponse>> {
        logger.info("Adding tag ${request.tagName} to task $id")

        val task = taskService.addTag(id, request.tagName)
            ?: throw IllegalArgumentException("Task를 찾을 수 없습니다: $id")

        return ApiResponse.ok(
            data = task.toResponse(taskService),
            message = "태그가 추가되었습니다."
        ).toResponseEntity()
    }

    /**
     * 태그 제거
     * DELETE /api/v1/tasks/{id}/tags/{tagName}
     */
    @DeleteMapping("/{id}/tags/{tagName}")
    fun removeTag(
        @PathVariable id: Long,
        @PathVariable tagName: String
    ): ResponseEntity<ApiResponse<TaskResponse>> {
        logger.info("Removing tag $tagName from task $id")

        val task = taskService.removeTag(id, tagName)
            ?: throw IllegalArgumentException("Task를 찾을 수 없습니다: $id")

        return ApiResponse.ok(
            data = task.toResponse(taskService),
            message = "태그가 제거되었습니다."
        ).toResponseEntity()
    }

    // ========== 담당자 관리 API ==========

    /**
     * 담당자 추가
     * POST /api/v1/tasks/{id}/assignees
     */
    @PostMapping("/{id}/assignees")
    fun addAssignee(
        @PathVariable id: Long,
        @Valid @RequestBody request: AddAssigneeRequest
    ): ResponseEntity<ApiResponse<TaskResponse>> {
        logger.info("Adding assignee ${request.assigneeName} to task $id")

        val task = taskService.addAssignee(id, request.assigneeName)
            ?: throw IllegalArgumentException("Task를 찾을 수 없습니다: $id")

        return ApiResponse.ok(
            data = task.toResponse(taskService),
            message = "담당자가 추가되었습니다."
        ).toResponseEntity()
    }

    /**
     * 담당자 제거
     * DELETE /api/v1/tasks/{id}/assignees/{assigneeName}
     */
    @DeleteMapping("/{id}/assignees/{assigneeName}")
    fun removeAssignee(
        @PathVariable id: Long,
        @PathVariable assigneeName: String
    ): ResponseEntity<ApiResponse<TaskResponse>> {
        logger.info("Removing assignee $assigneeName from task $id")

        val task = taskService.removeAssignee(id, assigneeName)
            ?: throw IllegalArgumentException("Task를 찾을 수 없습니다: $id")

        return ApiResponse.ok(
            data = task.toResponse(taskService),
            message = "담당자가 제거되었습니다."
        ).toResponseEntity()
    }

    // ========== 링크 관리 API ==========

    /**
     * 링크 추가
     * POST /api/v1/tasks/{id}/links
     */
    @PostMapping("/{id}/links")
    fun addLink(
        @PathVariable id: Long,
        @Valid @RequestBody request: AddLinkRequest
    ): ResponseEntity<ApiResponse<TaskResponse>> {
        logger.info("Adding link to task $id")

        val task = taskService.addLink(id, request.url, request.name)
            ?: throw IllegalArgumentException("Task를 찾을 수 없습니다: $id")

        return ApiResponse.ok(
            data = task.toResponse(taskService),
            message = "링크가 추가되었습니다."
        ).toResponseEntity()
    }

    /**
     * 링크 제거
     * DELETE /api/v1/tasks/{id}/links/{linkId}
     */
    @DeleteMapping("/{id}/links/{linkId}")
    fun removeLink(
        @PathVariable id: Long,
        @PathVariable linkId: Long
    ): ResponseEntity<ApiResponse<TaskResponse>> {
        logger.info("Removing link $linkId from task $id")

        val task = taskService.removeLink(id, linkId)
            ?: throw IllegalArgumentException("Task를 찾을 수 없습니다: $id")

        return ApiResponse.ok(
            data = task.toResponse(taskService),
            message = "링크가 제거되었습니다."
        ).toResponseEntity()
    }

    // ========== 의존성 관리 API ==========

    /**
     * 의존성 추가
     * POST /api/v1/tasks/{id}/dependencies
     */
    @PostMapping("/{id}/dependencies")
    fun addDependency(
        @PathVariable id: Long,
        @Valid @RequestBody request: AddDependencyRequest
    ): ResponseEntity<ApiResponse<TaskResponse>> {
        logger.info("Adding dependency ${request.dependencyTaskId} to task $id")

        val task = taskService.addDependency(id, request.dependencyTaskId)
            ?: throw IllegalArgumentException("Task를 찾을 수 없습니다: $id")

        return ApiResponse.ok(
            data = task.toResponse(taskService),
            message = "의존성이 추가되었습니다."
        ).toResponseEntity()
    }

    /**
     * 의존성 제거
     * DELETE /api/v1/tasks/{id}/dependencies/{dependencyId}
     */
    @DeleteMapping("/{id}/dependencies/{dependencyId}")
    fun removeDependency(
        @PathVariable id: Long,
        @PathVariable dependencyId: Long
    ): ResponseEntity<ApiResponse<TaskResponse>> {
        logger.info("Removing dependency $dependencyId from task $id")

        val task = taskService.removeDependency(id, dependencyId)
            ?: throw IllegalArgumentException("Task를 찾을 수 없습니다: $id")

        return ApiResponse.ok(
            data = task.toResponse(taskService),
            message = "의존성이 제거되었습니다."
        ).toResponseEntity()
    }

    // ========== 부모 Task 관리 API ==========

    /**
     * 부모 Task 추가
     * POST /api/v1/tasks/{id}/parent
     */
    @PostMapping("/{id}/parent")
    fun addParent(
        @PathVariable id: Long,
        @Valid @RequestBody request: AddParentRequest
    ): ResponseEntity<ApiResponse<TaskResponse>> {
        logger.info("Adding parent ${request.parentTaskId} to task $id")

        val task = taskService.addParent(id, request.parentTaskId)
            ?: throw IllegalArgumentException("Task를 찾을 수 없습니다: $id")

        return ApiResponse.ok(
            data = task.toResponse(taskService),
            message = "부모 Task가 추가되었습니다."
        ).toResponseEntity()
    }

    /**
     * 특정 부모 Task 제거
     * DELETE /api/v1/tasks/{id}/parent/{parentId}
     */
    @DeleteMapping("/{id}/parent/{parentId}")
    fun removeParent(
        @PathVariable id: Long,
        @PathVariable parentId: Long
    ): ResponseEntity<ApiResponse<TaskResponse>> {
        logger.info("Removing parent $parentId from task $id")

        val task = taskService.removeParent(id, parentId)
            ?: throw IllegalArgumentException("Task를 찾을 수 없습니다: $id")

        return ApiResponse.ok(
            data = task.toResponse(taskService),
            message = "부모 Task가 제거되었습니다."
        ).toResponseEntity()
    }

    /**
     * 모든 부모 Task 제거
     * DELETE /api/v1/tasks/{id}/parents
     */
    @DeleteMapping("/{id}/parents")
    fun removeAllParents(@PathVariable id: Long): ResponseEntity<ApiResponse<TaskResponse>> {
        logger.info("Removing all parents from task $id")

        val task = taskService.removeAllParents(id)
            ?: throw IllegalArgumentException("Task를 찾을 수 없습니다: $id")

        return ApiResponse.ok(
            data = task.toResponse(taskService),
            message = "모든 부모 Task가 제거되었습니다."
        ).toResponseEntity()
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
