package kr.geun.oss.dowoomi.domain.task

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TaskRegister(
  private val taskRepository: TaskRepository,
) {

  @Transactional
  fun registryTask(){

  }
}
