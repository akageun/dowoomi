package kr.geun.oss.dowoomi.domain.task

import kr.geun.oss.dowoomi.common.util.LoggerUtil
import org.springframework.stereotype.Service
import java.time.LocalDate

/**
 * Task 조회 전용 서비스
 */
@Service
class TaskRetriever(
    private val taskMapper: TaskMapper
) {
    companion object : LoggerUtil()

    /**
     * ID로 TaskProjection 조회
     */
    fun findProjectionById(id: Long): TaskProjection? {
        logger.debug("Fetching TaskProjection by id: $id")
        return taskMapper.findProjectionById(id)
    }

    /**
     * ID 목록으로 TaskProjection 조회
     */
    fun findProjectionsByIds(ids: List<Long>): List<TaskProjection> {
        if (ids.isEmpty()) return emptyList()
        logger.debug("Fetching TaskProjections by ids: ${ids.size} items")
        return taskMapper.findProjectionsByIds(ids)
    }

    /**
     * 조건에 따른 TaskProjection 조회
     */
    fun findAllProjections(
        param: TaskFindAllParam
    ): List<TaskProjection> {
        logger.debug("Fetching TaskProjections with filters - categoryId: ${param.categoryId}, statusProgress: ${param.statusProgress}, statusLifecycle: ${param.statusLifecycle}, keyword: ${param.keyword}")
        return taskMapper.findAllProjections(param)
    }

    /**
     * Active TaskProjection 조회
     */
    fun findAllActiveProjections(): List<TaskProjection> {
        logger.debug("Fetching all active TaskProjections")
        return taskMapper.findAllActiveProjections()
    }
}
