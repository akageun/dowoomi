package kr.geun.oss.dowoomi.domain.task.tag

import org.apache.ibatis.annotations.*

@Mapper
interface TaskTagMapper {

    @Select("SELECT * FROM task_tags WHERE task_id = #{taskId}")
    @Results(
        id = "TaskTagResultMap",
        value = [
            Result(property = "taskId", column = "task_id"),
            Result(property = "tagId", column = "tag_id")
        ]
    )
    fun findByTaskId(@Param("taskId") taskId: Long): List<TaskTagEntity>

    @Insert("INSERT INTO task_tags (task_id, tag_id) VALUES (#{taskId}, #{tagId})")
    fun insert(taskTag: TaskTagEntity): Int

    @Delete("DELETE FROM task_tags WHERE task_id = #{taskId}")
    fun clearByTaskId(@Param("taskId") taskId: Long): Int

    @Delete("DELETE FROM task_tags WHERE task_id = #{taskId} AND tag_id = #{tagId}")
    fun deleteByTaskIdAndTagId(@Param("taskId") taskId: Long, @Param("tagId") tagId: Long): Int

    @Select("SELECT COUNT(*) > 0 FROM task_tags WHERE task_id = #{taskId} AND tag_id = #{tagId}")
    fun existsByTaskIdAndTagId(@Param("taskId") taskId: Long, @Param("tagId") tagId: Long): Boolean

    @Select("SELECT tag_id FROM task_tags WHERE task_id = #{taskId}")
    fun findTagIdsByTaskId(@Param("taskId") taskId: Long): List<Long>

    @Select("""
        <script>
        SELECT
            t.id,
            t.name,
            t.created_at as createdAt,
            tt.task_id as taskId
        FROM task_tags tt
        INNER JOIN tags t ON tt.tag_id = t.id
        WHERE tt.task_id IN
        <foreach item='taskId' collection='taskIds' open='(' separator=',' close=')'>
            #{taskId}
        </foreach>
        </script>
    """)
    fun findTagsByTaskIds(@Param("taskIds") taskIds: List<Long>): List<TaskWithTagEntity>
}
