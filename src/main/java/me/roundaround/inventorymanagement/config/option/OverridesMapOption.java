package me.roundaround.inventorymanagement.config.option;

import me.roundaround.inventorymanagement.config.value.ButtonVisibility;
import me.roundaround.inventorymanagement.roundalib.client.gui.GuiUtil;
import me.roundaround.inventorymanagement.roundalib.config.ConfigPath;
import me.roundaround.inventorymanagement.roundalib.config.option.ConfigOption;
import me.roundaround.inventorymanagement.roundalib.config.value.Position;
import me.roundaround.inventorymanagement.roundalib.nightconfig.core.Config;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class OverridesMapOption extends ConfigOption<OverridesMapOption.OverridesMap> {
  public OverridesMapOption(Builder builder) {
    super(builder);
  }

  @Override
  public void deserialize(Object data) {
    // TODO: Implement
  }

  @Override
  public Object serialize() {
    // TODO: Implement
    return super.serialize();
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

  public static class OverridesMap extends HashMap<String, Overrides> {

  }

  public static class Overrides {
    private ButtonVisibility playerSort = ButtonVisibility.DEFAULT;
    private ButtonVisibility playerStack = ButtonVisibility.DEFAULT;
    private ButtonVisibility playerTransfer = ButtonVisibility.DEFAULT;
    private Position playerOffset = new Position(0, 0);
    private int playerSpacing = GuiUtil.PADDING;
    private ButtonVisibility containerSort = ButtonVisibility.DEFAULT;
    private ButtonVisibility containerStack = ButtonVisibility.DEFAULT;
    private ButtonVisibility containerTransfer = ButtonVisibility.DEFAULT;
    private Position containerOffset = new Position(0, 0);
    private int containerSpacing = GuiUtil.PADDING;
  }

  public static class Builder extends ConfigOption.AbstractBuilder<OverridesMap, OverridesMapOption, Builder> {
    private Builder(ConfigPath path) {
      super(path);
    }

    @Override
    public OverridesMapOption buildInternal() {
      return new OverridesMapOption(this);
    }
  }
}
