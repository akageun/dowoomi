//package kr.geun.oss.dowoomi.domain.dashboard
//
//import kr.geun.oss.dowoomi.domain.category.CategoryMapper
//import kr.geun.oss.dowoomi.domain.task.TaskLifecycle
//import kr.geun.oss.dowoomi.domain.task.TaskProgress
//import kr.geun.oss.dowoomi.domain.task.TaskRepository
//import kr.geun.oss.dowoomi.domain.task.TasksEntity
//import org.springframework.stereotype.Service
//import org.springframework.transaction.annotation.Transactional
//
//@Service
//@Transactional(readOnly = true)
//class DashboardService(
//    private val taskRepository: TaskRepository,
//    private val categoryMapper: CategoryMapper
//) {
//
//    /**
//     * 대시보드 전체 통계
//     */
//    fun getDashboardStats(): DashboardStats {
//        val activeTasks = taskRepository.findByStatusLifecycle(TaskLifecycle.ACTIVE.value)
//
//        val progressStats = ProgressStats(
//            todo = activeTasks.count { it.statusProgress == TaskProgress.TODO.value },
//            inProgress = activeTasks.count { it.statusProgress == TaskProgress.IN_PROGRESS.value },
//            done = activeTasks.count { it.statusProgress == TaskProgress.DONE.value }
//        )
//
//        val lifecycleStats = LifecycleStats(
//            active = activeTasks.size,
//            draft = taskRepository.findByStatusLifecycle(TaskLifecycle.DRAFT.value).size,
//            deleted = taskRepository.findByStatusLifecycle(TaskLifecycle.DELETED.value).size
//        )
//
//        val categoryStats = getCategoryStats()
//
//        val upcomingDeadlines = taskRepository.findUpcomingDeadlines(7).size
//        val overdueTasks = taskRepository.findOverdueTasks().size
//        val completedThisWeek = taskRepository.findCompletedThisWeek().size
//
//        return DashboardStats(
//            totalActiveTasks = activeTasks.size,
//            progressStats = progressStats,
//            lifecycleStats = lifecycleStats,
//            categoryStats = categoryStats,
//            upcomingDeadlines = upcomingDeadlines,
//            overdueTasks = overdueTasks,
//            completedThisWeek = completedThisWeek
//        )
//    }
//
//    /**
//     * 카테고리별 통계
//     */
//    fun getCategoryStats(): List<CategoryStats> {
//        val categories = categoryMapper.findAll()
//
//        return categories.mapNotNull { category ->
//            val categoryId = category.id ?: return@mapNotNull null
//            val tasks = taskRepository.findByCategoryId(categoryId)
//            val activeTasks = tasks.filter { it.statusLifecycle == TaskLifecycle.ACTIVE.value }
//
//            CategoryStats(
//                categoryId = categoryId,
//                categoryName = category.name,
//                totalTasks = activeTasks.size,
//                todoCount = activeTasks.count { it.statusProgress == TaskProgress.TODO.value },
//                inProgressCount = activeTasks.count { it.statusProgress == TaskProgress.IN_PROGRESS.value },
//                doneCount = activeTasks.count { it.statusProgress == TaskProgress.DONE.value }
//            )
//        }
//    }
//
//    /**
//     * 카테고리명 조회 헬퍼
//     */
//    private fun getCategoryName(categoryId: Long?): String? {
//        return categoryId?.let { categoryMapper.findById(it)?.name }
//    }
//
//    /**
//     * 오늘 집중해야 할 Task 목록
//     */
//    fun getTodayFocus(): TodayFocus {
//        val focusTasks = taskRepository.findTodayFocusTasks()
//        val readyTasks = taskRepository.findReadyToStartTasks()
//        val overdueTasks = taskRepository.findOverdueTasks()
//
//        return TodayFocus(
//            inProgressTasks = focusTasks.map { it.toSimpleTaskInfo() },
//            readyToStart = readyTasks.map { it.toSimpleTaskInfo() },
//            overdue = overdueTasks.map { it.toSimpleTaskInfo() }
//        )
//    }
//
//    /**
//     * Entity to SimpleTaskInfo 변환
//     */
//    private fun TasksEntity.toSimpleTaskInfo() = SimpleTaskInfo(
//        id = this.id!!,
//        title = this.title,
//        progress = this.statusProgress,
//        categoryName = getCategoryName(this.categoryId),
//        endDate = this.endDate?.toString()
//    )
//}
//
//// ========== DTOs ==========
//
//data class DashboardStats(
//    val totalActiveTasks: Int,
//    val progressStats: ProgressStats,
//    val lifecycleStats: LifecycleStats,
//    val categoryStats: List<CategoryStats>,
//    val upcomingDeadlines: Int,
//    val overdueTasks: Int,
//    val completedThisWeek: Int
//)
//
//data class ProgressStats(
//    val todo: Int,
//    val inProgress: Int,
//    val done: Int
//)
//
//data class LifecycleStats(
//    val active: Int,
//    val draft: Int,
//    val deleted: Int
//)
//
//data class CategoryStats(
//    val categoryId: Long,
//    val categoryName: String,
//    val totalTasks: Int,
//    val todoCount: Int,
//    val inProgressCount: Int,
//    val doneCount: Int
//)
//
//data class TodayFocus(
//    val inProgressTasks: List<SimpleTaskInfo>,
//    val readyToStart: List<SimpleTaskInfo>,
//    val overdue: List<SimpleTaskInfo>
//)
//
//data class SimpleTaskInfo(
//    val id: Long,
//    val title: String,
//    val progress: String,
//    val categoryName: String?,
//    val endDate: String?
//)
