package me.roundaround.inventorymanagement.api.gui.positioning;

import me.roundaround.inventorymanagement.api.gui.ButtonContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;

import java.util.function.Function;

public class BackgroundPositionReference<H extends ScreenHandler, S extends HandledScreen<H>> extends
    BasePositionReference<H, S> {
  private BackgroundPositionReference(Function<ButtonContext<H, S>, Integer> function) {
    super(function);
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> BackgroundPositionReference<H, S> left() {
    return new BackgroundPositionReference<>((context) -> context.getScreenAccessor().getX());
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> BackgroundPositionReference<H, S> right() {
    return new BackgroundPositionReference<>(
        (context) -> context.getScreenAccessor().getX() + context.getScreenAccessor().getBackgroundWidth());
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> BackgroundPositionReference<H, S> top() {
    return new BackgroundPositionReference<>((context) -> context.getScreenAccessor().getY());
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> BackgroundPositionReference<H, S> bottom() {
    return new BackgroundPositionReference<>(
        (context) -> context.getScreenAccessor().getY() + context.getScreenAccessor().getBackgroundHeight());
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> BackgroundPositionReference<H, S> centerX() {
    return new BackgroundPositionReference<>(
        (context) -> context.getScreenAccessor().getX() + context.getScreenAccessor().getBackgroundWidth() / 2);
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> BackgroundPositionReference<H, S> centerY() {
    return new BackgroundPositionReference<>(
        (context) -> context.getScreenAccessor().getY() + context.getScreenAccessor().getBackgroundHeight() / 2);
  }
}
