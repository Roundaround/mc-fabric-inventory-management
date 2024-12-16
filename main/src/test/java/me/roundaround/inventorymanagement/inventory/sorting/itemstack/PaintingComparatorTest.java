package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import com.google.common.collect.Lists;
import me.roundaround.inventorymanagement.testing.BaseMinecraftTest;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.decoration.painting.PaintingVariants;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import static me.roundaround.inventorymanagement.testing.AssertIterableMatches.assertIterableMatches;

public class PaintingComparatorTest extends BaseMinecraftTest {
  @Test
  void sortsBySizeThenId() {
    ArrayList<ItemStack> expected = getSortedPaintingList();

    ArrayList<ItemStack> actual = getRandomizedPaintingList();
    actual.sort(new PaintingComparator());

    assertIterableMatches(
        expected, actual, ItemStack::areItemsAndComponentsEqual, PaintingComparatorTest::getStackVariantId);
  }

  private static ArrayList<ItemStack> getSortedPaintingList() {
    return Lists.newArrayList(PaintingVariants.ALBAN, PaintingVariants.AZTEC, PaintingVariants.AZTEC2,
            PaintingVariants.BOMB, PaintingVariants.KEBAB, PaintingVariants.PLANT, PaintingVariants.WASTELAND,
            PaintingVariants.GRAHAM, PaintingVariants.WANDERER, PaintingVariants.COURBET, PaintingVariants.CREEBET,
            PaintingVariants.POOL, PaintingVariants.SEA, PaintingVariants.SUNSET, PaintingVariants.BUST,
            PaintingVariants.EARTH, PaintingVariants.FIRE, PaintingVariants.MATCH, PaintingVariants.SKULL_AND_ROSES,
            PaintingVariants.STAGE, PaintingVariants.VOID, PaintingVariants.WATER, PaintingVariants.WIND,
            PaintingVariants.WITHER, PaintingVariants.FIGHTERS, PaintingVariants.DONKEY_KONG, PaintingVariants.SKELETON,
            PaintingVariants.BURNING_SKULL, PaintingVariants.PIGSCENE, PaintingVariants.POINTER
        )
        .stream()
        .map(Registries.PAINTING_VARIANT::getEntry)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(PaintingComparatorTest::createStack)
        .collect(Collectors.toCollection(ArrayList::new));
  }

  @SuppressWarnings("ComparatorMethodParameterNotUsed")
  private static ArrayList<ItemStack> getRandomizedPaintingList() {
    ArrayList<ItemStack> stacks = new ArrayList<>();
    Registries.PAINTING_VARIANT.streamEntries()
        .sorted((o1, o2) -> Math.random() >= 0.5D ? 1 : -1)
        .forEach((variant) -> stacks.add(createStack(variant)));
    return stacks;
  }

  private static ItemStack createStack(RegistryEntry<PaintingVariant> variant) {
    NbtComponent nbtComponent = NbtComponent.DEFAULT.with(PaintingEntity.VARIANT_MAP_CODEC, variant)
        .getOrThrow()
        .apply(nbt -> nbt.putString("id", "minecraft:painting"));
    ItemStack stack = new ItemStack(Items.PAINTING);
    stack.set(DataComponentTypes.ENTITY_DATA, nbtComponent);
    return stack;
  }

  private static String getStackVariantId(ItemStack stack) {
    return stack.getOrDefault(DataComponentTypes.ENTITY_DATA, NbtComponent.DEFAULT)
        .get(PaintingEntity.VARIANT_MAP_CODEC)
        .mapOrElse(RegistryEntry::getIdAsString, (error) -> null);
  }
}
