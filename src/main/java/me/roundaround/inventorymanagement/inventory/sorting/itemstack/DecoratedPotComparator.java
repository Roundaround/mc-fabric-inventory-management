package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.CachingComparatorImpl;
import me.roundaround.inventorymanagement.inventory.sorting.LexicographicalListComparator;
import me.roundaround.inventorymanagement.inventory.sorting.PredicatedComparator;
import net.minecraft.block.entity.Sherds;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Language;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class DecoratedPotComparator extends CachingComparatorImpl<ItemStack, List<String>> {
  public DecoratedPotComparator(UUID player) {
    //@formatter:off
    super(PredicatedComparator.ignoreNulls(
        LexicographicalListComparator.comparing(
            Language.getInstance()::get,
//            ServerI18nTracker.getInstance(player).snapshot()::get,
            Comparator.nullsLast(String::compareToIgnoreCase)
        )
    ));
    //@formatter:on
  }

  @Override
  protected List<String> mapValue(ItemStack stack) {
    Sherds component = stack.get(DataComponentTypes.POT_DECORATIONS);
    if (component == null) {
      return null;
    }
    return component.stream().stream().map((item) -> item == Items.BRICK ? null : item.getTranslationKey()).toList();
  }
}
