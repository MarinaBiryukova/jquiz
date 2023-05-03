package ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AdminScreen extends BaseScreen {
    private final int buttonX;
    private final String[] items;
    private final List<Component> components;

    AdminScreen(JFrame parent) {
        super(parent);
        this.componentSize = new Dimension((int) (width * 0.37), (int) (height * 0.1));
        this.buttonX = (width - componentSize.width) / 2;
        this.items = new String[]{"Сменить пароль"};
        this.components = new ArrayList<>();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        drawUsername(g2);
        drawExitButton(components);
        drawAddQuestionButton();
        drawShowQuestionsBaseButton();
        drawShowResultBaseButton();
        drawSettingsBox();
        new KeyboardListener(components, this);
    }

    private void drawSettingsBox() {
        JComboBox<String> comboBox = createComboBox(items, "Администратор");
        comboBox.addActionListener(actionEvent -> {
            if (((JComboBox<?>) actionEvent.getSource()).getSelectedItem() instanceof String) {
                String item = (String) ((JComboBox<?>) actionEvent.getSource()).getSelectedItem();
                if (item != null && item.equals("Сменить пароль")) {
                    parentFrame.getContentPane().remove(0);
                    MainFrame mainFrame = (MainFrame) parentFrame;
                    parentFrame.add(new ChangePasswordScreen(parentFrame, mainFrame.getLogInManager().getAdmin()));
                    parentFrame.setVisible(true);
                }
            }
        });
        add(comboBox);
        components.add(comboBox);
    }

    private void drawUsername(Graphics2D g2) {
        g2.setFont(basicFont);
        g2.drawString("Администратор", usernameX, usernameY);
    }

    private void drawAddQuestionButton() {
        JButton addQuestion = createButton(buttonX, (int) (height * 0.3), "Добавить вопрос");
        addQuestion.addActionListener(actionEvent -> {
            parentFrame.getContentPane().remove(0);
            parentFrame.add(new AddQuestionScreen(parentFrame));
            parentFrame.setVisible(true);
        });
        add(addQuestion);
        components.add(addQuestion);
    }

    private void drawShowQuestionsBaseButton() {
        JButton showQuestions = createButton(buttonX, (int) (height * 0.45), "База вопросов");
        showQuestions.addActionListener(actionEvent -> {
            parentFrame.getContentPane().remove(0);
            parentFrame.add(new QuestionsBaseScreen(parentFrame, 0));
            parentFrame.setVisible(true);
        });
        add(showQuestions);
        components.add(showQuestions);
    }

    private void drawShowResultBaseButton() {
        JButton showResults = createButton(buttonX, (int) (height * 0.6), "База результатов");
        showResults.addActionListener(actionEvent -> {
            parentFrame.getContentPane().remove(0);
            parentFrame.add(new ResultsBaseScreen(parentFrame, 0));
            parentFrame.setVisible(true);
        });
        add(showResults);
        components.add(showResults);
    }
}
