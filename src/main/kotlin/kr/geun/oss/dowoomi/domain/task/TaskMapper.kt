package kr.geun.oss.dowoomi.domain.task

import org.apache.ibatis.annotations.*
import java.time.LocalDate

@Mapper
interface TaskMapper {

    /**
     * ID로 Task 조회
     */
    @Select("SELECT * FROM tasks WHERE id = #{id}")
    @Results(
        id = "TaskResultMap",
        value = [
            Result(property = "id", column = "id", id = true),
            Result(property = "title", column = "title"),
            Result(property = "description", column = "description"),
            Result(property = "categoryId", column = "category_id"),
            Result(property = "statusProgress", column = "status_progress"),
            Result(property = "statusLifecycle", column = "status_lifecycle"),
            Result(property = "startDate", column = "start_date"),
            Result(property = "endDate", column = "end_date"),
            Result(property = "createdAt", column = "created_at"),
            Result(property = "updatedAt", column = "updated_at")
        ]
    )
    fun findById(@Param("id") id: Long): TasksEntity?

    /**
     * TaskProjection 조회 (카테고리 정보 포함)
     */
    @Select("""
        SELECT 
            t.id as task_id,
            t.title,
            t.description,
            t.status_progress,
            t.status_lifecycle,
            t.start_date,
            t.end_date,
            t.created_at,
            t.updated_at,
            c.id as category_id,
            c.name as category_name,
            c.description as category_description,
            c.created_at as category_created_at,
            c.updated_at as category_updated_at
        FROM tasks t
        LEFT JOIN categories c ON t.category_id = c.id
        WHERE t.id = #{id}
    """)
    @Results(
        id = "TaskProjectionResultMap",
        value = [
            Result(property = "taskId", column = "task_id", id = true),
            Result(property = "title", column = "title"),
            Result(property = "description", column = "description"),
            Result(property = "statusProgress", column = "status_progress"),
            Result(property = "statusLifecycle", column = "status_lifecycle"),
            Result(property = "startDate", column = "start_date"),
            Result(property = "endDate", column = "end_date"),
            Result(property = "createdAt", column = "created_at"),
            Result(property = "updatedAt", column = "updated_at"),
            Result(property = "category.categoryId", column = "category_id"),
            Result(property = "category.name", column = "category_name"),
            Result(property = "category.description", column = "category_description"),
            Result(property = "category.createdAt", column = "category_created_at"),
            Result(property = "category.updatedAt", column = "category_updated_at")
        ]
    )
    fun findProjectionById(@Param("id") id: Long): TaskProjection?

    /**
     * 전체 TaskProjection 조회 (카테고리 정보 포함, 동적 검색 조건)
     */
    @Select("""
        <script>
        SELECT 
            t.id as task_id,
            t.title,
            t.description,
            t.status_progress,
            t.status_lifecycle,
            t.start_date,
            t.end_date,
            t.created_at,
            t.updated_at,
            c.id as category_id,
            c.name as category_name,
            c.description as category_description,
            c.created_at as category_created_at,
            c.updated_at as category_updated_at
        FROM tasks t
        LEFT JOIN categories c ON t.category_id = c.id
        <where>
            <if test='param.categoryId != null'>
                AND t.category_id = #{param.categoryId}
            </if>
            <if test='param.statusProgress != null'>
                AND t.status_progress = #{param.statusProgress}
            </if>
            <if test='param.statusLifecycle != null'>
                AND t.status_lifecycle = #{param.statusLifecycle}
            </if>
            <if test='param.startDateFrom != null'>
                AND t.start_date >= #{param.startDateFrom}
            </if>
            <if test='param.startDateTo != null'>
                AND t.start_date <= #{param.startDateTo}
            </if>
            <if test='param.endDateFrom != null'>
                AND t.end_date >= #{param.endDateFrom}
            </if>
            <if test='param.endDateTo != null'>
                AND t.end_date <= #{param.endDateTo}
            </if>
            <if test='param.keyword != null and param.keyword != ""'>
                AND LOWER(t.title) LIKE '%' || LOWER(#{param.keyword}) || '%'
            </if>
        </where>
        ORDER BY t.created_at DESC
        </script>
    """)
    @ResultMap("TaskProjectionResultMap")
    fun findAllProjections(@Param("param") param: TaskFindAllParam): List<TaskProjection>

    /**
     * ID 목록으로 TaskProjection 조회 (카테고리 정보 포함)
     */
    @Select("""
        <script>
        SELECT 
            t.id as task_id,
            t.title,
            t.description,
            t.status_progress,
            t.status_lifecycle,
            t.start_date,
            t.end_date,
            t.created_at,
            t.updated_at,
            c.id as category_id,
            c.name as category_name,
            c.description as category_description,
            c.created_at as category_created_at,
            c.updated_at as category_updated_at
        FROM tasks t
        LEFT JOIN categories c ON t.category_id = c.id
        WHERE t.id IN
        <foreach collection='ids' item='id' open='(' separator=',' close=')'>
            #{id}
        </foreach>
        ORDER BY t.created_at DESC
        </script>
    """)
    @ResultMap("TaskProjectionResultMap")
    fun findProjectionsByIds(@Param("ids") ids: List<Long>): List<TaskProjection>

    /**
     * Active TaskProjection 조회 (카테고리 정보 포함)
     */
    @Select("""
        SELECT 
            t.id as task_id,
            t.title,
            t.description,
            t.status_progress,
            t.status_lifecycle,
            t.start_date,
            t.end_date,
            t.created_at,
            t.updated_at,
            c.id as category_id,
            c.name as category_name,
            c.description as category_description,
            c.created_at as category_created_at,
            c.updated_at as category_updated_at
        FROM tasks t
        LEFT JOIN categories c ON t.category_id = c.id
        WHERE t.status_lifecycle = 'active'
        ORDER BY t.created_at DESC
    """)
    @ResultMap("TaskProjectionResultMap")
    fun findAllActiveProjections(): List<TaskProjection>

    /**
     * 전체 Task 조회
     */
    @Select("SELECT * FROM tasks ORDER BY created_at DESC")
    @ResultMap("TaskResultMap")
    fun findAll(): List<TasksEntity>

    /**
     * ID 목록으로 Task 조회
     */
    @Select("""
        <script>
        SELECT * FROM tasks WHERE id IN
        <foreach collection='ids' item='id' open='(' separator=',' close=')'>
            #{id}
        </foreach>
        </script>
    """)
    @ResultMap("TaskResultMap")
    fun findAllByIds(@Param("ids") ids: List<Long>): List<TasksEntity>

    /**
     * ID 존재 여부 확인
     */
    @Select("SELECT COUNT(*) > 0 FROM tasks WHERE id = #{id}")
    fun existsById(@Param("id") id: Long): Boolean

    /**
     * Task 저장 (신규)
     */
    @Insert("""
        INSERT INTO tasks (title, description, category_id, status_progress, status_lifecycle, start_date, end_date, created_at, updated_at)
        VALUES (#{title}, #{description}, #{categoryId}, #{statusProgress}, #{statusLifecycle}, #{startDate}, #{endDate}, datetime('now'), datetime('now'))
    """)
    fun insert(task: TasksEntity): Int

    /**
     * Task 수정
     */
    @Update("""
        UPDATE tasks SET
            title = #{title},
            description = #{description},
            category_id = #{categoryId},
            status_progress = #{statusProgress},
            status_lifecycle = #{statusLifecycle},
            start_date = #{startDate},
            end_date = #{endDate},
            updated_at = datetime('now')
        WHERE id = #{id}
    """)
    fun update(task: TasksEntity): Int

    /**
     * Task 삭제
     */
    @Delete("DELETE FROM tasks WHERE id = #{id}")
    fun deleteById(@Param("id") id: Long): Int

    /**
     * 생명주기 상태별 Task 조회
     */
    @Select("SELECT * FROM tasks WHERE status_lifecycle = #{lifecycle} ORDER BY created_at DESC")
    @ResultMap("TaskResultMap")
    fun findByStatusLifecycle(@Param("lifecycle") lifecycle: String): List<TasksEntity>

    /**
     * 진행 상태별 조회
     */
    @Select("SELECT * FROM tasks WHERE status_progress = #{progress} ORDER BY created_at DESC")
    @ResultMap("TaskResultMap")
    fun findByStatusProgress(@Param("progress") progress: String): List<TasksEntity>

    /**
     * 카테고리별 조회
     */
    @Select("SELECT * FROM tasks WHERE category_id = #{categoryId} ORDER BY created_at DESC")
    @ResultMap("TaskResultMap")
    fun findByCategoryId(@Param("categoryId") categoryId: Long): List<TasksEntity>

    /**
     * 날짜 범위로 조회 (간트 차트용)
     */
    @Select("SELECT * FROM tasks WHERE start_date BETWEEN #{startDate} AND #{endDate} ORDER BY start_date")
    @ResultMap("TaskResultMap")
    fun findByStartDateBetween(@Param("startDate") startDate: LocalDate, @Param("endDate") endDate: LocalDate): List<TasksEntity>

    /**
     * 특정 월의 Task 조회 (간트 차트용)
     */
    @Select("""
        SELECT * FROM tasks
        WHERE strftime('%Y-%m', start_date) = #{yearMonth}
           OR strftime('%Y-%m', end_date) = #{yearMonth}
        ORDER BY start_date
    """)
    @ResultMap("TaskResultMap")
    fun findByYearMonth(@Param("yearMonth") yearMonth: String): List<TasksEntity>

    /**
     * Active Task 전체 조회 (생성일 기준 정렬)
     */
    @Select("SELECT * FROM tasks WHERE status_lifecycle = 'active' ORDER BY created_at DESC")
    @ResultMap("TaskResultMap")
    fun findAllActiveTasks(): List<TasksEntity>



    /**
     * 이번 주에 완료한 Task 조회
     */
    @Select("""
        SELECT * FROM tasks
        WHERE status_progress = 'done'
          AND date(updated_at) >= date('now', 'weekday 0', '-7 days')
        ORDER BY updated_at DESC
    """)
    @ResultMap("TaskResultMap")
    fun findCompletedThisWeek(): List<TasksEntity>

    /**
     * 마감일이 가까운 Task 조회 (D-Day)
     */
    @Select("""
        SELECT * FROM tasks
        WHERE status_lifecycle = 'active'
          AND status_progress != 'done'
          AND end_date IS NOT NULL
          AND date(end_date) BETWEEN date('now') AND date('now', '+' || #{days} || ' days')
        ORDER BY end_date
    """)
    @ResultMap("TaskResultMap")
    fun findUpcomingDeadlines(@Param("days") days: Int): List<TasksEntity>

    /**
     * 기한 초과(Overdue) Task 조회
     */
    @Select("""
        SELECT * FROM tasks
        WHERE status_lifecycle = 'active'
          AND status_progress != 'done'
          AND end_date IS NOT NULL
          AND date(end_date) < date('now')
        ORDER BY end_date
    """)
    @ResultMap("TaskResultMap")
    fun findOverdueTasks(): List<TasksEntity>

    /**
     * 오늘 집중해야 할 Task
     */
    @Select("""
        SELECT * FROM tasks
        WHERE status_lifecycle = 'active'
          AND status_progress != 'done'
          AND (
              date(start_date) = date('now')
              OR date(end_date) = date('now')
              OR (date(start_date) <= date('now') AND date(end_date) >= date('now'))
          )
        ORDER BY end_date, start_date
    """)
    @ResultMap("TaskResultMap")
    fun findTodayFocusTasks(): List<TasksEntity>

    /**
     * 선행 작업이 모두 완료되어 바로 시작 가능한 Task
     */
    @Select("""
        SELECT t.* FROM tasks t
        WHERE t.status_lifecycle = 'active'
          AND t.status_progress = 'todo'
          AND NOT EXISTS (
              SELECT 1 FROM task_dependencies td
              JOIN tasks dt ON td.dependency_task_id = dt.id
              WHERE td.task_id = t.id
                AND dt.status_progress != 'done'
          )
        ORDER BY t.created_at DESC
    """)
    @ResultMap("TaskResultMap")
    fun findReadyToStartTasks(): List<TasksEntity>

    /**
     * 제목으로 Task 검색 (LIKE 검색)
     */
    @Select("SELECT * FROM tasks WHERE LOWER(title) LIKE '%' || LOWER(#{keyword}) || '%' ORDER BY created_at DESC LIMIT #{limit}")
    @ResultMap("TaskResultMap")
    fun searchByTitle(@Param("keyword") keyword: String, @Param("limit") limit: Int): List<TasksEntity>

    /**
     * 마지막 삽입된 ID 조회 (SQLite용)
     */
    @Select("SELECT last_insert_rowid()")
    fun lastInsertId(): Long
}

