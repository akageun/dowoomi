import { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { taskApi } from '../api/taskApi';
import type { Task, TaskProgress } from '../types/task';
import { progressLabels } from '../types/task';
import GanttChart from '../components/GanttChart';
import './TaskPage.css';

type ViewMode = 'list' | 'gantt';

function TaskPage() {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filter, setFilter] = useState<'all' | TaskProgress>('all');
  const [viewMode, setViewMode] = useState<ViewMode>('list');

  const fetchTasks = useCallback(async () => {
    try {
      setLoading(true);
      const data = await taskApi.getAllTasks();
      setTasks(data);
      setError(null);
    } catch (err) {
      setError('Task를 불러오는데 실패했습니다.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchTasks();
  }, [fetchTasks]);

  const handleProgressChange = async (taskId: number, progress: TaskProgress) => {
    try {
      await taskApi.changeProgress(taskId, { progress });
      await fetchTasks();
    } catch (err) {
      console.error('Failed to change progress:', err);
    }
  };

  const handleDelete = async (taskId: number) => {
    if (!confirm('정말 삭제하시겠습니까?')) return;
    try {
      await taskApi.deleteTask(taskId);
      await fetchTasks();
    } catch (err) {
      console.error('Failed to delete task:', err);
    }
  };

  const filteredTasks = filter === 'all' 
    ? tasks 
    : tasks.filter(task => task.progress === filter);

  const getProgressBadgeClass = (progress: TaskProgress) => {
    switch (progress) {
      case 'todo': return 'badge badge-todo';
      case 'in_progress': return 'badge badge-in-progress';
      case 'done': return 'badge badge-done';
    }
  };

  const getLifecycleClass = (lifecycle: string) => {
    switch (lifecycle) {
      case 'draft': return 'lifecycle-draft';
      case 'deleted': return 'lifecycle-deleted';
      default: return '';
    }
  };

  if (loading) {
    return <div className="loading">로딩 중...</div>;
  }

  return (
    <div className="task-page">
      <div className="task-header">
        <h1 className="page-title">Tasks</h1>
        <div className="header-actions">
          <div className="view-toggle">
            <button 
              className={`view-btn ${viewMode === 'list' ? 'active' : ''}`}
              onClick={() => setViewMode('list')}
              title="리스트 뷰"
            >
              📋 리스트
            </button>
            <button 
              className={`view-btn ${viewMode === 'gantt' ? 'active' : ''}`}
              onClick={() => setViewMode('gantt')}
              title="간트 차트"
            >
              📊 간트
            </button>
          </div>
          <Link to="/tasks/new" className="btn btn-primary">
            + 새 Task
          </Link>
        </div>
      </div>

      {error && <div className="error">{error}</div>}

      {viewMode === 'gantt' ? (
        <GanttChart tasks={tasks} onTaskClick={(taskId: number) => window.location.href = `/tasks/${taskId}`} />
      ) : (
        <>
          <div className="task-filters">
            <button 
              className={`filter-btn ${filter === 'all' ? 'active' : ''}`}
              onClick={() => setFilter('all')}
            >
              전체 ({tasks.length})
            </button>
            <button 
              className={`filter-btn ${filter === 'todo' ? 'active' : ''}`}
              onClick={() => setFilter('todo')}
            >
              할 일 ({tasks.filter(t => t.progress === 'todo').length})
            </button>
            <button 
              className={`filter-btn ${filter === 'in_progress' ? 'active' : ''}`}
              onClick={() => setFilter('in_progress')}
            >
              진행 중 ({tasks.filter(t => t.progress === 'in_progress').length})
            </button>
            <button 
              className={`filter-btn ${filter === 'done' ? 'active' : ''}`}
              onClick={() => setFilter('done')}
            >
              완료 ({tasks.filter(t => t.progress === 'done').length})
            </button>
          </div>

          <div className="task-list">
            {filteredTasks.length === 0 ? (
              <div className="no-tasks">표시할 Task가 없습니다.</div>
            ) : (
              filteredTasks.map(task => (
                <div key={task.id} className={`task-card ${getLifecycleClass(task.lifecycle)}`}>
                  <div className="task-card-header">
                    <Link to={`/tasks/${task.id}`} className="task-title">
                      {task.title}
                    </Link>
                    <span className={getProgressBadgeClass(task.progress)}>
                      {progressLabels[task.progress]}
                    </span>
                  </div>

                  {task.description && (
                    <p className="task-description">{task.description}</p>
                  )}

                  <div className="task-chips">
                    {task.categoryName && (
                      <span className="chip chip-category">📁 {task.categoryName}</span>
                    )}
                    {task.parents && task.parents.length > 0 && (
                      <span className="chip chip-parent">👆 상위 {task.parents.length}개</span>
                    )}
                    {task.dependencies && task.dependencies.length > 0 && (
                      <span className="chip chip-dependency">🔗 {task.dependencies.length}개 의존성</span>
                    )}
                    {task.assignees.length > 0 && (
                      <span className="chip chip-assignee">👤 {task.assignees.length}명</span>
                    )}
                  </div>

                  <div className="task-meta">
                    {task.startDate && (
                      <span className="meta-item">
                        <span className="meta-label">시작</span>
                        <span className="meta-value">{task.startDate}</span>
                      </span>
                    )}
                    {task.endDate && (
                      <span className="meta-item">
                        <span className="meta-label">종료</span>
                        <span className="meta-value">{task.endDate}</span>
                      </span>
                    )}

                  </div>

                  {task.tags.length > 0 && (
                    <div className="task-tags">
                      {task.tags.map(tag => (
                        <span key={tag} className="tag">{tag}</span>
                      ))}
                    </div>
                  )}

                  {task.assignees.length > 0 && (
                    <div className="task-assignees">
                      {task.assignees.map(a => (
                        <span key={a.id} className="assignee-badge">{a.name}</span>
                      ))}
                    </div>
                  )}

                  {task.links && task.links.length > 0 && (
                    <div className="task-links">
                      {task.links.map(link => (
                        <a 
                          key={link.id} 
                          href={link.url} 
                          target="_blank" 
                          rel="noopener noreferrer"
                          className="link-item"
                        >
                          🔗 {link.name}
                        </a>
                      ))}
                    </div>
                  )}

                  <div className="task-actions">
                    <select 
                      value={task.progress}
                      onChange={(e) => handleProgressChange(task.id, e.target.value as TaskProgress)}
                      className="progress-select"
                    >
                      <option value="todo">할 일</option>
                      <option value="in_progress">진행 중</option>
                      <option value="done">완료</option>
                    </select>
                    <Link to={`/tasks/${task.id}`} className="btn btn-secondary">
                      상세보기
                    </Link>
                    <button 
                      className="btn btn-danger"
                      onClick={() => handleDelete(task.id)}
                    >
                      삭제
                    </button>
                  </div>
                </div>
              ))
            )}
          </div>
        </>
      )}

    </div>
  );
}

export default TaskPage;
