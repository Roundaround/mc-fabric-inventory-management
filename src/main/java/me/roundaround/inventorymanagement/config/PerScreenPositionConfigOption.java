package me.roundaround.inventorymanagement.config;

import me.roundaround.roundalib.config.ConfigPath;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.config.value.Position;
import me.roundaround.roundalib.nightconfig.core.Config;
import net.minecraft.client.gui.screen.Screen;

import java.util.HashMap;
import java.util.List;
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
  @SuppressWarnings("unchecked")
  public void deserialize(Object data) {
    Config config = (Config) data;
    Map<String, Position> deserialized = new HashMap<>();

    config.valueMap().forEach((key, value) -> {
      if (value instanceof List<?> listValue) {
        deserialized.put(key, Position.fromList((List<Integer>) listValue));
      } else {
        deserialized.put(key, Position.fromString((String) value));
      }
    });

    Map<String, Position> defaultValue = this.getDefaultValue();
    for (String key : defaultValue.keySet()) {
      deserialized.putIfAbsent(key, defaultValue.get(key));
    }

    this.setValue(deserialized);
  }

  @Override
  public Object serialize() {
    Config serialized = Config.inMemory();
    this.getPendingValue().forEach((key, value) -> {
      serialized.set(key, List.of(value.x(), value.y()));
    });
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
