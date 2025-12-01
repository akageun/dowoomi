-- =============================================
-- Task / 글 작업 관리 시스템 - SQLite Schema
-- =============================================

-- 카테고리 테이블: categories
CREATE TABLE IF NOT EXISTS categories (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    name        TEXT NOT NULL UNIQUE,
    description TEXT,
    created_at  TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at  TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE INDEX IF NOT EXISTS idx_categories_name ON categories(name);

-- 메인 Task 테이블: tasks
CREATE TABLE IF NOT EXISTS tasks (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    title            TEXT NOT NULL,
    description      TEXT,

    -- 카테고리 ID (categories.id 참조)
    category_id      INTEGER,

    -- 진행 상태: todo / in_progress / done (TEXT로만 관리)
    status_progress  TEXT NOT NULL DEFAULT 'todo',

    -- 생명주기: active / draft / deleted (TEXT로만 관리)
    status_lifecycle TEXT NOT NULL DEFAULT 'active',

    -- 기간 (YYYY-MM-DD 형식 문자열)
    start_date       TEXT,
    end_date         TEXT,

    created_at       TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at       TEXT NOT NULL DEFAULT (datetime('now'))
);

-- 자주 쓰는 인덱스
CREATE INDEX IF NOT EXISTS idx_tasks_start_date        ON tasks(start_date);
CREATE INDEX IF NOT EXISTS idx_tasks_category_id       ON tasks(category_id);
CREATE INDEX IF NOT EXISTS idx_tasks_status_progress   ON tasks(status_progress);
CREATE INDEX IF NOT EXISTS idx_tasks_status_lifecycle  ON tasks(status_lifecycle);

-- Task 관련 링크: task_links
CREATE TABLE IF NOT EXISTS task_links (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    task_id     INTEGER NOT NULL,  -- tasks.id
    name        TEXT NOT NULL,
    description TEXT,
    url         TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_task_links_task_id ON task_links(task_id);

-- 태그 마스터 테이블: tags
CREATE TABLE IF NOT EXISTS tags (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    name        TEXT NOT NULL UNIQUE,
    created_at  TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE INDEX IF NOT EXISTS idx_tags_name ON tags(name);

-- Task ↔ Tag 매핑 테이블: task_tags
CREATE TABLE IF NOT EXISTS task_tags (
    task_id INTEGER NOT NULL,  -- tasks.id
    tag_id  INTEGER NOT NULL,  -- tags.id

    PRIMARY KEY (task_id, tag_id)
);

CREATE INDEX IF NOT EXISTS idx_task_tags_task_id ON task_tags(task_id);
CREATE INDEX IF NOT EXISTS idx_task_tags_tag_id  ON task_tags(tag_id);

-- 담당자: members (email 제거)
CREATE TABLE IF NOT EXISTS members (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    name       TEXT NOT NULL,
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE INDEX IF NOT EXISTS idx_members_name ON members(name);

-- Task ↔ Member 매핑: task_assignees
CREATE TABLE IF NOT EXISTS task_assignees (
    task_id   INTEGER NOT NULL,  -- tasks.id
    member_id INTEGER NOT NULL,  -- members.id

    PRIMARY KEY (task_id, member_id)
);

CREATE INDEX IF NOT EXISTS idx_task_assignees_task_id   ON task_assignees(task_id);
CREATE INDEX IF NOT EXISTS idx_task_assignees_member_id ON task_assignees(member_id);

-- 상위 작업 관계: task_parents
CREATE TABLE IF NOT EXISTS task_parents (
    task_id        INTEGER NOT NULL,  -- 자식 Task (tasks.id)
    parent_task_id INTEGER NOT NULL,  -- 상위 Task (tasks.id)

    PRIMARY KEY (task_id, parent_task_id)
);

CREATE INDEX IF NOT EXISTS idx_task_parents_task_id   ON task_parents(task_id);
CREATE INDEX IF NOT EXISTS idx_task_parents_parent_id ON task_parents(parent_task_id);

-- 선행 작업(디펜던시): task_dependencies
CREATE TABLE IF NOT EXISTS task_dependencies (
    task_id            INTEGER NOT NULL,  -- 이 Task가
    dependency_task_id INTEGER NOT NULL,  -- 끝나야 진행 가능

    PRIMARY KEY (task_id, dependency_task_id)
);

CREATE INDEX IF NOT EXISTS idx_task_deps_task_id       ON task_dependencies(task_id);
CREATE INDEX IF NOT EXISTS idx_task_deps_dependency_id ON task_dependencies(dependency_task_id);
