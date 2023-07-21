package me.roundaround.inventorymanagement.client.gui.screen;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.client.gui.widget.PerScreenConfigListWidget;
import me.roundaround.inventorymanagement.config.option.PerScreenConfigOption;
import me.roundaround.roundalib.client.gui.GuiUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class PerScreenConfigScreen extends Screen {
  private static final int LIST_MIN_WIDTH = 400;
  private static final int BUTTON_WIDTH = 100;
  private static final int BUTTON_HEIGHT = 20;

  private final Screen parent;
  private final PerScreenConfigOption configOption;

  public PerScreenConfigScreen(Screen parent, PerScreenConfigOption configOption) {
    super(Text.translatable("inventorymanagement.perscreen.title"));
    this.parent = parent;
    this.configOption = configOption;
  }

  @Override
  public void init() {
    int listWidth = (int) Math.max(LIST_MIN_WIDTH, width / 1.5f);
    int listLeft = (int) ((width / 2f) - (listWidth / 2f));
    int listHeight = this.height - 64;
    int listTop = 32;

    addDrawableChild(new PerScreenConfigListWidget(this.client,
        listLeft,
        listTop,
        listWidth,
        listHeight,
        this.configOption,
        this.parent));

    addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, (button) -> this.discardAndExit())
        .position(width / 2 - BUTTON_WIDTH - 2, height - BUTTON_HEIGHT - 10)
        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
        .build());
    addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, (button) -> this.saveAndExit())
        .position(width / 2 + 2, height - BUTTON_HEIGHT - 10)
        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
        .build());
  }

  private void saveAndExit() {
    InventoryManagementMod.CONFIG.saveToFile();
    this.close();
  }

  private void discardAndExit() {
    InventoryManagementMod.CONFIG.loadFromFile();
    this.close();
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
