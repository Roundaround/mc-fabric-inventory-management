package me.roundaround.inventorymanagement.client.gui;

import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import me.roundaround.inventorymanagement.network.TransferAllPacket;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.Text;

public class TransferAllButton extends InventoryManagementButton {
  private final boolean fromPlayerInventory;

  public TransferAllButton(HandledScreen<?> parent, int x, int y, boolean fromPlayerInventory) {
    super(parent, (HandledScreenAccessor) parent, x, y, fromPlayerInventory ? 4 : 3, 0, (button) -> {
      TransferAllPacket.sendToServer(fromPlayerInventory);
    });
    this.fromPlayerInventory = fromPlayerInventory;
  }

  @Override
  protected Text getTooltip() {
    String key = fromPlayerInventory
        ? "inventorymanagement.button.transfer_place"
        : "inventorymanagement.button.transfer_take";
    return Text.translatable(key);
  }
}
