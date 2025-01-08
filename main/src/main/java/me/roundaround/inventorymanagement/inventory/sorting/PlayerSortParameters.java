package me.roundaround.inventorymanagement.inventory.sorting;

import java.util.Objects;
import java.util.UUID;

public class PlayerSortParameters {
  private final UUID player;
  private final boolean alphabetical;
  private final boolean containersFirst;

  public PlayerSortParameters(UUID player) {
    // TODO: Populate
    this.player = player;
    this.alphabetical = true;
    this.containersFirst = false;
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

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof PlayerSortParameters that))
      return false;
    return alphabetical == that.alphabetical && containersFirst == that.containersFirst &&
           Objects.equals(player, that.player);
  }

  @Override
  public int hashCode() {
    return Objects.hash(player, alphabetical, containersFirst);
  }
}
