package kr.geun.oss.dowoomi.domain.task

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import java.time.LocalDate

@Mapper
interface TaskRepository {

    /**
     * ID로 Task 조회
     */
    fun findById(@Param("id") id: Long): TasksEntity?

    /**
     * 전체 Task 조회
     */
    fun findAll(): List<TasksEntity>

    /**
     * ID 목록으로 Task 조회
     */
    fun findAllByIds(@Param("ids") ids: List<Long>): List<TasksEntity>

    /**
     * ID 존재 여부 확인
     */
    fun existsById(@Param("id") id: Long): Boolean

    /**
     * Task 저장 (신규)
     */
    fun insert(task: TasksEntity): Int

    /**
     * Task 수정
     */
    fun update(task: TasksEntity): Int

    /**
     * Task 삭제
     */
    fun deleteById(@Param("id") id: Long): Int

    /**
     * 생명주기 상태별 Task 조회
     */
    fun findByStatusLifecycle(@Param("lifecycle") lifecycle: String): List<TasksEntity>

    /**
     * 진행 상태별 조회
     */
    fun findByStatusProgress(@Param("progress") progress: String): List<TasksEntity>

    /**
     * 카테고리별 조회
     */
    fun findByCategoryId(@Param("categoryId") categoryId: Long): List<TasksEntity>

    /**
     * 날짜 범위로 조회 (간트 차트용)
     */
    fun findByStartDateBetween(@Param("startDate") startDate: LocalDate, @Param("endDate") endDate: LocalDate): List<TasksEntity>

    /**
     * 특정 월의 Task 조회 (간트 차트용)
     */
    fun findByYearMonth(@Param("yearMonth") yearMonth: String): List<TasksEntity>

    /**
     * Active Task 전체 조회 (생성일 기준 정렬)
     */
    fun findAllActiveTasks(): List<TasksEntity>

    /**
     * 이번 주에 완료한 Task 조회
     */
    fun findCompletedThisWeek(): List<TasksEntity>

    /**
     * 마감일이 가까운 Task 조회 (D-Day)
     */
    fun findUpcomingDeadlines(@Param("days") days: Int): List<TasksEntity>

    /**
     * 기한 초과(Overdue) Task 조회
     */
    fun findOverdueTasks(): List<TasksEntity>

    /**
     * 오늘 집중해야 할 Task
     */
    fun findTodayFocusTasks(): List<TasksEntity>

    /**
     * 선행 작업이 모두 완료되어 바로 시작 가능한 Task
     */
    fun findReadyToStartTasks(): List<TasksEntity>

    /**
     * 제목으로 Task 검색 (LIKE 검색)
     */
    fun searchByTitle(@Param("keyword") keyword: String, @Param("limit") limit: Int): List<TasksEntity>

    /**
     * 마지막 삽입된 ID 조회 (SQLite용)
     */
    fun lastInsertId(): Long
}

