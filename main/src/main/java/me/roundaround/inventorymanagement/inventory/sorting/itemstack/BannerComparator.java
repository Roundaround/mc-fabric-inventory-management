package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.ItemStack;

import java.util.Comparator;

public class BannerComparator implements Comparator<ItemStack> {
  protected final Comparator<ItemStack> base;

  public BannerComparator() {
    this.base = Comparator.comparing(BannerSummary::of, Comparator.comparingInt(BannerSummary::count));
  }

  @Override
  public int compare(ItemStack o1, ItemStack o2) {
    return this.base.compare(o1, o2);
  }

  private record BannerSummary(int count) {
    public static BannerSummary of(ItemStack stack) {
      BannerPatternsComponent component = stack.get(DataComponentTypes.BANNER_PATTERNS);
      if (component == null || component.layers().isEmpty()) {
        return new BannerSummary(0);
      }
      return new BannerSummary(component.layers().size());
    }
  }
}
