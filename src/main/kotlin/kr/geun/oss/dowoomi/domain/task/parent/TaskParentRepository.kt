package kr.geun.oss.dowoomi.domain.task.parent

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface TaskParentRepository {
    
    fun findByTaskId(@Param("taskId") taskId: Long): List<TaskParentEntity>
    
    fun findFirstByTaskId(@Param("taskId") taskId: Long): TaskParentEntity?
    
    fun insert(taskParent: TaskParentEntity): Int
    
    fun deleteByTaskId(@Param("taskId") taskId: Long): Int
    
    fun deleteByTaskIdAndParentTaskId(@Param("taskId") taskId: Long, @Param("parentTaskId") parentTaskId: Long): Int
    
    fun existsByTaskIdAndParentTaskId(@Param("taskId") taskId: Long, @Param("parentTaskId") parentTaskId: Long): Boolean
    
    fun findParentTaskIdsByTaskId(@Param("taskId") taskId: Long): List<Long>
}
