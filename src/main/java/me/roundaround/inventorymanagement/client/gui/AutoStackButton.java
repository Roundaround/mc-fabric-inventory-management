package me.roundaround.inventorymanagement.client.gui;

import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import me.roundaround.inventorymanagement.network.AutoStackPacket;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

public class AutoStackButton extends InventoryManagementButton {
  public AutoStackButton(
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
        new Position(fromPlayerInventory ? 2 : 1, 0),
        fromPlayerInventory,
        (button) -> {
          AutoStackPacket.sendToServer(fromPlayerInventory);
        },
        getTooltip(fromPlayerInventory));
  }

  public AutoStackButton(
      HandledScreenAccessor parent,
      Inventory inventory,
      Slot referenceSlot,
      Position offset,
      boolean fromPlayerInventory) {
    super(
        parent,
        inventory,
        referenceSlot,
        offset,
        new Position(fromPlayerInventory ? 2 : 1, 0),
        fromPlayerInventory,
        (button) -> {
          AutoStackPacket.sendToServer(fromPlayerInventory);
        },
        getTooltip(fromPlayerInventory));
  }

  private static Text getTooltip(boolean fromPlayerInventory) {
    String key = fromPlayerInventory
        ? "inventorymanagement.button.autostack_into"
        : "inventorymanagement.button.autostack_from";
    return Text.translatable(key);
  }
}
