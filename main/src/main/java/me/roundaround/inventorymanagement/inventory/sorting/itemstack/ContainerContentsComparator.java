package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.WrapperComparatorImpl;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;

public class ContainerContentsComparator extends WrapperComparatorImpl<ItemStack> {
  private static ContainerContentsComparator instance;

  private ContainerContentsComparator() {
    super((o1, o2) -> {
      if (!isContainer(o1) || !isContainer(o2)) {
        return 0;
      }
      // TODO: Order based on shulker and bundle contents
      return 0;
    });
  }

  @Override
  public void clearCache() {
    instance = null;
  }

  public static ContainerContentsComparator getInstance() {
    if (instance == null) {
      instance = new ContainerContentsComparator();
    }
    return instance;
  }

  private static boolean isContainer(ItemStack stack) {
    for (Predicate<ItemStack> matcher : getMatchers()) {
      if (matcher.test(stack)) {
        return true;
      }
    }
    return false;
  }

  private static List<Predicate<ItemStack>> getMatchers() {
    // TODO: Registry/hook for mods to hook in their custom containers
    //@formatter:off
    return List.of(
        (stack) -> stack.getItem() instanceof BlockItem block && block.getBlock() instanceof ShulkerBoxBlock,
        (stack) -> stack.getItem() instanceof BundleItem
    );
    //@formatter:on
  }
}
