package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.WrapperComparatorImpl;
import net.minecraft.item.ItemStack;

import java.util.Comparator;

public class CountComparator extends WrapperComparatorImpl<ItemStack> {
  public CountComparator() {
    super(Comparator.comparingInt(ItemStack::getCount).reversed());
  }
}
