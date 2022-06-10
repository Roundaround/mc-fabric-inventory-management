package me.roundaround.inventorymanagement.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import me.roundaround.inventorymanagement.inventory.sorting.ItemStackComparator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.HorseScreenHandler;
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
  }

  public static void autoStack(PlayerEntity player, boolean fromPlayerInventory) {
    Inventory containerInventory = getContainerInventory(player);
    if (containerInventory == null) {
      return;
    }

    Inventory playerInventory = player.getInventory();

    if (fromPlayerInventory) {
      autoStackInventories(playerInventory, containerInventory);
    } else {
      autoStackInventories(containerInventory, playerInventory);
    }
  }

  public static void transferAll(PlayerEntity player, boolean fromPlayerInventory) {
    Inventory containerInventory = getContainerInventory(player);
    if (containerInventory == null) {
      return;
    }

    Inventory playerInventory = player.getInventory();

    SlotRange playerSlotRange = new SlotRange(9, 36);
    SlotRange containerSlotRange = SlotRange.fullRange(containerInventory);

    if (player.currentScreenHandler instanceof HorseScreenHandler) {
      containerSlotRange = new SlotRange(2, containerInventory.size());
    }

    if (fromPlayerInventory) {
      transferEntireInventory(playerInventory, containerInventory, playerSlotRange, containerSlotRange);
    } else {
      transferEntireInventory(containerInventory, playerInventory, containerSlotRange, playerSlotRange);
    }
  }

  private static void autoStackInventories(
      Inventory from,
      Inventory to) {
    autoStackInventories(from, to, SlotRange.fullRange(from), SlotRange.fullRange(to));
  }

  private static void autoStackInventories(
      Inventory from,
      Inventory to,
      SlotRange fromRange,
      SlotRange toRange) {
    transferEntireInventory(from, to, fromRange, toRange, (fromStack, toStack) -> !toStack.isEmpty());
  }

  private static void transferEntireInventory(
      Inventory from,
      Inventory to,
      SlotRange fromRange,
      SlotRange toRange) {
    transferEntireInventory(from, to, fromRange, toRange, (fromStack, toStack) -> true);
  }

  private static void transferEntireInventory(
      Inventory from,
      Inventory to,
      SlotRange fromRange,
      SlotRange toRange,
      BiFunction<ItemStack, ItemStack, Boolean> predicate) {
    for (int toIdx = toRange.min; toIdx < toRange.max; toIdx++) {
      for (int fromIdx = fromRange.min; fromIdx < fromRange.max; fromIdx++) {
        ItemStack fromStack = from.getStack(fromIdx).copy();
        ItemStack toStack = to.getStack(toIdx).copy();

        if (!predicate.apply(fromStack, toStack)) {
          continue;
        }

        if (areItemStacksMergeable(toStack, fromStack)) {
          int space = toStack.getMaxCount() - toStack.getCount();
          int amount = Math.min(space, fromStack.getCount());
          if (amount > 0) {
            toStack.increment(amount);
            fromStack.decrement(amount);

            to.setStack(toIdx, toStack);
            from.setStack(fromIdx, fromStack.isEmpty() ? ItemStack.EMPTY : fromStack);
          }
        } else if (toStack.isEmpty() && !fromStack.isEmpty()) {
          to.setStack(toIdx, fromStack);
          from.removeStack(fromIdx);
        }
      }
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

  public static boolean areItemStacksMergeable(ItemStack a, ItemStack b) {
    return !a.isEmpty() && !b.isEmpty() &&
        a.getItem() == b.getItem() &&
        ItemStack.areNbtEqual(a, b);
  }

  static class SlotRange {
    public int min;
    public int max;

    public SlotRange(int min, int max) {
      this.min = min;
      this.max = max;
    }

    public static SlotRange fullRange(Inventory inventory) {
      return new SlotRange(0, inventory.size());
    }
  }

}
