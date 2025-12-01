package kr.geun.oss.dowoomi.domain.category

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : JpaRepository<CategoryEntity, Long> {

    /**
     * 이름으로 카테고리 조회
     */
    fun findByName(name: String): CategoryEntity?

    /**
     * 이름으로 카테고리 존재 여부 확인
     */
    fun existsByName(name: String): Boolean

    /**
     * 이름에 포함된 카테고리 조회
     */
    fun findByNameContainingIgnoreCase(name: String): List<CategoryEntity>
}
