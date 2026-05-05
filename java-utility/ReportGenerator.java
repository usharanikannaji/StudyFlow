/**
 * ============================================
 *   STUDYFLOW — ReportGenerator.java
 *   Java Utility: Study Progress Report
 *   Developer: Usha Rani Kannaji
 * ============================================
 *
 * HOW TO RUN:
 *   1. Download MySQL JDBC connector JAR from:
 *      https://dev.mysql.com/downloads/connector/j/
 *   2. Place mysql-connector-j-x.x.x.jar in this folder
 *   3. Compile:  javac ReportGenerator.java
 *   4. Run (Windows): java -cp ".;mysql-connector-j-8.0.33.jar" ReportGenerator
 *      Run (Mac/Linux): java -cp ".:mysql-connector-j-8.0.33.jar" ReportGenerator
 */

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportGenerator {

    // ── Database Config ─────────────────────
    static final String DB_URL  = "jdbc:mysql://localhost:3306/studyflow_db";
    static final String DB_USER = "root";
    static final String DB_PASS = "your_password";  // ← Change this

    // ── Colors for terminal ──────────────────
    static final String RESET  = "\u001B[0m";
    static final String PURPLE = "\u001B[35m";
    static final String GREEN  = "\u001B[32m";
    static final String YELLOW = "\u001B[33m";
    static final String RED    = "\u001B[31m";
    static final String CYAN   = "\u001B[36m";
    static final String BOLD   = "\u001B[1m";

    public static void main(String[] args) {
        printBanner();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            System.out.println(GREEN + "✅ Connected to StudyFlow Database successfully!\n" + RESET);

            printSummary(conn);
            printSubjectReport(conn);
            printPriorityReport(conn);
            printUpcomingDeadlines(conn);
            printRecentActivity(conn);

        } catch (SQLException e) {
            System.err.println(RED + "❌ Database Error: " + e.getMessage() + RESET);
            System.err.println(YELLOW + "💡 Make sure MySQL is running and password is correct in DB_PASS." + RESET);
        }

        printFooter();
    }

    // ── BANNER ───────────────────────────────
    static void printBanner() {
        String time = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
        System.out.println(PURPLE + BOLD);
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║       📚  STUDYFLOW — PROGRESS REPORT           ║");
        System.out.println("║          Developer: Usha Rani Kannaji           ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println(RESET);
        System.out.println("  Generated on: " + CYAN + time + RESET + "\n");
    }

    // ── OVERALL SUMMARY ──────────────────────
    static void printSummary(Connection conn) throws SQLException {
        String sql = """
            SELECT
                COUNT(*) AS total,
                SUM(CASE WHEN status = 'Pending'   THEN 1 ELSE 0 END) AS pending,
                SUM(CASE WHEN status = 'Completed' THEN 1 ELSE 0 END) AS completed,
                IFNULL(ROUND(
                    SUM(CASE WHEN status = 'Completed' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 1
                ), 0) AS progress_pct
            FROM tasks
        """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int    total    = rs.getInt("total");
                int    pending  = rs.getInt("pending");
                int    completed= rs.getInt("completed");
                double pct      = rs.getDouble("progress_pct");

                System.out.println(BOLD + "📊  OVERALL SUMMARY" + RESET);
                System.out.println("    " + "─".repeat(36));
                System.out.printf("    %-20s : %s%d%s%n", "Total Tasks",    CYAN,   total,     RESET);
                System.out.printf("    %-20s : %s%d%s%n", "Pending",        YELLOW, pending,   RESET);
                System.out.printf("    %-20s : %s%d%s%n", "Completed",      GREEN,  completed, RESET);
                System.out.printf("    %-20s : %s%.1f%%%s%n", "Progress",   GREEN,  pct,       RESET);

                // Progress bar
                int filled = (int)(pct / 5);
                System.out.print("    [");
                System.out.print(GREEN + "█".repeat(filled) + RESET);
                System.out.print("░".repeat(20 - filled));
                System.out.printf("] %s%.1f%%%s%n%n", GREEN, pct, RESET);
            }
        }
    }

    // ── SUBJECT BREAKDOWN ────────────────────
    static void printSubjectReport(Connection conn) throws SQLException {
        String sql = """
            SELECT subject,
                COUNT(*) AS total,
                SUM(CASE WHEN status = 'Completed' THEN 1 ELSE 0 END) AS completed,
                SUM(CASE WHEN status = 'Pending'   THEN 1 ELSE 0 END) AS pending
            FROM tasks
            GROUP BY subject
            ORDER BY total DESC
        """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println(BOLD + "📚  SUBJECT-WISE BREAKDOWN" + RESET);
            System.out.println("    " + "─".repeat(56));
            System.out.printf("    %-28s %-8s %-10s %-8s%n",
                "Subject", "Total", "Completed", "Pending");
            System.out.println("    " + "─".repeat(56));

            while (rs.next()) {
                System.out.printf("    %-28s %-8d %s%-10d%s %s%-8d%s%n",
                    truncate(rs.getString("subject"), 27),
                    rs.getInt("total"),
                    GREEN,  rs.getInt("completed"), RESET,
                    YELLOW, rs.getInt("pending"),   RESET);
            }
            System.out.println();
        }
    }

    // ── PRIORITY BREAKDOWN ───────────────────
    static void printPriorityReport(Connection conn) throws SQLException {
        String sql = """
            SELECT priority, COUNT(*) AS count,
                SUM(CASE WHEN status = 'Completed' THEN 1 ELSE 0 END) AS done
            FROM tasks
            GROUP BY priority
            ORDER BY FIELD(priority, 'High', 'Medium', 'Low')
        """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println(BOLD + "🎯  PRIORITY BREAKDOWN" + RESET);
            System.out.println("    " + "─".repeat(36));

            while (rs.next()) {
                String p    = rs.getString("priority");
                int    cnt  = rs.getInt("count");
                int    done = rs.getInt("done");
                String icon = p.equals("High") ? "🔴" : p.equals("Medium") ? "🟡" : "🟢";
                String color= p.equals("High") ? RED  : p.equals("Medium") ? YELLOW : GREEN;

                System.out.printf("    %s %-10s : %s%d tasks%s (%d completed)%n",
                    icon, p, color, cnt, RESET, done);
            }
            System.out.println();
        }
    }

    // ── UPCOMING DEADLINES ───────────────────
    static void printUpcomingDeadlines(Connection conn) throws SQLException {
        String sql = """
            SELECT title, subject, deadline, priority,
                DATEDIFF(deadline, CURDATE()) AS days_left
            FROM tasks
            WHERE status = 'Pending' AND deadline >= CURDATE()
            ORDER BY deadline ASC
            LIMIT 5
        """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println(BOLD + "⏰  UPCOMING DEADLINES" + RESET);
            System.out.println("    " + "─".repeat(66));
            System.out.printf("    %-30s %-18s %-12s %-6s%n",
                "Task", "Subject", "Deadline", "Days Left");
            System.out.println("    " + "─".repeat(66));

            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                int daysLeft = rs.getInt("days_left");
                String urgency = daysLeft <= 1 ? RED : daysLeft <= 3 ? YELLOW : GREEN;

                System.out.printf("    %-30s %-18s %-12s %s%-6d%s%n",
                    truncate(rs.getString("title"),   29),
                    truncate(rs.getString("subject"), 17),
                    rs.getString("deadline"),
                    urgency, daysLeft, RESET);
            }

            if (!hasRows) {
                System.out.println("    " + GREEN + "✅ No pending deadlines — you're all caught up!" + RESET);
            }
            System.out.println();
        }
    }

    // ── RECENT ACTIVITY ──────────────────────
    static void printRecentActivity(Connection conn) throws SQLException {
        String sql = """
            SELECT title, status, created_at
            FROM tasks
            ORDER BY created_at DESC
            LIMIT 5
        """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println(BOLD + "🕐  RECENT ACTIVITY (Last 5 Tasks Added)" + RESET);
            System.out.println("    " + "─".repeat(56));

            while (rs.next()) {
                String status = rs.getString("status");
                String color  = status.equals("Completed") ? GREEN : YELLOW;
                System.out.printf("    • %-35s %s[%s]%s%n",
                    truncate(rs.getString("title"), 34),
                    color, status, RESET);
            }
            System.out.println();
        }
    }

    // ── FOOTER ───────────────────────────────
    static void printFooter() {
        System.out.println(PURPLE + BOLD);
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║         Report Generated Successfully! 🎉       ║");
        System.out.println("║   Stack: Java · Python Flask · MySQL · Docker   ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println(RESET);
    }

    // ── HELPER ───────────────────────────────
    static String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() > maxLen ? s.substring(0, maxLen - 2) + ".." : s;
    }
}
