package me.roundaround.inventorymanagement.config.option;

import me.roundaround.inventorymanagement.config.value.ButtonVisibility;
import me.roundaround.inventorymanagement.roundalib.client.gui.GuiUtil;
import me.roundaround.inventorymanagement.roundalib.config.ConfigPath;
import me.roundaround.inventorymanagement.roundalib.config.option.ConfigOption;
import me.roundaround.inventorymanagement.roundalib.config.value.Position;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenHandler;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class OverridesMapOption extends ConfigOption<Map<String, OverridesMapOption.Overrides>> {
  public OverridesMapOption(Builder builder) {
    super(builder);
  }

  public Optional<Overrides> get(ScreenHandler screenHandler) {
    return this.get(getKey(screenHandler));
  }

  public Optional<Overrides> get(Screen screen) {
    return this.get(getKey(screen));
  }

  private Optional<Overrides> get(String key) {
    return Optional.ofNullable(this.getValue().get(key));
  }

  public void modify(ScreenHandler screenHandler, Function<Overrides, Overrides> modifyHandler) {
    this.modify(getKey(screenHandler), modifyHandler);
  }

  public void modify(Screen screen, Function<Overrides, Overrides> modifyHandler) {
    this.modify(getKey(screen), modifyHandler);
  }

  private void modify(String key, Function<Overrides, Overrides> modifyHandler) {
    Overrides overrides = this.get(key).orElseGet(Overrides::new);
    overrides = modifyHandler.apply(overrides);
    var value = this.getValue();
    value.put(key, overrides);
    this.setValue(value);
  }

  private static String getKey(Screen screen) {
    return getKey(screen.getClass());
  }

  private static String getKey(ScreenHandler screenHandler) {
    return getKey(screenHandler.getClass());
  }

  private static String getKey(Class<?> clazz) {
    MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
    String unmapped = mappingResolver.unmapClassName("named", clazz.getName());
    return unmapped.replaceAll("\\.", "-");
  }

  public static class Overrides {
    private SidedOverrides player = new SidedOverrides();
    private SidedOverrides container = new SidedOverrides();
  }

  public static class SidedOverrides {
    private ButtonVisibility sortVisibility = ButtonVisibility.DEFAULT;
    private ButtonVisibility stackVisibility = ButtonVisibility.DEFAULT;
    private ButtonVisibility transferVisibility = ButtonVisibility.DEFAULT;
    private Position offset = new Position(0, 0);
    private int spacing = GuiUtil.PADDING;
  }

  public static class Builder extends ConfigOption.AbstractBuilder<Map<String, Overrides>, OverridesMapOption,
      Builder> {
    private Builder(ConfigPath path) {
      super(path);
    }

    @Override
    public OverridesMapOption buildInternal() {
      return new OverridesMapOption(this);
    }
  }
}
