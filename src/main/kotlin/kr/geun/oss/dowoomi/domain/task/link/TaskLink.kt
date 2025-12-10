package kr.geun.oss.dowoomi.domain.task.link

data class TaskLink(
  val taskLinkId: Long,
  var taskId: Long,
  var name: String,
  var description: String? = null,
  var url: String,
) {
}
