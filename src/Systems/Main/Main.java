package Systems.Main;

import Systems.Dashboard.Dashboard;
import Systems.Login.LoginPanel;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame loginFrame = new JFrame("Login");
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setContentPane(new LoginPanel());
            loginFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setVisible(true);

            // Create a new JFrame for the Dashboard
            JFrame dashboardFrame = new JFrame("Dashboard");
            dashboardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            dashboardFrame.setContentPane(new Dashboard());
            dashboardFrame.pack();
            dashboardFrame.setVisible(false);

            // Show the Dashboard when the login is successful
            LoginPanel.addLoginSuccessListener(() -> {
                dashboardFrame.setVisible(true);
                loginFrame.setVisible(false);
            });
        });
    }
}