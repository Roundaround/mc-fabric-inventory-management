package me.roundaround.inventorymanagement.api.positioning;

import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

import java.util.function.BiFunction;

public class ScreenPositionReference<S extends HandledScreen<?>> implements PositionReference<S> {
  private final BiFunction<S, HandledScreenAccessor, Integer> function;

  private ScreenPositionReference(BiFunction<S, HandledScreenAccessor, Integer> function) {
    this.function = function;
  }

  @Override
  public int get(S screen, HandledScreenAccessor accessor) {
    return this.function.apply(screen, accessor);
  }

  public static <S extends HandledScreen<?>> ScreenPositionReference<S> left() {
    return new ScreenPositionReference<>((screen, accessor) -> 0);
  }

  public static <S extends HandledScreen<?>> ScreenPositionReference<S> right() {
    return new ScreenPositionReference<>((screen, accessor) -> screen.width);
  }

  public static <S extends HandledScreen<?>> ScreenPositionReference<S> top() {
    return new ScreenPositionReference<>((screen, accessor) -> 0);
  }

  public static <S extends HandledScreen<?>> ScreenPositionReference<S> bottom() {
    return new ScreenPositionReference<>((screen, accessor) -> screen.height);
  }
}
