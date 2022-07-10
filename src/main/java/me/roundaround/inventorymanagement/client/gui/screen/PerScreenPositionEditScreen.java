package me.roundaround.inventorymanagement.client.gui.screen;

import java.util.ArrayList;
import java.util.List;

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
  private final List<InventoryManagementButton> playerButtons = new ArrayList<>();
  private final List<InventoryManagementButton> containerButtons = new ArrayList<>();

  public PerScreenPositionEditScreen(Screen parent) {
    super(Text.literal("Placeholder"), parent, generateDummyConfigOption(parent));
  }

  private static PositionConfigOption generateDummyConfigOption(Screen parent) {
    Position currentValue = InventoryManagementMod.CONFIG.SCREEN_POSITIONS
        .get(parent)
        .orElse(InventoryManagementMod.CONFIG.DEFAULT_POSITION.getValue());
    PositionConfigOption dummyConfig = PositionConfigOption
        .builder(
            InventoryManagementMod.CONFIG.SCREEN_POSITIONS.getScreenKey(parent),
            "",
            InventoryManagementMod.CONFIG.DEFAULT_POSITION.getDefault())
        .build();
    dummyConfig.setValue(currentValue);
    return dummyConfig;
  }

  @Override
  protected void init() {
    super.init();

    // TODO: Buttons change order when entering this screen
    // TODO: Adjust buttons on a per-inventory basis

    playerButtons.addAll(InventoryButtonsManager.INSTANCE.getPlayerButtons());
    containerButtons.addAll(InventoryButtonsManager.INSTANCE.getContainerButtons());

    Screens.getButtons(parent).removeIf((button) -> button instanceof InventoryManagementButton);

    for (int i = 0; i < playerButtons.size(); i++) {
      playerButtons.get(i).setOffset(InventoryButtonsManager.INSTANCE.getButtonPosition(i, getValue()));
    }
    for (int i = 0; i < containerButtons.size(); i++) {
      containerButtons.get(i).setOffset(InventoryButtonsManager.INSTANCE.getButtonPosition(i, getValue()));
    }
  }

  @Override
  protected void setValue(Position value) {
    super.setValue(value);

    for (int i = 0; i < playerButtons.size(); i++) {
      playerButtons.get(i).setOffset(InventoryButtonsManager.INSTANCE.getButtonPosition(i, value));
    }
    for (int i = 0; i < containerButtons.size(); i++) {
      containerButtons.get(i).setOffset(InventoryButtonsManager.INSTANCE.getButtonPosition(i, value));
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
    renderDarkenBackground(matrixStack, mouseX, mouseY, partialTicks);
  }

  @Override
  protected void renderContent(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    super.renderContent(matrixStack, mouseX, mouseY, partialTicks);

    playerButtons.forEach((button) -> button.render(matrixStack, mouseX, mouseY, partialTicks));
    containerButtons.forEach((button) -> button.render(matrixStack, mouseX, mouseY, partialTicks));

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
      InventoryManagementMod.CONFIG.SCREEN_POSITIONS.set(parent, getValue());
      InventoryManagementMod.CONFIG.saveToFile();
    }
  }
}
