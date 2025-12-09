package kr.geun.oss.dowoomi.domain.tag

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface TagRepository {

    fun findById(@Param("id") id: Long): TagEntity?

    fun findAll(): List<TagEntity>

    fun findAllByIds(@Param("ids") ids: List<Long>): List<TagEntity>

    fun existsById(@Param("id") id: Long): Boolean

    fun insert(tag: TagEntity): Int

    fun update(tag: TagEntity): Int

    fun deleteById(@Param("id") id: Long): Int

    fun findByName(@Param("name") name: String): TagEntity?

    fun existsByName(@Param("name") name: String): Boolean

    fun findByNameIn(@Param("names") names: List<String>): List<TagEntity>

    fun findByNameContaining(@Param("name") name: String): List<TagEntity>

    fun lastInsertId(): Long
}
