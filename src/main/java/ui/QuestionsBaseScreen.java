package ui;

import core.exceptions.FullPageException;
import model.Question;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class QuestionsBaseScreen extends BaseScreen {
    private final Question[] questions;
    private final String[] items;
    private final int startIndex;
    private final int questionFieldWidth;
    private final int answerFieldWidth;
    private final int correctAnswerFieldWidth;
    private final int stroke;
    private final int margin;
    private final List<JCheckBox> checkBoxes;
    private final List<List<Component>> questionsAreas;
    private final List<Component> components;
    private JComboBox<String> comboBox;
    private JButton saveButton;
    private int currentHeight;
    private int currentCellHeight;
    private int finalIndex;

    QuestionsBaseScreen(JFrame parent, int startIndex) {
        super(parent);
        this.componentSize = new Dimension((int) (width * 0.16), (int) (height * 0.1));
        MainFrame mainFrame = (MainFrame) parentFrame;
        this.questions = mainFrame.getQuestionManager().getAllQuestions();
        this.items = new String[]{"Удалить"};
        this.startIndex = startIndex;
        this.questionFieldWidth = (int) (width * 0.25);
        this.answerFieldWidth = (int) (width * 0.55) / 4;
        this.correctAnswerFieldWidth = (int) (width * 0.12);
        this.stroke = 1;
        this.margin = 5;
        this.checkBoxes = new ArrayList<>();
        this.questionsAreas = new ArrayList<>();
        this.components = new ArrayList<>();
        repaint();
    }

    int getStartIndex() {
        return startIndex;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        currentHeight = exitButtonSize.height + 25;
        currentCellHeight = 30;
        drawUsername(g2);
        drawBackButton();
        if (questions.length == 0) {
            drawEmptyBaseString(g2);
        } else {
            if (startIndex >= questions.length) {
                drawPreviousPage();
            }
            drawInstrumentBox();
            drawSaveButton();
            drawPreviousPageButton(startIndex != 0, components);
            currentHeight += pageSwitchButtonSize.height + 10;
            drawTableHeader(g2);
            int i = startIndex;
            for (; i < questions.length; i++) {
                drawCheckBox(
                        questionFieldWidth + answerFieldWidth * 4 + correctAnswerFieldWidth + 10,
                        currentHeight,
                        checkBoxes,
                        comboBox
                );
                try {
                    drawQuestion(g2, questions[i]);
                } catch (FullPageException e) {
                    drawNextPageButton(true, new QuestionsBaseScreen(parentFrame, i), components);
                    break;
                }
            }
            if (i == questions.length) {
                drawNextPageButton(false, new QuestionsBaseScreen(parentFrame, i), components);
            }
            finalIndex = i - 1;
            components.addAll(checkBoxes);
            components.add(comboBox);
        }
        new KeyboardListener(components, this);
    }

    private void drawUsername(Graphics2D g2) {
        g2.setFont(basicFont);
        g2.drawString("Администратор", usernameX, usernameY);
    }

    private void drawBackButton() {
        JButton backButton = createBackButton();
        backButton.addActionListener(actionEvent -> {
            parentFrame.getContentPane().removeAll();
            parentFrame.add(new AdminScreen(parentFrame));
            parentFrame.setVisible(true);
        });
        backButton.setLocation(backButton.getX(), 15);
        add(backButton);
        components.add(backButton);
    }

    private void drawEmptyBaseString(Graphics2D g2) {
        g2.setFont(basicFont);
        FontMetrics fm = g2.getFontMetrics();
        String string = "База вопросов пуста!";
        g2.drawString(string, (width - fm.stringWidth(string)) / 2, currentHeight);
    }

    private void drawPreviousPage() {
        parentFrame.getContentPane().remove(this);
        QuestionsBaseScreen screen = (QuestionsBaseScreen) parentFrame.getContentPane().getComponent(
                parentFrame.getContentPane().getComponentCount() - 1
        );
        int index = screen.getStartIndex();
        parentFrame.getContentPane().remove(parentFrame.getContentPane().getComponentCount() - 1);
        parentFrame.add(new QuestionsBaseScreen(parentFrame, index));
        parentFrame.getContentPane().getComponent(parentFrame.getContentPane().getComponentCount() - 1).setVisible(true);
        parentFrame.setVisible(true);
    }

    private void drawInstrumentBox() {
        comboBox = createComboBox(items, "Администратор");
        setComboBoxEnabled(comboBox, checkBoxes);
        comboBox.addActionListener(actionEvent -> {
            if (((JComboBox<?>) actionEvent.getSource()).getSelectedItem() instanceof String) {
                String item = (String) ((JComboBox<?>) actionEvent.getSource()).getSelectedItem();
                if (item != null && item.equals("Удалить")) {
                    NotificationFrame notificationFrame = new NotificationFrame(
                            "Вы действительно хотите безвозвратно удалить выделенные вопросы?",
                            true
                    );
                    JButton yes = (JButton) notificationFrame.getContentPane().getComponent(
                            notificationFrame.getContentPane().getComponentCount() - 1
                    );
                    yes.addActionListener(actionEvent1 -> deleteSelectedQuestions());
                    SwingUtilities.invokeLater(() -> notificationFrame.setVisible(true));
                }
            }
        });
        add(comboBox);
    }

    private void deleteSelectedQuestions() {
        List<Question> list = getSelectedQuestions();
        MainFrame mainFrame = (MainFrame) parentFrame;
        for (Question q : list) {
            mainFrame.getQuestionManager().deleteQuestion(q);
        }
        int index = getCurrentComponentIndex();
        for (int i = parentFrame.getContentPane().getComponentCount() - 1; i >= index; i--) {
            parentFrame.getContentPane().remove(i);
        }
        parentFrame.add(new QuestionsBaseScreen(parentFrame, startIndex), index);
        parentFrame.setVisible(true);
    }

    private List<Question> getSelectedQuestions() {
        List<Question> list = new ArrayList<>();
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                list.add(questions[i]);
            }
        }
        return list;
    }

    private void drawSaveButton() {
        JButton button = createButton(
                width - componentSize.width - 20 - getInsets().right,
                height - componentSize.height - 40,
                "Сохранить"
        );
        button.addActionListener(actionEvent -> updateQuestions());
        button.setEnabled(false);
        saveButton = button;
        add(button);
    }

    private void updateQuestions() {
        MainFrame mainFrame = (MainFrame) parentFrame;
        for (int i = 0; i <= finalIndex - startIndex; i++) {
            Question newQuestion = getQuestionByIndex(i);
            if (!newQuestion.getQuestion().equals(questions[i + startIndex].getQuestion())
                    || newQuestion.getCorrectAnswer() != questions[i + startIndex].getCorrectAnswer()) {
                mainFrame.getQuestionManager().updateQuestion(questions[i + startIndex], newQuestion);
                continue;
            }
            for (int j = 0; j < newQuestion.getAnswers().length; j++) {
                if (!newQuestion.getAnswers()[j].equals(questions[i + startIndex].getAnswers()[j])) {
                    mainFrame.getQuestionManager().updateQuestion(questions[i + startIndex], newQuestion);
                    break;
                }
            }
        }
    }

    private Question getQuestionByIndex(int i) {
        String[] answers = {
                ((JTextArea) questionsAreas.get(i).get(1)).getText(),
                ((JTextArea) questionsAreas.get(i).get(2)).getText(),
                ((JTextArea) questionsAreas.get(i).get(3)).getText(),
                ((JTextArea) questionsAreas.get(i).get(4)).getText()
        };
        return new Question(
                ((JTextArea) questionsAreas.get(i).get(0)).getText(),
                answers,
                Integer.parseInt(((JTextArea) questionsAreas.get(i).get(5)).getText()) - 1
        );
    }

    private void drawTableHeader(Graphics2D g2) {
        g2.setStroke(new BasicStroke(stroke));
        g2.drawLine(0, currentHeight, questionFieldWidth + answerFieldWidth * 4 + correctAnswerFieldWidth, currentHeight);
        g2.setFont(basicFont);
        FontMetrics fm = g2.getFontMetrics();
        String name = "Вопрос";
        int x = (questionFieldWidth - fm.stringWidth(name)) / 2;
        int y = currentHeight + currentCellHeight - (currentCellHeight - basicFont.getSize()) / 2;
        g2.drawString(name, x, y);
        for (int i = 0; i < 4; i++) {
            name = "Вариант " + (i + 1);
            x = questionFieldWidth + answerFieldWidth * i + (answerFieldWidth - fm.stringWidth(name)) / 2;
            g2.drawString(name, x, y);
        }
        name = "Верный";
        x = questionFieldWidth + answerFieldWidth * 4 + (correctAnswerFieldWidth - fm.stringWidth(name)) / 2;
        g2.drawString(name, x, y);
        drawTableCarcass(g2);
    }

    private void drawTableCarcass(Graphics2D g2) {
        for (int i = 0; i < 5; i++) {
            g2.drawLine(
                    questionFieldWidth + answerFieldWidth * i,
                    currentHeight,
                    questionFieldWidth + answerFieldWidth * i,
                    currentHeight + currentCellHeight
            );
        }
        g2.drawLine(
                questionFieldWidth + answerFieldWidth * 4 + correctAnswerFieldWidth,
                currentHeight,
                questionFieldWidth + answerFieldWidth * 4 + correctAnswerFieldWidth,
                currentHeight + currentCellHeight
        );
        currentHeight += currentCellHeight;
        g2.drawLine(0, currentHeight, questionFieldWidth + answerFieldWidth * 4 + correctAnswerFieldWidth, currentHeight);
        currentHeight += stroke;
    }

    private void drawQuestion(Graphics2D g2, Question question) throws FullPageException {
        drawQuestionText(g2, question);
        drawAnswers(g2, question);
        drawCorrectIndex(g2, question);
        if (currentHeight + currentCellHeight + stroke > height - componentSize.height - 50) {
            for (int i = 0; i < 3 + question.getAnswers().length; i++) {
                this.remove(this.getComponentCount() - 1);
            }
            throw new FullPageException();
        }
        List<Component> areas = new ArrayList<>();
        for (int i = 2 + question.getAnswers().length; i > 0; i--) {
            areas.add(this.getComponent(this.getComponentCount() - i));
        }
        questionsAreas.add(areas);
        drawTableCarcass(g2);
    }

    private void drawQuestionText(Graphics2D g2, Question question) {
        FontMetrics fm = g2.getFontMetrics();
        int linesNumber = (fm.stringWidth(question.getQuestion()) / (questionFieldWidth - 2 * margin)) + 1;
        JTextArea textArea = createTextArea(
                question.getQuestion(),
                questionFieldWidth - 2 * margin,
                linesNumber * fm.getHeight(),
                margin,
                currentHeight,
                true
        );
        textArea.addKeyListener(new TextListener());
        add(textArea);
        currentCellHeight = textArea.getHeight();
    }

    private void drawAnswers(Graphics2D g2, Question question) {
        FontMetrics fm = g2.getFontMetrics();
        for (int i = 0; i < question.getAnswers().length; i++) {
            int linesNumber = (fm.stringWidth(question.getAnswers()[i]) / (answerFieldWidth - 2 * margin)) + 1;
            JTextArea textArea = createTextArea(
                    question.getAnswers()[i],
                    answerFieldWidth - 2 * margin,
                    linesNumber * fm.getHeight(),
                    margin + questionFieldWidth + answerFieldWidth * i,
                    currentHeight,
                    true
            );
            textArea.addKeyListener(new TextListener());
            add(textArea);
            currentCellHeight = Math.max(textArea.getHeight(), currentCellHeight);
        }
    }

    private void drawCorrectIndex(Graphics2D g2, Question question) {
        FontMetrics fm = g2.getFontMetrics();
        JTextArea textArea = createTextArea(
                String.valueOf(question.getCorrectAnswer() + 1),
                correctAnswerFieldWidth - 2 * margin,
                fm.getHeight(),
                margin + questionFieldWidth + answerFieldWidth * 4,
                currentHeight,
                true
        );
        textArea.addKeyListener(new TextListener());
        add(textArea);
    }

    private class TextListener implements KeyListener {
        @Override
        public void keyPressed(KeyEvent ke) {
            for (int i = startIndex; i <= finalIndex; i++) {
                if (!saveButton.isEnabled()) {
                    saveButton.setEnabled(true);
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent ke) {
        }

        @Override
        public void keyTyped(KeyEvent ke) {
        }
    }
}
