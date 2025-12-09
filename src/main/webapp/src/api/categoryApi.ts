import axios from 'axios';

const API_BASE = '/api/categories';

export interface Category {
  id: number;
  name: string;
  description: string | null;
  label: CategoryLabel | null;
  createdAt: string;
  updatedAt: string;
}

export interface CategoryLabel {
  name: string;
  displayName: string;
  colorCode: string;
}

export interface CreateCategoryRequest {
  name: string;
  description?: string | null;
  label?: string | null;
}

export interface UpdateCategoryRequest {
  name?: string | null;
  description?: string | null;
  label?: string | null;
}

export interface CategoryListResponse {
  categories: Category[];
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T | null;
  error: any | null;
  timestamp: number;
}

export const categoryApi = {
  /**
   * 모든 카테고리 조회
   */
  getAllCategories: async (): Promise<Category[]> => {
    const response = await axios.get<ApiResponse<CategoryListResponse>>(API_BASE);
    return response.data.data?.categories || [];
  },

  /**
   * ID로 카테고리 조회
   */
  getCategoryById: async (id: number): Promise<Category | null> => {
    const response = await axios.get<ApiResponse<Category>>(`${API_BASE}/${id}`);
    return response.data.data;
  },

  /**
   * 카테고리 생성
   */
  createCategory: async (data: CreateCategoryRequest): Promise<Category> => {
    const response = await axios.post<ApiResponse<Category>>(API_BASE, data);
    if (!response.data.data) throw new Error(response.data.message);
    return response.data.data;
  },

  /**
   * 카테고리 수정
   */
  updateCategory: async (id: number, data: UpdateCategoryRequest): Promise<Category> => {
    const response = await axios.put<ApiResponse<Category>>(`${API_BASE}/${id}`, data);
    if (!response.data.data) throw new Error(response.data.message);
    return response.data.data;
  },

  /**
   * 카테고리 삭제
   */
  deleteCategory: async (id: number): Promise<void> => {
    await axios.delete<ApiResponse<void>>(`${API_BASE}/${id}`);
  },

  /**
   * 모든 카테고리 레이블 조회
   */
  getAllLabels: async (): Promise<CategoryLabel[]> => {
    const response = await axios.get<ApiResponse<CategoryLabel[]>>(`${API_BASE}/labels`);
    return response.data.data || [];
  },
};
