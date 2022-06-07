package me.roundaround.inventorymanagement.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import me.roundaround.inventorymanagement.inventory.sorting.ItemStackComparator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class InventoryHelper {
  public static void sortInventory(PlayerEntity player, boolean isPlayerInventory) {
    Inventory containerInventory = getContainerInventory(player);
    Inventory inventory = isPlayerInventory || containerInventory == null ? player.getInventory() : containerInventory;
    boolean isEventuallyPlayerInventory = inventory == player.getInventory();

    if (isEventuallyPlayerInventory) {
      sortInventory(inventory, 9, 36);
    } else {
      sortInventory(inventory);
    }
  }

  private static void sortInventory(Inventory inventory) {
    sortInventory(inventory, 0, inventory.size());
  }

  private static void sortInventory(Inventory inventory, int start, int end) {
    List<ItemStack> stacks = new ArrayList<>();

    for (int i = start; i < end; i++) {
      stacks.add(inventory.getStack(i).copy());
    }

    List<ItemStack> cleanedStacks = stacks.stream()
        .filter(itemStack -> !itemStack.isEmpty())
        .map(ItemStack::copy)
        .collect(Collectors.toList());

    for (int i = 0; i < cleanedStacks.size(); i++) {
      for (int j = i + 1; j < cleanedStacks.size(); j++) {
        ItemStack a = cleanedStacks.get(i);
        ItemStack b = cleanedStacks.get(j);

        if (areItemStacksMergeable(a, b)) {
          int itemsToShift = Math.min(a.getMaxCount() - a.getCount(), b.getCount());
          if (itemsToShift > 0) {
            a.increment(itemsToShift);
            b.decrement(itemsToShift);
          }
        }
      }
    }

    List<ItemStack> sortedStacks = cleanedStacks.stream()
        .filter(itemStack -> !itemStack.isEmpty())
        .sorted(ItemStackComparator.comparator())
        .collect(Collectors.toList());

    for (int i = start; i < end; i++) {
      int j = i - start;
      ItemStack itemStack = j >= sortedStacks.size() ? ItemStack.EMPTY : sortedStacks.get(j);
      inventory.setStack(i, itemStack);
    }

    inventory.markDirty();
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

  public static boolean areItemStacksMergeable(ItemStack a, ItemStack b) {
    return !a.isEmpty() && !b.isEmpty() &&
        a.getItem() == b.getItem() &&
        ItemStack.areNbtEqual(a, b);
  }
}
