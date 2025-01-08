package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.CachingComparatorImpl;
import me.roundaround.inventorymanagement.inventory.sorting.LexicographicalListComparator;
import me.roundaround.inventorymanagement.inventory.sorting.PredicatedComparator;
import me.roundaround.inventorymanagement.inventory.sorting.SerialComparator;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PotionComparator extends CachingComparatorImpl<ItemStack, PotionComparator.PotionSummary> {
  private static HashMap<RegistryEntry<Potion>, Integer> indices;

  public PotionComparator() {
    super(PredicatedComparator.ignoreNullsNaturalOrder());
  }

  @Override
  protected PotionSummary mapValue(ItemStack stack) {
    PotionContentsComponent component = stack.get(DataComponentTypes.POTION_CONTENTS);
    if (component == null) {
      return null;
    }
    return PotionSummary.of(component);
  }

  private static HashMap<RegistryEntry<Potion>, Integer> getPotionIndices() {
    if (indices != null) {
      return indices;
    }

    indices = new HashMap<>();
    AtomicInteger index = new AtomicInteger(0);
    BuiltinRegistries.createWrapperLookup()
        .getWrapperOrThrow(RegistryKeys.POTION)
        .streamEntries()
        .forEachOrdered((entry) -> indices.put(entry, index.getAndIncrement()));
    return indices;
  }

  protected record PotionSummary(Integer index, List<StatusEffectInstance> customEffects,
                                 Integer customColor) implements Comparable<PotionSummary> {
    private static Comparator<PotionSummary> comparator;

    public static PotionSummary of(PotionContentsComponent component) {
      Integer index = getPotionIndices().get(component.potion().orElse(null));
      List<StatusEffectInstance> customEffects = List.copyOf(component.customEffects());
      Integer customColor = component.customColor().orElse(null);

      return new PotionSummary(index, customEffects, customColor);
    }

    @Override
    public int compareTo(@NotNull PotionSummary other) {
      return getComparator().compare(this, other);
    }

    private static Comparator<PotionSummary> getComparator() {
      if (comparator == null) {
        //@formatter:off
        comparator = SerialComparator.comparing(
            Comparator.nullsLast(Comparator.comparingInt(PotionSummary::index)),
            Comparator.comparing((summary) -> summary.customEffects().size(), Comparator.reverseOrder()),
            Comparator.comparing(PotionSummary::customEffects, LexicographicalListComparator.naturalOrder()),
            Comparator.nullsLast(Comparator.comparingInt(PotionSummary::customColor))
        );
        //@formatter:on
      }
      return comparator;
    }
  }
}
