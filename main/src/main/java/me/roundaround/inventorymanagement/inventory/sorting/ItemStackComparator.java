package me.roundaround.inventorymanagement.inventory.sorting;

import me.roundaround.inventorymanagement.server.network.ServerI18nTracker;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ItemStackComparator extends SerialComparator<ItemStack> {
  private static final HashMap<UUID, ItemStackComparator> instances = new HashMap<>();

  private ItemStackComparator(UUID player) {
    super(List.of(
        Comparator.comparing((stack) -> getBaseName(stack, player)),
        Comparator.comparing(ItemStackComparator::getPlayerHeadName),
        Comparator.comparingInt(ItemStackComparator::getCountOrDurability).reversed()
    ));
  }

  public static ItemStackComparator getInstance(UUID player) {
    return instances.computeIfAbsent(player, ItemStackComparator::new);
  }

  private static String getBaseName(ItemStack itemStack, UUID player) {
    Text customName = itemStack.get(DataComponentTypes.CUSTOM_NAME);
    if (customName != null) {
      return customName.getString();
    }
    return ServerI18nTracker.getInstance(player).get(itemStack.getTranslationKey());
  }

  private static int getCountOrDurability(ItemStack itemStack) {
    if (itemStack.getCount() > 1) {
      return itemStack.getCount();
    }
    return itemStack.getMaxDamage() - itemStack.getDamage();
  }

  private static String getPlayerHeadName(ItemStack itemStack) {
    ProfileComponent profile = itemStack.get(DataComponentTypes.PROFILE);
    return profile == null || profile.name().isEmpty() ? "" : profile.name().get();
  }
}
