package me.roundaround.inventorymanagement.api.positioning;

import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

import java.util.function.BiFunction;

public class BackgroundPositionReference<S extends HandledScreen<?>> implements PositionReference<S> {
  private final BiFunction<S, HandledScreenAccessor, Integer> function;

  private BackgroundPositionReference(BiFunction<S, HandledScreenAccessor, Integer> function) {
    this.function = function;
  }

  @Override
  public int get(S screen, HandledScreenAccessor accessor) {
    return this.function.apply(screen, accessor);
  }

  public static <S extends HandledScreen<?>> BackgroundPositionReference<S> left() {
    return new BackgroundPositionReference<>((screen, accessor) -> accessor.getX());
  }

  public static <S extends HandledScreen<?>> BackgroundPositionReference<S> right() {
    return new BackgroundPositionReference<>((screen, accessor) -> accessor.getX() + accessor.getBackgroundWidth());
  }

  public static <S extends HandledScreen<?>> BackgroundPositionReference<S> top() {
    return new BackgroundPositionReference<>((screen, accessor) -> accessor.getY());
  }

  public static <S extends HandledScreen<?>> BackgroundPositionReference<S> bottom() {
    return new BackgroundPositionReference<>((screen, accessor) -> accessor.getY() + accessor.getBackgroundHeight());
  }
}
