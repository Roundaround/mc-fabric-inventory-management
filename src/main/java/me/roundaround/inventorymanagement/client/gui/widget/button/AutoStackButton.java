package me.roundaround.inventorymanagement.client.gui.widget.button;

import me.roundaround.inventorymanagement.client.ButtonBasePositionFunction;
import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import me.roundaround.inventorymanagement.network.AutoStackPacket;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

public class AutoStackButton<T extends HandledScreen<?>> extends InventoryManagementButton<T> {
  public AutoStackButton(
      T parent,
      ButtonBasePositionFunction<T> basePositionFunction,
      Position offset,
      boolean fromPlayerInventory) {
    super(parent,
        basePositionFunction,
        offset,
        new Position(fromPlayerInventory ? 2 : 1, 0),
        fromPlayerInventory,
        (button) -> {
          AutoStackPacket.sendToServer(fromPlayerInventory);
        },
        getTooltip(fromPlayerInventory));
  }

  public AutoStackButton(
      HandledScreenAccessor accessor,
      Slot referenceSlot,
      Position offset,
      boolean fromPlayerInventory) {
    super(accessor,
        referenceSlot,
        offset,
        new Position(fromPlayerInventory ? 2 : 1, 0),
        fromPlayerInventory,
        getTooltip(fromPlayerInventory));
  }

  private static Text getTooltip(boolean fromPlayerInventory) {
    String key = fromPlayerInventory
        ? "inventorymanagement.button.autostack_into"
        : "inventorymanagement.button.autostack_from";
    return Text.translatable(key);
  }
}
