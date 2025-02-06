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
import java.util.List;

import static me.roundaround.inventorymanagement.testing.AssertIterableMatches.selectNames;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class ContainerContentsComparatorTest extends BaseMinecraftTest {
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
    actual.sort(ContainerContentsComparator.getInstance());

    assertIterableEquals(expected, actual);
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

    actual.sort(ContainerContentsComparator.getInstance());

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

    actual.sort(ContainerContentsComparator.getInstance());

    assertIterableEquals(List.of("2", "3", "1"), selectNames(actual));
  }

  @Test
  void ignoresContainerItem() {
    //@formatter:off
    ArrayList<ItemStack> actual = Lists.newArrayList(
        createStack(Items.SHULKER_BOX, "1", generateFullInventory(Items.DIAMOND, 3)),
        createStack(Items.BLUE_SHULKER_BOX, "2", generateFullInventory(Items.DIAMOND, 5)),
        createStack(Items.BUNDLE, "3", generateFullInventory(Items.DIAMOND, 6)),
        createStack(Items.SHULKER_BOX, "4", generateFullInventory(Items.DIAMOND, 4)),
        createStack(Items.BLUE_SHULKER_BOX, "5", generateFullInventory(Items.DIAMOND, 1)),
        createStack(Items.BUNDLE, "6", generateFullInventory(Items.DIAMOND, 2))
    );
    //@formatter:on

    actual.sort(ContainerContentsComparator.getInstance());

    assertIterableEquals(List.of("3", "2", "4", "1", "6", "5"), selectNames(actual));
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
