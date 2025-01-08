package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.CachingComparatorImpl;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class SuspiciousStewComparator extends CachingComparatorImpl<ItemStack, SuspiciousStewComparator.StewSummary> {
  public SuspiciousStewComparator() {
    super(Comparator.naturalOrder());
  }

  @Override
  protected StewSummary mapValue(ItemStack stack) {
    return StewSummary.of(stack);
  }

  protected record StewSummary() implements Comparable<StewSummary> {
    public static StewSummary of(ItemStack stack) {
      SuspiciousStewEffectsComponent component = stack.get(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS);
      if (component == null) {
        return new StewSummary();
      }

      return null;
    }

    @Override
    public int compareTo(@NotNull SuspiciousStewComparator.StewSummary other) {
      return 0;
    }
  }
}
