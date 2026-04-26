package trainer;

import java.util.Random;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JOptionPane;

public class MathTrainerLogic {
    private int a, b;
    private int minA, maxA;
    private int minB, maxB;
    private Random random = new Random();

    public MathTrainerLogic() {
        setDefaultRanges();
        generateNewExample();
    }

    public void setDefaultRanges() {
this.minA = 2;
        this.maxA = 44;
        this.minB = 2;
        this.maxB = 44;
    }

    public void setRanges(int minA, int maxA, int minB, int maxB) {
if (minA > maxA) { int t = minA; minA = maxA; maxA = t; }
        if (minB > maxB) { int t = minB; minB = maxB; maxB = t; }
        this.minA = minA;
        this.maxA = maxA;
        this.minB = minB;
        this.maxB = maxB;
    }

    public void generateNewExample() {
a = random.nextInt(maxA - minA + 1) + minA;
        b = random.nextInt(maxB - minB + 1) + minB;
    }

    public String getExampleText() {
return a + " x " + b + " =";
    }

    public boolean checkAnswer(String answerText) {
try {
            int ans = Integer.parseInt(answerText.trim());
            return ans == (a * b);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public int getCorrectAnswer() {
return a * b;
    }

    // Новые геттеры для пасхалки
    public int getA() {
return a; }
    public int getB() {
return b; }

    public int getMinA() {
return minA; }
    public int getMaxA() {
return maxA; }
    public int getMinB() {
return minB; }
    public int getMaxB() {
return maxB; }
}