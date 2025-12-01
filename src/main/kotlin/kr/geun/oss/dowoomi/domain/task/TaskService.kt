package kr.geun.oss.dowoomi.domain.task

import kr.geun.oss.dowoomi.domain.category.CategoryRepository
import kr.geun.oss.dowoomi.domain.member.MemberRepository
import kr.geun.oss.dowoomi.domain.tag.TagEntity
import kr.geun.oss.dowoomi.domain.tag.TagRepository
import kr.geun.oss.dowoomi.domain.task.assignee.TaskAssigneeEntity
import kr.geun.oss.dowoomi.domain.task.dependency.TaskDependencyEntity
import kr.geun.oss.dowoomi.domain.task.link.TaskLinkEntity
import kr.geun.oss.dowoomi.domain.task.parent.TaskParentEntity
import kr.geun.oss.dowoomi.domain.task.tag.TaskTagEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class TaskService(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val tagRepository: TagRepository,
    private val memberRepository: MemberRepository
) {

    /**
     * 모든 Active Task 조회
     */
    fun findAllActiveTasks(): List<TasksEntity> = taskRepository.findAllActiveTasks()

    /**
     * ID로 Task 조회
     */
    fun findById(id: Long): TasksEntity? = taskRepository.findById(id).orElse(null)

    /**
     * ID로 Task 조회 (삭제되지 않은 것만)
     */
    fun findByIdNotDeleted(id: Long): TasksEntity? {
        val task = taskRepository.findById(id).orElse(null)
        return if (task != null && !task.isDeleted()) task else null
    }

    /**
     * 카테고리별 Task 조회
     */
    fun findByCategoryId(categoryId: Long): List<TasksEntity> = 
        taskRepository.findByCategoryId(categoryId)

    /**
     * 진행 상태별 Task 조회
     */
    fun findByProgress(progress: TaskProgress): List<TasksEntity> = 
        taskRepository.findByStatusProgress(progress.value)

    /**
     * 생명주기 상태별 Task 조회
     */
    fun findByLifecycle(lifecycle: TaskLifecycle): List<TasksEntity> = 
        taskRepository.findByStatusLifecycle(lifecycle.value)

    /**
     * 기간 범위로 Task 조회
     */
    fun findByDateRange(startDate: LocalDate, endDate: LocalDate): List<TasksEntity> = 
        taskRepository.findByStartDateBetween(startDate, endDate)

    /**
     * 특정 월의 Task 조회 (YYYY-MM 형식)
     */
    fun findByYearMonth(yearMonth: String): List<TasksEntity> = 
        taskRepository.findByYearMonth(yearMonth)

    /**
     * 마감일이 가까운 Task 조회
     */
    fun findUpcomingDeadlines(days: Int = 7): List<TasksEntity> = 
        taskRepository.findUpcomingDeadlines(days)

    /**
     * 기한 초과 Task 조회
     */
    fun findOverdueTasks(): List<TasksEntity> = taskRepository.findOverdueTasks()

    /**
     * 오늘 집중해야 할 Task
     */
    fun findTodayFocusTasks(): List<TasksEntity> = taskRepository.findTodayFocusTasks()

    /**
     * 바로 시작 가능한 Task
     */
    fun findReadyToStartTasks(): List<TasksEntity> = taskRepository.findReadyToStartTasks()

    /**
     * 이번 주 완료한 Task
     */
    fun findCompletedThisWeek(): List<TasksEntity> = taskRepository.findCompletedThisWeek()

    /**
     * Task 생성
     */
    @Transactional
    fun createTask(
        title: String,
        description: String? = null,
        categoryId: Long? = null,
        progress: TaskProgress = TaskProgress.TODO,
        lifecycle: TaskLifecycle = TaskLifecycle.ACTIVE,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        tags: List<String> = emptyList(),
        assigneeNames: List<String> = emptyList(),
        links: List<LinkInput> = emptyList()
    ): TasksEntity {
        val category = categoryId?.let { 
            categoryRepository.findById(it).orElse(null) 
        }

        val task = TasksEntity(
            title = title,
            description = description,
            category = category,
            statusProgress = progress.value,
            statusLifecycle = lifecycle.value,
            startDate = startDate,
            endDate = endDate
        )

        // 태그 처리 - 없으면 생성
        tags.forEach { tagName ->
            val tag = tagRepository.findByName(tagName)
                ?: tagRepository.save(TagEntity(name = tagName))
            task.tags.add(TaskTagEntity(task = task, tag = tag))
        }

        // 담당자 처리 - 없으면 생성
        assigneeNames.forEach { name ->
            val member = memberRepository.findByName(name)
                ?: memberRepository.save(kr.geun.oss.dowoomi.domain.member.MemberEntity(name = name))
            task.assignees.add(TaskAssigneeEntity(task = task, member = member))
        }

        // 링크 처리
        links.forEach { linkInput ->
            task.links.add(TaskLinkEntity(
                task = task, 
                url = linkInput.url, 
                name = linkInput.name,
                description = linkInput.description
            ))
        }

        return taskRepository.save(task)
    }

    /**
     * 링크 입력 데이터 클래스
     */
    data class LinkInput(
        val url: String,
        val name: String,
        val description: String? = null
    )

    /**
     * Task 수정
     */
    @Transactional
    fun updateTask(
        id: Long,
        title: String? = null,
        description: String? = null,
        categoryId: Long? = null,
        progress: TaskProgress? = null,
        lifecycle: TaskLifecycle? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        clearCategory: Boolean = false
    ): TasksEntity? {
        val task = findByIdNotDeleted(id) ?: return null

        title?.let { task.title = it }
        description?.let { task.description = it }
        progress?.let { task.setProgress(it) }
        lifecycle?.let { task.setLifecycle(it) }
        startDate?.let { task.startDate = it }
        endDate?.let { task.endDate = it }

        if (clearCategory) {
            task.category = null
        } else {
            categoryId?.let { 
                task.category = categoryRepository.findById(it).orElse(null)
            }
        }

        return taskRepository.save(task)
    }

    /**
     * 진행 상태 변경
     */
    @Transactional
    fun changeProgress(id: Long, progress: TaskProgress): TasksEntity? {
        val task = findByIdNotDeleted(id) ?: return null
        task.setProgress(progress)
        return taskRepository.save(task)
    }

    /**
     * Task 소프트 삭제
     */
    @Transactional
    fun softDelete(id: Long): Boolean {
        val task = taskRepository.findById(id).orElse(null) ?: return false
        task.softDelete()
        taskRepository.save(task)
        return true
    }

    /**
     * Task 하드 삭제 (실제 DB에서 삭제)
     */
    @Transactional
    fun hardDelete(id: Long): Boolean {
        if (!taskRepository.existsById(id)) return false
        taskRepository.deleteById(id)
        return true
    }

    /**
     * 태그 추가
     */
    @Transactional
    fun addTag(taskId: Long, tagName: String): TasksEntity? {
        val task = findByIdNotDeleted(taskId) ?: return null
        
        // 이미 있는 태그인지 확인
        val existingTag = task.tags.find { it.tag.name == tagName }
        if (existingTag != null) return task

        val tag = tagRepository.findByName(tagName)
            ?: tagRepository.save(TagEntity(name = tagName))
        task.tags.add(TaskTagEntity(task = task, tag = tag))
        return taskRepository.save(task)
    }

    /**
     * 태그 제거
     */
    @Transactional
    fun removeTag(taskId: Long, tagName: String): TasksEntity? {
        val task = findByIdNotDeleted(taskId) ?: return null
        task.tags.removeIf { it.tag.name == tagName }
        return taskRepository.save(task)
    }

    /**
     * 담당자 추가
     */
    @Transactional
    fun addAssignee(taskId: Long, memberName: String): TasksEntity? {
        val task = findByIdNotDeleted(taskId) ?: return null

        // 이미 있는 담당자인지 확인
        val existing = task.assignees.find { it.member.name == memberName }
        if (existing != null) return task

        val member = memberRepository.findByName(memberName)
            ?: memberRepository.save(kr.geun.oss.dowoomi.domain.member.MemberEntity(name = memberName))
        task.assignees.add(TaskAssigneeEntity(task = task, member = member))
        return taskRepository.save(task)
    }

    /**
     * 담당자 제거
     */
    @Transactional
    fun removeAssignee(taskId: Long, memberName: String): TasksEntity? {
        val task = findByIdNotDeleted(taskId) ?: return null
        task.assignees.removeIf { it.member.name == memberName }
        return taskRepository.save(task)
    }

    /**
     * 링크 추가
     */
    @Transactional
    fun addLink(taskId: Long, url: String, name: String): TasksEntity? {
        val task = findByIdNotDeleted(taskId) ?: return null
        task.links.add(TaskLinkEntity(task = task, url = url, name = name))
        return taskRepository.save(task)
    }

    /**
     * 링크 제거
     */
    @Transactional
    fun removeLink(taskId: Long, linkId: Long): TasksEntity? {
        val task = findByIdNotDeleted(taskId) ?: return null
        task.links.removeIf { it.id == linkId }
        return taskRepository.save(task)
    }

    /**
     * 의존성(선행 작업) 추가
     */
    @Transactional
    fun addDependency(taskId: Long, dependencyTaskId: Long): TasksEntity? {
        val task = findByIdNotDeleted(taskId) ?: return null
        val dependencyTask = findByIdNotDeleted(dependencyTaskId) ?: return null

        // 순환 참조 방지
        if (taskId == dependencyTaskId) return null

        // 이미 존재하는 의존성인지 확인
        val existing = task.dependencies.find { it.dependencyTask.id == dependencyTaskId }
        if (existing != null) return task

        task.dependencies.add(TaskDependencyEntity(task = task, dependencyTask = dependencyTask))
        return taskRepository.save(task)
    }

    /**
     * 의존성 제거
     */
    @Transactional
    fun removeDependency(taskId: Long, dependencyTaskId: Long): TasksEntity? {
        val task = findByIdNotDeleted(taskId) ?: return null
        task.dependencies.removeIf { it.dependencyTask.id == dependencyTaskId }
        return taskRepository.save(task)
    }

    /**
     * 부모 Task 설정
     */
    @Transactional
    fun setParent(taskId: Long, parentTaskId: Long): TasksEntity? {
        val task = findByIdNotDeleted(taskId) ?: return null
        val parentTask = findByIdNotDeleted(parentTaskId) ?: return null

        // 순환 참조 방지
        if (taskId == parentTaskId) return null

        // 기존 부모 관계 제거
        task.parents.clear()

        task.parents.add(TaskParentEntity(task = task, parentTask = parentTask))
        return taskRepository.save(task)
    }

    /**
     * 부모 Task 제거
     */
    @Transactional
    fun removeParent(taskId: Long): TasksEntity? {
        val task = findByIdNotDeleted(taskId) ?: return null
        task.parents.clear()
        return taskRepository.save(task)
    }
}
