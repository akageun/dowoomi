package kr.geun.oss.dowoomi.domain.task

import java.time.LocalDate

/**
 * Task 조회 파라미터
 */
data class TaskFindAllParam(
    val categoryId: Long? = null,
    val statusProgress: String? = null,
    val statusLifecycle: String? = null,
    val startDateFrom: LocalDate? = null,
    val startDateTo: LocalDate? = null,
    val endDateFrom: LocalDate? = null,
    val endDateTo: LocalDate? = null,
    val keyword: String? = null
)
