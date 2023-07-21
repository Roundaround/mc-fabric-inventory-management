package me.roundaround.inventorymanagement.config.value;

import me.roundaround.roundalib.config.value.Position;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class PerScreenConfig extends HashMap<String, PerScreenConfig.ScreenConfig> {
  public PerScreenConfig clear(String key) {
    this.remove(key);
    return this;
  }

  public ButtonVisibility getPlayerSideSortVisibility(String key) {
    return this.getValue(key, ScreenConfig::getPlayerSideSortVisibility);
  }

  public ButtonVisibility getPlayerSideTransferVisibility(String key) {
    return this.getValue(key, ScreenConfig::getPlayerSideTransferVisibility);
  }

  public ButtonVisibility getPlayerSideStackVisibility(String key) {
    return this.getValue(key, ScreenConfig::getPlayerSideStackVisibility);
  }

  public ButtonVisibility getContainerSideSortVisibility(String key) {
    return this.getValue(key, ScreenConfig::getContainerSideSortVisibility);
  }

  public ButtonVisibility getContainerSideTransferVisibility(String key) {
    return this.getValue(key, ScreenConfig::getContainerSideTransferVisibility);
  }

  public ButtonVisibility getContainerSideStackVisibility(String key) {
    return this.getValue(key, ScreenConfig::getContainerSideStackVisibility);
  }

  public Position getPlayerSideOffset(String key) {
    return this.getValue(key, ScreenConfig::getPlayerSideOffset);
  }

  public Position getContainerSideOffset(String key) {
    return this.getValue(key, ScreenConfig::getContainerSideOffset);
  }

  public PerScreenConfig setPlayerSideSortVisibility(String key, ButtonVisibility value) {
    return this.setValue(key, value, ScreenConfig::setPlayerSideSortVisibility);
  }

  public PerScreenConfig setPlayerSideTransferVisibility(String key, ButtonVisibility value) {
    return this.setValue(key, value, ScreenConfig::setPlayerSideTransferVisibility);
  }

  public PerScreenConfig setPlayerSideStackVisibility(String key, ButtonVisibility value) {
    return this.setValue(key, value, ScreenConfig::setPlayerSideStackVisibility);
  }

  public PerScreenConfig setContainerSideSortVisibility(String key, ButtonVisibility value) {
    return this.setValue(key, value, ScreenConfig::setContainerSideSortVisibility);
  }

  public PerScreenConfig setContainerSideTransferVisibility(String key, ButtonVisibility value) {
    return this.setValue(key, value, ScreenConfig::setContainerSideTransferVisibility);
  }

  public PerScreenConfig setContainerSideStackVisibility(String key, ButtonVisibility value) {
    return this.setValue(key, value, ScreenConfig::setContainerSideStackVisibility);
  }

  public PerScreenConfig setPlayerSideOffset(String key, Position value) {
    return this.setValue(key, value, ScreenConfig::setPlayerSideOffset);
  }

  public PerScreenConfig setContainerSideOffset(String key, Position value) {
    return this.setValue(key, value, ScreenConfig::setContainerSideOffset);
  }

  public PerScreenConfig clearPlayerSideSortVisibility(String key) {
    return this.clearValue(key, ScreenConfig::clearPlayerSideSortVisibility);
  }

  public PerScreenConfig clearPlayerSideTransferVisibility(String key) {
    return this.clearValue(key, ScreenConfig::clearPlayerSideTransferVisibility);
  }

  public PerScreenConfig clearPlayerSideStackVisibility(String key) {
    return this.clearValue(key, ScreenConfig::clearPlayerSideStackVisibility);
  }

  public PerScreenConfig clearContainerSideSortVisibility(String key) {
    return this.clearValue(key, ScreenConfig::clearContainerSideSortVisibility);
  }

  public PerScreenConfig clearContainerSideTransferVisibility(String key) {
    return this.clearValue(key, ScreenConfig::clearContainerSideTransferVisibility);
  }

  public PerScreenConfig clearContainerSideStackVisibility(String key) {
    return this.clearValue(key, ScreenConfig::clearContainerSideStackVisibility);
  }

  public PerScreenConfig clearPlayerSideOffset(String key) {
    return this.clearValue(key, ScreenConfig::clearPlayerSideOffset);
  }

  public PerScreenConfig clearContainerSideOffset(String key) {
    return this.clearValue(key, ScreenConfig::clearContainerSideOffset);
  }

  private <T> T getValue(String key, Function<ScreenConfig, T> getter) {
    ScreenConfig screenConfig = this.get(key);
    if (screenConfig == null) {
      return null;
    }
    return getter.apply(screenConfig);
  }

  private <T> PerScreenConfig setValue(String key, T value, BiConsumer<ScreenConfig, T> setter) {
    ScreenConfig screenConfig = this.get(key);
    if (screenConfig == null) {
      screenConfig = new ScreenConfig();
    }
    setter.accept(screenConfig, value);
    this.put(key, screenConfig);
    return this;
  }

  private PerScreenConfig clearValue(String key, Consumer<ScreenConfig> clearer) {
    ScreenConfig screenConfig = this.get(key);
    if (screenConfig == null) {
      return this;
    }
    clearer.accept(screenConfig);
    if (screenConfig.isEmpty()) {
      this.remove(key);
    } else {
      this.put(key, screenConfig);
    }
    return this;
  }

  public static HashMap<String, HashMap<String, String>> serialize(PerScreenConfig config) {
    HashMap<String, HashMap<String, String>> serialized = new HashMap<>();
    for (Entry<String, ScreenConfig> entry : config.entrySet()) {
      serialized.put(entry.getKey(), ScreenConfig.serialize(entry.getValue()));
    }
    return serialized;
  }

  public static PerScreenConfig deserialize(HashMap<String, HashMap<String, String>> serialized) {
    PerScreenConfig config = new PerScreenConfig();
    for (Entry<String, HashMap<String, String>> entry : serialized.entrySet()) {
      config.put(entry.getKey(), ScreenConfig.deserialize(entry.getValue()));
    }
    return config;
  }

  public static class ScreenConfig {
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
        Position containerSideOffset) {
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
          containerSideTransferVisibility == null && containerSideStackVisibility == null &&
          playerSideOffset == null && containerSideOffset == null;
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
        serialized.put("containerSideTransferVisibility",
            config.containerSideTransferVisibility.getId());
      }
      if (config.containerSideStackVisibility != null) {
        serialized.put("containerSideStackVisibility", config.containerSideStackVisibility.getId());
      }
      if (config.playerSideOffset != null) {
        serialized.put("playerSideOffset", Position.serialize(config.playerSideOffset));
      }
      if (config.containerSideOffset != null) {
        serialized.put("containerSideOffset", Position.serialize(config.containerSideOffset));
      }

      return serialized;
    }

    public static ScreenConfig deserialize(Map<String, String> serialized) {
      return new ScreenConfig(getOrNull("playerSideSortVisibility",
          serialized,
          ButtonVisibility::fromId),
          getOrNull("playerSideTransferVisibility", serialized, ButtonVisibility::fromId),
          getOrNull("playerSideStackVisibility", serialized, ButtonVisibility::fromId),
          getOrNull("containerSideSortVisibility", serialized, ButtonVisibility::fromId),
          getOrNull("containerSideTransferVisibility", serialized, ButtonVisibility::fromId),
          getOrNull("containerSideStackVisibility", serialized, ButtonVisibility::fromId),
          getOrNull("playerSideOffset", serialized, Position::deserialize),
          getOrNull("containerSideOffset", serialized, Position::deserialize));
    }

    private static <T> T getOrNull(
        String key, Map<String, String> serialized, Function<String, T> getter) {
      String value = serialized.get(key);
      if (value == null) {
        return null;
      }
      return getter.apply(value);
    }
  }
}
