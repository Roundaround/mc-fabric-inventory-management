package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.CachingComparatorImpl;
import me.roundaround.inventorymanagement.inventory.sorting.LexicographicalListComparator;
import me.roundaround.inventorymanagement.inventory.sorting.PredicatedComparator;
import me.roundaround.inventorymanagement.inventory.sorting.SerialComparator;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SuspiciousStewComparator extends CachingComparatorImpl<ItemStack,
    List<SuspiciousStewComparator.EffectSummary>> {
  private static HashMap<RegistryEntry<StatusEffect>, Integer> indices;

  public SuspiciousStewComparator() {
    //@formatter:off
    super(PredicatedComparator.ignoreNulls(
        SerialComparator.comparing(
            Comparator.comparing(List::size, Comparator.reverseOrder()),
            LexicographicalListComparator.naturalOrder()
        )
    ));
    //@formatter:on
  }

  @Override
  protected List<SuspiciousStewComparator.EffectSummary> mapValue(ItemStack stack) {
    SuspiciousStewEffectsComponent component = stack.get(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS);
    if (component == null) {
      return null;
    }
    return component.effects()
        .stream()
        .map(EffectSummary::of)
        .sorted(PredicatedComparator.ignoreNullsNaturalOrder())
        .toList();
  }

  private static HashMap<RegistryEntry<StatusEffect>, Integer> getEffectIndices() {
    if (indices != null) {
      return indices;
    }

    indices = new HashMap<>();
    AtomicInteger index = new AtomicInteger(0);
    BuiltinRegistries.createWrapperLookup()
        .getWrapperOrThrow(RegistryKeys.STATUS_EFFECT)
        .streamEntries()
        .forEachOrdered((entry) -> indices.put(entry, index.getAndIncrement()));
    return indices;
  }

  protected record EffectSummary(int index, int duration) implements Comparable<EffectSummary> {
    private static Comparator<EffectSummary> comparator;

    public static EffectSummary of(SuspiciousStewEffectsComponent.StewEffect effect) {
      HashMap<RegistryEntry<StatusEffect>, Integer> effectIndices = getEffectIndices();
      Integer index = effectIndices.get(effect.effect());
      if (index == null) {
        return null;
      }
      return new EffectSummary(index, effect.duration());
    }

    @Override
    public int compareTo(@NotNull EffectSummary other) {
      return getComparator().compare(this, other);
    }

    private static Comparator<EffectSummary> getComparator() {
      if (comparator == null) {
        //@formatter:off
        comparator = SerialComparator.comparing(
            Comparator.comparingInt(EffectSummary::index),
            Comparator.comparingInt(EffectSummary::duration).reversed()
        );
        //@formatter:on
      }
      return comparator;
    }
  }
}
