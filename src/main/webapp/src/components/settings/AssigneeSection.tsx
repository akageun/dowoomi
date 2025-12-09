import { useState, useEffect } from 'react';
import { assigneeApi, Assignee, CreateAssigneeRequest, UpdateAssigneeRequest } from '../../api/assigneeApi';
import './AssigneeSection.css';

function AssigneeSection() {
  const [assignees, setAssignees] = useState<Assignee[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [isCreating, setIsCreating] = useState(false);

  // Form state
  const [formData, setFormData] = useState<CreateAssigneeRequest>({
    name: '',
    memo: '',
  });

  useEffect(() => {
    fetchAssignees();
  }, []);

  const fetchAssignees = async () => {
    try {
      setLoading(true);
      const data = await assigneeApi.getAllAssignees();
      setAssignees(data);
      setError(null);
    } catch (err) {
      setError('담당자 목록을 불러오는데 실패했습니다.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.name.trim()) {
      alert('담당자 이름을 입력해주세요.');
      return;
    }

    try {
      await assigneeApi.createAssignee(formData);
      setFormData({ name: '', memo: '' });
      setIsCreating(false);
      await fetchAssignees();
    } catch (err: any) {
      alert(err.response?.data?.message || '담당자 생성에 실패했습니다.');
    }
  };

  const handleUpdate = async (id: number) => {
    try {
      const updateData: UpdateAssigneeRequest = {
        name: formData.name,
        memo: formData.memo || null,
      };
      await assigneeApi.updateAssignee(id, updateData);
      setEditingId(null);
      setFormData({ name: '', memo: '' });
      await fetchAssignees();
    } catch (err: any) {
      alert(err.response?.data?.message || '담당자 수정에 실패했습니다.');
    }
  };

  const handleDelete = async (id: number, name: string) => {
    if (!confirm(`"${name}" 담당자를 삭제하시겠습니까?`)) return;

    try {
      await assigneeApi.deleteAssignee(id);
      await fetchAssignees();
    } catch (err: any) {
      alert(err.response?.data?.message || '담당자 삭제에 실패했습니다.');
    }
  };

  const startEdit = (assignee: Assignee) => {
    setEditingId(assignee.id);
    setFormData({
      name: assignee.name,
      memo: assignee.memo || '',
    });
    setIsCreating(false);
  };

  const cancelEdit = () => {
    setEditingId(null);
    setIsCreating(false);
    setFormData({ name: '', memo: '' });
  };

  if (loading) return <div className="loading">로딩 중...</div>;
  if (error) return <div className="error-message">{error}</div>;

  return (
    <div className="assignee-section">
      <div className="section-header">
        <div>
          <h2>담당자 관리</h2>
          <p className="settings-description">
            작업에 할당할 수 있는 담당자를 관리합니다.
          </p>
        </div>
        {!isCreating && !editingId && (
          <button
            className="btn-primary"
            onClick={() => setIsCreating(true)}
          >
            + 새 담당자
          </button>
        )}
      </div>

      {/* Create Form */}
      {isCreating && (
        <form onSubmit={handleCreate} className="assignee-form">
          <div className="form-group">
            <label>담당자 이름 *</label>
            <input
              type="text"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              placeholder="예: 홍길동, 김철수"
              required
            />
          </div>
          <div className="form-group">
            <label>메모</label>
            <textarea
              value={formData.memo || ''}
              onChange={(e) => setFormData({ ...formData, memo: e.target.value })}
              placeholder="담당자에 대한 메모를 입력하세요 (예: 개발팀, 010-1234-5678)"
              rows={3}
            />
          </div>
          <div className="form-actions">
            <button type="submit" className="btn-primary">생성</button>
            <button type="button" className="btn-secondary" onClick={cancelEdit}>
              취소
            </button>
          </div>
        </form>
      )}

      {/* Assignee List */}
      <div className="assignee-list">
        {assignees.map((assignee) => (
          <div key={assignee.id} className="assignee-item">
            {editingId === assignee.id ? (
              <form
                onSubmit={(e) => {
                  e.preventDefault();
                  handleUpdate(assignee.id);
                }}
                className="assignee-form inline"
              >
                <div className="form-group">
                  <label>담당자 이름 *</label>
                  <input
                    type="text"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>메모</label>
                  <textarea
                    value={formData.memo || ''}
                    onChange={(e) => setFormData({ ...formData, memo: e.target.value })}
                    rows={2}
                  />
                </div>
                <div className="form-actions">
                  <button type="submit" className="btn-primary">저장</button>
                  <button type="button" className="btn-secondary" onClick={cancelEdit}>
                    취소
                  </button>
                </div>
              </form>
            ) : (
              <>
                <div className="assignee-info">
                  <div className="assignee-header">
                    <div className="assignee-avatar">
                      {assignee.name.charAt(0).toUpperCase()}
                    </div>
                    <div className="assignee-details">
                      <h3>{assignee.name}</h3>
                      {assignee.memo && (
                        <p className="assignee-memo">{assignee.memo}</p>
                      )}
                    </div>
                  </div>
                  <div className="assignee-meta">
                    <span>생성: {new Date(assignee.createdAt).toLocaleDateString()}</span>
                    <span>수정: {new Date(assignee.updatedAt).toLocaleDateString()}</span>
                  </div>
                </div>
                <div className="assignee-actions">
                  <button
                    className="btn-icon"
                    onClick={() => startEdit(assignee)}
                    title="수정"
                  >
                    ✏️
                  </button>
                  <button
                    className="btn-icon"
                    onClick={() => handleDelete(assignee.id, assignee.name)}
                    title="삭제"
                  >
                    🗑️
                  </button>
                </div>
              </>
            )}
          </div>
        ))}
      </div>

      {assignees.length === 0 && !isCreating && (
        <div className="empty-state">
          <p>등록된 담당자가 없습니다.</p>
          <button className="btn-primary" onClick={() => setIsCreating(true)}>
            첫 담당자 추가하기
          </button>
        </div>
      )}
    </div>
  );
}

export default AssigneeSection;
