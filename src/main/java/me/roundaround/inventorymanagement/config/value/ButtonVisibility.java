package me.roundaround.inventorymanagement.config.value;

import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.value.ListOptionValue;

import java.util.Arrays;

public enum ButtonVisibility implements ListOptionValue<ButtonVisibility> {
  DEFAULT,
  SHOW,
  HIDE;

  private final String id;

  ButtonVisibility() {
    this.id = this.name().toLowerCase();
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public String getI18nKey(ModConfig config) {
    return config.getModId() + ".button_visibility." + this.id;
  }

  @Override
  public ButtonVisibility getFromId(String id) {
    return fromId(id);
  }

  @Override
  public String toString() {
    return this.id;
  }

  @Override
  public ButtonVisibility getNext() {
    return values()[(this.ordinal() + 1) % values().length];
  }

  @Override
  public ButtonVisibility getPrev() {
    return values()[(this.ordinal() + values().length - 1) % values().length];
  }

  public static ButtonVisibility getDefault() {
    return DEFAULT;
  }

  public static ButtonVisibility fromId(String id) {
    return Arrays.stream(ButtonVisibility.values())
        .filter(value -> value.getId().equals(id))
        .findFirst()
        .orElse(getDefault());
  }
}
