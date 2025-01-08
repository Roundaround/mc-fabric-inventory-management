package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.CachingComparatorImpl;
import me.roundaround.inventorymanagement.inventory.sorting.SerialComparator;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class ContainerContentsComparator extends CachingComparatorImpl<ItemStack,
    ContainerContentsComparator.ContentsSummary> {
  private static ContainerContentsComparator instance;

  private ContainerContentsComparator() {
    super(Comparator.naturalOrder());
  }

  public static ContainerContentsComparator getInstance() {
    if (instance == null) {
      instance = new ContainerContentsComparator();
    }
    return instance;
  }

  @Override
  protected ContentsSummary mapValue(ItemStack stack) {
    return ContentsSummary.of(stack);
  }

  protected record ContentsSummary(int usedSlots, int totalCount) implements Comparable<ContentsSummary> {
    private static Comparator<ContentsSummary> comparator;

    public static ContentsSummary of(ItemStack stack) {
      ContainerComponent component = stack.get(DataComponentTypes.CONTAINER);
      if (component == null) {
        return null;
      }

      // This is one of the very few cases where I don't get too specific. If you have two full shulker boxes, they will
      // be treated as equivalent, even if the actual contained items differ.

      var usedSlots = new Object() {
        int value = 0;
      };
      var totalCount = new Object() {
        int value = 0;
      };
      component.stream().forEach((slotStack) -> {
        if (!slotStack.isEmpty()) {
          usedSlots.value++;
        }

        totalCount.value += slotStack.getCount();
      });

      return new ContentsSummary(usedSlots.value, totalCount.value);
    }

    @Override
    public int compareTo(@NotNull ContainerContentsComparator.ContentsSummary other) {
      return getComparator().compare(this, other);
    }

    private static Comparator<ContentsSummary> getComparator() {
      if (comparator == null) {
        comparator = SerialComparator.comparing(
            Comparator.comparingInt(ContentsSummary::usedSlots).reversed(),
            Comparator.comparingInt(ContentsSummary::totalCount).reversed()
        );
      }
      return comparator;
    }
  }
}
