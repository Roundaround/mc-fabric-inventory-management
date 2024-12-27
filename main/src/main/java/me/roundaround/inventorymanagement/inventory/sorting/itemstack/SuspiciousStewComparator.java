package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.WrapperComparatorImpl;
import net.minecraft.item.ItemStack;

import java.util.Comparator;

public class SuspiciousStewComparator extends WrapperComparatorImpl<ItemStack> {
  public SuspiciousStewComparator() {
    // TODO: Order based on suspicious stew effects
    super(Comparator.comparingInt((stack) -> 0));
  }
}
