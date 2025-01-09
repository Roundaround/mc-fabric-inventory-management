package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import com.google.common.collect.ImmutableSet;
import me.roundaround.inventorymanagement.inventory.sorting.*;
import me.roundaround.inventorymanagement.server.network.ServerI18nTracker;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class ItemNameComparator extends CachingComparatorImpl<ItemStack, List<String>> {
  //@formatter:off
  // TODO: Move into some kind of custom registry
  // TODO: Create tags for the ones with items listed out explicitly (i.e. stained glass)
  // TODO: Find a way to implement proper i18n for the ones that have no root item (i.e. wool)
  private static final List<Group> groups = List.of(
      Group.byBlockTag(Items.SHULKER_BOX, BlockTags.SHULKER_BOXES),
      Group.byItems(Items.GLASS,
          Items.WHITE_STAINED_GLASS,
          Items.ORANGE_STAINED_GLASS,
          Items.MAGENTA_STAINED_GLASS,
          Items.LIGHT_BLUE_STAINED_GLASS,
          Items.YELLOW_STAINED_GLASS,
          Items.LIME_STAINED_GLASS,
          Items.PINK_STAINED_GLASS,
          Items.GRAY_STAINED_GLASS,
          Items.LIGHT_GRAY_STAINED_GLASS,
          Items.CYAN_STAINED_GLASS,
          Items.PURPLE_STAINED_GLASS,
          Items.BLUE_STAINED_GLASS,
          Items.BROWN_STAINED_GLASS,
          Items.GREEN_STAINED_GLASS,
          Items.RED_STAINED_GLASS,
          Items.BLACK_STAINED_GLASS),
      Group.byItems(Items.GLASS,
          Items.WHITE_STAINED_GLASS_PANE,
          Items.ORANGE_STAINED_GLASS_PANE,
          Items.MAGENTA_STAINED_GLASS_PANE,
          Items.LIGHT_BLUE_STAINED_GLASS_PANE,
          Items.YELLOW_STAINED_GLASS_PANE,
          Items.LIME_STAINED_GLASS_PANE,
          Items.PINK_STAINED_GLASS_PANE,
          Items.GRAY_STAINED_GLASS_PANE,
          Items.LIGHT_GRAY_STAINED_GLASS_PANE,
          Items.CYAN_STAINED_GLASS_PANE,
          Items.PURPLE_STAINED_GLASS_PANE,
          Items.BLUE_STAINED_GLASS_PANE,
          Items.BROWN_STAINED_GLASS_PANE,
          Items.GREEN_STAINED_GLASS_PANE,
          Items.RED_STAINED_GLASS_PANE,
          Items.BLACK_STAINED_GLASS_PANE),
      Group.byItemTag("Wool", ItemTags.WOOL),
      Group.byItemTag("Carpet", ItemTags.WOOL_CARPETS),
      Group.byItems("Dye",
          Items.WHITE_DYE,
          Items.ORANGE_DYE,
          Items.MAGENTA_DYE,
          Items.LIGHT_BLUE_DYE,
          Items.YELLOW_DYE,
          Items.LIME_DYE,
          Items.PINK_DYE,
          Items.GRAY_DYE,
          Items.LIGHT_GRAY_DYE,
          Items.CYAN_DYE,
          Items.PURPLE_DYE,
          Items.BLUE_DYE,
          Items.BROWN_DYE,
          Items.GREEN_DYE,
          Items.RED_DYE,
          Items.BLACK_DYE),
      Group.byItemTag(Items.CANDLE, ItemTags.CANDLES),
      Group.byItemTag("Bed", ItemTags.BEDS),
      Group.byItemTag("Banner", ItemTags.BANNERS),
      Group.byItemTag(Items.TERRACOTTA, ItemTags.TERRACOTTA),
      Group.byItems("Glazed Terracotta",
          Items.WHITE_GLAZED_TERRACOTTA,
          Items.ORANGE_GLAZED_TERRACOTTA,
          Items.MAGENTA_GLAZED_TERRACOTTA,
          Items.LIGHT_BLUE_GLAZED_TERRACOTTA,
          Items.YELLOW_GLAZED_TERRACOTTA,
          Items.LIME_GLAZED_TERRACOTTA,
          Items.PINK_GLAZED_TERRACOTTA,
          Items.GRAY_GLAZED_TERRACOTTA,
          Items.LIGHT_GRAY_GLAZED_TERRACOTTA,
          Items.CYAN_GLAZED_TERRACOTTA,
          Items.PURPLE_GLAZED_TERRACOTTA,
          Items.BLUE_GLAZED_TERRACOTTA,
          Items.BROWN_GLAZED_TERRACOTTA,
          Items.GREEN_GLAZED_TERRACOTTA,
          Items.RED_GLAZED_TERRACOTTA,
          Items.BLACK_GLAZED_TERRACOTTA),
      Group.byItems("Concrete",
          Items.WHITE_CONCRETE,
          Items.ORANGE_CONCRETE,
          Items.MAGENTA_CONCRETE,
          Items.LIGHT_BLUE_CONCRETE,
          Items.YELLOW_CONCRETE,
          Items.LIME_CONCRETE,
          Items.PINK_CONCRETE,
          Items.GRAY_CONCRETE,
          Items.LIGHT_GRAY_CONCRETE,
          Items.CYAN_CONCRETE,
          Items.PURPLE_CONCRETE,
          Items.BLUE_CONCRETE,
          Items.BROWN_CONCRETE,
          Items.GREEN_CONCRETE,
          Items.RED_CONCRETE,
          Items.BLACK_CONCRETE),
      Group.byBlockTag("Concrete Powder", BlockTags.CONCRETE_POWDER)
  );
  //@formatter:on

  private final PlayerSortParameters parameters;

  public ItemNameComparator(PlayerSortParameters parameters) {
    //@formatter:off
    super(LexicographicalListComparator.comparing(
        SerialComparator.comparing(
            Comparator.comparing(String::isEmpty).reversed(),
            PredicatedComparator.of(
                (name) -> !name.isEmpty(),
                String::compareToIgnoreCase
            )
        )
    ));
    //@formatter:on
    this.parameters = parameters;
  }

  @Override
  protected List<String> mapValue(ItemStack stack) {
    if (!this.parameters.isGroupItems()) {
      return List.of(this.getName(stack));
    }

    for (Group group : groups) {
      if (group.predicate().test(stack)) {
        return group.groupProducer().apply(this.parameters.getPlayer(), stack);
      }
    }

    return List.of(this.getName(stack));
  }

  private String getName(ItemStack stack) {
    return ServerI18nTracker.getInstance(this.parameters.getPlayer()).snapshot().get(stack);
  }

  private static String getName(UUID player, ItemStack stack) {
    if (stack.isEmpty()) {
      return "";
    }
    return ServerI18nTracker.getInstance(player).snapshot().get(stack);
  }

  protected record Group(Predicate<ItemStack> predicate, BiFunction<UUID, ItemStack, List<String>> groupProducer) {
    public static Group byItemTag(Item root, TagKey<Item> tag) {
      return new Group((stack) -> stack.isIn(tag), groupUnderItem(root));
    }

    public static Group byItemTag(String root, TagKey<Item> tag) {
      return new Group((stack) -> stack.isIn(tag), groupUnderName(root));
    }

    public static Group byItems(Item root, Item first, Item... additional) {
      ImmutableSet<Item> items = ImmutableSet.<Item>builder().add(first).addAll(List.of(additional)).build();
      return new Group((stack) -> items.contains(stack.getItem()), groupUnderItem(root));
    }

    public static Group byItems(String root, Item first, Item... additional) {
      ImmutableSet<Item> items = ImmutableSet.<Item>builder().add(first).addAll(List.of(additional)).build();
      return new Group((stack) -> items.contains(stack.getItem()), groupUnderName(root));
    }

    public static Group byBlockTag(Item root, TagKey<Block> tag) {
      return new Group(
          (stack) -> stack.getItem() instanceof BlockItem block && block.getBlock().getDefaultState().isIn(tag),
          groupUnderItem(root)
      );
    }

    public static Group byBlockTag(String root, TagKey<Block> tag) {
      return new Group(
          (stack) -> stack.getItem() instanceof BlockItem block && block.getBlock().getDefaultState().isIn(tag),
          groupUnderName(root)
      );
    }

    private static BiFunction<UUID, ItemStack, List<String>> groupUnderItem(Item item) {
      return (uuid, stack) -> List.of(
          getName(uuid, stack.copyComponentsToNewStack(item, stack.getCount())),
          getName(uuid, stack.isOf(item) ? ItemStack.EMPTY : stack)
      );
    }

    private static BiFunction<UUID, ItemStack, List<String>> groupUnderName(String root) {
      return (uuid, stack) -> List.of(root, getName(uuid, stack));
    }
  }
}
