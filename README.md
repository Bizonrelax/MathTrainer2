# MathTrainer2 – Mental Math Trainer (Multiplication)

A simple but effective desktop application for practicing mental multiplication.  
Written in pure Java (Swing), no external dependencies.

## Features

- Customizable ranges for both multipliers
- **Dual mode**: with or without stopwatch  
  - **Untimed**: correct answer blocks the field (green), wait for "Next" button  
  - **Timed**: correct answer instantly advances to the next example
- Stopwatch to track exercise duration
- Hint button reveals the correct answer
- Adjustable font size (persisted across sessions)
- **Norm counter** – set a goal, indicator shows progress (cyan/yellow/green)
- **Last solved examples** – three most recent answers displayed vertically
- **Statistics** – cumulative data saved locally:  
  total solved, generated, time spent, max answer, session lengths, total uptime
- **Easter egg** – rare hidden messages appear when numeric conditions met
- **About** and **Stats** dialogs with resizable text
- Saves and restores window position, size, and maximized state
- All settings and stats stored in `%USERPROFILE%\.MathTrainer2\`

## Requirements

- Java 21 or later (uses `java.awt.Desktop`, `java.time`)
- Eclipse or any Java IDE

## Build & Run

1. Open project in your IDE
2. Run `trainer.Main` as a Java application

## License

[The Unlicense](https://unlicense.org/)

## Author

Bizonrelax




```bash
javac trainer/*.java
java trainer.Main



# MathTrainer2 – Тренажер для устного счета (Умножение)

Простое, но эффективное настольное приложение для отработки навыков устного умножения.

Написано на чистом Java (Swing), без внешних зависимостей.


## Особенности

- Примеры случайного умножения: `a × b = ?`
- Настраиваемые диапазоны для обоих множителей (мин–макс)
- Проверка ответа в реальном времени – становится зеленым, когда ответ правильный
- Секундомер для отслеживания времени решения (начинается с "Старт", останавливается с "Стоп")
- Кнопка подсказки показывает правильный ответ
- Регулируемый размер шрифта (текстовое поле или кнопки +/-)
- Диапазоны по умолчанию: 2–44 для обоих чисел
- Клавиатурные сочетания:

- `Enter` (пока поле ответа находится в фокусе) – запускает проверку и переходит к следующему?

На самом деле, после правильного ответа фокус перемещается на кнопку «Следующий пример»; нажмите `Enter` или `Space`, чтобы сгенерировать новый пример.

- `Ctrl+N` – сгенерировать следующий пример немедленно
- Удобные для пользователя сообщения и четкая визуальная обратная связь

## Как запустить

1. Убедитесь, что у вас установлена ​​**Java 8 или выше**.

2. Загрузите или клонируйте репозиторий.

3. Скомпилируйте и запустите:

```bash
javac trainer/*.java
java trainer.Main
