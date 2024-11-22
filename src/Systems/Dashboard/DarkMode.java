package Systems.Dashboard;

import java.awt.Color;
import javax.swing.JComponent;

public class DarkMode implements DarkModeInterface {
    private boolean isDarkMode;
    private static final Color DARK_BLUE = new Color(0, 20, 60);
    private static final Color LIGHT_BLUE = new Color(100, 150, 200);
    private static final Color WHITE = Color.WHITE;
    private static final Color LIGHT_GRAY = new Color(200, 200, 200);

    private Color primaryColor;
    private Color backgroundColor;
    private Color textColor;
    private Color cardBackgroundColor;

    private Color inputBackgroundColor;
    private Color inputTextColor;
    private Color buttonBackgroundColor;
    private Color buttonTextColor;
    private Color borderColor;
    private Color titleTextColor;

    public DarkMode() {
        this(false);
    }

    public DarkMode(boolean isDarkMode) {
        this.isDarkMode = isDarkMode;
        initializeColors();
    }

    private void initializeColors() {
        if (isDarkMode) {
            primaryColor = new Color(0, 103, 66).darker();
            backgroundColor = new Color(50, 50, 50);
            textColor = Color.WHITE;
            cardBackgroundColor = new Color(60, 60, 60);
        } else {
            primaryColor = new Color(0, 103, 66);
            backgroundColor = Color.WHITE;
            textColor = Color.BLACK;
            cardBackgroundColor = Color.WHITE;
        }
        inputBackgroundColor = isDarkMode ? DARK_BLUE : WHITE;
        inputTextColor = isDarkMode ? WHITE : Color.BLACK;
        buttonBackgroundColor = primaryColor;
        buttonTextColor = isDarkMode ? WHITE : Color.BLACK;
        borderColor = Color.GRAY;
        titleTextColor = textColor;
    }

    public boolean isDarkMode() {
        return isDarkMode;
    }

    public void setDarkMode(boolean darkMode) {
        if (this.isDarkMode != darkMode) {
            this.isDarkMode = darkMode;
            initializeColors();
        }
    }

    public void toggleDarkMode() {
        setDarkMode(!isDarkMode);
    }

    @Override
    public boolean isEnabled() {
        return isDarkMode;
    }

    @Override
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public Color getTextColor() {
        return textColor;
    }

    public Color getCardBackgroundColor() {
        return cardBackgroundColor;
    }

    public Color getMutedTextColor() {
        return isDarkMode ? LIGHT_GRAY : Color.GRAY;
    }

    public Color getPrimaryColor() {
        return primaryColor;
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
        return borderColor;
    }

    public Color getTitleTextColor() {
        return titleTextColor;
    }

    public void setInputBackgroundColor(Color color) {
        this.inputBackgroundColor = color;
    }

    public void setInputTextColor(Color color) {
        this.inputTextColor = color;
    }

    public void setButtonBackgroundColor(Color color) {
        this.buttonBackgroundColor = color;
    }

    public boolean isDarkModeEnabled() {
        return isDarkMode;
    }

    public void setButtonTextColor(Color color) {
        this.buttonTextColor = color;
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
    }

    public void setTitleTextColor(Color color) {
        this.titleTextColor = color;
    }
}