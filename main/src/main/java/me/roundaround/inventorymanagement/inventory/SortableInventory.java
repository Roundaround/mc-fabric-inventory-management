package me.roundaround.inventorymanagement.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class SortableInventory implements Inventory {
  private final Inventory source;
  private final DefaultedList<ItemStack> stacks;

  public SortableInventory(Inventory source) {
    this.source = source;
    this.stacks = DefaultedList.ofSize(source.size(), ItemStack.EMPTY);
    this.copyStacks();
  }

  @Override
  public int size() {
    return this.stacks.size();
  }

  @Override
  public boolean isEmpty() {
    return this.stacks.isEmpty();
  }

  @Override
  public ItemStack getStack(int slot) {
    return this.stacks.get(slot);
  }

  @Override
  public ItemStack removeStack(int slot, int amount) {
    return this.stacks.get(slot).isEmpty() ? ItemStack.EMPTY : Inventories.splitStack(this.stacks, slot, amount);
  }

  @Override
  public ItemStack removeStack(int slot) {
    if (this.stacks.get(slot).isEmpty()) {
      return ItemStack.EMPTY;
    }
    ItemStack stack = this.stacks.get(slot);
    this.stacks.set(slot, ItemStack.EMPTY);
    return stack;
  }

  @Override
  public void setStack(int slot, ItemStack stack) {
    this.stacks.set(slot, stack);
  }

  @Override
  public void markDirty() {
  }

  @Override
  public boolean canPlayerUse(PlayerEntity player) {
    return true;
  }

  @Override
  public void clear() {
    this.stacks.clear();
  }

  public ArrayList<Integer> sort(SlotRange slotRange, Comparator<ItemStack> comparator) {
    StacksList stacks = this.getNonEmptyStacksInRange(slotRange)
        .stream()
        .filter((ref) -> !ref.stack().isEmpty())
        .sorted(Comparator.comparing(ItemStackRef::stack, comparator))
        .collect(Collectors.toCollection(StacksList::new));

    ArrayList<Integer> sorted = new ArrayList<>(stacks.size());

    for (int slot = slotRange.min(); slot < slotRange.max(); slot++) {
      int stackIndex = slot - slotRange.min();
      ItemStackRef ref = stacks.getOrEmpty(stackIndex);
      sorted.add(ref.originalSlot());
    }

    return sorted;
  }

  public StacksList getNonEmptyStacksInRange(SlotRange slotRange) {
    StacksList stacks = new StacksList(slotRange.size());
    for (int i = slotRange.min(); i < slotRange.max(); i++) {
      ItemStack stack = this.getStack(i);
      if (stack.isEmpty()) {
        continue;
      }
      stacks.add(new ItemStackRef(stack.copy(), i));
    }
    return stacks;
  }

  private void copyStacks() {
    this.clear();
    for (int i = 0; i < this.size(); i++) {
      this.setStack(i, this.source.getStack(i).copy());
    }
  }

  public record ItemStackRef(ItemStack stack, int originalSlot) {
    public static ItemStackRef empty() {
      return new ItemStackRef(ItemStack.EMPTY, -1);
    }
  }

  public static class StacksList extends ArrayList<ItemStackRef> {
    public StacksList() {
      super();
    }

    public StacksList(int initialSize) {
      super(initialSize);
    }

    public ItemStackRef getOrEmpty(int index) {
      if (index >= this.size()) {
        return ItemStackRef.empty();
      }
      return this.get(index);
    }
  }

  public record SortedEntry(int source, int destination) {
  }
}
