package Systems.Window;

import Systems.Login.LoginPanel;
import java.awt.*;
import javax.swing.*;

public class Window extends JFrame {

    public Window() {
        super("Hospital Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new FlowLayout());

        add(new LoginPanel());

        setVisible(true);
    }
}
