package me.roundaround.inventorymanagement.client.gui;

import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import me.roundaround.inventorymanagement.network.SortInventoryPacket;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

public class SortInventoryButton extends InventoryManagementButton {
  private final boolean isPlayerInventory;

  public SortInventoryButton(
      HandledScreen<?> parent,
      Slot referenceSlot,
      Position offset,
      boolean isPlayerInventory) {
    super(
        parent,
        (HandledScreenAccessor) parent,
        referenceSlot,
        offset,
        new Position(0, 0),
        (button) -> {
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
