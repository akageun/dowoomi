package kr.geun.oss.dowoomi.domain.task.tag

import kr.geun.oss.dowoomi.domain.tag.TagEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TaskTagService(
    private val taskTagMapper: TaskTagMapper
) {

    /**
     * taskIds로 해당 Task들의 Tag 정보 조회
     * @return Map<taskId, List<TagEntity>>
     */
    fun findTagsByTaskIds(taskIds: List<Long>): Map<Long, List<TagEntity>> {
        if (taskIds.isEmpty()) return emptyMap()

        val taskWithTags = taskTagMapper.findTagsByTaskIds(taskIds)

        // taskId별로 그룹핑
        return taskWithTags.groupBy { it.taskId }
            .mapValues { (_, tags) ->
                tags.map { taskWithTag ->
                    TagEntity(
                        id = taskWithTag.id,
                        name = taskWithTag.name,
                        createdAt = taskWithTag.createdAt
                    )
                }
            }
    }

    /**
     * taskId로 해당 Task의 Tag 정보 조회
     */
    fun findTagsByTaskId(taskId: Long): List<TagEntity> {
        return findTagsByTaskIds(listOf(taskId))[taskId] ?: emptyList()
    }

    /**
     * Task에 Tag 추가
     */
    @Transactional
    fun addTagToTask(taskId: Long, tagId: Long) {
        if (taskTagMapper.existsByTaskIdAndTagId(taskId, tagId)) {
            throw IllegalArgumentException("이미 추가된 태그입니다.")
        }
        taskTagMapper.insert(TaskTagEntity(taskId = taskId, tagId = tagId))
    }

    /**
     * Task에서 Tag 제거
     */
    @Transactional
    fun removeTagFromTask(taskId: Long, tagId: Long) {
        taskTagMapper.deleteByTaskIdAndTagId(taskId, tagId)
    }

    /**
     * Task의 모든 Tag 제거
     */
    @Transactional
    fun removeAllTagsFromTask(taskId: Long) {
        taskTagMapper.clearByTaskId(taskId)
    }
}
