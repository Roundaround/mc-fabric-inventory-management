package me.roundaround.inventorymanagement.client.gui.screen;

import java.util.LinkedList;

import me.roundaround.inventorymanagement.client.InventoryButtonsManager;
import me.roundaround.inventorymanagement.client.gui.InventoryManagementButton;
import me.roundaround.inventorymanagement.config.InventoryManagementConfig;
import me.roundaround.inventorymanagement.generated.Constants;
import me.roundaround.inventorymanagement.roundalib.client.gui.util.GuiUtil;
import me.roundaround.inventorymanagement.roundalib.config.option.PositionConfigOption;
import me.roundaround.inventorymanagement.roundalib.config.value.Position;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class PerScreenPositionEditScreen extends AnywherePositionEditScreen {
  private final LinkedList<InventoryManagementButton> buttons = new LinkedList<>();

  private final boolean isPlayerInventory;

  public PerScreenPositionEditScreen(Screen parent, boolean isPlayerInventory) {
    super(
        Text.translatable("inventorymanagement.position_edit.title"),
        parent,
        generateDummyConfigOption(parent, isPlayerInventory));
    this.isPlayerInventory = isPlayerInventory;
  }

  private static PositionConfigOption generateDummyConfigOption(Screen parent, boolean isPlayerInventory) {
    InventoryManagementConfig config = InventoryManagementConfig.getInstance();
    Position defaultValue = config.defaultPosition.getValue();
    Position currentValue = config.screenPositions.get(parent, isPlayerInventory).orElse(defaultValue);

    PositionConfigOption option = PositionConfigOption.builder(config.screenPositions.getPath())
        .setDefaultValue(defaultValue)
        .build();
    option.setModId(Constants.MOD_ID);
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

    this.buttons.addAll(this.isPlayerInventory
        ? InventoryButtonsManager.INSTANCE.getPlayerButtons()
        : InventoryButtonsManager.INSTANCE.getContainerButtons());

    Screens.getButtons(this.anywhereParent).removeIf((button) -> button instanceof InventoryManagementButton);

    this.refreshButtonPositions(this.getValue());
  }

  @Override
  public void close() {
    super.close();
    InventoryManagementConfig.getInstance().writeToStore();
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    context.createNewRootLayer();
    this.anywhereParent.renderBackground(context, mouseX, mouseY, delta);
    context.createNewRootLayer();
    this.anywhereParent.render(context, mouseX, mouseY, delta);
    context.createNewRootLayer();

    super.render(context, mouseX, mouseY, delta);

    this.buttons.forEach((button) -> {
      button.clearSelected();
      button.render(context, mouseX, mouseY, delta);
    });
    context.drawTextWithShadow(
        this.textRenderer,
        Text.literal(this.getValue().toString()),
        GuiUtil.PADDING,
        GuiUtil.PADDING,
        GuiUtil.LABEL_COLOR);
  }

  private void refreshButtonPositions(Position value) {
    for (int i = 0; i < this.buttons.size(); i++) {
      this.buttons.get(i).setOffset(InventoryButtonsManager.INSTANCE.getButtonPosition(i, value));
    }
  }
}
