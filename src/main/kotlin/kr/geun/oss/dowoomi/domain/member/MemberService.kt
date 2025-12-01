package kr.geun.oss.dowoomi.domain.member

import kr.geun.oss.dowoomi.common.util.LoggerUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 멤버 서비스
 */
@Service
@Transactional
class MemberService(
    private val memberRepository: MemberRepository
) {
    companion object : LoggerUtil()

    /**
     * 멤버 생성
     */
    fun createMember(name: String): MemberEntity {
        logger.info("Creating member: $name")

        if (memberRepository.existsByName(name)) {
            throw IllegalArgumentException("Member already exists: $name")
        }

        val member = MemberEntity(name = name)
        return memberRepository.save(member)
    }

    /**
     * 멤버 전체 조회
     */
    @Transactional(readOnly = true)
    fun findAll(): List<MemberEntity> {
        logger.info("Fetching all members")
        return memberRepository.findAll()
    }

    /**
     * ID로 멤버 조회
     */
    @Transactional(readOnly = true)
    fun findById(id: Long): MemberEntity? {
        return memberRepository.findById(id).orElse(null)
    }

    /**
     * 이름으로 멤버 조회
     */
    @Transactional(readOnly = true)
    fun findByName(name: String): MemberEntity? {
        return memberRepository.findByName(name)
    }

    /**
     * 멤버 수정
     */
    fun updateMember(id: Long, name: String): MemberEntity {
        logger.info("Updating member: id=$id, name=$name")

        val member = memberRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Member not found: $id") }

        if (name != member.name && memberRepository.existsByName(name)) {
            throw IllegalArgumentException("Member name already exists: $name")
        }

        member.name = name
        return memberRepository.save(member)
    }

    /**
     * 멤버 삭제
     */
    fun deleteMember(id: Long) {
        logger.info("Deleting member: $id")

        if (!memberRepository.existsById(id)) {
            throw IllegalArgumentException("Member not found: $id")
        }

        memberRepository.deleteById(id)
    }

    /**
     * 이름으로 멤버 조회 또는 생성
     */
    fun findOrCreateByName(name: String): MemberEntity {
        return memberRepository.findByName(name)
            ?: createMember(name)
    }
}
