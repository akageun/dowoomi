package kr.geun.oss.dowoomi.domain.task.parent

import kr.geun.oss.dowoomi.route.task.SimpleTaskResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TaskParentService(
    private val taskParentRepository: TaskParentRepository
) {

    /**
     * taskIds로 해당 Task들의 Parent Task 정보 조회
     * @return Map<taskId, List<SimpleTaskResponse>>
     */
    fun findParentsByTaskIds(taskIds: List<Long>): Map<Long, List<SimpleTaskResponse>> {
        if (taskIds.isEmpty()) return emptyMap()

        val taskWithParents = taskParentRepository.findParentsByTaskIds(taskIds)

        // taskId별로 그룹핑
        return taskWithParents.groupBy { it.taskId }
            .mapValues { (_, parents) ->
                parents.map { taskWithParent ->
                    SimpleTaskResponse(
                        taskId = taskWithParent.parentTaskId,
                        title = taskWithParent.parentTitle
                    )
                }
            }
    }

    /**
     * taskId로 해당 Task의 Parent Task 정보 조회
     */
    fun findParentsByTaskId(taskId: Long): List<SimpleTaskResponse> {
        return findParentsByTaskIds(listOf(taskId))[taskId] ?: emptyList()
    }

    /**
     * Task에 Parent 추가
     */
    @Transactional
    fun addParentToTask(taskId: Long, parentTaskId: Long) {
        if (taskParentRepository.existsByTaskIdAndParentTaskId(taskId, parentTaskId)) {
            throw IllegalArgumentException("이미 추가된 Parent Task입니다.")
        }
        taskParentRepository.insert(TaskParentEntity(taskId = taskId, parentTaskId = parentTaskId))
    }

    /**
     * Task에서 Parent 제거
     */
    @Transactional
    fun removeParentFromTask(taskId: Long, parentTaskId: Long) {
        taskParentRepository.deleteByTaskIdAndParentTaskId(taskId, parentTaskId)
    }

    /**
     * Task의 모든 Parent 제거
     */
    @Transactional
    fun removeAllParentsFromTask(taskId: Long) {
        taskParentRepository.deleteByTaskId(taskId)
    }
}
