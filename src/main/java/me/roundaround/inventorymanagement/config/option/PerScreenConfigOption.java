package me.roundaround.inventorymanagement.config.option;

import me.roundaround.inventorymanagement.config.value.PerScreenConfig;
import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.text.Text;

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
    return null;
  }

  @Override
  public void deserialize(Object data) {
    super.deserialize(data);
  }

  @Override
  public Object serialize() {
    return super.serialize();
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
