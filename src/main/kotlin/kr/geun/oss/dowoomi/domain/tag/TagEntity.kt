package kr.geun.oss.dowoomi.domain.tag

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 태그 마스터 엔티티
 */
@Entity
@Table(name = "tags")
class TagEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    var name: String,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)
