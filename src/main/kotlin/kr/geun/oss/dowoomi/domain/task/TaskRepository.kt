package kr.geun.oss.dowoomi.domain.task

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface TaskRepository : JpaRepository<TasksEntity, Long> {

    /**
     * 생명주기 상태별 Task 조회 (TEXT 기반)
     */
    fun findByStatusLifecycle(lifecycle: String): List<TasksEntity>

    /**
     * 삭제되지 않은 Task 조회
     */
    fun findByStatusLifecycleNot(lifecycle: String): List<TasksEntity>

    /**
     * 진행 상태별 조회 (TEXT 기반)
     */
    fun findByStatusProgress(progress: String): List<TasksEntity>

    /**
     * 카테고리별 조회
     */
    fun findByCategoryId(categoryId: Long): List<TasksEntity>

    /**
     * 날짜 범위로 조회 (간트 차트용)
     */
    fun findByStartDateBetween(startDate: LocalDate, endDate: LocalDate): List<TasksEntity>

    /**
     * 특정 월의 Task 조회 (간트 차트용)
     * - SQLite strftime 함수 사용
     */
    @Query(
        value = """
            SELECT * FROM tasks t
            WHERE strftime('%Y-%m', t.start_date) = :yearMonth
            AND t.status_lifecycle != 'deleted'
            ORDER BY t.start_date, t.id
        """,
        nativeQuery = true
    )
    fun findByYearMonth(@Param("yearMonth") yearMonth: String): List<TasksEntity>

    /**
     * Active Task 전체 조회 (생성일 기준 정렬)
     */
    @Query("""
        SELECT t FROM TasksEntity t
        WHERE t.statusLifecycle = 'active'
        ORDER BY t.createdAt DESC
    """)
    fun findAllActiveTasks(): List<TasksEntity>

    /**
     * 이번 주에 완료한 Task 조회
     */
    @Query(
        value = """
            SELECT * FROM tasks t
            WHERE t.status_progress = 'done'
            AND t.status_lifecycle = 'active'
            AND date(t.updated_at) >= date('now', 'weekday 0', '-7 days')
            ORDER BY t.updated_at DESC
        """,
        nativeQuery = true
    )
    fun findCompletedThisWeek(): List<TasksEntity>

    /**
     * 마감일이 가까운 Task 조회 (D-Day)
     * - 오늘 기준 N일 이내 마감
     */
    @Query(
        value = """
            SELECT * FROM tasks t
            WHERE t.end_date IS NOT NULL
            AND date(t.end_date) BETWEEN date('now') AND date('now', '+' || :days || ' days')
            AND t.status_lifecycle = 'active'
            AND t.status_progress != 'done'
            ORDER BY t.end_date ASC
        """,
        nativeQuery = true
    )
    fun findUpcomingDeadlines(@Param("days") days: Int): List<TasksEntity>

    /**
     * 기한 초과(Overdue) Task 조회
     */
    @Query(
        value = """
            SELECT * FROM tasks t
            WHERE t.end_date IS NOT NULL
            AND date(t.end_date) < date('now')
            AND t.status_lifecycle = 'active'
            AND t.status_progress != 'done'
            ORDER BY t.end_date ASC
        """,
        nativeQuery = true
    )
    fun findOverdueTasks(): List<TasksEntity>

    /**
     * 오늘 집중해야 할 Task (기간 안 + 진행 중)
     */
    @Query(
        value = """
            SELECT * FROM tasks t
            WHERE t.status_lifecycle = 'active'
            AND t.status_progress = 'in_progress'
            AND (t.start_date IS NULL OR date(t.start_date) <= date('now'))
            AND (t.end_date IS NULL OR date(t.end_date) >= date('now'))
            ORDER BY t.end_date ASC NULLS LAST, t.created_at ASC
        """,
        nativeQuery = true
    )
    fun findTodayFocusTasks(): List<TasksEntity>

    /**
     * 선행 작업이 모두 완료되어 바로 시작 가능한 Task
     */
    @Query(
        value = """
            SELECT t.* FROM tasks t
            WHERE t.status_lifecycle = 'active'
            AND t.status_progress = 'todo'
            AND NOT EXISTS (
                SELECT 1 FROM task_dependencies td
                JOIN tasks dep ON td.dependency_task_id = dep.id
                WHERE td.task_id = t.id
                AND dep.status_progress != 'done'
            )
            ORDER BY t.start_date ASC NULLS LAST, t.created_at ASC
        """,
        nativeQuery = true
    )
    fun findReadyToStartTasks(): List<TasksEntity>
}

