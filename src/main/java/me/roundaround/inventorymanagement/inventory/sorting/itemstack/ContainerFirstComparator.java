package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.CachingComparatorImpl;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;

import java.util.Comparator;

public class ContainerFirstComparator extends CachingComparatorImpl<ItemStack, Integer> {
  private static ContainerFirstComparator instance;

  private ContainerFirstComparator() {
    super(Comparator.naturalOrder());
  }

  public static ContainerFirstComparator getInstance() {
    if (instance == null) {
      instance = new ContainerFirstComparator();
    }
    return instance;
  }

  @Override
  protected Integer mapValue(ItemStack stack) {
    if (stack.get(DataComponentTypes.CONTAINER) != null) {
      return 1;
    }
    if (stack.get(DataComponentTypes.BUNDLE_CONTENTS) != null) {
      return 2;
    }
    return 10;
  }
}
