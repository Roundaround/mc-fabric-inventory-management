package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.SerialComparator;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ItemStackComparator implements SerialComparator<ItemStack>, AutoCloseable {
  private final List<Comparator<ItemStack>> subComparators;

  private ItemStackComparator(Collection<Comparator<ItemStack>> subComparators) {
    this.subComparators = List.copyOf(subComparators);
  }

  @SafeVarargs
  private ItemStackComparator(Comparator<ItemStack>... subComparators) {
    this(List.of(subComparators));
  }

  @Override
  public @NotNull Iterator<Comparator<ItemStack>> iterator() {
    return this.subComparators.iterator();
  }

  @Override
  public void close() {
    this.clearCache();
  }

  public static ItemStackComparator containersFirst(Comparator<ItemStack> andThen) {
    return new ItemStackComparator(new ContainerFirstComparator(), andThen);
  }

  public static ItemStackComparator creativeInventoryOrder(UUID player) {
    return new ItemStackComparator(CreativeIndexComparator.getInstance(), new ItemNameComparator(player),
        ItemMetadataComparator.getInstance(), viaRegistry(), containerContents()
    );
  }

  public static ItemStackComparator alphabetical(UUID player) {
    return new ItemStackComparator(
        new ItemNameComparator(player), ItemMetadataComparator.getInstance(), viaRegistry(), containerContents());
  }

  private static Comparator<ItemStack> containerContents() {
    // TODO: Order based on shulker and bundle contents
    // TODO: Registry/hook for mods to hook in to sort their own custom containers
    // TODO: Pass all the other comparators down to this one internally so that it matches top level algorithm
    return Comparator.comparingInt((stack) -> 0);
  }

  private static Comparator<ItemStack> viaRegistry() {
    // TODO: Order based on the comparator registry
    // TODO: Create a comparator registry for mods to register custom comparators based on their own data
    return Comparator.comparingInt((stack) -> 0);
  }
}
