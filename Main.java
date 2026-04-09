package trainer;

public class Main {
    public static void main(String[] args) {
        // Запуск графического интерфейса в потоке обработки событий
        javax.swing.SwingUtilities.invokeLater(() -> {
            new CalculatorFrame().setVisible(true);
        });
    }
}