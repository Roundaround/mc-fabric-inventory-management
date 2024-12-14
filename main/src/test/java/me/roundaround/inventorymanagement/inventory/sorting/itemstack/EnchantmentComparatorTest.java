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

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class EnchantmentComparatorTest extends BaseMinecraftTest {
  @Nested
  class AppliedEnchantments {
    @Test
    void ignoresActualItem() {
      ArrayList<ItemStack> items = Lists.newArrayList(createStack(Items.NETHERITE_CHESTPLATE),
          createStack(Items.DIAMOND_CHESTPLATE), createStack(Items.FIRE_CHARGE), createStack(Items.BAMBOO)
      );
      List<ItemStack> copy = List.copyOf(items);
      items.sort(new EnchantmentComparator(DataComponentTypes.ENCHANTMENTS));

      assertIterableEquals(copy, items);
    }

    @Test
    void sortsNumberOfEnchantmentsDesc() {
      ArrayList<ItemStack> items = Lists.newArrayList(createStack(Items.NETHERITE_CHESTPLATE),
          createStack(Items.NETHERITE_CHESTPLATE, Map.of(Enchantments.PROTECTION, 3), DataComponentTypes.ENCHANTMENTS),
          createStack(Items.NETHERITE_CHESTPLATE,
              Map.of(Enchantments.PROTECTION, 1, Enchantments.UNBREAKING, 1, Enchantments.MENDING, 1),
              DataComponentTypes.ENCHANTMENTS
          ), createStack(Items.NETHERITE_CHESTPLATE, Map.of(Enchantments.PROTECTION, 2, Enchantments.UNBREAKING, 1),
              DataComponentTypes.ENCHANTMENTS
          )
      );
      items.sort(new EnchantmentComparator(DataComponentTypes.ENCHANTMENTS));

      assertIterableEquals(List.of(3, 2, 1, 0), select(items, (stack) -> stack.getEnchantments().getSize()));
    }

    @Test
    void sortsHighestLevelDesc() {
      ArrayList<ItemStack> items = Lists.newArrayList(createStack(Items.NETHERITE_CHESTPLATE, "4",
          Map.of(Enchantments.MENDING, 1, Enchantments.PROTECTION, 4, Enchantments.BLAST_PROTECTION, 1,
              Enchantments.UNBREAKING, 2
          ), DataComponentTypes.ENCHANTMENTS
      ), createStack(Items.NETHERITE_CHESTPLATE, "5",
          Map.of(Enchantments.MENDING, 1, Enchantments.PROTECTION, 5, Enchantments.BLAST_PROTECTION, 1,
              Enchantments.UNBREAKING, 1
          ), DataComponentTypes.ENCHANTMENTS
      ), createStack(Items.NETHERITE_CHESTPLATE, "3",
          Map.of(Enchantments.MENDING, 1, Enchantments.PROTECTION, 3, Enchantments.BLAST_PROTECTION, 3,
              Enchantments.UNBREAKING, 1
          ), DataComponentTypes.ENCHANTMENTS
      ));
      items.sort(new EnchantmentComparator(DataComponentTypes.ENCHANTMENTS));

      assertIterableEquals(List.of("5", "4", "3"), names(items));
    }

    @Test
    void sortsLevelSumDesc() {
      ArrayList<ItemStack> items = Lists.newArrayList(
          createStack(Items.NETHERITE_CHESTPLATE, "7", Map.of(Enchantments.PROTECTION, 5, Enchantments.UNBREAKING, 2),
              DataComponentTypes.ENCHANTMENTS
          ),
          createStack(Items.NETHERITE_CHESTPLATE, "8", Map.of(Enchantments.PROTECTION, 5, Enchantments.UNBREAKING, 3),
              DataComponentTypes.ENCHANTMENTS
          ),
          createStack(Items.NETHERITE_CHESTPLATE, "6", Map.of(Enchantments.PROTECTION, 5, Enchantments.UNBREAKING, 1),
              DataComponentTypes.ENCHANTMENTS
          )
      );
      items.sort(new EnchantmentComparator(DataComponentTypes.ENCHANTMENTS));

      assertIterableEquals(List.of("8", "7", "6"), names(items));
    }

    @Test
    void sortsLevelOfFirstEnchantmentDesc() {
      ArrayList<ItemStack> items = Lists.newArrayList(createStack(Items.NETHERITE_CHESTPLATE, "4",
          Map.of(Enchantments.PROTECTION, 5, Enchantments.BLAST_PROTECTION, 4, Enchantments.UNBREAKING, 2),
          DataComponentTypes.ENCHANTMENTS
      ), createStack(Items.NETHERITE_CHESTPLATE, "5",
          Map.of(Enchantments.PROTECTION, 5, Enchantments.BLAST_PROTECTION, 5, Enchantments.UNBREAKING, 1),
          DataComponentTypes.ENCHANTMENTS
      ), createStack(Items.NETHERITE_CHESTPLATE, "3",
          Map.of(Enchantments.PROTECTION, 5, Enchantments.BLAST_PROTECTION, 3, Enchantments.UNBREAKING, 3),
          DataComponentTypes.ENCHANTMENTS
      ));
      items.sort(new EnchantmentComparator(DataComponentTypes.ENCHANTMENTS));

      // "First" is first alphabetically, so BLAST_PROTECTION here.
      assertIterableEquals(List.of("5", "4", "3"), names(items));
    }

    @Test
    void sortsByEnchantNamesDesc() {
      ArrayList<ItemStack> items = Lists.newArrayList(createStack(Items.NETHERITE_CHESTPLATE, "BPU",
          Map.of(Enchantments.PROTECTION, 1, Enchantments.BLAST_PROTECTION, 1, Enchantments.UNBREAKING, 1),
          DataComponentTypes.ENCHANTMENTS
      ), createStack(Items.NETHERITE_CHESTPLATE, "FMU",
          Map.of(Enchantments.FIRE_PROTECTION, 1, Enchantments.MENDING, 1, Enchantments.UNBREAKING, 1),
          DataComponentTypes.ENCHANTMENTS
      ), createStack(Items.NETHERITE_CHESTPLATE, "BMP",
          Map.of(Enchantments.PROTECTION, 1, Enchantments.BLAST_PROTECTION, 1, Enchantments.MENDING, 1),
          DataComponentTypes.ENCHANTMENTS
      ));
      items.sort(new EnchantmentComparator(DataComponentTypes.ENCHANTMENTS));

      // All enchantment names are concatenated in alphabetical order.
      assertIterableEquals(List.of("BMP", "BPU", "FMU"), names(items));
    }
  }

  @Nested
  class StoredEnchantments {
    @Test
    void sortsNumberOfEnchantmentsDesc() {
      ArrayList<ItemStack> items = Lists.newArrayList(createStack(Items.ENCHANTED_BOOK),
          createStack(Items.ENCHANTED_BOOK, Map.of(Enchantments.PROTECTION, 3), DataComponentTypes.STORED_ENCHANTMENTS),
          createStack(Items.ENCHANTED_BOOK,
              Map.of(Enchantments.PROTECTION, 1, Enchantments.UNBREAKING, 1, Enchantments.MENDING, 1),
              DataComponentTypes.STORED_ENCHANTMENTS
          ), createStack(Items.ENCHANTED_BOOK, Map.of(Enchantments.PROTECTION, 2, Enchantments.UNBREAKING, 1),
              DataComponentTypes.STORED_ENCHANTMENTS
          )
      );
      items.sort(new EnchantmentComparator(DataComponentTypes.STORED_ENCHANTMENTS));

      assertIterableEquals(
          List.of(3, 2, 1, 0), select(items, (stack) -> stack.get(DataComponentTypes.STORED_ENCHANTMENTS).getSize()));
    }

    @Test
    void sortsHighestLevelDesc() {
      ArrayList<ItemStack> items = Lists.newArrayList(createStack(Items.ENCHANTED_BOOK, "4",
          Map.of(Enchantments.MENDING, 1, Enchantments.PROTECTION, 4, Enchantments.BLAST_PROTECTION, 1,
              Enchantments.UNBREAKING, 2
          ), DataComponentTypes.STORED_ENCHANTMENTS
      ), createStack(Items.ENCHANTED_BOOK, "5",
          Map.of(Enchantments.MENDING, 1, Enchantments.PROTECTION, 5, Enchantments.BLAST_PROTECTION, 1,
              Enchantments.UNBREAKING, 1
          ), DataComponentTypes.STORED_ENCHANTMENTS
      ), createStack(Items.ENCHANTED_BOOK, "3",
          Map.of(Enchantments.MENDING, 1, Enchantments.PROTECTION, 3, Enchantments.BLAST_PROTECTION, 3,
              Enchantments.UNBREAKING, 1
          ), DataComponentTypes.STORED_ENCHANTMENTS
      ));
      items.sort(new EnchantmentComparator(DataComponentTypes.STORED_ENCHANTMENTS));

      assertIterableEquals(List.of("5", "4", "3"), names(items));
    }

    @Test
    void sortsLevelSumDesc() {
      ArrayList<ItemStack> items = Lists.newArrayList(
          createStack(Items.ENCHANTED_BOOK, "7", Map.of(Enchantments.PROTECTION, 5, Enchantments.UNBREAKING, 2),
              DataComponentTypes.STORED_ENCHANTMENTS
          ), createStack(Items.ENCHANTED_BOOK, "8", Map.of(Enchantments.PROTECTION, 5, Enchantments.UNBREAKING, 3),
              DataComponentTypes.STORED_ENCHANTMENTS
          ), createStack(Items.ENCHANTED_BOOK, "6", Map.of(Enchantments.PROTECTION, 5, Enchantments.UNBREAKING, 1),
              DataComponentTypes.STORED_ENCHANTMENTS
          ));
      items.sort(new EnchantmentComparator(DataComponentTypes.STORED_ENCHANTMENTS));

      assertIterableEquals(List.of("8", "7", "6"), names(items));
    }

    @Test
    void sortsLevelOfFirstEnchantmentDesc() {
      ArrayList<ItemStack> items = Lists.newArrayList(createStack(Items.ENCHANTED_BOOK, "4",
          Map.of(Enchantments.PROTECTION, 5, Enchantments.BLAST_PROTECTION, 4, Enchantments.UNBREAKING, 2),
          DataComponentTypes.STORED_ENCHANTMENTS
      ), createStack(Items.ENCHANTED_BOOK, "5",
          Map.of(Enchantments.PROTECTION, 5, Enchantments.BLAST_PROTECTION, 5, Enchantments.UNBREAKING, 1),
          DataComponentTypes.STORED_ENCHANTMENTS
      ), createStack(Items.ENCHANTED_BOOK, "3",
          Map.of(Enchantments.PROTECTION, 5, Enchantments.BLAST_PROTECTION, 3, Enchantments.UNBREAKING, 3),
          DataComponentTypes.STORED_ENCHANTMENTS
      ));
      items.sort(new EnchantmentComparator(DataComponentTypes.STORED_ENCHANTMENTS));

      // "First" is first alphabetically, so BLAST_PROTECTION here.
      assertIterableEquals(List.of("5", "4", "3"), names(items));
    }

    @Test
    void sortsByEnchantNamesDesc() {
      ArrayList<ItemStack> items = Lists.newArrayList(createStack(Items.ENCHANTED_BOOK, "BPU",
          Map.of(Enchantments.PROTECTION, 1, Enchantments.BLAST_PROTECTION, 1, Enchantments.UNBREAKING, 1),
          DataComponentTypes.STORED_ENCHANTMENTS
      ), createStack(Items.ENCHANTED_BOOK, "FMU",
          Map.of(Enchantments.FIRE_PROTECTION, 1, Enchantments.MENDING, 1, Enchantments.UNBREAKING, 1),
          DataComponentTypes.STORED_ENCHANTMENTS
      ), createStack(Items.ENCHANTED_BOOK, "BMP",
          Map.of(Enchantments.PROTECTION, 1, Enchantments.BLAST_PROTECTION, 1, Enchantments.MENDING, 1),
          DataComponentTypes.STORED_ENCHANTMENTS
      ));
      items.sort(new EnchantmentComparator(DataComponentTypes.STORED_ENCHANTMENTS));

      // All enchantment names are concatenated in alphabetical order.
      assertIterableEquals(List.of("BMP", "BPU", "FMU"), names(items));
    }
  }

  private static ItemStack createStack(Item item) {
    return createStack(item, Map.of(), null);
  }

  private static ItemStack createStack(
      Item item, Map<Enchantment, Integer> enchantments, DataComponentType<ItemEnchantmentsComponent> dataComponentType
  ) {
    return createStack(item, null, enchantments, dataComponentType);
  }

  private static ItemStack createStack(
      Item item,
      String customName,
      Map<Enchantment, Integer> enchantments,
      DataComponentType<ItemEnchantmentsComponent> dataComponentType
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
}
