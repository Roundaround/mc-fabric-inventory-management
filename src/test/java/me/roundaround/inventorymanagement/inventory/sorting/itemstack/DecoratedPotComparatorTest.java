package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import com.google.common.collect.Lists;
import me.roundaround.inventorymanagement.testing.BaseMinecraftTest;
import net.minecraft.block.entity.Sherds;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static me.roundaround.inventorymanagement.testing.AssertIterableMatches.selectNames;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class DecoratedPotComparatorTest extends BaseMinecraftTest {
  @Test
  void ignoresItemsWithoutComponent() {
    //@formatter:off
    ArrayList<ItemStack> samples = Lists.newArrayList(
        createEmpty(Items.NETHERITE_CHESTPLATE),
        createEmpty(Items.RED_BANNER),
        createEmpty(Items.DIAMOND_CHESTPLATE),
        createEmpty(Items.FIRE_CHARGE),
        createEmpty(Items.DECORATED_POT),
        createEmpty(Items.BLUE_BANNER),
        createEmpty(Items.BAMBOO)
    );
    //@formatter:on

    DecoratedPotComparator comparator = new DecoratedPotComparator();
    for (ItemStack a : samples) {
      for (ItemStack b : samples) {
        assertEquals(0, comparator.compare(a, b));
      }
    }
  }

  @Test
  void treatsEmptyDecorationsComponentAsEqual() {
    ItemStack a = new Builder().name("a").build();
    ItemStack b = new Builder().name("b").build();

    DecoratedPotComparator comparator = new DecoratedPotComparator();
    assertEquals(0, comparator.compare(a, b));
  }

  @Test
  void sortsByCountAsc() {
    //@formatter:off
    ArrayList<ItemStack> actual = Lists.newArrayList(
        new Builder().name("1")
            .back(Items.ARCHER_POTTERY_SHERD)
            .left(Items.BLADE_POTTERY_SHERD)
            .right(Items.FLOW_POTTERY_SHERD)
            .front(Items.SCRAPE_POTTERY_SHERD).build(),
        new Builder().name("2")
            .back(Items.ARCHER_POTTERY_SHERD)
            .left(Items.BLADE_POTTERY_SHERD).build(),
        new Builder().name("3")
            .back(Items.ARCHER_POTTERY_SHERD)
            .left(Items.BLADE_POTTERY_SHERD)
            .right(Items.FLOW_POTTERY_SHERD).build()
    );
    //@formatter:on

    Collections.shuffle(actual);
    actual.sort(new DecoratedPotComparator());

    assertIterableEquals(List.of("2", "3", "1"), selectNames(actual));
  }

  @Test
  void sortsByCountThenAlphabetically() {
    //@formatter:off
    ArrayList<ItemStack> actual = Lists.newArrayList(
        new Builder().name("1")
            .back(Items.ARCHER_POTTERY_SHERD)
            .left(Items.BLADE_POTTERY_SHERD)
            .right(Items.FLOW_POTTERY_SHERD)
            .front(Items.SCRAPE_POTTERY_SHERD).build(),
        new Builder().name("2")
            .back(Items.ARCHER_POTTERY_SHERD)
            .left(Items.BLADE_POTTERY_SHERD).build(),
        new Builder().name("3")
            .back(Items.ARCHER_POTTERY_SHERD)
            .left(Items.BLADE_POTTERY_SHERD)
            .right(Items.FRIEND_POTTERY_SHERD).build(),
        new Builder().name("4")
            .back(Items.ARCHER_POTTERY_SHERD)
            .left(Items.BLADE_POTTERY_SHERD)
            .right(Items.FLOW_POTTERY_SHERD).build()
    );
    //@formatter:on

    Collections.shuffle(actual);
    actual.sort(new DecoratedPotComparator());

    assertIterableEquals(List.of("2", "4", "3", "1"), selectNames(actual));
  }

  @Test
  void sortsByBackLeftRightFrontAlphabetically() {
    //@formatter:off
    ArrayList<ItemStack> actual = Lists.newArrayList(
        new Builder().name("1")
            .back(Items.ARMS_UP_POTTERY_SHERD)
            .left(Items.BLADE_POTTERY_SHERD)
            .right(Items.FLOW_POTTERY_SHERD)
            .front(Items.SCRAPE_POTTERY_SHERD).build(),
        new Builder().name("2")
            .back(Items.ARCHER_POTTERY_SHERD)
            .left(Items.BLADE_POTTERY_SHERD)
            .right(Items.FRIEND_POTTERY_SHERD)
            .front(Items.SCRAPE_POTTERY_SHERD).build(),
        new Builder().name("3")
            .back(Items.ARCHER_POTTERY_SHERD)
            .left(Items.BLADE_POTTERY_SHERD)
            .right(Items.FLOW_POTTERY_SHERD)
            .front(Items.SCRAPE_POTTERY_SHERD).build(),
        new Builder().name("4")
            .back(Items.ARCHER_POTTERY_SHERD)
            .left(Items.BREWER_POTTERY_SHERD)
            .right(Items.FLOW_POTTERY_SHERD)
            .front(Items.SCRAPE_POTTERY_SHERD).build(),
        new Builder().name("5")
            .back(Items.ARCHER_POTTERY_SHERD)
            .left(Items.BLADE_POTTERY_SHERD)
            .right(Items.FLOW_POTTERY_SHERD)
            .front(Items.SKULL_POTTERY_SHERD).build()
    );
    //@formatter:on

    Collections.shuffle(actual);
    actual.sort(new DecoratedPotComparator());

    assertIterableEquals(List.of("3", "5", "2", "4", "1"), selectNames(actual));
  }

  @Test
  void sortsEmptyLast() {
    //@formatter:off
    ArrayList<ItemStack> actual = Lists.newArrayList(
    new Builder().name("1") // left empty
        .back(Items.ARCHER_POTTERY_SHERD)
        .right(Items.FLOW_POTTERY_SHERD)
        .front(Items.SCRAPE_POTTERY_SHERD).build(),
    new Builder().name("2") // front empty
        .back(Items.ARCHER_POTTERY_SHERD)
        .left(Items.BREWER_POTTERY_SHERD)
        .right(Items.FLOW_POTTERY_SHERD).build(),
    new Builder().name("3") // right empty
        .back(Items.ARCHER_POTTERY_SHERD)
        .left(Items.BREWER_POTTERY_SHERD)
        .front(Items.SCRAPE_POTTERY_SHERD).build()
    );
    //@formatter:on

    Collections.shuffle(actual);
    actual.sort(new DecoratedPotComparator());

    assertIterableEquals(List.of("2", "3", "1"), selectNames(actual));
  }

  private static ItemStack createEmpty(Item item) {
    ItemStack stack = new ItemStack(item);
    stack.remove(DataComponentTypes.POT_DECORATIONS);
    return stack;
  }

  private static class Builder {
    private Item item = Items.DECORATED_POT;
    private String name = null;
    private Optional<Item> back = Optional.empty();
    private Optional<Item> left = Optional.empty();
    private Optional<Item> right = Optional.empty();
    private Optional<Item> front = Optional.empty();

    public Builder item(Item item) {
      this.item = item;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

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
      ItemStack stack = new ItemStack(this.item);

      if (this.name != null && !this.name.isBlank()) {
        stack.set(DataComponentTypes.CUSTOM_NAME, Text.of(this.name));
      }

      Sherds sherds = new Sherds(this.back, this.left, this.right, this.front);
      stack.set(DataComponentTypes.POT_DECORATIONS, sherds);

      return stack;
    }
  }
}
