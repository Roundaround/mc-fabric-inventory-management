package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import com.google.common.collect.Lists;
import me.roundaround.inventorymanagement.testing.BaseMinecraftTest;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static me.roundaround.inventorymanagement.testing.AssertIterableMatches.assertIterableMatches;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class CountComparatorTest extends BaseMinecraftTest {
  @Test
  void ignoresActualItem() {
    //@formatter:off
    ArrayList<ItemStack> actual = Lists.newArrayList(
        new ItemStack(Items.NETHERITE_CHESTPLATE),
        new ItemStack(Items.RED_BANNER),
        new ItemStack(Items.DIAMOND_CHESTPLATE),
        new ItemStack(Items.FIRE_CHARGE),
        new ItemStack(Items.BLUE_BANNER),
        new ItemStack(Items.BAMBOO)
    );
    //@formatter:on

    List<ItemStack> expected = List.copyOf(actual);
    actual.sort(new CountComparator());

    assertIterableEquals(expected, actual);
  }

  @Test
  void sortsCountDesc() {
    //@formatter:off
    ArrayList<ItemStack> actual = Lists.newArrayList(
        new ItemStack(Items.DIAMOND, 16),
        new ItemStack(Items.DIAMOND, 64),
        new ItemStack(Items.DIAMOND, 48)
    );
    //@formatter:on

    actual.sort(new CountComparator());

    assertIterableMatches(List.of(64, 48, 16), actual, Function.identity(), ItemStack::getCount);
  }

  @Test
  void ignoresMaxCount() {
    //@formatter:off
    ArrayList<ItemStack> actual = Lists.newArrayList(
        new ItemStack(Items.DIAMOND_SWORD, 16),
        new ItemStack(Items.DIAMOND, 64),
        new ItemStack(Items.SNOWBALL, 48)
    );
    //@formatter:on

    actual.sort(new CountComparator());

    assertIterableMatches(List.of(64, 48, 16), actual, Function.identity(), ItemStack::getCount);
  }
}
