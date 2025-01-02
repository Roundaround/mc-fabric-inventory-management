package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.WrapperComparatorImpl;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class ContainerFirstComparator extends WrapperComparatorImpl<ItemStack> {
  private static ContainerFirstComparator instance;

  private ContainerFirstComparator() {
    super(Comparator.comparing((stack) -> {
      int index = 0;
      for (Predicate<ItemStack> matcher : getMatchers()) {
        if (matcher.test(stack)) {
          return index;
        }
        index++;
      }

      return null;
    }, Comparator.nullsLast(Integer::compareTo)));
  }

  public static ContainerFirstComparator getInstance() {
    if (instance == null) {
      instance = new ContainerFirstComparator();
    }
    return instance;
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
