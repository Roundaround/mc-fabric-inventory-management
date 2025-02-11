package me.roundaround.inventorymanagement.inventory;

import me.roundaround.inventorymanagement.api.gui.SlotRangeRegistry;
import me.roundaround.inventorymanagement.inventory.sorting.itemstack.ItemStackComparator;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

import java.util.*;
import java.util.function.BiFunction;

public class InventoryHelper {
  public static ArrayList<Integer> tempSortInventory(PlayerEntity player, boolean isPlayerInventory) {
    Inventory containerInventory = getContainerInventory(player);
    Inventory inventory = isPlayerInventory || containerInventory == null ? player.getInventory() : containerInventory;

    SlotRange slotRange = isPlayerInventory ?
        SlotRangeRegistry.getPlayerSide(player, inventory) :
        SlotRangeRegistry.getContainerSide(player, inventory);

    return tempSortInventory(player, inventory, slotRange);
  }

  public static ArrayList<Integer> tempSortInventory(PlayerEntity player, Inventory inventory, SlotRange slotRange) {
    return new SortableInventory(inventory).sort(slotRange, ItemStackComparator.create(player.getUuid()));
  }

  public static void applySort(PlayerEntity player, boolean isPlayerInventory, List<Integer> sorted) {
    Inventory containerInventory = getContainerInventory(player);
    Inventory inventory = isPlayerInventory || containerInventory == null ? player.getInventory() : containerInventory;

    SlotRange slotRange = isPlayerInventory ?
        SlotRangeRegistry.getPlayerSide(player, inventory) :
        SlotRangeRegistry.getContainerSide(player, inventory);
    applySort(player, inventory, slotRange, sorted);
  }

  public static void applySort(PlayerEntity player, Inventory inventory, SlotRange slotRange, List<Integer> sorted) {
    HashSet<Integer> slotsWithItems = new HashSet<>();
    for (int i = slotRange.min(); i < slotRange.max(); i++) {
      if (!inventory.getStack(i).isEmpty()) {
        slotsWithItems.add(i);
      }
    }

    DefaultedList<ItemStack> reconstructed = DefaultedList.ofSize(sorted.size(), ItemStack.EMPTY);
    for (int destIndex = 0; destIndex < sorted.size(); destIndex++) {
      int srcIndex = sorted.get(destIndex);
      ItemStack stack = srcIndex == -1 ? ItemStack.EMPTY : inventory.getStack(srcIndex).copy();
      reconstructed.set(destIndex, stack);

      if (srcIndex > -1 && !slotsWithItems.remove(srcIndex)) {
        // TODO: CHEATER (Specified an invalid source inventory slot index)
        return;
      }
    }

    if (!slotsWithItems.isEmpty()) {
      // TODO: CHEATER (Missing destination index for at least one slot)
      // TODO: This one could also just be resolved by tossing any non-specified slots at the end if there's room
      return;
    }

    for (int i = 1; i < reconstructed.size(); i++) {
      ItemStack current = reconstructed.get(i);
      if (current.isEmpty()) {
        continue;
      }

      // Search backwards for a non-empty merge candidate
      for (int j = i - 1; j >= 0 && !current.isEmpty(); j--) {
        ItemStack dest = reconstructed.get(j);

        // Skip empty stacks (result of emptying stacks during merges)
        if (dest.isEmpty()) {
          continue;
        }
        // Stop searching and move on to the next stack if we hit even one that isn't mergeable, since we know it means
        // anything before it is empty, non-mergeable, or full (and therefore also non-mergeable).
        if (!canStacksBeMerged(dest, current)) {
          break;
        }

        mergeStacks(dest, current);
        if (current.isEmpty()) {
          reconstructed.set(i, ItemStack.EMPTY);
        }
      }
    }

    Iterator<ItemStack> merged = reconstructed.iterator();
    for (int slotIndex = slotRange.min(); slotIndex < slotRange.max(); slotIndex++) {
      ItemStack stack = ItemStack.EMPTY;
      while (merged.hasNext()) {
        stack = merged.next();
        if (!stack.isEmpty()) {
          break;
        }
      }
      inventory.setStack(slotIndex, stack);
    }
  }

  public static void sortInventory(PlayerEntity player, boolean isPlayerInventory) {
    Inventory containerInventory = getContainerInventory(player);
    Inventory inventory = isPlayerInventory || containerInventory == null ? player.getInventory() : containerInventory;

    SlotRange slotRange = isPlayerInventory ?
        SlotRangeRegistry.getPlayerSide(player, inventory) :
        SlotRangeRegistry.getContainerSide(player, inventory);
    sortInventory(player, inventory, slotRange);
  }

  public static void sortAll(PlayerEntity player) {
    sortInventory(player, true);
    sortInventory(player, false);
  }

  public static void sortInventory(PlayerEntity player, Inventory inventory) {
    sortInventory(player, inventory, 0, inventory.size());
  }

  public static void sortInventory(PlayerEntity player, Inventory inventory, int start, int end) {
    sortInventory(player, inventory, new SlotRange(start, end));
  }

  public static void sortInventory(PlayerEntity player, Inventory inventory, SlotRange slotRange) {
    List<ItemStack> stacks = new ArrayList<>(slotRange.size());

    for (int i = slotRange.min(); i < slotRange.max(); i++) {
      stacks.add(inventory.getStack(i).copy());
    }

    stacks = stacks.stream().filter((stack) -> !stack.isEmpty()).toList();

    for (int i = 0; i < stacks.size(); i++) {
      for (int j = i + 1; j < stacks.size(); j++) {
        ItemStack a = stacks.get(i);
        ItemStack b = stacks.get(j);

        if (canStacksBeMerged(a, b)) {
          int itemsToShift = Math.min(a.getMaxCount() - a.getCount(), b.getCount());
          if (itemsToShift > 0) {
            a.increment(itemsToShift);
            b.decrement(itemsToShift);
          }
        }
      }
    }

    stacks = stacks.stream()
        .filter((stack) -> !stack.isEmpty())
        .sorted(ItemStackComparator.create(player.getUuid()))
        .toList();

    for (int slotIndex = slotRange.min(); slotIndex < slotRange.max(); slotIndex++) {
      int stacksIndex = slotIndex - slotRange.min();
      ItemStack stack = stacksIndex < stacks.size() ? stacks.get(stacksIndex) : ItemStack.EMPTY;
      inventory.setStack(slotIndex, stack);
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

    SlotRange playerSlotRange = SlotRangeRegistry.getPlayerSide(player, playerInventory);
    SlotRange containerSlotRange = SlotRangeRegistry.getContainerSide(player, containerInventory);

    if (player.currentScreenHandler instanceof HorseScreenHandler) {
      containerSlotRange = SlotRange.horseMainRange(containerInventory);
    }

    if (fromPlayerInventory) {
      transferEntireInventory(playerInventory, containerInventory, playerSlotRange, containerSlotRange,
          player.playerScreenHandler, player.currentScreenHandler, player
      );
    } else {
      transferEntireInventory(containerInventory, playerInventory, containerSlotRange, playerSlotRange,
          player.currentScreenHandler, player.playerScreenHandler, player
      );
    }
  }

  private static void autoStackInventories(
      Inventory from, Inventory to, PlayerEntity player
  ) {
    autoStackInventories(from, to, SlotRange.fullRange(from), SlotRange.fullRange(to), player);
  }

  private static void autoStackInventories(
      Inventory from, Inventory to, SlotRange fromRange, SlotRange toRange, PlayerEntity player
  ) {
    transferEntireInventory(from, to, fromRange, toRange, (fromStack, toStack) -> !toStack.isEmpty(), player);
  }

  private static void transferEntireInventory(
      Inventory from,
      Inventory to,
      SlotRange fromRange,
      SlotRange toRange,
      BiFunction<ItemStack, ItemStack, Boolean> predicate,
      PlayerEntity player
  ) {
    transferEntireInventory(from, to, fromRange, toRange, predicate, null, null, player);
  }

  private static void transferEntireInventory(
      Inventory from,
      Inventory to,
      SlotRange fromRange,
      SlotRange toRange,
      ScreenHandler fromScreenHandler,
      ScreenHandler toScreenHandler,
      PlayerEntity player
  ) {
    transferEntireInventory(from, to, fromRange, toRange, (fromStack, toStack) -> true, fromScreenHandler,
        toScreenHandler, player
    );
  }

  private static void transferEntireInventory(
      Inventory from,
      Inventory to,
      SlotRange fromRange,
      SlotRange toRange,
      BiFunction<ItemStack, ItemStack, Boolean> predicate,
      ScreenHandler fromScreenHandler,
      ScreenHandler toScreenHandler,
      PlayerEntity player
  ) {
    for (int toIdx = toRange.min(); toIdx < toRange.max(); toIdx++) {
      for (int fromIdx = fromRange.min(); fromIdx < fromRange.max(); fromIdx++) {
        ItemStack fromStack = from.getStack(fromIdx).copy();
        ItemStack toStack = to.getStack(toIdx).copy();

        if (fromStack.isEmpty()) {
          continue;
        }

        if (!predicate.apply(fromStack, toStack)) {
          continue;
        }

        if (!canTakeItemFromSlot(fromScreenHandler, from, fromIdx, player)) {
          continue;
        }

        if (!canPlaceItemInSlot(toScreenHandler, to, toIdx, fromStack)) {
          continue;
        }

        if (canStacksBeMerged(toStack, fromStack)) {
          if (mergeStacks(toStack, fromStack)) {
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

  private static boolean canTakeItemFromSlot(
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

  private static boolean canPlaceItemInSlot(
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
