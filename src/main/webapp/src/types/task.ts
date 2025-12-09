// Task 관련 타입 정의

export interface Task {
  id: number;
  title: string;
  description: string | null;
  categoryId: number | null;
  categoryName: string | null;
  progress: TaskProgress;
  lifecycle: TaskLifecycle;
  startDate: string | null;
  endDate: string | null;
  tags: string[];
  assignees: Assignee[];
  links: Link[];
  dependencies: Dependency[];
  parents: Parent[];
  createdAt: string;
  updatedAt: string;
}

export type TaskProgress = 'todo' | 'in_progress' | 'done';
export type TaskLifecycle = 'active' | 'draft' | 'deleted';

export interface Assignee {
  id: number;
  name: string;
}

export interface Link {
  id: number;
  url: string;
  name: string;
  description: string | null;
}

export interface Dependency {
  taskId: number;
  taskTitle: string;
}

export interface Parent {
  taskId: number;
  taskTitle: string;
}

// Request DTOs
export interface CreateTaskRequest {
  title: string;
  description?: string;
  categoryId?: number;
  progress?: TaskProgress;
  lifecycle?: TaskLifecycle;
  startDate?: string;
  endDate?: string;
  tags?: string[];
  assignees?: string[];
  links?: CreateLinkRequest[];
}

export interface CreateLinkRequest {
  url: string;
  name: string;
  description?: string;
}

export interface UpdateTaskRequest {
  title?: string;
  description?: string;
  categoryId?: number;
  clearCategory?: boolean;
  progress?: TaskProgress;
  lifecycle?: TaskLifecycle;
  startDate?: string;
  endDate?: string;
}

export interface ChangeProgressRequest {
  progress: TaskProgress;
}

export interface AddTagRequest {
  tag: string;
}

export interface AddAssigneeRequest {
  name: string;
}

export interface AddLinkRequest {
  url: string;
  name: string;
}

export interface AddDependencyRequest {
  dependencyTaskId: number;
}

export interface AddParentRequest {
  parentTaskId: number;
}

// 진행 상태 한글 라벨
export const progressLabels: Record<TaskProgress, string> = {
  todo: '할 일',
  in_progress: '진행 중',
  done: '완료',
};

// 생명주기 한글 라벨
export const lifecycleLabels: Record<TaskLifecycle, string> = {
  active: '활성',
  draft: '초안',
  deleted: '삭제됨',
};
