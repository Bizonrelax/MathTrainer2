package trainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AboutDialog extends JDialog {
    private JTextArea textArea;
    private int fontSize = 16;

    public AboutDialog(Frame owner) {
        super(owner, "О Пр", false); // немодальное
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        textArea = new JTextArea(10, 40);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, fontSize));
        textArea.setText("""
        	   
        	Тренажёр устного счёта — умножение
    Версия 1.1

    Автор: Bizonrelax
    Лицензия: The Unlicense
Скачать можно там:
[Bizonrelax/MathTrainer2: Mental math trainer for multiplication. Generates random examples within custom ranges, checks answers in real time, includes a stopwatch, hints, adjustable font size, and keyboard shortcuts. Pure Java Swing.](https://github.com/Bizonrelax/MathTrainer2)
Возможности:
    • Настраиваемые диапазоны для обоих множителей
    • Два режима: с секундомером и без
      – Без времени: правильный ответ подсвечивается зелёным,
        переход к следующему примеру по кнопке
      – С секундомером: после правильного ответа сразу
        генерируется новый пример
    • Подсказка, показывающая правильный ответ
    • Регулируемый размер шрифта (сохраняется)
    • Счётчик нормы с цветовым индикатором
    • Три последних решённых примера (вертикально)
    • Статистика: общее время, длина сессий, рекорды
    • Пасхалка :)
    • Сохранение размеров окна и диапазонов между запусками

    Файлы конфигурации и статистики:
    %USERPROFILE%\\.MathTrainer2\\
    
___
## Краткое руководство

### Основное использование
1. Программа запускается с диапазонами по умолчанию (2–44 для обоих множителей).

2. Введите свой ответ в поле и нажмите Enter.
3. Правильные ответы записываются; счетчик и индикатор показывают прогресс в достижении нормы.

### Режимы
- **Без секундомера** (по умолчанию): после правильного ответа поле становится зеленым и блокируется. Нажмите «Следующий пример» (или нажмите Enter/Space), чтобы получить новый пример.

- **С секундомером**: нажмите «Старт», чтобы запустить таймер. После каждого правильного ответа автоматически появляется новый пример.

### Настройки
- Изменяйте диапазоны на панели «Диапазоны норм».

- Установите целевую норму в поле «Норма» (сохраняется автоматически).

- Используйте кнопки «+»/«-» для регулировки размера шрифта (сохраняется между сеансами).

- Все настройки сохраняются после перезапуска программы.


### Статистика
- Нажмите «Стат», чтобы просмотреть сводную статистику.

- Данные обновляются в режиме реального времени; нажмите «Обновить», чтобы увидеть последние значения.

- «📂 Файлы» открывает папку с файлами конфигурации и статистики.

### Советы
- «Подсказка» показывает правильный ответ для текущего примера.

- «мин/макс по норме» сбрасывает диапазоны до 2–44.

- «Сбросить счётчик» сбрасывает счётчик решенных задач до 0.
- Индикатор показывает ваш прогресс относительно нормы: голубой (0), жёлтый (ниже нормы), зелёный (достигнуто или превышено).


____

Добавлен двойной режим (с таймером/без таймера), статистика сессии, память состояния окна,
и улучшения пользовательского интерфейса.

- Автоматический переход к следующему примеру при активном секундомере; ручной режим в противном случае.

- Новая статистика: продолжительность сессии, общее время работы, время последнего упражнения.

- Сохранение/восстановление размера окна, положения, состояния развернутого окна и диапазонов.

- Вертикальное расположение для последних 3 решенных примеров (более широкие поля, более крупный шрифт).

- Сохранение размера шрифта и настроек нормали в ~/.MathTrainer2.

- Исправлена ​​ошибка: отсутствие исключения мутации во время прослушивания документа; ввод только цифр.

___
# MathTrainer2

Тренер для устного счета по умножению (Java Swing).

## Особенности
- Настраиваемые диапазоны для обоих множителей
- **Двойной режим**: с секундомером или без него

- **Без ограничения времени**: правильный ответ блокирует поле (зеленый), дождитесь нажатия кнопки «Далее»

- **С ограничением времени**: правильный ответ мгновенно переходит к следующему примеру
- Секундомер для отслеживания продолжительности упражнения
- Кнопка подсказки показывает правильный ответ
- Регулируемый размер шрифта (сохраняется между сессиями)
- **Счетчик норм** – установите цель, индикатор показывает прогресс (голубой/желтый/зеленый)
- **Последние решенные примеры** – три последних ответа отображаются вертикально
- **Статистика** – кумулятивные данные сохраняются локально:
всего решено, сгенерировано, затраченное время, максимальный ответ, длительность сессий, общее время работы
- **Пасхальное яйцо** – редкие скрытые сообщения появляются при выполнении числовых условий

- Все Настройки и статистика хранятся в %USERPROFILE%
-.MathTrainer2-

## Требования

- Java 21 или более поздняя версия (использует `java.awt.Desktop`, `java.time`)
- Eclipse или любая другая Java IDE

## Сборка и запуск

1. Откройте проект в вашей IDE
2. Запустите `trainer.Main` как Java-приложение

## Лицензия

[The Unlicense](https://unlicense.org/)

## Автор

Bizonrelax



___

Mental Math Trainer - Multiplication
Version 1.1

Author: Bizonrelax
License: The Unlicense

Features:
• Customizable ranges for both multipliers
• Two modes: with and without a stopwatch
– Without a timer: the correct answer is highlighted in green,
the button moves to the next problem
– With a stopwatch: after the correct answer, a new problem is immediately
generated
• Hint showing the correct answer
• Adjustable font size (saved)
• Standard counter with a color indicator
• The last three solved problems (vertically)
• Statistics: total time, session length, records
• Easter egg :)
• Saving window sizes and ranges between runs

Configuration and statistics files:

%USERPROFILE%\\.MathTrainer2\\



___
# MathTrainer2

Mental math trainer for multiplication (Java Swing).

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
- All settings and stats stored in `%USERPROFILE%.-MathTrainer2`



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

___
Add dual mode (timed/untimed), session stats, window state memory,
and UI improvements.

- Automatic example advance when stopwatch is active; manual mode otherwise.
- New statistics: session duration, total uptime, last exercise time.
- Save/restore window size, position, maximized state, and ranges.
- Vertical layout for last 3 solved examples (wider fields, larger font).
- Persist font size and norm setting in ~/.MathTrainer2.



- Bugfix: no mutation exception during document listener; digit-only input.
___
## Quick Guide

### Basic usage
1. The program starts with default ranges (2–44 for both multipliers).
2. Enter your answer in the field and press Enter.
3. Correct answers are recorded; counter and indicator reflect progress toward the norm.

### Modes
- **Without stopwatch** (default): after a correct answer, the field turns green and locks. Click «Следующий пример» (or press Enter/Space) to get a new example.
- **With stopwatch**: press «Старт» to start the timer. After each correct answer, a new example appears automatically.

### Settings
- Change ranges in the «Диапазоны чисел» panel.
- Set a target norm in the «Норма» field (saved automatically).
- Use «+»/«-» buttons to adjust the font size (saved between sessions).
- All settings survive program restart.

### Statistics
- Click «Стат» to view cumulative statistics.
- Data is updated in real time; click «Обновить» to see the latest values.
- «📂 Файлы» opens the folder with configuration and statistics files.

### Tips
- «Подсказка» reveals the correct answer for the current example.
- «мин/макс по умолчанию» resets ranges to 2–44.
- «Сбросить счётчик» resets the solved counter to 0.
- The indicator shows your progress against the norm: cyan (0), yellow (below norm), green (reached or exceeded).

        	
        	
        	    """);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        JButton plusBtn = new JButton("+");
        plusBtn.addActionListener(e -> changeFontSize(2));
        JButton minusBtn = new JButton("-");
        minusBtn.addActionListener(e -> changeFontSize(-2));
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(minusBtn);
        btnPanel.add(plusBtn);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void changeFontSize(int delta) {
        int newSize = fontSize + delta;
        if (newSize < 8) newSize = 8;
        fontSize = newSize;
        textArea.setFont(new Font("Monospaced", Font.PLAIN, fontSize));
    }
}