# ============================================
#   STUDYFLOW — routes.py
#   All REST API Endpoints
# ============================================

from flask import Blueprint, request, jsonify
from models import (
    get_all_tasks, insert_task,
    update_task_status, delete_task_by_id, get_stats
)

task_routes = Blueprint('task_routes', __name__)

# ── HEALTH CHECK ─────────────────────────────
@task_routes.route('/api/health', methods=['GET'])
def health():
    return jsonify({
        'status':  'ok',
        'message': '🚀 StudyFlow API is running!',
        'version': '1.0.0',
        'developer': 'Usha Rani Kannaji'
    }), 200

# ── GET ALL TASKS ─────────────────────────────
@task_routes.route('/api/tasks', methods=['GET'])
def get_tasks():
    try:
        tasks = get_all_tasks()
        return jsonify(tasks), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500

# ── CREATE TASK ───────────────────────────────
@task_routes.route('/api/tasks', methods=['POST'])
def create_task():
    try:
        data = request.get_json()

        # Basic validation
        required = ['title', 'subject', 'priority', 'deadline']
        for field in required:
            if not data.get(field):
                return jsonify({'error': f'{field} is required'}), 400

        new_id = insert_task(data)
        return jsonify({
            'message': 'Task created successfully!',
            'id': new_id
        }), 201

    except Exception as e:
        return jsonify({'error': str(e)}), 500

# ── UPDATE TASK STATUS ────────────────────────
@task_routes.route('/api/tasks/<int:task_id>', methods=['PUT'])
def update_task(task_id):
    try:
        data   = request.get_json()
        status = data.get('status')

        if status not in ['Pending', 'Completed']:
            return jsonify({'error': 'Status must be Pending or Completed'}), 400

        rows = update_task_status(task_id, status)
        if rows == 0:
            return jsonify({'error': 'Task not found'}), 404

        return jsonify({'message': f'Task updated to {status}'}), 200

    except Exception as e:
        return jsonify({'error': str(e)}), 500

# ── DELETE TASK ───────────────────────────────
@task_routes.route('/api/tasks/<int:task_id>', methods=['DELETE'])
def delete_task(task_id):
    try:
        rows = delete_task_by_id(task_id)
        if rows == 0:
            return jsonify({'error': 'Task not found'}), 404

        return jsonify({'message': 'Task deleted successfully!'}), 200

    except Exception as e:
        return jsonify({'error': str(e)}), 500

# ── GET STATS ─────────────────────────────────
@task_routes.route('/api/stats', methods=['GET'])
def stats():
    try:
        data = get_stats()
        return jsonify(data), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500
