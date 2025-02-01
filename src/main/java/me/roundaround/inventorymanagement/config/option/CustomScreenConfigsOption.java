package me.roundaround.inventorymanagement.config.option;

import me.roundaround.inventorymanagement.config.value.ScreenOverrides;
import me.roundaround.roundalib.config.ConfigPath;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

public class CustomScreenConfigsOption extends ConfigOption<CustomScreenConfigsOption.Value> {
  protected CustomScreenConfigsOption(Builder builder) {
    super(builder);
  }

  public Optional<ScreenOverrides> getPlayer(ScreenHandler screenHandler) {
    return Optional.ofNullable(getValue().playerScreenHandler.get(screenHandler));
  }

  public Optional<ScreenOverrides> getPlayer(HandledScreen<?> handledScreen) {
    return Optional.ofNullable(getValue().playerHandledScreen.get(handledScreen));
  }

  public Optional<ScreenOverrides> getContainer(ScreenHandler screenHandler) {
    return Optional.ofNullable(getValue().containerScreenHandler.get(screenHandler));
  }

  public Optional<ScreenOverrides> getContainer(HandledScreen<?> handledScreen) {
    return Optional.ofNullable(getValue().containerHandledScreen.get(handledScreen));
  }

  public void modifyPlayer(ScreenHandler screenHandler, Function<ScreenOverrides, ScreenOverrides> modifyHandler) {
    ScreenOverrides overrides = this.getPlayer(screenHandler).orElseGet(ScreenOverrides::new);
    overrides = modifyHandler.apply(overrides);
    Value value = this.getValue();
    value.playerScreenHandler.put(screenHandler, overrides);
    this.setValue(value);
  }

  public void modifyPlayer(HandledScreen<?> handledScreen, Function<ScreenOverrides, ScreenOverrides> modifyHandler) {
    ScreenOverrides overrides = this.getPlayer(handledScreen).orElseGet(ScreenOverrides::new);
    overrides = modifyHandler.apply(overrides);
    Value value = this.getValue();
    value.playerHandledScreen.put(handledScreen, overrides);
    this.setValue(value);
  }

  public void modifyContainer(ScreenHandler screenHandler, Function<ScreenOverrides, ScreenOverrides> modifyHandler) {
    ScreenOverrides overrides = this.getContainer(screenHandler).orElseGet(ScreenOverrides::new);
    overrides = modifyHandler.apply(overrides);
    Value value = this.getValue();
    value.containerScreenHandler.put(screenHandler, overrides);
    this.setValue(value);
  }

  public void modifyContainer(
      HandledScreen<?> handledScreen, Function<ScreenOverrides, ScreenOverrides> modifyHandler
  ) {
    ScreenOverrides overrides = this.getContainer(handledScreen).orElseGet(ScreenOverrides::new);
    overrides = modifyHandler.apply(overrides);
    Value value = this.getValue();
    value.containerHandledScreen.put(handledScreen, overrides);
    this.setValue(value);
  }

  public static Builder builder(ConfigPath path) {
    return new Builder(path);
  }

  public static class Builder extends ConfigOption.AbstractBuilder<CustomScreenConfigsOption.Value,
      CustomScreenConfigsOption, Builder> {
    private Builder(ConfigPath path) {
      super(path);
    }

    @Override
    public CustomScreenConfigsOption buildInternal() {
      return new CustomScreenConfigsOption(this);
    }
  }

  public static class Value {
    private final HashMap<HandledScreen<?>, ScreenOverrides> playerHandledScreen = new HashMap<>();
    private final HashMap<ScreenHandler, ScreenOverrides> playerScreenHandler = new HashMap<>();
    private final HashMap<HandledScreen<?>, ScreenOverrides> containerHandledScreen = new HashMap<>();
    private final HashMap<ScreenHandler, ScreenOverrides> containerScreenHandler = new HashMap<>();
  }
}
