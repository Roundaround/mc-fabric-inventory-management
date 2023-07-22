package me.roundaround.inventorymanagement.client.gui.screen;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.client.InventoryButtonsManager;
import me.roundaround.inventorymanagement.client.gui.widget.button.InventoryManagementButton;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.screen.PositionEditScreen;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.LinkedList;

public class PerScreenPositionEditScreen extends PositionEditScreen {
  private final LinkedList<InventoryManagementButton> buttons = new LinkedList<>();

  private final boolean isPlayerInventory;

  public PerScreenPositionEditScreen(
      Screen parent, Screen workingScreen, boolean isPlayerInventory) {
    super(Text.translatable("inventorymanagement.position_edit.title"),
        parent,
        generateDummyConfigOption(workingScreen, isPlayerInventory));
    this.isPlayerInventory = isPlayerInventory;
  }

  private static PositionConfigOption generateDummyConfigOption(
      Screen parent, boolean isPlayerInventory) {
    Position currentValue =
        InventoryManagementMod.CONFIG.PER_SCREEN_CONFIGS.getPosition(parent, isPlayerInventory);
    if (currentValue == null) {
      currentValue = InventoryManagementMod.CONFIG.DEFAULT_POSITION.getValue();
    }

    PositionConfigOption dummyConfig = PositionConfigOption.builder(InventoryManagementMod.CONFIG,
        InventoryManagementMod.CONFIG.PER_SCREEN_CONFIGS.getScreenKey(parent),
        "",
        InventoryManagementMod.CONFIG.DEFAULT_POSITION.getDefault()).build();
    dummyConfig.setValue(currentValue);
    return dummyConfig;
  }

  @Override
  protected void init() {
    super.init();

    buttons.addAll(isPlayerInventory
        ? InventoryButtonsManager.INSTANCE.getPlayerButtons()
        : InventoryButtonsManager.INSTANCE.getContainerButtons());

    Screens.getButtons(parent).removeIf((button) -> button instanceof InventoryManagementButton);

    for (int i = 0; i < buttons.size(); i++) {
      buttons.get(i).setOffset(InventoryButtonsManager.INSTANCE.getButtonPosition(i, getValue()));
    }
  }

  @Override
  protected void setValue(Position value) {
    super.setValue(value);

    for (int i = 0; i < buttons.size(); i++) {
      buttons.get(i).setOffset(InventoryButtonsManager.INSTANCE.getButtonPosition(i, getValue()));
    }
  }

  @Override
  public void render(DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
    MatrixStack matrixStack = drawContext.getMatrices();
    matrixStack.push();
    matrixStack.translate(0, 0, -51);
    parent.render(drawContext, mouseX, mouseY, partialTicks);
    matrixStack.pop();

    super.render(drawContext, mouseX, mouseY, partialTicks);
  }

  @Override
  protected void renderBackground(
      DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
    renderDarkenBackground(drawContext, mouseX, mouseY, partialTicks);
  }

  @Override
  protected void renderContent(
      DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
    super.renderContent(drawContext, mouseX, mouseY, partialTicks);

    buttons.forEach((button) -> button.render(drawContext, mouseX, mouseY, partialTicks));

    drawContext.drawTextWithShadow(textRenderer,
        Text.literal(getValue().toString()),
        4,
        4,
        GuiUtil.LABEL_COLOR);
  }

  @Override
  protected void commitValueToConfig() {
    if (isDirty()) {
      if (getValue() == configOption.getDefault()) {
        InventoryManagementMod.CONFIG.PER_SCREEN_CONFIGS.clearPosition(parent, isPlayerInventory);
      } else {
        InventoryManagementMod.CONFIG.PER_SCREEN_CONFIGS.setPosition(parent,
            isPlayerInventory,
            getValue());
      }
      InventoryManagementMod.CONFIG.saveToFile();
    }
  }
}
