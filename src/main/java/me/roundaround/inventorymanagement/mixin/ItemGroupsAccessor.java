package me.roundaround.inventorymanagement.mixin;

import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.entry.RegistryEntry;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Comparator;

@Mixin(ItemGroups.class)
public interface ItemGroupsAccessor {
  @Accessor("PAINTING_VARIANT_COMPARATOR")
  static Comparator<RegistryEntry<PaintingVariant>> getPaintingVariantComparator() {
    throw new NotImplementedException();
  }
}
