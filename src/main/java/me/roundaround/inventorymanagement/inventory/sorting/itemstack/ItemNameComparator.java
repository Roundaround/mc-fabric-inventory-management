package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.api.sorting.ItemVariantRegistry;
import me.roundaround.inventorymanagement.api.sorting.VariantGroup;
import me.roundaround.inventorymanagement.inventory.sorting.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Language;

import java.util.Comparator;
import java.util.List;

public class ItemNameComparator extends CachingComparatorImpl<ItemStack, List<String>> {
  private final SortContext parameters;

  public ItemNameComparator(SortContext parameters) {
    //@formatter:off
    super(LexicographicalListComparator.comparing(
        SerialComparator.comparing(
            Comparator.comparing(String::isEmpty).reversed(),
            PredicatedComparator.of(
                (name) -> !name.isEmpty(),
                String::compareToIgnoreCase
            )
        )
    ));
    //@formatter:on
    this.parameters = parameters;
  }

  @Override
  protected List<String> mapValue(ItemStack stack) {
    return this.mapToTranslationKeys(stack).stream().map(Language.getInstance()::get).toList();
  }

  private List<String> mapToTranslationKeys(ItemStack stack) {
    if (!this.parameters.itemGrouping()) {
      return List.of(getTranslationKey(stack));
    }

    for (VariantGroup group : ItemVariantRegistry.COLOR.list()) {
      if (group.predicate().test(stack)) {
        return group.groupProducer().apply(this.parameters, stack);
      }
    }

    return List.of(getTranslationKey(stack));
  }

  private static String getTranslationKey(ItemStack stack) {
    if (stack.isEmpty()) {
      return "";
    }
    return stack.getTranslationKey();
  }
}
