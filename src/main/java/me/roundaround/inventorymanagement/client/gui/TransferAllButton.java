package me.roundaround.inventorymanagement.client.gui;

import me.roundaround.inventorymanagement.network.TransferAllPacket;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

public class TransferAllButton extends InventoryManagementButton {
  private final boolean fromPlayerInventory;

  public TransferAllButton(
      HandledScreen<?> parent,
      Inventory inventory,
      Slot referenceSlot,
      Position offset,
      boolean fromPlayerInventory) {
    super(
        parent,
        inventory,
        referenceSlot,
        offset,
        new Position(fromPlayerInventory ? 4 : 3, 0),
        fromPlayerInventory,
        (button) -> {
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
