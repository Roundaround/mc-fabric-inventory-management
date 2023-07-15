package me.roundaround.inventorymanagement.config.option;

import me.roundaround.inventorymanagement.config.value.PerScreenConfig;
import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.shadow.nightconfig.core.Config;
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
