package kr.geun.oss.dowoomi.route

import kr.geun.oss.dowoomi.domain.dashboard.CategoryStats
import kr.geun.oss.dowoomi.domain.dashboard.DashboardService
import kr.geun.oss.dowoomi.domain.dashboard.DashboardStats
import kr.geun.oss.dowoomi.domain.dashboard.TodayFocus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/dashboard")
class DashboardController(
    private val dashboardService: DashboardService
) {

    /**
     * 대시보드 전체 통계 조회
     */
    @GetMapping
    fun getDashboardStats(): ResponseEntity<DashboardStats> {
        return ResponseEntity.ok(dashboardService.getDashboardStats())
    }

    /**
     * 카테고리별 통계 조회
     */
    @GetMapping("/category-stats")
    fun getCategoryStats(): ResponseEntity<List<CategoryStats>> {
        return ResponseEntity.ok(dashboardService.getCategoryStats())
    }

    /**
     * 오늘 집중해야 할 Task 조회
     */
    @GetMapping("/today-focus")
    fun getTodayFocus(): ResponseEntity<TodayFocus> {
        return ResponseEntity.ok(dashboardService.getTodayFocus())
    }
}
