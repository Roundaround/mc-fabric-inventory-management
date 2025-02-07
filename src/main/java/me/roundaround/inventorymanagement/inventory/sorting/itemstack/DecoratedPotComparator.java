package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.CachingComparatorImpl;
import me.roundaround.inventorymanagement.inventory.sorting.LexicographicalListComparator;
import me.roundaround.inventorymanagement.inventory.sorting.SerialComparator;
import net.minecraft.block.entity.Sherds;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Language;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DecoratedPotComparator extends CachingComparatorImpl<ItemStack,
    DecoratedPotComparator.DecoratedPotSummary> {
  public DecoratedPotComparator() {
    super(Comparator.naturalOrder());
  }

  @Override
  protected DecoratedPotSummary mapValue(ItemStack stack) {
    return DecoratedPotSummary.of(stack);
  }

  protected record DecoratedPotSummary(int count, List<String> translated) implements Comparable<DecoratedPotSummary> {
    private static Comparator<DecoratedPotSummary> comparator;

    public static DecoratedPotSummary of(ItemStack stack) {
      Sherds component = stack.get(DataComponentTypes.POT_DECORATIONS);
      if (component == null) {
        return new DecoratedPotSummary(0, List.of());
      }

      List<Item> items = component.stream();

      int count = 0;
      ArrayList<String> translated = new ArrayList<>();

      Language language = Language.getInstance();
      for (Item item : items) {
        if (item != Items.BRICK) {
          count++;
          translated.add(language.get(item.getTranslationKey()));
        } else {
          translated.add(null);
        }
      }

      return new DecoratedPotSummary(count, translated);
    }

    @Override
    public int compareTo(@NotNull DecoratedPotSummary other) {
      return getComparator().compare(this, other);
    }

    private static Comparator<DecoratedPotSummary> getComparator() {
      if (comparator == null) {
        //@formatter:off
        comparator = SerialComparator.comparing(
            Comparator.comparingInt(DecoratedPotSummary::count),
            Comparator.comparing(
                DecoratedPotSummary::translated,
                LexicographicalListComparator.comparing(Comparator.nullsLast(String::compareToIgnoreCase))
            ));
        //@formatter:on
      }
      return comparator;
    }
  }
}
