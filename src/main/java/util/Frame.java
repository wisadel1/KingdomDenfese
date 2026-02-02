package util;

import javafx.scene.image.Image;

public class Frame {
    private final Image image;
    private final int index;
    private double anchorX;
    private double anchorY;
    private final double width;
    private final double height;

    public Frame(Image image, int index){
        this.image = image;
        this.index = index;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.anchorX = width / 2;
        this.anchorY = height / 2;
    }

    // Getter & Setter（锚点坐标提供Setter，支持编辑）
    public Image getImage() { return image; }
    public int getIndex() { return index; }
    public double getAnchorX() { return anchorX; }
    public void setAnchorX(double anchorX) { this.anchorX = anchorX; }
    public double getAnchorY() { return anchorY; }
    public void setAnchorY(double anchorY) { this.anchorY = anchorY; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}
