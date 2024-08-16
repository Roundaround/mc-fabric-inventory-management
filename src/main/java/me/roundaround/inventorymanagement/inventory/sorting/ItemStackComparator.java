package me.roundaround.inventorymanagement.inventory.sorting;

import net.minecraft.client.item.TooltipType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ItemStackComparator implements Comparator<ItemStack> {
  private static final List<Comparator<ItemStack>> SUB_COMPARATORS = List.of(
      Comparator.comparing(ItemStackComparator::getBaseName),
      Comparator.comparingInt(ItemStackComparator::getCountOrDurability).reversed(),
      Comparator.comparing(ItemStackComparator::getTooltipContent)
  );

  private final SerialComparator<ItemStack> underlyingComparator;

  private ItemStackComparator(SerialComparator<ItemStack> underlyingComparator) {
    this.underlyingComparator = underlyingComparator;
  }

  @Override
  public int compare(ItemStack o1, ItemStack o2) {
    return underlyingComparator.compare(o1, o2);
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

  public static ItemStackComparator comparator() {
    return new ItemStackComparator(SerialComparator.comparing(SUB_COMPARATORS));
  }
}
