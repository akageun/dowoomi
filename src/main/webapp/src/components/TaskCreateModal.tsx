import { useState } from 'react';
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

  const handleAddTag = () => {
    if (tagInput.trim() && !form.tags?.includes(tagInput.trim())) {
      setForm({
        ...form,
        tags: [...(form.tags || []), tagInput.trim()],
      });
      setTagInput('');
    }
  };

  const handleRemoveTag = (tag: string) => {
    setForm({
      ...form,
      tags: form.tags?.filter((t) => t !== tag) || [],
    });
  };

  const handleAddAssignee = () => {
    if (assigneeInput.trim() && !form.assignees?.includes(assigneeInput.trim())) {
      setForm({
        ...form,
        assignees: [...(form.assignees || []), assigneeInput.trim()],
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
                onChange={(e) => setTagInput(e.target.value)}
                placeholder="태그 입력 후 Enter"
                onKeyDown={(e) => {
                  if (e.key === 'Enter') {
                    e.preventDefault();
                    handleAddTag();
                  }
                }}
              />
              <button type="button" className="btn btn-secondary" onClick={handleAddTag}>
                추가
              </button>
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
            <div className="tag-input-wrapper">
              <input
                type="text"
                value={assigneeInput}
                onChange={(e) => setAssigneeInput(e.target.value)}
                placeholder="담당자 이름 입력 후 Enter"
                onKeyDown={(e) => {
                  if (e.key === 'Enter') {
                    e.preventDefault();
                    handleAddAssignee();
                  }
                }}
              />
              <button type="button" className="btn btn-secondary" onClick={handleAddAssignee}>
                추가
              </button>
            </div>
            {form.assignees && form.assignees.length > 0 && (
              <div className="assignee-list">
                {form.assignees.map((name) => (
                  <span key={name} className="assignee-item">
                    {name}
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
