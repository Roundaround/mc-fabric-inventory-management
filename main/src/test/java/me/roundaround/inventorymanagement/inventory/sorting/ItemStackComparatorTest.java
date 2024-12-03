package me.roundaround.inventorymanagement.inventory.sorting;

import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItemStackComparatorTest {
  @BeforeAll
  static void beforeAll() {
    SharedConstants.createGameVersion();
    Bootstrap.initialize();
  }

  @Test
  void emptyTest() {
    ItemStack stack = new ItemStack(Items.NETHERITE_CHESTPLATE);
    assertTrue(stack.isOf(Items.NETHERITE_CHESTPLATE));
  }
}
