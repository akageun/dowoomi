package kr.geun.oss.dowoomi.route.task

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class TasksApi {

  @PostMapping("/tasks")
  fun createTask(): String {
    return "task created"
  }

}
