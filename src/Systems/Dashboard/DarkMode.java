package Systems.Dashboard;

import java.awt.Color;
import javax.swing.JComponent;

public class DarkMode {
    private boolean isDarkMode = true; // Set to true by default
    private static final Color DARK_BLUE = new Color(0, 20, 60); // Dark blue color
    private static final Color LIGHT_BLUE = new Color(100, 150, 200);
    private static final Color WHITE = Color.WHITE;
    private static final Color LIGHT_GRAY = new Color(200, 200, 200);


    private Color inputBackgroundColor;
    private Color inputTextColor;
    private Color buttonBackgroundColor;
    private Color buttonTextColor;
    private Color borderColor;
    private Color titleTextColor;

    public DarkMode() {
        this.isDarkMode = false; // Initialize with default value
    }

     // Getter for the defined color
     public Color getTitleTextColor() {
        return titleTextColor;
    }

    // Setter for the defined color (optional)
    public void setTitleTextColor(Color color) {
        this.titleTextColor = color;
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

    public Color getInputBackgroundColor() {
        return inputBackgroundColor;
    }

    public Color getInputTextColor() {
        return inputTextColor;
    }

    public Color getButtonBackgroundColor() {
        return buttonBackgroundColor;
    }

    public Color getButtonTextColor() {
        return buttonTextColor;
    }

    public Color getBorderColor() {
        if (borderColor == null) {
            // Set a default color if the border color is null
            borderColor = Color.GRAY;
        }
        return borderColor;
    }

    // Setters for the defined colors (optional)
    public void setInputBackgroundColor(Color color) {
        this.inputBackgroundColor = color;
    }

    public void setInputTextColor(Color color) {
        this.inputTextColor = color;
    }

    public void setButtonBackgroundColor(Color color) {
        this.buttonBackgroundColor = color;
    }

    public void setButtonTextColor(Color color) {
        this.buttonTextColor = color;
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
    }


}