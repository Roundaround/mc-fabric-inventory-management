package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.*;
import me.roundaround.inventorymanagement.registry.tag.InventoryManagementItemTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
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
      Group.by(ItemTags.WOOL),
      Group.by(ItemTags.WOOL_CARPETS),
      Group.by(ConventionalItemTags.DYES),
      Group.by(ItemTags.CANDLES),
      Group.by(ItemTags.BEDS),
      Group.by(ItemTags.BANNERS),
      Group.by(ItemTags.TERRACOTTA),
      // TODO: Replace with ConventionalItemTags.GLAZED_TERRACOTTAS starting in 1.21
      Group.by(InventoryManagementItemTags.GLAZED_TERRACOTTAS),
      // TODO: Replace with ConventionalItemTags.CONCRETES starting in 1.21
      Group.by(InventoryManagementItemTags.CONCRETES),
      // TODO: Replace with ConventionalItemTags.CONCRETE_POWDERS starting in 1.21
      Group.by(InventoryManagementItemTags.CONCRETE_POWDERS)
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

    public static Group by(TagKey<Item> tag) {
      return new Group((stack) -> stack.isIn(tag), groupUnderName(tag.getTranslationKey()));
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
