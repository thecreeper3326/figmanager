package net.johnseagull.figManagerClient;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class FigBox extends AbstractWidget {
    public int x;
    public int y;
    public int width;
    public int height;
    public int col1;
    public int col2;
    public int border1 = 0x00000000;
    public int border2 = 0x00000000;
    public int borderSize = 0;
    public boolean inList = false;
    public FigBox(int x, int y, int width, int height, int col1, int col2, int border1, int border2, int borderSize) {
        super(x, y, width, height, Component.empty());
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.col1 = col1;
        this.col2 = col2;
        this.borderSize = borderSize;
        this.border1 = border1;
        this.border2 = border2;
    }
    public FigBox(int x, int y, int width, int height, int col1, int col2) {
        super(x, y, width, height, Component.empty());
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.col1 = col1;
        this.col2 = col2;
    }
    public FigBox(int x, int y, int width, int height, int col) {
        super(x, y, width, height, Component.empty());
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.col1 = col;
        this.col2 = col;
    }
    public int getY() {
        return y;
    }
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return false;
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.fillGradient(x-borderSize,y-borderSize,x+width+borderSize,y+height+borderSize,border1,border2);
        graphics.fillGradient(x,y,x+width,y+height,col1,col2);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {

    }
    protected void handleCursor(final GuiGraphicsExtractor graphics) {
        if (this.isHovered()) {
            graphics.requestCursor(CursorTypes.ARROW);
        }
    }
}
