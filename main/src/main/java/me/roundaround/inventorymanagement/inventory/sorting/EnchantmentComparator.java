package me.roundaround.inventorymanagement.inventory.sorting;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.ItemStack;

import java.util.Comparator;

public class EnchantmentComparator extends AbstractComparator<ItemStack> {
  @Override
  protected Comparator<ItemStack> init() {
    return Comparator.comparing(EnchantmentSummary::of, Comparator.nullsLast(SerialComparator.comparing(
        Comparator.comparingInt(EnchantmentSummary::count).reversed(),
        Comparator.comparingInt(EnchantmentSummary::max).reversed(),
        Comparator.comparingInt(EnchantmentSummary::sum).reversed(),
        Comparator.comparingInt(EnchantmentSummary::first).reversed(),
        Comparator.comparing(EnchantmentSummary::text, Comparator.nullsLast(String::compareToIgnoreCase))
    )));
  }

  private record EnchantmentSummary(int count, int max, int sum, int first, String text) {
    public static EnchantmentSummary of(ItemStack stack) {
      ItemEnchantmentsComponent component = stack.get(DataComponentTypes.ENCHANTMENTS);
      if (component == null || component.isEmpty()) {
        return null;
      }

      int count = component.getSize();
      int max = 0;
      int sum = 0;
      int first = 0;
      String text = "";

      for (var entry : component.getEnchantments()) {
        int level = component.getLevel(entry.value());
        max = Math.max(max, level);
        sum += level;
        if (first == 0) {
          first = level;
          text = entry.getIdAsString();
        }
      }

      return new EnchantmentSummary(count, max, sum, first, text);
    }
  }
}
