package me.roundaround.inventorymanagement.inventory.sorting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItemStackComparatorTest {
  @Test
  void emptyTest() {
    ItemStack stack = new ItemStack(Items.NETHERITE_CHESTPLATE);
    assertTrue(stack.isOf(Items.NETHERITE_CHESTPLATE));
  }
}
