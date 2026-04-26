package trainer;

import javax.swing.*;
import java.awt.*;

public class StatsDialog extends JDialog {
    private JTextArea textArea;
    private int fontSize = 16;

    public StatsDialog(Frame owner) {
        super(owner, "Статистика", false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        textArea = new JTextArea(16, 50);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, fontSize));
        updateStatsText();

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 420));

        JButton plusBtn = new JButton("+");
        plusBtn.addActionListener(e -> changeFontSize(2));
        JButton minusBtn = new JButton("-");
        minusBtn.addActionListener(e -> changeFontSize(-2));
        JButton refreshBtn = new JButton("Обновить");
        refreshBtn.addActionListener(e -> updateStatsText());
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(minusBtn);
        btnPanel.add(plusBtn);
        btnPanel.add(refreshBtn);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void updateStatsText() {
        long sumAnswers = AppConfig.getTotalCorrectAnswersSum();
        long totalGen = AppConfig.getTotalGenerated();
        long sumMultipliers = AppConfig.getSumMultipliers();
        double totalExerciseTime = AppConfig.getTotalExerciseTime();
        long totalSolved = AppConfig.getTotalSolved();
        int starts = AppConfig.getStartsCount();
        double lastExerciseTime = AppConfig.getLastExerciseTime();
        long maxAnswer = AppConfig.getMaxCorrectAnswer();
        int maxMult = AppConfig.getMaxMultiplier();
        double lastSessionMin = AppConfig.getLastSessionDurationMinutes();
        double totalUptimeHours = AppConfig.getTotalUptimeHours();

        String text = String.format("""
                ===== Статистика =====
                Сумма всех правильных ответов: %d
                Количество сгенерированных примеров: %d
                Сумма всех множителей: %d
                Длительность всех упражнений: %.1f с
                Решённых примеров: %d
                Запусков программы: %d
                Длительность последнего упражнения: %.1f с
                Время, затраченное на все упражнения: %.1f с
                Максимальный правильный ответ: %d
                Максимальный множитель: %d
                Длительность последней сессии: %.1f мин
                Общее время работы: %.1f ч
                """,
                sumAnswers, totalGen, sumMultipliers,
                totalExerciseTime, totalSolved, starts,
                lastExerciseTime, totalExerciseTime,
                maxAnswer, maxMult,
                lastSessionMin, totalUptimeHours);
        textArea.setText(text);
    }

    private void changeFontSize(int delta) {
        int newSize = fontSize + delta;
        if (newSize < 8) newSize = 8;
        fontSize = newSize;
        textArea.setFont(new Font("Monospaced", Font.PLAIN, fontSize));
    }
}