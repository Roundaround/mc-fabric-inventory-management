package me.roundaround.inventorymanagement.inventory.sorting;

import java.util.Objects;
import java.util.UUID;

public class PlayerSortParameters {
  private final UUID player;
  private final boolean alphabetical;
  private final boolean containersFirst;
  private final boolean groupItems;

  public PlayerSortParameters(UUID player) {
    // TODO: Populate
    this.player = player;
    this.alphabetical = true;
    this.containersFirst = false;
    this.groupItems = true;
  }

  public boolean isStillValid() {
    return new PlayerSortParameters(this.player).equals(this);
  }

  public UUID getPlayer() {
    return this.player;
  }

  public boolean isAlphabetical() {
    return this.alphabetical;
  }

  public boolean isContainersFirst() {
    return this.containersFirst;
  }

  public boolean isGroupItems() {
    return this.groupItems;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof PlayerSortParameters that))
      return false;
    return this.alphabetical == that.alphabetical && this.containersFirst == that.containersFirst &&
           this.groupItems == that.groupItems && Objects.equals(player, that.player);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.player, this.alphabetical, this.containersFirst, this.groupItems);
  }
}
