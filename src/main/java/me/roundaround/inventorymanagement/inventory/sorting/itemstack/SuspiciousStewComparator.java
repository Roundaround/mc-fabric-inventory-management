package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.CachingComparatorImpl;
import me.roundaround.inventorymanagement.inventory.sorting.LexicographicalListComparator;
import me.roundaround.inventorymanagement.inventory.sorting.PredicatedComparator;
import me.roundaround.inventorymanagement.inventory.sorting.SerialComparator;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Language;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

public class SuspiciousStewComparator extends CachingComparatorImpl<ItemStack,
    List<SuspiciousStewComparator.EffectSummary>> {
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

  protected record EffectSummary(String translated, int duration) implements Comparable<EffectSummary> {
    private static Comparator<EffectSummary> comparator;

    public static EffectSummary of(SuspiciousStewEffectsComponent.StewEffect effect) {
      return new EffectSummary(Language.getInstance().get(effect.createStatusEffectInstance().getTranslationKey()),
          effect.duration()
      );
    }

    @Override
    public int compareTo(@NotNull EffectSummary other) {
      return getComparator().compare(this, other);
    }

    private static Comparator<EffectSummary> getComparator() {
      if (comparator == null) {
        //@formatter:off
        comparator = SerialComparator.comparing(
            Comparator.comparing(EffectSummary::translated, Comparator.naturalOrder()),
            Comparator.comparingInt(EffectSummary::duration).reversed()
        );
        //@formatter:on
      }
      return comparator;
    }
  }
}
