package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.SerialComparator;
import me.roundaround.inventorymanagement.inventory.sorting.WrapperComparatorImpl;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;

public class ItemMetadataComparator extends WrapperComparatorImpl<ItemStack> {
  private static ItemMetadataComparator instance;

  private ItemMetadataComparator() {
    //@formatter:off
    super(SerialComparator.comparing(
        new CustomNameComparator(),
        new PlayerHeadNameComparator(),
        new EnchantmentComparator(DataComponentTypes.ENCHANTMENTS),
        new EnchantmentComparator(DataComponentTypes.STORED_ENCHANTMENTS),
        new PaintingComparator(),
        new BannerComparator(),
        new FireworkRocketComparator(),
        new InstrumentComparator(),
        new PotionComparator(),
        new SuspiciousStewComparator(),
        new DecoratedPotComparator(),
        new CountComparator(),
        new DamageComparator()
    ));
    //@formatter:on
  }

  public static ItemMetadataComparator getInstance() {
    if (instance == null) {
      instance = new ItemMetadataComparator();
    }
    return instance;
  }
}
