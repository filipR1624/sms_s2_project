package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Main starter class for the School Management System.
 * Initializes the application and displays the login form.
 */
public class SchoolManagementStarter {

    /**
     * Main method to start the application
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Display a splash screen with loading message
        JFrame splashFrame = createSplashScreen();
        splashFrame.setVisible(true);

        // Run the main application startup in a separate thread
        new Thread(() -> {
            try {
                // Simulate initialization time (in a real app, this would be actual initialization)
                Thread.sleep(2000);

                // Close splash screen and open login form on the Event Dispatch Thread
                SwingUtilities.invokeLater(() -> {
                    splashFrame.dispose();
                    LoginForm loginForm = new LoginForm();
                    loginForm.setVisible(true);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Creates a simple splash screen
     *
     * @return The splash screen frame
     */
    private static JFrame createSplashScreen() {
        JFrame frame = new JFrame("Loading");
        frame.setUndecorated(true);
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("School Management System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel loadingLabel = new JLabel("Loading application...", JLabel.CENTER);
        loadingLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(loadingLabel, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);

        frame.add(panel);
        return frame;
    }
}