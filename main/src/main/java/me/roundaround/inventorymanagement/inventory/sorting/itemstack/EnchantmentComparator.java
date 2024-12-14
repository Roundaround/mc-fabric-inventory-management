package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.SerialComparator;
import net.minecraft.component.DataComponentType;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Language;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class EnchantmentComparator implements Comparator<ItemStack> {
  protected final Comparator<ItemStack> base;

  public EnchantmentComparator(DataComponentType<ItemEnchantmentsComponent> type) {
    this.base = Comparator.comparing((stack) -> EnchantmentSummary.of(stack, type),
        SerialComparator.comparing(Comparator.comparingInt(EnchantmentSummary::count).reversed(),
            Comparator.comparingInt(EnchantmentSummary::max).reversed(),
            Comparator.comparingInt(EnchantmentSummary::sum).reversed(),
            Comparator.comparingInt(EnchantmentSummary::first).reversed(),
            Comparator.comparing(EnchantmentSummary::text, Comparator.nullsLast(String::compareToIgnoreCase))
        )
    );
  }

  @Override
  public int compare(ItemStack o1, ItemStack o2) {
    return this.base.compare(o1, o2);
  }

  private record EnchantmentSummary(int count, int max, int sum, int first, String text) {
    public static EnchantmentSummary of(ItemStack stack, DataComponentType<ItemEnchantmentsComponent> type) {
      ItemEnchantmentsComponent component = stack.get(type);
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

    private void mapRegistryEntriesToIndices() {
      AtomicInteger index = new AtomicInteger(0);
      Registries.ENCHANTMENT.forEach((enchantment) -> {

      });
    }
  }
}
