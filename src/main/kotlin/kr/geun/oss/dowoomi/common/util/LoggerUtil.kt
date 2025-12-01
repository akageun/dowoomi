package kr.geun.oss.dowoomi.common.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Logger Utility
 *
 * Usage:
 * ```
 * class MyClass {
 *     companion object : LoggerUtil()
 *
 *     fun someMethod() {
 *         logger.info("This is info log")
 *         logger.debug("This is debug log")
 *         logger.error("This is error log", exception)
 *     }
 * }
 * ```
 */
open class LoggerUtil {
    val logger: Logger
        get() = LoggerFactory.getLogger(getClassName())

    private fun getClassName(): Class<*> {
        return this.javaClass.enclosingClass ?: this.javaClass
    }
}

/**
 * Extension function to get logger for any class
 *
 * Usage:
 * ```
 * class MyClass {
 *     private val log = logger()
 *
 *     fun someMethod() {
 *         log.info("This is info log")
 *     }
 * }
 * ```
 */
inline fun <reified T : Any> T.logger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}

