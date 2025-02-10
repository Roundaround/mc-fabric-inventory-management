package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import com.google.common.collect.Lists;
import me.roundaround.inventorymanagement.testing.BaseMinecraftTest;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BannerPatterns;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static me.roundaround.inventorymanagement.testing.DataGen.createListOfEmpty;
import static me.roundaround.inventorymanagement.testing.DataGen.getUniquePairs;
import static me.roundaround.inventorymanagement.testing.IterableMatchHelpers.assertPreservesOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BannerComparatorTest extends BaseMinecraftTest {
  private static RegistryWrapper<BannerPattern> registry;
  private static BannerComparator comparator;

  @BeforeAll
  static void beforeAll() {
    registry = BuiltinRegistries.createWrapperLookup().getWrapperOrThrow(RegistryKeys.BANNER_PATTERN);
    comparator = new BannerComparator();
  }

  @ParameterizedTest
  @MethodSource("getEmptySamples")
  void ignoresItemsWithoutComponent(ItemStack a, ItemStack b) {
    assertEquals(0, comparator.compare(a, b));
  }

  private static Stream<Arguments> getEmptySamples() {
    return getUniquePairs(createListOfEmpty(
        DataComponentTypes.BANNER_PATTERNS,
        Items.NETHERITE_CHESTPLATE,
        Items.RED_BANNER,
        Items.DIAMOND_CHESTPLATE,
        Items.FIRE_CHARGE,
        Items.WHITE_BANNER,
        Items.BLUE_BANNER,
        Items.BAMBOO
    ));
  }

  @ParameterizedTest
  @MethodSource("getBannerSamples")
  void ignoresActualItem(ItemStack a, ItemStack b) {
    assertEquals(0, comparator.compare(a, b));
  }

  private static Stream<Arguments> getBannerSamples() {
    return getUniquePairs(List.of(
        createStack(Items.WHITE_BANNER, createLayer()),
        createStack(Items.BLUE_BANNER, createLayer()),
        createStack(Items.ORANGE_BANNER, createLayer()),
        createStack(Items.GREEN_BANNER, createLayer())
    ));
  }

  @Test
  void sortsByNumberOfLayersAsc() {
    //@formatter:off
    assertPreservesOrder(comparator, Lists.newArrayList(
        createStack(),
        createStack(createLayer()),
        createStack(createLayer(), createLayer()),
        createStack(createLayer(), createLayer(), createLayer())
    ));
    //@formatter:on
  }

  @Test
  void sortsByTranslatedLayerNamesLexicographically() {
    //@formatter:off
    assertPreservesOrder(comparator, Lists.newArrayList(
        createStack(
            createLayer(BannerPatterns.BASE, DyeColor.GREEN),       // Fully Green Field
            createLayer(BannerPatterns.STRIPE_TOP, DyeColor.BLACK), // Black Chief
            createLayer(BannerPatterns.BRICKS, DyeColor.BLUE)),     // Blue Field Masoned
        createStack(
            createLayer(BannerPatterns.BASE, DyeColor.WHITE),       // Fully White Field
            createLayer(BannerPatterns.STRIPE_TOP, DyeColor.BLACK), // Black Chief
            createLayer(BannerPatterns.BRICKS, DyeColor.BLUE)),     // Blue Field Masoned
        createStack(
            createLayer(BannerPatterns.BASE, DyeColor.WHITE),       // Fully White Field
            createLayer(BannerPatterns.STRIPE_TOP, DyeColor.BLACK), // Black Chief
            createLayer(BannerPatterns.CIRCLE, DyeColor.BLUE))      // Blue Roundel
    ));
    //@formatter:on
  }

  private static RegistryEntry<BannerPattern> getPattern(RegistryKey<BannerPattern> key) {
    return registry.getOrThrow(key);
  }

  private static BannerPatternsComponent.Layer createLayer() {
    return createLayer(BannerPatterns.BASE, DyeColor.WHITE);
  }

  private static BannerPatternsComponent.Layer createLayer(RegistryKey<BannerPattern> key, DyeColor color) {
    return new BannerPatternsComponent.Layer(getPattern(key), color);
  }

  private static ItemStack createStack(BannerPatternsComponent.Layer... layers) {
    return createStack(Items.WHITE_BANNER, layers);
  }

  private static ItemStack createStack(Item baseBanner, BannerPatternsComponent.Layer... layers) {
    ItemStack stack = new ItemStack(baseBanner);

    if (layers.length == 0) {
      return stack;
    }

    BannerPatternsComponent.Builder builder = new BannerPatternsComponent.Builder();
    for (BannerPatternsComponent.Layer layer : layers) {
      builder.add(layer);
    }
    stack.set(DataComponentTypes.BANNER_PATTERNS, builder.build());

    return stack;
  }
}
