package kr.geun.oss.dowoomi.domain.tag

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TagService(
    private val tagRepository: TagRepository
) {

    /**
     * 모든 태그 조회
     */
    fun findAll(): List<TagEntity> = tagRepository.findAll()

    /**
     * ID로 태그 조회
     */
    fun findById(id: Long): TagEntity? = tagRepository.findById(id)

    /**
     * 이름으로 태그 조회
     */
    fun findByName(name: String): TagEntity? = tagRepository.findByName(name)

    /**
     * 이름 목록으로 태그 조회
     */
    fun findByNames(names: List<String>): List<TagEntity> = tagRepository.findByNameIn(names)

    /**
     * 이름에 포함된 문자열로 검색
     */
    fun search(keyword: String): List<TagEntity> = 
        tagRepository.findByNameContaining(keyword)

    /**
     * 태그 생성
     */
    @Transactional
    fun create(name: String): TagEntity {
        // 중복 체크
        if (tagRepository.existsByName(name)) {
            throw IllegalArgumentException("이미 존재하는 태그입니다: $name")
        }
        val tag = TagEntity(name = name)
        tagRepository.insert(tag)
        return tag.copy(id = tagRepository.lastInsertId())
    }

    /**
     * 태그 생성 또는 조회 (없으면 생성)
     */
    @Transactional
    fun findOrCreate(name: String): TagEntity {
        val existing = tagRepository.findByName(name)
        if (existing != null) return existing
        
        val tag = TagEntity(name = name)
        tagRepository.insert(tag)
        return tag.copy(id = tagRepository.lastInsertId())
    }

    /**
     * 태그 수정
     */
    @Transactional
    fun update(id: Long, name: String): TagEntity? {
        val tag = findById(id) ?: return null
        
        // 다른 태그와 이름 중복 체크
        val existingTag = tagRepository.findByName(name)
        if (existingTag != null && existingTag.id != id) {
            throw IllegalArgumentException("이미 존재하는 태그 이름입니다: $name")
        }
        
        tag.name = name
        tagRepository.update(tag)
        return tag
    }

    /**
     * 태그 삭제
     */
    @Transactional
    fun delete(id: Long): Boolean {
        if (!tagRepository.existsById(id)) return false
        tagRepository.deleteById(id)
        return true
    }

    /**
     * 태그 존재 여부 확인
     */
    fun existsByName(name: String): Boolean = tagRepository.existsByName(name)
}
