package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.CachingComparatorImpl;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;

import java.util.Comparator;

public class ContainerFirstComparator extends CachingComparatorImpl<ItemStack, Boolean> {
  private static ContainerFirstComparator instance;

  private ContainerFirstComparator() {
    super(Comparator.reverseOrder());
  }

  public static ContainerFirstComparator getInstance() {
    if (instance == null) {
      instance = new ContainerFirstComparator();
    }
    return instance;
  }

  @Override
  protected Boolean mapValue(ItemStack stack) {
    return stack.get(DataComponentTypes.CONTAINER) != null;
  }
}
