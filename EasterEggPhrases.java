package trainer;

import java.util.Random;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JOptionPane;

public class EasterEggPhrases {
    private static final String[] PHRASES = {
        "Бомбарда Максима",
        "Полтергейст",
        "Ресницы",
        "Гарри и Джинни",
        "Кром",
        "Валгалла",
        "Конан",
        "Оберон",
        "Марс",
        "Саймак",
        "Герой"
    };
    private static final Random RAND = new Random();

    public static String getRandomPhrase() {
return PHRASES[RAND.nextInt(PHRASES.length)];
    }
}