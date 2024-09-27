package Systems.Dashboard;

import java.awt.Color;
import javax.swing.JComponent;

public class DarkMode {
    private boolean isDarkMode = true; // Set to true by default
    private static final Color DARK_BLUE = new Color(0, 20, 60); // Dark blue color
    private static final Color LIGHT_BLUE = new Color(100, 150, 200);
    private static final Color WHITE = Color.WHITE;
    private static final Color LIGHT_GRAY = new Color(200, 200, 200);


    public DarkMode() {
        this.isDarkMode = false; // Initialize with default value
    }

    public boolean isDarkMode() {
        return isDarkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.isDarkMode = darkMode;
    }

    // Other methods...


    public void toggleDarkMode() {
        isDarkMode = !isDarkMode;
    }

    public Color getBackgroundColor() {
        return isDarkMode ? DARK_BLUE : WHITE;
    }

    public Color getCardBackgroundColor() {
        return isDarkMode ? new Color(0, 30, 80) : LIGHT_GRAY;
    }

    public Color getTextColor() {
        return isDarkMode ? WHITE : Color.BLACK;
    }

    public Color getMutedTextColor() {
        return isDarkMode ? LIGHT_GRAY : Color.GRAY;
    }

    public Color getPrimaryColor() {
        return isDarkMode ? LIGHT_BLUE : DARK_BLUE;
    }

    public void updateComponentColors(JComponent component) {
        component.setBackground(getBackgroundColor());
        component.setForeground(getTextColor());
    }
}