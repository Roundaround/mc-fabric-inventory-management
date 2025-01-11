package me.roundaround.inventorymanagement.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.List;

public class ChangeTrackingInventory implements Inventory {
  private final ArrayList<Operation> operations = new ArrayList<>();
  private final Inventory source;
  private final DefaultedList<ItemStack> stacks;

  public ChangeTrackingInventory(Inventory source) {
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

  public void setStackTracked(int slot, ItemStackRef ref) {
    this.setStack(slot, ref.stack());
    this.operations.add(new Operation(ref.originalSlot(), slot, ref.stack().getCount()));
  }

  public void reset() {
    this.operations.clear();
    this.copyStacks();
  }

  public List<Operation> getOperations() {
    return List.copyOf(this.operations);
  }

  private void copyStacks() {
    for (int i = 0; i < this.size(); i++) {
      this.setStack(i, this.source.getStack(i).copy());
    }
  }

  public record ItemStackRef(ItemStack stack, int originalSlot) {
    public static ItemStackRef empty() {
      return new ItemStackRef(ItemStack.EMPTY, -1);
    }
  }

  public record Operation(int sourceSlot, int destSlot, int count) {
  }

  public static class StacksList extends ArrayList<ItemStackRef> {
    public StacksList() {
      super();
    }

    public StacksList(int initialSize) {
      super(initialSize);
    }

    @Override
    public ItemStackRef get(int index) {
      if (index >= this.size()) {
        return ItemStackRef.empty();
      }
      return super.get(index);
    }
  }
}
