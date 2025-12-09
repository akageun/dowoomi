package kr.geun.oss.dowoomi.domain.category

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface CategoryMapper {

    fun findById(@Param("id") id: Long): CategoryEntity?

    fun findAll(): List<CategoryEntity>

    fun findAllByIds(@Param("ids") ids: List<Long>): List<CategoryEntity>

    fun existsById(@Param("id") id: Long): Boolean

    fun insert(category: CategoryEntity): Int

    fun update(category: CategoryEntity): Int

    fun deleteById(@Param("id") id: Long): Int

    fun findByName(@Param("name") name: String): CategoryEntity?

    fun existsByName(@Param("name") name: String): Boolean

    fun findByNameContaining(@Param("name") name: String): List<CategoryEntity>

    fun lastInsertId(): Long
}
