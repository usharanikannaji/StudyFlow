# 📚 StudyFlow — Full Stack Study Planner

> **Plan Smart. Study Better. Achieve More.**

A complete full-stack web application to help students manage, track, and crush their study goals.

---

## 🌐 Live Demo

> Open `frontend/index.html` directly in your browser — works offline instantly!

---

## 🛠️ Tech Stack

| Layer | Technology | Purpose |
|---|---|---|
| 🎨 Frontend | HTML5, CSS3, JavaScript | UI, interactions, localStorage |
| ⚙️ Backend | Python 3, Flask | REST API (GET, POST, PUT, DELETE) |
| 🗄️ Database | MySQL | Persistent task storage |
| ☕ Java Utility | Java (JDBC) | Study progress report generator |
| 🐳 DevOps | Docker, Docker Compose | Containerized deployment |
| 🔀 Version Control | Git, GitHub | Source control |

---

## ✨ Features

- ✅ Add study tasks with title, subject, priority, deadline & notes
- ✅ Mark tasks complete / undo completion
- ✅ Delete tasks with confirmation modal
- ✅ Filter by status (Pending / Completed) or priority (High / Medium / Low)
- ✅ Live progress bar showing completion percentage
- ✅ Stats dashboard in header (Total / Pending / Done)
- ✅ Works offline with localStorage (no backend needed to run)
- ✅ REST API with Flask (6 endpoints)
- ✅ MySQL database with sample data
- ✅ Java terminal report with colorized output
- ✅ Fully Dockerized with docker-compose
- ✅ Responsive design (mobile + desktop)

---

## 🚀 How to Run

### Option 1 — Frontend Only (Instant, No Setup)
```bash
# Just open this in your browser:
frontend/index.html
```
App works fully with localStorage. No server needed!

---

### Option 2 — Full Stack (Flask + MySQL)

**Step 1: Setup MySQL**
```bash
mysql -u root -p < database/schema.sql
```

**Step 2: Configure backend**
```bash
# In backend/app.py, change:
app.config['MYSQL_PASSWORD'] = 'your_actual_password'
```

**Step 3: Install and run Flask**
```bash
cd backend
pip install -r requirements.txt
python app.py
```

**Step 4: Open frontend**
```
Open frontend/index.html in browser
```
API runs at: `http://localhost:5000/api`

---

### Option 3 — Docker (One Command!)
```bash
docker-compose up --build
```
Then open `frontend/index.html` in your browser.

---

### Java Report Generator
```bash
cd java-utility

# Compile
javac ReportGenerator.java

# Run (Windows)
java -cp ".;mysql-connector-j-8.0.33.jar" ReportGenerator

# Run (Mac/Linux)
java -cp ".:mysql-connector-j-8.0.33.jar" ReportGenerator
```
Download MySQL connector: https://dev.mysql.com/downloads/connector/j/

---

## 📡 API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/health` | Health check |
| GET | `/api/tasks` | Get all tasks |
| POST | `/api/tasks` | Create new task |
| PUT | `/api/tasks/:id` | Update task status |
| DELETE | `/api/tasks/:id` | Delete task |
| GET | `/api/stats` | Get task statistics |

---

## 📁 Project Structure

```
studyflow/
│
├── frontend/
│   ├── index.html        # Main UI
│   ├── style.css         # Styling (dark theme)
│   └── app.js            # JS logic + API calls
│
├── backend/
│   ├── app.py            # Flask app entry point
│   ├── models.py         # Database queries
│   ├── routes.py         # API endpoints
│   └── requirements.txt  # Python dependencies
│
├── database/
│   └── schema.sql        # MySQL schema + sample data
│
├── java-utility/
│   └── ReportGenerator.java  # Java progress report
│
├── Dockerfile
├── docker-compose.yml
├── .gitignore
└── README.md
```

---

## 👩‍💻 Developer

**Usha Rani Kannaji**
- 🎓 B.Tech ECE — RGUKT Srikakulam (CGPA: 8.98)
- 📧 usharanikannaji822@gmail.com
- 🔗 [LinkedIn](https://www.linkedin.com/in/usha-rani-kannaji-70aa01305/)
- 🌐 [Portfolio](https://usharanikannaji.github.io/portifolio/)
- 💻 [GitHub](https://github.com/usharanikannaji)

---

## 📄 License

MIT License — free to use and modify.
