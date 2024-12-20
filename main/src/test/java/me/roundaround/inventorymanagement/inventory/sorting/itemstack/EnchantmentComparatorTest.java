package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import com.google.common.collect.Lists;
import me.roundaround.inventorymanagement.testing.BaseMinecraftTest;
import net.minecraft.component.DataComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static me.roundaround.inventorymanagement.testing.AssertIterableMatches.assertIterableMatches;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class EnchantmentComparatorTest {
  @Nested
  class AppliedEnchantments extends BaseMinecraftTest {
    @Test
    void calculatesTheSummaryOnlyOncePerStack() {
      //@formatter:off
      ArrayList<ItemStack> items = Lists.newArrayList(
          createStack(Items.NETHERITE_CHESTPLATE),
          createStack(Items.NETHERITE_CHESTPLATE, DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 3)),
          createStack(Items.NETHERITE_CHESTPLATE, DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 1,
              Enchantments.UNBREAKING, 1,
              Enchantments.MENDING, 1)),
          createStack(Items.NETHERITE_CHESTPLATE, DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 2,
              Enchantments.UNBREAKING, 1)),
          createStack(Items.NETHERITE_CHESTPLATE, "1", DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.MENDING, 1,
              Enchantments.PROTECTION, 4,
              Enchantments.BLAST_PROTECTION, 1,
              Enchantments.UNBREAKING, 2)),
          createStack(Items.NETHERITE_CHESTPLATE, "2", DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.MENDING, 1,
              Enchantments.PROTECTION, 5,
              Enchantments.BLAST_PROTECTION, 1,
              Enchantments.UNBREAKING, 1)),
          createStack(Items.NETHERITE_CHESTPLATE, "3", DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.MENDING, 1,
              Enchantments.PROTECTION, 3,
              Enchantments.BLAST_PROTECTION, 3,
              Enchantments.UNBREAKING, 1))
      );
      //@formatter:on

      // TODO: Move this test down to the base class
      WrappedEnchantmentComparator comparator = new WrappedEnchantmentComparator(DataComponentTypes.ENCHANTMENTS);
      items.sort(comparator);
      assertEquals(comparator.getComputeCount(), 7);
    }

    @Test
    void ignoresActualItem() {
      //@formatter:off
      ArrayList<ItemStack> items = Lists.newArrayList(
          createStack(Items.NETHERITE_CHESTPLATE),
          createStack(Items.DIAMOND_CHESTPLATE),
          createStack(Items.FIRE_CHARGE),
          createStack(Items.BAMBOO)
      );
      //@formatter:on

      List<ItemStack> copy = List.copyOf(items);
      items.sort(new EnchantmentComparator(DataComponentTypes.ENCHANTMENTS));

      assertIterableEquals(copy, items);
    }

    @Test
    void sortsNumberOfEnchantmentsDesc() {
      //@formatter:off
      ArrayList<ItemStack> items = Lists.newArrayList(
          createStack(Items.NETHERITE_CHESTPLATE),
          createStack(Items.NETHERITE_CHESTPLATE, DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 3)),
          createStack(Items.NETHERITE_CHESTPLATE, DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 1,
              Enchantments.UNBREAKING, 1,
              Enchantments.MENDING, 1)),
          createStack(Items.NETHERITE_CHESTPLATE, DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 2,
              Enchantments.UNBREAKING, 1))
      );
      //@formatter:on

      items.sort(new EnchantmentComparator(DataComponentTypes.ENCHANTMENTS));

      assertIterableMatches(
          List.of(3, 2, 1, 0), items, Function.identity(), (stack) -> getSize(stack, DataComponentTypes.ENCHANTMENTS));
    }

    @Test
    void sortsHighestLevelDesc() {
      //@formatter:off
      ArrayList<ItemStack> items = Lists.newArrayList(
          createStack(Items.NETHERITE_CHESTPLATE, "1", DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.MENDING, 1,
              Enchantments.PROTECTION, 4,
              Enchantments.BLAST_PROTECTION, 1,
              Enchantments.UNBREAKING, 2)),
          createStack(Items.NETHERITE_CHESTPLATE, "2", DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.MENDING, 1,
              Enchantments.PROTECTION, 5,
              Enchantments.BLAST_PROTECTION, 1,
              Enchantments.UNBREAKING, 1)),
          createStack(Items.NETHERITE_CHESTPLATE, "3", DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.MENDING, 1,
              Enchantments.PROTECTION, 3,
              Enchantments.BLAST_PROTECTION, 3,
              Enchantments.UNBREAKING, 1))
      );
      //@formatter:on

      items.sort(new EnchantmentComparator(DataComponentTypes.ENCHANTMENTS));

      assertIterableEquals(List.of("2", "1", "3"), names(items));
    }

    @Test
    void sortsLevelSumDesc() {
      //@formatter:off
      ArrayList<ItemStack> items = Lists.newArrayList(
          createStack(Items.NETHERITE_CHESTPLATE, "1", DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 5,
              Enchantments.UNBREAKING, 2)),
          createStack(Items.NETHERITE_CHESTPLATE, "2", DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 5,
              Enchantments.UNBREAKING, 3)),
          createStack(Items.NETHERITE_CHESTPLATE, "3", DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 5,
              Enchantments.UNBREAKING, 1))
      );
      //@formatter:on

      items.sort(new EnchantmentComparator(DataComponentTypes.ENCHANTMENTS));

      assertIterableEquals(List.of("2", "1", "3"), names(items));
    }

    @Test
    void sortsLevelOfFirstEnchantmentDesc() {
      //@formatter:off
      ArrayList<ItemStack> items = Lists.newArrayList(
          createStack(Items.NETHERITE_CHESTPLATE, "1", DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.BLAST_PROTECTION, 5,
              Enchantments.PROTECTION, 4,
              Enchantments.UNBREAKING, 2)),
          createStack(Items.NETHERITE_CHESTPLATE, "2", DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.BLAST_PROTECTION, 5,
              Enchantments.PROTECTION, 5,
              Enchantments.UNBREAKING, 1)),
          createStack(Items.NETHERITE_CHESTPLATE, "3", DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.BLAST_PROTECTION, 5,
              Enchantments.PROTECTION, 3,
              Enchantments.UNBREAKING, 3))
      );
      //@formatter:on

      items.sort(new EnchantmentComparator(DataComponentTypes.ENCHANTMENTS));

      // Enchantments are registered in PROTECTION, BLAST_PROTECTION, UNBREAKING order.
      assertIterableEquals(List.of("2", "1", "3"), names(items));
    }

    @Test
    void sortsByAllEnchantmentRegistryIndices() {
      //@formatter:off
      ArrayList<ItemStack> items = Lists.newArrayList(
          createStack(Items.NETHERITE_CHESTPLATE, "1", DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 1,
              Enchantments.BLAST_PROTECTION, 1,
              Enchantments.UNBREAKING, 1)),
          createStack(Items.NETHERITE_CHESTPLATE, "2", DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.FIRE_PROTECTION, 1,
              Enchantments.MENDING, 1,
              Enchantments.UNBREAKING, 1)),
          createStack(Items.NETHERITE_CHESTPLATE, "3", DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 1,
              Enchantments.BLAST_PROTECTION, 1,
              Enchantments.MENDING, 1))
      );
      //@formatter:on

      items.sort(new EnchantmentComparator(DataComponentTypes.ENCHANTMENTS));

      // Enchantments are registered in PROTECTION, FIRE_PROTECTION, BLAST_PROTECTION, UNBREAKING, MENDING order.
      assertIterableEquals(List.of("1", "3", "2"), names(items));
    }
  }

  @Nested
  class StoredEnchantments extends BaseMinecraftTest {
    @Test
    void sortsNumberOfEnchantmentsDesc() {
      //@formatter:off
      ArrayList<ItemStack> items = Lists.newArrayList(
          createStack(Items.ENCHANTED_BOOK),
          createStack(Items.ENCHANTED_BOOK, DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 3)),
          createStack(Items.ENCHANTED_BOOK, DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 1,
              Enchantments.UNBREAKING, 1,
              Enchantments.MENDING, 1)),
          createStack(Items.ENCHANTED_BOOK, DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 2,
              Enchantments.UNBREAKING, 1))
      );
      //@formatter:on

      items.sort(new EnchantmentComparator(DataComponentTypes.STORED_ENCHANTMENTS));

      assertIterableMatches(List.of(3, 2, 1, 0), items, Function.identity(),
          (stack) -> getSize(stack, DataComponentTypes.STORED_ENCHANTMENTS)
      );
    }

    @Test
    void sortsHighestLevelDesc() {
      //@formatter:off
      ArrayList<ItemStack> items = Lists.newArrayList(
          createStack(Items.ENCHANTED_BOOK, "1", DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.MENDING, 1,
              Enchantments.PROTECTION, 4,
              Enchantments.BLAST_PROTECTION, 1,
              Enchantments.UNBREAKING, 2)),
          createStack(Items.ENCHANTED_BOOK, "2", DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.MENDING, 1,
              Enchantments.PROTECTION, 5,
              Enchantments.BLAST_PROTECTION, 1,
              Enchantments.UNBREAKING, 1)),
          createStack(Items.ENCHANTED_BOOK, "3", DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.MENDING, 1,
              Enchantments.PROTECTION, 3,
              Enchantments.BLAST_PROTECTION, 3,
              Enchantments.UNBREAKING, 1))
      );
      //@formatter:on

      items.sort(new EnchantmentComparator(DataComponentTypes.STORED_ENCHANTMENTS));

      assertIterableEquals(List.of("2", "1", "3"), names(items));
    }

    @Test
    void sortsLevelSumDesc() {
      //@formatter:off
      ArrayList<ItemStack> items = Lists.newArrayList(
          createStack(Items.ENCHANTED_BOOK, "1", DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 5,
              Enchantments.UNBREAKING, 2)),
          createStack(Items.ENCHANTED_BOOK, "2", DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 5,
              Enchantments.UNBREAKING, 3)),
          createStack(Items.ENCHANTED_BOOK, "3", DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 5,
              Enchantments.UNBREAKING, 1))
      );
      //@formatter:on

      items.sort(new EnchantmentComparator(DataComponentTypes.STORED_ENCHANTMENTS));

      assertIterableEquals(List.of("2", "1", "3"), names(items));
    }

    @Test
    void sortsLevelOfFirstEnchantmentDesc() {
      //@formatter:off
      ArrayList<ItemStack> items = Lists.newArrayList(
          createStack(Items.ENCHANTED_BOOK, "1", DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.BLAST_PROTECTION, 5,
              Enchantments.PROTECTION, 4,
              Enchantments.UNBREAKING, 2)),
          createStack(Items.ENCHANTED_BOOK, "2", DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.BLAST_PROTECTION, 5,
              Enchantments.PROTECTION, 5,
              Enchantments.UNBREAKING, 1)),
          createStack(Items.ENCHANTED_BOOK, "3", DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.BLAST_PROTECTION, 5,
              Enchantments.PROTECTION, 3,
              Enchantments.UNBREAKING, 3))
      );
      //@formatter:on

      items.sort(new EnchantmentComparator(DataComponentTypes.STORED_ENCHANTMENTS));

      // Enchantments are registered in PROTECTION, BLAST_PROTECTION, UNBREAKING order.
      assertIterableEquals(List.of("2", "1", "3"), names(items));
    }

    @Test
    void sortsByAllEnchantmentRegistryIndices() {
      //@formatter:off
      ArrayList<ItemStack> items = Lists.newArrayList(
          createStack(Items.ENCHANTED_BOOK, "1", DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 1,
              Enchantments.BLAST_PROTECTION, 1,
              Enchantments.UNBREAKING, 1)),
          createStack(Items.ENCHANTED_BOOK, "2", DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.FIRE_PROTECTION, 1,
              Enchantments.MENDING, 1,
              Enchantments.UNBREAKING, 1)),
          createStack(Items.ENCHANTED_BOOK, "3", DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 1,
              Enchantments.BLAST_PROTECTION, 1,
              Enchantments.MENDING, 1)));
      //@formatter:on

      items.sort(new EnchantmentComparator(DataComponentTypes.STORED_ENCHANTMENTS));

      // Enchantments are registered in PROTECTION, FIRE_PROTECTION, BLAST_PROTECTION, UNBREAKING, MENDING order.
      assertIterableEquals(List.of("1", "3", "2"), names(items));
    }
  }

  private static ItemStack createStack(Item item) {
    return createStack(item, null, Map.of());
  }

  private static ItemStack createStack(
      Item item, DataComponentType<ItemEnchantmentsComponent> dataComponentType, Map<Enchantment, Integer> enchantments
  ) {
    return createStack(item, null, dataComponentType, enchantments);
  }

  private static ItemStack createStack(
      Item item,
      String customName,
      DataComponentType<ItemEnchantmentsComponent> dataComponentType,
      Map<Enchantment, Integer> enchantments
  ) {
    ItemStack stack = new ItemStack(item);

    if (customName != null) {
      stack.set(DataComponentTypes.CUSTOM_NAME, Text.of(customName));
    }

    if (enchantments.isEmpty()) {
      return stack;
    }

    ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(
        ItemEnchantmentsComponent.DEFAULT);
    enchantments.forEach(builder::set);
    stack.set(dataComponentType, builder.build());

    return stack;
  }

  private static <T, U> List<U> select(List<T> source, Function<T, U> map) {
    return source.stream().map(map).toList();
  }

  private static List<String> names(List<ItemStack> source) {
    return select(source, (stack) -> Objects.requireNonNull(stack.get(DataComponentTypes.CUSTOM_NAME)).getString());
  }

  private static int getSize(
      ItemStack stack, DataComponentType<ItemEnchantmentsComponent> dataComponentType
  ) {
    return stack.getOrDefault(dataComponentType, ItemEnchantmentsComponent.DEFAULT).getSize();
  }

  private static class WrappedEnchantmentComparator extends EnchantmentComparator {
    private int computeCount = 0;

    public WrappedEnchantmentComparator(DataComponentType<ItemEnchantmentsComponent> type) {
      super(type);
    }

    @Override
    protected EnchantmentSummary mapValue(ItemStack stack) {
      this.computeCount++;
      return super.mapValue(stack);
    }

    public int getComputeCount() {
      return this.computeCount;
    }
  }
}
