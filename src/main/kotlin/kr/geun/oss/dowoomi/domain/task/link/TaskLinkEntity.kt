package kr.geun.oss.dowoomi.domain.task.link

import jakarta.persistence.*
import kr.geun.oss.dowoomi.domain.task.TasksEntity

/**
 * Task 링크 엔티티
 * links: [{ name, description?, url }]
 */
@Entity
@Table(
    name = "task_links",
    indexes = [
        Index(name = "idx_task_links_task_id", columnList = "task_id")
    ]
)
class TaskLinkEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    var task: TasksEntity,

    @Column(nullable = false)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(nullable = false)
    var url: String
)
