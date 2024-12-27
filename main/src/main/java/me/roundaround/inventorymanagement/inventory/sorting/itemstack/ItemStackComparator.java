package me.roundaround.inventorymanagement.inventory.sorting.itemstack;

import me.roundaround.inventorymanagement.inventory.sorting.NoOpComparator;
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

  @SafeVarargs
  private ItemStackComparator(UUID player, Comparator<ItemStack>... subComparators) {
    this(player, List.of(subComparators));
  }

  @Override
  public @NotNull Iterator<Comparator<ItemStack>> iterator() {
    return this.subComparators.iterator();
  }

  public static ItemStackComparator creativeInventoryOrder(UUID player) {
    return new ItemStackComparator(player, CreativeIndexComparator.getInstance(), new ItemNameComparator(player),
        ItemMetadataComparator.getInstance(), viaRegistry(), containerContents()
    );
  }

  private static List<Comparator<ItemStack>> alphabetical(UUID player, boolean containersFirst) {
    return List.of(containersFirst ? new ContainerFirstComparator() : new NoOpComparator<>(),
        new ItemNameComparator(player), ItemMetadataComparator.getInstance(), viaRegistry(), containerContents()
    );
  }

  public static ItemStackComparator create(UUID player) {
    Parameters parameters = new Parameters(player);
    ItemStackComparator comparator = new ItemStackComparator(player, alphabetical(player, parameters.containersFirst));
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
    COMPARATORS.remove(player);
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
