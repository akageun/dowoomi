import { useState, useEffect } from 'react';
import { categoryApi, Category, CategoryLabel, CreateCategoryRequest, UpdateCategoryRequest } from '../../api/categoryApi';
import './CategorySection.css';

function CategorySection() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [isCreating, setIsCreating] = useState(false);

  // Form state
  const [formData, setFormData] = useState<CreateCategoryRequest>({
    name: '',
    description: '',
    label: null,
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const categoriesData = await categoryApi.getAllCategories();
      setCategories(categoriesData);
      setError(null);
    } catch (err) {
      setError('데이터를 불러오는데 실패했습니다.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.name.trim()) {
      alert('카테고리 이름을 입력해주세요.');
      return;
    }

    try {
      await categoryApi.createCategory(formData);
      setFormData({ name: '', description: '', label: null });
      setIsCreating(false);
      await fetchData();
    } catch (err: any) {
      alert(err.response?.data?.message || '카테고리 생성에 실패했습니다.');
    }
  };

  const handleUpdate = async (id: number) => {
    try {
      const updateData: UpdateCategoryRequest = {
        name: formData.name,
        description: formData.description || null,
        label: formData.label,
      };
      await categoryApi.updateCategory(id, updateData);
      setEditingId(null);
      setFormData({ name: '', description: '', label: null });
      await fetchData();
    } catch (err: any) {
      alert(err.response?.data?.message || '카테고리 수정에 실패했습니다.');
    }
  };

  const handleDelete = async (id: number, name: string) => {
    if (!confirm(`"${name}" 카테고리를 삭제하시겠습니까?`)) return;

    try {
      await categoryApi.deleteCategory(id);
      await fetchData();
    } catch (err: any) {
      alert(err.response?.data?.message || '카테고리 삭제에 실패했습니다.');
    }
  };

  const startEdit = (category: Category) => {
    setEditingId(category.id);
    setFormData({
      name: category.name,
      description: category.description || '',
      label: category.label?.name || null,
    });
    setIsCreating(false);
  };

  const cancelEdit = () => {
    setEditingId(null);
    setIsCreating(false);
    setFormData({ name: '', description: '', label: null });
  };

  if (loading) return <div className="loading">로딩 중...</div>;
  if (error) return <div className="error-message">{error}</div>;

  return (
    <div className="category-section">
      <div className="section-header">
        <div>
          <h2>카테고리 관리</h2>
          <p className="settings-description">
            작업을 분류하기 위한 카테고리를 관리합니다.
          </p>
        </div>
        {!isCreating && !editingId && (
          <button
            className="btn-primary"
            onClick={() => setIsCreating(true)}
          >
            + 새 카테고리
          </button>
        )}
      </div>

      {/* Create Form */}
      {isCreating && (
        <form onSubmit={handleCreate} className="category-form">
          <div className="form-group">
            <label>카테고리 이름 *</label>
            <input
              type="text"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              placeholder="예: 개발, 디자인, 마케팅"
              required
            />
          </div>
          <div className="form-group">
            <label>설명</label>
            <textarea
              value={formData.description || ''}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              placeholder="카테고리에 대한 설명을 입력하세요"
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

      {/* Category List */}
      <div className="category-list">
        {categories.map((category) => (
          <div key={category.id} className="category-item">
            {editingId === category.id ? (
              <form
                onSubmit={(e) => {
                  e.preventDefault();
                  handleUpdate(category.id);
                }}
                className="category-form inline"
              >
                <div className="form-group">
                  <label>카테고리 이름 *</label>
                  <input
                    type="text"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>설명</label>
                  <textarea
                    value={formData.description || ''}
                    onChange={(e) => setFormData({ ...formData, description: e.target.value })}
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
                <div className="category-info">
                  <div className="category-header">
                    <h3>{category.name}</h3>
                    {category.label && (
                      <span
                        className="category-label-badge"
                        style={{ backgroundColor: category.label.colorCode }}
                      >
                        {category.label.displayName}
                      </span>
                    )}
                  </div>
                  {category.description && (
                    <p className="category-description">{category.description}</p>
                  )}
                  <div className="category-meta">
                    <span>생성: {new Date(category.createdAt).toLocaleDateString()}</span>
                    <span>수정: {new Date(category.updatedAt).toLocaleDateString()}</span>
                  </div>
                </div>
                <div className="category-actions">
                  <button
                    className="btn-icon"
                    onClick={() => startEdit(category)}
                    title="수정"
                  >
                    ✏️
                  </button>
                  <button
                    className="btn-icon"
                    onClick={() => handleDelete(category.id, category.name)}
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

      {categories.length === 0 && !isCreating && (
        <div className="empty-state">
          <p>등록된 카테고리가 없습니다.</p>
          <button className="btn-primary" onClick={() => setIsCreating(true)}>
            첫 카테고리 만들기
          </button>
        </div>
      )}
    </div>
  );
}

export default CategorySection;
