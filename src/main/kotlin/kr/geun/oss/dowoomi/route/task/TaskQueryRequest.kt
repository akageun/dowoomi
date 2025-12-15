package kr.geun.oss.dowoomi.route.task

import kr.geun.oss.dowoomi.domain.task.TaskFindAllParam
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

/**
 * Task 전체 조회 요청 DTO
 */
data class TaskQueryAllRequest(
    val categoryId: Long? = null,
    val statusProgress: String? = null,
    val statusLifecycle: String? = null,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val startDateFrom: LocalDate? = null,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val startDateTo: LocalDate? = null,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val endDateFrom: LocalDate? = null,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val endDateTo: LocalDate? = null,
    val keyword: String? = null
) {
    fun toParam() = TaskFindAllParam(
        categoryId = categoryId,
        statusProgress = statusProgress,
        statusLifecycle = statusLifecycle,
        startDateFrom = startDateFrom,
        startDateTo = startDateTo,
        endDateFrom = endDateFrom,
        endDateTo = endDateTo,
        keyword = keyword
    )
}
