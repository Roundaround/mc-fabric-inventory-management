package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.WrapperComparatorImpl;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.Comparator;

public class CustomNameComparator extends WrapperComparatorImpl<ItemStack> {
  public CustomNameComparator() {
    super(Comparator.comparing(CustomNameComparator::getCustomName, Comparator.nullsLast(String::compareToIgnoreCase)));
  }

  private static String getCustomName(ItemStack stack) {
    Text customName = stack.get(DataComponentTypes.CUSTOM_NAME);
    return customName == null ? null : customName.getString();
  }
}
