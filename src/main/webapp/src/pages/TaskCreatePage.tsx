import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { taskApi } from '../api/taskApi';
import { categoryApi, Category } from '../api/categoryApi';
import { assigneeApi, Assignee } from '../api/assigneeApi';
import { tagApi, Tag } from '../api/tagApi';
import type { Task, CreateTaskRequest, TaskProgress, TaskLifecycle, CreateLinkRequest } from '../types/task';
import './TaskCreatePage.css';

// 디바운스 훅
function useDebounce<T>(value: T, delay: number): T {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);

  return debouncedValue;
}

function TaskCreatePage() {
  const navigate = useNavigate();
  
  const [form, setForm] = useState<CreateTaskRequest>({
    title: '',
    description: '',
    progress: 'todo',
    lifecycle: 'active',
    tags: [],
    assignees: [],
    links: [],
  });
  
  // 선택 가능한 옵션 목록
  const [categories, setCategories] = useState<Category[]>([]);
  const [assignees, setAssignees] = useState<Assignee[]>([]);
  const [tags, setTags] = useState<Tag[]>([]);
  const [filteredTags, setFilteredTags] = useState<Tag[]>([]);
  const [showTagDropdown, setShowTagDropdown] = useState(false);
  
  // 입력 필드 상태
  const [tagInput, setTagInput] = useState('');
  const [assigneeInput, setAssigneeInput] = useState('');
  
  // 링크 입력 상태
  const [linkName, setLinkName] = useState('');
  const [linkUrl, setLinkUrl] = useState('');
  const [linkDescription, setLinkDescription] = useState('');
  
  // Parent Tasks 검색 상태 (여러 개 선택 가능)
  const [parentSearchKeyword, setParentSearchKeyword] = useState('');
  const [parentSearchResults, setParentSearchResults] = useState<Task[]>([]);
  const [selectedParents, setSelectedParents] = useState<{ id: number; title: string }[]>([]);
  const [showParentDropdown, setShowParentDropdown] = useState(false);
  
  // Dependencies 검색 상태
  const [depSearchKeyword, setDepSearchKeyword] = useState('');
  const [depSearchResults, setDepSearchResults] = useState<Task[]>([]);
  const [selectedDependencies, setSelectedDependencies] = useState<{ id: number; title: string }[]>([]);
  const [showDepDropdown, setShowDepDropdown] = useState(false);
  
  // 로딩/에러 상태
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // 디바운스된 검색어
  const debouncedParentKeyword = useDebounce(parentSearchKeyword, 300);
  const debouncedDepKeyword = useDebounce(depSearchKeyword, 300);

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

  // Parent Task 검색
  useEffect(() => {
    const searchParent = async () => {
      if (debouncedParentKeyword.trim().length < 1) {
        setParentSearchResults([]);
        return;
      }
      try {
        const results = await taskApi.searchTasks(debouncedParentKeyword, 10);
        // 이미 선택된 상위 Task는 제외
        const filteredResults = results.filter(
          task => !selectedParents.some(parent => parent.id === task.id)
        );
        setParentSearchResults(filteredResults);
      } catch (err) {
        console.error('Parent search failed:', err);
      }
    };
    searchParent();
  }, [debouncedParentKeyword, selectedParents]);

  // Dependencies 검색
  useEffect(() => {
    const searchDep = async () => {
      if (debouncedDepKeyword.trim().length < 1) {
        setDepSearchResults([]);
        return;
      }
      try {
        const results = await taskApi.searchTasks(debouncedDepKeyword, 10);
        // 이미 선택된 의존성은 제외
        const filteredResults = results.filter(
          task => !selectedDependencies.some(dep => dep.id === task.id)
        );
        setDepSearchResults(filteredResults);
      } catch (err) {
        console.error('Dependency search failed:', err);
      }
    };
    searchDep();
  }, [debouncedDepKeyword, selectedDependencies]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.title.trim()) {
      setError('제목을 입력해주세요.');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      
      // Task 생성
      const createdTask = await taskApi.createTask(form);
      
      // Parent Tasks 설정 (여러 개)
      for (const parent of selectedParents) {
        await taskApi.addParent(createdTask.id, { parentTaskId: parent.id });
      }
      
      // Dependencies 설정
      for (const dep of selectedDependencies) {
        await taskApi.addDependency(createdTask.id, { dependencyTaskId: dep.id });
      }
      
      // 생성 완료 후 Task 목록으로 이동
      navigate('/tasks');
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

  // 링크 추가
  const handleAddLink = () => {
    if (!linkName.trim() || !linkUrl.trim()) {
      return;
    }
    
    const newLink: CreateLinkRequest = {
      name: linkName.trim(),
      url: linkUrl.trim(),
      description: linkDescription.trim() || undefined,
    };
    
    setForm({
      ...form,
      links: [...(form.links || []), newLink],
    });
    
    setLinkName('');
    setLinkUrl('');
    setLinkDescription('');
  };

  const handleRemoveLink = (index: number) => {
    setForm({
      ...form,
      links: form.links?.filter((_, i) => i !== index) || [],
    });
  };

  // Parent Task 추가
  const handleAddParent = (task: Task) => {
    if (!selectedParents.some(parent => parent.id === task.id)) {
      setSelectedParents([...selectedParents, { id: task.id, title: task.title }]);
    }
    setParentSearchKeyword('');
    setParentSearchResults([]);
    setShowParentDropdown(false);
  };

  const handleRemoveParent = (id: number) => {
    setSelectedParents(selectedParents.filter(parent => parent.id !== id));
  };

  // Dependency 추가
  const handleAddDependency = (task: Task) => {
    if (!selectedDependencies.some(dep => dep.id === task.id)) {
      setSelectedDependencies([...selectedDependencies, { id: task.id, title: task.title }]);
    }
    setDepSearchKeyword('');
    setDepSearchResults([]);
    setShowDepDropdown(false);
  };

  const handleRemoveDependency = (id: number) => {
    setSelectedDependencies(selectedDependencies.filter(dep => dep.id !== id));
  };

  return (
    <div className="task-create-page">
      <div className="page-header">
        <h1 className="page-title">새 Task 만들기</h1>
        <button className="btn btn-secondary" onClick={() => navigate('/tasks')}>
          ← 목록으로
        </button>
      </div>

      <form onSubmit={handleSubmit} className="task-form">
        {error && <div className="error-message">{error}</div>}

        {/* 기본 정보 섹션 */}
        <section className="form-section">
          <h2 className="section-title">기본 정보</h2>
          
          <div className="form-group">
            <label>제목 *</label>
            <input
              type="text"
              value={form.title}
              onChange={(e) => setForm({ ...form, title: e.target.value })}
              placeholder="Task 제목을 입력하세요"
              autoFocus
            />
          </div>

          <div className="form-group">
            <label>설명</label>
            <textarea
              value={form.description || ''}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
              placeholder="Task에 대한 상세 설명을 입력하세요"
              rows={4}
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
        </section>

        {/* 관계 설정 섹션 */}
        <section className="form-section">
          <h2 className="section-title">관계 설정</h2>
          
          {/* Parent Tasks (다중 선택) */}
          <div className="form-group">
            <label>상위 Task (여러 개 선택 가능)</label>
            <div className="search-input-wrapper">
              <input
                type="text"
                value={parentSearchKeyword}
                onChange={(e) => {
                  setParentSearchKeyword(e.target.value);
                  setShowParentDropdown(true);
                }}
                onFocus={() => setShowParentDropdown(true)}
                placeholder="상위 Task를 검색하세요..."
              />
              {showParentDropdown && parentSearchResults.length > 0 && (
                <div className="search-dropdown">
                  {parentSearchResults.map((task) => (
                    <div
                      key={task.id}
                      className="search-dropdown-item"
                      onClick={() => handleAddParent(task)}
                    >
                      <span className="task-title">{task.title}</span>
                      <span className="task-status">{task.progress}</span>
                    </div>
                  ))}
                </div>
              )}
            </div>
            {selectedParents.length > 0 && (
              <div className="selected-items-list">
                {selectedParents.map((parent) => (
                  <div key={parent.id} className="selected-item parent-item">
                    <span className="selected-item-text">👆 {parent.title}</span>
                    <button 
                      type="button" 
                      className="remove-btn"
                      onClick={() => handleRemoveParent(parent.id)}
                    >
                      ×
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Dependencies */}
          <div className="form-group">
            <label>선행 작업 (Dependencies)</label>
            <div className="search-input-wrapper">
              <input
                type="text"
                value={depSearchKeyword}
                onChange={(e) => {
                  setDepSearchKeyword(e.target.value);
                  setShowDepDropdown(true);
                }}
                onFocus={() => setShowDepDropdown(true)}
                placeholder="선행 작업을 검색하세요..."
              />
              {showDepDropdown && depSearchResults.length > 0 && (
                <div className="search-dropdown">
                  {depSearchResults.map((task) => (
                    <div
                      key={task.id}
                      className="search-dropdown-item"
                      onClick={() => handleAddDependency(task)}
                    >
                      <span className="task-title">{task.title}</span>
                      <span className="task-status">{task.progress}</span>
                    </div>
                  ))}
                </div>
              )}
            </div>
            {selectedDependencies.length > 0 && (
              <div className="selected-items-list">
                {selectedDependencies.map((dep) => (
                  <div key={dep.id} className="selected-item">
                    <span className="selected-item-text">⛓ {dep.title}</span>
                    <button 
                      type="button" 
                      className="remove-btn"
                      onClick={() => handleRemoveDependency(dep.id)}
                    >
                      ×
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        </section>

        {/* 링크 섹션 */}
        <section className="form-section">
          <h2 className="section-title">링크</h2>
          
          <div className="link-input-group">
            <div className="form-row">
              <div className="form-group">
                <label>링크 이름</label>
                <input
                  type="text"
                  value={linkName}
                  onChange={(e) => setLinkName(e.target.value)}
                  placeholder="링크 이름"
                />
              </div>
              <div className="form-group flex-2">
                <label>URL</label>
                <input
                  type="url"
                  value={linkUrl}
                  onChange={(e) => setLinkUrl(e.target.value)}
                  placeholder="https://..."
                />
              </div>
            </div>
            <div className="form-row">
              <div className="form-group flex-1">
                <label>설명 (선택)</label>
                <input
                  type="text"
                  value={linkDescription}
                  onChange={(e) => setLinkDescription(e.target.value)}
                  placeholder="링크에 대한 설명"
                />
              </div>
              <button 
                type="button" 
                className="btn btn-secondary add-link-btn"
                onClick={handleAddLink}
                disabled={!linkName.trim() || !linkUrl.trim()}
              >
                + 링크 추가
              </button>
            </div>
          </div>
          
          {form.links && form.links.length > 0 && (
            <div className="links-list">
              {form.links.map((link, index) => (
                <div key={index} className="link-item">
                  <div className="link-info">
                    <a href={link.url} target="_blank" rel="noopener noreferrer">
                      🔗 {link.name}
                    </a>
                    {link.description && (
                      <span className="link-description">{link.description}</span>
                    )}
                  </div>
                  <button 
                    type="button" 
                    className="remove-btn"
                    onClick={() => handleRemoveLink(index)}
                  >
                    ×
                  </button>
                </div>
              ))}
            </div>
          )}
        </section>

        {/* 태그 & 담당자 섹션 */}
        <section className="form-section">
          <h2 className="section-title">태그 & 담당자</h2>
          
          {/* 태그 */}
          <div className="form-group">
            <label>태그</label>
            <div className="search-input-wrapper">
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
                <div className="search-dropdown">
                  {filteredTags.map((tag) => (
                    <div
                      key={tag.id}
                      className="search-dropdown-item"
                      onClick={() => handleAddTag(tag.name)}
                    >
                      <span className="tag-name">🏷️ {tag.name}</span>
                    </div>
                  ))}
                  {tagInput.trim() && !tags.find(t => t.name.toLowerCase() === tagInput.toLowerCase()) && (
                    <div
                      className="search-dropdown-item new-item"
                      onClick={() => handleAddTag()}
                    >
                      <span className="tag-name">+ "새 태그: {tagInput}" 만들기</span>
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
                  className="assignee-option"
                  onClick={() => handleAddAssignee(assignee.name)}
                >
                  <div className="assignee-avatar">{assignee.name.charAt(0).toUpperCase()}</div>
                  <span>{assignee.name}</span>
                </button>
              ))}
            </div>
            {form.assignees && form.assignees.length > 0 && (
              <div className="assignee-list selected">
                <div className="selected-label">선택된 담당자:</div>
                {form.assignees.map((name) => (
                  <span key={name} className="assignee-item">
                    👤 {name}
                    <button type="button" onClick={() => handleRemoveAssignee(name)}>×</button>
                  </span>
                ))}
              </div>
            )}
          </div>
        </section>

        {/* 제출 버튼 */}
        <div className="form-actions">
          <button type="button" className="btn btn-secondary" onClick={() => navigate('/tasks')}>
            취소
          </button>
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? '생성 중...' : 'Task 생성'}
          </button>
        </div>
      </form>
    </div>
  );
}

export default TaskCreatePage;
