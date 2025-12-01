import axios from 'axios';
import type {
  Task,
  CreateTaskRequest,
  UpdateTaskRequest,
  ChangeProgressRequest,
  AddTagRequest,
  AddAssigneeRequest,
  AddLinkRequest,
  AddDependencyRequest,
  SetParentRequest,
} from '../types/task';

const API_BASE = '/api/tasks';

export const taskApi = {
  // ========== 조회 API ==========

  /**
   * 모든 Active Task 조회
   */
  getAllTasks: async (): Promise<Task[]> => {
    const response = await axios.get<Task[]>(API_BASE);
    return response.data;
  },

  /**
   * ID로 Task 조회
   */
  getTaskById: async (id: number): Promise<Task> => {
    const response = await axios.get<Task>(`${API_BASE}/${id}`);
    return response.data;
  },

  /**
   * 카테고리별 Task 조회
   */
  getTasksByCategoryId: async (categoryId: number): Promise<Task[]> => {
    const response = await axios.get<Task[]>(`${API_BASE}/category/${categoryId}`);
    return response.data;
  },

  /**
   * 진행 상태별 Task 조회
   */
  getTasksByProgress: async (progress: string): Promise<Task[]> => {
    const response = await axios.get<Task[]>(`${API_BASE}/progress/${progress}`);
    return response.data;
  },

  /**
   * 특정 월의 Task 조회
   */
  getTasksByMonth: async (yearMonth: string): Promise<Task[]> => {
    const response = await axios.get<Task[]>(`${API_BASE}/month/${yearMonth}`);
    return response.data;
  },

  /**
   * 마감일이 가까운 Task 조회
   */
  getUpcomingDeadlines: async (days: number = 7): Promise<Task[]> => {
    const response = await axios.get<Task[]>(`${API_BASE}/deadlines`, { params: { days } });
    return response.data;
  },

  /**
   * 기한 초과 Task 조회
   */
  getOverdueTasks: async (): Promise<Task[]> => {
    const response = await axios.get<Task[]>(`${API_BASE}/overdue`);
    return response.data;
  },

  /**
   * 오늘 집중해야 할 Task
   */
  getFocusTasks: async (): Promise<Task[]> => {
    const response = await axios.get<Task[]>(`${API_BASE}/focus`);
    return response.data;
  },

  /**
   * 바로 시작 가능한 Task
   */
  getReadyTasks: async (): Promise<Task[]> => {
    const response = await axios.get<Task[]>(`${API_BASE}/ready`);
    return response.data;
  },

  /**
   * 이번 주 완료한 Task
   */
  getCompletedThisWeek: async (): Promise<Task[]> => {
    const response = await axios.get<Task[]>(`${API_BASE}/completed-this-week`);
    return response.data;
  },

  // ========== 생성/수정/삭제 API ==========

  /**
   * Task 생성
   */
  createTask: async (data: CreateTaskRequest): Promise<Task> => {
    const response = await axios.post<Task>(API_BASE, data);
    return response.data;
  },

  /**
   * Task 수정
   */
  updateTask: async (id: number, data: UpdateTaskRequest): Promise<Task> => {
    const response = await axios.put<Task>(`${API_BASE}/${id}`, data);
    return response.data;
  },

  /**
   * 진행 상태 변경
   */
  changeProgress: async (id: number, data: ChangeProgressRequest): Promise<Task> => {
    const response = await axios.patch<Task>(`${API_BASE}/${id}/progress`, data);
    return response.data;
  },

  /**
   * Task 소프트 삭제
   */
  deleteTask: async (id: number): Promise<void> => {
    await axios.delete(`${API_BASE}/${id}`);
  },

  /**
   * Task 하드 삭제
   */
  hardDeleteTask: async (id: number): Promise<void> => {
    await axios.delete(`${API_BASE}/${id}/hard`);
  },

  // ========== 태그 API ==========

  /**
   * 태그 추가
   */
  addTag: async (id: number, data: AddTagRequest): Promise<Task> => {
    const response = await axios.post<Task>(`${API_BASE}/${id}/tags`, data);
    return response.data;
  },

  /**
   * 태그 제거
   */
  removeTag: async (id: number, tag: string): Promise<Task> => {
    const response = await axios.delete<Task>(`${API_BASE}/${id}/tags/${tag}`);
    return response.data;
  },

  // ========== 담당자 API ==========

  /**
   * 담당자 추가
   */
  addAssignee: async (id: number, data: AddAssigneeRequest): Promise<Task> => {
    const response = await axios.post<Task>(`${API_BASE}/${id}/assignees`, data);
    return response.data;
  },

  /**
   * 담당자 제거
   */
  removeAssignee: async (id: number, name: string): Promise<Task> => {
    const response = await axios.delete<Task>(`${API_BASE}/${id}/assignees/${name}`);
    return response.data;
  },

  // ========== 링크 API ==========

  /**
   * 링크 추가
   */
  addLink: async (id: number, data: AddLinkRequest): Promise<Task> => {
    const response = await axios.post<Task>(`${API_BASE}/${id}/links`, data);
    return response.data;
  },

  /**
   * 링크 제거
   */
  removeLink: async (id: number, linkId: number): Promise<Task> => {
    const response = await axios.delete<Task>(`${API_BASE}/${id}/links/${linkId}`);
    return response.data;
  },

  // ========== 의존성 API ==========

  /**
   * 의존성 추가
   */
  addDependency: async (id: number, data: AddDependencyRequest): Promise<Task> => {
    const response = await axios.post<Task>(`${API_BASE}/${id}/dependencies`, data);
    return response.data;
  },

  /**
   * 의존성 제거
   */
  removeDependency: async (id: number, dependencyId: number): Promise<Task> => {
    const response = await axios.delete<Task>(`${API_BASE}/${id}/dependencies/${dependencyId}`);
    return response.data;
  },

  // ========== 부모 Task API ==========

  /**
   * 부모 Task 설정
   */
  setParent: async (id: number, data: SetParentRequest): Promise<Task> => {
    const response = await axios.post<Task>(`${API_BASE}/${id}/parent`, data);
    return response.data;
  },

  /**
   * 부모 Task 제거
   */
  removeParent: async (id: number): Promise<Task> => {
    const response = await axios.delete<Task>(`${API_BASE}/${id}/parent`);
    return response.data;
  },
};
