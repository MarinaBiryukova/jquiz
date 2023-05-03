package ui;

import core.exceptions.FullPageException;
import model.Result;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ResultsBaseScreen extends BaseScreen {
    private final int firstCellWidth;
    private final int secondCellWidth;
    private final int thirdCellWidth;
    private final int stroke;
    private final int margin;
    private final Result[] results;
    private final int startIndex;
    private final String[] items;
    private final List<JCheckBox> checkBoxes;
    private final List<Component> components;
    private JComboBox<String> comboBox;
    private int currentHeight;
    private int currentCellHeight;

    ResultsBaseScreen(JFrame parent, int startIndex) {
        super(parent);
        this.componentSize = new Dimension((int) (width * 0.16), (int) (height * 0.1));
        this.firstCellWidth = (int) (width * 0.35);
        this.secondCellWidth = (int) (width * 0.1);
        this.thirdCellWidth = (int) (width * 0.45);
        this.stroke = 1;
        this.margin = 10;
        this.startIndex = startIndex;
        MainFrame mainFrame = (MainFrame) parentFrame;
        this.results = mainFrame.getResultManager().getAllResults();
        this.items = new String[]{"Удалить"};
        this.checkBoxes = new ArrayList<>();
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
        this.currentHeight = componentSize.height + 30;
        this.currentCellHeight = 30;
        drawUsername(g2);
        drawBackButton();
        if (results.length == 0) {
            drawEmptyBaseString(g2);
        } else {
            if (startIndex >= results.length) {
                drawPreviousPage();
            }
            drawInstrumentBox();
            drawPreviousPageButton(startIndex != 0, components);
            currentHeight += pageSwitchButtonSize.height + 10;
            drawTableHeader(g2);
            int i = startIndex;
            for (; i < results.length; i++) {
                drawCheckBox(firstCellWidth + secondCellWidth + thirdCellWidth + 10, currentHeight, checkBoxes, comboBox);
                try {
                    drawResult(g2, results[i]);
                } catch (FullPageException e) {
                    drawNextPageButton(true, new ResultsBaseScreen(parentFrame, i), components);
                    break;
                }
            }
            if (i == results.length) {
                drawNextPageButton(false, new ResultsBaseScreen(parentFrame, i), components);
            }
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
        add(backButton);
        components.add(backButton);
    }

    private void drawEmptyBaseString(Graphics2D g2) {
        g2.setFont(basicFont);
        FontMetrics fm = g2.getFontMetrics();
        String string = "База результатов пуста!";
        g2.drawString(string, (width - fm.stringWidth(string)) / 2, currentHeight);
    }

    private void drawPreviousPage() {
        parentFrame.getContentPane().remove(this);
        ResultsBaseScreen screen = (ResultsBaseScreen) parentFrame.getContentPane().getComponent(
                parentFrame.getContentPane().getComponentCount() - 1
        );
        int index = screen.getStartIndex();
        parentFrame.getContentPane().remove(parentFrame.getContentPane().getComponentCount() - 1);
        parentFrame.add(new ResultsBaseScreen(parentFrame, index));
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
                            "Вы действительно хотите безвозвратно удалить выделенные результаты?",
                            true
                    );
                    JButton yes = (JButton) notificationFrame.getContentPane().getComponent(
                            notificationFrame.getContentPane().getComponentCount() - 1
                    );
                    yes.addActionListener(actionEvent1 -> deleteSelectedResults());
                    SwingUtilities.invokeLater(() -> notificationFrame.setVisible(true));
                }
            }
        });
        add(comboBox);
    }

    private void deleteSelectedResults() {
        MainFrame mainFrame = (MainFrame) parentFrame;
        List<Result> list = getSelectedResults();
        for (Result r : list) {
            mainFrame.getResultManager().deleteResult(r);
        }
        int index = getCurrentComponentIndex();
        for (int i = parentFrame.getContentPane().getComponentCount() - 1; i >= index; i--) {
            parentFrame.getContentPane().remove(i);
        }
        parentFrame.add(new ResultsBaseScreen(parentFrame, startIndex), index);
        parentFrame.setVisible(true);
    }

    private List<Result> getSelectedResults() {
        List<Result> list = new ArrayList<>();
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                list.add(results[i + startIndex]);
            }
        }
        return list;
    }

    private void drawTableHeader(Graphics2D g2) {
        g2.setStroke(new BasicStroke(stroke));
        g2.drawLine(0, currentHeight, firstCellWidth + secondCellWidth + thirdCellWidth, currentHeight);
        String name = "Имя пользователя";
        g2.setFont(basicFont);
        FontMetrics fm = g2.getFontMetrics();
        int x = (firstCellWidth - fm.stringWidth(name)) / 2;
        int y = currentHeight + 30 - (30 - basicFont.getSize()) / 2;
        g2.drawString(name, x, y);
        name = "Дата";
        x = firstCellWidth + secondCellWidth + (thirdCellWidth - fm.stringWidth(name)) / 2;
        g2.drawString(name, x, y);
        name = "Баллы";
        x = firstCellWidth + (secondCellWidth - fm.stringWidth(name)) / 2;
        g2.drawString(name, x, y);
        drawTableCarcass(g2);
    }

    private void drawTableCarcass(Graphics2D g2) {
        g2.drawLine(firstCellWidth, currentHeight, firstCellWidth, currentHeight + currentCellHeight);
        g2.drawLine(
                firstCellWidth + secondCellWidth,
                currentHeight,
                firstCellWidth + secondCellWidth,
                currentHeight + currentCellHeight
        );
        g2.drawLine(
                firstCellWidth + secondCellWidth + thirdCellWidth,
                currentHeight,
                firstCellWidth + secondCellWidth + thirdCellWidth,
                currentHeight + currentCellHeight
        );
        currentHeight += currentCellHeight;
        g2.drawLine(0, currentHeight, firstCellWidth + secondCellWidth + thirdCellWidth, currentHeight);
        currentHeight += stroke;
    }

    private void drawResult(Graphics2D g2, Result result) throws FullPageException {
        FontMetrics fm = g2.getFontMetrics();
        currentCellHeight = Math.max(
                Math.max(((fm.stringWidth(result.getName()) / (firstCellWidth - 2 * margin)) + 1) * fm.getHeight(),
                        ((fm.stringWidth(String.valueOf(result.getScore())) / (secondCellWidth - 2 * margin)) + 1) * fm.getHeight()),
                Math.max(((fm.stringWidth(result.getDate().toString()) / (thirdCellWidth - 2 * margin)) + 1) * fm.getHeight(),
                        componentSize.height)
        );
        if (currentHeight + currentCellHeight + stroke > height - parentFrame.getInsets().top) {
            this.remove(this.getComponentCount() - 1);
            throw new FullPageException();
        }
        drawUsername(result);
        drawScore(result);
        drawDate(result);
        drawTableCarcass(g2);
    }

    private void drawUsername(Result result) {
        JTextArea textArea = createTextArea(
                result.getName(),
                firstCellWidth - 2 * margin,
                currentCellHeight,
                margin,
                currentHeight,
                false
        );
        add(textArea);
    }

    private void drawScore(Result result) {
        JTextArea textArea = createTextArea(
                String.valueOf(result.getScore()),
                secondCellWidth - 2 * margin,
                currentCellHeight,
                firstCellWidth + margin,
                currentHeight,
                false
        );
        add(textArea);
    }

    private void drawDate(Result result) {
        JTextArea textArea = createTextArea(
                result.getDate().toString(),
                thirdCellWidth - 2 * margin,
                currentCellHeight,
                firstCellWidth + secondCellWidth + margin,
                currentHeight,
                false
        );
        add(textArea);
    }
}
