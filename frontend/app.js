// ============================================
//   STUDYFLOW — APP.JS
//   Developer: Usha Rani Kannaji
//   Stack: Vanilla JS + LocalStorage + Flask API
// ============================================

// ===== STATE =====
let tasks = [];
let currentFilter = 'all';
let deleteTargetId = null;

// ===== INIT =====
window.onload = () => {
  loadTasks();
  setTodayAsMinDate();
};

// ===== SET MIN DATE =====
function setTodayAsMinDate() {
  const today = new Date().toISOString().split('T')[0];
  document.getElementById('taskDeadline').setAttribute('min', today);
}

// ===== LOAD TASKS =====
async function loadTasks() {
  // Try backend first
  const backendTasks = await fetchFromBackend();
  if (backendTasks && backendTasks.length > 0) {
    tasks = backendTasks;
  } else {
    // Fall back to localStorage
    tasks = JSON.parse(localStorage.getItem('studyflow_tasks')) || getSampleTasks();
  }
  saveLocal();
  render();
}

// ===== SAMPLE TASKS (first time) =====
function getSampleTasks() {
  return [
    {
      id: Date.now() + 1,
      title: 'Complete DSA Chapter 5 — Trees & Graphs',
      subject: 'Data Structures',
      priority: 'High',
      deadline: getFutureDate(3),
      notes: 'Focus on BFS, DFS and binary trees',
      status: 'Pending',
      createdAt: today()
    },
    {
      id: Date.now() + 2,
      title: 'Practice Flask REST API endpoints',
      subject: 'Backend Development',
      priority: 'High',
      deadline: getFutureDate(2),
      notes: 'Cover GET, POST, PUT, DELETE methods',
      status: 'Pending',
      createdAt: today()
    },
    {
      id: Date.now() + 3,
      title: 'Review Docker & Kubernetes basics',
      subject: 'DevOps',
      priority: 'Medium',
      deadline: getFutureDate(5),
      notes: 'Focus on containers, pods and deployments',
      status: 'Completed',
      createdAt: today()
    }
  ];
}

function getFutureDate(days) {
  const d = new Date();
  d.setDate(d.getDate() + days);
  return d.toISOString().split('T')[0];
}

function today() {
  return new Date().toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
}

// ===== SAVE TO LOCALSTORAGE =====
function saveLocal() {
  localStorage.setItem('studyflow_tasks', JSON.stringify(tasks));
}

// ===== ADD TASK =====
async function addTask() {
  const title    = document.getElementById('taskTitle').value.trim();
  const subject  = document.getElementById('taskSubject').value.trim();
  const priority = document.getElementById('taskPriority').value;
  const deadline = document.getElementById('taskDeadline').value;
  const notes    = document.getElementById('taskNotes').value.trim();

  // Validate
  if (!title)    { shake('taskTitle');    showToast('⚠️ Task title is required!', 'warn'); return; }
  if (!subject)  { shake('taskSubject');  showToast('⚠️ Subject is required!', 'warn'); return; }
  if (!priority) { shake('taskPriority'); showToast('⚠️ Please select a priority!', 'warn'); return; }
  if (!deadline) { shake('taskDeadline'); showToast('⚠️ Please set a deadline!', 'warn'); return; }

  const task = {
    id: Date.now(),
    title, subject, priority, deadline, notes,
    status: 'Pending',
    createdAt: today()
  };

  tasks.unshift(task);
  saveLocal();
  clearForm();
  render();
  showToast('✅ Task added to StudyFlow!', 'success');

  // Send to backend (optional)
  await sendToBackend(task);
}

// ===== SHAKE ANIMATION FOR VALIDATION =====
function shake(id) {
  const el = document.getElementById(id);
  el.style.animation = 'none';
  el.style.borderColor = 'var(--danger)';
  el.style.boxShadow = '0 0 0 3px rgba(239,68,68,0.2)';
  setTimeout(() => {
    el.style.borderColor = '';
    el.style.boxShadow = '';
  }, 2000);
}

// ===== COMPLETE TASK =====
function completeTask(id) {
  tasks = tasks.map(t => {
    if (t.id === id) {
      t.status = t.status === 'Completed' ? 'Pending' : 'Completed';
    }
    return t;
  });
  saveLocal();
  render();
  const task = tasks.find(t => t.id === id);
  if (task.status === 'Completed') {
    showToast('🎉 Great job! Task completed!', 'success');
  } else {
    showToast('↩️ Task moved back to Pending', 'info');
  }
}

// ===== DELETE TASK (with confirm) =====
function confirmDelete(id) {
  deleteTargetId = id;
  document.getElementById('modal').classList.add('open');
  document.getElementById('confirmDeleteBtn').onclick = () => deleteTask(id);
}

function deleteTask(id) {
  tasks = tasks.filter(t => t.id !== id);
  saveLocal();
  closeModal();
  render();
  showToast('🗑️ Task deleted', 'error');
}

function closeModal() {
  document.getElementById('modal').classList.remove('open');
  deleteTargetId = null;
}

// ===== FILTER =====
function filterTasks(filter, btn) {
  currentFilter = filter;
  document.querySelectorAll('.pill').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');
  render();
}

// ===== RENDER =====
function render() {
  const container = document.getElementById('taskList');

  // Filter
  let filtered = [...tasks];
  if (currentFilter === 'Pending')   filtered = tasks.filter(t => t.status === 'Pending');
  if (currentFilter === 'Completed') filtered = tasks.filter(t => t.status === 'Completed');
  if (['High','Medium','Low'].includes(currentFilter)) {
    filtered = tasks.filter(t => t.priority === currentFilter);
  }

  // Update count label
  document.getElementById('taskCountLabel').textContent =
    `${filtered.length} task${filtered.length !== 1 ? 's' : ''}`;

  // Empty state
  if (filtered.length === 0) {
    container.innerHTML = `
      <div class="empty-state">
        <div class="empty-icon">📚</div>
        <h3>No tasks here!</h3>
        <p>${currentFilter === 'all' ? 'Add your first study task above to get started.' : `No ${currentFilter} tasks found.`}</p>
      </div>`;
    updateStats();
    return;
  }

  // Render cards
  container.innerHTML = filtered.map((task, i) => `
    <div class="task-card priority-${task.priority} ${task.status === 'Completed' ? 'completed' : ''}"
         style="animation-delay: ${i * 0.05}s">

      <div class="task-top">
        <span class="task-title-text ${task.status === 'Completed' ? 'done' : ''}">${escHtml(task.title)}</span>
        <span class="badge badge-${task.priority}">${task.priority}</span>
      </div>

      <div class="task-meta">
        <div class="meta-item">
          <i class="fas fa-book"></i>
          <span>${escHtml(task.subject)}</span>
        </div>
        <div class="meta-item">
          <i class="fas fa-calendar-alt"></i>
          <span>Deadline: <strong>${task.deadline}</strong></span>
        </div>
        <div class="meta-item">
          <i class="fas fa-clock"></i>
          <span>Added: ${task.createdAt}
            <span class="status-chip chip-${task.status}">${task.status === 'Completed' ? '✅' : '⏳'} ${task.status}</span>
          </span>
        </div>
      </div>

      ${task.notes ? `<div class="task-notes-text">"${escHtml(task.notes)}"</div>` : ''}

      <div class="task-actions">
        <button class="btn-done" onclick="completeTask(${task.id})">
          <i class="fas fa-${task.status === 'Completed' ? 'undo' : 'check'}"></i>
          ${task.status === 'Completed' ? 'Undo' : 'Complete'}
        </button>
        <button class="btn-del" onclick="confirmDelete(${task.id})">
          <i class="fas fa-trash"></i> Delete
        </button>
      </div>
    </div>
  `).join('');

  updateStats();
}

// ===== UPDATE STATS =====
function updateStats() {
  const total     = tasks.length;
  const completed = tasks.filter(t => t.status === 'Completed').length;
  const pending   = tasks.filter(t => t.status === 'Pending').length;
  const pct       = total > 0 ? Math.round((completed / total) * 100) : 0;

  document.getElementById('navTotal').textContent     = total;
  document.getElementById('navPending').textContent   = pending;
  document.getElementById('navCompleted').textContent = completed;
  document.getElementById('progressPercent').textContent = pct + '%';
  document.getElementById('progressBar').style.width  = pct + '%';
}

// ===== CLEAR FORM =====
function clearForm() {
  ['taskTitle','taskSubject','taskNotes'].forEach(id => document.getElementById(id).value = '');
  document.getElementById('taskPriority').value = '';
  document.getElementById('taskDeadline').value = '';
}

// ===== TOAST =====
function showToast(msg, type = 'info') {
  const toast = document.getElementById('toast');
  toast.textContent = msg;
  toast.className = `toast show ${type}`;
  setTimeout(() => toast.classList.remove('show'), 3200);
}

// ===== ESCAPE HTML =====
function escHtml(str) {
  return str.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}

// ===== BACKEND API (Flask) =====
const API = 'http://localhost:5000/api';

async function fetchFromBackend() {
  try {
    const res = await fetch(`${API}/tasks`, { signal: AbortSignal.timeout(2000) });
    if (!res.ok) return null;
    return await res.json();
  } catch {
    return null; // Backend offline — use localStorage
  }
}

async function sendToBackend(task) {
  try {
    await fetch(`${API}/tasks`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(task),
      signal: AbortSignal.timeout(2000)
    });
  } catch {
    // Silently fail — localStorage is source of truth
  }
}
