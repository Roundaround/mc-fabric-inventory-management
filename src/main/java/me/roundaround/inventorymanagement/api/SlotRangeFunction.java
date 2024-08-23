package me.roundaround.inventorymanagement.api;

import me.roundaround.inventorymanagement.inventory.SlotRange;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;

@FunctionalInterface
public interface SlotRangeFunction<H extends ScreenHandler> {
  SlotRange apply(H screenHandler, Inventory inventory, boolean isPlayerSide);

  static <H extends ScreenHandler> SlotRangeFunction<H> fullInventory() {
    return (screenHandler, inventory, isPlayerSide) -> {
      if (inventory == null) {
        return SlotRange.empty();
      }
      return SlotRange.fullRange(inventory);
    };
  }

  static <H extends ScreenHandler> SlotRangeFunction<H> playerMainRange() {
    return (screenHandler, inventory, isPlayerSide) -> {
      if (inventory == null) {
        return SlotRange.empty();
      }
      return SlotRange.playerMainRange();
    };
  }

  static <H extends ScreenHandler> SlotRangeFunction<H> empty() {
    return (screenHandler, inventory, isPlayerSide) -> SlotRange.empty();
  }
}
