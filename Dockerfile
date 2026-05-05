# ============================================
#   STUDYFLOW — Dockerfile
#   Developer: Usha Rani Kannaji
# ============================================

# Use official Python slim image
FROM python:3.11-slim

# Set working directory inside container
WORKDIR /app

# Copy backend code
COPY backend/ .

# Install Python dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Expose Flask port
EXPOSE 5000

# Environment variables (overridden by docker-compose)
ENV MYSQL_HOST=db
ENV MYSQL_USER=root
ENV MYSQL_PASSWORD=studyflow123
ENV MYSQL_DB=studyflow_db

# Start the Flask app
CMD ["python", "app.py"]
