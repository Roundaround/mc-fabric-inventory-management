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
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.roundaround.inventorymanagement.testing.AssertIterableMatches.selectNames;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class BannerComparatorTest extends BaseMinecraftTest {
  @Test
  void ignoresItemsWithoutComponent() {
    //@formatter:off
    ArrayList<ItemStack> samples = Lists.newArrayList(
        createEmpty(Items.NETHERITE_CHESTPLATE),
        createEmpty(Items.RED_BANNER),
        createEmpty(Items.DIAMOND_CHESTPLATE),
        createEmpty(Items.FIRE_CHARGE),
        createEmpty(Items.WHITE_BANNER),
        createEmpty(Items.BLUE_BANNER),
        createEmpty(Items.BAMBOO)
    );
    //@formatter:on

    BannerComparator comparator = new BannerComparator();
    for (ItemStack a : samples) {
      for (ItemStack b : samples) {
        assertEquals(0, comparator.compare(a, b));
      }
    }
  }

  @Test
  void ignoresActualItem() {
    //@formatter:off
    ArrayList<ItemStack> samples = Lists.newArrayList(
        createStack(Items.WHITE_BANNER, createLayer()),
        createStack(Items.BLUE_BANNER, createLayer()),
        createStack(Items.ORANGE_BANNER, createLayer()),
        createStack(Items.GREEN_BANNER, createLayer())
    );
    //@formatter:on

    BannerComparator comparator = new BannerComparator();
    for (ItemStack a : samples) {
      for (ItemStack b : samples) {
        assertEquals(0, comparator.compare(a, b));
      }
    }
  }

  @Test
  void sortsNumberOfLayersAsc() {
    //@formatter:off
    ArrayList<ItemStack> actual = Lists.newArrayList(
        createStack("1", createLayer()),
        createStack("2", createLayer(), createLayer(), createLayer()),
        createStack("3"),
        createStack("4", createLayer(), createLayer())
    );
    //@formatter:on

    Collections.shuffle(actual);
    actual.sort(new BannerComparator());

    assertIterableEquals(List.of("3", "1", "4", "2"), selectNames(actual));
  }

  @Test
  void sortsPatternNameWhenLayerCountMatches() {
    //@formatter:off
    ArrayList<ItemStack> actual = Lists.newArrayList(
        createStack("1",
            createLayer(BannerPatterns.BASE),
            createLayer(BannerPatterns.STRIPE_TOP),
            createLayer(BannerPatterns.TRIANGLES_TOP)),
        createStack("2",
            createLayer(BannerPatterns.BASE),
            createLayer(BannerPatterns.BASE),
            createLayer(BannerPatterns.BASE)),
        createStack("3",
            createLayer(BannerPatterns.BASE),
            createLayer(BannerPatterns.STRIPE_TOP),
            createLayer(BannerPatterns.CIRCLE))
    );
    //@formatter:on

    Collections.shuffle(actual);
    actual.sort(new BannerComparator());

    assertIterableEquals(List.of("2", "3", "1"), selectNames(actual));
  }

  @Test
  void sortsColorNameWhenPatternNameMatches() {
    //@formatter:off
    ArrayList<ItemStack> actual = Lists.newArrayList(
        createStack("1",
            createLayer(BannerPatterns.BASE),
            createLayer(BannerPatterns.STRIPE_TOP, DyeColor.PINK),
            createLayer(BannerPatterns.TRIANGLES_TOP, DyeColor.ORANGE)),
        createStack("2",
            createLayer(BannerPatterns.BASE),
            createLayer(BannerPatterns.STRIPE_TOP, DyeColor.BLUE),
            createLayer(BannerPatterns.TRIANGLES_TOP, DyeColor.BLACK)),
        createStack("3",
            createLayer(BannerPatterns.BASE),
            createLayer(BannerPatterns.STRIPE_TOP, DyeColor.PINK),
            createLayer(BannerPatterns.TRIANGLES_TOP, DyeColor.MAGENTA))
    );
    //@formatter:on

    Collections.shuffle(actual);
    actual.sort(new BannerComparator());

    assertIterableEquals(List.of("2", "3", "1"), selectNames(actual));
  }

  private static ItemStack createEmpty(Item item) {
    ItemStack stack = new ItemStack(item);
    stack.remove(DataComponentTypes.BANNER_PATTERNS);
    return stack;
  }

  private static RegistryEntry<BannerPattern> getPattern(RegistryKey<BannerPattern> key) {
    return BuiltinRegistries.createWrapperLookup().getWrapperOrThrow(RegistryKeys.BANNER_PATTERN).getOrThrow(key);
  }

  private static BannerPatternsComponent.Layer createLayer() {
    return createLayer(BannerPatterns.BASE, DyeColor.WHITE);
  }

  private static BannerPatternsComponent.Layer createLayer(RegistryKey<BannerPattern> key) {
    return createLayer(key, DyeColor.WHITE);
  }

  private static BannerPatternsComponent.Layer createLayer(RegistryKey<BannerPattern> key, DyeColor color) {
    return new BannerPatternsComponent.Layer(getPattern(key), color);
  }

  private static ItemStack createStack(BannerPatternsComponent.Layer... layers) {
    return createStack("", layers);
  }

  private static ItemStack createStack(String customName, BannerPatternsComponent.Layer... layers) {
    return createStack(Items.WHITE_BANNER, customName, layers);
  }

  private static ItemStack createStack(Item baseBanner, BannerPatternsComponent.Layer... layers) {
    return createStack(baseBanner, "", layers);
  }

  private static ItemStack createStack(Item baseBanner, String customName, BannerPatternsComponent.Layer... layers) {
    ItemStack stack = new ItemStack(baseBanner);

    if (customName != null && !customName.isBlank()) {
      stack.set(DataComponentTypes.CUSTOM_NAME, Text.of(customName));
    }

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
