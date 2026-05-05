# ============================================
#   STUDYFLOW — models.py
#   Database queries (MySQL)
# ============================================

from flask_mysqldb import MySQL

mysql = MySQL()

def row_to_dict(row):
    """Convert a database row tuple to a dictionary"""
    return {
        'id':         row[0],
        'title':      row[1],
        'subject':    row[2],
        'priority':   row[3],
        'deadline':   str(row[4]),
        'notes':      row[5] or '',
        'status':     row[6],
        'created_at': str(row[7])
    }

# ── GET ALL TASKS ────────────────────────────
def get_all_tasks():
    cur = mysql.connection.cursor()
    cur.execute("SELECT * FROM tasks ORDER BY created_at DESC")
    rows = cur.fetchall()
    cur.close()
    return [row_to_dict(r) for r in rows]

# ── INSERT TASK ──────────────────────────────
def insert_task(task):
    cur = mysql.connection.cursor()
    cur.execute("""
        INSERT INTO tasks (title, subject, priority, deadline, notes, status)
        VALUES (%s, %s, %s, %s, %s, %s)
    """, (
        task.get('title'),
        task.get('subject'),
        task.get('priority'),
        task.get('deadline'),
        task.get('notes', ''),
        task.get('status', 'Pending')
    ))
    mysql.connection.commit()
    new_id = cur.lastrowid
    cur.close()
    return new_id

# ── UPDATE STATUS ────────────────────────────
def update_task_status(task_id, status):
    cur = mysql.connection.cursor()
    cur.execute("UPDATE tasks SET status = %s WHERE id = %s", (status, task_id))
    mysql.connection.commit()
    rows_affected = cur.rowcount
    cur.close()
    return rows_affected

# ── DELETE TASK ──────────────────────────────
def delete_task_by_id(task_id):
    cur = mysql.connection.cursor()
    cur.execute("DELETE FROM tasks WHERE id = %s", (task_id,))
    mysql.connection.commit()
    rows_affected = cur.rowcount
    cur.close()
    return rows_affected

# ── GET STATS ────────────────────────────────
def get_stats():
    cur = mysql.connection.cursor()
    cur.execute("""
        SELECT
            COUNT(*) AS total,
            SUM(CASE WHEN status = 'Pending'   THEN 1 ELSE 0 END) AS pending,
            SUM(CASE WHEN status = 'Completed' THEN 1 ELSE 0 END) AS completed
        FROM tasks
    """)
    row = cur.fetchone()
    cur.close()
    return {
        'total':     row[0] or 0,
        'pending':   row[1] or 0,
        'completed': row[2] or 0
    }
