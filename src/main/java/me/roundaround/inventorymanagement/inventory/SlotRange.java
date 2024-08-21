package me.roundaround.inventorymanagement.inventory;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;

public record SlotRange(int min, int max) {
  public int size() {
    return this.max - this.min;
  }

  public boolean contains(int slot) {
    return slot >= this.min && slot <= this.max;
  }

  public static SlotRange fullRange(Inventory inventory) {
    return new SlotRange(0, inventory.size());
  }

  public static SlotRange playerMainRange() {
    return new SlotRange(PlayerInventory.getHotbarSize(), PlayerInventory.MAIN_SIZE);
  }

  public static SlotRange horseMainRange(Inventory inventory) {
    return new SlotRange(2, inventory.size());
  }
}
