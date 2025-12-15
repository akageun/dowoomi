package kr.geun.oss.dowoomi.domain.task.assignee

import org.apache.ibatis.annotations.*

@Mapper
interface TaskAssigneeRepository {
    
    @Select("SELECT * FROM task_assignees WHERE task_id = #{taskId}")
    @Results(
        id = "TaskAssigneeResultMap",
        value = [
            Result(property = "taskId", column = "task_id"),
            Result(property = "memberId", column = "member_id")
        ]
    )
    fun findByTaskId(@Param("taskId") taskId: Long): List<TaskAssigneeEntity>
    
    @Insert("INSERT INTO task_assignees (task_id, member_id) VALUES (#{taskId}, #{memberId})")
    fun insert(taskAssignee: TaskAssigneeEntity): Int
    
    @Delete("DELETE FROM task_assignees WHERE task_id = #{taskId}")
    fun deleteByTaskId(@Param("taskId") taskId: Long): Int
    
    @Delete("DELETE FROM task_assignees WHERE task_id = #{taskId} AND member_id = #{memberId}")
    fun deleteByTaskIdAndMemberId(@Param("taskId") taskId: Long, @Param("memberId") memberId: Long): Int
    
    @Select("SELECT COUNT(*) > 0 FROM task_assignees WHERE task_id = #{taskId} AND member_id = #{memberId}")
    fun existsByTaskIdAndMemberId(@Param("taskId") taskId: Long, @Param("memberId") memberId: Long): Boolean
    
    @Select("SELECT member_id FROM task_assignees WHERE task_id = #{taskId}")
    fun findMemberIdsByTaskId(@Param("taskId") taskId: Long): List<Long>

    @Select("""
        <script>
        SELECT
            ta.task_id as taskId,
            ta.member_id as memberId,
            a.name as memberName,
            a.memo as memberMemo
        FROM task_assignees ta
        INNER JOIN assignees a ON ta.member_id = a.id
        WHERE ta.task_id IN
        <foreach item='taskId' collection='taskIds' open='(' separator=',' close=')'>
            #{taskId}
        </foreach>
        </script>
    """)
    fun findAssigneesByTaskIds(@Param("taskIds") taskIds: List<Long>): List<TaskWithAssigneeEntity>
}
