package kr.geun.oss.dowoomi.domain.category

import kr.geun.oss.dowoomi.common.util.LoggerUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 카테고리 서비스
 */
@Service
@Transactional
class CategoryService(
    private val categoryRepository: CategoryRepository
) {
    companion object : LoggerUtil()

    /**
     * 카테고리 생성
     */
    fun createCategory(name: String, description: String? = null): CategoryEntity {
        logger.info("Creating category: $name")

        if (categoryRepository.existsByName(name)) {
            throw IllegalArgumentException("Category already exists: $name")
        }

        val category = CategoryEntity(
            name = name,
            description = description
        )
        return categoryRepository.save(category)
    }

    /**
     * 카테고리 전체 조회
     */
    @Transactional(readOnly = true)
    fun findAll(): List<CategoryEntity> {
        logger.info("Fetching all categories")
        return categoryRepository.findAll()
    }

    /**
     * ID로 카테고리 조회
     */
    @Transactional(readOnly = true)
    fun findById(id: Long): CategoryEntity? {
        return categoryRepository.findById(id).orElse(null)
    }

    /**
     * 이름으로 카테고리 조회
     */
    @Transactional(readOnly = true)
    fun findByName(name: String): CategoryEntity? {
        return categoryRepository.findByName(name)
    }

    /**
     * 카테고리 수정
     */
    fun updateCategory(id: Long, name: String?, description: String?): CategoryEntity {
        logger.info("Updating category: id=$id")

        val category = categoryRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Category not found: $id") }

        name?.let {
            if (it != category.name && categoryRepository.existsByName(it)) {
                throw IllegalArgumentException("Category name already exists: $it")
            }
            category.name = it
        }
        description?.let { category.description = it }

        return categoryRepository.save(category)
    }

    /**
     * 카테고리 삭제
     */
    fun deleteCategory(id: Long) {
        logger.info("Deleting category: $id")

        if (!categoryRepository.existsById(id)) {
            throw IllegalArgumentException("Category not found: $id")
        }

        categoryRepository.deleteById(id)
    }
}
