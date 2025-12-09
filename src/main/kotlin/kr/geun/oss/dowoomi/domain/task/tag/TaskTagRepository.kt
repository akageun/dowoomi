package kr.geun.oss.dowoomi.domain.task.tag

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface TaskTagRepository {
    
    fun findByTaskId(@Param("taskId") taskId: Long): List<TaskTagEntity>
    
    fun insert(taskTag: TaskTagEntity): Int
    
    fun deleteByTaskId(@Param("taskId") taskId: Long): Int
    
    fun deleteByTaskIdAndTagId(@Param("taskId") taskId: Long, @Param("tagId") tagId: Long): Int
    
    fun existsByTaskIdAndTagId(@Param("taskId") taskId: Long, @Param("tagId") tagId: Long): Boolean
    
    fun findTagIdsByTaskId(@Param("taskId") taskId: Long): List<Long>
}
