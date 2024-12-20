package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.WrapperComparatorImpl;
import me.roundaround.inventorymanagement.server.network.ServerI18nTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.Comparator;
import java.util.UUID;

public class ItemNameComparator extends WrapperComparatorImpl<ItemStack> {
  public ItemNameComparator(PlayerEntity player) {
    this(player.getUuid());
  }

  public ItemNameComparator(UUID player) {
    super(Comparator.comparing(ServerI18nTracker.getInstance(player).snapshot()::get, String::compareToIgnoreCase));
  }
}
