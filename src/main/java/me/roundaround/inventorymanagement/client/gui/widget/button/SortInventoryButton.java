package me.roundaround.inventorymanagement.client.gui.widget.button;

import me.roundaround.inventorymanagement.client.ButtonBasePositionFunction;
import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import me.roundaround.inventorymanagement.network.SortInventoryPacket;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

public class SortInventoryButton<T extends HandledScreen<?>> extends InventoryManagementButton<T> {
  public SortInventoryButton(
      T parent,
      ButtonBasePositionFunction<T> basePositionFunction,
      Position offset,
      boolean isPlayerInventory) {
    super(parent, basePositionFunction, offset, new Position(0, 0), isPlayerInventory, (button) -> {
      SortInventoryPacket.sendToServer(isPlayerInventory);
    }, getTooltip(isPlayerInventory));
  }

  public SortInventoryButton(
      HandledScreenAccessor accessor,
      Slot referenceSlot,
      Position offset,
      boolean isPlayerInventory) {
    super(accessor,
        referenceSlot,
        offset,
        new Position(0, 0),
        isPlayerInventory,
        getTooltip(isPlayerInventory));
  }

  private static Text getTooltip(boolean isPlayerInventory) {
    String key = isPlayerInventory
        ? "inventorymanagement.button.sort_player"
        : "inventorymanagement.button.sort_container";
    return Text.translatable(key);
  }
}
