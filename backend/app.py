# ============================================
#   STUDYFLOW — app.py
#   Developer: Usha Rani Kannaji
#   Backend: Python Flask REST API
# ============================================

from flask import Flask
from flask_cors import CORS

app = Flask(__name__)
CORS(app)  # Allow frontend (any origin) to talk to this API

# ── MySQL Configuration ──────────────────────
# Change password to your MySQL root password
app.config['MYSQL_HOST']     = 'localhost'
app.config['MYSQL_USER']     = 'root'
app.config['MYSQL_PASSWORD'] = 'your_password'   # ← CHANGE THIS
app.config['MYSQL_DB']       = 'studyflow_db'

# ── Register Blueprints ──────────────────────
from routes import task_routes
app.register_blueprint(task_routes)

# ── Run ──────────────────────────────────────
if __name__ == '__main__':
    print("=" * 45)
    print("  🚀 StudyFlow Backend is RUNNING!")
    print("  📡 API Base: http://localhost:5000/api")
    print("  🏥 Health:   http://localhost:5000/api/health")
    print("=" * 45)
    app.run(debug=True, host='0.0.0.0', port=5000)
