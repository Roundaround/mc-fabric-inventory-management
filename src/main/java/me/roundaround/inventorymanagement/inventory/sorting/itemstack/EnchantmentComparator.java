package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.CachingComparatorImpl;
import me.roundaround.inventorymanagement.inventory.sorting.LexicographicalListComparator;
import me.roundaround.inventorymanagement.inventory.sorting.SerialComparator;
import net.minecraft.component.DataComponentType;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class EnchantmentComparator extends CachingComparatorImpl<ItemStack, EnchantmentComparator.EnchantmentSummary> {
  private static HashMap<Enchantment, Integer> indices;

  private final DataComponentType<ItemEnchantmentsComponent> type;

  public EnchantmentComparator(DataComponentType<ItemEnchantmentsComponent> type) {
    super(Comparator.naturalOrder());
    this.type = type;
  }

  @Override
  protected EnchantmentSummary mapValue(ItemStack stack) {
    return EnchantmentSummary.of(stack, this.type);
  }

  private static HashMap<Enchantment, Integer> getEnchantmentIndices() {
    if (indices != null) {
      return indices;
    }

    indices = new HashMap<>();
    AtomicInteger index = new AtomicInteger(0);
    Registries.ENCHANTMENT.forEach((enchantment) -> indices.put(enchantment, index.getAndIncrement()));
    return indices;
  }

  protected record EnchantmentSummary(int count, int max, int sum, int first, List<Integer> indices) implements
      Comparable<EnchantmentSummary> {
    private static Comparator<EnchantmentSummary> comparator;

    public static EnchantmentSummary of(ItemStack stack, DataComponentType<ItemEnchantmentsComponent> type) {
      ItemEnchantmentsComponent component = stack.get(type);
      if (component == null || component.isEmpty()) {
        return new EnchantmentSummary(0, 0, 0, 0, List.of());
      }

      int count = component.getSize();
      int max = 0;
      int sum = 0;
      int first = 0;
      ArrayList<Integer> indices = new ArrayList<>(count);

      HashMap<Enchantment, Integer> allIndices = getEnchantmentIndices();

      // Sort the list of enchantments ahead of time so that it's deterministic
      LinkedHashSet<RegistryEntry<Enchantment>> enchantments = component.getEnchantments()
          .stream()
          .sorted(Comparator.comparingInt((entry) -> allIndices.getOrDefault(entry.value(), Integer.MAX_VALUE)))
          .collect(Collectors.toCollection(LinkedHashSet::new));

      for (var entry : enchantments) {
        int level = component.getLevel(entry.value());
        max = Math.max(max, level);
        sum += level;

        int index = allIndices.getOrDefault(entry.value(), -1);
        if (index > -1) {
          if (first == 0) {
            first = level;
          }
          indices.add(index);
        }
      }

      return new EnchantmentSummary(count, max, sum, first, indices);
    }

    @Override
    public int compareTo(@NotNull EnchantmentSummary other) {
      return getComparator().compare(this, other);
    }

    private static Comparator<EnchantmentSummary> getComparator() {
      if (comparator == null) {
        comparator = SerialComparator.comparing(
            Comparator.comparingInt(EnchantmentSummary::count).reversed(),
            Comparator.comparingInt(EnchantmentSummary::max).reversed(),
            Comparator.comparingInt(EnchantmentSummary::sum).reversed(),
            Comparator.comparingInt(EnchantmentSummary::first).reversed(),
            Comparator.comparing(EnchantmentSummary::indices, LexicographicalListComparator.naturalOrder())
        );
      }
      return comparator;
    }
  }
}
