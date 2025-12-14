package kr.geun.oss.dowoomi.domain.assignee

import org.apache.ibatis.annotations.*

@Mapper
interface AssigneeRepository {

    @Select("SELECT * FROM assignees WHERE id = #{id}")
    @Results(
        id = "AssigneeResultMap",
        value = [
            Result(property = "id", column = "id", id = true),
            Result(property = "name", column = "name"),
            Result(property = "memo", column = "memo"),
            Result(property = "createdAt", column = "created_at"),
            Result(property = "updatedAt", column = "updated_at")
        ]
    )
    fun findById(@Param("id") id: Long): AssigneeEntity?

    @Select("SELECT * FROM assignees ORDER BY name")
    @ResultMap("AssigneeResultMap")
    fun findAll(): List<AssigneeEntity>

    @Select("""
        <script>
        SELECT * FROM assignees WHERE id IN
        <foreach collection='ids' item='id' open='(' separator=',' close=')'>
            #{id}
        </foreach>
        </script>
    """)
    @ResultMap("AssigneeResultMap")
    fun findAllByIds(@Param("ids") ids: List<Long>): List<AssigneeEntity>

    @Select("SELECT COUNT(*) > 0 FROM assignees WHERE id = #{id}")
    fun existsById(@Param("id") id: Long): Boolean

    @Insert("INSERT INTO assignees (name, memo, created_at, updated_at) VALUES (#{name}, #{memo}, datetime('now'), datetime('now'))")
    fun insert(assignee: AssigneeEntity): Int

    @Update("UPDATE assignees SET name = #{name}, memo = #{memo}, updated_at = datetime('now') WHERE id = #{id}")
    fun update(assignee: AssigneeEntity): Int

    @Delete("DELETE FROM assignees WHERE id = #{id}")
    fun deleteById(@Param("id") id: Long): Int

    @Select("SELECT * FROM assignees WHERE name = #{name}")
    @ResultMap("AssigneeResultMap")
    fun findByName(@Param("name") name: String): AssigneeEntity?

    @Select("SELECT COUNT(*) > 0 FROM assignees WHERE name = #{name}")
    fun existsByName(@Param("name") name: String): Boolean

    @Select("SELECT * FROM assignees WHERE LOWER(name) LIKE '%' || LOWER(#{name}) || '%' ORDER BY name")
    @ResultMap("AssigneeResultMap")
    fun findByNameContaining(@Param("name") name: String): List<AssigneeEntity>

    @Select("SELECT last_insert_rowid()")
    fun lastInsertId(): Long
}
