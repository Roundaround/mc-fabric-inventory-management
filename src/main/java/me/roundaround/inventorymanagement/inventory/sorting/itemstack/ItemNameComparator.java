package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import com.google.common.collect.ImmutableSet;
import me.roundaround.inventorymanagement.inventory.sorting.*;
import me.roundaround.inventorymanagement.registry.tag.InventoryManagementItemTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Language;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ItemNameComparator extends CachingComparatorImpl<ItemStack, List<String>> {
  //@formatter:off
  // TODO: Move into some kind of custom registry
  private static final List<Group> COLOR_GROUPS = List.of(
      Group.by(Items.SHULKER_BOX, ConventionalItemTags.SHULKER_BOXES),
      // TODO: Replace with ConventionalItemTags.GLASS_BLOCKS_CHEAP starting in 1.21
      Group.by(Items.GLASS, GroupPredicates.GLASS_SANS_TINTED),
      Group.by(Items.GLASS_PANE, ConventionalItemTags.GLASS_PANES),
      Group.by(GroupNames.WOOLS, ItemTags.WOOL),
      Group.by(GroupNames.WOOL_CARPETS, ItemTags.WOOL_CARPETS),
      Group.by(GroupNames.DYES, ConventionalItemTags.DYES),
      Group.by(Items.CANDLE, ItemTags.CANDLES),
      Group.by(GroupNames.BEDS, ItemTags.BEDS),
      Group.by(GroupNames.BANNERS, ItemTags.BANNERS),
      Group.by(Items.TERRACOTTA, ItemTags.TERRACOTTA),
      // TODO: Replace with ConventionalItemTags.GLAZED_TERRACOTTAS starting in 1.21
      Group.by(GroupNames.GLAZED_TERRACOTTAS, InventoryManagementItemTags.GLAZED_TERRACOTTAS),
      // TODO: Replace with ConventionalItemTags.CONCRETES starting in 1.21
      Group.by(GroupNames.CONCRETES, InventoryManagementItemTags.CONCRETES),
      // TODO: Replace with ConventionalItemTags.CONCRETE_POWDERS starting in 1.21
      Group.by(GroupNames.CONCRETE_POWDERS, InventoryManagementItemTags.CONCRETE_POWDERS)
  );
  //@formatter:on

  //@formatter:off
  // TODO: Move into some kind of custom registry
  private static final List<Group> MATERIAL_GROUPS = List.of(
      // TODO: E.g. logs, planks, fences, stairs, doors, etc. etc. etc.
  );
  //@formatter:on

  // TODO: Move into some kind of custom registry
  private static final List<Group> GROUPS = Stream.concat(COLOR_GROUPS.stream(), MATERIAL_GROUPS.stream()).toList();

  // TODO: Replace all usages of GroupNames with a new Group static method that pulls i18n key from tag
  private static class GroupNames {
    // TODO: Replace with ItemTags.WOOL.getTranslationKey() starting in 1.21
    public static String WOOLS = "tag.item.inventorymanagement.wools";
    // TODO: Replace with ItemTags.WOOL_CARPETS.getTranslationKey() starting in 1.21
    public static String WOOL_CARPETS = "tag.item.inventorymanagement.wool_carpets";
    // TODO: Replace with ConventionalItemTags.DYES.getTranslationKey() starting in 1.21
    public static String DYES = "tag.item.inventorymanagement.dyes";
    // TODO: Replace with ItemTags.BEDS.getTranslationKey() starting in 1.21
    public static String BEDS = "tag.item.inventorymanagement.beds";
    // TODO: Replace with ItemTags.BANNERS.getTranslationKey() starting in 1.21
    public static String BANNERS = "tag.item.inventorymanagement.banners";
    // TODO: Replace with ConventionalItemTags.GLAZED_TERRACOTTAS.getTranslationKey() starting in 1.21
    public static String GLAZED_TERRACOTTAS = "tag.item.inventorymanagement.glazed_terracottas";
    // TODO: Replace with ConventionalItemTags.CONCRETES.getTranslationKey() starting in 1.21
    public static String CONCRETES = "tag.item.inventorymanagement.concretes";
    // TODO: Replace with ConventionalItemTags.CONCRETE_POWDERS.getTranslationKey() starting in 1.21
    public static String CONCRETE_POWDERS = "tag.item.inventorymanagement.concrete_powders";
  }

  private static class GroupPredicates {
    // TODO: Starting with 1.21, simply use the ConventionalItemTags.GLASS_BLOCKS_CHEAP instead
    public static Predicate<ItemStack> GLASS_SANS_TINTED = (stack) -> stack.isIn(ConventionalItemTags.GLASS_BLOCKS) &&
                                                                      !stack.isOf(Items.TINTED_GLASS);
  }

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
    return this.mapToTranslationKeys(stack).stream().map(Language.getInstance()::get).toList();
  }

  private List<String> mapToTranslationKeys(ItemStack stack) {
    if (!this.parameters.isGroupItems()) {
      return List.of(getTranslationKey(stack));
    }

    // TODO: Further customization options to e.g. let you group by color only

    for (Group group : GROUPS) {
      if (group.predicate().test(stack)) {
        return group.groupProducer().apply(this.parameters.getPlayer(), stack);
      }
    }

    return List.of(getTranslationKey(stack));
  }

  private static String getTranslationKey(ItemStack stack) {
    if (stack.isEmpty()) {
      return "";
    }
    return stack.getTranslationKey();
  }

  protected record Group(Predicate<ItemStack> predicate, BiFunction<UUID, ItemStack, List<String>> groupProducer) {
    public static Group by(Item root, Predicate<ItemStack> predicate) {
      return new Group(predicate, groupUnderItem(root));
    }

    public static Group by(Item root, TagKey<Item> tag) {
      return new Group((stack) -> stack.isIn(tag), groupUnderItem(root));
    }

    public static Group by(String root, TagKey<Item> tag) {
      return new Group((stack) -> stack.isIn(tag), groupUnderName(root));
    }

    public static Group by(Item root, Item first, Item... additional) {
      ImmutableSet<Item> items = ImmutableSet.<Item>builder().add(first).addAll(List.of(additional)).build();
      return new Group((stack) -> items.contains(stack.getItem()), groupUnderItem(root));
    }

    public static Group by(String root, Item first, Item... additional) {
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
          getTranslationKey(stack.copyComponentsToNewStack(item, stack.getCount())),
          getTranslationKey(stack.isOf(item) ? ItemStack.EMPTY : stack)
      );
    }

    private static BiFunction<UUID, ItemStack, List<String>> groupUnderName(String root) {
      return (uuid, stack) -> List.of(root, getTranslationKey(stack));
    }
  }
}
