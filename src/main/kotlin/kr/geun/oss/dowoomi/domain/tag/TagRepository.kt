package kr.geun.oss.dowoomi.domain.tag

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TagRepository : JpaRepository<TagEntity, Long> {

    /**
     * 이름으로 태그 조회
     */
    fun findByName(name: String): TagEntity?

    /**
     * 이름으로 태그 존재 여부 확인
     */
    fun existsByName(name: String): Boolean

    /**
     * 여러 이름으로 태그 조회
     */
    fun findByNameIn(names: List<String>): List<TagEntity>

    /**
     * 이름에 포함된 태그 조회
     */
    fun findByNameContainingIgnoreCase(name: String): List<TagEntity>
}
