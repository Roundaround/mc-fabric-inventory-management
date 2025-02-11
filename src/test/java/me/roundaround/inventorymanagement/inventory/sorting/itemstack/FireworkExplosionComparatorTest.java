package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.roundaround.inventorymanagement.testing.BaseMinecraftTest;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.roundaround.inventorymanagement.testing.DataGen.createListOfEmpty;
import static me.roundaround.inventorymanagement.testing.DataGen.getUniquePairs;
import static me.roundaround.inventorymanagement.testing.IterableMatchHelpers.assertPreservesOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FireworkExplosionComparatorTest extends BaseMinecraftTest {
  private static FireworkExplosionComparator comparator;

  @BeforeAll
  static void beforeAll() {
    comparator = new FireworkExplosionComparator();
  }

  @ParameterizedTest
  @MethodSource("getEmptySamples")
  void ignoresItemsWithoutComponent(ItemStack a, ItemStack b) {
    assertEquals(0, comparator.compare(a, b));
  }

  private static Stream<Arguments> getEmptySamples() {
    return getUniquePairs(createListOfEmpty(DataComponentTypes.FIREWORK_EXPLOSION,
        Items.NETHERITE_CHESTPLATE,
        Items.RED_BANNER,
        Items.DIAMOND_CHESTPLATE,
        Items.FIRE_CHARGE,
        Items.FIREWORK_ROCKET,
        Items.FIREWORK_STAR,
        Items.BLUE_BANNER,
        Items.BAMBOO
    ));
  }

  @Test
  void sortsByType() {
    // Type order based on enum value IDs
    //@formatter:off
    assertPreservesOrder(comparator, Lists.newArrayList(
        createStackWithType(FireworkExplosionComponent.Type.SMALL_BALL),
        createStackWithType(FireworkExplosionComponent.Type.LARGE_BALL),
        createStackWithType(FireworkExplosionComponent.Type.STAR),
        createStackWithType(FireworkExplosionComponent.Type.CREEPER),
        createStackWithType(FireworkExplosionComponent.Type.BURST)
    ));
    //@formatter:on
  }

  @Test
  void sortsByEffects() {
    // No effects = 0, trail = 1, twinkle = 2, both = 3
    //@formatter:off
    assertPreservesOrder(comparator, Lists.newArrayList(
        createStackWithEffects(false, false),
        createStackWithEffects(true, false),
        createStackWithEffects(false, true),
        createStackWithEffects(true, true)
    ));
    //@formatter:on
  }

  @Test
  void presortsColorListForDeterminism() {
    ItemStack a = createStackWithColors(DyeColor.GREEN, DyeColor.LIME, DyeColor.MAGENTA);
    ItemStack b = createStackWithColors(DyeColor.MAGENTA, DyeColor.LIME, DyeColor.GREEN);
    assertEquals(0, comparator.compare(a, b));
  }

  @Test
  void sortsByColors() {
    //@formatter:off
    assertPreservesOrder(comparator, Lists.newArrayList(
        createStackWithColors(DyeColor.CYAN),
        createStackWithColors(DyeColor.CYAN, DyeColor.GREEN),
        createStackWithColors(DyeColor.CYAN, DyeColor.LIME),
        createStackWithColors(DyeColor.CYAN, DyeColor.LIME, DyeColor.MAGENTA),
        createStackWithColors(DyeColor.CYAN, DyeColor.RED)
    ));
    //@formatter:on
  }

  @Test
  void handlesNonStandardColors() {
    //@formatter:off
    assertPreservesOrder(comparator, Lists.newArrayList(
        createStackWithColors(123, DyeColor.GREEN.getFireworkColor()),
        createStackWithColors(456, DyeColor.GREEN.getFireworkColor()),
        createStackWithColors(456, DyeColor.GREEN.getFireworkColor(), DyeColor.LIME.getFireworkColor())
    ));
    //@formatter:on
  }

  @Test
  void sortsByFades() {
    //@formatter:off
    assertPreservesOrder(comparator, Lists.newArrayList(
        createStackWithFades(DyeColor.CYAN),
        createStackWithFades(DyeColor.CYAN, DyeColor.GREEN),
        createStackWithFades(DyeColor.CYAN, DyeColor.LIME),
        createStackWithFades(DyeColor.CYAN, DyeColor.LIME, DyeColor.MAGENTA),
        createStackWithFades(DyeColor.CYAN, DyeColor.RED)
    ));
    //@formatter:on
  }

  private static ItemStack createStackWithType(FireworkExplosionComponent.Type type) {
    return createStack(type, List.of(DyeColor.WHITE), List.of(), false, false);
  }

  private static ItemStack createStackWithEffects(boolean hasTrail, boolean hasTwinkle) {
    return createStack(FireworkExplosionComponent.Type.SMALL_BALL,
        List.of(DyeColor.WHITE),
        List.of(),
        hasTrail,
        hasTwinkle
    );
  }

  private static ItemStack createStackWithColors(DyeColor... dyeColors) {
    return createStack(FireworkExplosionComponent.Type.SMALL_BALL, List.of(dyeColors), List.of(), false, false);
  }

  private static ItemStack createStackWithColors(Integer... fireworkColors) {
    return createStack(FireworkExplosionComponent.Type.SMALL_BALL, List.of(fireworkColors), List.of(), false, false);
  }

  private static ItemStack createStackWithFades(DyeColor... fades) {
    return createStack(FireworkExplosionComponent.Type.SMALL_BALL,
        List.of(DyeColor.WHITE),
        List.of(fades),
        false,
        false
    );
  }

  private static ItemStack createStack(
      FireworkExplosionComponent.Type type,
      List<Object> colors,
      List<Object> fades,
      boolean hasTrail,
      boolean hasTwinkle
  ) {
    ItemStack stack = new ItemStack(Items.FIREWORK_STAR);
    stack.set(DataComponentTypes.FIREWORK_EXPLOSION,
        new FireworkExplosionComponent(type, toIntList(colors), toIntList(fades), hasTrail, hasTwinkle)
    );
    return stack;
  }

  private static IntList toIntList(List<Object> colors) {
    return colors.stream().map((color) -> {
      if (color instanceof DyeColor dyeColor) {
        return dyeColor.getFireworkColor();
      }
      return Integer.valueOf(color.toString());
    }).collect(Collectors.toCollection(IntArrayList::new));
  }
}
