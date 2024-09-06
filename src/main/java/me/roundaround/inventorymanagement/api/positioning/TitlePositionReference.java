package me.roundaround.inventorymanagement.api.positioning;

import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import me.roundaround.roundalib.client.gui.GuiUtil;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.Text;

import java.util.function.BiFunction;

public class TitlePositionReference<S extends HandledScreen<?>> implements PositionReference<S> {
  private final BiFunction<S, HandledScreenAccessor, Integer> function;

  private TitlePositionReference(BiFunction<S, HandledScreenAccessor, Integer> function) {
    this.function = function;
  }

  @Override
  public int get(S screen, HandledScreenAccessor accessor) {
    return this.function.apply(screen, accessor);
  }

  public static <S extends HandledScreen<?>> TitlePositionReference<S> left() {
    return new TitlePositionReference<>((screen, accessor) -> accessor.getTitleX());
  }

  public static <S extends HandledScreen<?>> TitlePositionReference<S> right() {
    return new TitlePositionReference<>((screen, accessor) -> accessor.getTitleX() + getWidth(screen.getTitle()));
  }

  public static <S extends HandledScreen<?>> TitlePositionReference<S> top() {
    return new TitlePositionReference<>((screen, accessor) -> accessor.getTitleY());
  }

  public static <S extends HandledScreen<?>> TitlePositionReference<S> bottom() {
    return new TitlePositionReference<>((screen, accessor) -> accessor.getTitleY() + getHeight());
  }

  public static <S extends HandledScreen<?>> TitlePositionReference<S> playerLeft() {
    return new TitlePositionReference<>((screen, accessor) -> accessor.getPlayerInventoryTitleX());
  }

  public static <S extends HandledScreen<?>> TitlePositionReference<S> playerRight() {
    return new TitlePositionReference<>(
        (screen, accessor) -> accessor.getPlayerInventoryTitleX() + getWidth(accessor.getPlayerInventoryTitle()));
  }

  public static <S extends HandledScreen<?>> TitlePositionReference<S> playerTop() {
    return new TitlePositionReference<>((screen, accessor) -> accessor.getPlayerInventoryTitleY());
  }

  public static <S extends HandledScreen<?>> TitlePositionReference<S> playerBottom() {
    return new TitlePositionReference<>((screen, accessor) -> accessor.getPlayerInventoryTitleY() + getHeight());
  }

  private static int getWidth(Text text) {
    return GuiUtil.getClient().textRenderer.getWidth(text);
  }

  private static int getHeight() {
    return GuiUtil.getClient().textRenderer.fontHeight;
  }
}
