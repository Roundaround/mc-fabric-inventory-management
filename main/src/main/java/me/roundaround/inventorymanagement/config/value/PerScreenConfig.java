package me.roundaround.inventorymanagement.config.value;

import me.roundaround.roundalib.config.value.Position;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class PerScreenConfig extends HashMap<String, ScreenConfig> {
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
}
