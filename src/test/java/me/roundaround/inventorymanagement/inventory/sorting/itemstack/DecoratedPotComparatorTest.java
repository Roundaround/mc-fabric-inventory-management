package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import com.google.common.collect.Lists;
import me.roundaround.inventorymanagement.testing.BaseMinecraftTest;
import net.minecraft.block.entity.Sherds;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static me.roundaround.inventorymanagement.testing.DataGen.createListOfEmpty;
import static me.roundaround.inventorymanagement.testing.DataGen.getUniquePairs;
import static me.roundaround.inventorymanagement.testing.IterableMatchHelpers.assertPreservesOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecoratedPotComparatorTest extends BaseMinecraftTest {
  private static DecoratedPotComparator comparator;

  @BeforeAll
  static void beforeAll() {
    comparator = new DecoratedPotComparator();
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
        Items.DECORATED_POT,
        Items.BLUE_BANNER,
        Items.BAMBOO
    ));
  }

  @Test
  void treatsEmptyDecorationsComponentAsEqual() {
    ItemStack a = new ItemStack(Items.DECORATED_POT);
    ItemStack b = new ItemStack(Items.DECORATED_POT);

    assertEquals(0, comparator.compare(a, b));
  }

  @Test
  void sortsByCountAsc() {
    //@formatter:off
    assertPreservesOrder(comparator, Lists.newArrayList(
        new Builder()
            .back(Items.ARCHER_POTTERY_SHERD)
            .left(Items.BLADE_POTTERY_SHERD).build(),
        new Builder()
            .back(Items.ARCHER_POTTERY_SHERD)
            .left(Items.BLADE_POTTERY_SHERD)
            .right(Items.FLOW_POTTERY_SHERD).build(),
        new Builder()
            .back(Items.ARCHER_POTTERY_SHERD)
            .left(Items.BLADE_POTTERY_SHERD)
            .right(Items.FLOW_POTTERY_SHERD)
            .front(Items.SCRAPE_POTTERY_SHERD).build()
    ));
    //@formatter:on
  }

  @Test
  void sortsByCountThenAlphabetically() {
    //@formatter:off
    assertPreservesOrder(comparator, Lists.newArrayList(
        new Builder()
            .back(Items.ARCHER_POTTERY_SHERD)
            .left(Items.BLADE_POTTERY_SHERD).build(),
        new Builder()
            .back(Items.ARCHER_POTTERY_SHERD)
            .left(Items.BLADE_POTTERY_SHERD)
            .right(Items.FLOW_POTTERY_SHERD).build(),
        new Builder()
            .back(Items.ARCHER_POTTERY_SHERD)
            .left(Items.BLADE_POTTERY_SHERD)
            .right(Items.FRIEND_POTTERY_SHERD).build(),
        new Builder()
            .back(Items.ARCHER_POTTERY_SHERD)
            .left(Items.BLADE_POTTERY_SHERD)
            .right(Items.FLOW_POTTERY_SHERD)
            .front(Items.SCRAPE_POTTERY_SHERD).build()
    ));
    //@formatter:on
  }

  @Test
  void sortsByBackLeftRightFrontAlphabetically() {
    //@formatter:off
    assertPreservesOrder(comparator, Lists.newArrayList(
        new Builder()
            .back(Items.ARCHER_POTTERY_SHERD)
            .left(Items.BLADE_POTTERY_SHERD)
            .right(Items.FLOW_POTTERY_SHERD)
            .front(Items.SCRAPE_POTTERY_SHERD).build(),
        new Builder()
            .back(Items.ARCHER_POTTERY_SHERD)
            .left(Items.BLADE_POTTERY_SHERD)
            .right(Items.FLOW_POTTERY_SHERD)
            .front(Items.SKULL_POTTERY_SHERD).build(),
        new Builder()
            .back(Items.ARCHER_POTTERY_SHERD)
            .left(Items.BLADE_POTTERY_SHERD)
            .right(Items.FRIEND_POTTERY_SHERD)
            .front(Items.SCRAPE_POTTERY_SHERD).build(),
        new Builder()
            .back(Items.ARCHER_POTTERY_SHERD)
            .left(Items.BREWER_POTTERY_SHERD)
            .right(Items.FLOW_POTTERY_SHERD)
            .front(Items.SCRAPE_POTTERY_SHERD).build(),
        new Builder()
            .back(Items.ARMS_UP_POTTERY_SHERD)
            .left(Items.BLADE_POTTERY_SHERD)
            .right(Items.FLOW_POTTERY_SHERD)
            .front(Items.SCRAPE_POTTERY_SHERD).build()
    ));
    //@formatter:on
  }

  @Test
  void sortsEmptyLast() {
    //@formatter:off
    assertPreservesOrder(comparator, Lists.newArrayList(
        new Builder() // front empty
            .back(Items.ARCHER_POTTERY_SHERD)
            .left(Items.BREWER_POTTERY_SHERD)
            .right(Items.FLOW_POTTERY_SHERD).build(),
        new Builder() // right empty
            .back(Items.ARCHER_POTTERY_SHERD)
            .left(Items.BREWER_POTTERY_SHERD)
            .front(Items.SCRAPE_POTTERY_SHERD).build(),
        new Builder() // left empty
            .back(Items.ARCHER_POTTERY_SHERD)
            .right(Items.FLOW_POTTERY_SHERD)
            .front(Items.SCRAPE_POTTERY_SHERD).build()
    ));
    //@formatter:on
  }

  private static class Builder {
    private Optional<Item> back = Optional.empty();
    private Optional<Item> left = Optional.empty();
    private Optional<Item> right = Optional.empty();
    private Optional<Item> front = Optional.empty();

    public Builder back(Item back) {
      this.back = Optional.of(back);
      return this;
    }

    public Builder left(Item left) {
      this.left = Optional.of(left);
      return this;
    }

    public Builder right(Item right) {
      this.right = Optional.of(right);
      return this;
    }

    public Builder front(Item front) {
      this.front = Optional.of(front);
      return this;
    }

    public ItemStack build() {
      ItemStack stack = new ItemStack(Items.DECORATED_POT);

      Sherds sherds = new Sherds(this.back, this.left, this.right, this.front);
      stack.set(DataComponentTypes.POT_DECORATIONS, sherds);

      return stack;
    }
  }
}
