package kr.geun.oss.dowoomi.route.assignees

/**
 * 담당자 생성 요청
 */
data class CreateAssigneeRequest(
    val name: String,
    val memo: String? = null
)

/**
 * 담당자 수정 요청
 */
data class UpdateAssigneeRequest(
    val name: String? = null,
    val memo: String? = null
)
