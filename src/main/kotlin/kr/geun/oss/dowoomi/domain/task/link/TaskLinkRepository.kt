package kr.geun.oss.dowoomi.domain.task.link

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface TaskLinkRepository {
    
    fun findById(@Param("id") id: Long): TaskLinkEntity?

    fun findByTaskId(@Param("taskId") taskId: Long): List<TaskLinkEntity>
    
    fun insert(taskLink: TaskLinkEntity): Int
    
    fun deleteByTaskId(@Param("taskId") taskId: Long): Int
    
    fun deleteByTaskIdAndId(@Param("taskId") taskId: Long, @Param("id") id: Long): Int

    fun lastInsertId(): Long
}
