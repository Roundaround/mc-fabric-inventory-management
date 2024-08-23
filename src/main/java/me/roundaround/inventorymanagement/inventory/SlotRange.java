package me.roundaround.inventorymanagement.inventory;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;

public record SlotRange(int min, int max) {
  public static SlotRange byMax(int min, int max) {
    return new SlotRange(min, max);
  }

  public static SlotRange bySize(int min, int size) {
    return new SlotRange(min, min + size);
  }

  public static SlotRange fullRange(Inventory inventory) {
    return byMax(0, inventory.size());
  }

  public static SlotRange playerMainRange() {
    return bySize(PlayerInventory.getHotbarSize(), PlayerInventory.MAIN_SIZE);
  }

  public static SlotRange horseMainRange(Inventory inventory) {
    // Saddle is index 0, so start at 1.
    return byMax(1, inventory.size());
  }

  public static SlotRange empty() {
    return new SlotRange(0, 0);
  }

  public int size() {
    return this.max - this.min;
  }

  public boolean contains(int slot) {
    return slot >= this.min && slot < this.max;
  }
}
