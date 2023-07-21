package me.roundaround.inventorymanagement.config.option;

import me.roundaround.inventorymanagement.config.value.ButtonVisibility;
import me.roundaround.inventorymanagement.config.value.PerScreenConfig;
import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.config.value.Position;
import me.roundaround.roundalib.shadow.nightconfig.core.Config;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class PerScreenConfigOption
    extends ConfigOption<PerScreenConfig, PerScreenConfigOption.Builder> {

  protected PerScreenConfigOption(Builder builder) {
    super(builder);
  }

  protected PerScreenConfigOption(PerScreenConfigOption other) {
    super(other);
  }

  public void clear(Screen screen) {
    this.setValue(this.getValue().clear(getScreenKey(screen)));
  }

  public ButtonVisibility getSortVisibility(Screen screen, boolean isPlayerInventory) {
    return this.getValue(screen,
        isPlayerInventory,
        PerScreenConfig::getPlayerSideSortVisibility,
        PerScreenConfig::getContainerSideSortVisibility);
  }

  public ButtonVisibility getTransferVisibility(Screen screen, boolean isPlayerInventory) {
    return this.getValue(screen,
        isPlayerInventory,
        PerScreenConfig::getPlayerSideTransferVisibility,
        PerScreenConfig::getContainerSideTransferVisibility);
  }

  public ButtonVisibility getStackVisibility(Screen screen, boolean isPlayerInventory) {
    return this.getValue(screen,
        isPlayerInventory,
        PerScreenConfig::getPlayerSideStackVisibility,
        PerScreenConfig::getContainerSideStackVisibility);
  }

  public Position getPosition(Screen screen, boolean isPlayerInventory) {
    return this.getValue(screen,
        isPlayerInventory,
        PerScreenConfig::getPlayerSideOffset,
        PerScreenConfig::getContainerSideOffset);
  }

  public void setSortVisibility(Screen screen, boolean isPlayerInventory, ButtonVisibility value) {
    this.setValue(screen,
        isPlayerInventory,
        PerScreenConfig::setPlayerSideSortVisibility,
        PerScreenConfig::setContainerSideSortVisibility,
        value);
  }

  public void setTransferVisibility(
      Screen screen, boolean isPlayerInventory, ButtonVisibility value) {
    this.setValue(screen,
        isPlayerInventory,
        PerScreenConfig::setPlayerSideTransferVisibility,
        PerScreenConfig::setContainerSideTransferVisibility,
        value);
  }

  public void setStackVisibility(Screen screen, boolean isPlayerInventory, ButtonVisibility value) {
    this.setValue(screen,
        isPlayerInventory,
        PerScreenConfig::setPlayerSideStackVisibility,
        PerScreenConfig::setContainerSideStackVisibility,
        value);
  }

  public void setPosition(Screen screen, boolean isPlayerInventory, Position value) {
    this.setValue(screen,
        isPlayerInventory,
        PerScreenConfig::setPlayerSideOffset,
        PerScreenConfig::setContainerSideOffset,
        value);
  }

  public void clearSortVisibility(Screen screen, boolean isPlayerInventory) {
    this.clearValue(screen,
        isPlayerInventory,
        PerScreenConfig::clearPlayerSideSortVisibility,
        PerScreenConfig::clearContainerSideSortVisibility);
  }

  public void clearTransferVisibility(Screen screen, boolean isPlayerInventory) {
    this.clearValue(screen,
        isPlayerInventory,
        PerScreenConfig::clearPlayerSideTransferVisibility,
        PerScreenConfig::clearContainerSideTransferVisibility);
  }

  public void clearStackVisibility(Screen screen, boolean isPlayerInventory) {
    this.clearValue(screen,
        isPlayerInventory,
        PerScreenConfig::clearPlayerSideStackVisibility,
        PerScreenConfig::clearContainerSideStackVisibility);
  }

  public void clearPosition(Screen screen, boolean isPlayerInventory) {
    this.clearValue(screen,
        isPlayerInventory,
        PerScreenConfig::clearPlayerSideOffset,
        PerScreenConfig::clearContainerSideOffset);
  }

  public String getScreenKey(Screen screen) {
    return screen.getClass().getName().replaceAll("\\.", "-");
  }

  private <T> T getValue(
      Screen screen,
      boolean isPlayerInventory,
      BiFunction<PerScreenConfig, String, T> getPlayerSide,
      BiFunction<PerScreenConfig, String, T> getContainerSide) {
    PerScreenConfig config = this.getValue();
    String key = getScreenKey(screen);
    return isPlayerInventory
        ? getPlayerSide.apply(config, key)
        : getContainerSide.apply(config, key);
  }

  private <T> void setValue(
      Screen screen,
      boolean isPlayerInventory,
      TriFunction<PerScreenConfig, String, T, PerScreenConfig> setPlayerSide,
      TriFunction<PerScreenConfig, String, T, PerScreenConfig> setContainerSide,
      T value) {
    PerScreenConfig config = this.getValue();
    String key = getScreenKey(screen);
    if (isPlayerInventory) {
      this.setValue(setPlayerSide.apply(config, key, value));
    } else {
      this.setValue(setContainerSide.apply(config, key, value));
    }
  }

  private void clearValue(
      Screen screen,
      boolean isPlayerInventory,
      BiFunction<PerScreenConfig, String, PerScreenConfig> clearPlayerSide,
      BiFunction<PerScreenConfig, String, PerScreenConfig> clearContainerSide) {
    PerScreenConfig config = this.getValue();
    String key = getScreenKey(screen);
    if (isPlayerInventory) {
      this.setValue(clearPlayerSide.apply(config, key));
    } else {
      this.setValue(clearContainerSide.apply(config, key));
    }
  }

  @Override
  public ConfigOption<PerScreenConfig, Builder> copy() {
    return new PerScreenConfigOption(this);
  }

  @Override
  public void deserialize(Object data) {
    setValue(PerScreenConfigOption.deserialize(data, getDefault()));
  }

  @Override
  public Object serialize() {
    return PerScreenConfigOption.serialize(getValue());
  }

  public static PerScreenConfig deserialize(Object data, PerScreenConfig defaultValue) {
    Config rawValue = (Config) data;
    Map<String, Object> rawMap = rawValue.valueMap();

    HashMap<String, HashMap<String, String>> deserializedMap = new HashMap<>();

    for (String key : rawMap.keySet()) {
      Config subMap = rawValue.get(key);
      HashMap<String, String> deserializedSubMap = new HashMap<>();
      for (String subKey : subMap.valueMap().keySet()) {
        deserializedSubMap.put(subKey, subMap.get(subKey).toString());
      }
      deserializedMap.put(key, deserializedSubMap);
    }

    PerScreenConfig value = PerScreenConfig.deserialize(deserializedMap);

    for (String key : defaultValue.keySet()) {
      value.putIfAbsent(key, defaultValue.get(key));
    }

    return value;
  }

  public static Object serialize(PerScreenConfig perScreenConfig) {
    Config serialized = Config.inMemory();
    HashMap<String, HashMap<String, String>> value = PerScreenConfig.serialize(perScreenConfig);

    for (String key : value.keySet()) {
      Config forScreen = Config.inMemory();
      for (String subKey : value.get(key).keySet()) {
        forScreen.set(subKey, value.get(key).get(subKey));
      }
      serialized.set(key, forScreen);
    }

    return serialized;
  }

  public static Builder builder(ModConfig config, String id, String labelI18nKey) {
    return builder(config, id, Text.translatable(labelI18nKey));
  }

  public static Builder builder(ModConfig config, String id, Text label) {
    return new Builder(config, id, label);
  }

  public static class Builder extends ConfigOption.AbstractBuilder<PerScreenConfig, Builder> {
    public Builder(ModConfig config, String id, Text label) {
      super(config, id, label, new PerScreenConfig());
    }

    @Override
    public PerScreenConfigOption build() {
      return new PerScreenConfigOption(this);
    }
  }

  @FunctionalInterface
  interface TriFunction<T, U, V, R> {
    R apply(T t, U u, V v);
  }
}
