package net.johnseagull.figManagerClient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/**
 * Custom button thing
 */
public class FigButton extends Button {
    public Object customData;
    public int col1;
    public int col2;
    public int border1;
    public int border2;
    public int borderSize;
    public Component label;
    public boolean pressed = false;
    public boolean inList = false;
    public OnPress onPress;
    @Override
    public void onPress(final InputWithModifiers input) {
        pressed = true;
    }

    @Override
    public void onRelease(MouseButtonEvent event) {
        if (isHovered) {
            this.onPress.onPress(this);
        }
        pressed = false;
    }

    /**
     * Creates a FigButton widget
     * @param label Text to be displayed on the button
     * @param event Event to be executed on mouseRelease
     * @param col1 Top gradient color
     * @param col2 Bottom gradient color
     * @param border1 Top border color
     * @param border2 Bottom border color
     * @param borderSize Border thickness
     */
    public FigButton(int x, int y, int width, int height, Component label, OnPress event, int col1, int col2, int border1, int border2, int borderSize) {

        super(x, y, width, height, label, event, DEFAULT_NARRATION);
        this.col1 = col1;
        this.col2 = col2;
        this.borderSize = borderSize;
        this.border1 = border1;
        this.border2 = border2;
        this.label = label;
        this.onPress = event;
    }
    boolean e = false;

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int x = this.getX();
        int y = this.getY();
        graphics.fillGradient(x-borderSize, y -borderSize,x + width +borderSize, y+height + borderSize,border1,border2);
        graphics.fillGradient(x, y, x +width,y + height,col1,col2);
        int col1b = new java.awt.Color(col1, true).brighter().getRGB();
        int col2b = new java.awt.Color(col2, true).brighter().getRGB();
        if (isHovered) {
            graphics.fillGradient(x, y, x +width,y + height,col1b,col2b);
        } else {
            pressed = false;
        }
        if (pressed) {
            graphics.fillGradient(x, y, x +width,y + height,col2,col1);
        }
        graphics.centeredText(Minecraft.getInstance().font,label,x+width/2,y+height/4,0xFFFFFFFF);

    }
}



