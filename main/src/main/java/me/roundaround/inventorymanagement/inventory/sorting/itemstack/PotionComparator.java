package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.WrapperComparatorImpl;
import net.minecraft.item.ItemStack;

import java.util.Comparator;

public class PotionComparator extends WrapperComparatorImpl<ItemStack> {
  public PotionComparator() {
    // TODO: Order based on potion type, then effects
    super(Comparator.comparingInt((stack) -> 0));
  }
}
