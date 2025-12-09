import { Routes, Route, Link } from 'react-router-dom'
import HomePage from './pages/HomePage'
import TaskPage from './pages/TaskPage'
import TaskCreatePage from './pages/TaskCreatePage'
import DocumentsPage from './pages/DocumentsPage'
import TaskDetailPage from './pages/TaskDetailPage'
import SettingsPage from './pages/SettingsPage'
import './App.css'

function App() {
  return (
    <div className="app">
      <nav className="navbar">
        <div className="navbar-brand">
          <Link to="/">Dowoomi</Link>
        </div>
        <ul className="navbar-menu">
          <li><Link to="/">Home</Link></li>
          <li><Link to="/tasks">Tasks</Link></li>
          <li><Link to="/documents">Documents</Link></li>
          <li><Link to="/settings">Settings</Link></li>
        </ul>
      </nav>
      
      <main className="main-content">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/tasks" element={<TaskPage />} />
          <Route path="/tasks/new" element={<TaskCreatePage />} />
          <Route path="/tasks/:id" element={<TaskDetailPage />} />
          <Route path="/documents" element={<DocumentsPage />} />
          <Route path="/settings" element={<SettingsPage />} />
        </Routes>
      </main>
    </div>
  )
}

export default App
