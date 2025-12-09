package kr.geun.oss.dowoomi.domain.task.assignee

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface TaskAssigneeRepository {
    
    fun findByTaskId(@Param("taskId") taskId: Long): List<TaskAssigneeEntity>
    
    fun insert(taskAssignee: TaskAssigneeEntity): Int
    
    fun deleteByTaskId(@Param("taskId") taskId: Long): Int
    
    fun deleteByTaskIdAndMemberId(@Param("taskId") taskId: Long, @Param("memberId") memberId: Long): Int
    
    fun existsByTaskIdAndMemberId(@Param("taskId") taskId: Long, @Param("memberId") memberId: Long): Boolean
    
    fun findMemberIdsByTaskId(@Param("taskId") taskId: Long): List<Long>
}
