package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import com.google.common.collect.Lists;
import me.roundaround.inventorymanagement.testing.BaseMinecraftTest;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContainerFirstComparatorTest extends BaseMinecraftTest {
  private ArrayList<ItemStack> items;

  @BeforeEach
  void populateItems() {
    this.items = Lists.newArrayList(new ItemStack(Items.FIRE_CHARGE), new ItemStack(Items.BONE_MEAL),
        new ItemStack(Items.SHULKER_BOX), new ItemStack(Items.BUNDLE), new ItemStack(Items.TARGET),
        new ItemStack(Items.BLUE_SHULKER_BOX), new ItemStack(Items.NETHERITE_CHESTPLATE)
    );
    this.items.sort(new ContainerFirstComparator());
  }

  @Test
  void putsShulkersFirst() {
    assertTrue(isShulker(this.items.get(0)));
    assertTrue(isShulker(this.items.get(1)));
  }

  @Test
  void doesNotReorderShulkersOfDifferentColors() {
    assertEquals(this.items.get(0).getItem(), Items.SHULKER_BOX);
    assertEquals(this.items.get(1).getItem(), Items.BLUE_SHULKER_BOX);
  }

  @Test
  void putsBundlesSecond() {
    assertEquals(this.items.get(2).getItem(), Items.BUNDLE);
  }

  private static boolean isShulker(ItemStack stack) {
    return stack.getItem() instanceof BlockItem block && block.getBlock() instanceof ShulkerBoxBlock;
  }
}
