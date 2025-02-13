package me.roundaround.inventorymanagement.client.inventory;

import me.roundaround.inventorymanagement.api.gui.SlotRangeRegistry;
import me.roundaround.inventorymanagement.config.GameScopedConfig;
import me.roundaround.inventorymanagement.inventory.InventoryHelper;
import me.roundaround.inventorymanagement.inventory.SlotRange;
import me.roundaround.inventorymanagement.inventory.SortableInventory;
import me.roundaround.inventorymanagement.inventory.sorting.itemstack.ItemStackComparator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;

import java.util.List;

public final class ClientInventoryHelper {
  private ClientInventoryHelper() {
  }

  public static List<Integer> calculateContainerSort(PlayerEntity player) {
    Inventory inventory = player.getInventory();
    SlotRange slotRange = SlotRangeRegistry.getPlayerSide(player, inventory).withExclusions(getLockedSlots());
    return calculateSort(player, inventory, slotRange);
  }

  public static List<Integer> calculatePlayerSort(PlayerEntity player) {
    Inventory inventory = InventoryHelper.getContainerInventoryOrElse(player, player.getInventory());
    SlotRange slotRange = SlotRangeRegistry.getContainerSide(player, inventory);
    return calculateSort(player, inventory, slotRange);
  }

  private static List<Integer> calculateSort(PlayerEntity player, Inventory inventory, SlotRange slotRange) {
    return new SortableInventory(inventory).sort(slotRange, ItemStackComparator.create(player.getUuid()));
  }

  public static List<Integer> getLockedSlots() {
    return List.copyOf(GameScopedConfig.getInstance().lockedInventorySlots.getValue());
  }
}
