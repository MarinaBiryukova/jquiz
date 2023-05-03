package ui;

import core.DatabaseManager;
import core.LogInManager;
import core.QuestionManager;
import core.ResultManager;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(232, 238, 242);
    private final LogInManager logInManager;
    private final ResultManager resultManager;
    private final QuestionManager questionManager;

    public MainFrame(String dbFolderName) {
        setTitle("JQuiz");
        this.logInManager = new LogInManager(dbFolderName);
        this.resultManager = new ResultManager(dbFolderName);
        this.questionManager = new QuestionManager(dbFolderName);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int height = (int) (screenSize.height * 0.55);
        int width = (int) (height * 1.5);
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        DatabaseManager dbm = new DatabaseManager(dbFolderName);
        JComponent component;
        if (dbm.getUsersSize() == 0) {
            component = new RegisterScreen(this, true);
        } else {
            component = new LogInScreen(this);
        }
        component.setAlignmentX(Component.CENTER_ALIGNMENT);
        component.setAlignmentY(Component.CENTER_ALIGNMENT);
        getContentPane().setBackground(BACKGROUND_COLOR);
        add(component);
    }

    LogInManager getLogInManager() {
        return logInManager;
    }

    ResultManager getResultManager() {
        return resultManager;
    }

    QuestionManager getQuestionManager() {
        return questionManager;
    }
}
