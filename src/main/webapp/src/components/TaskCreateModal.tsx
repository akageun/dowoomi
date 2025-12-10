import { useState, useEffect } from 'react';
import { categoryApi, Category } from '../api/categoryApi';
import { assigneeApi, Assignee } from '../api/assigneeApi';
import { tagApi, Tag } from '../api/tagApi';
import type { CreateTaskRequest, TaskProgress, TaskLifecycle } from '../types/task';
import './TaskCreateModal.css';

interface TaskCreateModalProps {
  onClose: () => void;
  onSubmit: (data: CreateTaskRequest) => Promise<void>;
}

function TaskCreateModal({ onClose, onSubmit }: TaskCreateModalProps) {
  const [form, setForm] = useState<CreateTaskRequest>({
    title: '',
    description: '',
    progress: 'todo',
    lifecycle: 'active',
    tags: [],
    assignees: [],
    links: [],
  });
  const [tagInput, setTagInput] = useState('');
  const [assigneeInput, setAssigneeInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  
  // 선택 가능한 옵션 목록
  const [categories, setCategories] = useState<Category[]>([]);
  const [assignees, setAssignees] = useState<Assignee[]>([]);
  const [tags, setTags] = useState<Tag[]>([]);
  const [filteredTags, setFilteredTags] = useState<Tag[]>([]);
  const [showTagDropdown, setShowTagDropdown] = useState(false);

  // 초기 데이터 로드
  useEffect(() => {
    const loadInitialData = async () => {
      try {
        const [categoriesData, assigneesData, tagsData] = await Promise.all([
          categoryApi.getAllCategories(),
          assigneeApi.getAllAssignees(),
          tagApi.getAllTags(),
        ]);
        setCategories(categoriesData);
        setAssignees(assigneesData);
        setTags(tagsData);
      } catch (err) {
        console.error('Failed to load initial data:', err);
      }
    };
    loadInitialData();
  }, []);

  // 태그 검색 필터링
  useEffect(() => {
    if (tagInput.trim()) {
      const filtered = tags.filter(tag => 
        tag.name.toLowerCase().includes(tagInput.toLowerCase()) &&
        !form.tags?.includes(tag.name)
      );
      setFilteredTags(filtered);
    } else {
      setFilteredTags([]);
    }
  }, [tagInput, tags, form.tags]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.title.trim()) {
      setError('제목을 입력해주세요.');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      await onSubmit(form);
    } catch (err) {
      setError('Task 생성에 실패했습니다.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleAddTag = (tagName?: string) => {
    const tag = tagName || tagInput.trim();
    if (tag && !form.tags?.includes(tag)) {
      setForm({
        ...form,
        tags: [...(form.tags || []), tag],
      });
      setTagInput('');
      setShowTagDropdown(false);
      setFilteredTags([]);
    }
  };

  const handleRemoveTag = (tag: string) => {
    setForm({
      ...form,
      tags: form.tags?.filter((t) => t !== tag) || [],
    });
  };

  const handleAddAssignee = (assigneeName?: string) => {
    const name = assigneeName || assigneeInput.trim();
    if (name && !form.assignees?.includes(name)) {
      setForm({
        ...form,
        assignees: [...(form.assignees || []), name],
      });
      setAssigneeInput('');
    }
  };

  const handleRemoveAssignee = (name: string) => {
    setForm({
      ...form,
      assignees: form.assignees?.filter((a) => a !== name) || [],
    });
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>새 Task 만들기</h2>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>

        <form onSubmit={handleSubmit}>
          {error && <div className="error">{error}</div>}

          <div className="form-group">
            <label>제목 *</label>
            <input
              type="text"
              value={form.title}
              onChange={(e) => setForm({ ...form, title: e.target.value })}
              placeholder="Task 제목"
              autoFocus
            />
          </div>

          <div className="form-group">
            <label>설명</label>
            <textarea
              value={form.description || ''}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
              placeholder="Task 설명"
              rows={3}
            />
          </div>

          <div className="form-group">
            <label>카테고리</label>
            <select
              value={form.categoryId || ''}
              onChange={(e) => setForm({ ...form, categoryId: e.target.value ? Number(e.target.value) : undefined })}
            >
              <option value="">선택 안함</option>
              {categories.map((category) => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>진행 상태</label>
              <select
                value={form.progress}
                onChange={(e) => setForm({ ...form, progress: e.target.value as TaskProgress })}
              >
                <option value="todo">할 일</option>
                <option value="in_progress">진행 중</option>
                <option value="done">완료</option>
              </select>
            </div>

            <div className="form-group">
              <label>생명주기</label>
              <select
                value={form.lifecycle}
                onChange={(e) => setForm({ ...form, lifecycle: e.target.value as TaskLifecycle })}
              >
                <option value="active">활성</option>
                <option value="draft">초안</option>
              </select>
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>시작일</label>
              <input
                type="date"
                value={form.startDate || ''}
                onChange={(e) => setForm({ ...form, startDate: e.target.value || undefined })}
              />
            </div>

            <div className="form-group">
              <label>종료일</label>
              <input
                type="date"
                value={form.endDate || ''}
                onChange={(e) => setForm({ ...form, endDate: e.target.value || undefined })}
              />
            </div>
          </div>

          {/* 태그 */}
          <div className="form-group">
            <label>태그</label>
            <div className="tag-input-wrapper">
              <input
                type="text"
                value={tagInput}
                onChange={(e) => {
                  setTagInput(e.target.value);
                  setShowTagDropdown(true);
                }}
                onFocus={() => setShowTagDropdown(true)}
                placeholder="태그 검색 또는 새 태그 입력"
                onKeyDown={(e) => {
                  if (e.key === 'Enter') {
                    e.preventDefault();
                    handleAddTag();
                  }
                }}
              />
              {showTagDropdown && (filteredTags.length > 0 || tagInput.trim()) && (
                <div className="tag-dropdown">
                  {filteredTags.map((tag) => (
                    <div
                      key={tag.id}
                      className="tag-dropdown-item"
                      onClick={() => handleAddTag(tag.name)}
                    >
                      🏷️ {tag.name}
                    </div>
                  ))}
                  {tagInput.trim() && !tags.find(t => t.name.toLowerCase() === tagInput.toLowerCase()) && (
                    <div
                      className="tag-dropdown-item new-item"
                      onClick={() => handleAddTag()}
                    >
                      + "새 태그: {tagInput}" 만들기
                    </div>
                  )}
                </div>
              )}
            </div>
            {form.tags && form.tags.length > 0 && (
              <div className="tag-list">
                {form.tags.map((tag) => (
                  <span key={tag} className="tag">
                    {tag}
                    <button type="button" onClick={() => handleRemoveTag(tag)}>×</button>
                  </span>
                ))}
              </div>
            )}
          </div>

          {/* 담당자 */}
          <div className="form-group">
            <label>담당자</label>
            <div className="assignee-selector">
              {assignees.filter(a => !form.assignees?.includes(a.name)).map((assignee) => (
                <button
                  key={assignee.id}
                  type="button"
                  className="assignee-option-btn"
                  onClick={() => handleAddAssignee(assignee.name)}
                >
                  <div className="assignee-avatar-small">{assignee.name.charAt(0).toUpperCase()}</div>
                  <span>{assignee.name}</span>
                </button>
              ))}
            </div>
            {form.assignees && form.assignees.length > 0 && (
              <div className="assignee-list">
                {form.assignees.map((name) => (
                  <span key={name} className="assignee-item">
                    👤 {name}
                    <button type="button" onClick={() => handleRemoveAssignee(name)}>×</button>
                  </span>
                ))}
              </div>
            )}
          </div>

          <div className="modal-actions">
            <button type="button" className="btn btn-secondary" onClick={onClose}>
              취소
            </button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? '생성 중...' : '생성'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default TaskCreateModal;
