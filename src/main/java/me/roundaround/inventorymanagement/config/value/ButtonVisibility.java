package me.roundaround.inventorymanagement.config.value;

import me.roundaround.roundalib.config.value.EnumValue;

import java.util.Arrays;
import java.util.Optional;

public enum ButtonVisibility implements EnumValue<ButtonVisibility> {
  DEFAULT, SHOW, HIDE;

  private final String id;

  ButtonVisibility() {
    this.id = this.name().toLowerCase();
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public String getI18nKey(String modId) {
    return String.format("%s.buttonVisibility.%s", modId, this.id);
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

  public static ButtonVisibility of(Boolean visible) {
    if (visible == null) {
      return DEFAULT;
    }
    return visible ? SHOW : HIDE;
  }

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  public static ButtonVisibility of(Optional<Boolean> optVisible) {
    return optVisible.map((visible) -> visible ? SHOW : HIDE).orElse(DEFAULT);
  }

  public static ButtonVisibility fromId(String id) {
    return Arrays.stream(ButtonVisibility.values())
        .filter(value -> value.getId().equals(id))
        .findFirst()
        .orElse(getDefault());
  }
}
