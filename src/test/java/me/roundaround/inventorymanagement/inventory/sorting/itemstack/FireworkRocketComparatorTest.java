package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import me.roundaround.inventorymanagement.testing.BaseMinecraftTest;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static me.roundaround.inventorymanagement.testing.DataGen.createListOfEmpty;
import static me.roundaround.inventorymanagement.testing.DataGen.getUniquePairs;
import static me.roundaround.inventorymanagement.testing.IterableMatchHelpers.assertPreservesOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FireworkRocketComparatorTest extends BaseMinecraftTest {
  private static FireworkRocketComparator comparator;

  @BeforeAll
  static void beforeAll() {
    comparator = new FireworkRocketComparator();
  }

  @ParameterizedTest
  @MethodSource("getEmptySamples")
  void ignoresItemsWithoutComponent(ItemStack a, ItemStack b) {
    assertEquals(0, comparator.compare(a, b));
  }

  private static Stream<Arguments> getEmptySamples() {
    return getUniquePairs(createListOfEmpty(DataComponentTypes.FIREWORKS,
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
  void sortsRocketDurationDesc() {
    //@formatter:off
    assertPreservesOrder(comparator, Lists.newArrayList(
        createStack(3),
        createStack(2),
        createStack(1),
        createStack(0)
    ));
    //@formatter:on
  }

  @Test
  void sortsRocketDurationRegardlessOfExplosions() {
    Random random = new Random();

    //@formatter:off
    assertPreservesOrder(comparator, Lists.newArrayList(
        createStack(3, createRandomExplosionList(random)),
        createStack(2, createRandomExplosionList(random)),
        createStack(1, createRandomExplosionList(random)),
        createStack(0, createRandomExplosionList(random))
    ));
    //@formatter:on
  }

  @Test
  void sortsByExplosionsLexicographically() {
    // Generate and sort a bunch of explosions, then assign them in a way we know is "in order"
    Random random = new Random();
    ArrayList<FireworkExplosionComponent> explosions = createRandomExplosionList(5, random);
    explosions.sort(new FireworkExplosionComparator.ByComponent());

    FireworkExplosionComponent explosion1 = explosions.get(0);
    FireworkExplosionComponent explosion2 = explosions.get(1);
    FireworkExplosionComponent explosion3 = explosions.get(2);
    FireworkExplosionComponent explosion4 = explosions.get(3);
    FireworkExplosionComponent explosion5 = explosions.get(4);

    //@formatter:off
    assertPreservesOrder(comparator, Lists.newArrayList(
        createStack(1, explosion1, explosion2),
        createStack(1, explosion1, explosion3),
        createStack(1, explosion1, explosion3, explosion4),
        createStack(1, explosion1, explosion5)
    ));
    //@formatter:on
  }

  private static ItemStack createStack(int rocketDuration) {
    return createStack(rocketDuration, List.of());
  }

  private static ItemStack createStack(int rocketDuration, FireworkExplosionComponent... explosions) {
    return createStack(rocketDuration, List.of(explosions));
  }

  private static ItemStack createStack(int rocketDuration, List<FireworkExplosionComponent> explosions) {
    ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
    stack.set(DataComponentTypes.FIREWORKS, new FireworksComponent(rocketDuration, List.copyOf(explosions)));
    return stack;
  }

  private static ArrayList<FireworkExplosionComponent> createRandomExplosionList(Random random) {
    return createRandomExplosionList(random.nextInt(256) + 1, random);
  }

  private static ArrayList<FireworkExplosionComponent> createRandomExplosionList(int count, Random random) {
    FireworkExplosionComponent[] explosions = new FireworkExplosionComponent[count];
    for (int i = 0; i < count; i++) {
      explosions[i] = createRandomExplosion(random);
    }
    return Lists.newArrayList(explosions);
  }

  private static FireworkExplosionComponent createRandomExplosion(Random random) {
    FireworkExplosionComponent.Type shape = getRandomExplosionType(random);

    int colorCount = random.nextInt(64) + 1;
    int[] colors = new int[colorCount];
    for (int i = 0; i < colorCount; i++) {
      colors[i] = getRandomFireworkColor(random);
    }

    int fadeCount = random.nextInt(64) + 1;
    int[] fades = new int[fadeCount];
    for (int i = 0; i < fadeCount; i++) {
      fades[i] = getRandomFireworkColor(random);
    }

    boolean hasTrail = random.nextBoolean();
    boolean hasTwinkle = random.nextBoolean();

    return new FireworkExplosionComponent(shape,
        new IntArrayList(colors),
        new IntArrayList(fades),
        hasTrail,
        hasTwinkle
    );
  }

  private static FireworkExplosionComponent.Type getRandomExplosionType(Random random) {
    FireworkExplosionComponent.Type[] types = FireworkExplosionComponent.Type.values();
    return types[random.nextInt(types.length)];
  }

  private static int getRandomFireworkColor(Random random) {
    DyeColor[] colors = DyeColor.values();
    return colors[random.nextInt(colors.length)].getFireworkColor();
  }
}
