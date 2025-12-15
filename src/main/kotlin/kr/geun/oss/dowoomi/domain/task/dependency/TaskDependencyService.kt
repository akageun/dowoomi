package kr.geun.oss.dowoomi.domain.task.dependency

import kr.geun.oss.dowoomi.route.task.SimpleTaskResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TaskDependencyService(
    private val taskDependencyRepository: TaskDependencyRepository
) {

    /**
     * taskIds로 해당 Task들의 Dependency Task 정보 조회
     * @return Map<taskId, List<SimpleTaskResponse>>
     */
    fun findDependenciesByTaskIds(taskIds: List<Long>): Map<Long, List<SimpleTaskResponse>> {
        if (taskIds.isEmpty()) return emptyMap()

        val taskWithDependencies = taskDependencyRepository.findDependenciesByTaskIds(taskIds)

        // taskId별로 그룹핑
        return taskWithDependencies.groupBy { it.taskId }
            .mapValues { (_, dependencies) ->
                dependencies.map { taskWithDependency ->
                    SimpleTaskResponse(
                        taskId = taskWithDependency.dependencyTaskId,
                        title = taskWithDependency.dependencyTitle
                    )
                }
            }
    }

    /**
     * taskId로 해당 Task의 Dependency Task 정보 조회
     */
    fun findDependenciesByTaskId(taskId: Long): List<SimpleTaskResponse> {
        return findDependenciesByTaskIds(listOf(taskId))[taskId] ?: emptyList()
    }

    /**
     * Task에 Dependency 추가
     */
    @Transactional
    fun addDependencyToTask(taskId: Long, dependencyTaskId: Long) {
        if (taskDependencyRepository.existsByTaskIdAndDependencyTaskId(taskId, dependencyTaskId)) {
            throw IllegalArgumentException("이미 추가된 Dependency Task입니다.")
        }
        taskDependencyRepository.insert(TaskDependencyEntity(taskId = taskId, dependencyTaskId = dependencyTaskId))
    }

    /**
     * Task에서 Dependency 제거
     */
    @Transactional
    fun removeDependencyFromTask(taskId: Long, dependencyTaskId: Long) {
        taskDependencyRepository.deleteByTaskIdAndDependencyTaskId(taskId, dependencyTaskId)
    }

    /**
     * Task의 모든 Dependency 제거
     */
    @Transactional
    fun removeAllDependenciesFromTask(taskId: Long) {
        taskDependencyRepository.deleteByTaskId(taskId)
    }
}
