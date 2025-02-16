package me.roundaround.inventorymanagement.config;

import me.roundaround.roundalib.config.ConfigPath;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.config.value.Position;
import me.roundaround.roundalib.nightconfig.core.Config;
import net.minecraft.client.gui.screen.Screen;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public class PerScreenPositionConfigOption extends ConfigOption<Map<String, Position>> {

  public PerScreenPositionConfigOption(Builder builder) {
    super(builder);
  }

  public void set(Screen screen, boolean isPlayerInventory, Position value) {
    this.update(screen, isPlayerInventory, (map, key) -> map.put(key, value));
  }

  public void remove(Screen screen, boolean isPlayerInventory) {
    this.update(screen, isPlayerInventory, Map::remove);
  }

  private void update(Screen screen, boolean isPlayerInventory, BiConsumer<Map<String, Position>, String> updater) {
    Map<String, Position> value = new HashMap<>(this.getPendingValue());
    updater.accept(value, getScreenKey(screen, isPlayerInventory));
    this.setValue(value);
  }

  public Optional<Position> get(Screen screen, boolean isPlayerInventory) {
    Map<String, Position> value = this.getPendingValue();
    String key = getScreenKey(screen, isPlayerInventory);
    if (value.containsKey(key)) {
      return Optional.of(value.get(key));
    }
    return Optional.empty();
  }

  @Override
  public void deserialize(Object data) {
    Config deserialized = (Config) data;
    Map<String, Object> deserializedMap = deserialized.valueMap();

    Map<String, Position> value = new HashMap<>();

    for (String key : deserializedMap.keySet()) {
      value.put(key, Position.fromString(deserialized.get(key)));
    }

    Map<String, Position> defaultValue = this.getDefaultValue();
    for (String key : defaultValue.keySet()) {
      value.putIfAbsent(key, defaultValue.get(key));
    }

    this.setValue(value);
  }

  @Override
  public Object serialize() {
    Config serialized = Config.inMemory();
    Map<String, Position> value = this.getValue();

    for (String key : value.keySet()) {
      serialized.set(key, value.get(key).toString());
    }

    return serialized;
  }

  public static String getScreenKey(Screen screen, boolean isPlayerInventory) {
    return screen.getClass().getName().replaceAll("\\.", "-") + (isPlayerInventory ? "-player" : "-container");
  }

  public static Builder builder(ConfigPath path) {
    return new Builder(path);
  }

  public static class Builder extends ConfigOption.AbstractBuilder<Map<String, Position>,
      PerScreenPositionConfigOption, Builder> {
    private Builder(ConfigPath path) {
      super(path);
      this.setDefaultValue(new HashMap<>());
    }

    public Builder addDefaultEntry(String key, Position position) {
      this.defaultValue.put(key, position);
      return this;
    }

    @Override
    public PerScreenPositionConfigOption buildInternal() {
      return new PerScreenPositionConfigOption(this);
    }
  }
}
