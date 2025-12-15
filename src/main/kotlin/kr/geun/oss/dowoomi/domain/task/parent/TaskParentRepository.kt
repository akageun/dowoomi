package kr.geun.oss.dowoomi.domain.task.parent

import org.apache.ibatis.annotations.*

@Mapper
interface TaskParentRepository {
    
    @Select("SELECT * FROM task_parents WHERE task_id = #{taskId}")
    @Results(
        id = "TaskParentResultMap",
        value = [
            Result(property = "taskId", column = "task_id"),
            Result(property = "parentTaskId", column = "parent_task_id")
        ]
    )
    fun findByTaskId(@Param("taskId") taskId: Long): List<TaskParentEntity>
    
    @Select("SELECT * FROM task_parents WHERE task_id = #{taskId} LIMIT 1")
    @ResultMap("TaskParentResultMap")
    fun findFirstByTaskId(@Param("taskId") taskId: Long): TaskParentEntity?
    
    @Insert("INSERT INTO task_parents (task_id, parent_task_id) VALUES (#{taskId}, #{parentTaskId})")
    fun insert(taskParent: TaskParentEntity): Int
    
    @Delete("DELETE FROM task_parents WHERE task_id = #{taskId}")
    fun deleteByTaskId(@Param("taskId") taskId: Long): Int
    
    @Delete("DELETE FROM task_parents WHERE task_id = #{taskId} AND parent_task_id = #{parentTaskId}")
    fun deleteByTaskIdAndParentTaskId(@Param("taskId") taskId: Long, @Param("parentTaskId") parentTaskId: Long): Int
    
    @Select("SELECT COUNT(*) > 0 FROM task_parents WHERE task_id = #{taskId} AND parent_task_id = #{parentTaskId}")
    fun existsByTaskIdAndParentTaskId(@Param("taskId") taskId: Long, @Param("parentTaskId") parentTaskId: Long): Boolean
    
    @Select("SELECT parent_task_id FROM task_parents WHERE task_id = #{taskId}")
    fun findParentTaskIdsByTaskId(@Param("taskId") taskId: Long): List<Long>

    @Select("""
        <script>
        SELECT
            tp.task_id as taskId,
            tp.parent_task_id as parentTaskId,
            t.title as parentTitle
        FROM task_parents tp
        INNER JOIN tasks t ON tp.parent_task_id = t.id
        WHERE tp.task_id IN
        <foreach item='taskId' collection='taskIds' open='(' separator=',' close=')'>
            #{taskId}
        </foreach>
        </script>
    """)
    fun findParentsByTaskIds(@Param("taskIds") taskIds: List<Long>): List<TaskWithParentEntity>
}
