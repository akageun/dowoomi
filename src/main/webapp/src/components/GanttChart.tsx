import { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import type { Task } from '../types/task';
import './GanttChart.css';

interface GanttChartProps {
  tasks: Task[];
  onTaskClick?: (taskId: number) => void;
}

interface Popover {
  x: number;
  y: number;
  title: string;
  content: React.ReactNode;
}

function GanttChart({ tasks, onTaskClick }: GanttChartProps) {
  const [currentMonth, setCurrentMonth] = useState<string>('');
  const [months, setMonths] = useState<string[]>([]);
  const [popover, setPopover] = useState<Popover | null>(null);
  const popoverRef = useRef<HTMLDivElement>(null);

  // 날짜 유틸 함수들
  const formatDate = (dateStr: string | null) => {
    if (!dateStr) return '-';
    const [y, m, d] = dateStr.split('-');
    if (!y || !m || !d) return dateStr;
    return `${y}.${m}.${d}`;
  };

  const toDate = (dateStr: string | null): Date | null => {
    return dateStr ? new Date(dateStr + 'T00:00:00') : null;
  };

  const dayDiff = (a: Date, b: Date): number => {
    const ms = b.getTime() - a.getTime();
    return Math.round(ms / (1000 * 60 * 60 * 24));
  };

  const getDaysInMonth = (year: number, monthIndex: number): number => {
    return new Date(year, monthIndex + 1, 0).getDate();
  };

  const pad2 = (num: number): string => {
    return num < 10 ? '0' + num : '' + num;
  };

  // visible tasks (deleted 제외)
  const visibleTasks = tasks
    .filter(t => t.lifecycle !== 'deleted')
    .sort((a, b) => {
      const aTime = a.startDate ? new Date(a.startDate).getTime() : 0;
      const bTime = b.startDate ? new Date(b.startDate).getTime() : 0;
      return aTime - bTime;
    });

  // 월 리스트 계산
  useEffect(() => {
    const monthSet = new Set<string>();
    visibleTasks.forEach(t => {
      const s = toDate(t.startDate);
      const e = toDate(t.endDate || t.startDate);
      if (!s || !e) return;

      const cur = new Date(s.getTime());
      cur.setDate(1);
      while (cur <= e) {
        const ym = cur.getFullYear() + '-' + pad2(cur.getMonth() + 1);
        monthSet.add(ym);
        cur.setMonth(cur.getMonth() + 1);
      }
    });

    const sortedMonths = Array.from(monthSet).sort();
    setMonths(sortedMonths);
    if (sortedMonths.length > 0 && !currentMonth) {
      setCurrentMonth(sortedMonths[0]);
    }
  }, [tasks]);

  // popover 외부 클릭 시 닫기
  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (popoverRef.current && !popoverRef.current.contains(e.target as Node)) {
        setPopover(null);
      }
    };
    document.addEventListener('click', handleClickOutside);
    return () => document.removeEventListener('click', handleClickOutside);
  }, []);

  const showPopover = (x: number, y: number, title: string, content: React.ReactNode) => {
    // 화면 밖으로 나가지 않도록 보정
    let left = x;
    let top = y;
    const popWidth = 280;
    const popHeight = 200;

    if (left + popWidth > window.innerWidth - 8) {
      left = window.innerWidth - popWidth - 8;
    }
    if (top + popHeight > window.innerHeight - 8) {
      top = window.innerHeight - popHeight - 8;
    }

    setPopover({ x: left, y: top, title, content });
  };

  // 현재 선택된 월 정보
  const [yearStr, monthStr] = currentMonth ? currentMonth.split('-') : ['', ''];
  const year = parseInt(yearStr, 10);
  const monthIndex = parseInt(monthStr, 10) - 1;
  const daysInMonth = currentMonth ? getDaysInMonth(year, monthIndex) : 0;
  const monthStart = currentMonth ? new Date(year, monthIndex, 1) : null;
  const monthEnd = currentMonth ? new Date(year, monthIndex, daysInMonth) : null;

  // 이 월에 표시할 task 필터링
  const tasksInMonth = visibleTasks.filter(task => {
    if (!currentMonth || !monthStart || !monthEnd) return false;
    const s = toDate(task.startDate);
    const e = toDate(task.endDate || task.startDate);
    if (!s || !e) return false;
    return !(e < monthStart || s > monthEnd);
  });

  const handleTaskNameClick = (e: React.MouseEvent, task: Task) => {
    e.stopPropagation();
    const rect = (e.target as HTMLElement).getBoundingClientRect();
    
    if (task.links.length === 0) {
      // 링크가 없으면 상세 페이지로 이동
      if (onTaskClick) {
        onTaskClick(task.id);
      }
      return;
    }
    
    if (task.links.length === 1) {
      window.open(task.links[0].url, '_blank', 'noopener,noreferrer');
      return;
    }

    showPopover(
      rect.left + rect.width / 2,
      rect.bottom + 8,
      'Task 링크',
      <ul className="popover-list">
        {task.links.map(link => (
          <li key={link.id}>
            <a href={link.url} target="_blank" rel="noopener noreferrer">
              {link.name || link.url}
            </a>
          </li>
        ))}
      </ul>
    );
  };

  const handleDepClick = (e: React.MouseEvent, task: Task) => {
    e.stopPropagation();
    const rect = (e.target as HTMLElement).getBoundingClientRect();

    if (task.dependencies.length === 0) {
      showPopover(
        rect.left + rect.width / 2,
        rect.bottom + 8,
        '연관 작업',
        <div className="popover-empty">연관 작업 정보가 없습니다.</div>
      );
      return;
    }

    showPopover(
      rect.left + rect.width / 2,
      rect.bottom + 8,
      '연관 작업 (Dependencies)',
      <ul className="popover-list">
        {task.dependencies.map(dep => (
          <li key={dep.taskId}>
            <Link to={`/tasks/${dep.taskId}`} onClick={() => setPopover(null)}>
              {dep.taskTitle}
            </Link>
          </li>
        ))}
      </ul>
    );
  };

  const getBarClass = (task: Task): string => {
    const classes = ['gantt-bar'];
    if (task.progress === 'done') classes.push('done');
    if (task.lifecycle === 'draft') classes.push('draft');
    if (task.lifecycle === 'deleted') classes.push('deleted');
    return classes.join(' ');
  };

  const getBarStyle = (task: Task): React.CSSProperties => {
    if (!monthStart || !monthEnd) return {};

    const s = toDate(task.startDate);
    const e = toDate(task.endDate || task.startDate);
    if (!s || !e) return {};

    const taskStart = s < monthStart ? monthStart : s;
    const taskEnd = e > monthEnd ? monthEnd : e;

    const startOffset = dayDiff(monthStart, taskStart);
    const taskDays = Math.max(dayDiff(taskStart, taskEnd) + 1, 1);
    const totalDays = daysInMonth;

    const leftPercent = (startOffset / totalDays) * 100;
    const widthPercent = (taskDays / totalDays) * 100;

    return {
      left: `${leftPercent}%`,
      width: `${widthPercent}%`,
    };
  };

  if (months.length === 0) {
    return (
      <div className="gantt-wrapper">
        <div className="gantt-empty">표시할 Task가 없습니다.</div>
      </div>
    );
  }

  return (
    <div className="gantt-wrapper">
      <div className="gantt-header">
        <div className="gantt-date-range">
          {currentMonth && (
            <>
              {formatDate(`${year}-${pad2(monthIndex + 1)}-01`)} ~ {formatDate(`${year}-${pad2(monthIndex + 1)}-${pad2(daysInMonth)}`)}
            </>
          )}
        </div>
        <div className="gantt-controls">
          <label htmlFor="ganttMonthSelect">월 선택:</label>
          <select 
            id="ganttMonthSelect" 
            value={currentMonth}
            onChange={(e) => setCurrentMonth(e.target.value)}
          >
            {months.map(ym => {
              const [y, m] = ym.split('-');
              return (
                <option key={ym} value={ym}>
                  {y}년 {m}월
                </option>
              );
            })}
          </select>
        </div>
        <div className="gantt-legend">
          <div className="gantt-legend-item">
            <span className="gantt-legend-color" style={{ background: '#bfdbfe' }}></span>
            <span>진행 / 예정</span>
          </div>
          <div className="gantt-legend-item">
            <span className="gantt-legend-color" style={{ background: '#bbf7d0' }}></span>
            <span>완료</span>
          </div>
          <div className="gantt-legend-item">
            <span className="gantt-legend-color" style={{ background: '#e0e7ff' }}></span>
            <span>Draft</span>
          </div>
        </div>
      </div>

      <div className="gantt-container">
        <div className="gantt-task-names">
          {tasksInMonth.map(task => (
            <div key={task.id} className="gantt-task-name">
              <button
                type="button"
                className="gantt-task-link-btn"
                onClick={(e) => handleTaskNameClick(e, task)}
              >
                {task.title}
              </button>
            </div>
          ))}
        </div>

        <div className="gantt-chart">
          <div className="gantt-inner">
            {/* 타임라인 헤더 */}
            <div className="gantt-timeline">
              {Array.from({ length: daysInMonth }, (_, i) => (
                <div key={i} className="gantt-day-label">
                  {i + 1}
                </div>
              ))}
            </div>

            {/* 그리드 배경 */}
            <div className="gantt-grid">
              {Array.from({ length: daysInMonth }, (_, i) => (
                <div key={i} className="gantt-grid-cell"></div>
              ))}
            </div>

            {/* Task 바들 */}
            <div className="gantt-rows">
              {tasksInMonth.map(task => (
                <div key={task.id} className="gantt-row">
                  <div
                    className={getBarClass(task)}
                    style={getBarStyle(task)}
                    title={`${task.title} (${task.startDate} ~ ${task.endDate})`}
                  >
                    {task.dependencies.length > 0 && (
                      <button
                        type="button"
                        className="gantt-dep-icon"
                        onClick={(e) => handleDepClick(e, task)}
                      >
                        ⛓
                      </button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Popover */}
      {popover && (
        <div
          ref={popoverRef}
          className="popover"
          style={{ left: popover.x, top: popover.y }}
        >
          <button 
            className="popover-close" 
            type="button"
            onClick={() => setPopover(null)}
          >
            ✕
          </button>
          <div className="popover-header">{popover.title}</div>
          <div className="popover-body">{popover.content}</div>
        </div>
      )}
    </div>
  );
}

export default GanttChart;
