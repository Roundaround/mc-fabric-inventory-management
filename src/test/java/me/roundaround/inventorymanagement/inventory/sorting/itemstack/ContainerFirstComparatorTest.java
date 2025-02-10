package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.registry.tag.InventoryManagementItemTags;
import me.roundaround.inventorymanagement.testing.BaseMinecraftTest;
import me.roundaround.inventorymanagement.testing.SimpleTestServer;
import net.fabricmc.fabric.api.tag.convention.v2.TagUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static me.roundaround.inventorymanagement.testing.DataGen.getAllPairs;
import static me.roundaround.inventorymanagement.testing.DataGen.getUniquePairs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContainerFirstComparatorTest extends BaseMinecraftTest {
  private static ContainerFirstComparator comparator;

  @BeforeAll
  static void beforeAll() {
    comparator = new ContainerFirstComparator();
    SimpleTestServer.create();
  }

  @AfterAll
  static void afterAll() {
    SimpleTestServer.cleanup();
  }

  @ParameterizedTest
  @MethodSource("getAllItemPairs")
  void putsContainersFirst(Item container, Item other) {
    assertEquals(-1, comparator.compare(new ItemStack(container), new ItemStack(other)));
  }

  @ParameterizedTest
  @MethodSource("getAllContainerPairs")
  void treatsAllContainersAsEqual(Item a, Item b) {
    assertEquals(0, comparator.compare(new ItemStack(a), new ItemStack(b)));
  }

  @ParameterizedTest
  @MethodSource("getAllContainers")
  void includesAllItemsInTag(Item item) {
    assertTrue(TagUtil.isIn(InventoryManagementItemTags.HAS_INVENTORY, item));
  }

  private static Stream<Arguments> getAllItemPairs() {
    return getAllPairs(getAllContainers(), getSomeNonContainers());
  }

  private static Stream<Arguments> getAllContainerPairs() {
    return getUniquePairs(getAllContainers());
  }

  private static List<Item> getAllContainers() {
    //@formatter:off
    return List.of(
        Items.SHULKER_BOX,
        Items.WHITE_SHULKER_BOX,
        Items.ORANGE_SHULKER_BOX,
        Items.MAGENTA_SHULKER_BOX,
        Items.LIGHT_BLUE_SHULKER_BOX,
        Items.YELLOW_SHULKER_BOX,
        Items.LIME_SHULKER_BOX,
        Items.PINK_SHULKER_BOX,
        Items.GRAY_SHULKER_BOX,
        Items.LIGHT_GRAY_SHULKER_BOX,
        Items.CYAN_SHULKER_BOX,
        Items.PURPLE_SHULKER_BOX,
        Items.BLUE_SHULKER_BOX,
        Items.BROWN_SHULKER_BOX,
        Items.GREEN_SHULKER_BOX,
        Items.RED_SHULKER_BOX,
        Items.BLACK_SHULKER_BOX,
        Items.BUNDLE
    );
    //@formatter:on
  }

  private static List<Item> getSomeNonContainers() {
    //@formatter:off
    return List.of(
        Items.FIRE_CHARGE,
        Items.BONE_MEAL,
        Items.DIAMOND,
        Items.DIAMOND_CHESTPLATE,
        Items.CHEST,
        Items.BARREL,
        Items.ENDER_CHEST
    );
    //@formatter:on
  }
}
