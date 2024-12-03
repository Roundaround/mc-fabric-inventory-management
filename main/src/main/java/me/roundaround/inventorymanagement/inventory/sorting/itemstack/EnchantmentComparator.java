package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.AbstractComparator;
import me.roundaround.inventorymanagement.inventory.sorting.SerialComparator;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Language;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class EnchantmentComparator extends AbstractComparator<ItemStack> {
  @Override
  protected Comparator<ItemStack> init() {
    return Comparator.comparing(EnchantmentSummary::of,
        SerialComparator.comparing(Comparator.comparingInt(EnchantmentSummary::count).reversed(),
            Comparator.comparingInt(EnchantmentSummary::max).reversed(),
            Comparator.comparingInt(EnchantmentSummary::sum).reversed(),
            Comparator.comparingInt(EnchantmentSummary::first).reversed(),
            Comparator.comparing(EnchantmentSummary::text, Comparator.nullsLast(String::compareToIgnoreCase))
        )
    );
  }

  private record EnchantmentSummary(int count, int max, int sum, int first, String text) {
    public static EnchantmentSummary of(ItemStack stack) {
      ItemEnchantmentsComponent component = stack.get(DataComponentTypes.ENCHANTMENTS);
      if (component == null || component.isEmpty()) {
        return new EnchantmentSummary(0, 0, 0, 0, "");
      }

      int count = component.getSize();
      int max = 0;
      int sum = 0;
      int first = 0;
      StringBuilder text = new StringBuilder();

      // Sort the list of enchantments ahead of time so that it's deterministic
      LinkedHashSet<RegistryEntry<Enchantment>> enchantments = component.getEnchantments()
          .stream()
          .sorted(Comparator.comparing((entry) -> Language.getInstance().get(entry.value().getTranslationKey()),
              String::compareToIgnoreCase
          ))
          .collect(Collectors.toCollection(LinkedHashSet::new));

      for (var entry : enchantments) {
        int level = component.getLevel(entry.value());
        max = Math.max(max, level);
        sum += level;
        first = first == 0 ? level : first;
        text.append(Language.getInstance().get(entry.value().getTranslationKey()));
      }

      return new EnchantmentSummary(count, max, sum, first, text.toString());
    }
  }
}
