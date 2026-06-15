package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.util;

import br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.config.BawAnalysisConfig;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Centralized logging and monitoring system for IBM BAW analysis.
 * Provides structured logging, performance monitoring, and analysis tracking.
 */
public class AnalysisLogger {

    private static final String LOG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final Map<String, AnalysisSession> activeSessions = new ConcurrentHashMap<>();

    private final PrintWriter writer;
    private final String sessionId;
    private final boolean enableDetailedLogging;
    private final Map<String, Long> performanceTimers = new HashMap<>();
    private final List<String> warnings = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();

    public AnalysisLogger(PrintWriter writer, String sessionId, boolean enableDetailedLogging) {
        this.writer = writer;
        this.sessionId = sessionId;
        this.enableDetailedLogging = enableDetailedLogging;

        activeSessions.put(sessionId, new AnalysisSession(sessionId));
    }

    /**
     * Creates a logger for console output.
     */
    public static AnalysisLogger createConsoleLogger(String sessionId, boolean detailed) {
        return new AnalysisLogger(new PrintWriter(System.out, true), sessionId, detailed);
    }

    /**
     * Creates a logger for file output.
     */
    public static AnalysisLogger createFileLogger(String filePath, String sessionId, boolean detailed) throws IOException {
        FileWriter fileWriter = new FileWriter(filePath, true); // Append mode
        return new AnalysisLogger(new PrintWriter(fileWriter, true), sessionId, detailed);
    }

    // ========== LOGGING METHODS ==========

    /**
     * Logs an info message.
     */
    public void info(String component, String message) {
        log(LogLevel.INFO, component, message, null);
    }

    /**
     * Logs a warning message.
     */
    public void warn(String component, String message) {
        warnings.add(message);
        log(LogLevel.WARN, component, message, null);
    }

    /**
     * Logs an error message.
     */
    public void error(String component, String message, Throwable throwable) {
        errors.add(message);
        log(LogLevel.ERROR, component, message, throwable);
    }

    /**
     * Logs a debug message (only if detailed logging is enabled).
     */
    public void debug(String component, String message) {
        if (enableDetailedLogging) {
            log(LogLevel.DEBUG, component, message, null);
        }
    }

    /**
     * Logs a progress update.
     */
    public void progress(String component, String operation, int current, int total) {
        double percentage = total > 0 ? (double) current / total * 100 : 0;
        String message = String.format("%s: %d/%d (%.1f%%)", operation, current, total, percentage);
        log(LogLevel.PROGRESS, component, message, null);
    }

    // ========== PERFORMANCE MONITORING ==========

    /**
     * Starts a performance timer.
     */
    public void startTimer(String timerName) {
        performanceTimers.put(timerName, System.currentTimeMillis());
        debug("PERF", "Started timer: " + timerName);
    }

    /**
     * Stops a performance timer and logs the duration.
     */
    public long stopTimer(String timerName) {
        Long startTime = performanceTimers.remove(timerName);
        if (startTime == null) {
            warn("PERF", "Timer not found: " + timerName);
            return 0;
        }

        long duration = System.currentTimeMillis() - startTime;
        info("PERF", String.format("Timer %s: %s", timerName, BawAnalysisUtils.formatDuration(duration)));

        // Update session metrics
        AnalysisSession session = activeSessions.get(sessionId);
        if (session != null) {
            session.addPerformanceMetric(timerName, duration);
        }

        return duration;
    }

    /**
     * Logs a performance milestone.
     */
    public void milestone(String component, String milestone, Object... args) {
        String message = args.length > 0 ? String.format(milestone, args) : milestone;
        log(LogLevel.MILESTONE, component, message, null);
    }

    // ========== ANALYSIS TRACKING ==========

    /**
     * Logs the start of artifact processing.
     */
    public void startArtifactProcessing(String artifactId, String artifactName, String artifactType) {
        String message = String.format("Processing artifact: %s (%s) - Type: %s",
                artifactName, artifactId, artifactType);
        info("ARTIFACT", message);
        startTimer("artifact_" + artifactId);
    }

    /**
     * Logs the completion of artifact processing.
     */
    public void completeArtifactProcessing(String artifactId, String artifactName) {
        long duration = stopTimer("artifact_" + artifactId);
        String message = String.format("Completed artifact: %s in %s",
                artifactName, BawAnalysisUtils.formatDuration(duration));
        info("ARTIFACT", message);

        // Update session statistics
        AnalysisSession session = activeSessions.get(sessionId);
        if (session != null) {
            session.incrementProcessedArtifacts();
        }
    }

    /**
     * Logs dependency discovery.
     */
    public void logDependencyDiscovery(String artifactId, Set<String> dependencies) {
        if (dependencies.isEmpty()) {
            debug("DEPENDENCY", "No dependencies found for: " + artifactId);
        } else {
            info("DEPENDENCY", String.format("Found %d dependencies for %s: %s",
                    dependencies.size(), artifactId, dependencies));
        }
    }

    /**
     * Logs quality analysis results.
     */
    public void logQualityAnalysis(String artifactId, String qualityScore, int issueCount) {
        String message = String.format("Quality analysis for %s: Score=%s, Issues=%d",
                artifactId, qualityScore, issueCount);

        if (issueCount > 5) {
            warn("QUALITY", message);
        } else {
            info("QUALITY", message);
        }
    }

    /**
     * Logs memory usage information.
     */
    public void logMemoryUsage(String component) {
        if (enableDetailedLogging) {
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            long maxMemory = runtime.maxMemory();

            String message = String.format("Memory: Used=%dMB, Free=%dMB, Total=%dMB, Max=%dMB",
                    usedMemory / 1024 / 1024, freeMemory / 1024 / 1024,
                    totalMemory / 1024 / 1024, maxMemory / 1024 / 1024);

            debug(component, message);
        }
    }

    // ========== SESSION MANAGEMENT ==========

    /**
     * Starts an analysis session.
     */
    public void startSession(String projectName, String processId) {
        AnalysisSession session = activeSessions.get(sessionId);
        if (session != null) {
            session.setProjectName(projectName);
            session.setProcessId(processId);
            session.setStartTime(System.currentTimeMillis());
        }

        info("SESSION", String.format("Started analysis session: %s (Project: %s, Process: %s)",
                sessionId, projectName, processId));
        startTimer("total_analysis");
    }

    /**
     * Ends an analysis session and logs summary.
     */
    public void endSession() {
        long totalDuration = stopTimer("total_analysis");
        AnalysisSession session = activeSessions.remove(sessionId);

        if (session != null) {
            session.setEndTime(System.currentTimeMillis());
            logSessionSummary(session, totalDuration);
        }

        info("SESSION", "Analysis session completed: " + sessionId);
    }

    /**
     * Logs a comprehensive session summary.
     */
    private void logSessionSummary(AnalysisSession session, long totalDuration) {
        info("SESSION", "=== ANALYSIS SESSION SUMMARY ===");
        info("SESSION", "Session ID: " + session.getSessionId());
        info("SESSION", "Project: " + session.getProjectName());
        info("SESSION", "Process: " + session.getProcessId());
        info("SESSION", "Duration: " + BawAnalysisUtils.formatDuration(totalDuration));
        info("SESSION", "Artifacts Processed: " + session.getProcessedArtifacts());
        info("SESSION", "Warnings: " + warnings.size());
        info("SESSION", "Errors: " + errors.size());

        if (enableDetailedLogging && !session.getPerformanceMetrics().isEmpty()) {
            info("SESSION", "Performance Breakdown:");
            for (Map.Entry<String, Long> entry : session.getPerformanceMetrics().entrySet()) {
                info("SESSION", String.format("  %s: %s",
                        entry.getKey(), BawAnalysisUtils.formatDuration(entry.getValue())));
            }
        }

        if (!warnings.isEmpty()) {
            info("SESSION", "Warnings Summary:");
            for (int i = 0; i < Math.min(5, warnings.size()); i++) {
                info("SESSION", "  - " + warnings.get(i));
            }
            if (warnings.size() > 5) {
                info("SESSION", String.format("  ... and %d more warnings", warnings.size() - 5));
            }
        }

        if (!errors.isEmpty()) {
            info("SESSION", "Errors Summary:");
            for (String error : errors) {
                info("SESSION", "  - " + error);
            }
        }

        info("SESSION", "=== END SUMMARY ===");
    }

    // ========== CORE LOGGING METHOD ==========

    /**
     * Core logging method that handles all log output.
     */
    private void log(LogLevel level, String component, String message, Throwable throwable) {
        String timestamp = new SimpleDateFormat(LOG_DATE_FORMAT).format(new Date());
        String logEntry = String.format("[%s] [%s] [%s] %s", timestamp, level, component, message);

        // Write to configured output
        writer.println(logEntry);

        // Include stack trace for errors
        if (throwable != null) {
            throwable.printStackTrace(writer);
        }

        // Force flush for important messages
        if (level == LogLevel.ERROR || level == LogLevel.WARN) {
            writer.flush();
        }
    }

    // ========== CLEANUP ==========

    /**
     * Closes the logger and releases resources.
     */
    public void close() {
        try {
            endSession();
        } catch (Exception e) {
            // Ignore errors during cleanup
        }

        if (writer != null) {
            writer.close();
        }
    }

    // ========== GETTERS FOR SUMMARY ==========

    public List<String> getWarnings() { return Collections.unmodifiableList(warnings); }
    public List<String> getErrors() { return Collections.unmodifiableList(errors); }
    public String getSessionId() { return sessionId; }

    // ========== HELPER CLASSES ==========

    /**
     * Represents an analysis session with metadata and metrics.
     */
    public static class AnalysisSession {
        private final String sessionId;
        private String projectName;
        private String processId;
        private long startTime;
        private long endTime;
        private int processedArtifacts = 0;
        private final Map<String, Long> performanceMetrics = new HashMap<>();

        public AnalysisSession(String sessionId) {
            this.sessionId = sessionId;
        }

        // Getters and setters
        public String getSessionId() { return sessionId; }
        public String getProjectName() { return projectName; }
        public void setProjectName(String projectName) { this.projectName = projectName; }
        public String getProcessId() { return processId; }
        public void setProcessId(String processId) { this.processId = processId; }
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
        public int getProcessedArtifacts() { return processedArtifacts; }
        public void incrementProcessedArtifacts() { this.processedArtifacts++; }
        public Map<String, Long> getPerformanceMetrics() { return Collections.unmodifiableMap(performanceMetrics); }

        public void addPerformanceMetric(String name, long duration) {
            performanceMetrics.put(name, duration);
        }
    }

    /**
     * Log levels for different types of messages.
     */
    public enum LogLevel {
        DEBUG("DEBUG"),
        INFO("INFO "),
        PROGRESS("PROG "),
        MILESTONE("MILE "),
        WARN("WARN "),
        ERROR("ERROR");

        private final String displayName;
        LogLevel(String displayName) { this.displayName = displayName; }
        @Override public String toString() { return displayName; }
    }
}