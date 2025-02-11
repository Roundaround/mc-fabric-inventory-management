package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import com.google.common.collect.Lists;
import me.roundaround.inventorymanagement.testing.BaseMinecraftTest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static me.roundaround.inventorymanagement.testing.DataGen.getUniquePairs;
import static me.roundaround.inventorymanagement.testing.IterableMatchHelpers.assertPreservesOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DamageComparatorTest extends BaseMinecraftTest {
  private static DamageComparator comparator;

  @BeforeAll
  static void beforeAll() {
    comparator = new DamageComparator();
  }

  @ParameterizedTest
  @MethodSource("getMiscSamples")
  void ignoresActualItem(ItemStack a, ItemStack b) {
    assertEquals(0, comparator.compare(a, b));
  }

  private static Stream<Arguments> getMiscSamples() {
    return getUniquePairs(List.of(
        new ItemStack(Items.NETHERITE_CHESTPLATE),
        new ItemStack(Items.RED_BANNER),
        new ItemStack(Items.DIAMOND_CHESTPLATE),
        new ItemStack(Items.FIRE_CHARGE),
        new ItemStack(Items.BLUE_BANNER),
        new ItemStack(Items.BAMBOO)
    ));
  }

  @Test
  void sortsDamageDesc() {
    //@formatter:off
    assertPreservesOrder(comparator, Lists.newArrayList(
        createStack(Items.DIAMOND_CHESTPLATE, 10),
        createStack(Items.DIAMOND_CHESTPLATE, 5),
        createStack(Items.DIAMOND_CHESTPLATE, 0)
    ));
    //@formatter:on
  }

  @ParameterizedTest
  @MethodSource("getDamagedSamples")
  void ignoresMaxDamage(ItemStack a, ItemStack b) {
    assertEquals(0, comparator.compare(a, b));
  }

  private static Stream<Arguments> getDamagedSamples() {
    return getUniquePairs(List.of(
        createStack(Items.DIAMOND_CHESTPLATE, 10),
        createStack(Items.GOLDEN_CHESTPLATE, 10),
        createStack(Items.IRON_SWORD, 10),
        createStack(Items.WOODEN_AXE, 10)
    ));
  }

  private static ItemStack createStack(Item item, int damage) {
    ItemStack stack = new ItemStack(item);
    stack.setDamage(damage);
    return stack;
  }
}
