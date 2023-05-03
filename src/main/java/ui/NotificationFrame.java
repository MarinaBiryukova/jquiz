package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class NotificationFrame extends JDialog {
    private final Font textFont = new Font("TimesRoman", Font.PLAIN, 16);
    private final Font buttonFont = new Font("TimesRoman", Font.PLAIN, 14);
    private final Color buttonColor = new Color(199, 211, 221);
    private final int height;
    private final int width;
    private final Dimension buttonSize;

    NotificationFrame(String message, boolean isYesButton) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        this.height = (int) (screenSize.height * 0.25);
        this.width = height * 2;
        this.buttonSize = new Dimension((int) (this.width * 0.15), (int) (this.height * 0.2));
        setTitle("Уведомление");
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setResizable(false);
        GridBagLayout gridBagLayout = new GridBagLayout();
        setLayout(gridBagLayout);
        drawMessageTextArea(message, gridBagLayout);
        if (isYesButton) {
            drawYesButton(gridBagLayout);
        } else {
            drawOkButton(gridBagLayout);
        }
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (getContentPane().getComponent(getContentPane().getComponentCount() - 1) instanceof JButton) {
                        ((JButton) getContentPane().getComponent(getContentPane().getComponentCount() - 1)).doClick();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
            }
        });
        setFocusable(true);
    }

    private void drawMessageTextArea(String message, GridBagLayout gridBagLayout) {
        JTextArea text = new JTextArea(message);
        text.setSize(width - 40, height - 100);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setEditable(false);
        text.setFont(textFont);
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.NONE;
        c.gridx = GridBagConstraints.RELATIVE;
        c.gridy = GridBagConstraints.RELATIVE;
        c.insets = new Insets(20, 20, 0, 20);
        c.gridheight = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 0.0;
        c.weighty = 1.0;
        gridBagLayout.setConstraints(text, c);
        text.setBackground(getBackground());
        add(text);
    }

    private void drawYesButton(GridBagLayout gridBagLayout) {
        JButton button = createButton("Да");
        addButton(button, gridBagLayout);
    }

    private void drawOkButton(GridBagLayout gridBagLayout) {
        JButton button = createButton("ОК");
        button.addActionListener(actionEvent -> dispose());
        addButton(button, gridBagLayout);
    }

    private JButton createButton(String name) {
        JButton button = new JButton(name);
        button.setBackground(buttonColor);
        button.setSize(buttonSize);
        button.setMinimumSize(buttonSize);
        button.setMaximumSize(buttonSize);
        button.setPreferredSize(buttonSize);
        button.setFont(buttonFont);
        button.setMargin(new Insets(0, 0, 0, 0));
        return button;
    }

    private void addButton(JButton button, GridBagLayout gridBagLayout) {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.NONE;
        c.gridx = GridBagConstraints.RELATIVE;
        c.gridy = GridBagConstraints.RELATIVE;
        c.insets = new Insets(20, 0, 0, 0);
        c.gridheight = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 0.0;
        c.weighty = 1.0;
        gridBagLayout.setConstraints(button, c);
        add(button);
    }
}
