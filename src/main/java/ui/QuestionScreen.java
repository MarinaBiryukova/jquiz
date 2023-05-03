package ui;

import model.Question;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionScreen extends BaseScreen {
    private static final Color VARIANT_COLOR = new Color(214, 201, 201);
    private final User user;
    private final Dimension variantSize;
    private final int serialNumber;
    private final int questionsCount;
    private final Question question;
    private final JButton[] variants;
    private final List<Component> components;
    private int chosenIndex;

    QuestionScreen(JFrame parent, User user, int number, int count, Question question) {
        super(parent);
        this.user = user;
        this.variantSize = new Dimension((int) (width * 0.44), (int) (height * 0.14));
        this.serialNumber = number;
        this.questionsCount = count;
        this.question = question;
        this.variants = new JButton[4];
        this.components = new ArrayList<>();
        this.chosenIndex = -1;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        drawUsername(g2);
        drawSerialNumber(g2);
        drawQuestion();
        drawVariants();
        drawNextButton();
        drawMenuButton();
        new KeyboardListener(components, this);
    }

    private void drawUsername(Graphics2D g2) {
        g2.setFont(basicFont);
        if (user == null) {
            g2.drawString("Гостевой режим", usernameX, usernameY);
        } else {
            g2.drawString(user.getName(), usernameX, usernameY);
        }
    }

    private void drawSerialNumber(Graphics2D g2) {
        g2.setFont(basicFont);
        FontMetrics fm = g2.getFontMetrics();
        String serialNumber = this.serialNumber + "/" + questionsCount;
        int posX = (width - fm.stringWidth(serialNumber)) / 2;
        int posY = 20;
        g2.drawString(serialNumber, posX, posY);
    }

    private void drawQuestion() {
        JTextArea textArea = new JTextArea(question.getQuestion());
        textArea.setSize((int) (width * 0.8), 95);
        textArea.setBackground(parentFrame.getContentPane().getBackground());
        Font font = new Font("TimesRoman", Font.PLAIN, 16);
        textArea.setFont(font);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setLocation((width - textArea.getSize().width) / 2, 35);
        textArea.setEditable(false);
        add(textArea);
    }

    private void drawVariants() {
        for (int i = 0; i < 4; i++) {
            variants[i] = new JButton(question.getAnswers()[i]);
            variants[i].setMargin(new Insets(0, 0, 0, 0));
            variants[i].setBackground(VARIANT_COLOR);
            variants[i].setSize(variantSize);
            int posX = (width - 2 * variantSize.width - 14) / 2 + (variantSize.width + 14) * (i % 2);
            int posY = height / 2 - variantSize.height + (variantSize.height + 14) * (i / 2);
            variants[i].setLocation(posX, posY);
            variants[i].setFont(basicFont);
            int finalI = i;
            variants[i].addActionListener(actionEvent -> {
                if (chosenIndex != -1) {
                    SwingUtilities.invokeLater(() -> new NotificationFrame("Вы уже выбрали ответ!", false).setVisible(true));
                } else {
                    chosenIndex = finalI;
                    if (finalI == question.getCorrectAnswer()) {
                        variants[finalI].setBackground(Color.GREEN);
                    } else {
                        variants[finalI].setBackground(Color.RED);
                    }
                }
            });
            add(variants[i]);
            components.add(variants[i]);
        }
    }

    private void drawMenuButton() {
        JButton menu = createButton(40, height - 20 - 2 * componentSize.height, "Главное меню");
        menu.addActionListener(actionEvent -> {
            parentFrame.getContentPane().remove(1);
            parentFrame.getContentPane().remove(0);
            parentFrame.add(new TestScreen(parentFrame, user));
            parentFrame.setVisible(true);
        });
        add(menu);
        components.add(menu);
    }

    private void drawNextButton() {
        JButton nextQuestion;
        if (serialNumber == questionsCount) {
            nextQuestion = createButton(width - 40 - componentSize.width, height - 20 - 2 * componentSize.height, "Завершить");
        } else {
            nextQuestion = createButton(width - 40 - componentSize.width, height - 20 - 2 * componentSize.height,
                    "Следующий вопрос");
        }
        nextQuestion.addActionListener(actionEvent -> {
            if (chosenIndex == -1) {
                SwingUtilities.invokeLater(() -> new NotificationFrame("Вы не выбрали ответ!", false).setVisible(true));
            } else {
                TestScreen ts = (TestScreen) parentFrame.getContentPane().getComponent(0);
                ts.drawQuestion(chosenIndex == question.getCorrectAnswer());
            }
        });
        add(nextQuestion);
        components.add(nextQuestion);
    }
}
