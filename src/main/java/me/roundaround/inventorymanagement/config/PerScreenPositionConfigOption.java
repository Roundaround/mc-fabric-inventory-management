package me.roundaround.inventorymanagement.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.config.value.Position;
import me.roundaround.roundalib.shadow.nightconfig.core.Config;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class PerScreenPositionConfigOption
    extends ConfigOption<Map<String, Position>, PerScreenPositionConfigOption.Builder> {

  public PerScreenPositionConfigOption(Builder builder) {
    super(builder);
  }

  private PerScreenPositionConfigOption(PerScreenPositionConfigOption other) {
    super(other);
  }

  public void set(Screen screen, Position value) {
    String key = getScreenKey(screen);
    getValue().put(key, value);
  }

  public Optional<Position> get(Screen screen) {
    Map<String, Position> value = getValue();
    String key = getScreenKey(screen);
    if (value.containsKey(key)) {
      return Optional.of(value.get(key));
    }
    return Optional.empty();
  }

  public String getScreenKey(Screen screen) {
    return screen.getClass().getName().replaceAll("\\.", "-");
  }

  @Override
  public void deserialize(Object data) {
    Config deserialized = (Config) data;
    Map<String, Object> deserializedMap = deserialized.valueMap();

    Map<String, Position> value = new HashMap<>();

    for (String key : deserializedMap.keySet()) {
      value.put(key, Position.deserialize(deserialized.get(key)));
    }

    Map<String, Position> defaultValue = getDefault();
    for (String key : defaultValue.keySet()) {
      value.putIfAbsent(key, defaultValue.get(key));
    }

    setValue(value);
  }

  @Override
  public Object serialize() {
    Config serialized = Config.inMemory();
    Map<String, Position> value = getValue();

    for (String key : value.keySet()) {
      serialized.set(key, Position.serialize(value.get(key)));
    }

    return serialized;
  }

  @Override
  public PerScreenPositionConfigOption copy() {
    return new PerScreenPositionConfigOption(this);
  }

  public static Builder builder(String id, String labelI18nKey) {
    return new Builder(id, labelI18nKey);
  }

  public static Builder builder(String id, Text label) {
    return new Builder(id, label);
  }

  public static class Builder extends ConfigOption.Builder<Map<String, Position>, Builder> {
    public Builder(String id, Text label) {
      super(id, label, new HashMap<>());
    }

    public Builder(String id, String labelI18nKey) {
      super(id, labelI18nKey, new HashMap<>());
    }

    public Builder addDefaultEntry(String key, Position position) {
      defaultValue.put(key, position);
      return this;
    }

    @Override
    public PerScreenPositionConfigOption build() {
      return new PerScreenPositionConfigOption(this);
    }
  }
}
