package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import com.google.common.collect.Lists;
import me.roundaround.inventorymanagement.testing.BaseMinecraftTest;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.roundaround.inventorymanagement.testing.AssertIterableMatches.selectNames;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class ContainerContentsComparatorTest extends BaseMinecraftTest {
  @Test
  void ignoresItemsWithoutComponent() {
    //@formatter:off
    ArrayList<ItemStack> samples = Lists.newArrayList(
        createEmpty(Items.NETHERITE_CHESTPLATE),
        createEmpty(Items.RED_BANNER),
        createEmpty(Items.DIAMOND_CHESTPLATE),
        createEmpty(Items.FIRE_CHARGE),
        createEmpty(Items.SHULKER_BOX),
        createEmpty(Items.BLUE_BANNER),
        createEmpty(Items.BAMBOO)
    );
    //@formatter:on

    ContainerContentsComparator comparator = new ContainerContentsComparator();
    for (ItemStack a : samples) {
      for (ItemStack b : samples) {
        assertEquals(0, comparator.compare(a, b));
      }
    }
  }

  @Test
  void ignoresActualItem() {
    //@formatter:off
    ArrayList<ItemStack> samples = Lists.newArrayList(
        createStack(Items.SHULKER_BOX, generateFullInventory(Items.DIRT, 27)),
        createStack(Items.BLUE_SHULKER_BOX, generateFullInventory(Items.DIRT, 27)),
        createStack(Items.ORANGE_SHULKER_BOX, generateFullInventory(Items.DIRT, 27)),
        createStack(Items.GREEN_SHULKER_BOX, generateFullInventory(Items.DIRT, 27))
    );
    //@formatter:on

    ContainerContentsComparator comparator = new ContainerContentsComparator();
    for (ItemStack a : samples) {
      for (ItemStack b : samples) {
        assertEquals(0, comparator.compare(a, b));
      }
    }
  }

  @Test
  void ignoresActualContentItems() {
    //@formatter:off
    ArrayList<ItemStack> samples = Lists.newArrayList(
        createStack(generateFullInventory(Items.DIRT, 27)),
        createStack(generateFullInventory(Items.DIAMOND, 27)),
        createStack(generateFullInventory(Items.MUD, 27)),
        createStack(generateFullInventory(Items.SAND, 27))
    );
    //@formatter:on

    ContainerContentsComparator comparator = new ContainerContentsComparator();
    for (ItemStack a : samples) {
      for (ItemStack b : samples) {
        assertEquals(0, comparator.compare(a, b));
      }
    }
  }

  @Test
  void sortsDescendingBySlotsUsed() {
    //@formatter:off
    ArrayList<ItemStack> actual = Lists.newArrayList(
        createStack("1", generateFullInventory(Items.DIAMOND, 3)),
        createStack("2", generateFullInventory(Items.DIAMOND, 5)),
        createStack("3", generateFullInventory(Items.DIAMOND, 6)),
        createStack("4", generateFullInventory(Items.DIAMOND, 4)),
        createStack("5", generateFullInventory(Items.DIAMOND, 1)),
        createStack("6", generateFullInventory(Items.DIAMOND, 2))
    );
    //@formatter:on

    Collections.shuffle(actual);
    actual.sort(new ContainerContentsComparator());

    assertIterableEquals(List.of("3", "2", "4", "1", "6", "5"), selectNames(actual));
  }

  @Test
  void sortsDescendingByTotalItemCount() {
    //@formatter:off
    ArrayList<ItemStack> actual = Lists.newArrayList(
        createStack("1", List.of(
            new ItemStack(Items.DIAMOND, 16),
            new ItemStack(Items.DIAMOND, 16),
            new ItemStack(Items.DIAMOND, 16))),
        createStack("2", List.of(
            new ItemStack(Items.DIAMOND, 16),
            new ItemStack(Items.DIAMOND, 64),
            new ItemStack(Items.DIAMOND, 16))),
        createStack("3", List.of(
            new ItemStack(Items.DIAMOND, 16),
            new ItemStack(Items.DIAMOND, 48),
            new ItemStack(Items.DIAMOND, 16)))
    );
    //@formatter:on

    Collections.shuffle(actual);
    actual.sort(new ContainerContentsComparator());

    assertIterableEquals(List.of("2", "3", "1"), selectNames(actual));
  }

  private static ItemStack createEmpty(Item item) {
    ItemStack stack = new ItemStack(item);
    stack.remove(DataComponentTypes.CONTAINER);
    return stack;
  }

  private static ItemStack createStack(List<ItemStack> items) {
    return createStack("", items);
  }

  private static ItemStack createStack(Item container, List<ItemStack> items) {
    return createStack(container, "", items);
  }

  private static ItemStack createStack(String customName, List<ItemStack> items) {
    return createStack(Items.SHULKER_BOX, customName, items);
  }

  private static ItemStack createStack(Item container, String customName, List<ItemStack> items) {
    ItemStack stack = new ItemStack(container);

    if (customName != null && !customName.isBlank()) {
      stack.set(DataComponentTypes.CUSTOM_NAME, Text.of(customName));
    }

    stack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(items));
    return stack;
  }

  private static List<ItemStack> generateFullInventory(Item item, int slots) {
    ArrayList<ItemStack> list = new ArrayList<>(slots);
    for (int i = 0; i < slots; i++) {
      ItemStack stack = new ItemStack(item);
      stack.setCount(stack.getMaxCount());
      list.add(stack);
    }
    return list;
  }
}
