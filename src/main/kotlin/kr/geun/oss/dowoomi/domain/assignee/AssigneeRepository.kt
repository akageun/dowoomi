package kr.geun.oss.dowoomi.domain.assignee

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface AssigneeRepository {

    fun findById(@Param("id") id: Long): AssigneeEntity?

    fun findAll(): List<AssigneeEntity>

    fun findAllByIds(@Param("ids") ids: List<Long>): List<AssigneeEntity>

    fun existsById(@Param("id") id: Long): Boolean

    fun insert(assignee: AssigneeEntity): Int

    fun update(assignee: AssigneeEntity): Int

    fun deleteById(@Param("id") id: Long): Int

    fun findByName(@Param("name") name: String): AssigneeEntity?

    fun existsByName(@Param("name") name: String): Boolean

    fun findByNameContaining(@Param("name") name: String): List<AssigneeEntity>

    fun lastInsertId(): Long
}
