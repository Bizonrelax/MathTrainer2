package trainer;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

public class CalculatorFrame extends JFrame {
    // Модель логики
    private MathTrainerLogic logic;
    
    // Компоненты интерфейса
    private JTextField exampleField;
    private JTextField answerField;
    private JTextField hintField;
    private JTextField messageField;
    
    private JTextField minAField, maxAField, minBField, maxBField;
    private JTextField fontSizeField;
    
    // Секундомер
    private JLabel stopwatchLabel;
    private Timer stopwatchTimer;
    private double stopwatchSeconds = 0.0;
    private boolean stopwatchRunning = false;
    
    // Кнопки (некоторые понадобятся для управления фокусом)
    private JButton nextButton;
    
    private int currentFontSize = 18;
    
    public CalculatorFrame() {
        logic = new MathTrainerLogic();
        initUI();
        applyFontSize();
        updateExampleDisplay();
        updateRangesFromLogic();
        setFocusToAnswer();
        showMessage("Тренажёр запущен. Диапазоны по умолчанию 2–44 / 2–44");
        
        // Создаём таймер секундомера (обновление каждые 100 мс)
        stopwatchTimer = new Timer(100, e -> updateStopwatchDisplay());
    }
    
    private void initUI() {
        setTitle("Тренажёр устного счёта — умножение");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        setContentPane(mainPanel);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // ---- Ячейка примера ----
        exampleField = new JTextField(20);
        exampleField.setEditable(false);
        exampleField.setHorizontalAlignment(JTextField.CENTER);
        exampleField.setFont(new Font("Monospaced", Font.BOLD, currentFontSize));
        exampleField.setBorder(new LineBorder(Color.BLACK, 2));
        exampleField.setBackground(Color.WHITE);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(exampleField, gbc);
        
        // ---- Поле ввода ответа ----
        answerField = new JTextField(10);
        answerField.setHorizontalAlignment(JTextField.CENTER);
        answerField.setFont(new Font("Monospaced", Font.PLAIN, currentFontSize));
        answerField.getDocument().addDocumentListener(new AnswerDocumentListener());
        
        gbc.gridy = 1;
        mainPanel.add(answerField, gbc);
        
        // ---- Кнопки управления ----
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton startButton = new JButton("Старт");
        nextButton = new JButton("Следующий пример");
        JButton hintButton = new JButton("Подсказка");
        JButton defaultRangesButton = new JButton("мин/макс по умолчанию");
        JButton stopStopwatchButton = new JButton("Стоп");
        
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
        
        // Делаем кнопку "Следующий пример" реагирующей на Enter
        getRootPane().setDefaultButton(nextButton);
        
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);
        
        // ---- Панель диапазонов ----
        JPanel rangesPanel = new JPanel(new GridLayout(2, 4, 10, 5));
        rangesPanel.setBackground(Color.WHITE);
        rangesPanel.setBorder(BorderFactory.createTitledBorder("Диапазоны чисел"));
        
        rangesPanel.add(new JLabel("min a:", SwingConstants.RIGHT));
        minAField = new JTextField(5);
        rangesPanel.add(minAField);
        rangesPanel.add(new JLabel("max a:", SwingConstants.RIGHT));
        maxAField = new JTextField(5);
        rangesPanel.add(maxAField);
        
        rangesPanel.add(new JLabel("min b:", SwingConstants.RIGHT));
        minBField = new JTextField(5);
        rangesPanel.add(minBField);
        rangesPanel.add(new JLabel("max b:", SwingConstants.RIGHT));
        maxBField = new JTextField(5);
        rangesPanel.add(maxBField);
        
        gbc.gridy = 3;
        mainPanel.add(rangesPanel, gbc);
        
        // ---- Панель шрифта + секундомер + подсказка ----
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        bottomPanel.setBackground(Color.WHITE);
        
        // Блок шрифта
        JPanel fontPanel = new JPanel(new FlowLayout());
        fontPanel.setBackground(Color.WHITE);
        fontPanel.add(new JLabel("Размер шрифта:"));
        fontSizeField = new JTextField(String.valueOf(currentFontSize), 4);
        fontSizeField.setHorizontalAlignment(JTextField.CENTER);
        JButton fontPlus = new JButton("+");
        JButton fontMinus = new JButton("-");
        
        fontPlus.addActionListener(e -> changeFontSize(+1));
        fontMinus.addActionListener(e -> changeFontSize(-1));
        fontSizeField.addActionListener(e -> applyFontSizeFromField());
        fontSizeField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { applyFontSizeFromField(); }
        });
        
        fontPanel.add(fontSizeField);
        fontPanel.add(fontPlus);
        fontPanel.add(fontMinus);
        
        // Секундомер
        stopwatchLabel = new JLabel("0.0 с");
        stopwatchLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        stopwatchLabel.setVisible(false);  // изначально не виден
        
        // Подсказка
        hintField = new JTextField(10);
        hintField.setEditable(false);
        hintField.setHorizontalAlignment(JTextField.CENTER);
        hintField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        hintField.setBackground(Color.LIGHT_GRAY);
        
        bottomPanel.add(fontPanel);
        bottomPanel.add(stopwatchLabel);
        bottomPanel.add(new JLabel("Подсказка:"));
        bottomPanel.add(hintField);
        
        gbc.gridy = 4;
        mainPanel.add(bottomPanel, gbc);
        
        // ---- Поле сообщений ----
        messageField = new JTextField();
        messageField.setEditable(false);
        messageField.setBackground(new Color(240, 240, 240));
        messageField.setFont(new Font("SansSerif", Font.ITALIC, 12));
        
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(messageField, gbc);
        
        // Ctrl+N для следующего примера (оставляем)
        getRootPane().registerKeyboardAction(e -> generateNextExample(),
                KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
    
    // ==================== Секундомер ====================
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
            stopwatchRunning = false;
            showMessage("Секундомер остановлен: " + formatStopwatchTime());
        }
    }
    
    private void updateStopwatchDisplay() {
        if (stopwatchRunning) {
            stopwatchSeconds += 0.1;
            stopwatchLabel.setText(formatStopwatchTime());
        }
    }
    
    private String formatStopwatchTime() {
        // Формат: "X.X с" (одна десятая)
        return String.format("%.1f с", stopwatchSeconds);
    }
    
    // ==================== Логика сессии ====================
    private void startNewSession() {
        // Сброс и запуск секундомера
        startStopwatch();
        // Генерируем новый пример с текущими диапазонами
        readRangesFromFieldsAndApply();
        logic.generateNewExample();
        resetAfterNewExample();
        showMessage("Новая сессия! Пример: " + logic.getExampleText());
    }
    
    // ==================== Исправление размеров полей при смене шрифта ====================
    private void updateFieldSizes() {
        // Пример: максимальная длина строки "999 x 999 ="
        FontMetrics fmExample = exampleField.getFontMetrics(exampleField.getFont());
        int exampleWidth = fmExample.stringWidth("999 x 999 =") + 20; // запас
        Dimension exampleDim = new Dimension(exampleWidth, exampleField.getPreferredSize().height);
        exampleField.setPreferredSize(exampleDim);
        
        // Поле ответа: максимум 5-6 цифр (произведение до 44*44=1936)
        FontMetrics fmAnswer = answerField.getFontMetrics(answerField.getFont());
        int answerWidth = fmAnswer.stringWidth("99999") + 20;
        Dimension answerDim = new Dimension(answerWidth, answerField.getPreferredSize().height);
        answerField.setPreferredSize(answerDim);
        
        // Обновляем layout
        revalidate();
        repaint();
    }
    
    // ==================== Остальные методы (без изменений, но некоторые дополнены) ====================
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
        if (answerField.isEditable()) {
            answerField.requestFocusInWindow();
        } else {
            // Если поле заблокировано, фокус на кнопку "Следующий пример"
            nextButton.requestFocusInWindow();
        }
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
    
    private void generateNextExample() {
        readRangesFromFieldsAndApply();
        logic.generateNewExample();
        resetAfterNewExample();
        showMessage("Следующий пример: " + logic.getExampleText());
    }
    
    private void resetAfterNewExample() {
        updateExampleDisplay();
        answerField.setEditable(true);
        answerField.setText("");
        answerField.setBackground(Color.WHITE);
        hintField.setText("");
        // После генерации нового примера фокус всегда на поле ответа (оно разблокировано)
        answerField.requestFocusInWindow();
    }
    
    private void setDefaultRangesAndUpdate() {
        logic.setDefaultRanges();
        updateRangesFromLogic();
        showMessage("Установлены диапазоны по умолчанию: a=2..44, b=2..44");
    }
    
    private void showHint() {
        int correct = logic.getCorrectAnswer();
        hintField.setText(String.valueOf(correct));
    }
    
    // DocumentListener для проверки ответа
    private class AnswerDocumentListener implements DocumentListener {
        private void check() {
            String text = answerField.getText();
            boolean correct = logic.checkAnswer(text);
            if (correct && !text.isEmpty()) {
                answerField.setBackground(Color.GREEN);
                answerField.setEditable(false);
                // Фокус на кнопку "Следующий пример" (чтобы нажать Пробел/Enter)
                nextButton.requestFocusInWindow();
                showMessage("Правильно! Нажмите «Следующий пример» (Пробел или Enter).");
            } else {
                if (answerField.isEditable()) {
                    answerField.setBackground(Color.WHITE);
                }
            }
        }
        @Override public void insertUpdate(DocumentEvent e) { check(); }
        @Override public void removeUpdate(DocumentEvent e) { check(); }
        @Override public void changedUpdate(DocumentEvent e) { check(); }
    }
    
    // Изменение шрифта
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
        // Важно: пересчитываем размеры полей после смены шрифта
        updateFieldSizes();
        showMessage("Размер шрифта изменён на " + size);
    }
    
    private void applyFontSize() {
        Font newFont = new Font("Monospaced", Font.BOLD, currentFontSize);
        exampleField.setFont(newFont);
        Font answerFont = new Font("Monospaced", Font.PLAIN, currentFontSize);
        answerField.setFont(answerFont);
        // Шрифт секундомера не меняем, он фиксированный 24
    }
}