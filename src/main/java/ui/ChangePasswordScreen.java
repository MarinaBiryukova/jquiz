package ui;

import core.exceptions.EmptyPasswordException;
import core.exceptions.EmptyUsernameException;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ChangePasswordScreen extends BaseScreen {
    private final User user;
    private final List<Component> components;

    ChangePasswordScreen(JFrame parent, User user) {
        super(parent);
        this.user = user;
        this.components = new ArrayList<>();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        String description = "Текущий пароль:";
        drawDescription(
                g2,
                description,
                (int) (height * 0.28) + componentSize.height / 2
        );
        JPasswordField oldPassword = createPasswordField((width - componentSize.width) / 2, (int) (height * 0.28));
        add(oldPassword);
        components.add(oldPassword);
        description = "Новый пароль:";
        drawDescription(
                g2,
                description,
                (int) (height * 0.42) + componentSize.height / 2
        );
        JPasswordField newPassword = createPasswordField((width - componentSize.width) / 2, (int) (height * 0.42));
        add(newPassword);
        components.add(newPassword);
        JButton submitButton = createButton((width - componentSize.width) / 2, (int) (height * 0.56), "Подтвердить");
        submitButton.addActionListener(actionEvent -> {
            if (user.getPassword().equals(new String(oldPassword.getPassword()))) {
                try {
                    MainFrame mainFrame = (MainFrame) parentFrame;
                    mainFrame.getLogInManager().changePassword(user.getName(), new String(newPassword.getPassword()));
                    showNecessaryScreen();
                } catch (EmptyPasswordException | EmptyUsernameException e) {
                    SwingUtilities.invokeLater(() -> new NotificationFrame(e.getMessage(), false).setVisible(true));
                }
            } else {
                SwingUtilities.invokeLater(() -> new NotificationFrame("Текущий пароль введен неверно!", false).setVisible(true));
            }
        });
        add(submitButton);
        components.add(submitButton);
        drawBackButton();
        new KeyboardListener(components, this);
    }

    private void drawDescription(Graphics2D g2, String description, int y) {
        g2.setFont(basicFont);
        FontMetrics fm = g2.getFontMetrics();
        int x = (width - componentSize.width) / 2 - fm.stringWidth(description) - 10;
        g2.drawString(description, x, y);
    }

    private void drawBackButton() {
        JButton backButton = createBackButton();
        backButton.addActionListener(actionEvent -> showNecessaryScreen());
        add(backButton);
        components.add(backButton);
    }

    private void showNecessaryScreen() {
        parentFrame.getContentPane().remove(0);
        if (user.getName().equals("admin")) {
            parentFrame.add(new AdminScreen(parentFrame));
        } else {
            parentFrame.add(new TestScreen(parentFrame, user));
        }
        parentFrame.setVisible(true);
    }
}
