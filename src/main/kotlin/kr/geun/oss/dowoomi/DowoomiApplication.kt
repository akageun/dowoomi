package kr.geun.oss.dowoomi

import kr.geun.oss.dowoomi.common.util.LoggerUtil
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DowoomiApplication {
  companion object : LoggerUtil()
}

fun main(args: Array<String>) {
  DowoomiApplication.logger.info("Starting Dowoomi Application...")
  runApplication<DowoomiApplication>(*args)
  DowoomiApplication.logger.info("Dowoomi Application started successfully!")
}
