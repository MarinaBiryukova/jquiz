package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

public abstract class BaseScreen extends JComponent {
    private static final Color BUTTON_COLOR = new Color(199, 211, 221);
    private static final Color EXIT_BUTTON_COLOR = new Color(214, 201, 201);
    private static final Color BACKGROUND_COLOR = new Color(232, 238, 242);
    protected final Font basicFont = new Font("TimesRoman", Font.PLAIN, 14);
    protected final Font greetingFont = new Font("TimesRoman", Font.PLAIN, 20);
    protected final int usernameX = 10;
    protected final int usernameY = 12;
    protected final int width;
    protected final int height;
    protected final Dimension exitButtonSize;
    protected final Dimension pageSwitchButtonSize;
    protected Dimension componentSize;
    protected JFrame parentFrame;

    BaseScreen(JFrame parentFrame) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        this.height = (int) (screenSize.height * 0.55);
        this.width = (int) (this.height * 1.5) - 20;
        this.exitButtonSize = new Dimension((int) (this.width * 0.16), (int) (this.height * 0.1));
        this.pageSwitchButtonSize = new Dimension((int) (this.width * 0.3), (int) (this.height * 0.07));
        this.componentSize = new Dimension((int) (this.width * 0.3), (int) (this.height * 0.1));
        this.parentFrame = parentFrame;
    }

    static void setComboBoxEnabled(JComboBox<String> comboBox, List<JCheckBox> checkBoxes) {
        int i = 0;
        for (; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                break;
            }
        }
        comboBox.setEnabled(i != checkBoxes.size());
    }

    protected JButton createButton(int x, int y, String name) {
        JButton button = new JButton(name);
        button.setBackground(BUTTON_COLOR);
        button.setSize(componentSize);
        button.setLocation(x, y);
        button.setFont(basicFont);
        button.setMargin(new Insets(0, 0, 0, 0));
        return button;
    }

    protected JButton createBackButton() {
        JButton button = new JButton("Назад");
        button.setBackground(BUTTON_COLOR);
        button.setSize(exitButtonSize);
        button.setLocation(width - exitButtonSize.width - 20 - getInsets().right, 20);
        button.setFont(basicFont);
        button.setMargin(new Insets(0, 0, 0, 0));
        addKeyListener(button);
        return button;
    }


    protected void drawExitButton(List<Component> components) {
        JButton button = new JButton("Выйти");
        button.setBackground(EXIT_BUTTON_COLOR);
        button.setSize(exitButtonSize);
        int x = width - exitButtonSize.width - 20;
        int y = 20;
        button.setLocation(x, y);
        button.setFont(basicFont);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.addActionListener(actionEvent -> {
            parentFrame.getContentPane().remove(0);
            parentFrame.add(new LogInScreen(parentFrame));
            parentFrame.repaint();
            parentFrame.setVisible(true);
        });
        addKeyListener(button);
        add(button);
        components.add(button);
    }

    protected JTextField createTextField(int x, int y, int symbolNumbers, int width, int height, int alignment) {
        JTextField textField = new JTextField(symbolNumbers);
        textField.setSize(width, height);
        textField.setHorizontalAlignment(alignment);
        textField.setLocation(x, y);
        textField.setFont(basicFont);
        return textField;
    }

    protected JPasswordField createPasswordField(int x, int y) {
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setEchoChar('*');
        passwordField.setSize(componentSize);
        passwordField.setHorizontalAlignment(SwingConstants.CENTER);
        passwordField.setLocation(x, y);
        passwordField.setFont(basicFont);
        return passwordField;
    }

    protected JTextArea createTextArea(String text, int width, int height, int x, int y, boolean isEditable) {
        JTextArea textArea = new JTextArea(text);
        textArea.setFont(basicFont);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(false);  //don't work with true
        textArea.setEditable(isEditable);
        textArea.setSize(width, height);
        textArea.setLocation(x, y);
        textArea.setBackground(parentFrame.getContentPane().getBackground());
        return textArea;
    }

    protected void drawPreviousPageButton(boolean isEnabled, List<Component> components) {
        JButton button = createButton(10, exitButtonSize.height + 30, "Предыдущая страница");
        button.addActionListener(actionEvent -> {
            int index = getCurrentComponentIndex();
            parentFrame.getContentPane().getComponent(index).setVisible(false);
            parentFrame.getContentPane().getComponent(index - 1).setVisible(true);
        });
        button.setSize(pageSwitchButtonSize);
        button.setBackground(BACKGROUND_COLOR);
        button.setBorder(null);
        button.setEnabled(isEnabled);
        if (isEnabled) {
            components.add(button);
        }
        add(button);
    }

    protected void drawNextPageButton(boolean isEnabled, BaseScreen nextScreen, List<Component> components) {
        JButton button = createButton(
                width - parentFrame.getInsets().right - pageSwitchButtonSize.width,
                30 + exitButtonSize.height,
                "Следующая страница"
        );
        button.addActionListener(actionEvent -> {
            int index = getCurrentComponentIndex();
            parentFrame.getContentPane().getComponent(index).setVisible(false);
            if (parentFrame.getContentPane().getComponentCount() == index + 1) {
                parentFrame.add(nextScreen);
                parentFrame.getContentPane().getComponent(parentFrame.getContentPane().getComponentCount() - 1).setVisible(true);
            } else {
                parentFrame.getContentPane().getComponent(index + 1).setVisible(true);
            }
        });
        button.setSize(pageSwitchButtonSize);
        button.setBackground(BACKGROUND_COLOR);
        button.setBorder(null);
        button.setEnabled(isEnabled);
        if (isEnabled) {
            components.add(button);
        }
        add(button);
    }

    protected int getCurrentComponentIndex() {
        int index = 0;
        for (int j = 0; j < parentFrame.getContentPane().getComponentCount(); j++) {
            if (parentFrame.getContentPane().getComponent(j) == this) {
                index = j;
            }
        }
        return index;
    }

    protected void drawCheckBox(int x, int y, List<JCheckBox> checkBoxes, JComboBox<String> comboBox) {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setLocation(x, y);
        checkBox.setSize(new Dimension(20, 20));
        checkBox.addActionListener(actionEvent -> {
            if (checkBox.isSelected()) {
                comboBox.setEnabled(true);
            } else {
                for (JCheckBox cb : checkBoxes) {
                    if (cb.isSelected()) {
                        return;
                    }
                }
                comboBox.setEnabled(false);
            }
        });
        checkBoxes.add(checkBox);
        add(checkBox);
    }

    protected JComboBox<String> createComboBox(String[] items, String username) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(basicFont);
        FontMetrics fm = getFontMetrics(basicFont);
        comboBox.setLocation(usernameX + fm.stringWidth(username) + 10, 0);
        comboBox.setSize(fm.stringWidth(items[0]) + 30, 20);
        return comboBox;
    }

    private void addKeyListener(JButton button) {
        button.addKeyListener(new KeyListener() {
                                  @Override
                                  public void keyTyped(KeyEvent keyEvent) {
                                  }

                                  @Override
                                  public void keyPressed(KeyEvent keyEvent) {
                                      if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                                          button.doClick();
                                      }
                                  }

                                  @Override
                                  public void keyReleased(KeyEvent keyEvent) {
                                  }
                              }
        );
    }
}
