import { useState } from 'react';
import CategorySection from '../components/settings/CategorySection';
import AssigneeSection from '../components/settings/AssigneeSection';
import './SettingsPage.css';

type SettingsTab = 'categories' | 'assignees' | 'general';

function SettingsPage() {
  const [activeTab, setActiveTab] = useState<SettingsTab>('categories');

  return (
    <div className="settings-page">
      <div className="settings-header">
        <h1>설정</h1>
        <p>프로젝트 관리를 위한 기본 설정을 구성합니다.</p>
      </div>

      <div className="settings-container">
        <aside className="settings-sidebar">
          <nav className="settings-nav">
            <button
              className={`settings-nav-item ${activeTab === 'categories' ? 'active' : ''}`}
              onClick={() => setActiveTab('categories')}
            >
              <span className="icon">📁</span>
              <span>카테고리 관리</span>
            </button>
            <button
              className={`settings-nav-item ${activeTab === 'assignees' ? 'active' : ''}`}
              onClick={() => setActiveTab('assignees')}
            >
              <span className="icon">👥</span>
              <span>담당자 관리</span>
            </button>
            <button
              className={`settings-nav-item ${activeTab === 'general' ? 'active' : ''}`}
              onClick={() => setActiveTab('general')}
            >
              <span className="icon">⚙️</span>
              <span>일반 설정</span>
            </button>
          </nav>
        </aside>

        <main className="settings-content">
          {activeTab === 'categories' && <CategorySection />}
          {activeTab === 'assignees' && <AssigneeSection />}
          {activeTab === 'general' && (
            <div className="settings-section">
              <h2>일반 설정</h2>
              <p className="settings-description">
                일반 설정 기능은 추후 추가 예정입니다.
              </p>
            </div>
          )}
        </main>
      </div>
    </div>
  );
}

export default SettingsPage;
