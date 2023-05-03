package ui;

import core.exceptions.FullPageException;
import model.Result;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ResultsTableScreen extends BaseScreen {
    private final User user;
    private final Result[] results;
    private final int firstCellWidth;
    private final int secondCellWidth;
    private final int thirdCellWidth;
    private final int stroke;
    private final int margin;
    private final int startIndex;
    private final List<Component> components;
    private int currentHeight;
    private int currentCellHeight;

    ResultsTableScreen(JFrame parent, User user, int startIndex) {
        super(parent);
        this.user = user;
        MainFrame mainFrame = (MainFrame) parentFrame;
        this.results = mainFrame.getResultManager().getAllResults();
        this.firstCellWidth = (int) (width * 0.4);
        this.secondCellWidth = (int) (width * 0.15);
        this.thirdCellWidth = width - firstCellWidth - secondCellWidth;
        this.stroke = 1;
        this.margin = 10;
        this.startIndex = startIndex;
        this.components = new ArrayList<>();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        currentHeight = 30 + exitButtonSize.height;
        currentCellHeight = 30;
        drawUsername(g2);
        drawBackButton();
        if (results.length == 0) {
            drawEmptyBaseString(g2);
        } else {
            drawPreviousPageButton(startIndex != 0, components);
            currentHeight += pageSwitchButtonSize.height + 10;
            drawTableHeader(g2);
            int i = startIndex;
            for (; i < results.length; i++) {
                try {
                    drawResult(g2, results[i]);
                } catch (FullPageException e) {
                    drawNextPageButton(true, new ResultsTableScreen(parentFrame, user, i), components);
                    break;
                }
            }
            if (i == results.length) {
                drawNextPageButton(false, new ResultsTableScreen(parentFrame, user, i), components);
            }
        }
        new KeyboardListener(components, this);
        currentHeight = 30 + exitButtonSize.height;
        currentCellHeight = 30;
    }

    private void drawUsername(Graphics2D g2) {
        g2.setFont(basicFont);
        if (user == null) {
            g2.drawString("Гостевой режим", usernameX, usernameY);
        } else {
            g2.drawString(user.getName(), usernameX, usernameY);
        }
    }

    private void drawBackButton() {
        JButton backButton = createBackButton();
        backButton.addActionListener(actionEvent -> {
            int size = parentFrame.getContentPane().getComponentCount();
            for (int i = 0; i < size; i++) {
                parentFrame.getContentPane().remove(0);
            }
            parentFrame.add(new TestScreen(parentFrame, user));
            parentFrame.setVisible(true);
        });
        add(backButton);
        components.add(backButton);
    }

    private void drawEmptyBaseString(Graphics2D g2) {
        g2.setFont(basicFont);
        FontMetrics fm = g2.getFontMetrics();
        String string = "В таблице еще нет результатов!";
        g2.drawString(string, (width - fm.stringWidth(string)) / 2, currentHeight);
        currentHeight += fm.getHeight();
        if (user == null) {
            string = "Зарегистрируйтесь, пройдите тест и сохраните свой результат";
        } else {
            string = "Пройдите тест и сохраните свой результат";
        }
        g2.drawString(string, (width - fm.stringWidth(string)) / 2, currentHeight);
    }

    private void drawTableCarcass(Graphics2D g2) {
        g2.drawLine(firstCellWidth, currentHeight, firstCellWidth, currentHeight + currentCellHeight);
        g2.drawLine(
                firstCellWidth + secondCellWidth,
                currentHeight,
                firstCellWidth + secondCellWidth,
                currentHeight + currentCellHeight
        );
        currentHeight += currentCellHeight;
        g2.drawLine(0, currentHeight, width, currentHeight);
        currentHeight += stroke;
    }

    private void drawTableHeader(Graphics2D g2) {
        g2.setStroke(new BasicStroke(stroke));
        g2.drawLine(0, currentHeight, width, currentHeight);
        String name = "Имя пользователя";
        g2.setFont(basicFont);
        FontMetrics fm = g2.getFontMetrics();
        int x = (firstCellWidth - fm.stringWidth(name)) / 2;
        int y = currentHeight + currentCellHeight - (currentCellHeight - basicFont.getSize()) / 2;
        g2.drawString(name, x, y);
        name = "Баллы";
        x = firstCellWidth + (secondCellWidth - fm.stringWidth(name)) / 2;
        g2.drawString(name, x, y);
        name = "Дата";
        x = firstCellWidth + secondCellWidth + (thirdCellWidth - fm.stringWidth(name)) / 2;
        g2.drawString(name, x, y);
        drawTableCarcass(g2);
    }

    private void drawResult(Graphics2D g2, Result result) throws FullPageException {
        drawUsername(g2, result);
        drawDate(g2, result);
        drawScore(g2, result);
        drawTableCarcass(g2);
        if (currentHeight > height - parentFrame.getInsets().top) {
            this.remove(this.getComponentCount() - 1);
            this.remove(this.getComponentCount() - 1);
            JTextArea textArea = createTextArea(
                    "",
                    width,
                    currentCellHeight - currentHeight + height,
                    0,
                    currentHeight - currentCellHeight - stroke,
                    false
            );
            add(textArea);
            throw new FullPageException();
        }
    }

    private void drawUsername(Graphics2D g2, Result result) {
        FontMetrics fm = g2.getFontMetrics();
        int linesNumber = (fm.stringWidth(result.getName()) / (firstCellWidth - 2 * margin)) + 1;
        JTextArea textArea = createTextArea(
                result.getName(),
                firstCellWidth - 2 * margin,
                linesNumber * fm.getHeight(),
                margin,
                currentHeight,
                false
        );
        add(textArea);
        currentCellHeight = textArea.getHeight();
    }

    private void drawScore(Graphics2D g2, Result result) {
        FontMetrics fm = g2.getFontMetrics();
        int x = firstCellWidth + (secondCellWidth - fm.stringWidth(String.valueOf(result.getScore()))) / 2;
        int y = currentHeight + currentCellHeight - ((currentCellHeight - fm.getFont().getSize()) / 2 + 1);
        g2.drawString(String.valueOf(result.getScore()), x, y);
    }

    private void drawDate(Graphics2D g2, Result result) {
        FontMetrics fm = g2.getFontMetrics();
        int linesNumber = (fm.stringWidth(result.getDate().toString()) / (thirdCellWidth - 2 * margin)) + 1;
        JTextArea textArea = createTextArea(
                result.getDate().toString(),
                thirdCellWidth - 2 * margin,
                linesNumber * fm.getHeight(),
                firstCellWidth + secondCellWidth + margin,
                currentHeight,
                false
        );
        add(textArea);
        currentCellHeight = Math.max(textArea.getHeight(), currentCellHeight);
    }
}
