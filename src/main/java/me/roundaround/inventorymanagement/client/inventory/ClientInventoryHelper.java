package me.roundaround.inventorymanagement.client.inventory;

import me.roundaround.inventorymanagement.api.gui.SlotRangeRegistry;
import me.roundaround.inventorymanagement.config.ConfigHelpers;
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

  public static List<Integer> calculateSort(PlayerEntity player, boolean isPlayerInventory) {
    Inventory containerInventory = InventoryHelper.getContainerInventory(player);
    Inventory inventory = isPlayerInventory || containerInventory == null ? player.getInventory() : containerInventory;

    SlotRange slotRange = isPlayerInventory ?
        SlotRangeRegistry.getPlayerSide(player, inventory).withExclusions(ConfigHelpers.getLockedSlots()) :
        SlotRangeRegistry.getContainerSide(player, inventory);

    return calculateSort(player, inventory, slotRange);
  }

  private static List<Integer> calculateSort(PlayerEntity player, Inventory inventory, SlotRange slotRange) {
    return new SortableInventory(inventory).sort(slotRange, ItemStackComparator.create(player.getUuid()));
  }
}
