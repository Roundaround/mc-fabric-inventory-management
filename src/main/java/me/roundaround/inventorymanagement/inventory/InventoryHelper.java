package me.roundaround.inventorymanagement.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import me.roundaround.inventorymanagement.inventory.sorting.ItemStackComparator;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;

public class InventoryHelper {
  public static void sortInventory(PlayerEntity player, boolean isPlayerInventory) {
    Inventory containerInventory = getContainerInventory(player);
    Inventory inventory = isPlayerInventory || containerInventory == null ? player.getInventory() : containerInventory;

    if (inventory instanceof PlayerInventory) {
      sortInventory(inventory, SlotRange.playerMainRange());
    } else {
      sortInventory(inventory);
    }
  }

  private static void sortInventory(Inventory inventory) {
    sortInventory(inventory, 0, inventory.size());
  }

  private static void sortInventory(Inventory inventory, int start, int end) {
    sortInventory(inventory, new SlotRange(start, end));
  }

  private static void sortInventory(Inventory inventory, SlotRange slotRange) {
    List<ItemStack> stacks = new ArrayList<>();

    for (int i = slotRange.min; i < slotRange.max; i++) {
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

    for (int i = slotRange.min; i < slotRange.max; i++) {
      int j = i - slotRange.min;
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
      autoStackInventories(playerInventory, containerInventory, player);
    } else {
      autoStackInventories(containerInventory, playerInventory, player);
    }
  }

  public static void transferAll(PlayerEntity player, boolean fromPlayerInventory) {
    Inventory containerInventory = getContainerInventory(player);
    if (containerInventory == null) {
      return;
    }

    Inventory playerInventory = player.getInventory();

    SlotRange playerSlotRange = SlotRange.playerMainRange();
    SlotRange containerSlotRange = SlotRange.fullRange(containerInventory);

    if (player.currentScreenHandler instanceof HorseScreenHandler) {
      containerSlotRange = SlotRange.horseMainRange(containerInventory);
    }

    if (fromPlayerInventory) {
      transferEntireInventory(
          playerInventory,
          containerInventory,
          playerSlotRange,
          containerSlotRange,
          player.playerScreenHandler,
          player.currentScreenHandler,
          player);
    } else {
      transferEntireInventory(
          containerInventory,
          playerInventory,
          containerSlotRange,
          playerSlotRange,
          player.currentScreenHandler,
          player.playerScreenHandler,
          player);
    }
  }

  private static void autoStackInventories(
      Inventory from,
      Inventory to,
      PlayerEntity player) {
    autoStackInventories(
        from,
        to,
        SlotRange.fullRange(from),
        SlotRange.fullRange(to),
        player);
  }

  private static void autoStackInventories(
      Inventory from,
      Inventory to,
      SlotRange fromRange,
      SlotRange toRange,
      PlayerEntity player) {
    transferEntireInventory(
        from,
        to,
        fromRange,
        toRange,
        (fromStack, toStack) -> !toStack.isEmpty(),
        player);
  }

  private static void transferEntireInventory(
      Inventory from,
      Inventory to,
      SlotRange fromRange,
      SlotRange toRange,
      BiFunction<ItemStack, ItemStack, Boolean> predicate,
      PlayerEntity player) {
    transferEntireInventory(
        from,
        to,
        fromRange,
        toRange,
        predicate,
        null,
        null,
        player);
  }

  private static void transferEntireInventory(
      Inventory from,
      Inventory to,
      SlotRange fromRange,
      SlotRange toRange,
      ScreenHandler fromScreenHandler,
      ScreenHandler toScreenHandler,
      PlayerEntity player) {
    transferEntireInventory(
        from,
        to,
        fromRange,
        toRange,
        (fromStack, toStack) -> true,
        fromScreenHandler,
        toScreenHandler,
        player);
  }

  private static void transferEntireInventory(
      Inventory from,
      Inventory to,
      SlotRange fromRange,
      SlotRange toRange,
      BiFunction<ItemStack, ItemStack, Boolean> predicate,
      ScreenHandler fromScreenHandler,
      ScreenHandler toScreenHandler,
      PlayerEntity player) {
    for (int toIdx = toRange.min; toIdx < toRange.max; toIdx++) {
      for (int fromIdx = fromRange.min; fromIdx < fromRange.max; fromIdx++) {
        ItemStack fromStack = from.getStack(fromIdx).copy();
        ItemStack toStack = to.getStack(toIdx).copy();

        if (fromStack.isEmpty()) {
          continue;
        }

        if (!predicate.apply(fromStack, toStack)) {
          continue;
        }

        if (!canTakeItemFromSlot(fromScreenHandler, fromIdx, player)) {
          continue;
        }

        if (!canPlaceItemInSlot(toScreenHandler, toIdx, fromStack)) {
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
          from.setStack(fromIdx, ItemStack.EMPTY);
        }
      }
    }
  }

  private static boolean canTakeItemFromSlot(ScreenHandler screenHandler, int idx, PlayerEntity player) {
    if (screenHandler == null) {
      return true;
    }
    try {
      return screenHandler.getSlot(idx).canTakeItems(player);
    } catch (IndexOutOfBoundsException e) {
      return false;
    }
  }

  private static boolean canPlaceItemInSlot(ScreenHandler screenHandler, int idx, ItemStack itemStack) {
    if (screenHandler == null) {
      return true;
    }
    try {
      return screenHandler.getSlot(idx).canInsert(itemStack);
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

    public static SlotRange playerMainRange() {
      return new SlotRange(PlayerInventory.getHotbarSize(), PlayerInventory.MAIN_SIZE);
    }

    public static SlotRange horseMainRange(Inventory inventory) {
      return new SlotRange(2, inventory.size());
    }
  }
}
