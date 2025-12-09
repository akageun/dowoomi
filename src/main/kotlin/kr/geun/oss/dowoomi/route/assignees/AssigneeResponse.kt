package kr.geun.oss.dowoomi.route.assignees

import kr.geun.oss.dowoomi.domain.assignee.AssigneeEntity

/**
 * 담당자 응답
 */
data class AssigneeResponse(
    val id: Long,
    val name: String,
    val memo: String?,
    val createdAt: String,
    val updatedAt: String
)

data class AssigneeListResponse(
  val assignees: List<AssigneeResponse>
){

}

/**
 * Entity to Response 변환
 */
fun AssigneeEntity.toResponse() = AssigneeResponse(
    id = this.id!!,
    name = this.name,
    memo = this.memo,
    createdAt = this.createdAt.toString(),
    updatedAt = this.updatedAt.toString()
)
