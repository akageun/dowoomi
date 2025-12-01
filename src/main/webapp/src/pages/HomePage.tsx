import { Link } from 'react-router-dom';
import './HomePage.css';

function HomePage() {
  return (
    <div className="home-page">
      <h1 className="page-title">Welcome to Dowoomi</h1>
      <p className="home-description">
        Dowoomi는 효율적인 작업 관리를 위한 도구입니다.
      </p>

      <div className="home-cards">
        <div className="home-card">
          <h2>📋 Tasks</h2>
          <p>작업을 생성하고 관리하세요. 진행 상태, 담당자, 태그 등을 관리할 수 있습니다.</p>
          <Link to="/tasks" className="btn btn-primary">
            Tasks 관리하기
          </Link>
        </div>

        <div className="home-card">
          <h2>📄 Documents</h2>
          <p>프로젝트 관련 문서를 관리하세요.</p>
          <Link to="/documents" className="btn btn-primary">
            Documents 보기
          </Link>
        </div>
      </div>

      <div className="home-features">
        <h2>주요 기능</h2>
        <ul>
          <li>✅ Task 생성/수정/삭제</li>
          <li>📊 진행 상태 관리 (Todo, In Progress, Done)</li>
          <li>🏷️ 태그 관리</li>
          <li>👥 담당자 지정</li>
          <li>🔗 링크 첨부</li>
          <li>📅 일정 관리 (시작일/종료일)</li>
          <li>🔄 의존성 관리</li>
          <li>📁 카테고리 분류</li>
        </ul>
      </div>
    </div>
  );
}

export default HomePage;
