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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static me.roundaround.inventorymanagement.testing.DataGen.getUniquePairs;
import static me.roundaround.inventorymanagement.testing.IterableMatchHelpers.assertPreservesOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaintingComparatorTest extends BaseMinecraftTest {
  private static final List<ItemStack> SORTED_PAINTINGS = Stream.of(
          PaintingVariants.ALBAN,
          PaintingVariants.AZTEC,
          PaintingVariants.AZTEC2,
          PaintingVariants.BOMB,
          PaintingVariants.KEBAB,
          PaintingVariants.PLANT,
          PaintingVariants.WASTELAND,
          PaintingVariants.GRAHAM,
          PaintingVariants.WANDERER,
          PaintingVariants.COURBET,
          PaintingVariants.CREEBET,
          PaintingVariants.POOL,
          PaintingVariants.SEA,
          PaintingVariants.SUNSET,
          PaintingVariants.BUST,
          PaintingVariants.EARTH,
          PaintingVariants.FIRE,
          PaintingVariants.MATCH,
          PaintingVariants.SKULL_AND_ROSES,
          PaintingVariants.STAGE,
          PaintingVariants.VOID,
          PaintingVariants.WATER,
          PaintingVariants.WIND,
          PaintingVariants.WITHER,
          PaintingVariants.FIGHTERS,
          PaintingVariants.DONKEY_KONG,
          PaintingVariants.SKELETON,
          PaintingVariants.BURNING_SKULL,
          PaintingVariants.PIGSCENE,
          PaintingVariants.POINTER
      )
      .map(Registries.PAINTING_VARIANT::getEntry)
      .filter(Optional::isPresent)
      .map(Optional::get)
      .map(PaintingComparatorTest::createStack)
      .toList();

  private static PaintingComparator comparator;

  @BeforeAll
  static void beforeAll() {
    comparator = new PaintingComparator();
  }

  @Test
  void sortsBySizeThenId() {
    assertPreservesOrder(comparator, Lists.newArrayList(SORTED_PAINTINGS));
  }

  @ParameterizedTest
  @MethodSource("getMiscSamples")
  void ignoresNonPaintings(ItemStack a, ItemStack b) {
    assertEquals(0, comparator.compare(a, b));
  }

  private static Stream<Arguments> getMiscSamples() {
    return getUniquePairs(List.of(
        new ItemStack(Items.NETHERITE_SWORD),
        createStack(Registries.PAINTING_VARIANT.getEntry(PaintingVariants.KEBAB).orElseThrow()),
        new ItemStack(Items.DIAMOND, 16)
    ));
  }

  private static ItemStack createStack(RegistryEntry<PaintingVariant> variant) {
    NbtComponent nbtComponent = NbtComponent.DEFAULT.with(PaintingEntity.VARIANT_MAP_CODEC, variant)
        .getOrThrow()
        .apply(nbt -> nbt.putString("id", "minecraft:painting"));
    ItemStack stack = new ItemStack(Items.PAINTING);
    stack.set(DataComponentTypes.ENTITY_DATA, nbtComponent);
    return stack;
  }
}
