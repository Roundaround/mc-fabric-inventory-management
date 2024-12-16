package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import com.google.common.collect.Lists;
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
import net.minecraft.util.DyeColor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static me.roundaround.inventorymanagement.testing.AssertIterableMatches.assertIterableMatches;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class BannerComparatorTest {
  @Test
  void ignoresActualItem() {
    ArrayList<ItemStack> actual = Lists.newArrayList(new ItemStack(Items.NETHERITE_CHESTPLATE),
        new ItemStack(Items.RED_BANNER), new ItemStack(Items.DIAMOND_CHESTPLATE), new ItemStack(Items.FIRE_CHARGE),
        new ItemStack(Items.BLUE_BANNER), new ItemStack(Items.BAMBOO)
    );
    List<ItemStack> expected = List.copyOf(actual);
    actual.sort(new BannerComparator());

    assertIterableEquals(expected, actual);
  }

  @Test
  void sortsNumberOfLayersAsc() {
    // Originally 1, 3, 0, 2
    ArrayList<ItemStack> actual = Lists.newArrayList(createStack(createLayer()),
        createStack(createLayer(), createLayer(), createLayer()), createStack(),
        createStack(createLayer(), createLayer())
    );
    actual.sort(new BannerComparator());

    assertIterableMatches(List.of(0, 1, 2, 3), actual, Function.identity(), (stack) -> {
      BannerPatternsComponent component = stack.get(DataComponentTypes.BANNER_PATTERNS);
      if (component == null) {
        return 0;
      }
      return component.layers().size();
    });
  }

  private static RegistryEntry<BannerPattern> getPattern(RegistryKey<BannerPattern> key) {
    return BuiltinRegistries.createWrapperLookup().getWrapperOrThrow(RegistryKeys.BANNER_PATTERN).getOrThrow(key);
  }

  private static BannerPatternsComponent.Layer createLayer() {
    return new BannerPatternsComponent.Layer(getPattern(BannerPatterns.BASE), DyeColor.WHITE);
  }

  private static ItemStack createStack(BannerPatternsComponent.Layer... layers) {
    return createStack(Items.WHITE_BANNER, layers);
  }

  private static ItemStack createStack(Item baseBanner, BannerPatternsComponent.Layer... layers) {
    return createStack(baseBanner, List.of(layers));
  }

  private static ItemStack createStack(List<BannerPatternsComponent.Layer> layers) {
    return createStack(Items.WHITE_BANNER, layers);
  }

  private static ItemStack createStack(Item baseBanner, List<BannerPatternsComponent.Layer> layers) {
    ItemStack stack = new ItemStack(baseBanner);

    if (layers.isEmpty()) {
      return stack;
    }

    BannerPatternsComponent.Builder builder = new BannerPatternsComponent.Builder();
    layers.forEach(builder::add);
    stack.set(DataComponentTypes.BANNER_PATTERNS, builder.build());

    return stack;
  }
}
