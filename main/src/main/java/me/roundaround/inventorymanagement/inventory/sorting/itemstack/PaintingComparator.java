package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.ConditionalComparator;
import me.roundaround.inventorymanagement.inventory.sorting.SerialComparator;
import me.roundaround.inventorymanagement.mixin.ItemGroupsAccessor;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.decoration.painting.PaintingVariants;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Comparator;
import java.util.Objects;

public class PaintingComparator implements Comparator<ItemStack> {
  private static Comparator<ItemStack> base;

  public PaintingComparator() {
    if (base == null) {
      base = Comparator.comparing(PaintingComparator::getVariant, getVariantComparator());
    }
  }

  @Override
  public int compare(ItemStack o1, ItemStack o2) {
    return base.compare(o1, o2);
  }

  private static RegistryEntry<PaintingVariant> getVariant(ItemStack stack) {
    NbtComponent nbtComponent = stack.get(DataComponentTypes.ENTITY_DATA);
    if (nbtComponent == null || nbtComponent.isEmpty()) {
      return null;
    }

    return nbtComponent.get(PaintingEntity.VARIANT_MAP_CODEC)
        .result()
        .orElseGet(() -> Registries.PAINTING_VARIANT.entryOf(PaintingVariants.KEBAB));
  }

  private static Comparator<RegistryEntry<PaintingVariant>> getVariantComparator() {
    return ConditionalComparator.of(Objects::nonNull,
        SerialComparator.comparing(ItemGroupsAccessor.getPaintingVariantComparator(),
            Comparator.comparing(RegistryEntry::getIdAsString)
        )
    );
  }
}
