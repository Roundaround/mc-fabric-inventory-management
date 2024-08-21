package me.roundaround.inventorymanagement.api;

import me.roundaround.inventorymanagement.config.value.ButtonVisibility;
import me.roundaround.roundalib.config.value.Position;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ScreenConfig {
  public ButtonVisibility playerSideSortVisibility;
  public ButtonVisibility playerSideTransferVisibility;
  public ButtonVisibility playerSideStackVisibility;
  public ButtonVisibility containerSideSortVisibility;
  public ButtonVisibility containerSideTransferVisibility;
  public ButtonVisibility containerSideStackVisibility;
  public Position playerSideOffset;
  public Position containerSideOffset;

  public ScreenConfig() {
  }

  public ScreenConfig(
      ButtonVisibility playerSideSortVisibility,
      ButtonVisibility playerSideTransferVisibility,
      ButtonVisibility playerSideStackVisibility,
      ButtonVisibility containerSideSortVisibility,
      ButtonVisibility containerSideTransferVisibility,
      ButtonVisibility containerSideStackVisibility,
      Position playerSideOffset,
      Position containerSideOffset
  ) {
    this.playerSideSortVisibility = playerSideSortVisibility;
    this.playerSideTransferVisibility = playerSideTransferVisibility;
    this.playerSideStackVisibility = playerSideStackVisibility;
    this.containerSideSortVisibility = containerSideSortVisibility;
    this.containerSideTransferVisibility = containerSideTransferVisibility;
    this.containerSideStackVisibility = containerSideStackVisibility;
    this.playerSideOffset = playerSideOffset;
    this.containerSideOffset = containerSideOffset;
  }

  public ScreenConfig(ScreenConfig other) {
    this.playerSideSortVisibility = other.playerSideSortVisibility;
    this.playerSideTransferVisibility = other.playerSideTransferVisibility;
    this.playerSideStackVisibility = other.playerSideStackVisibility;
    this.containerSideSortVisibility = other.containerSideSortVisibility;
    this.containerSideTransferVisibility = other.containerSideTransferVisibility;
    this.containerSideStackVisibility = other.containerSideStackVisibility;
    this.playerSideOffset = other.playerSideOffset;
    this.containerSideOffset = other.containerSideOffset;
  }

  public ScreenConfig patch(ScreenConfig other) {
    if (other == null) {
      return this;
    }

    if (other.playerSideSortVisibility != null) {
      this.playerSideSortVisibility = other.playerSideSortVisibility;
    }
    if (other.playerSideTransferVisibility != null) {
      this.playerSideTransferVisibility = other.playerSideTransferVisibility;
    }
    if (other.playerSideStackVisibility != null) {
      this.playerSideStackVisibility = other.playerSideStackVisibility;
    }
    if (other.containerSideSortVisibility != null) {
      this.containerSideSortVisibility = other.containerSideSortVisibility;
    }
    if (other.containerSideTransferVisibility != null) {
      this.containerSideTransferVisibility = other.containerSideTransferVisibility;
    }
    if (other.containerSideStackVisibility != null) {
      this.containerSideStackVisibility = other.containerSideStackVisibility;
    }
    if (other.playerSideOffset != null) {
      this.playerSideOffset = other.playerSideOffset;
    }
    if (other.containerSideOffset != null) {
      this.containerSideOffset = other.containerSideOffset;
    }

    return this;
  }

  public ButtonVisibility getPlayerSideSortVisibility() {
    return playerSideSortVisibility;
  }

  public void setPlayerSideSortVisibility(ButtonVisibility playerSideSortVisibility) {
    this.playerSideSortVisibility = playerSideSortVisibility;
  }

  public void clearPlayerSideSortVisibility() {
    this.playerSideSortVisibility = null;
  }

  public ButtonVisibility getPlayerSideTransferVisibility() {
    return playerSideTransferVisibility;
  }

  public void setPlayerSideTransferVisibility(ButtonVisibility playerSideTransferVisibility) {
    this.playerSideTransferVisibility = playerSideTransferVisibility;
  }

  public void clearPlayerSideTransferVisibility() {
    this.playerSideTransferVisibility = null;
  }

  public ButtonVisibility getPlayerSideStackVisibility() {
    return playerSideStackVisibility;
  }

  public void setPlayerSideStackVisibility(ButtonVisibility playerSideStackVisibility) {
    this.playerSideStackVisibility = playerSideStackVisibility;
  }

  public void clearPlayerSideStackVisibility() {
    this.playerSideStackVisibility = null;
  }

  public ButtonVisibility getContainerSideSortVisibility() {
    return containerSideSortVisibility;
  }

  public void setContainerSideSortVisibility(ButtonVisibility containerSideSortVisibility) {
    this.containerSideSortVisibility = containerSideSortVisibility;
  }

  public void clearContainerSideSortVisibility() {
    this.containerSideSortVisibility = null;
  }

  public ButtonVisibility getContainerSideTransferVisibility() {
    return containerSideTransferVisibility;
  }

  public void setContainerSideTransferVisibility(ButtonVisibility containerSideTransferVisibility) {
    this.containerSideTransferVisibility = containerSideTransferVisibility;
  }

  public void clearContainerSideTransferVisibility() {
    this.containerSideTransferVisibility = null;
  }

  public ButtonVisibility getContainerSideStackVisibility() {
    return containerSideStackVisibility;
  }

  public void setContainerSideStackVisibility(ButtonVisibility containerSideStackVisibility) {
    this.containerSideStackVisibility = containerSideStackVisibility;
  }

  public void clearContainerSideStackVisibility() {
    this.containerSideStackVisibility = null;
  }

  public Position getPlayerSideOffset() {
    return playerSideOffset;
  }

  public void setPlayerSideOffset(Position playerSideOffset) {
    this.playerSideOffset = playerSideOffset;
  }

  public void clearPlayerSideOffset() {
    this.playerSideOffset = null;
  }

  public Position getContainerSideOffset() {
    return containerSideOffset;
  }

  public void setContainerSideOffset(Position containerSideOffset) {
    this.containerSideOffset = containerSideOffset;
  }

  public void clearContainerSideOffset() {
    this.containerSideOffset = null;
  }

  public boolean isEmpty() {
    return playerSideSortVisibility == null && playerSideTransferVisibility == null &&
        playerSideStackVisibility == null && containerSideSortVisibility == null &&
        containerSideTransferVisibility == null && containerSideStackVisibility == null && playerSideOffset == null &&
        containerSideOffset == null;
  }

  public static HashMap<String, String> serialize(ScreenConfig config) {
    HashMap<String, String> serialized = new HashMap<>();

    if (config.playerSideSortVisibility != null) {
      serialized.put("playerSideSortVisibility", config.playerSideSortVisibility.getId());
    }
    if (config.playerSideTransferVisibility != null) {
      serialized.put("playerSideTransferVisibility", config.playerSideTransferVisibility.getId());
    }
    if (config.playerSideStackVisibility != null) {
      serialized.put("playerSideStackVisibility", config.playerSideStackVisibility.getId());
    }
    if (config.containerSideSortVisibility != null) {
      serialized.put("containerSideSortVisibility", config.containerSideSortVisibility.getId());
    }
    if (config.containerSideTransferVisibility != null) {
      serialized.put("containerSideTransferVisibility", config.containerSideTransferVisibility.getId());
    }
    if (config.containerSideStackVisibility != null) {
      serialized.put("containerSideStackVisibility", config.containerSideStackVisibility.getId());
    }
    if (config.playerSideOffset != null) {
      serialized.put("playerSideOffset", config.playerSideOffset.toString());
    }
    if (config.containerSideOffset != null) {
      serialized.put("containerSideOffset", config.containerSideOffset.toString());
    }

    return serialized;
  }

  public static ScreenConfig deserialize(Map<String, String> serialized) {
    return new ScreenConfig(
        getOrNull("playerSideSortVisibility", serialized, ButtonVisibility::fromId),
        getOrNull("playerSideTransferVisibility", serialized, ButtonVisibility::fromId),
        getOrNull("playerSideStackVisibility", serialized, ButtonVisibility::fromId),
        getOrNull("containerSideSortVisibility", serialized, ButtonVisibility::fromId),
        getOrNull("containerSideTransferVisibility", serialized, ButtonVisibility::fromId),
        getOrNull("containerSideStackVisibility", serialized, ButtonVisibility::fromId),
        getOrNull("playerSideOffset", serialized, Position::fromString),
        getOrNull("containerSideOffset", serialized, Position::fromString)
    );
  }

  private static <T> T getOrNull(
      String key, Map<String, String> serialized, Function<String, T> getter
  ) {
    String value = serialized.get(key);
    if (value == null) {
      return null;
    }
    return getter.apply(value);
  }
}
