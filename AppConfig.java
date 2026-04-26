

package trainer;

import java.io.*;
import java.util.Properties;

public class AppConfig {
    private static final File CONFIG_DIR = new File(System.getProperty("user.home"), ".MathTrainer2");
    private static final File CONFIG_FILE = new File(CONFIG_DIR, "config.properties");
    private static final File STATS_FILE = new File(CONFIG_DIR, "statistics.properties");

    private static Properties config = new Properties();
    private static Properties stats = new Properties();

    public static void load() {
        CONFIG_DIR.mkdirs();
        loadProps(CONFIG_FILE, config);
        loadProps(STATS_FILE, stats);
    }

    public static void save() {
        saveProps(CONFIG_FILE, config, "MathTrainer2 config");
        saveProps(STATS_FILE, stats, "MathTrainer2 statistics");
    }

    // ---- Config ----
    public static int getNorm() { return Integer.parseInt(config.getProperty("norm", "20")); }
    public static void setNorm(int norm) { config.setProperty("norm", String.valueOf(norm)); }

    public static int getFontSize() { return Integer.parseInt(config.getProperty("fontSize", "18")); }
    public static void setFontSize(int size) { config.setProperty("fontSize", String.valueOf(size)); }

    // Окно
    public static void saveWindowBounds(int x, int y, int width, int height) {
        config.setProperty("win.x", String.valueOf(x));
        config.setProperty("win.y", String.valueOf(y));
        config.setProperty("win.w", String.valueOf(width));
        config.setProperty("win.h", String.valueOf(height));
    }
    public static int getWindowX() { return Integer.parseInt(config.getProperty("win.x", "-1")); }
    public static int getWindowY() { return Integer.parseInt(config.getProperty("win.y", "-1")); }
    public static int getWindowWidth() { return Integer.parseInt(config.getProperty("win.w", "1200")); }
    public static int getWindowHeight() { return Integer.parseInt(config.getProperty("win.h", "720")); }

    public static boolean isWindowMaximized() {
        return Boolean.parseBoolean(config.getProperty("win.maximized", "true"));
    }
    public static void setWindowMaximized(boolean maximized) {
        config.setProperty("win.maximized", String.valueOf(maximized));
    }







    // Диапазоны
    public static void saveRanges(int minA, int maxA, int minB, int maxB) {
        config.setProperty("minA", String.valueOf(minA));
        config.setProperty("maxA", String.valueOf(maxA));
        config.setProperty("minB", String.valueOf(minB));
        config.setProperty("maxB", String.valueOf(maxB));
    }
    public static int getMinA() { return Integer.parseInt(config.getProperty("minA", "2")); }
    public static int getMaxA() { return Integer.parseInt(config.getProperty("maxA", "44")); }
    public static int getMinB() { return Integer.parseInt(config.getProperty("minB", "2")); }
    public static int getMaxB() { return Integer.parseInt(config.getProperty("maxB", "44")); }

    
    // ---- Statistics ----
    public static long getTotalCorrectAnswersSum() {
        return Long.parseLong(stats.getProperty("sumAnswers", "0"));
    }
    public static void addToSumAnswers(long value) {
        long old = getTotalCorrectAnswersSum();
        stats.setProperty("sumAnswers", String.valueOf(old + value));
    }

    public static long getTotalGenerated() {
        return Long.parseLong(stats.getProperty("totalGenerated", "0"));
    }
    public static void incrementGenerated() {
        long old = getTotalGenerated();
        stats.setProperty("totalGenerated", String.valueOf(old + 1));
    }

    public static long getSumMultipliers() {
        return Long.parseLong(stats.getProperty("sumMultipliers", "0"));
    }
    public static void addToSumMultipliers(long value) {
        long old = getSumMultipliers();
        stats.setProperty("sumMultipliers", String.valueOf(old + value));
    }

    public static double getTotalExerciseTime() {
        return Double.parseDouble(stats.getProperty("totalTimeSec", "0.0"));
    }
    public static void addToTotalTime(double sec) {
        double old = getTotalExerciseTime();
        stats.setProperty("totalTimeSec", String.valueOf(old + sec));
    }

    public static long getTotalSolved() {
        return Long.parseLong(stats.getProperty("totalSolved", "0"));
    }
    public static void incrementSolved() {
        long old = getTotalSolved();
        stats.setProperty("totalSolved", String.valueOf(old + 1));
    }

    public static int getStartsCount() {
        return Integer.parseInt(stats.getProperty("starts", "0"));
    }
    public static void incrementStarts() {
        int old = getStartsCount();
        stats.setProperty("starts", String.valueOf(old + 1));
    }

    // Время последнего упражнения (секундомер)
    public static double getLastExerciseTime() {
        return Double.parseDouble(stats.getProperty("lastExerciseSec", "0.0"));
    }
    public static void setLastExerciseTime(double sec) {
        stats.setProperty("lastExerciseSec", String.valueOf(sec));
    }

    public static long getMaxCorrectAnswer() {
        return Long.parseLong(stats.getProperty("maxCorrectAnswer", "0"));
    }
    public static void updateMaxCorrectAnswer(long candidate) {
        long cur = getMaxCorrectAnswer();
        if (candidate > cur) {
            stats.setProperty("maxCorrectAnswer", String.valueOf(candidate));
        }
    }

    public static int getMaxMultiplier() {
        return Integer.parseInt(stats.getProperty("maxMultiplier", "0"));
    }
    public static void updateMaxMultiplier(int candidate) {
        int cur = getMaxMultiplier();
        if (candidate > cur) {
            stats.setProperty("maxMultiplier", String.valueOf(candidate));
        }
    }

    // Новые поля: длительность последней сессии (мин) и общее время работы (часы)
    public static double getLastSessionDurationMinutes() {
        return Double.parseDouble(stats.getProperty("lastSessionMin", "0.0"));
    }
    public static void setLastSessionDurationMinutes(double minutes) {
        stats.setProperty("lastSessionMin", String.valueOf(minutes));
    }

    public static double getTotalUptimeHours() {
        return Double.parseDouble(stats.getProperty("totalUptimeHours", "0.0"));
    }
    public static void addTotalUptimeHours(double hours) {
        double old = getTotalUptimeHours();
        stats.setProperty("totalUptimeHours", String.valueOf(old + hours));
    }

    // ---- Вспомогательные методы ----
    private static void loadProps(File file, Properties props) {
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                props.load(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void saveProps(File file, Properties props, String comment) {
        try (FileWriter writer = new FileWriter(file)) {
            props.store(writer, comment);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getConfigFolderPath() {
        return CONFIG_DIR.getAbsolutePath();
    }
}