package net.johnseagull.figManagerClient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.min;

public class FigList<T extends AbstractWidget & Renderable> extends AbstractWidget {
    public List<String> widgets = new ArrayList<>();
    private List<String> k = new ArrayList<>();
    private List<String> v = new ArrayList<>();
    private int y = 0;
    private int X = 0;
    public int Y = 0;
    public int count = 0;
    public int offset = 0;
    private int mx = 0;
    private int my = 0;
    public int columns = 1;
    boolean hovered = false;
    public FigList(int x, int yy, int width, int height, Component message, int c) {
        super(x, yy, width, height, message);
        Y = yy;
        X = x;
        columns = c;
        FigManagerClient.clientLogger.info(y+"");
    }
    boolean temp = false;
    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {

        FigBox bgTop = new FigBox(0,Y,this.width+10,30,0xFF000000,0x11000000);
        bgTop.extractWidgetRenderState(graphics,mouseX,mouseY,a);
        FigBox bgBottom = new FigBox(0,Y+this.height-30,this.width+10,30,0x11000000,0xFF000000);
        bgBottom.extractWidgetRenderState(graphics,mouseX,mouseY,a);

        y = Y + offset +5;
        count = k.size();
        if (columns == 2) {
            for (int i = 0; i < k.size(); i++) {

                if (y >= this.Y && y <= this.Y + this.height) {
                    graphics.text(Minecraft.getInstance().font, k.get(i), X, y, 0xFFFFFFFF);
                    graphics.text(Minecraft.getInstance().font, v.get(i), X + this.width / 2, y, 0xFFFFFFFF);
                }
                y+=22;
            }
        } else {
            for (int i = 0; i < k.size(); i++) {

                if (y >= this.Y && y <= this.Y + this.height) {
                    graphics.text(Minecraft.getInstance().font, k.get(i), X, y, 0xFFFFFFFF);
                }
                y+=22;

            }
        }

        mx = mouseX;
        my = mouseY;
        FigBox thumb = new FigBox(
                this.width - 8,
                this.Y,
                6,
                20, 0xFF606060, 0xFF303030, 0xFF202020, 0xFF000000, 1);
        int max = (count * 22) - this.height;
        float progress = ((float) offset / max);
        int trackSpace = (this.height) - thumb.height;
        thumb.y = Math.min(this.Y - (int) (min(progress, 1.0f) * trackSpace),this.Y + this.height-thumb.height);
        if (count > this.height/22) {
            thumb.extractWidgetRenderState(graphics, mouseX, mouseY, a);
        }

        if( mx > this.X && mx < this.X + this.width && my > this.Y && my < this.Y + this.height) {
            temp=true;
        }
        hovered = temp;
      

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {

    }
    public void shift(double amount) {
        if (offset >= 0 && amount < 0) {
            temp = false;

        }
        if (offset <= -count * 22 + this.height && amount > 0) {
            temp = false;
        }
        if (offset < 0 && amount > 0.0) {
            offset += (int) amount * 22;

        }
        if (offset > -count * 22 + this.height && amount < 0.0) {
            offset += (int) amount * 22;
        } else {
            temp = false;
        }

    }

    public void addWidget(String key, String value) {
        k.add(key);
        v.add(value);
    }
    public void addWidget(String txt) {
        k.add(txt);
    }
    public void clearWidgets() {
        k.clear();
        v.clear();
    }




}