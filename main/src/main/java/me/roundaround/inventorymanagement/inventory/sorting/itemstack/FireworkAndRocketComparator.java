package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.WrapperComparatorImpl;
import net.minecraft.item.ItemStack;

import java.util.Comparator;

public class FireworkAndRocketComparator extends WrapperComparatorImpl<ItemStack> {
  public FireworkAndRocketComparator() {
    // TODO: Order based on rocket duration or firework colors/patterns
    super(Comparator.comparingInt((stack) -> 0));
  }
}
