package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.server.network.ServerI18nTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.Comparator;
import java.util.UUID;

public class ItemNameComparator implements Comparator<ItemStack> {
  private final Comparator<ItemStack> base;

  public ItemNameComparator(PlayerEntity player) {
    this(player.getUuid());
  }

  public ItemNameComparator(UUID player) {
    ServerI18nTracker.Snapshot i18nSnapshot = ServerI18nTracker.getInstance(player).snapshot();
    this.base = Comparator.comparing(i18nSnapshot::get, String::compareToIgnoreCase);
  }

  @Override
  public int compare(ItemStack o1, ItemStack o2) {
    return this.base.compare(o1, o2);
  }
}
