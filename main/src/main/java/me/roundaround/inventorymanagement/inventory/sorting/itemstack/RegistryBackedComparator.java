package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.NoOpComparator;
import me.roundaround.inventorymanagement.inventory.sorting.WrapperComparatorImpl;
import net.minecraft.item.ItemStack;

public class RegistryBackedComparator extends WrapperComparatorImpl<ItemStack> {
  private static RegistryBackedComparator instance;

  private RegistryBackedComparator() {
    // TODO: Order based on the comparator registry
    // TODO: Create a comparator registry for mods to register custom comparators based on their own data
    super(new NoOpComparator<>());
  }

  public static RegistryBackedComparator getInstance() {
    if (instance == null) {
      instance = new RegistryBackedComparator();
    }
    return new RegistryBackedComparator();
  }
}
