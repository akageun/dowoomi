//package kr.geun.oss.dowoomi.route
//
//import kr.geun.oss.dowoomi.common.exception.ResourceNotFoundException
//import kr.geun.oss.dowoomi.domain.task.TaskLifecycle
//import kr.geun.oss.dowoomi.domain.task.TaskProgress
//import kr.geun.oss.dowoomi.domain.task.TaskService
//import kr.geun.oss.dowoomi.domain.task.TasksEntity
//import org.springframework.http.ResponseEntity
//import org.springframework.web.bind.annotation.*
//import java.time.LocalDate
//
//@RestController
//@RequestMapping("/api/tasks")
//class TaskController(
//    private val taskService: TaskService
//) {
//
//    // ========== 조회 API ==========
//
//    /**
//     * 모든 Active Task 조회
//     */
//    @GetMapping
//    fun getAllActiveTasks(): ResponseEntity<List<TaskResponse>> {
//        val tasks = taskService.findAllActiveTasks()
//        return ResponseEntity.ok(tasks.map { it.toResponse() })
//    }
//
//    /**
//     * ID로 Task 조회
//     */
//    @GetMapping("/{id}")
//    fun getTaskById(@PathVariable id: Long): ResponseEntity<TaskResponse> {
//        val task = taskService.findByIdNotDeleted(id)
//            ?: throw ResourceNotFoundException("태스크", id)
//        return ResponseEntity.ok(task.toResponse())
//    }
//
//    /**
//     * 카테고리별 Task 조회
//     */
//    @GetMapping("/category/{categoryId}")
//    fun getTasksByCategoryId(@PathVariable categoryId: Long): ResponseEntity<List<TaskResponse>> {
//        val tasks = taskService.findByCategoryId(categoryId)
//        return ResponseEntity.ok(tasks.map { it.toResponse() })
//    }
//
//    /**
//     * 진행 상태별 Task 조회
//     */
//    @GetMapping("/progress/{progress}")
//    fun getTasksByProgress(@PathVariable progress: String): ResponseEntity<List<TaskResponse>> {
//        val taskProgress = try {
//            TaskProgress.fromValue(progress)
//        } catch (e: IllegalArgumentException) {
//            return ResponseEntity.badRequest().build()
//        }
//        val tasks = taskService.findByProgress(taskProgress)
//        return ResponseEntity.ok(tasks.map { it.toResponse() })
//    }
//
//    /**
//     * 특정 월의 Task 조회 (YYYY-MM 형식)
//     */
//    @GetMapping("/month/{yearMonth}")
//    fun getTasksByYearMonth(@PathVariable yearMonth: String): ResponseEntity<List<TaskResponse>> {
//        val tasks = taskService.findByYearMonth(yearMonth)
//        return ResponseEntity.ok(tasks.map { it.toResponse() })
//    }
//
//    /**
//     * 마감일이 가까운 Task 조회
//     */
//    @GetMapping("/deadlines")
//    fun getUpcomingDeadlines(@RequestParam(defaultValue = "7") days: Int): ResponseEntity<List<TaskResponse>> {
//        val tasks = taskService.findUpcomingDeadlines(days)
//        return ResponseEntity.ok(tasks.map { it.toResponse() })
//    }
//
//    /**
//     * 기한 초과 Task 조회
//     */
//    @GetMapping("/overdue")
//    fun getOverdueTasks(): ResponseEntity<List<TaskResponse>> {
//        val tasks = taskService.findOverdueTasks()
//        return ResponseEntity.ok(tasks.map { it.toResponse() })
//    }
//
//    /**
//     * 오늘 집중해야 할 Task
//     */
//    @GetMapping("/focus")
//    fun getTodayFocusTasks(): ResponseEntity<List<TaskResponse>> {
//        val tasks = taskService.findTodayFocusTasks()
//        return ResponseEntity.ok(tasks.map { it.toResponse() })
//    }
//
//    /**
//     * 바로 시작 가능한 Task
//     */
//    @GetMapping("/ready")
//    fun getReadyToStartTasks(): ResponseEntity<List<TaskResponse>> {
//        val tasks = taskService.findReadyToStartTasks()
//        return ResponseEntity.ok(tasks.map { it.toResponse() })
//    }
//
//    /**
//     * 이번 주 완료한 Task
//     */
//    @GetMapping("/completed-this-week")
//    fun getCompletedThisWeek(): ResponseEntity<List<TaskResponse>> {
//        val tasks = taskService.findCompletedThisWeek()
//        return ResponseEntity.ok(tasks.map { it.toResponse() })
//    }
//
//    /**
//     * 제목으로 Task 검색 (LIKE 검색)
//     */
//    @GetMapping("/search")
//    fun searchTasks(
//        @RequestParam keyword: String,
//        @RequestParam(defaultValue = "20") limit: Int
//    ): ResponseEntity<List<TaskResponse>> {
//        if (keyword.isBlank()) {
//            return ResponseEntity.ok(emptyList())
//        }
//        val tasks = taskService.searchByTitle(keyword, limit)
//        return ResponseEntity.ok(tasks.map { it.toResponse() })
//    }
//
//    // ========== 생성/수정/삭제 API ==========
//
//    /**
//     * Task 생성
//     */
//    @PostMapping
//    fun createTask(@RequestBody request: CreateTaskRequest): ResponseEntity<TaskResponse> {
//        val task = taskService.createTask(
//            title = request.title,
//            description = request.description,
//            categoryId = request.categoryId,
//            progress = request.progress?.let { TaskProgress.fromValue(it) } ?: TaskProgress.TODO,
//            lifecycle = request.lifecycle?.let { TaskLifecycle.fromValue(it) } ?: TaskLifecycle.ACTIVE,
//            startDate = request.startDate,
//            endDate = request.endDate,
//            tags = request.tags,
//            assigneeNames = request.assignees,
//            links = request.links.map {
//                TaskService.LinkInput(url = it.url, name = it.name, description = it.description)
//            }
//        )
//        return ResponseEntity.ok(task.toResponse())
//    }
//
//    /**
//     * Task 수정
//     */
//    @PutMapping("/{id}")
//    fun updateTask(
//        @PathVariable id: Long,
//        @RequestBody request: UpdateTaskRequest
//    ): ResponseEntity<TaskResponse> {
//        val task = taskService.updateTask(
//            id = id,
//            title = request.title,
//            description = request.description,
//            categoryId = request.categoryId,
//            progress = request.progress?.let { TaskProgress.fromValue(it) },
//            lifecycle = request.lifecycle?.let { TaskLifecycle.fromValue(it) },
//            startDate = request.startDate,
//            endDate = request.endDate,
//            clearCategory = request.clearCategory ?: false
//        ) ?: throw ResourceNotFoundException("태스크", id)
//        return ResponseEntity.ok(task.toResponse())
//    }
//
//    /**
//     * 진행 상태 변경
//     */
//    @PatchMapping("/{id}/progress")
//    fun changeProgress(
//        @PathVariable id: Long,
//        @RequestBody request: ChangeProgressRequest
//    ): ResponseEntity<TaskResponse> {
//        val progress = try {
//            TaskProgress.fromValue(request.progress)
//        } catch (e: IllegalArgumentException) {
//            return ResponseEntity.badRequest().build()
//        }
//        val task = taskService.changeProgress(id, progress)
//            ?: throw ResourceNotFoundException("태스크", id)
//        return ResponseEntity.ok(task.toResponse())
//    }
//
//    /**
//     * Task 소프트 삭제
//     */
//    @DeleteMapping("/{id}")
//    fun softDeleteTask(@PathVariable id: Long): ResponseEntity<Void> {
//        if (!taskService.softDelete(id)) {
//            throw ResourceNotFoundException("태스크", id)
//        }
//        return ResponseEntity.noContent().build()
//    }
//
//    /**
//     * Task 완전 삭제 (하드 삭제)
//     */
//    @DeleteMapping("/{id}/hard")
//    fun hardDeleteTask(@PathVariable id: Long): ResponseEntity<Void> {
//        if (!taskService.hardDelete(id)) {
//            throw ResourceNotFoundException("태스크", id)
//        }
//        return ResponseEntity.noContent().build()
//    }
//
//    // ========== 태그 API ==========
//
//    @PostMapping("/{id}/tags")
//    fun addTag(@PathVariable id: Long, @RequestBody request: AddTagRequest): ResponseEntity<TaskResponse> {
//        val task = taskService.addTag(id, request.tag)
//            ?: throw ResourceNotFoundException("태스크", id)
//        return ResponseEntity.ok(task.toResponse())
//    }
//
//    @DeleteMapping("/{id}/tags/{tag}")
//    fun removeTag(@PathVariable id: Long, @PathVariable tag: String): ResponseEntity<TaskResponse> {
//        val task = taskService.removeTag(id, tag)
//            ?: throw ResourceNotFoundException("태스크", id)
//        return ResponseEntity.ok(task.toResponse())
//    }
//
//    // ========== 담당자 API ==========
//
//    @PostMapping("/{id}/assignees")
//    fun addAssignee(@PathVariable id: Long, @RequestBody request: AddAssigneeRequest): ResponseEntity<TaskResponse> {
//        val task = taskService.addAssignee(id, request.name)
//            ?: throw ResourceNotFoundException("태스크", id)
//        return ResponseEntity.ok(task.toResponse())
//    }
//
//    @DeleteMapping("/{id}/assignees/{name}")
//    fun removeAssignee(@PathVariable id: Long, @PathVariable name: String): ResponseEntity<TaskResponse> {
//        val task = taskService.removeAssignee(id, name)
//            ?: throw ResourceNotFoundException("태스크", id)
//        return ResponseEntity.ok(task.toResponse())
//    }
//
//    // ========== 링크 API ==========
//
//    @PostMapping("/{id}/links")
//    fun addLink(@PathVariable id: Long, @RequestBody request: AddLinkRequest): ResponseEntity<TaskResponse> {
//        val task = taskService.addLink(id, request.url, request.name)
//            ?: throw ResourceNotFoundException("태스크", id)
//        return ResponseEntity.ok(task.toResponse())
//    }
//
//    @DeleteMapping("/{id}/links/{linkId}")
//    fun removeLink(@PathVariable id: Long, @PathVariable linkId: Long): ResponseEntity<TaskResponse> {
//        val task = taskService.removeLink(id, linkId)
//            ?: throw ResourceNotFoundException("태스크", id)
//        return ResponseEntity.ok(task.toResponse())
//    }
//
//    // ========== 의존성 API ==========
//
//    @PostMapping("/{id}/dependencies")
//    fun addDependency(@PathVariable id: Long, @RequestBody request: AddDependencyRequest): ResponseEntity<TaskResponse> {
//        val task = taskService.addDependency(id, request.dependencyTaskId)
//            ?: throw ResourceNotFoundException("태스크", id)
//        return ResponseEntity.ok(task.toResponse())
//    }
//
//    @DeleteMapping("/{id}/dependencies/{dependencyId}")
//    fun removeDependency(@PathVariable id: Long, @PathVariable dependencyId: Long): ResponseEntity<TaskResponse> {
//        val task = taskService.removeDependency(id, dependencyId)
//            ?: throw ResourceNotFoundException("태스크", id)
//        return ResponseEntity.ok(task.toResponse())
//    }
//
//    // ========== 부모 Task API (여러 개 지원) ==========
//
//    @PostMapping("/{id}/parent")
//    fun addParent(@PathVariable id: Long, @RequestBody request: AddParentRequest): ResponseEntity<TaskResponse> {
//        val task = taskService.addParent(id, request.parentTaskId)
//            ?: throw ResourceNotFoundException("태스크", id)
//        return ResponseEntity.ok(task.toResponse())
//    }
//
//    @DeleteMapping("/{id}/parent/{parentId}")
//    fun removeParent(@PathVariable id: Long, @PathVariable parentId: Long): ResponseEntity<TaskResponse> {
//        val task = taskService.removeParent(id, parentId)
//            ?: throw ResourceNotFoundException("태스크", id)
//        return ResponseEntity.ok(task.toResponse())
//    }
//
//    @DeleteMapping("/{id}/parents")
//    fun removeAllParents(@PathVariable id: Long): ResponseEntity<TaskResponse> {
//        val task = taskService.removeAllParents(id)
//            ?: throw ResourceNotFoundException("태스크", id)
//        return ResponseEntity.ok(task.toResponse())
//    }
//
//    // ========== Helper: Entity to Response 변환 ==========
//
//    private fun TasksEntity.toResponse(): TaskResponse {
//        val taskId = this.id!!
//        return TaskResponse(
//            id = taskId,
//            title = this.title,
//            description = this.description,
//            categoryId = this.categoryId,
//            categoryName = taskService.getCategoryName(this.categoryId),
//            progress = this.statusProgress,
//            lifecycle = this.statusLifecycle,
//            startDate = this.startDate,
//            endDate = this.endDate,
//            tags = taskService.getTaskTags(taskId),
//            assignees = taskService.getTaskAssignees(taskId).map {
//                AssigneeResponse(it.id!!, it.name)
//            },
//            links = taskService.getTaskLinks(taskId).map {
//                LinkResponse(it.id!!, it.url, it.name, it.description)
//            },
//            dependencies = taskService.getTaskDependencies(taskId).map {
//                DependencyResponse(it.first, it.second)
//            },
//            parents = taskService.getTaskParents(taskId).map {
//                ParentResponse(it.first, it.second)
//            },
//            createdAt = this.createdAt.toString(),
//            updatedAt = this.updatedAt.toString()
//        )
//    }
//}
//
//// ========== Request DTOs ==========
//
//data class CreateTaskRequest(
//    val title: String,
//    val description: String? = null,
//    val categoryId: Long? = null,
//    val progress: String? = null, // todo, in_progress, done
//    val lifecycle: String? = null, // active, draft, deleted
//    val startDate: LocalDate? = null,
//    val endDate: LocalDate? = null,
//    val tags: List<String> = emptyList(),
//    val assignees: List<String> = emptyList(),
//    val links: List<CreateLinkRequest> = emptyList()
//)
//
//data class CreateLinkRequest(
//    val url: String,
//    val name: String,
//    val description: String? = null
//)
//
//data class UpdateTaskRequest(
//    val title: String? = null,
//    val description: String? = null,
//    val categoryId: Long? = null,
//    val clearCategory: Boolean? = null,
//    val progress: String? = null,
//    val lifecycle: String? = null,
//    val startDate: LocalDate? = null,
//    val endDate: LocalDate? = null
//)
//
//data class ChangeProgressRequest(
//    val progress: String // todo, in_progress, done
//)
//
//data class AddTagRequest(val tag: String)
//data class AddAssigneeRequest(val name: String)
//data class AddLinkRequest(val url: String, val name: String)
//data class AddDependencyRequest(val dependencyTaskId: Long)
//data class AddParentRequest(val parentTaskId: Long)
//
//// ========== Response DTO ==========
//
//data class TaskResponse(
//    val id: Long,
//    val title: String,
//    val description: String?,
//    val categoryId: Long?,
//    val categoryName: String?,
//    val progress: String,
//    val lifecycle: String,
//    val startDate: LocalDate?,
//    val endDate: LocalDate?,
//    val tags: List<String>,
//    val assignees: List<AssigneeResponse>,
//    val links: List<LinkResponse>,
//    val dependencies: List<DependencyResponse>,
//    val parents: List<ParentResponse>,
//    val createdAt: String,
//    val updatedAt: String
//)
//
//data class AssigneeResponse(
//    val id: Long,
//    val name: String
//)
//
//data class LinkResponse(
//    val id: Long,
//    val url: String,
//    val name: String,
//    val description: String?
//)
//
//data class DependencyResponse(
//    val taskId: Long,
//    val taskTitle: String
//)
//
//data class ParentResponse(
//    val taskId: Long,
//    val taskTitle: String
//)
