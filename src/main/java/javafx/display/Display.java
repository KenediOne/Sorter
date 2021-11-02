package javafx.display;

import java.awt.*;

public class Display {

    private int width;

    private int height;

    public Display() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.width = screenSize.width;
        this.height = screenSize.height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
