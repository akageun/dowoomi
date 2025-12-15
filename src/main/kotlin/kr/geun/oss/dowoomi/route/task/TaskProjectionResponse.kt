package kr.geun.oss.dowoomi.route.task

import kr.geun.oss.dowoomi.route.tag.TagResponse

/**
 * TaskProjection 응답 DTO
 */
data class TaskProjectionResponse(
    val taskId: Long,
    val title: String,
    val description: String?,
    val category: CategoryResponse?,
    val statusProgress: String,
    val statusLifecycle: String,
    val startDate: String?,
    val endDate: String?,
    val createdAt: String,
    val updatedAt: String,
    val tags: List<TagResponse> = emptyList(),
    val assignees: List<AssigneeResponse> = emptyList(),
    val dependencies: List<SimpleTaskResponse> = emptyList(),
    val parents: List<SimpleTaskResponse> = emptyList()
)

/**
 * Assignee 응답 DTO
 */
data class AssigneeResponse(
    val id: Long,
    val name: String,
    val memo: String?
)

/**
 * 단순 Task 응답 DTO (taskId, title만)
 */
data class SimpleTaskResponse(
    val taskId: Long,
    val title: String
)
