import { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { taskApi } from '../api/taskApi';
import type { Task, TaskProgress, UpdateTaskRequest } from '../types/task';
import { progressLabels, lifecycleLabels } from '../types/task';
import './TaskDetailPage.css';

function TaskDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [task, setTask] = useState<Task | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [editForm, setEditForm] = useState<UpdateTaskRequest>({});
  
  // 태그/담당자 추가 상태
  const [newTag, setNewTag] = useState('');
  const [newAssignee, setNewAssignee] = useState('');
  const [newLinkName, setNewLinkName] = useState('');
  const [newLinkUrl, setNewLinkUrl] = useState('');

  const fetchTask = useCallback(async () => {
    if (!id) return;
    try {
      setLoading(true);
      const data = await taskApi.getTaskById(Number(id));
      setTask(data);
      setEditForm({
        title: data.title,
        description: data.description || '',
        categoryId: data.categoryId || undefined,
        progress: data.progress,
        lifecycle: data.lifecycle,
        startDate: data.startDate || undefined,
        endDate: data.endDate || undefined,
      });
      setError(null);
    } catch (err) {
      setError('Task를 불러오는데 실패했습니다.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchTask();
  }, [fetchTask]);

  const handleUpdate = async () => {
    if (!id || !task) return;
    try {
      await taskApi.updateTask(Number(id), editForm);
      await fetchTask();
      setIsEditing(false);
    } catch (err) {
      console.error('Failed to update task:', err);
    }
  };

  const handleProgressChange = async (progress: TaskProgress) => {
    if (!id) return;
    try {
      await taskApi.changeProgress(Number(id), { progress });
      await fetchTask();
    } catch (err) {
      console.error('Failed to change progress:', err);
    }
  };

  const handleDelete = async () => {
    if (!id) return;
    if (!confirm('정말 삭제하시겠습니까?')) return;
    try {
      await taskApi.deleteTask(Number(id));
      navigate('/tasks');
    } catch (err) {
      console.error('Failed to delete task:', err);
    }
  };

  const handleAddTag = async () => {
    if (!id || !newTag.trim()) return;
    try {
      await taskApi.addTag(Number(id), { tag: newTag.trim() });
      setNewTag('');
      await fetchTask();
    } catch (err) {
      console.error('Failed to add tag:', err);
    }
  };

  const handleRemoveTag = async (tag: string) => {
    if (!id) return;
    try {
      await taskApi.removeTag(Number(id), tag);
      await fetchTask();
    } catch (err) {
      console.error('Failed to remove tag:', err);
    }
  };

  const handleAddAssignee = async () => {
    if (!id || !newAssignee.trim()) return;
    try {
      await taskApi.addAssignee(Number(id), { name: newAssignee.trim() });
      setNewAssignee('');
      await fetchTask();
    } catch (err) {
      console.error('Failed to add assignee:', err);
    }
  };

  const handleRemoveAssignee = async (name: string) => {
    if (!id) return;
    try {
      await taskApi.removeAssignee(Number(id), name);
      await fetchTask();
    } catch (err) {
      console.error('Failed to remove assignee:', err);
    }
  };

  const handleAddLink = async () => {
    if (!id || !newLinkName.trim() || !newLinkUrl.trim()) return;
    try {
      await taskApi.addLink(Number(id), { 
        name: newLinkName.trim(), 
        url: newLinkUrl.trim() 
      });
      setNewLinkName('');
      setNewLinkUrl('');
      await fetchTask();
    } catch (err) {
      console.error('Failed to add link:', err);
    }
  };

  const handleRemoveLink = async (linkId: number) => {
    if (!id) return;
    try {
      await taskApi.removeLink(Number(id), linkId);
      await fetchTask();
    } catch (err) {
      console.error('Failed to remove link:', err);
    }
  };

  const getProgressBadgeClass = (progress: TaskProgress) => {
    switch (progress) {
      case 'todo': return 'badge badge-todo';
      case 'in_progress': return 'badge badge-in-progress';
      case 'done': return 'badge badge-done';
    }
  };

  if (loading) {
    return <div className="loading">로딩 중...</div>;
  }

  if (error || !task) {
    return (
      <div className="task-detail-page">
        <div className="error">{error || 'Task를 찾을 수 없습니다.'}</div>
        <Link to="/tasks" className="btn btn-secondary">목록으로</Link>
      </div>
    );
  }

  return (
    <div className="task-detail-page">
      <div className="task-detail-header">
        <Link to="/tasks" className="back-link">← 목록으로</Link>
        <div className="task-detail-actions">
          <button 
            className="btn btn-secondary" 
            onClick={() => setIsEditing(!isEditing)}
          >
            {isEditing ? '취소' : '수정'}
          </button>
          <button className="btn btn-danger" onClick={handleDelete}>
            삭제
          </button>
        </div>
      </div>

      <div className="task-detail-content">
        {isEditing ? (
          <div className="task-edit-form">
            <div className="form-group">
              <label>제목</label>
              <input
                type="text"
                value={editForm.title || ''}
                onChange={(e) => setEditForm({ ...editForm, title: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label>설명</label>
              <textarea
                value={editForm.description || ''}
                onChange={(e) => setEditForm({ ...editForm, description: e.target.value })}
                rows={4}
              />
            </div>
            <div className="form-row">
              <div className="form-group">
                <label>시작일</label>
                <input
                  type="date"
                  value={editForm.startDate || ''}
                  onChange={(e) => setEditForm({ ...editForm, startDate: e.target.value })}
                />
              </div>
              <div className="form-group">
                <label>종료일</label>
                <input
                  type="date"
                  value={editForm.endDate || ''}
                  onChange={(e) => setEditForm({ ...editForm, endDate: e.target.value })}
                />
              </div>
            </div>
            <button className="btn btn-primary" onClick={handleUpdate}>
              저장
            </button>
          </div>
        ) : (
          <>
            <div className="task-detail-title">
              <h1>{task.title}</h1>
              <span className={getProgressBadgeClass(task.progress)}>
                {progressLabels[task.progress]}
              </span>
            </div>

            {task.description && (
              <div className="task-detail-description">
                <p>{task.description}</p>
              </div>
            )}

            <div className="task-detail-info">
              <div className="info-item">
                <span className="info-label">상태</span>
                <select
                  value={task.progress}
                  onChange={(e) => handleProgressChange(e.target.value as TaskProgress)}
                  className="progress-select"
                >
                  <option value="todo">할 일</option>
                  <option value="in_progress">진행 중</option>
                  <option value="done">완료</option>
                </select>
              </div>
              <div className="info-item">
                <span className="info-label">생명주기</span>
                <span>{lifecycleLabels[task.lifecycle]}</span>
              </div>
              {task.categoryName && (
                <div className="info-item">
                  <span className="info-label">카테고리</span>
                  <span>{task.categoryName}</span>
                </div>
              )}
              {task.startDate && (
                <div className="info-item">
                  <span className="info-label">시작일</span>
                  <span>{task.startDate}</span>
                </div>
              )}
              {task.endDate && (
                <div className="info-item">
                  <span className="info-label">종료일</span>
                  <span>{task.endDate}</span>
                </div>
              )}
              <div className="info-item">
                <span className="info-label">생성일</span>
                <span>{task.createdAt}</span>
              </div>
              <div className="info-item">
                <span className="info-label">수정일</span>
                <span>{task.updatedAt}</span>
              </div>
            </div>
          </>
        )}

        {/* 태그 섹션 */}
        <div className="task-section">
          <h3>태그</h3>
          <div className="tag-list">
            {task.tags.map(tag => (
              <span key={tag} className="tag">
                {tag}
                <button 
                  className="tag-remove" 
                  onClick={() => handleRemoveTag(tag)}
                >×</button>
              </span>
            ))}
          </div>
          <div className="add-item-form">
            <input
              type="text"
              placeholder="새 태그"
              value={newTag}
              onChange={(e) => setNewTag(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleAddTag()}
            />
            <button className="btn btn-secondary" onClick={handleAddTag}>추가</button>
          </div>
        </div>

        {/* 담당자 섹션 */}
        <div className="task-section">
          <h3>담당자</h3>
          <div className="assignee-list">
            {task.assignees.map(assignee => (
              <span key={assignee.id} className="assignee-item">
                {assignee.name}
                <button 
                  className="assignee-remove" 
                  onClick={() => handleRemoveAssignee(assignee.name)}
                >×</button>
              </span>
            ))}
          </div>
          <div className="add-item-form">
            <input
              type="text"
              placeholder="담당자 이름"
              value={newAssignee}
              onChange={(e) => setNewAssignee(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleAddAssignee()}
            />
            <button className="btn btn-secondary" onClick={handleAddAssignee}>추가</button>
          </div>
        </div>

        {/* 링크 섹션 */}
        <div className="task-section">
          <h3>링크</h3>
          <div className="link-list">
            {task.links.map(link => (
              <div key={link.id} className="link-item">
                <a href={link.url} target="_blank" rel="noopener noreferrer">
                  🔗 {link.name}
                </a>
                {link.description && <span className="link-desc">{link.description}</span>}
                <button 
                  className="link-remove" 
                  onClick={() => handleRemoveLink(link.id)}
                >×</button>
              </div>
            ))}
          </div>
          <div className="add-link-form">
            <input
              type="text"
              placeholder="링크 이름"
              value={newLinkName}
              onChange={(e) => setNewLinkName(e.target.value)}
            />
            <input
              type="url"
              placeholder="URL"
              value={newLinkUrl}
              onChange={(e) => setNewLinkUrl(e.target.value)}
            />
            <button className="btn btn-secondary" onClick={handleAddLink}>추가</button>
          </div>
        </div>

        {/* 의존성 섹션 */}
        {task.dependencies.length > 0 && (
          <div className="task-section">
            <h3>선행 작업</h3>
            <div className="dependency-list">
              {task.dependencies.map(dep => (
                <Link key={dep.taskId} to={`/tasks/${dep.taskId}`} className="dependency-item">
                  🔗 {dep.taskTitle}
                </Link>
              ))}
            </div>
          </div>
        )}

        {/* 부모 Tasks (여러 개) */}
        {task.parents && task.parents.length > 0 && (
          <div className="task-section">
            <h3>상위 작업 ({task.parents.length}개)</h3>
            <div className="parent-list">
              {task.parents.map((parent) => (
                <Link key={parent.taskId} to={`/tasks/${parent.taskId}`} className="parent-item">
                  👆 {parent.taskTitle}
                </Link>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default TaskDetailPage;
