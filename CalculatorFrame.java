package trainer;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;

public class CalculatorFrame extends JFrame {
    private MathTrainerLogic logic;

    private JTextField exampleField;
    private JTextField answerField;
    private JTextField hintField;
    private JTextField messageField;

    private JTextField minAField, maxAField, minBField, maxBField;
    private JTextField fontSizeField;

    private JLabel stopwatchLabel;
    private Timer stopwatchTimer;
    private double stopwatchSeconds = 0.0;
    private boolean stopwatchRunning = false;

    private JButton nextButton;

    private JTextField normField;
    private JTextField resultField;
    private JPanel indicatorPanel;
    private JTextField[] lastExamples;
    private LinkedList<String> recentHistory = new LinkedList<>();

    private int solvedCount = 0;
    private int currentFontSize;
    private long appStartTime = System.currentTimeMillis();

    public CalculatorFrame() {
        logic = new MathTrainerLogic();
        AppConfig.load();
        AppConfig.incrementStarts();

        // Восстанавливаем диапазоны из конфига
        logic.setRanges(AppConfig.getMinA(), AppConfig.getMaxA(),
                        AppConfig.getMinB(), AppConfig.getMaxB());
        logic.generateNewExample();

        currentFontSize = AppConfig.getFontSize();

        AppConfig.incrementGenerated();
        AppConfig.addToSumMultipliers(logic.getA() + logic.getB());

        // === ВСЕГДА задаём базовый размер для кнопки "Уменьшить" ===
        setSize(1200, 750);

        initUI();
        applyFontSize();
        updateExampleDisplay();
        updateRangesFromLogic();
        setFocusToAnswer();
        loadNormFromConfig();
        updateResultDisplay();
        updateIndicatorColor();
        showMessage("Тренажёр запущен. Диапазоны: " + logic.getMinA() + "–" + logic.getMaxA()
                    + " / " + logic.getMinB() + "–" + logic.getMaxB());

        stopwatchTimer = new Timer(100, e -> updateStopwatchDisplay());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopStopwatchSilent();

                // Сохраняем состояние максимизации и размеры
                if ((getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0) {
                    AppConfig.setWindowMaximized(true);
                    // нормальные размеры не трогаем – они уже сохранены, когда окно было не развёрнуто
                } else {
                    AppConfig.setWindowMaximized(false);
                    Rectangle bounds = getBounds();
                    AppConfig.saveWindowBounds(bounds.x, bounds.y, bounds.width, bounds.height);
                }
                // Диапазоны
                AppConfig.saveRanges(logic.getMinA(), logic.getMaxA(), logic.getMinB(), logic.getMaxB());

                long now = System.currentTimeMillis();
                double sessionMinutes = (now - appStartTime) / 60000.0;
                AppConfig.setLastSessionDurationMinutes(sessionMinutes);
                AppConfig.addTotalUptimeHours(sessionMinutes / 60.0);
                AppConfig.save();
            }
        });

        ToolTipManager.sharedInstance().setDismissDelay(8000);

        // Восстановление размера/позиции окна при старте
        if (AppConfig.isWindowMaximized()) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            int wx = AppConfig.getWindowX();
            int wy = AppConfig.getWindowY();
            int ww = AppConfig.getWindowWidth();
            int wh = AppConfig.getWindowHeight();
            if (wx >= 0 && wy >= 0) {
                setBounds(wx, wy, ww, wh);
            } else {
                // координаты не сохранены – используем текущий размер (1200x750) и центрируем
                setLocationRelativeTo(null);
            }
        }
    }

    private void initUI() {
        setTitle("Тренажёр устного счёта — умножение");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        setContentPane(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Пример
        exampleField = new JTextField(20);
        exampleField.setEditable(false);
        exampleField.setHorizontalAlignment(JTextField.CENTER);
        exampleField.setFont(new Font("Monospaced", Font.BOLD, currentFontSize));
        exampleField.setBorder(new LineBorder(Color.BLACK, 2));
        exampleField.setBackground(Color.WHITE);
        exampleField.setToolTipText("Текущий пример");

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(exampleField, gbc);

        // Поле ввода ответа
        answerField = new JTextField(10);
        answerField.setHorizontalAlignment(JTextField.CENTER);
        answerField.setFont(new Font("Monospaced", Font.PLAIN, currentFontSize));
        answerField.setToolTipText("Введи ответ и нажми Enter");
        ((AbstractDocument) answerField.getDocument()).setDocumentFilter(new NumericDocumentFilter());
        answerField.getDocument().addDocumentListener(new AnswerDocumentListener());

        gbc.gridy = 1; gbc.gridwidth = 2;
        mainPanel.add(answerField, gbc);

        // Кнопки управления
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 5));
        buttonPanel.setBackground(Color.WHITE);

        JButton startButton = new JButton("Старт");
        startButton.setToolTipText("Запустить секундомер и новый пример");
        nextButton = new JButton("Следующий пример");
        nextButton.setToolTipText("Сгенерировать следующий пример (Ctrl+N)");
        JButton hintButton = new JButton("Подсказка");
        hintButton.setToolTipText("Показать правильный ответ");
        JButton defaultRangesButton = new JButton("мин/макс по умолчанию");
        defaultRangesButton.setToolTipText("Вернуть диапазоны 2–44");
        JButton stopStopwatchButton = new JButton("Стоп");
        stopStopwatchButton.setToolTipText("Остановить секундомер");

        startButton.addActionListener(e -> startNewSession());
        nextButton.addActionListener(e -> generateNextExample());
        hintButton.addActionListener(e -> showHint());
        defaultRangesButton.addActionListener(e -> setDefaultRangesAndUpdate());
        stopStopwatchButton.addActionListener(e -> stopStopwatch());

        buttonPanel.add(startButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(hintButton);
        buttonPanel.add(defaultRangesButton);
        buttonPanel.add(stopStopwatchButton);

        getRootPane().setDefaultButton(nextButton);

        gbc.gridy = 2; gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        // Диапазоны
        JPanel rangesPanel = new JPanel(new GridLayout(2, 4, 10, 5));
        rangesPanel.setBackground(Color.WHITE);
        rangesPanel.setBorder(BorderFactory.createTitledBorder("Диапазоны чисел"));

        rangesPanel.add(new JLabel("min a:", SwingConstants.RIGHT));
        minAField = new JTextField(5);
        minAField.setToolTipText("Минимальное значение a");
        rangesPanel.add(minAField);
        rangesPanel.add(new JLabel("max a:", SwingConstants.RIGHT));
        maxAField = new JTextField(5);
        maxAField.setToolTipText("Максимальное значение a");
        rangesPanel.add(maxAField);

        rangesPanel.add(new JLabel("min b:", SwingConstants.RIGHT));
        minBField = new JTextField(5);
        minBField.setToolTipText("Минимальное значение b");
        rangesPanel.add(minBField);
        rangesPanel.add(new JLabel("max b:", SwingConstants.RIGHT));
        maxBField = new JTextField(5);
        maxBField.setToolTipText("Максимальное значение b");
        rangesPanel.add(maxBField);

        gbc.gridy = 3;
        mainPanel.add(rangesPanel, gbc);

        // Норма, результат, индикатор
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        statsPanel.setBackground(Color.WHITE);

        normField = new JTextField(5);
        normField.setHorizontalAlignment(JTextField.CENTER);
        normField.setToolTipText("Норма (цель) решённых примеров");
        normField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) { saveNormFromField(); }
        });
        statsPanel.add(new JLabel("Норма:"));
        statsPanel.add(normField);

        resultField = new JTextField(8);
        resultField.setEditable(false);
        resultField.setHorizontalAlignment(JTextField.CENTER);
        resultField.setToolTipText("Счётчик правильно решённых примеров");
        statsPanel.add(new JLabel("Результат:"));
        statsPanel.add(resultField);

        JButton resetCounterBtn = new JButton("Сбросить счётчик");
        resetCounterBtn.setToolTipText("Обнулить счётчик результатов");
        resetCounterBtn.addActionListener(e -> resetCounter());
        statsPanel.add(resetCounterBtn);

        indicatorPanel = new JPanel();
        indicatorPanel.setPreferredSize(new Dimension(40, 30));
        indicatorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        indicatorPanel.setToolTipText("Цвет: голубой=0, жёлтый<нормы, зелёный>=нормы");
        statsPanel.add(indicatorPanel);

        gbc.gridy = 4;
        mainPanel.add(statsPanel, gbc);

        // Последние примеры (вертикально)
        JPanel lastPanel = new JPanel();
        lastPanel.setBackground(Color.WHITE);
        lastPanel.setBorder(BorderFactory.createTitledBorder("Последние примеры"));
        lastPanel.setLayout(new BoxLayout(lastPanel, BoxLayout.Y_AXIS));
        lastExamples = new JTextField[3];
        Font lastFont = new Font("Monospaced", Font.PLAIN, 24);
        for (int i = 0; i < 3; i++) {
            lastExamples[i] = new JTextField(20);
            lastExamples[i].setEditable(false);
            lastExamples[i].setHorizontalAlignment(JTextField.CENTER);
            lastExamples[i].setFont(lastFont);
            lastExamples[i].setMaximumSize(new Dimension(Short.MAX_VALUE, lastExamples[i].getPreferredSize().height));
            lastExamples[i].setToolTipText("Последний решённый пример");
            lastPanel.add(lastExamples[i]);
        }
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(lastPanel, gbc);
        gbc.fill = GridBagConstraints.NONE;

        // Шрифт, секундомер, подсказка
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 5));
        bottomPanel.setBackground(Color.WHITE);

        JPanel fontPanel = new JPanel(new FlowLayout());
        fontPanel.setBackground(Color.WHITE);
        fontPanel.add(new JLabel("Размер шрифта:"));
        fontSizeField = new JTextField(String.valueOf(currentFontSize), 4);
        fontSizeField.setHorizontalAlignment(JTextField.CENTER);
        fontSizeField.setToolTipText("Укажи размер шрифта");
        JButton fontPlus = new JButton("+");
        fontPlus.setToolTipText("Увеличить шрифт");
        JButton fontMinus = new JButton("-");
        fontMinus.setToolTipText("Уменьшить шрифт");
        fontPlus.addActionListener(e -> changeFontSize(+1));
        fontMinus.addActionListener(e -> changeFontSize(-1));
        fontSizeField.addActionListener(e -> applyFontSizeFromField());
        fontSizeField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { applyFontSizeFromField(); }
        });
        fontPanel.add(fontSizeField);
        fontPanel.add(fontPlus);
        fontPanel.add(fontMinus);

        stopwatchLabel = new JLabel("0.0 с");
        stopwatchLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        stopwatchLabel.setVisible(false);
        stopwatchLabel.setToolTipText("Секундомер (запускается кнопкой Старт)");

        hintField = new JTextField(10);
        hintField.setEditable(false);
        hintField.setHorizontalAlignment(JTextField.CENTER);
        hintField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        hintField.setBackground(Color.LIGHT_GRAY);
        hintField.setToolTipText("Подсказка правильного ответа");

        bottomPanel.add(fontPanel);
        bottomPanel.add(stopwatchLabel);
        bottomPanel.add(new JLabel("Подсказка:"));
        bottomPanel.add(hintField);

        gbc.gridy = 6;
        mainPanel.add(bottomPanel, gbc);

        // Поле сообщений
        messageField = new JTextField();
        messageField.setEditable(false);
        messageField.setBackground(new Color(240, 240, 240));
        messageField.setFont(new Font("SansSerif", Font.ITALIC, 12));
        messageField.setToolTipText("Сообщения программы");
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(messageField, gbc);

        // Нижние кнопки
        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        actionButtonsPanel.setBackground(Color.WHITE);

        JButton aboutBtn = new JButton("О Пр");
        aboutBtn.setToolTipText("Информация о программе");
        aboutBtn.addActionListener(e -> new AboutDialog(this).setVisible(true));

        JButton statsBtn = new JButton("Стат");
        statsBtn.setToolTipText("Показать статистику");
        statsBtn.addActionListener(e -> new StatsDialog(this).setVisible(true));

        JButton filesBtn = new JButton("📂 Файлы");
        filesBtn.setToolTipText("Открыть папку с файлами сохранения");
        filesBtn.addActionListener(e -> openConfigFolder());

        actionButtonsPanel.add(aboutBtn);
        actionButtonsPanel.add(statsBtn);
        actionButtonsPanel.add(filesBtn);

        gbc.gridy = 8;
        mainPanel.add(actionButtonsPanel, gbc);

        // Горячие клавиши
        getRootPane().registerKeyboardAction(e -> generateNextExample(),
                KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    // ==================== ФИЛЬТР ЦИФР ====================
    private class NumericDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (string == null) return;
            String filtered = string.replaceAll("[^0-9]", "");
            if (!filtered.isEmpty()) {
                super.insertString(fb, offset, filtered, attr);
            }
        }
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (text == null) return;
            String filtered = text.replaceAll("[^0-9]", "");
            super.replace(fb, offset, length, filtered, attrs);
        }
    }

    // ==================== СЕКУНДОМЕР ====================
    private void startStopwatch() {
        stopwatchSeconds = 0.0;
        updateStopwatchDisplay();
        stopwatchLabel.setVisible(true);
        if (stopwatchTimer.isRunning()) stopwatchTimer.stop();
        stopwatchTimer.start();
        stopwatchRunning = true;
    }

    private void stopStopwatch() {
        if (stopwatchTimer.isRunning()) {
            stopwatchTimer.stop();
            double time = stopwatchSeconds;
            stopwatchRunning = false;
            showMessage("Секундомер остановлен: " + formatStopwatchTime());
            AppConfig.addToTotalTime(time);
            AppConfig.setLastExerciseTime(time);
            AppConfig.save();
        }
    }

    private void stopStopwatchSilent() {
        if (stopwatchRunning) {
            stopwatchTimer.stop();
            stopwatchRunning = false;
            AppConfig.addToTotalTime(stopwatchSeconds);
            AppConfig.setLastExerciseTime(stopwatchSeconds);
        }
    }

    private void updateStopwatchDisplay() {
        if (stopwatchRunning) {
            stopwatchSeconds += 0.1;
            stopwatchLabel.setText(formatStopwatchTime());
        }
    }

    private String formatStopwatchTime() {
        return String.format("%.1f с", stopwatchSeconds);
    }

    // ==================== СЕССИЯ И ПРИМЕРЫ ====================
    private void startNewSession() {
        startStopwatch();
        readRangesFromFieldsAndApply();
        logic.generateNewExample();
        AppConfig.incrementGenerated();
        AppConfig.addToSumMultipliers(logic.getA() + logic.getB());
        resetAfterNewExample();
        showMessage("Новая сессия! Пример: " + logic.getExampleText());
    }

    private void generateNextExample() {
        readRangesFromFieldsAndApply();
        logic.generateNewExample();
        AppConfig.incrementGenerated();
        AppConfig.addToSumMultipliers(logic.getA() + logic.getB());
        resetAfterNewExample();
        showMessage("Следующий пример: " + logic.getExampleText());
    }

    private void autoAdvanceAfterCorrect() {
        readRangesFromFieldsAndApply();
        logic.generateNewExample();
        AppConfig.incrementGenerated();
        AppConfig.addToSumMultipliers(logic.getA() + logic.getB());
        answerField.setText("");
        answerField.setBackground(Color.WHITE);
        answerField.setEditable(true);
        hintField.setText("");
        answerField.requestFocusInWindow();
        updateExampleDisplay();
    }

    private void resetAfterNewExample() {
        updateExampleDisplay();
        answerField.setEditable(true);
        answerField.setText("");
        answerField.setBackground(Color.WHITE);
        hintField.setText("");
        answerField.requestFocusInWindow();
    }

    private void setDefaultRangesAndUpdate() {
        logic.setDefaultRanges();
        updateRangesFromLogic();
        showMessage("Установлены диапазоны по умолчанию: a=2..44, b=2..44");
    }

    private void showHint() {
        hintField.setText(String.valueOf(logic.getCorrectAnswer()));
    }

    // ==================== НОРМА, СЧЁТЧИК, ПАСХАЛКА ====================
    private void loadNormFromConfig() {
        normField.setText(String.valueOf(AppConfig.getNorm()));
    }

    private void saveNormFromField() {
        String text = normField.getText().trim();
        try {
            int val = Integer.parseInt(text);
            if (val > 0) {
                AppConfig.setNorm(val);
                AppConfig.save();
                updateIndicatorColor();
                return;
            }
        } catch (NumberFormatException ignored) {}
        normField.setText(String.valueOf(AppConfig.getNorm()));
    }

    private void resetCounter() {
        solvedCount = 0;
        updateResultDisplay();
        updateIndicatorColor();
        showMessage("Счётчик сброшен.");
    }

    private void updateResultDisplay() {
        resultField.setText(String.valueOf(solvedCount));
    }

    private void updateIndicatorColor() {
        int norm = AppConfig.getNorm();
        if (solvedCount == 0) {
            indicatorPanel.setBackground(Color.CYAN);
        } else if (solvedCount < norm) {
            indicatorPanel.setBackground(new Color(255, 255, 150));
        } else {
            indicatorPanel.setBackground(Color.GREEN);
        }
    }

    private void addToRecentHistory(String entry) {
        if (recentHistory.size() == 3) {
            recentHistory.removeFirst();
        }
        recentHistory.addLast(entry);
        updateLastExampleFields();
    }

    private void updateLastExampleFields() {
        for (int i = 0; i < 3; i++) {
            if (i < recentHistory.size()) {
                lastExamples[i].setText(recentHistory.get(i));
            } else {
                lastExamples[i].setText("");
            }
        }
    }

    private boolean tryEasterEgg() {
        int a = logic.getA();
        int b = logic.getB();
        LocalDate now = LocalDate.now();
        LocalTime time = LocalTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        int hour = time.getHour();

        int ones = countOnes(a) + countOnes(b) + countOnes(year) + countOnes(month) + countOnes(day) + countOnes(hour);
        if (ones > 8) {
            String phrase = EasterEggPhrases.getRandomPhrase();
            addToRecentHistory(phrase);
            showMessage("✨ Пасхалка! " + phrase);
            return true;
        }
        return false;
    }

    private int countOnes(int number) {
        int count = 0;
        for (char c : String.valueOf(number).toCharArray()) {
            if (c == '1') count++;
        }
        return count;
    }

    // ==================== ПРОВЕРКА ОТВЕТА ====================
    private class AnswerDocumentListener implements DocumentListener {
        private void check() {
            String text = answerField.getText();
            boolean correct = logic.checkAnswer(text);
            if (correct && !text.isEmpty() && answerField.isEditable()) {
                boolean easter = tryEasterEgg();
                if (!easter) {
                    String example = logic.getExampleText().replace("=", "= " + text.trim());
                    addToRecentHistory(example);
                }

                int answerValue = logic.getCorrectAnswer();
                AppConfig.addToSumAnswers(answerValue);
                AppConfig.updateMaxCorrectAnswer(answerValue);
                AppConfig.updateMaxMultiplier(Math.max(logic.getA(), logic.getB()));
                AppConfig.incrementSolved();

                solvedCount++;
                updateResultDisplay();
                updateIndicatorColor();

                if (stopwatchRunning) {
                    SwingUtilities.invokeLater(() -> autoAdvanceAfterCorrect());
                    showMessage("Правильно!");
                } else {
                    answerField.setEditable(false);
                    answerField.setBackground(Color.GREEN);
                    nextButton.requestFocusInWindow();
                    showMessage("Правильно! Нажмите «Следующий пример» (Пробел или Enter).");
                }
            } else if (!answerField.isEditable()) {
                // поле заблокировано
            } else {
                answerField.setBackground(Color.WHITE);
            }
        }

        @Override public void insertUpdate(DocumentEvent e) { check(); }
        @Override public void removeUpdate(DocumentEvent e) { check(); }
        @Override public void changedUpdate(DocumentEvent e) { check(); }
    }

    // ==================== ПРОЧИЕ UI-МЕТОДЫ ====================
    private void updateExampleDisplay() {
        exampleField.setText(logic.getExampleText());
    }

    private void updateRangesFromLogic() {
        minAField.setText(String.valueOf(logic.getMinA()));
        maxAField.setText(String.valueOf(logic.getMaxA()));
        minBField.setText(String.valueOf(logic.getMinB()));
        maxBField.setText(String.valueOf(logic.getMaxB()));
    }

    private void setFocusToAnswer() {
        answerField.requestFocusInWindow();
    }

    private void showMessage(String msg) {
        messageField.setText(msg);
    }

    private void readRangesFromFieldsAndApply() {
        try {
            int minA = parseIntOrDefault(minAField.getText(), logic.getMinA());
            int maxA = parseIntOrDefault(maxAField.getText(), logic.getMaxA());
            int minB = parseIntOrDefault(minBField.getText(), logic.getMinB());
            int maxB = parseIntOrDefault(maxBField.getText(), logic.getMaxB());
            logic.setRanges(minA, maxA, minB, maxB);
            updateRangesFromLogic();
        } catch (NumberFormatException e) {
            showMessage("Ошибка в диапазонах. Использованы предыдущие значения.");
        }
    }

    private int parseIntOrDefault(String text, int defaultValue) {
        if (text == null || text.trim().isEmpty()) return defaultValue;
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void updateFieldSizes() {
        Font exampleFont = exampleField.getFont();
        FontMetrics fmEx = exampleField.getFontMetrics(exampleFont);
        int exampleWidth = fmEx.stringWidth("999 x 999 =") + 20;
        int exampleHeight = fmEx.getHeight() + 10;

        Font answerFont = answerField.getFont();
        FontMetrics fmAns = answerField.getFontMetrics(answerFont);
        int answerWidth = fmAns.stringWidth("99999") + 20;
        int answerHeight = fmAns.getHeight() + 10;

        exampleField.setPreferredSize(new Dimension(exampleWidth, exampleHeight));
        answerField.setPreferredSize(new Dimension(answerWidth, answerHeight));

        revalidate();
        repaint();
    }

    private void changeFontSize(int delta) {
        int newSize = currentFontSize + delta;
        if (newSize < 1) newSize = 1;
        setFontSize(newSize);
    }

    private void applyFontSizeFromField() {
        try {
            int newSize = Integer.parseInt(fontSizeField.getText().trim());
            if (newSize < 1) throw new NumberFormatException();
            setFontSize(newSize);
        } catch (NumberFormatException e) {
            showMessage("Некорректный размер шрифта. Оставлен предыдущий.");
            fontSizeField.setText(String.valueOf(currentFontSize));
        }
    }

    private void setFontSize(int size) {
        this.currentFontSize = size;
        fontSizeField.setText(String.valueOf(size));
        applyFontSize();
        updateFieldSizes();
        AppConfig.setFontSize(size);
        AppConfig.save();
        showMessage("Размер шрифта изменён на " + size);
    }

    private void applyFontSize() {
        Font newFont = new Font("Monospaced", Font.BOLD, currentFontSize);
        exampleField.setFont(newFont);
        Font answerFont = new Font("Monospaced", Font.PLAIN, currentFontSize);
        answerField.setFont(answerFont);
    }

    private void openConfigFolder() {
        try {
            Desktop.getDesktop().open(new java.io.File(AppConfig.getConfigFolderPath()));
        } catch (Exception e) {
            showMessage("Не удалось открыть папку: " + e.getMessage());
        }
    }
}