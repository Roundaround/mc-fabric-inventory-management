package me.roundaround.inventorymanagement.inventory;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.Comparator;
import java.util.OptionalInt;

public class InventoryHelper {
  public static boolean canTakeItemFromSlot(
      ScreenHandler screenHandler, Inventory inventory, int inventoryIndex, PlayerEntity player
  ) {
    if (screenHandler == null) {
      return true;
    }

    OptionalInt slotIndex = screenHandler.getSlotIndex(inventory, inventoryIndex);
    if (slotIndex.isEmpty()) {
      return false;
    }

    try {
      return screenHandler.getSlot(slotIndex.getAsInt()).canTakeItems(player);
    } catch (IndexOutOfBoundsException e) {
      return false;
    }
  }

  public static boolean canPlaceItemInSlot(
      ScreenHandler screenHandler, Inventory inventory, int inventoryIndex, ItemStack stack
  ) {
    if (screenHandler == null) {
      return true;
    }

    OptionalInt slotIndex = screenHandler.getSlotIndex(inventory, inventoryIndex);
    if (slotIndex.isEmpty()) {
      return false;
    }

    try {
      return screenHandler.getSlot(slotIndex.getAsInt()).canInsert(stack);
    } catch (IndexOutOfBoundsException e) {
      return false;
    }
  }

  public static Inventory getContainerInventory(PlayerEntity player) {
    ScreenHandler currentScreenHandler = player.currentScreenHandler;
    if (currentScreenHandler == null) {
      return null;
    }

    try {
      return currentScreenHandler.getSlot(0).inventory;
    } catch (IndexOutOfBoundsException e) {
      return null;
    }
  }

  public static Inventory getContainerInventoryOrElse(PlayerEntity player, Inventory fallback) {
    Inventory containerInventory = getContainerInventory(player);
    return containerInventory == null ? fallback : containerInventory;
  }

  public static boolean canStacksBeMerged(ItemStack toStack, ItemStack fromStack) {
    return !toStack.isEmpty() && ItemStack.areItemsAndComponentsEqual(toStack, fromStack) && toStack.isStackable() &&
           toStack.getCount() < toStack.getMaxCount();
  }

  public static boolean mergeStacks(ItemStack toStack, ItemStack fromStack) {
    int space = toStack.getMaxCount() - toStack.getCount();
    int amount = Math.min(space, fromStack.getCount());
    if (amount > 0) {
      toStack.increment(amount);
      fromStack.decrement(amount);
      return true;
    }
    return false;
  }

  public static Slot getReferenceSlot(HandledScreen<?> screen, boolean isPlayerInventory) {
    return screen.getScreenHandler().slots.stream().filter((slot) -> {
      if (isPlayerInventory != (slot.inventory instanceof PlayerInventory)) {
        return false;
      }

      // Only consider "bulk inventory" slots if player inventory
      return !isPlayerInventory || SlotRange.playerMainRange().contains(slot.getIndex());
    }).max(Comparator.comparingInt(slot -> slot.x - slot.y)).orElse(null);
  }
}
