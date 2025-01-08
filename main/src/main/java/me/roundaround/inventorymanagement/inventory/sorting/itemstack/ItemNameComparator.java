package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.WrapperComparatorImpl;
import me.roundaround.inventorymanagement.server.network.ServerI18nTracker;
import net.minecraft.item.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class ItemNameComparator extends WrapperComparatorImpl<ItemStack> {
  private static final List<GroupMatcher> groups = List.of(

  );

  public ItemNameComparator(UUID player) {
    super(Comparator.comparing(ServerI18nTracker.getInstance(player).snapshot()::get, String::compareToIgnoreCase));
  }

  private static String getName(UUID player, ItemStack stack) {
    // TODO: Check player options for grouping
    // TODO: Put all the group matchers into some kind of custom registry


  }

  private

  protected record GroupMatcher(Predicate<ItemStack> predicate, ItemStack sortValue) {
  }
}
