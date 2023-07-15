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

  public void setPlayerSideSortVisibility(Screen screen, ButtonVisibility value) {
    this.setValue(this.getValue().setPlayerSideSortVisibility(getScreenKey(screen), value));
  }

  public void clearPlayerSideSortVisibility(Screen screen) {
    this.setValue(this.getValue().clearPlayerSideSortVisibility(getScreenKey(screen)));
  }

  public void setPlayerSideTransferVisibility(Screen screen, ButtonVisibility value) {
    this.setValue(this.getValue().setPlayerSideTransferVisibility(getScreenKey(screen), value));
  }

  public void clearPlayerSideTransferVisibility(Screen screen) {
    this.setValue(this.getValue().clearPlayerSideTransferVisibility(getScreenKey(screen)));
  }

  public void setContainerSideSortVisibility(Screen screen, ButtonVisibility value) {
    this.setValue(this.getValue().setContainerSideSortVisibility(getScreenKey(screen), value));
  }

  public void clearContainerSideSortVisibility(Screen screen) {
    this.setValue(this.getValue().clearContainerSideSortVisibility(getScreenKey(screen)));
  }

  public void setContainerSideTransferVisibility(Screen screen, ButtonVisibility value) {
    this.setValue(this.getValue().setContainerSideTransferVisibility(getScreenKey(screen), value));
  }

  public void clearContainerSideTransferVisibility(Screen screen) {
    this.setValue(this.getValue().clearContainerSideTransferVisibility(getScreenKey(screen)));
  }

  public void setPlayerSideOffset(Screen screen, Position position) {
    getValue().setPlayerSideOffset(getScreenKey(screen), position);
  }

  public void clearPlayerSideOffset(Screen screen) {
    this.setValue(this.getValue().clearPlayerSideOffset(getScreenKey(screen)));
  }

  public void setContainerSideOffset(Screen screen, Position position) {
    getValue().setContainerSideOffset(getScreenKey(screen), position);
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
    Config deserialized = (Config) data;
    Map<String, Object> deserializedMap = deserialized.valueMap();

    PerScreenConfig value = new PerScreenConfig();

    for (String key : deserializedMap.keySet()) {
      value.put(key, PerScreenConfig.ScreenConfig.deserialize(deserialized.get(key)));
    }

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
      serialized.set(key, value.get(key));
    }

    return serialized;
  }

  public static class Builder extends ConfigOption.AbstractBuilder<PerScreenConfig, Builder> {
    public Builder(ModConfig config, String id, Text label, PerScreenConfig defaultValue) {
      super(config, id, label, defaultValue);
    }

    @Override
    public PerScreenConfigOption build() {
      return new PerScreenConfigOption(this);
    }
  }
}
