package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.SerialComparator;
import me.roundaround.inventorymanagement.inventory.sorting.SortContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ItemStackComparator implements SerialComparator<ItemStack> {
  private final List<Comparator<ItemStack>> subComparators;

  private ItemStackComparator(Collection<Comparator<ItemStack>> subComparators) {
    this.subComparators = List.copyOf(subComparators);
  }

  @Override
  public @NotNull Iterator<Comparator<ItemStack>> iterator() {
    return this.subComparators.iterator();
  }

  public static ItemStackComparator create(UUID player) {
    SortContext context = new SortContext(player);
    ArrayList<Comparator<ItemStack>> delegates = new ArrayList<>();

    if (context.alphabetical() && context.containersFirst()) {
      delegates.add(new ContainerFirstComparator());
    }
    if (!context.alphabetical()) {
      delegates.add(CreativeIndexComparator.getInstance());
    }

    delegates.add(new ItemNameComparator(context));
    delegates.add(ItemMetadataComparator.getInstance());
    delegates.add(RegistryBackedComparator.getInstance());
    delegates.add(new ContainerContentsComparator());

    return new ItemStackComparator(delegates);
  }
}
