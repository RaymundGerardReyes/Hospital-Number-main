package Systems.Main;

import Systems.Dashboard.Dashboard;
import Systems.Login.LoginPanel;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame loginFrame = new JFrame("Hospital Management System - Login");
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            LoginPanel loginPanel = new LoginPanel();
            loginFrame.setContentPane(loginPanel);
            loginFrame.pack();
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setVisible(true);

            // Add login success listener
            LoginPanel.addLoginSuccessListener(() -> {
                loginFrame.dispose(); // Close the login frame
                SwingUtilities.invokeLater(() -> {
                    JFrame dashboardFrame = new Dashboard();
                    dashboardFrame.setVisible(true);
                });
            });
        });
    }
}