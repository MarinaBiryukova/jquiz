package ui;

import core.exceptions.*;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class LogInScreen extends BaseScreen {
    private final List<Component> components;

    LogInScreen(JFrame parent) {
        super(parent);
        this.components = new ArrayList<>();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        drawGreetingString(g2);
        String description = "Логин:";
        drawDescription(
                g2,
                description,
                (int) (height * 0.28) + componentSize.height / 2
        );
        JTextField loginField = createTextField(
                (width - componentSize.width) / 2,
                (int) (height * 0.28),
                20,
                componentSize.width,
                componentSize.height,
                SwingConstants.CENTER
        );
        add(loginField);
        components.add(loginField);
        description = "Пароль:";
        drawDescription(
                g2,
                description,
                (int) (height * 0.42) + componentSize.height / 2
        );
        JPasswordField passwordField = createPasswordField((width - componentSize.width) / 2, (int) (height * 0.42));
        add(passwordField);
        components.add(passwordField);
        JButton enterButton = createButton((width - componentSize.width) / 2, (int) (height * 0.56), "Войти");
        enterButton.addActionListener(actionEvent -> {
            MainFrame mainFrame = (MainFrame) parentFrame;
            if (mainFrame.getLogInManager().isAdmin(loginField.getText(), new String(passwordField.getPassword()))) {
                parentFrame.getContentPane().remove(0);
                parentFrame.add(new AdminScreen(parentFrame));
                parentFrame.setVisible(true);
            } else {
                try {
                    mainFrame.getLogInManager().logIn(loginField.getText(), new String(passwordField.getPassword()));
                    parentFrame.getContentPane().remove(0);
                    parentFrame.add(new TestScreen(parentFrame, new User(loginField.getText(), new String(passwordField.getPassword()))));
                    parentFrame.setVisible(true);
                } catch (NullFieldsException e) {
                    System.out.println(e.getMessage());
                } catch (WrongPasswordException | EmptyPasswordException | NoSuchUserException | EmptyUsernameException e) {
                    SwingUtilities.invokeLater(() -> new NotificationFrame(e.getMessage(), false).setVisible(true));
                }
            }
        });
        add(enterButton);
        components.add(enterButton);
        drawRegisterButton();
        drawGuestButton();
        new KeyboardListener(components, this);
    }

    private void drawGreetingString(Graphics2D g2) {
        g2.setFont(greetingFont);
        FontMetrics fm = g2.getFontMetrics();
        String greeting = "Добро пожаловать в систему тестирования!";
        int posX = (width - fm.stringWidth(greeting)) / 2;
        int posY = (int) (height * 0.14);
        g2.drawString(greeting, posX, posY);
    }

    private void drawDescription(Graphics2D g2, String description, int y) {
        g2.setFont(basicFont);
        FontMetrics fm = g2.getFontMetrics();
        int x = (width - componentSize.width) / 2 - fm.stringWidth(description) - 10;
        g2.drawString(description, x, y);
    }

    private void drawRegisterButton() {
        JButton registerButton = createButton((width - 2 * componentSize.width - 40) / 2, (int) (height * 0.7), "Зарегистрироваться");
        registerButton.addActionListener(actionEvent -> {
            parentFrame.getContentPane().remove(0);
            parentFrame.add(new RegisterScreen(parentFrame, false));
            parentFrame.setVisible(true);
        });
        add(registerButton);
        components.add(registerButton);
    }

    private void drawGuestButton() {
        JButton guestButton = createButton(width / 2 + 20, (int) (height * 0.7), "Войти как гость");
        guestButton.addActionListener(actionEvent -> {
            parentFrame.getContentPane().remove(0);
            parentFrame.add(new TestScreen(parentFrame, null));
            parentFrame.setVisible(true);
        });
        add(guestButton);
        components.add(guestButton);
    }
}
