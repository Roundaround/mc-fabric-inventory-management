package me.roundaround.inventorymanagement.client.gui.screen;

import java.util.LinkedList;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.client.InventoryButtonsManager;
import me.roundaround.inventorymanagement.client.gui.InventoryManagementButton;
import me.roundaround.roundalib.config.gui.GuiUtil;
import me.roundaround.roundalib.config.gui.screen.PositionEditScreen;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class PerScreenPositionEditScreen extends PositionEditScreen {
  private final LinkedList<InventoryManagementButton> buttons = new LinkedList<>();

  private boolean isPlayerInventory;

  public PerScreenPositionEditScreen(Screen parent, boolean isPlayerInventory) {
    super(Text.literal("Placeholder"), parent, generateDummyConfigOption(parent, isPlayerInventory));
    this.isPlayerInventory = isPlayerInventory;
  }

  private static PositionConfigOption generateDummyConfigOption(Screen parent, boolean isPlayerInventory) {
    Position currentValue = InventoryManagementMod.CONFIG.SCREEN_POSITIONS
        .get(parent, isPlayerInventory)
        .orElse(InventoryManagementMod.CONFIG.DEFAULT_POSITION.getValue());
    PositionConfigOption dummyConfig = PositionConfigOption
        .builder(
            InventoryManagementMod.CONFIG.SCREEN_POSITIONS.getScreenKey(parent, isPlayerInventory),
            "",
            InventoryManagementMod.CONFIG.DEFAULT_POSITION.getDefault())
        .build();
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
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    matrixStack.push();
    matrixStack.translate(0, 0, -51);
    parent.render(matrixStack, mouseX, mouseY, partialTicks);
    matrixStack.pop();

    super.render(matrixStack, mouseX, mouseY, partialTicks);
  }

  @Override
  protected void renderBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    // TODO: Inventory player preview and items render on top of background
    renderDarkenBackground(matrixStack, mouseX, mouseY, partialTicks);
  }

  @Override
  protected void renderContent(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    super.renderContent(matrixStack, mouseX, mouseY, partialTicks);

    buttons.forEach((button) -> button.render(matrixStack, mouseX, mouseY, partialTicks));

    drawTextWithShadow(
        matrixStack,
        textRenderer,
        Text.literal(getValue().toString()),
        4,
        4,
        GuiUtil.LABEL_COLOR);
  }

  @Override
  protected void commitValueToConfig() {
    if (isDirty()) {
      InventoryManagementMod.CONFIG.SCREEN_POSITIONS.set(parent, isPlayerInventory, getValue());
      InventoryManagementMod.CONFIG.saveToFile();
    }
  }
}
