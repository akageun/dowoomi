package kr.geun.oss.dowoomi.route.categories

/**
 * 카테고리 레이블 Enum
 * 여러 세트의 레이블을 관리합니다.
 */
enum class CategoryLabel(
    val displayName: String,
    val colorCode: String
) {
    // 기본 레이블
    DEFAULT("기본", "#6B7280"),
    
    // 우선순위 레이블
    HIGH_PRIORITY("높은 우선순위", "#EF4444"),
    MEDIUM_PRIORITY("보통 우선순위", "#F59E0B"),
    LOW_PRIORITY("낮은 우선순위", "#10B981"),
    
    // 상태 레이블
    IN_PROGRESS("진행중", "#3B82F6"),
    COMPLETED("완료", "#22C55E"),
    PENDING("대기", "#9CA3AF"),
    
    // 유형 레이블
    WORK("업무", "#8B5CF6"),
    PERSONAL("개인", "#EC4899"),
    STUDY("학습", "#06B6D4"),
    PROJECT("프로젝트", "#F97316");

    companion object {
        /**
         * displayName으로 레이블 찾기
         */
        fun findByDisplayName(displayName: String): CategoryLabel? {
            return entries.find { it.displayName == displayName }
        }

        /**
         * colorCode로 레이블 찾기
         */
        fun findByColorCode(colorCode: String): CategoryLabel? {
            return entries.find { it.colorCode == colorCode }
        }
    }
}
