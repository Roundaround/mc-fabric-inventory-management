package me.roundaround.inventorymanagement.client.gui.screen;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.client.InventoryButtonsManager;
import me.roundaround.inventorymanagement.client.gui.InventoryManagementButton;
import me.roundaround.inventorymanagement.config.InventoryManagementConfig;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.LinkedList;

public class PerScreenPositionEditScreen extends AnywherePositionEditScreen {
  private final LinkedList<InventoryManagementButton> buttons = new LinkedList<>();

  private final boolean isPlayerInventory;

  public PerScreenPositionEditScreen(Screen parent, boolean isPlayerInventory) {
    super(Text.translatable("inventorymanagement.position_edit.title"),
        parent,
        generateDummyConfigOption(parent, isPlayerInventory)
    );
    this.isPlayerInventory = isPlayerInventory;
  }

  private static PositionConfigOption generateDummyConfigOption(
      Screen parent, boolean isPlayerInventory
  ) {
    InventoryManagementConfig config = InventoryManagementConfig.getInstance();
    Position defaultValue = config.defaultPosition.getValue();
    Position currentValue = config.screenPositions.get(parent, isPlayerInventory).orElse(defaultValue);

    PositionConfigOption option = PositionConfigOption.builder(config.screenPositions.getPath())
        .setDefaultValue(defaultValue)
        .build();
    option.setModId(InventoryManagementMod.MOD_ID);
    option.setValue(currentValue);

    return option;
  }

  @Override
  protected void init() {
    super.init();

    this.subscriptions.add(this.getOption().pendingValue.subscribe((value) -> {
      InventoryManagementConfig.getInstance().screenPositions.set(this.anywhereParent, this.isPlayerInventory, value);
      this.refreshButtonPositions(value);
    }));

    this.buttons.addAll(this.isPlayerInventory ?
        InventoryButtonsManager.INSTANCE.getPlayerButtons() :
        InventoryButtonsManager.INSTANCE.getContainerButtons());

    Screens.getButtons(this.anywhereParent).removeIf((button) -> button instanceof InventoryManagementButton);

    this.refreshButtonPositions(this.getValue());
  }

  @Override
  public void close() {
    super.close();
    InventoryManagementConfig.getInstance().writeToStore();
  }

  @Override
  public void render(DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
    MatrixStack matrixStack = drawContext.getMatrices();
    matrixStack.push();
    matrixStack.translate(0, 0, -51);
    this.anywhereParent.render(drawContext, mouseX, mouseY, partialTicks);
    matrixStack.pop();

    super.render(drawContext, mouseX, mouseY, partialTicks);

    this.buttons.forEach((button) -> button.render(drawContext, mouseX, mouseY, partialTicks));
    drawContext.drawTextWithShadow(this.textRenderer,
        Text.literal(this.getValue().toString()),
        4,
        4,
        GuiUtil.LABEL_COLOR
    );
  }

  private void refreshButtonPositions(Position value) {
    for (int i = 0; i < this.buttons.size(); i++) {
      this.buttons.get(i).setOffset(InventoryButtonsManager.INSTANCE.getButtonPosition(i, value));
    }
  }
}
