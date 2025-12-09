import axios from 'axios';

const API_BASE = '/api/assignees';

export interface Assignee {
  id: number;
  name: string;
  memo: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateAssigneeRequest {
  name: string;
  memo?: string | null;
}

export interface UpdateAssigneeRequest {
  name?: string | null;
  memo?: string | null;
}

export interface AssigneeListResponse {
  assignees: Assignee[];
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T | null;
  error: any | null;
  timestamp: number;
}

export const assigneeApi = {
  /**
   * 모든 담당자 조회
   */
  getAllAssignees: async (): Promise<Assignee[]> => {
    const response = await axios.get<ApiResponse<AssigneeListResponse>>(API_BASE);
    return response.data.data?.assignees || [];
  },

  /**
   * ID로 담당자 조회
   */
  getAssigneeById: async (id: number): Promise<Assignee | null> => {
    const response = await axios.get<ApiResponse<Assignee>>(`${API_BASE}/${id}`);
    return response.data.data;
  },

  /**
   * 담당자 생성
   */
  createAssignee: async (data: CreateAssigneeRequest): Promise<Assignee> => {
    const response = await axios.post<ApiResponse<Assignee>>(API_BASE, data);
    if (!response.data.data) throw new Error(response.data.message);
    return response.data.data;
  },

  /**
   * 담당자 수정
   */
  updateAssignee: async (id: number, data: UpdateAssigneeRequest): Promise<Assignee> => {
    const response = await axios.put<ApiResponse<Assignee>>(`${API_BASE}/${id}`, data);
    if (!response.data.data) throw new Error(response.data.message);
    return response.data.data;
  },

  /**
   * 담당자 삭제
   */
  deleteAssignee: async (id: number): Promise<void> => {
    await axios.delete<ApiResponse<void>>(`${API_BASE}/${id}`);
  },

  /**
   * 담당자 검색 (이름으로)
   */
  searchAssignees: async (name: string): Promise<Assignee[]> => {
    const response = await axios.get<ApiResponse<Assignee[]>>(`${API_BASE}/search`, {
      params: { name }
    });
    return response.data.data || [];
  },
};
