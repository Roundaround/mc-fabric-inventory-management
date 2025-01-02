package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.SerialComparator;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ItemStackComparator implements SerialComparator<ItemStack> {
  private static final HashMap<UUID, ItemStackComparator> COMPARATORS = new HashMap<>();

  private final Parameters parameters;
  private final List<Comparator<ItemStack>> subComparators;

  private ItemStackComparator(UUID player, Collection<Comparator<ItemStack>> subComparators) {
    this.parameters = new Parameters(player);
    this.subComparators = List.copyOf(subComparators);
  }

  @Override
  public @NotNull Iterator<Comparator<ItemStack>> iterator() {
    return this.subComparators.iterator();
  }

  public static ItemStackComparator create(UUID player) {
    Parameters parameters = new Parameters(player);
    ArrayList<Comparator<ItemStack>> delegates = new ArrayList<>();

    if (parameters.alphabetical && parameters.containersFirst) {
      delegates.add(ContainerFirstComparator.getInstance());
    }
    if (!parameters.alphabetical) {
      delegates.add(CreativeIndexComparator.getInstance());
    }

    delegates.add(new ItemNameComparator(player));
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

  private static class Parameters {
    private final UUID player;
    private final boolean alphabetical;
    private final boolean containersFirst;

    public Parameters(UUID player) {
      // TODO: Populate
      this.player = player;
      this.alphabetical = true;
      this.containersFirst = false;
    }

    public boolean isStillValid() {
      return new Parameters(this.player).equals(this);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (!(o instanceof Parameters that))
        return false;
      return alphabetical == that.alphabetical && containersFirst == that.containersFirst &&
             Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
      return Objects.hash(player, alphabetical, containersFirst);
    }
  }
}
