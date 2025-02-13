package me.roundaround.inventorymanagement.server.inventory;

import me.roundaround.inventorymanagement.api.gui.SlotRangeRegistry;
import me.roundaround.inventorymanagement.inventory.SlotRange;
import me.roundaround.inventorymanagement.server.network.ServerNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;

import static me.roundaround.inventorymanagement.inventory.InventoryHelper.*;

public final class ServerInventoryHelper {
  private ServerInventoryHelper() {
  }

  public static void applySort(
      PlayerEntity player, boolean isPlayerInventory, List<Integer> sorted, List<Integer> locked
  ) {
    if (isPlayerInventory) {
      applyPlayerSort(player, sorted, locked);
    } else {
      applyContainerSort(player, sorted);
    }
  }

  public static void applyPlayerSort(PlayerEntity player, List<Integer> sorted, List<Integer> locked) {
    Inventory inventory = player.getInventory();
    SlotRange slotRange = SlotRangeRegistry.getPlayerSide(player, inventory).withExclusions(locked);
    applySort(player, inventory, slotRange, sorted);
  }

  public static void applyContainerSort(PlayerEntity player, List<Integer> sorted) {
    Inventory inventory = getContainerInventory(player);
    if (inventory == null) {
      ServerNetworking.sendErrorAlert(player, "No sortable container found!");
      return;
    }

    SlotRange slotRange = SlotRangeRegistry.getContainerSide(player, inventory);
    applySort(player, inventory, slotRange, sorted);
  }

  private static void applySort(PlayerEntity player, Inventory inventory, SlotRange slotRange, List<Integer> sorted) {
    HashSet<Integer> slotsWithItems = new HashSet<>();
    for (int i : slotRange.getSlots()) {
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
        ServerNetworking.sendCheaterAlert(player, "Specified an invalid source inventory slot index");
        return;
      }
    }

    if (!slotsWithItems.isEmpty()) {
      ServerNetworking.sendCheaterAlert(player, "Missing destination index for at least one slot");
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
    for (int slotIndex : slotRange.getSlots()) {
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

  public static void autoStack(PlayerEntity player, boolean fromPlayerInventory, List<Integer> locked) {
    Inventory containerInventory = getContainerInventory(player);
    if (containerInventory == null) {
      return;
    }

    Inventory playerInventory = player.getInventory();
    SlotRange playerSlotRange = SlotRangeRegistry.getPlayerSide(player, playerInventory).withExclusions(locked);
    SlotRange containerSlotRange = SlotRangeRegistry.getContainerSide(player, containerInventory);

    if (fromPlayerInventory) {
      autoStackInventories(playerInventory, playerSlotRange, containerInventory, containerSlotRange, player);
    } else {
      autoStackInventories(containerInventory, containerSlotRange, playerInventory, playerSlotRange, player);
    }
  }

  private static void autoStackInventories(
      Inventory from, SlotRange fromRange, Inventory to, SlotRange toRange, PlayerEntity player
  ) {
    transferEntireInventory(from, fromRange, to, toRange, (fromStack, toStack) -> !toStack.isEmpty(), player);
  }

  public static void transferAll(PlayerEntity player, boolean fromPlayerInventory, List<Integer> locked) {
    Inventory containerInventory = getContainerInventory(player);
    if (containerInventory == null) {
      return;
    }

    Inventory playerInventory = player.getInventory();
    SlotRange playerSlotRange = SlotRangeRegistry.getPlayerSide(player, playerInventory).withExclusions(locked);
    SlotRange containerSlotRange = SlotRangeRegistry.getContainerSide(player, containerInventory);

    if (fromPlayerInventory) {
      transferEntireInventory(playerInventory,
          playerSlotRange,
          containerInventory,
          containerSlotRange,
          player.playerScreenHandler,
          player.currentScreenHandler,
          player
      );
    } else {
      transferEntireInventory(containerInventory,
          containerSlotRange,
          playerInventory,
          playerSlotRange,
          player.currentScreenHandler,
          player.playerScreenHandler,
          player
      );
    }
  }

  private static void transferEntireInventory(
      Inventory from,
      SlotRange fromRange,
      Inventory to,
      SlotRange toRange,
      BiFunction<ItemStack, ItemStack, Boolean> predicate,
      PlayerEntity player
  ) {
    transferEntireInventory(from, fromRange, to, toRange, predicate, null, null, player);
  }

  private static void transferEntireInventory(
      Inventory from,
      SlotRange fromRange,
      Inventory to,
      SlotRange toRange,
      ScreenHandler fromScreenHandler,
      ScreenHandler toScreenHandler,
      PlayerEntity player
  ) {
    transferEntireInventory(from,
        fromRange,
        to,
        toRange,
        (fromStack, toStack) -> true,
        fromScreenHandler,
        toScreenHandler,
        player
    );
  }

  private static void transferEntireInventory(
      Inventory from,
      SlotRange fromRange,
      Inventory to,
      SlotRange toRange,
      BiFunction<ItemStack, ItemStack, Boolean> predicate,
      ScreenHandler fromScreenHandler,
      ScreenHandler toScreenHandler,
      PlayerEntity player
  ) {
    for (int toIdx : toRange.getSlots()) {
      for (int fromIdx : fromRange.getSlots()) {
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
}
