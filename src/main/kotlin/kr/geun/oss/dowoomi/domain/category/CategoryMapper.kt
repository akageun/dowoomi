package kr.geun.oss.dowoomi.domain.category

import org.apache.ibatis.annotations.*

@Mapper
interface CategoryMapper {

    @Select("SELECT * FROM categories WHERE id = #{id}")
    @Results(
        id = "CategoryResultMap",
        value = [
            Result(property = "id", column = "id", id = true),
            Result(property = "name", column = "name"),
            Result(property = "description", column = "description"),
            Result(property = "createdAt", column = "created_at"),
            Result(property = "updatedAt", column = "updated_at")
        ]
    )
    fun findById(@Param("id") id: Long): CategoryEntity?

    @Select("SELECT * FROM categories ORDER BY name")
    @ResultMap("CategoryResultMap")
    fun findAll(): List<CategoryEntity>

    @Select("""
        <script>
        SELECT * FROM categories WHERE id IN
        <foreach collection='ids' item='id' open='(' separator=',' close=')'>
            #{id}
        </foreach>
        </script>
    """)
    @ResultMap("CategoryResultMap")
    fun findAllByIds(@Param("ids") ids: List<Long>): List<CategoryEntity>

    @Select("SELECT COUNT(*) > 0 FROM categories WHERE id = #{id}")
    fun existsById(@Param("id") id: Long): Boolean

    @Insert("INSERT INTO categories (name, description, created_at, updated_at) VALUES (#{name}, #{description}, datetime('now'), datetime('now'))")
    fun insert(category: CategoryEntity): Int

    @Update("UPDATE categories SET name = #{name}, description = #{description}, updated_at = datetime('now') WHERE id = #{id}")
    fun update(category: CategoryEntity): Int

    @Delete("DELETE FROM categories WHERE id = #{id}")
    fun deleteById(@Param("id") id: Long): Int

    @Select("SELECT * FROM categories WHERE name = #{name}")
    @ResultMap("CategoryResultMap")
    fun findByName(@Param("name") name: String): CategoryEntity?

    @Select("SELECT COUNT(*) > 0 FROM categories WHERE name = #{name}")
    fun existsByName(@Param("name") name: String): Boolean

    @Select("SELECT * FROM categories WHERE LOWER(name) LIKE '%' || LOWER(#{name}) || '%' ORDER BY name")
    @ResultMap("CategoryResultMap")
    fun findByNameContaining(@Param("name") name: String): List<CategoryEntity>

    @Select("SELECT last_insert_rowid()")
    fun lastInsertId(): Long
}
