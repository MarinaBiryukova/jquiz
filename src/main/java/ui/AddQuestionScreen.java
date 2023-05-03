package ui;

import core.exceptions.QuestionAlreadyExistsException;
import model.Question;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AddQuestionScreen extends BaseScreen {
    private final int margin;
    private final int descriptionWidth;
    private final Dimension textAreaSize;
    private final String[] descriptions;
    private final JTextField[] fields;
    private final List<Component> components;
    private int currentHeight;

    AddQuestionScreen(JFrame parent) {
        super(parent);
        this.margin = 10;
        this.descriptionWidth = (int) (width * 0.2);
        this.textAreaSize = new Dimension(width - descriptionWidth - 4 * this.margin, (int) (height * 0.07));
        this.descriptions = new String[]{"Вопрос", "Вариант 1", "Вариант 2", "Вариант 3", "Вариант 4", "Верный ответ"};
        this.fields = new JTextField[descriptions.length];
        this.components = new ArrayList<>();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        currentHeight = 20 + exitButtonSize.height + margin;
        drawUsername(g2);
        drawBackButton();
        for (int i = 0; i < descriptions.length; i++) {
            fields[i] = createTextField(
                    descriptionWidth + 2 * margin,
                    currentHeight,
                    400,
                    textAreaSize.width,
                    textAreaSize.height,
                    SwingConstants.LEFT
            );
            add(fields[i]);
            components.add(fields[i]);
            drawDescription(g2, descriptions[i] + ":");
            currentHeight += textAreaSize.height + margin;
        }
        drawSubmitButton();
        new KeyboardListener(components, this);
    }

    private void drawUsername(Graphics2D g2) {
        g2.setFont(basicFont);
        g2.drawString("Администратор", usernameX, usernameY);
    }

    private void drawBackButton() {
        JButton backButton = createBackButton();
        backButton.addActionListener(actionEvent -> {
            parentFrame.getContentPane().remove(0);
            parentFrame.add(new AdminScreen(parentFrame));
            parentFrame.setVisible(true);
        });
        add(backButton);
        components.add(backButton);
    }

    private void drawDescription(Graphics2D g2, String description) {
        g2.setFont(basicFont);
        g2.drawString(description, margin, currentHeight + textAreaSize.height / 2);
    }

    private void drawSubmitButton() {
        JButton submitButton = createButton(width - componentSize.width - 20, currentHeight, "Добавить");
        submitButton.addActionListener(actionEvent -> {
            for (int i = 0; i < descriptions.length; i++) {
                if (fields[i].getText().isEmpty()) {
                    int finalI = i;
                    SwingUtilities.invokeLater(() -> new NotificationFrame(
                            "Вы не заполнили поле \"" + descriptions[finalI] + "\"!",
                            false
                    ).setVisible(true));
                    return;
                }
            }
            String[] variants = new String[]{fields[1].getText(), fields[2].getText(), fields[3].getText(), fields[4].getText()};
            Question question = new Question(fields[0].getText(), variants, Integer.parseInt(fields[5].getText()) - 1);
            try {
                MainFrame mainFrame = (MainFrame) parentFrame;
                mainFrame.getQuestionManager().addQuestion(question);
                SwingUtilities.invokeLater(() -> new NotificationFrame("Вопрос успешно добавлен!", false).setVisible(true));
            } catch (QuestionAlreadyExistsException e) {
                SwingUtilities.invokeLater(() -> new NotificationFrame(e.getMessage(), false).setVisible(true));
            }
        });
        add(submitButton);
        components.add(submitButton);
    }
}
