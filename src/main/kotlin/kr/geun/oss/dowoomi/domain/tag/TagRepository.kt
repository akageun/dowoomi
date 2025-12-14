package kr.geun.oss.dowoomi.domain.tag

import org.apache.ibatis.annotations.*

@Mapper
interface TagRepository {

    @Select("SELECT * FROM tags WHERE id = #{id}")
    @Results(
        id = "TagResultMap",
        value = [
            Result(property = "id", column = "id", id = true),
            Result(property = "name", column = "name"),
            Result(property = "createdAt", column = "created_at")
        ]
    )
    fun findById(@Param("id") id: Long): TagEntity?

    @Select("SELECT * FROM tags ORDER BY name")
    @ResultMap("TagResultMap")
    fun findAll(): List<TagEntity>

    @Select("""
        <script>
        SELECT * FROM tags WHERE id IN
        <foreach collection='ids' item='id' open='(' separator=',' close=')'>
            #{id}
        </foreach>
        </script>
    """)
    @ResultMap("TagResultMap")
    fun findAllByIds(@Param("ids") ids: List<Long>): List<TagEntity>

    @Select("SELECT COUNT(*) > 0 FROM tags WHERE id = #{id}")
    fun existsById(@Param("id") id: Long): Boolean

    @Insert("INSERT INTO tags (name, created_at) VALUES (#{name}, datetime('now'))")
    fun insert(tag: TagEntity): Int

    @Update("UPDATE tags SET name = #{name} WHERE id = #{id}")
    fun update(tag: TagEntity): Int

    @Delete("DELETE FROM tags WHERE id = #{id}")
    fun deleteById(@Param("id") id: Long): Int

    @Select("SELECT * FROM tags WHERE name = #{name}")
    @ResultMap("TagResultMap")
    fun findByName(@Param("name") name: String): TagEntity?

    @Select("SELECT COUNT(*) > 0 FROM tags WHERE name = #{name}")
    fun existsByName(@Param("name") name: String): Boolean

    @Select("""
        <script>
        SELECT * FROM tags WHERE name IN
        <foreach collection='names' item='name' open='(' separator=',' close=')'>
            #{name}
        </foreach>
        </script>
    """)
    @ResultMap("TagResultMap")
    fun findByNameIn(@Param("names") names: List<String>): List<TagEntity>

    @Select("SELECT * FROM tags WHERE LOWER(name) LIKE '%' || LOWER(#{name}) || '%' ORDER BY name")
    @ResultMap("TagResultMap")
    fun findByNameContaining(@Param("name") name: String): List<TagEntity>

    @Select("SELECT last_insert_rowid()")
    fun lastInsertId(): Long
}
