package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.CachingComparatorImpl;
import me.roundaround.inventorymanagement.inventory.sorting.LexicographicalListComparator;
import me.roundaround.inventorymanagement.inventory.sorting.SerialComparator;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BannerComparator extends CachingComparatorImpl<ItemStack, BannerComparator.BannerSummary> {
  private static HashMap<RegistryEntry<BannerPattern>, Integer> indices;

  public BannerComparator() {
    super(Comparator.naturalOrder());
  }

  @Override
  protected BannerSummary mapValue(ItemStack stack) {
    return BannerSummary.of(stack);
  }

  private static HashMap<RegistryEntry<BannerPattern>, Integer> getPatternIndices() {
    if (indices != null) {
      return indices;
    }

    indices = new HashMap<>();
    AtomicInteger index = new AtomicInteger(0);
    BuiltinRegistries.createWrapperLookup()
        .getWrapperOrThrow(RegistryKeys.BANNER_PATTERN)
        .streamEntries()
        .forEachOrdered((entry) -> indices.put(entry, index.getAndIncrement()));
    return indices;
  }

  protected record BannerSummary(int count, List<Integer> indices) implements Comparable<BannerSummary> {
    private static Comparator<BannerSummary> comparator;

    public static BannerSummary of(ItemStack stack) {
      BannerPatternsComponent component = stack.get(DataComponentTypes.BANNER_PATTERNS);
      if (component == null || component.layers().isEmpty()) {
        return new BannerSummary(0, List.of());
      }

      int count = component.layers().size();
      ArrayList<Integer> indices = new ArrayList<>(count);

      HashMap<RegistryEntry<BannerPattern>, Integer> allIndices = getPatternIndices();

      for (var layer : component.layers()) {
        indices.add(allIndices.get(layer.pattern()));
        indices.add(layer.color().getId());
      }

      return new BannerSummary(count, indices);
    }

    @Override
    public int compareTo(@NotNull BannerSummary other) {
      return getComparator().compare(this, other);
    }

    private static Comparator<BannerSummary> getComparator() {
      if (comparator == null) {
        comparator = SerialComparator.comparing(
            Comparator.comparingInt(BannerSummary::count),
            Comparator.comparing(BannerSummary::indices, LexicographicalListComparator.naturalOrder())
        );
      }
      return comparator;
    }
  }
}
