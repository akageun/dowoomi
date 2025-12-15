package kr.geun.oss.dowoomi.domain.task

import kr.geun.oss.dowoomi.domain.category.Category
import java.time.LocalDate
import java.time.LocalDateTime

data class TaskProjection(
    val taskId: Long,
    val title: String,
    val description: String? = null,
    val category: Category? = null,
    val statusProgress: String,
    val statusLifecycle: String,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
}
