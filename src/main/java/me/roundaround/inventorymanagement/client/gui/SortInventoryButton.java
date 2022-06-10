package me.roundaround.inventorymanagement.client.gui;

import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import me.roundaround.inventorymanagement.network.SortInventoryPacket;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.Text;

public class SortInventoryButton extends InventoryManagementButton {
  private final boolean isPlayerInventory;

  public SortInventoryButton(HandledScreen<?> parent, int x, int y, boolean isPlayerInventory) {
    super(parent, (HandledScreenAccessor) parent, x, y, 0, 0, (button) -> {
      SortInventoryPacket.sendToServer(isPlayerInventory);
    });
    this.isPlayerInventory = isPlayerInventory;
  }

  @Override
  protected Text getTooltip() {
    String key = isPlayerInventory
        ? "inventorymanagement.button.sort_player"
        : "inventorymanagement.button.sort_container";
    return Text.translatable(key);
  }
}
