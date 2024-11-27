package me.roundaround.inventorymanagement.inventory.sorting;

import net.minecraft.client.item.TooltipType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ItemStackComparator extends SerialComparator<ItemStack> {
  private static final ItemStackComparator instance = new ItemStackComparator();

  private ItemStackComparator() {
    super(List.of(
        Comparator.comparing(ItemStackComparator::getBaseName),
        Comparator.comparingInt(ItemStackComparator::getCountOrDurability).reversed(),
        Comparator.comparing(ItemStackComparator::getTooltipContent)
    ));
  }

  public static ItemStackComparator get() {
    return instance;
  }

  private static String getBaseName(ItemStack itemStack) {
    return itemStack.getName().getString();
  }

  private static int getCountOrDurability(ItemStack itemStack) {
    if (itemStack.getCount() > 1) {
      return itemStack.getCount();
    }
    return itemStack.getMaxDamage() - itemStack.getDamage();
  }

  private static String getTooltipContent(ItemStack itemStack) {
    return itemStack.getTooltip(Item.TooltipContext.DEFAULT, null, TooltipType.ADVANCED)
        .stream()
        .map(Text::getString)
        .collect(Collectors.joining(" "));
  }

  private static String getPlayerHeadName(ItemStack itemStack) {
    ProfileComponent profile = itemStack.get(DataComponentTypes.PROFILE);
    return profile == null || profile.name().isEmpty() ?
        "" :
        Text.translatable(itemStack.getItem().getTranslationKey() + ".named", profile.name().get()).getString();
  }
}
