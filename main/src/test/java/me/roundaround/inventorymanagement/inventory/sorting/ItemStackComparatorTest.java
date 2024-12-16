package me.roundaround.inventorymanagement.inventory.sorting;

import com.google.common.collect.Lists;
import me.roundaround.inventorymanagement.inventory.sorting.itemstack.ItemStackComparator;
import me.roundaround.inventorymanagement.testing.BaseMinecraftTest;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItemStackComparatorTest extends BaseMinecraftTest {
  @Test
  void emptyTest() {
    ItemStack stack = new ItemStack(Items.NETHERITE_CHESTPLATE);
    assertTrue(stack.isOf(Items.NETHERITE_CHESTPLATE));
  }

  @Test
  void containersFirst_putsContainersFirst() {
    ArrayList<ItemStack> items = Lists.newArrayList(new ItemStack(Items.FIRE_CHARGE, 17),
        new ItemStack(Items.BONE_MEAL, 17), new ItemStack(Items.SHULKER_BOX), new ItemStack(Items.TARGET, 17),
        new ItemStack(Items.SHULKER_BOX), new ItemStack(Items.NETHERITE_CHESTPLATE)
    );

    items.sort(ItemStackComparator.containersFirst(NOOP_COMPARATOR));

    assertEquals(items.get(0).getItem(), Items.SHULKER_BOX);
    assertEquals(items.get(1).getItem(), Items.SHULKER_BOX);
  }
}
