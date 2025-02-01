package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.WrapperComparatorImpl;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;

import java.util.Comparator;

public class PlayerHeadNameComparator extends WrapperComparatorImpl<ItemStack> {
  public PlayerHeadNameComparator() {
    super(Comparator.comparing(PlayerHeadNameComparator::getPlayerHeadName,
        Comparator.nullsLast(String::compareToIgnoreCase)
    ));
  }

  private static String getPlayerHeadName(ItemStack stack) {
    ProfileComponent profile = stack.get(DataComponentTypes.PROFILE);
    return profile == null || profile.name().isEmpty() ? null : profile.name().get();
  }
}
