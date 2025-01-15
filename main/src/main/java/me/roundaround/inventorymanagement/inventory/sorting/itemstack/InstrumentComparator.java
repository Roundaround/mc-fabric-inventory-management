package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.CachingComparatorImpl;
import me.roundaround.inventorymanagement.inventory.sorting.PredicatedComparator;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Instrument;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InstrumentComparator extends CachingComparatorImpl<ItemStack, Integer> {
  private static HashMap<RegistryEntry<Instrument>, Integer> indices;

  public InstrumentComparator() {
    super(PredicatedComparator.ignoreNullsNaturalOrder());
  }

  @Override
  protected Integer mapValue(ItemStack stack) {
    return getInstrumentIndices().get(stack.get(DataComponentTypes.INSTRUMENT));
  }

  private static HashMap<RegistryEntry<Instrument>, Integer> getInstrumentIndices() {
    if (indices != null) {
      return indices;
    }

    indices = new HashMap<>();
    AtomicInteger index = new AtomicInteger(0);
    BuiltinRegistries.createWrapperLookup()
        .getWrapperOrThrow(RegistryKeys.INSTRUMENT)
        .streamEntries()
        .forEachOrdered((entry) -> indices.put(entry, index.getAndIncrement()));
    return indices;
  }
}
