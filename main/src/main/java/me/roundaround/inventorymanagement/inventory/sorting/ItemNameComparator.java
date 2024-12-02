package me.roundaround.inventorymanagement.inventory.sorting;

import me.roundaround.inventorymanagement.server.network.ServerI18nTracker;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.Comparator;
import java.util.Map;
import java.util.UUID;

public class ItemNameComparator implements Comparator<ItemStack> {
  private final Map<String, String> store;

  public ItemNameComparator() {
    this.store = null;
  }

  public ItemNameComparator(UUID player) {
    this.store = ServerI18nTracker.getInstance(player).getAll();
  }

  @Override
  public int compare(ItemStack o1, ItemStack o2) {
    return this.getName(o1).compareToIgnoreCase(this.getName(o2));
  }

  private String getName(ItemStack stack) {
    Text customName = stack.get(DataComponentTypes.CUSTOM_NAME);
    if (customName != null) {
      return customName.getString();
    }

    if (this.store == null) {
      return "";
    }

    String i18nKey = stack.getTranslationKey();
    return this.store.getOrDefault(i18nKey, i18nKey);
  }
}
