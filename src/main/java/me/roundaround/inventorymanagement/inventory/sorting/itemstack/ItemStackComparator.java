package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.PlayerSortParameters;
import me.roundaround.inventorymanagement.inventory.sorting.SerialComparator;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ItemStackComparator implements SerialComparator<ItemStack> {
  private static final HashMap<UUID, ItemStackComparator> COMPARATORS = new HashMap<>();

  private final PlayerSortParameters parameters;
  private final List<Comparator<ItemStack>> subComparators;

  private ItemStackComparator(UUID player, Collection<Comparator<ItemStack>> subComparators) {
    this.parameters = new PlayerSortParameters(player);
    this.subComparators = List.copyOf(subComparators);
  }

  @Override
  public @NotNull Iterator<Comparator<ItemStack>> iterator() {
    return this.subComparators.iterator();
  }

  public static ItemStackComparator create(UUID player) {
    PlayerSortParameters parameters = new PlayerSortParameters(player);
    ArrayList<Comparator<ItemStack>> delegates = new ArrayList<>();

    if (parameters.isAlphabetical() && parameters.isContainersFirst()) {
      delegates.add(ContainerFirstComparator.getInstance());
    }
    if (!parameters.isAlphabetical()) {
      delegates.add(CreativeIndexComparator.getInstance());
    }

    delegates.add(new ItemNameComparator(parameters));
    delegates.add(ItemMetadataComparator.getInstance());
    delegates.add(RegistryBackedComparator.getInstance());
    delegates.add(ContainerContentsComparator.getInstance());

    ItemStackComparator comparator = new ItemStackComparator(player, delegates);
    COMPARATORS.put(player, comparator);
    return comparator;
  }

  public static ItemStackComparator get(UUID player) {
    ItemStackComparator comparator = COMPARATORS.get(player);
    if (comparator != null && comparator.parameters.isStillValid()) {
      return comparator;
    }
    return create(player);
  }

  public static void remove(UUID player) {
    // TODO: Call anywhere that justifies clearing the cached comparator: player logout, config change, lang change, etc
    COMPARATORS.remove(player);
  }

  public static void clear() {
    COMPARATORS.clear();
  }
}
