package kr.geun.oss.dowoomi.domain.assignee

import kr.geun.oss.dowoomi.common.exception.ResourceNotFoundException
import kr.geun.oss.dowoomi.common.util.LoggerUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 담당자 서비스
 */
@Service
@Transactional
class AssigneeService(
    private val assigneeRepository: AssigneeRepository
) {
    companion object : LoggerUtil()

    /**
     * 담당자 생성
     */
    fun createAssignee(name: String, memo: String?): AssigneeEntity {
        logger.info("Creating assignee: $name")

        if (assigneeRepository.existsByName(name)) {
            throw IllegalArgumentException("이미 존재하는 담당자 이름입니다: $name")
        }

        val assignee = AssigneeEntity(
            name = name,
            memo = memo
        )
        assigneeRepository.insert(assignee)
        return assignee.copy(id = assigneeRepository.lastInsertId())
    }

    /**
     * 담당자 전체 조회
     */
    @Transactional(readOnly = true)
    fun findAll(): List<AssigneeEntity> {
        logger.info("Fetching all assignees")
        return assigneeRepository.findAll()
    }

    /**
     * ID로 담당자 조회
     */
    @Transactional(readOnly = true)
    fun findById(id: Long): AssigneeEntity? {
        return assigneeRepository.findById(id)
    }

    /**
     * 이름으로 담당자 조회
     */
    @Transactional(readOnly = true)
    fun findByName(name: String): AssigneeEntity? {
        return assigneeRepository.findByName(name)
    }

    /**
     * 담당자 수정
     */
    fun updateAssignee(id: Long, name: String?, memo: String?): AssigneeEntity {
        logger.info("Updating assignee: id=$id")

        val assignee = assigneeRepository.findById(id)
            ?: throw ResourceNotFoundException("담당자를 찾을 수 없습니다: $id")

        // 이름 변경 검증
        if (name != null && name != assignee.name) {
            if (assigneeRepository.existsByName(name)) {
                throw IllegalArgumentException("이미 존재하는 담당자 이름입니다: $name")
            }
            assignee.name = name
        }

        // 메모 변경
        if (memo != null) {
            assignee.memo = memo
        }

        assigneeRepository.update(assignee)
        return assignee
    }

    /**
     * 담당자 삭제
     */
    fun deleteAssignee(id: Long) {
        logger.info("Deleting assignee: $id")

        if (!assigneeRepository.existsById(id)) {
            throw ResourceNotFoundException("담당자를 찾을 수 없습니다: $id")
        }

        assigneeRepository.deleteById(id)
    }

    /**
     * 이름으로 담당자 검색
     */
    @Transactional(readOnly = true)
    fun searchByName(name: String): List<AssigneeEntity> {
        return assigneeRepository.findByNameContaining(name)
    }
}
