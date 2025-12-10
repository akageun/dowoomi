package kr.geun.oss.dowoomi.domain.task

import kr.geun.oss.dowoomi.domain.category.CategoryMapper
import kr.geun.oss.dowoomi.domain.tag.TagEntity
import kr.geun.oss.dowoomi.domain.tag.TagRepository
import kr.geun.oss.dowoomi.domain.task.assignee.TaskAssigneeRepository
import kr.geun.oss.dowoomi.domain.task.dependency.TaskDependencyEntity
import kr.geun.oss.dowoomi.domain.task.dependency.TaskDependencyRepository
import kr.geun.oss.dowoomi.domain.task.link.TaskLinkEntity
import kr.geun.oss.dowoomi.domain.task.link.TaskLinkRepository
import kr.geun.oss.dowoomi.domain.task.parent.TaskParentEntity
import kr.geun.oss.dowoomi.domain.task.parent.TaskParentRepository
import kr.geun.oss.dowoomi.domain.task.tag.TaskTagEntity
import kr.geun.oss.dowoomi.domain.task.tag.TaskTagRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class TaskService(
  private val taskRepository: TaskRepository,
  private val categoryMapper: CategoryMapper,
  private val tagRepository: TagRepository,
  private val taskLinkRepository: TaskLinkRepository,
  private val taskTagRepository: TaskTagRepository,
  private val taskAssigneeRepository: TaskAssigneeRepository,
  private val taskDependencyRepository: TaskDependencyRepository,
  private val taskParentRepository: TaskParentRepository
) {

  /**
   * 모든 Active Task 조회
   */
  fun findAllActiveTasks(): List<TasksEntity> = taskRepository.findAllActiveTasks()

  /**
   * ID로 Task 조회
   */
  fun findById(id: Long): TasksEntity? = taskRepository.findById(id)

  /**
   * ID로 Task 조회 (삭제되지 않은 것만)
   */
  fun findByIdNotDeleted(id: Long): TasksEntity? {
    val task = taskRepository.findById(id)
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
   * 제목으로 Task 검색 (LIKE 검색)
   */
  fun searchByTitle(keyword: String, limit: Int = 20): List<TasksEntity> =
    taskRepository.searchByTitle(keyword, limit)

  // ========== 관계 데이터 조회 ==========

  /**
   * Task의 카테고리명 조회
   */
  fun getCategoryName(categoryId: Long?): String? {
    return categoryId?.let { categoryMapper.findById(it)?.name }
  }

  /**
   * Task의 태그 목록 조회
   */
  fun getTaskTags(taskId: Long): List<String> {
    val tagIds = taskTagRepository.findTagIdsByTaskId(taskId)
    if (tagIds.isEmpty()) return emptyList()
    return tagRepository.findAllByIds(tagIds).map { it.name }
  }

  /**
   * Task의 담당자 목록 조회
   */
//  fun getTaskAssignees(taskId: Long): List<MemberEntity> {
//    val memberIds = taskAssigneeRepository.findMemberIdsByTaskId(taskId)
//    if (memberIds.isEmpty()) return emptyList()
//    return memberRepository.findAllByIds(memberIds)
//  }

  /**
   * Task의 링크 목록 조회
   */
  fun getTaskLinks(taskId: Long): List<TaskLinkEntity> {
    return taskLinkRepository.findByTaskId(taskId)
  }

  /**
   * Task의 의존성 목록 조회 (ID와 제목 쌍)
   */
  fun getTaskDependencies(taskId: Long): List<Pair<Long, String>> {
    val dependencyTaskIds = taskDependencyRepository.findDependencyTaskIdsByTaskId(taskId)
    if (dependencyTaskIds.isEmpty()) return emptyList()
    return taskRepository.findAllByIds(dependencyTaskIds).map { it.id!! to it.title }
  }

  /**
   * Task의 부모 Tasks 조회 (여러 개 - ID와 제목 쌍 리스트)
   */
  fun getTaskParents(taskId: Long): List<Pair<Long, String>> {
    val parentTaskIds = taskParentRepository.findParentTaskIdsByTaskId(taskId)
    return parentTaskIds.mapNotNull { parentTaskId ->
      val parentTask = taskRepository.findById(parentTaskId)
      parentTask?.let { it.id!! to it.title }
    }
  }

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
    tags: List<Long> = emptyList(),
    assignees: List<Long> = emptyList(),
    links: List<LinkInput> = emptyList()
  ): TasksEntity {
    // 카테고리 존재 확인
    val validCategoryId = categoryId?.let {
      if (categoryMapper.existsById(it)) it else null
    }

    var task = TasksEntity(
      title = title,
      description = description,
      categoryId = validCategoryId,
      statusProgress = progress.value,
      statusLifecycle = lifecycle.value,
      startDate = startDate,
      endDate = endDate
    )

    taskRepository.insert(task)
    val taskId = taskRepository.lastInsertId()
    task = task.copy(id = taskId)

    // 태그 처리 - 없으면 생성
    tags.forEach { tagName ->
      var tag = tagRepository.findByName(tagName)
      if (tag == null) {
        tagRepository.insert(TagEntity(name = tagName))
        tag = TagEntity(id = tagRepository.lastInsertId(), name = tagName)
      }
      taskTagRepository.insert(TaskTagEntity(taskId = taskId, tagId = tag.id!!))
    }

    // 담당자 처리 - 없으면 생성
//    assigneeNames.forEach { name ->
//      var member = memberRepository.findByName(name)
//      if (member == null) {
//        memberRepository.insert(MemberEntity(name = name))
//        member = MemberEntity(id = memberRepository.lastInsertId(), name = name)
//      }
//      taskAssigneeRepository.insert(TaskAssigneeEntity(taskId = taskId, memberId = member.id!!))
//    }

    // 링크 처리
    links.forEach { linkInput ->
      taskLinkRepository.insert(
        TaskLinkEntity(
          taskId = taskId,
          url = linkInput.url,
          name = linkInput.name,
          description = linkInput.description
        )
      )
    }

    return task
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
      task.categoryId = null
    } else {
      categoryId?.let {
        if (categoryMapper.existsById(it)) {
          task.categoryId = it
        }
      }
    }

    taskRepository.update(task)
    return task
  }

  /**
   * 진행 상태 변경
   */
  @Transactional
  fun changeProgress(id: Long, progress: TaskProgress): TasksEntity? {
    val task = findByIdNotDeleted(id) ?: return null
    task.setProgress(progress)
    taskRepository.update(task)
    return task
  }

  /**
   * Task 소프트 삭제
   */
  @Transactional
  fun softDelete(id: Long): Boolean {
    val task = taskRepository.findById(id) ?: return false
    task.softDelete()
    taskRepository.update(task)
    return true
  }

  /**
   * Task 하드 삭제 (실제 DB에서 삭제)
   */
  @Transactional
  fun hardDelete(id: Long): Boolean {
    if (!taskRepository.existsById(id)) return false

    // 관계 데이터 먼저 삭제
    taskTagRepository.deleteByTaskId(id)
    taskAssigneeRepository.deleteByTaskId(id)
    taskLinkRepository.deleteByTaskId(id)
    taskDependencyRepository.deleteByTaskId(id)
    taskParentRepository.deleteByTaskId(id)

    taskRepository.deleteById(id)
    return true
  }

  /**
   * 태그 추가
   */
  @Transactional
  fun addTag(taskId: Long, tagName: String): TasksEntity? {
    val task = findByIdNotDeleted(taskId) ?: return null

    var tag = tagRepository.findByName(tagName)
    if (tag == null) {
      tagRepository.insert(TagEntity(name = tagName))
      tag = TagEntity(id = tagRepository.lastInsertId(), name = tagName)
    }

    // 이미 있는 태그인지 확인
    if (taskTagRepository.existsByTaskIdAndTagId(taskId, tag.id!!)) {
      return task
    }

    taskTagRepository.insert(TaskTagEntity(taskId = taskId, tagId = tag.id!!))
    return task
  }

  /**
   * 태그 제거
   */
  @Transactional
  fun removeTag(taskId: Long, tagName: String): TasksEntity? {
    val task = findByIdNotDeleted(taskId) ?: return null
    val tag = tagRepository.findByName(tagName) ?: return task

    taskTagRepository.deleteByTaskIdAndTagId(taskId, tag.id!!)
    return task
  }

  /**
   * 담당자 추가
   */
  @Transactional
  fun addAssignee(taskId: Long, memberName: String): TasksEntity? {
    val task = findByIdNotDeleted(taskId) ?: return null

//    var member = memberRepository.findByName(memberName)
//    if (member == null) {
//      memberRepository.insert(MemberEntity(name = memberName))
//      member = MemberEntity(id = memberRepository.lastInsertId(), name = memberName)
//    }

    // 이미 있는 담당자인지 확인
//    if (taskAssigneeRepository.existsByTaskIdAndMemberId(taskId, member.id!!)) {
//      return task
//    }

    //taskAssigneeRepository.insert(TaskAssigneeEntity(taskId = taskId, memberId = member.id!!))
    return task
  }

  /**
   * 담당자 제거
   */
  @Transactional
  fun removeAssignee(taskId: Long, memberName: String): TasksEntity? {
    val task = findByIdNotDeleted(taskId) ?: return null
    //val member = memberRepository.findByName(memberName) ?: return task

    //taskAssigneeRepository.deleteByTaskIdAndMemberId(taskId, member.id!!)
    return task
  }

  /**
   * 링크 추가
   */
  @Transactional
  fun addLink(taskId: Long, url: String, name: String): TasksEntity? {
    val task = findByIdNotDeleted(taskId) ?: return null
    taskLinkRepository.insert(TaskLinkEntity(taskId = taskId, url = url, name = name))
    return task
  }

  /**
   * 링크 제거
   */
  @Transactional
  fun removeLink(taskId: Long, linkId: Long): TasksEntity? {
    val task = findByIdNotDeleted(taskId) ?: return null
    taskLinkRepository.deleteByTaskIdAndId(taskId, linkId)
    return task
  }

  /**
   * 의존성(선행 작업) 추가
   */
  @Transactional
  fun addDependency(taskId: Long, dependencyTaskId: Long): TasksEntity? {
    val task = findByIdNotDeleted(taskId) ?: return null
    findByIdNotDeleted(dependencyTaskId) ?: return null

    // 자기 자신 참조 방지
    if (taskId == dependencyTaskId) {
      throw IllegalArgumentException("Task는 자기 자신을 의존성으로 설정할 수 없습니다. (taskId: $taskId)")
    }

    // 순환 참조 검사 - dependencyTaskId가 이미 taskId를 의존하고 있는지
    if (hasCircularDependency(taskId, dependencyTaskId)) {
      throw IllegalArgumentException(
        "순환 참조가 발생합니다. Task $dependencyTaskId는 이미 Task $taskId를 직접 또는 간접적으로 의존하고 있습니다."
      )
    }

    // 이미 존재하는 의존성인지 확인
    if (taskDependencyRepository.existsByTaskIdAndDependencyTaskId(taskId, dependencyTaskId)) {
      return task
    }

    taskDependencyRepository.insert(TaskDependencyEntity(taskId = taskId, dependencyTaskId = dependencyTaskId))
    return task
  }

  /**
   * 의존성 제거
   */
  @Transactional
  fun removeDependency(taskId: Long, dependencyTaskId: Long): TasksEntity? {
    val task = findByIdNotDeleted(taskId) ?: return null
    taskDependencyRepository.deleteByTaskIdAndDependencyTaskId(taskId, dependencyTaskId)
    return task
  }

  /**
   * 부모 Task 추가 (여러 개 가능)
   */
  @Transactional
  fun addParent(taskId: Long, parentTaskId: Long): TasksEntity? {
    val task = findByIdNotDeleted(taskId) ?: return null
    findByIdNotDeleted(parentTaskId) ?: return null

    // 자기 자신 참조 방지
    if (taskId == parentTaskId) {
      throw IllegalArgumentException("Task는 자기 자신을 부모로 설정할 수 없습니다. (taskId: $taskId)")
    }

    // 순환 참조 검사 - parentTaskId가 이미 taskId를 부모로 가지고 있는지
    if (hasCircularParent(taskId, parentTaskId)) {
      throw IllegalArgumentException(
        "순환 참조가 발생합니다. Task $parentTaskId는 이미 Task $taskId를 직접 또는 간접적으로 부모로 가지고 있습니다."
      )
    }

    // 이미 존재하는 관계인지 확인
    val existing = taskParentRepository.findByTaskId(taskId)
    if (existing.any { it.parentTaskId == parentTaskId }) {
      return task // 이미 존재하면 그대로 반환
    }

    taskParentRepository.insert(TaskParentEntity(taskId = taskId, parentTaskId = parentTaskId))
    return task
  }

  /**
   * 특정 부모 Task 제거
   */
  @Transactional
  fun removeParent(taskId: Long, parentTaskId: Long): TasksEntity? {
    val task = findByIdNotDeleted(taskId) ?: return null
    taskParentRepository.deleteByTaskIdAndParentTaskId(taskId, parentTaskId)
    return task
  }

  /**
   * 모든 부모 Task 제거
   */
  @Transactional
  fun removeAllParents(taskId: Long): TasksEntity? {
    val task = findByIdNotDeleted(taskId) ?: return null
    taskParentRepository.deleteByTaskId(taskId)
    return task
  }

  // ========== 순환 참조 검증 ==========

  /**
   * Dependency 순환 참조 검사
   * taskId가 dependencyTaskId를 의존성으로 추가하려고 할 때,
   * dependencyTaskId가 이미 taskId를 직접 또는 간접적으로 의존하고 있는지 확인
   *
   * @param taskId 의존성을 추가하려는 Task ID
   * @param dependencyTaskId 의존성으로 추가하려는 Task ID
   * @return 순환 참조가 있으면 true, 없으면 false
   */
  fun hasCircularDependency(taskId: Long, dependencyTaskId: Long): Boolean {
    val visited = mutableSetOf<Long>()
    val stack = mutableListOf(dependencyTaskId)

    while (stack.isNotEmpty()) {
      val currentId = stack.removeAt(stack.size - 1)

      if (currentId == taskId) {
        return true // 순환 참조 발견
      }

      if (visited.contains(currentId)) {
        continue
      }
      visited.add(currentId)

      // currentId가 의존하고 있는 모든 Task를 스택에 추가
      val dependencies = taskDependencyRepository.findDependencyTaskIdsByTaskId(currentId)
      stack.addAll(dependencies.filter { !visited.contains(it) })
    }

    return false
  }

  /**
   * Parent 순환 참조 검사
   * taskId가 parentTaskId를 부모로 추가하려고 할 때,
   * parentTaskId가 이미 taskId를 직접 또는 간접적으로 부모로 가지고 있는지 확인
   *
   * @param taskId 부모를 추가하려는 Task ID
   * @param parentTaskId 부모로 추가하려는 Task ID
   * @return 순환 참조가 있으면 true, 없으면 false
   */
  fun hasCircularParent(taskId: Long, parentTaskId: Long): Boolean {
    val visited = mutableSetOf<Long>()
    val stack = mutableListOf(parentTaskId)

    while (stack.isNotEmpty()) {
      val currentId = stack.removeAt(stack.size - 1)

      if (currentId == taskId) {
        return true // 순환 참조 발견
      }

      if (visited.contains(currentId)) {
        continue
      }
      visited.add(currentId)

      // currentId의 모든 부모 Task를 스택에 추가
      val parents = taskParentRepository.findParentTaskIdsByTaskId(currentId)
      stack.addAll(parents.filter { !visited.contains(it) })
    }

    return false
  }

  /**
   * 여러 Parent Task들이 모두 유효한지 검증 (순환 참조 체크)
   *
   * @param taskId 검증할 Task ID
   * @param parentTaskIds 추가하려는 부모 Task ID 목록
   * @throws IllegalArgumentException 순환 참조가 발견되면
   */
  fun validateParentTasks(taskId: Long, parentTaskIds: List<Long>) {
    parentTaskIds.forEach { parentTaskId ->
      if (taskId == parentTaskId) {
        throw IllegalArgumentException("Task는 자기 자신을 부모로 설정할 수 없습니다. (taskId: $taskId)")
      }

      if (hasCircularParent(taskId, parentTaskId)) {
        throw IllegalArgumentException(
          "순환 참조가 발생합니다. Task $parentTaskId는 이미 Task $taskId를 직접 또는 간접적으로 부모로 가지고 있습니다."
        )
      }
    }
  }

  /**
   * 여러 Dependency Task들이 모두 유효한지 검증 (순환 참조 체크)
   *
   * @param taskId 검증할 Task ID
   * @param dependencyTaskIds 추가하려는 의존성 Task ID 목록
   * @throws IllegalArgumentException 순환 참조가 발견되면
   */
  fun validateDependencyTasks(taskId: Long, dependencyTaskIds: List<Long>) {
    dependencyTaskIds.forEach { dependencyTaskId ->
      if (taskId == dependencyTaskId) {
        throw IllegalArgumentException("Task는 자기 자신을 의존성으로 설정할 수 없습니다. (taskId: $taskId)")
      }

      if (hasCircularDependency(taskId, dependencyTaskId)) {
        throw IllegalArgumentException(
          "순환 참조가 발생합니다. Task $dependencyTaskId는 이미 Task $taskId를 직접 또는 간접적으로 의존하고 있습니다."
        )
      }
    }
  }
}
