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
  public ContainerFirstComparator() {
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

  private static List<Predicate<ItemStack>> getMatchers() {
    // TODO: Registry/hook for mods to hook in their custom containers
    return List.of(
        (stack) -> stack.getItem() instanceof BlockItem block && block.getBlock() instanceof ShulkerBoxBlock,
        (stack) -> stack.getItem() instanceof BundleItem
    );
  }
}
