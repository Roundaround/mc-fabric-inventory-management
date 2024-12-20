package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.IntListComparator;
import me.roundaround.inventorymanagement.inventory.sorting.SerialComparator;
import me.roundaround.inventorymanagement.inventory.sorting.WrapperComparatorImpl;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BannerComparator extends WrapperComparatorImpl<ItemStack> {
  private static HashMap<RegistryEntry<BannerPattern>, Integer> indices;

  public BannerComparator() {
    super(Comparator.comparing(BannerSummary::of, SerialComparator.comparing(
        Comparator.comparingInt(BannerSummary::count),
        Comparator.comparing(BannerSummary::indices, new IntListComparator())
    )));
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

  private record BannerSummary(int count, List<Integer> indices) {
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
  }
}
