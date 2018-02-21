package display;

import core.Spyder;

import javax.swing.*;
import java.awt.*;

public class Window extends Canvas {
    private JFrame frame;

    public Window(int width, int height, String title, Spyder spyder) {
        frame = new JFrame(title);
        frame.setPreferredSize(new Dimension(width, height));
        frame.setMaximumSize(new Dimension(width, height));
        frame.setMinimumSize(new Dimension(width, height));
        frame.add(spyder);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.requestFocus();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public void setTitle(String title) {
        frame.setTitle(title);
    }
}

