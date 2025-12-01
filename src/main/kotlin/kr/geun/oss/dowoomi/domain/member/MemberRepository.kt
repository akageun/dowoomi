package kr.geun.oss.dowoomi.domain.member

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : JpaRepository<MemberEntity, Long> {

    /**
     * 이름으로 멤버 조회
     */
    fun findByName(name: String): MemberEntity?

    /**
     * 이름으로 멤버 존재 여부 확인
     */
    fun existsByName(name: String): Boolean

    /**
     * 여러 이름으로 멤버 조회
     */
    fun findByNameIn(names: List<String>): List<MemberEntity>

    /**
     * 이름에 포함된 멤버 조회
     */
    fun findByNameContainingIgnoreCase(name: String): List<MemberEntity>
}
