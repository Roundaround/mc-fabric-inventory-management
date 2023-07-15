package me.roundaround.inventorymanagement.client.gui.screen;

import me.roundaround.inventorymanagement.client.gui.widget.ConfigListWidget;
import me.roundaround.roundalib.client.gui.GuiUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class PerScreenConfigScreen extends Screen {
  private static final int LIST_MIN_WIDTH = 400;

  private final Screen parent;

  public PerScreenConfigScreen(Screen parent, Text title) {
    super(title);
    this.parent = parent;
  }

  @Override
  public void init() {
    int listWidth = (int) Math.max(LIST_MIN_WIDTH, width / 1.5f);
    int listLeft = (int) ((width / 2f) - (listWidth / 2f));
    int listHeight = this.height - 64;
    int listTop = 32;

    addDrawableChild(new ConfigListWidget(this.client, listLeft, listTop, listWidth, listHeight));
  }

  @Override
  public void close() {
    if (this.client == null) {
      return;
    }
    this.client.setScreen(this.parent);
  }

  @Override
  public void render(DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
    GuiUtil.renderBackgroundInRegion(64, 0, this.height, 0, this.width);

    drawContext.drawCenteredTextWithShadow(this.textRenderer,
        this.title,
        this.width / 2,
        20,
        GuiUtil.LABEL_COLOR);

    super.render(drawContext, mouseX, mouseY, partialTicks);
  }
}