package ui;

import model.Question;
import model.Result;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestScreen extends BaseScreen {
    private final User user;
    private final Question[] questions;
    private final boolean[] answers;
    private final String[] items;
    private final List<Component> components;
    private int questionsCount;
    private int currentQuestion;

    TestScreen(JFrame parent, User user) {
        super(parent);
        this.user = user;
        MainFrame mainFrame = (MainFrame) parentFrame;
        this.questionsCount = 10;
        this.questions = mainFrame.getQuestionManager().getTestQuestions(this.questionsCount);
        this.questionsCount = questions.length;
        this.answers = new boolean[questionsCount];
        this.items = new String[]{"Сменить пароль"};
        this.components = new ArrayList<>();
        currentQuestion = 0;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        drawUsername(g2);
        drawExitButton(components);
        drawGreetingString(g2);
        drawShowTableButton();
        drawStartTestButton();
        if (user != null) {
            drawSettingsBox();
        }
        new KeyboardListener(components, this);
    }

    private void drawSettingsBox() {
        JComboBox<String> comboBox = createComboBox(items, user == null ? "Гостевой режим" : user.getName());
        comboBox.addActionListener(actionEvent -> {
            if (((JComboBox<?>) actionEvent.getSource()).getSelectedItem() instanceof String) {
                String item = (String) ((JComboBox<?>) actionEvent.getSource()).getSelectedItem();
                if (item != null && item.equals("Сменить пароль")) {
                    parentFrame.getContentPane().remove(0);
                    parentFrame.add(new ChangePasswordScreen(parentFrame, user));
                    parentFrame.setVisible(true);
                }
            }
        });
        add(comboBox);
        components.add(comboBox);
    }

    private void drawUsername(Graphics2D g2) {
        g2.setFont(basicFont);
        if (user == null) {
            g2.drawString("Гостевой режим", usernameX, usernameY);
        } else {
            g2.drawString(user.getName(), usernameX, usernameY);
        }
    }

    private void drawGreetingString(Graphics2D g2) {
        g2.setFont(greetingFont);
        FontMetrics fm = g2.getFontMetrics();
        String greeting = "Проверь свои знания языка программирования";
        int x = ((width - fm.stringWidth(greeting)) / 2);
        int y = (int) (height * 0.32);
        g2.drawString(greeting, x, y);
        String language = "Java!";
        x = ((width - fm.stringWidth(language)) / 2);
        y = (int) (height * 0.32) + 20;
        g2.drawString(language, x, y);
    }

    private void drawShowTableButton() {
        JButton showTable = createButton(width / 2 - 20 - componentSize.width, (int) (height * 0.67), "Посмотреть таблицу");
        showTable.addActionListener(actionEvent -> {
            parentFrame.getContentPane().remove(0);
            parentFrame.add(new ResultsTableScreen(parentFrame, user, 0));
            parentFrame.setVisible(true);
        });
        add(showTable);
        components.add(showTable);
    }

    private void drawStartTestButton() {
        JButton startTest = createButton(width / 2 + 20, (int) (height * 0.67), "Пройти тест");
        startTest.addActionListener(actionEvent -> drawQuestion(true));
        add(startTest);
        components.add(startTest);
    }

    void drawQuestion(boolean answer) {
        if (questionsCount == 0) {
            SwingUtilities.invokeLater(() -> new NotificationFrame("В тесте нет вопросов!", false).setVisible(true));
            return;
        }
        if (currentQuestion < questionsCount) {
            if (currentQuestion > 0) {
                answers[currentQuestion - 1] = answer;
                parentFrame.getContentPane().remove(1);
            } else {
                parentFrame.getContentPane().getComponent(0).setVisible(false);
            }
            parentFrame.add(new QuestionScreen(parentFrame, user, currentQuestion + 1, questionsCount, questions[currentQuestion]));
            parentFrame.setVisible(true);
            currentQuestion++;
        } else {
            answers[currentQuestion - 1] = answer;
            Result result;
            if (user == null) {
                result = new Result("guest", new Date(), answers);
            } else {
                result = new Result(user.getName(), new Date(), answers);
            }
            parentFrame.getContentPane().remove(1);
            parentFrame.getContentPane().remove(0);
            parentFrame.add(new ResultScreen(parentFrame, user, result, questionsCount));
            parentFrame.setVisible(true);
        }
    }
}
