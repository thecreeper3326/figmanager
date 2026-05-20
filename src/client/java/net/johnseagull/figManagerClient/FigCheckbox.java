package net.johnseagull.figManagerClient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.Nullable;

/**
 * Custom checkbox.<br>
 * It wouldnt let me extend the standard checkbox class so I just copied the standard one and made some modifications, mainly making it show its value as a label and hiding it when it does not have enough room.
 *
 */
public class FigCheckbox extends AbstractButton {
    private static final Identifier CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE = Identifier.withDefaultNamespace("widget/checkbox_selected_highlighted");
    private static final Identifier CHECKBOX_SELECTED_SPRITE = Identifier.withDefaultNamespace("widget/checkbox_selected");
    private static final Identifier CHECKBOX_HIGHLIGHTED_SPRITE = Identifier.withDefaultNamespace("widget/checkbox_highlighted");
    private static final Identifier CHECKBOX_SPRITE = Identifier.withDefaultNamespace("widget/checkbox");
    private static final int SPACING = 4;
    private static final int BOX_PADDING = 8;
    public int col1 = 0x00000000;
    public int col2 = 0x00000000;
    public int bdr1 = 0x00000000;
    public int bdr2 = 0x00000000;
    public int bdr = 2;
    int col1b = new java.awt.Color(col1, true).brighter().getRGB();
    int col2b = new java.awt.Color(col2, true).brighter().getRGB();
    private boolean selected;
    private final FigCheckbox.OnValueChange onValueChange;
    private MultiLineTextWidget textWidget;
    private Font font;
    public int w;
    private FigCheckbox(
            final int x, final int y, final int maxWidth, final Component message, final Font font, final boolean selected, final FigCheckbox.OnValueChange onValueChange
    ) {
        super(x, y, 0, 0, message);
        this.textWidget = new MultiLineTextWidget(Component.literal(String.valueOf(this.selected)), font);
        this.textWidget.setMaxRows(2);
        this.width = this.adjustWidth(maxWidth, font);
        this.height = this.getAdjustedHeight(font);
        this.selected = selected;
        this.onValueChange = onValueChange;
        this.font = font;

    }

    public int adjustWidth(final int maxWidth, final Font font) {
        this.width = this.getAdjustedWidth(maxWidth, this.getMessage(), font);
        this.textWidget.setMaxWidth(this.width);
        return this.width;
    }

    private int getAdjustedWidth(final int maxWidth, final Component message, final Font font) {
        return Math.min(getDefaultWidth(message, font), maxWidth);
    }

    private int getAdjustedHeight(final Font font) {
        return Math.max(getBoxSize(font), this.textWidget.getHeight());
    }

    private static int getDefaultWidth(final Component message, final Font font) {
        return getBoxSize(font) + 4 + font.width(message);
    }

    public static FigCheckbox.Builder builder(final Component message, final Font font) {
        return new FigCheckbox.Builder(message, font);
    }

    public static int getBoxSize(final Font font) {
        return 9 + 8;
    }

    @Override
    public void onPress(final InputWithModifiers input) {
        this.textWidget = new MultiLineTextWidget(Component.literal(String.valueOf(this.selected)), font);
        this.selected = !this.selected;
        this.onValueChange.onValueChange(this, this.selected);
    }

    public boolean selected() {
        return this.selected;
    }

    @Override
    public void updateWidgetNarration(final NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, this.createNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                output.add(
                        NarratedElementType.USAGE, Component.translatable(this.selected ? "narration.FigCheckbox.usage.focused.uncheck" : "narration.FigCheckbox.usage.focused.check")
                );
            } else {
                output.add(
                        NarratedElementType.USAGE, Component.translatable(this.selected ? "narration.FigCheckbox.usage.hovered.uncheck" : "narration.FigCheckbox.usage.hovered.check")
                );
            }
        }
    }

    @Override
    public void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        Identifier sprite;

        this.textWidget = new MultiLineTextWidget(Component.literal(String.valueOf(this.selected)), font);
        if (w < 44) {
            textWidget = new MultiLineTextWidget(Component.literal(""), font);
        }
        int tempcol1;
        int tempcol2;
        int col1b = new java.awt.Color(col1, true).brighter().getRGB();
        int col2b = new java.awt.Color(col2, true).brighter().getRGB();

        if (this.selected) {

            tempcol1 = this.isFocused() ? col1b : col1;
            tempcol2 = this.isFocused() ? col2b : col2;

        } else {
            tempcol1 = bdr2;
            tempcol2 = bdr1;
        }
        int boxSize = getBoxSize(font)-(bdr*2);
        FigBox box = new FigBox(this.getX(), this.getY(), boxSize, boxSize, tempcol1, tempcol2);
        FigBox border = new FigBox(this.getX()-bdr, this.getY()-bdr, boxSize+bdr, boxSize+bdr, bdr2, bdr1);
        border.extractWidgetRenderState(graphics, mouseX, mouseY, a);
        box.extractWidgetRenderState(graphics, mouseX, mouseY, a);
        int textX = this.getX() + boxSize + 4;
        int textY = this.getY() + boxSize / 2 - this.textWidget.getHeight() / 2;
        this.textWidget.setPosition(textX, textY);
        this.textWidget.visitLines(graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.notClickable(this.isHovered())));

    }

    public static class Builder {
        private final Component message;
        private final Font font;
        private int maxWidth;
        private int x = 0;
        private int y = 0;
        private FigCheckbox.OnValueChange onValueChange = FigCheckbox.OnValueChange.NOP;
        private boolean selected = false;
        @Nullable
        private OptionInstance<Boolean> option = null;
        @Nullable
        private Tooltip tooltip = null;

        private Builder(final Component message, final Font font) {
            this.message = message;
            this.font = font;
            this.maxWidth = FigCheckbox.getDefaultWidth(message, font);
        }

        public FigCheckbox.Builder pos(final int x, final int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public FigCheckbox.Builder onValueChange(final FigCheckbox.OnValueChange onValueChange) {
            this.onValueChange = onValueChange;
            return this;
        }

        public FigCheckbox.Builder selected(final boolean selected) {
            this.selected = selected;
            this.option = null;
            return this;
        }

        public FigCheckbox.Builder selected(final OptionInstance<Boolean> option) {
            this.option = option;
            this.selected = option.get();
            return this;
        }

        public FigCheckbox.Builder tooltip(final Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public FigCheckbox.Builder maxWidth(final int maxWidth) {
            this.maxWidth = maxWidth;
            return this;
        }

        public FigCheckbox build() {
            FigCheckbox.OnValueChange onChange = this.option == null ? this.onValueChange : (FigCheckbox, value) -> {
                this.option.set(value);
                this.onValueChange.onValueChange(FigCheckbox, value);
            };
            FigCheckbox box = new FigCheckbox(this.x, this.y, this.maxWidth, this.message, this.font, this.selected, onChange);
            box.setTooltip(this.tooltip);
            return box;
        }
    }

    public interface OnValueChange {
        OnValueChange NOP = (FigCheckbox, value) -> {};

        void onValueChange(FigCheckbox FigCheckbox, boolean value);
    }
}

