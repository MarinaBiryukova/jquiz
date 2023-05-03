package ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardListener implements KeyListener {
    private final JComponent screen;
    private final List<Component> components;
    private int selectedIndex;

    KeyboardListener(List<Component> components, JComponent screen) {
        this.screen = screen;
        this.components = components;
        for (Component component : components) {
            component.addKeyListener(this);
        }
        components.get(0).requestFocus();
        this.selectedIndex = 0;
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
            if (selectedIndex != components.size() - 1) {
                selectedIndex++;
            } else {
                selectedIndex = 0;
            }
            components.get(selectedIndex).requestFocus();
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
            if (selectedIndex != 0) {
                selectedIndex--;
            } else {
                selectedIndex = components.size() - 1;
            }
            components.get(selectedIndex).requestFocus();
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
            if (components.get(selectedIndex) instanceof JButton) {
                ((JButton) components.get(selectedIndex)).doClick();
            } else if (components.get(selectedIndex) instanceof JCheckBox) {
                ((JCheckBox) components.get(selectedIndex)).setSelected(!((JCheckBox) components.get(selectedIndex)).isSelected());
                if (screen instanceof QuestionsBaseScreen || screen instanceof ResultsBaseScreen) {
                    if (components.get(components.size() - 1) instanceof JComboBox) {
                        @SuppressWarnings("unchecked")
                        JComboBox<String> settings = (JComboBox<String>) components.get(components.size() - 1);
                        BaseScreen.setComboBoxEnabled(settings, getCheckBoxes());
                    }
                }
            } else if (components.get(selectedIndex) instanceof JComboBox) {
                ((JComboBox<?>) components.get(selectedIndex)).setSelectedIndex(0);
            } else {
                if (selectedIndex != components.size() - 1) {
                    selectedIndex++;
                } else {
                    selectedIndex = 0;
                }
                components.get(selectedIndex).requestFocus();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }

    List<JCheckBox> getCheckBoxes() {
        List<JCheckBox> list = new ArrayList<>();
        for (Component component : components) {
            if (component instanceof JCheckBox) {
                list.add((JCheckBox) component);
            }
        }
        return list;
    }
}
