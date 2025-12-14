package kr.geun.oss.dowoomi.domain.task.link

import org.apache.ibatis.annotations.*

@Mapper
interface TaskLinkRepository {
    
    @Select("SELECT * FROM task_links WHERE id = #{id}")
    @Results(
        id = "TaskLinkResultMap",
        value = [
            Result(property = "id", column = "id", id = true),
            Result(property = "taskId", column = "task_id"),
            Result(property = "url", column = "url"),
            Result(property = "name", column = "name"),
            Result(property = "description", column = "description")
        ]
    )
    fun findById(@Param("id") id: Long): TaskLinkEntity?

    @Select("SELECT * FROM task_links WHERE task_id = #{taskId}")
    @ResultMap("TaskLinkResultMap")
    fun findByTaskId(@Param("taskId") taskId: Long): List<TaskLinkEntity>
    
    @Insert("INSERT INTO task_links (task_id, url, name, description) VALUES (#{taskId}, #{url}, #{name}, #{description})")
    fun insert(taskLink: TaskLinkEntity): Int
    
    @Delete("DELETE FROM task_links WHERE task_id = #{taskId}")
    fun deleteByTaskId(@Param("taskId") taskId: Long): Int
    
    @Delete("DELETE FROM task_links WHERE task_id = #{taskId} AND id = #{id}")
    fun deleteByTaskIdAndId(@Param("taskId") taskId: Long, @Param("id") id: Long): Int

    @Select("SELECT last_insert_rowid()")
    fun lastInsertId(): Long
}
