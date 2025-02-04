package me.roundaround.inventorymanagement.inventory.sorting;

import java.util.Objects;
import java.util.UUID;

public record SortContext(UUID player, boolean alphabetical, boolean containersFirst, boolean itemGrouping) {
  public SortContext(UUID player) {
    // TODO: Populate
    this(player, true, false, true);
  }

  public boolean isStillValid() {
    return new SortContext(this.player).equals(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof SortContext that))
      return false;
    return this.alphabetical == that.alphabetical && this.containersFirst == that.containersFirst &&
           this.itemGrouping == that.itemGrouping && Objects.equals(player, that.player);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.player, this.alphabetical, this.containersFirst, this.itemGrouping);
  }
}
