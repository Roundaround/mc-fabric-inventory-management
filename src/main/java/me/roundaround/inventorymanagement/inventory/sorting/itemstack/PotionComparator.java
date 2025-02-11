package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.CachingComparatorImpl;
import me.roundaround.inventorymanagement.inventory.sorting.LexicographicalListComparator;
import me.roundaround.inventorymanagement.inventory.sorting.PredicatedComparator;
import me.roundaround.inventorymanagement.inventory.sorting.SerialComparator;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

public class PotionComparator extends CachingComparatorImpl<ItemStack, PotionComparator.PotionSummary> {
  public PotionComparator() {
    super(PredicatedComparator.ignoreNullsNaturalOrder());
  }

  @Override
  protected PotionSummary mapValue(ItemStack stack) {
    return PotionSummary.of(stack);
  }

  protected record PotionSummary(String translated,
                                 List<StatusEffectInstance> customEffects,
                                 Integer customColor) implements Comparable<PotionSummary> {
    private static Comparator<PotionSummary> comparator;

    public static PotionSummary of(ItemStack stack) {
      PotionContentsComponent component = stack.get(DataComponentTypes.POTION_CONTENTS);
      if (component == null) {
        return null;
      }

      String translated = Items.POTION.getTranslationKey(stack);
      List<StatusEffectInstance> customEffects = List.copyOf(component.customEffects());
      Integer customColor = component.customColor().orElse(null);

      return new PotionSummary(translated, customEffects, customColor);
    }

    @Override
    public int compareTo(@NotNull PotionSummary other) {
      return getComparator().compare(this, other);
    }

    private static Comparator<PotionSummary> getComparator() {
      if (comparator == null) {
        //@formatter:off
        comparator = SerialComparator.comparing(
            Comparator.comparing(PotionSummary::translated, Comparator.nullsLast(Comparator.naturalOrder())),
            Comparator.comparing((summary) -> summary.customEffects().size(), Comparator.reverseOrder()),
            Comparator.comparing(PotionSummary::customEffects, LexicographicalListComparator.naturalOrder()),
            Comparator.comparing(PotionSummary::customColor, Comparator.nullsLast(Comparator.naturalOrder()))
        );
        //@formatter:on
      }
      return comparator;
    }
  }
}
