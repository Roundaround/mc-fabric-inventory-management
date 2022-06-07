package me.roundaround.inventorymanagement.client.gui;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class SortInventoryButton extends InventoryManagementButton {
  private final boolean isPlayerInventory;

  public SortInventoryButton(HandledScreen<?> parent, int x, int y, boolean isPlayerInventory) {
    // super(parent, x, y, 0, 0, (button) -> {
    // NetworkHandler.sendToServer(new SortInventoryMessage(isPlayerInventory));
    // });
    super(parent, (HandledScreenAccessor) parent, x, y, 0, 0, (button) -> {
      InventoryManagementMod.LOGGER.info(((SortInventoryButton) button).getTooltip().getString());
    });
    this.isPlayerInventory = isPlayerInventory;
  }

  @Override
  protected Text getTooltip() {
    String key = isPlayerInventory
        ? "inventorymanagement.button.sort_player"
        : "inventorymanagement.button.sort_container";
    return new TranslatableText(key);
  }
}
