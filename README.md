# MathTrainer2 – Mental Math Trainer (Multiplication)

A simple but effective desktop application for practicing mental multiplication.  
Written in pure Java (Swing), no external dependencies.

## Features

- Random multiplication examples: `a × b = ?`
- Customizable ranges for both multipliers (min–max)
- Real‑time answer validation – turns green when correct
- Stopwatch to track solving time (start with "Старт", stop with "Стоп")
- Hint button shows the correct answer
- Adjustable font size (text field or +/- buttons)
- Default ranges: 2–44 for both numbers
- Keyboard shortcuts:  
  - `Enter` (while answer field is focused) – triggers check and moves to next?  
    Actually, after correct answer, focus moves to "Next example" button; press `Enter` or `Space` to generate a new example.  
  - `Ctrl+N` – generate next example immediately
- User‑friendly messages and clear visual feedback

## How to Run

1. Make sure you have **Java 8 or higher** installed.
2. Download or clone the repository.
3. Compile and run:

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
