package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.WrapperComparatorImpl;
import net.minecraft.item.ItemStack;

import java.util.Comparator;

public class InstrumentComparator extends WrapperComparatorImpl<ItemStack> {
  public InstrumentComparator() {
    // TODO: Order based on instrument type (goat horn sound)
    super(Comparator.comparingInt((stack) -> 0));
  }
}
