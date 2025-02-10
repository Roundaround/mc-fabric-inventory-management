package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.CachingComparatorImpl;
import me.roundaround.inventorymanagement.inventory.sorting.PredicatedComparator;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Language;
import net.minecraft.util.Util;

import java.util.Optional;

public class InstrumentComparator extends CachingComparatorImpl<ItemStack, String> {
  public InstrumentComparator() {
    super(PredicatedComparator.ignoreNullsNaturalOrder());
  }

  @Override
  protected String mapValue(ItemStack stack) {
    return Optional.ofNullable(stack.get(DataComponentTypes.INSTRUMENT))
        .flatMap(RegistryEntry::getKey)
        .map((key) -> Language.getInstance().get(Util.createTranslationKey("instrument", key.getValue())))
        .orElse(null);
  }
}
