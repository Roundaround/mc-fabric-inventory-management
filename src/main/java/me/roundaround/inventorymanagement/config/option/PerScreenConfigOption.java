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

  public ButtonVisibility getPlayerSideSortVisibility(Screen screen) {
    return this.getValue().getPlayerSideSortVisibility(getScreenKey(screen));
  }

  public void setPlayerSideSortVisibility(Screen screen, ButtonVisibility value) {
    this.setValue(this.getValue().setPlayerSideSortVisibility(getScreenKey(screen), value));
  }

  public void clearPlayerSideSortVisibility(Screen screen) {
    this.setValue(this.getValue().clearPlayerSideSortVisibility(getScreenKey(screen)));
  }

  public ButtonVisibility getPlayerSideTransferVisibility(Screen screen) {
    return this.getValue().getPlayerSideTransferVisibility(getScreenKey(screen));
  }

  public void setPlayerSideTransferVisibility(Screen screen, ButtonVisibility value) {
    this.setValue(this.getValue().setPlayerSideTransferVisibility(getScreenKey(screen), value));
  }

  public void clearPlayerSideTransferVisibility(Screen screen) {
    this.setValue(this.getValue().clearPlayerSideTransferVisibility(getScreenKey(screen)));
  }

  public ButtonVisibility getContainerSideSortVisibility(Screen screen) {
    return this.getValue().getContainerSideSortVisibility(getScreenKey(screen));
  }

  public void setContainerSideSortVisibility(Screen screen, ButtonVisibility value) {
    this.setValue(this.getValue().setContainerSideSortVisibility(getScreenKey(screen), value));
  }

  public void clearContainerSideSortVisibility(Screen screen) {
    this.setValue(this.getValue().clearContainerSideSortVisibility(getScreenKey(screen)));
  }

  public ButtonVisibility getContainerSideTransferVisibility(Screen screen) {
    return this.getValue().getContainerSideTransferVisibility(getScreenKey(screen));
  }

  public void setContainerSideTransferVisibility(Screen screen, ButtonVisibility value) {
    this.setValue(this.getValue().setContainerSideTransferVisibility(getScreenKey(screen), value));
  }

  public void clearContainerSideTransferVisibility(Screen screen) {
    this.setValue(this.getValue().clearContainerSideTransferVisibility(getScreenKey(screen)));
  }

  public Position getPlayerSideOffset(Screen screen) {
    return this.getValue().getPlayerSideOffset(getScreenKey(screen));
  }

  public void setPlayerSideOffset(Screen screen, Position position) {
    this.setValue(this.getValue().setPlayerSideOffset(getScreenKey(screen), position));
  }

  public void clearPlayerSideOffset(Screen screen) {
    this.setValue(this.getValue().clearPlayerSideOffset(getScreenKey(screen)));
  }

  public Position getContainerSideOffset(Screen screen) {
    return this.getValue().getContainerSideOffset(getScreenKey(screen));
  }

  public void setContainerSideOffset(Screen screen, Position position) {
    this.setValue(this.getValue().setContainerSideOffset(getScreenKey(screen), position));
  }

  public void clearContainerSideOffset(Screen screen) {
    this.setValue(this.getValue().clearContainerSideOffset(getScreenKey(screen)));
  }

  public String getScreenKey(Screen screen) {
    return screen.getClass().getName().replaceAll("\\.", "-");
  }

  @Override
  public ConfigOption<PerScreenConfig, Builder> copy() {
    return new PerScreenConfigOption(this);
  }

  @Override
  public void deserialize(Object data) {
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

    PerScreenConfig defaultValue = getDefault();
    for (String key : defaultValue.keySet()) {
      value.putIfAbsent(key, defaultValue.get(key));
    }

    setValue(value);
  }

  @Override
  public Object serialize() {
    Config serialized = Config.inMemory();
    HashMap<String, HashMap<String, String>> value = PerScreenConfig.serialize(getValue());

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
}
