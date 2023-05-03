package ui;

import core.exceptions.EmptyPasswordException;
import core.exceptions.EmptyUsernameException;
import core.exceptions.UserAlreadyExistsException;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RegisterScreen extends BaseScreen {
    private final int componentX;
    private final boolean isAdmin;
    private final List<Component> components;

    RegisterScreen(JFrame parent, boolean isAdmin) {
        super(parent);
        this.componentX = (width - componentSize.width) / 2;
        this.isAdmin = isAdmin;
        this.components = new ArrayList<>();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        String description = "Логин:";
        drawDescription(
                g2,
                description,
                (int) (height * 0.28) + componentSize.height / 2
        );
        JTextField loginField = createTextField(
                componentX,
                (int) (height * 0.28),
                20,
                componentSize.width,
                componentSize.height,
                SwingConstants.CENTER
        );
        if (isAdmin) {
            loginField.setText("admin");
            loginField.setEditable(false);
        }
        add(loginField);
        components.add(loginField);
        description = "Пароль:";
        drawDescription(
                g2,
                description,
                (int) (height * 0.42) + componentSize.height / 2
        );
        JPasswordField passwordField = createPasswordField(componentX, (int) (height * 0.42));
        add(passwordField);
        components.add(passwordField);
        JButton register = createButton(componentX, (int) (height * 0.56), "Зарегистрироваться");
        register.addActionListener(actionEvent -> {
            try {
                MainFrame mainFrame = (MainFrame) parentFrame;
                if (isAdmin) {
                    mainFrame.getLogInManager().registerAdmin(loginField.getText(), new String(passwordField.getPassword()));
                    parentFrame.getContentPane().remove(0);
                    parentFrame.add(new LogInScreen(parentFrame));
                } else {
                    mainFrame.getLogInManager().register(loginField.getText(), new String(passwordField.getPassword()));
                    parentFrame.getContentPane().remove(0);
                    parentFrame.add(new TestScreen(parentFrame, new User(loginField.getText(), new String(passwordField.getPassword()))));
                }
                parentFrame.setVisible(true);
            } catch (UserAlreadyExistsException | EmptyPasswordException | EmptyUsernameException e) {
                SwingUtilities.invokeLater(() -> new NotificationFrame(e.getMessage(), false).setVisible(true));
            }
        });
        add(register);
        components.add(register);
        if (isAdmin) {
            drawGreetingString(g2);
        } else {
            drawBackButton();
        }
        new KeyboardListener(components, this);
    }

    private void drawDescription(Graphics2D g2, String description, int y) {
        g2.setFont(basicFont);
        FontMetrics fm = g2.getFontMetrics();
        int x = (width - componentSize.width) / 2 - fm.stringWidth(description) - 10;
        g2.drawString(description, x, y);
    }

    private void drawGreetingString(Graphics2D g2) {
        g2.setFont(greetingFont);
        FontMetrics fm = g2.getFontMetrics();
        String greeting = "Для начала работы создайте аккаунт администратора";
        int posX = (width - fm.stringWidth(greeting)) / 2;
        int posY = (int) (height * 0.14);
        g2.drawString(greeting, posX, posY);
    }

    private void drawBackButton() {
        JButton backButton = createBackButton();
        backButton.addActionListener(actionEvent -> {
            parentFrame.getContentPane().remove(0);
            parentFrame.add(new LogInScreen(parentFrame));
            parentFrame.setVisible(true);
        });
        components.add(backButton);
        add(backButton);
    }
}
