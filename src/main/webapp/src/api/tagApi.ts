import axios from 'axios';

const API_BASE = '/api/tags';

export interface Tag {
  id: number;
  name: string;
  createdAt: string;
}

export interface CreateTagRequest {
  name: string;
}

export interface UpdateTagRequest {
  name: string;
}

export interface TagListResponse {
  tags: Tag[];
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T | null;
  error: any | null;
  timestamp: number;
}

export const tagApi = {
  /**
   * 모든 태그 조회
   */
  getAllTags: async (): Promise<Tag[]> => {
    const response = await axios.get<ApiResponse<TagListResponse>>(API_BASE);
    return response.data.data?.tags || [];
  },

  /**
   * ID로 태그 조회
   */
  getTagById: async (id: number): Promise<Tag | null> => {
    const response = await axios.get<ApiResponse<Tag>>(`${API_BASE}/${id}`);
    return response.data.data;
  },

  /**
   * 이름으로 태그 조회
   */
  getTagByName: async (name: string): Promise<Tag | null> => {
    const response = await axios.get<ApiResponse<Tag>>(`${API_BASE}/name/${name}`);
    return response.data.data;
  },

  /**
   * 태그 검색 (키워드 포함)
   */
  searchTags: async (keyword: string): Promise<Tag[]> => {
    const response = await axios.get<ApiResponse<Tag[]>>(`${API_BASE}/search`, {
      params: { keyword }
    });
    return response.data.data || [];
  },

  /**
   * 태그 생성
   */
  createTag: async (data: CreateTagRequest): Promise<Tag> => {
    const response = await axios.post<ApiResponse<Tag>>(API_BASE, data);
    if (!response.data.data) throw new Error(response.data.message);
    return response.data.data;
  },

  /**
   * 태그 생성 또는 조회 (없으면 생성)
   */
  findOrCreateTag: async (data: CreateTagRequest): Promise<Tag> => {
    const response = await axios.post<ApiResponse<Tag>>(`${API_BASE}/find-or-create`, data);
    if (!response.data.data) throw new Error(response.data.message);
    return response.data.data;
  },

  /**
   * 태그 수정
   */
  updateTag: async (id: number, data: UpdateTagRequest): Promise<Tag> => {
    const response = await axios.put<ApiResponse<Tag>>(`${API_BASE}/${id}`, data);
    if (!response.data.data) throw new Error(response.data.message);
    return response.data.data;
  },

  /**
   * 태그 삭제
   */
  deleteTag: async (id: number): Promise<void> => {
    await axios.delete<ApiResponse<void>>(`${API_BASE}/${id}`);
  },

  /**
   * 태그 존재 여부 확인
   */
  existsByName: async (name: string): Promise<boolean> => {
    const response = await axios.get<ApiResponse<{ exists: boolean }>>(`${API_BASE}/exists`, {
      params: { name }
    });
    return response.data.data?.exists || false;
  },
};
