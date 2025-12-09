package kr.geun.oss.dowoomi.domain.task.dependency

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface TaskDependencyRepository {
    
    fun findByTaskId(@Param("taskId") taskId: Long): List<TaskDependencyEntity>
    
    fun insert(taskDependency: TaskDependencyEntity): Int
    
    fun deleteByTaskId(@Param("taskId") taskId: Long): Int
    
    fun deleteByTaskIdAndDependencyTaskId(@Param("taskId") taskId: Long, @Param("dependencyTaskId") dependencyTaskId: Long): Int
    
    fun existsByTaskIdAndDependencyTaskId(@Param("taskId") taskId: Long, @Param("dependencyTaskId") dependencyTaskId: Long): Boolean
    
    fun findDependencyTaskIdsByTaskId(@Param("taskId") taskId: Long): List<Long>
}
