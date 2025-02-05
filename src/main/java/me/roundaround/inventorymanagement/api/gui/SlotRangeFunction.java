package me.roundaround.inventorymanagement.api.gui;

import me.roundaround.inventorymanagement.inventory.SlotRange;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;

@FunctionalInterface
public interface SlotRangeFunction<H extends ScreenHandler> {
  SlotRange apply(PlayerEntity player, Inventory inventory, boolean isPlayerSide);

  static <H extends ScreenHandler> SlotRangeFunction<H> defaultBulkInventory() {
    return (player, inventory, isPlayerSide) -> {
      if (inventory == null) {
        return SlotRange.empty();
      }
      return isPlayerSide ? SlotRange.playerMainRange() : SlotRange.fullRange(inventory);
    };
  }

  static <H extends ScreenHandler> SlotRangeFunction<H> empty() {
    return (player, inventory, isPlayerSide) -> SlotRange.empty();
  }
}
