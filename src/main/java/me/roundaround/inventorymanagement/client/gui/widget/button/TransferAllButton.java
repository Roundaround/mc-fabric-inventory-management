package me.roundaround.inventorymanagement.client.gui.widget.button;

import me.roundaround.inventorymanagement.client.ButtonBasePositionFunction;
import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import me.roundaround.inventorymanagement.network.TransferAllPacket;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

public class TransferAllButton<T extends HandledScreen<?>> extends InventoryManagementButton<T> {
  public TransferAllButton(
      T parent,
      ButtonBasePositionFunction<T> basePositionFunction,
      Position offset,
      boolean fromPlayerInventory) {
    super(parent,
        basePositionFunction,
        offset,
        new Position(fromPlayerInventory ? 4 : 3, 0),
        fromPlayerInventory,
        (button) -> {
          TransferAllPacket.sendToServer(fromPlayerInventory);
        },
        getTooltip(fromPlayerInventory));
  }

  public TransferAllButton(
      HandledScreenAccessor accessor,
      Slot referenceSlot,
      Position offset,
      boolean fromPlayerInventory) {
    super(accessor,
        referenceSlot,
        offset,
        new Position(fromPlayerInventory ? 4 : 3, 0),
        fromPlayerInventory,
        getTooltip(fromPlayerInventory));
  }

  private static Text getTooltip(boolean fromPlayerInventory) {
    String key = fromPlayerInventory
        ? "inventorymanagement.button.transfer_place"
        : "inventorymanagement.button.transfer_take";
    return Text.translatable(key);
  }
}
