package trainer;

import java.util.Random;

/**
 * Логика тренажёра: генерация примеров, проверка ответов, работа с диапазонами.
 */
public class MathTrainerLogic {
    private int a, b;               // текущие множители
    private int minA, maxA;         // диапазон для a
    private int minB, maxB;         // диапазон для b
    
    private Random random = new Random();
    
    // Конструктор: устанавливает диапазоны по умолчанию
    public MathTrainerLogic() {
        setDefaultRanges();
        generateNewExample(); // сразу генерируем пример
    }
    
    // Установка диапазонов по умолчанию: a=2..44, b=2..44
    public void setDefaultRanges() {
        this.minA = 2;
        this.maxA = 44;
        this.minB = 2;
        this.maxB = 44;
    }
    
    // Установка пользовательских диапазонов (с автоматической корректировкой min/max)
    public void setRanges(int minA, int maxA, int minB, int maxB) {
        // Корректировка: если min > max, меняем местами
        if (minA > maxA) {
            int temp = minA;
            minA = maxA;
            maxA = temp;
        }
        if (minB > maxB) {
            int temp = minB;
            minB = maxB;
            maxB = temp;
        }
        this.minA = minA;
        this.maxA = maxA;
        this.minB = minB;
        this.maxB = maxB;
    }
    
    // Генерация нового примера в текущих диапазонах
    public void generateNewExample() {
        a = random.nextInt(maxA - minA + 1) + minA;
        b = random.nextInt(maxB - minB + 1) + minB;
    }
    
    // Получить текст примера для отображения
    public String getExampleText() {
        return a + " x " + b + " =";
    }
    
    // Проверить ответ (строка от пользователя)
    public boolean checkAnswer(String answerText) {
        try {
            int answer = Integer.parseInt(answerText.trim());
            return answer == (a * b);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    // Получить правильный ответ (число) для подсказки
    public int getCorrectAnswer() {
        return a * b;
    }
    
    // Геттеры для диапазонов (нужны для отображения в интерфейсе)
    public int getMinA() { return minA; }
    public int getMaxA() { return maxA; }
    public int getMinB() { return minB; }
    public int getMaxB() { return maxB; }
}