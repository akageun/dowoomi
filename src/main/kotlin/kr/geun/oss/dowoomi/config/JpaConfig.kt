package kr.geun.oss.dowoomi.config

import org.springframework.context.annotation.Configuration

/**
 * JPA Configuration for SQLite
 * - TEXT 기반 스키마 사용으로 JpaAuditing 제거
 * - 날짜/시간은 엔티티에서 직접 관리
 */
@Configuration
class JpaConfig
