package kr.geun.oss.dowoomi.domain.task

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TaskRegister(
    private val taskMapper: TaskMapper,
) {

  @Transactional
  fun registryTask(){

  }
}
