package kr.geun.oss.dowoomi.domain.task.assignee

import kr.geun.oss.dowoomi.route.task.AssigneeResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TaskAssigneeService(
    private val taskAssigneeRepository: TaskAssigneeRepository
) {

    /**
     * taskIds로 해당 Task들의 Assignee 정보 조회
     * @return Map<taskId, List<AssigneeResponse>>
     */
    fun findAssigneesByTaskIds(taskIds: List<Long>): Map<Long, List<AssigneeResponse>> {
        if (taskIds.isEmpty()) return emptyMap()

        val taskWithAssignees = taskAssigneeRepository.findAssigneesByTaskIds(taskIds)

        // taskId별로 그룹핑
        return taskWithAssignees.groupBy { it.taskId }
            .mapValues { (_, assignees) ->
                assignees.map { taskWithAssignee ->
                    AssigneeResponse(
                        id = taskWithAssignee.memberId,
                        name = taskWithAssignee.memberName,
                        memo = taskWithAssignee.memberMemo
                    )
                }
            }
    }

    /**
     * taskId로 해당 Task의 Assignee 정보 조회
     */
    fun findAssigneesByTaskId(taskId: Long): List<AssigneeResponse> {
        return findAssigneesByTaskIds(listOf(taskId))[taskId] ?: emptyList()
    }

    /**
     * Task에 Assignee 추가
     */
    @Transactional
    fun addAssigneeToTask(taskId: Long, memberId: Long) {
        if (taskAssigneeRepository.existsByTaskIdAndMemberId(taskId, memberId)) {
            throw IllegalArgumentException("이미 추가된 Assignee입니다.")
        }
        taskAssigneeRepository.insert(TaskAssigneeEntity(taskId = taskId, memberId = memberId))
    }

    /**
     * Task에서 Assignee 제거
     */
    @Transactional
    fun removeAssigneeFromTask(taskId: Long, memberId: Long) {
        taskAssigneeRepository.deleteByTaskIdAndMemberId(taskId, memberId)
    }

    /**
     * Task의 모든 Assignee 제거
     */
    @Transactional
    fun removeAllAssigneesFromTask(taskId: Long) {
        taskAssigneeRepository.deleteByTaskId(taskId)
    }
}
