package me.roundaround.inventorymanagement.api.gui.positioning;

import me.roundaround.inventorymanagement.api.gui.ButtonContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;

import java.util.function.Function;

public class ScreenPositionReference<H extends ScreenHandler, S extends HandledScreen<H>> extends BasePositionReference<H, S> {
  private ScreenPositionReference(Function<ButtonContext<H, S>, Integer> function) {
    super(function);
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> ScreenPositionReference<H, S> left() {
    return new ScreenPositionReference<>((context) -> 0);
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> ScreenPositionReference<H, S> right() {
    return new ScreenPositionReference<>((context) -> context.getScreen().width);
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> ScreenPositionReference<H, S> top() {
    return new ScreenPositionReference<>((context) -> 0);
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> ScreenPositionReference<H, S> bottom() {
    return new ScreenPositionReference<>((context) -> context.getScreen().height);
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> ScreenPositionReference<H, S> centerX() {
    return new ScreenPositionReference<>((context) -> context.getScreen().width / 2);
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> ScreenPositionReference<H, S> centerY() {
    return new ScreenPositionReference<>((context) -> context.getScreen().height / 2);
  }
}
