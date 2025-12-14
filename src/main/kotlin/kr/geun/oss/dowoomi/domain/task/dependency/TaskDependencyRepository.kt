package kr.geun.oss.dowoomi.domain.task.dependency

import org.apache.ibatis.annotations.*

@Mapper
interface TaskDependencyRepository {
    
    @Select("SELECT * FROM task_dependencies WHERE task_id = #{taskId}")
    @Results(
        id = "TaskDependencyResultMap",
        value = [
            Result(property = "taskId", column = "task_id"),
            Result(property = "dependencyTaskId", column = "dependency_task_id")
        ]
    )
    fun findByTaskId(@Param("taskId") taskId: Long): List<TaskDependencyEntity>
    
    @Insert("INSERT INTO task_dependencies (task_id, dependency_task_id) VALUES (#{taskId}, #{dependencyTaskId})")
    fun insert(taskDependency: TaskDependencyEntity): Int
    
    @Delete("DELETE FROM task_dependencies WHERE task_id = #{taskId}")
    fun deleteByTaskId(@Param("taskId") taskId: Long): Int
    
    @Delete("DELETE FROM task_dependencies WHERE task_id = #{taskId} AND dependency_task_id = #{dependencyTaskId}")
    fun deleteByTaskIdAndDependencyTaskId(@Param("taskId") taskId: Long, @Param("dependencyTaskId") dependencyTaskId: Long): Int
    
    @Select("SELECT COUNT(*) > 0 FROM task_dependencies WHERE task_id = #{taskId} AND dependency_task_id = #{dependencyTaskId}")
    fun existsByTaskIdAndDependencyTaskId(@Param("taskId") taskId: Long, @Param("dependencyTaskId") dependencyTaskId: Long): Boolean
    
    @Select("SELECT dependency_task_id FROM task_dependencies WHERE task_id = #{taskId}")
    fun findDependencyTaskIdsByTaskId(@Param("taskId") taskId: Long): List<Long>
}
