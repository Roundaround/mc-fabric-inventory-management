package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.*;
import me.roundaround.inventorymanagement.server.network.ServerI18nTracker;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ItemNameComparator extends CachingComparatorImpl<ItemStack, List<ItemStack>> {
  //@formatter:off
  // TODO: Move into some kind of custom registry
  private static final List<Group> groups = List.of(
      Group.rootBlockItem(Items.SHULKER_BOX, BlockTags.SHULKER_BOXES)
  );
  //@formatter:on

  private final PlayerSortParameters parameters;

  public ItemNameComparator(PlayerSortParameters parameters) {
    //@formatter:off
    super(LexicographicalListComparator.comparing(
        SerialComparator.comparing(
            Comparator.comparing(ItemStack::isEmpty).reversed(),
            PredicatedComparator.of(
                (stack) -> !stack.isEmpty(),
                Comparator.comparing(
                  ServerI18nTracker.getInstance(parameters.getPlayer()).snapshot()::get,
                  Comparator.nullsFirst(String::compareToIgnoreCase)
                )
            )
        )
    ));
    //@formatter:on
    this.parameters = parameters;
  }

  @Override
  protected List<ItemStack> mapValue(ItemStack stack) {
    if (!this.parameters.isGroupItems()) {
      return List.of(stack);
    }

    for (Group group : groups) {
      if (group.predicate().test(stack)) {
        return group.groupProducer().apply(stack);
      }
    }

    return List.of(stack);
  }

  private String getName(ItemStack stack) {
    return ServerI18nTracker.getInstance(this.parameters.getPlayer()).snapshot().get(stack);
  }

  protected record Group(Predicate<ItemStack> predicate, Function<ItemStack, List<ItemStack>> groupProducer) {
    public static Group rootItem(Item root, Item... items) {
      return rootItem(root, List.of(items));
    }

    public static Group rootItem(Item root, Collection<Item> items) {
      return new Group((stack) -> items.contains(stack.getItem()), groupUnderItem(root));
    }

    public static Group rootItem(Item root, TagKey<Item> tag) {
      return new Group((stack) -> stack.isIn(tag), groupUnderItem(root));
    }

    public static Group rootBlockItem(Item root, TagKey<Block> tag) {
      return new Group(
          (stack) -> stack.getItem() instanceof BlockItem block && block.getBlock().getDefaultState().isIn(tag),
          groupUnderItem(root)
      );
    }

    private static Function<ItemStack, List<ItemStack>> groupUnderItem(Item item) {
      return (stack) -> List.of(
          stack.copyComponentsToNewStack(item, stack.getCount()), stack.isOf(item) ? ItemStack.EMPTY : stack);
    }
  }
}
