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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static me.roundaround.inventorymanagement.testing.DataGen.createListOfEmpty;
import static me.roundaround.inventorymanagement.testing.DataGen.getUniquePairs;
import static me.roundaround.inventorymanagement.testing.IterableMatchHelpers.assertPreservesOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnchantmentComparatorTest {
  @Nested
  class AppliedEnchantments extends BaseMinecraftTest {
    private static EnchantmentComparator comparator;

    @BeforeAll
    static void beforeAll() {
      comparator = new EnchantmentComparator(DataComponentTypes.ENCHANTMENTS);
    }

    @ParameterizedTest
    @MethodSource("getEmptySamples")
    void ignoresItemsWithoutComponent(ItemStack a, ItemStack b) {
      assertEquals(0, comparator.compare(a, b));
    }

    private static Stream<Arguments> getEmptySamples() {
      return getUniquePairs(createListOfEmpty(
          DataComponentTypes.ENCHANTMENTS,
          Items.NETHERITE_CHESTPLATE,
          Items.RED_BANNER,
          Items.DIAMOND_CHESTPLATE,
          Items.FIRE_CHARGE,
          Items.DECORATED_POT,
          Items.BLUE_BANNER,
          Items.BAMBOO
      ));
    }

    @ParameterizedTest
    @MethodSource("getMiscSamples")
    void ignoresActualItem(ItemStack a, ItemStack b) {
      assertEquals(0, comparator.compare(a, b));
    }

    private static Stream<Arguments> getMiscSamples() {
      return getUniquePairs(List.of(
          createStack(Items.NETHERITE_CHESTPLATE),
          createStack(Items.DIAMOND_CHESTPLATE),
          createStack(Items.FIRE_CHARGE),
          createStack(Items.BAMBOO)
      ));
    }

    @Test
    void sortsNumberOfEnchantmentsDesc() {
      //@formatter:off
      assertPreservesOrder(comparator, Lists.newArrayList(
          createStack(Items.NETHERITE_CHESTPLATE, DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 1,
              Enchantments.UNBREAKING, 1,
              Enchantments.MENDING, 1)),
          createStack(Items.NETHERITE_CHESTPLATE, DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 2,
              Enchantments.UNBREAKING, 1)),
          createStack(Items.NETHERITE_CHESTPLATE, DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 3)),
          createStack(Items.NETHERITE_CHESTPLATE)
      ));
      //@formatter:on
    }

    @Test
    void sortsHighestLevelDesc() {
      //@formatter:off
      assertPreservesOrder(comparator, Lists.newArrayList(
          createStack(Items.NETHERITE_CHESTPLATE, DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.MENDING, 1,
              Enchantments.PROTECTION, 5,
              Enchantments.BLAST_PROTECTION, 1,
              Enchantments.UNBREAKING, 1)),
          createStack(Items.NETHERITE_CHESTPLATE, DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.MENDING, 1,
              Enchantments.PROTECTION, 4,
              Enchantments.BLAST_PROTECTION, 1,
              Enchantments.UNBREAKING, 2)),
          createStack(Items.NETHERITE_CHESTPLATE, DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.MENDING, 1,
              Enchantments.PROTECTION, 3,
              Enchantments.BLAST_PROTECTION, 3,
              Enchantments.UNBREAKING, 1))
      ));
      //@formatter:on
    }

    @Test
    void sortsLevelSumDesc() {
      //@formatter:off
      assertPreservesOrder(comparator, Lists.newArrayList(
          createStack(Items.NETHERITE_CHESTPLATE, DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 5,
              Enchantments.UNBREAKING, 3)),
          createStack(Items.NETHERITE_CHESTPLATE, DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 5,
              Enchantments.UNBREAKING, 2)),
          createStack(Items.NETHERITE_CHESTPLATE, DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 5,
              Enchantments.UNBREAKING, 1))
      ));
      //@formatter:on
    }

    @Test
    void sortsByEnchantmentNames() {
      //@formatter:off
      assertPreservesOrder(comparator, Lists.newArrayList(
          createStack(Items.NETHERITE_CHESTPLATE, DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.BLAST_PROTECTION, 1,
              Enchantments.PROTECTION, 1,
              Enchantments.SILK_TOUCH, 1)),
          createStack(Items.NETHERITE_CHESTPLATE, DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.BLAST_PROTECTION, 1,
              Enchantments.PROTECTION, 1,
              Enchantments.UNBREAKING, 1)),
          createStack(Items.NETHERITE_CHESTPLATE, DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.FIRE_PROTECTION, 1,
              Enchantments.MENDING, 1,
              Enchantments.UNBREAKING, 1))
      ));
      //@formatter:on
    }

    @Test
    void sortsByEnchantmentLevels() {
      //@formatter:off
      assertPreservesOrder(comparator, Lists.newArrayList(
          createStack(Items.NETHERITE_CHESTPLATE, DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.BLAST_PROTECTION, 1,
              Enchantments.PROTECTION, 3,
              Enchantments.UNBREAKING, 1)),
          createStack(Items.NETHERITE_CHESTPLATE, DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.BLAST_PROTECTION, 1,
              Enchantments.PROTECTION, 1,
              Enchantments.UNBREAKING, 3)),
          createStack(Items.NETHERITE_CHESTPLATE, DataComponentTypes.ENCHANTMENTS, Map.of(
              Enchantments.FIRE_PROTECTION, 3,
              Enchantments.MENDING, 1,
              Enchantments.UNBREAKING, 1))
      ));
      //@formatter:on
    }
  }

  @Nested
  class StoredEnchantments extends BaseMinecraftTest {
    private static EnchantmentComparator comparator;

    @BeforeAll
    static void beforeAll() {
      comparator = new EnchantmentComparator(DataComponentTypes.STORED_ENCHANTMENTS);
    }

    @Test
    void sortsNumberOfEnchantmentsDesc() {
      //@formatter:off
      assertPreservesOrder(comparator, Lists.newArrayList(
          createStack(Items.ENCHANTED_BOOK, DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 1,
              Enchantments.UNBREAKING, 1,
              Enchantments.MENDING, 1)),
          createStack(Items.ENCHANTED_BOOK, DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 2,
              Enchantments.UNBREAKING, 1)),
          createStack(Items.ENCHANTED_BOOK, DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 3)),
          createStack(Items.ENCHANTED_BOOK)
      ));
      //@formatter:on
    }

    @Test
    void sortsHighestLevelDesc() {
      //@formatter:off
      assertPreservesOrder(comparator, Lists.newArrayList(
          createStack(Items.ENCHANTED_BOOK, DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.MENDING, 1,
              Enchantments.PROTECTION, 5,
              Enchantments.BLAST_PROTECTION, 1,
              Enchantments.UNBREAKING, 1)),
          createStack(Items.ENCHANTED_BOOK, DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.MENDING, 1,
              Enchantments.PROTECTION, 4,
              Enchantments.BLAST_PROTECTION, 1,
              Enchantments.UNBREAKING, 2)),
          createStack(Items.ENCHANTED_BOOK, DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.MENDING, 1,
              Enchantments.PROTECTION, 3,
              Enchantments.BLAST_PROTECTION, 3,
              Enchantments.UNBREAKING, 1))
      ));
      //@formatter:on
    }

    @Test
    void sortsLevelSumDesc() {
      //@formatter:off
      assertPreservesOrder(comparator, Lists.newArrayList(
          createStack(Items.ENCHANTED_BOOK, DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 5,
              Enchantments.UNBREAKING, 3)),
          createStack(Items.ENCHANTED_BOOK, DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 5,
              Enchantments.UNBREAKING, 2)),
          createStack(Items.ENCHANTED_BOOK, DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.PROTECTION, 5,
              Enchantments.UNBREAKING, 1))
      ));
      //@formatter:on
    }

    @Test
    void sortsByEnchantmentNames() {
      //@formatter:off
      assertPreservesOrder(comparator, Lists.newArrayList(
          createStack(Items.ENCHANTED_BOOK, DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.BLAST_PROTECTION, 1,
              Enchantments.PROTECTION, 1,
              Enchantments.SILK_TOUCH, 1)),
          createStack(Items.ENCHANTED_BOOK, DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.BLAST_PROTECTION, 1,
              Enchantments.PROTECTION, 1,
              Enchantments.UNBREAKING, 1)),
          createStack(Items.ENCHANTED_BOOK, DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.FIRE_PROTECTION, 1,
              Enchantments.MENDING, 1,
              Enchantments.UNBREAKING, 1))
      ));
      //@formatter:on
    }

    @Test
    void sortsByEnchantmentLevels() {
      //@formatter:off
      assertPreservesOrder(comparator, Lists.newArrayList(
          createStack(Items.ENCHANTED_BOOK, DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.BLAST_PROTECTION, 1,
              Enchantments.PROTECTION, 3,
              Enchantments.UNBREAKING, 1)),
          createStack(Items.ENCHANTED_BOOK, DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.BLAST_PROTECTION, 1,
              Enchantments.PROTECTION, 1,
              Enchantments.UNBREAKING, 3)),
          createStack(Items.ENCHANTED_BOOK, DataComponentTypes.STORED_ENCHANTMENTS, Map.of(
              Enchantments.FIRE_PROTECTION, 3,
              Enchantments.MENDING, 1,
              Enchantments.UNBREAKING, 1))
      ));
      //@formatter:on
    }
  }

  private static ItemStack createStack(Item item) {
    return createStack(item, null, Map.of());
  }

  private static ItemStack createStack(
      Item item, DataComponentType<ItemEnchantmentsComponent> dataComponentType, Map<Enchantment, Integer> enchantments
  ) {
    ItemStack stack = new ItemStack(item);

    if (enchantments.isEmpty()) {
      return stack;
    }

    ItemEnchantmentsComponent.Builder builder =
        new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
    enchantments.forEach(builder::set);
    stack.set(dataComponentType, builder.build());

    return stack;
  }
}
