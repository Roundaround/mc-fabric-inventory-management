package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.CachingComparatorImpl;
import me.roundaround.inventorymanagement.registry.tag.InventoryManagementItemTags;
import net.minecraft.item.ItemStack;

import java.util.Comparator;

public class ContainerFirstComparator extends CachingComparatorImpl<ItemStack, Boolean> {
  public ContainerFirstComparator() {
    super(Comparator.reverseOrder());
  }

  @Override
  protected Boolean mapValue(ItemStack stack) {
    return stack.isIn(InventoryManagementItemTags.HAS_INVENTORY);
  }
}
