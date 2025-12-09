package kr.geun.oss.dowoomi.domain.task.link

/**
 * Task 링크 데이터 클래스
 * links: [{ name, description?, url }]
 */
data class TaskLinkEntity(
    val id: Long? = null,
    var taskId: Long,
    var name: String,
    var description: String? = null,
    var url: String
)
