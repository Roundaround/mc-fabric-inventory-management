package me.roundaround.inventorymanagement.api.positioning;

import me.roundaround.inventorymanagement.api.ButtonContext;
import me.roundaround.roundalib.client.gui.GuiUtil;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

import java.util.function.Function;

public class TitlePositionReference<H extends ScreenHandler, S extends HandledScreen<H>> extends BasePositionReference<H, S> {
  private TitlePositionReference(Function<ButtonContext<H, S>, Integer> function) {
    super(function);
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> TitlePositionReference<H, S> left() {
    return new TitlePositionReference<>((context) -> context.getScreenAccessor().getTitleX());
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> TitlePositionReference<H, S> right() {
    return new TitlePositionReference<>(
        (context) -> context.getScreenAccessor().getTitleX() + getWidth(context.getScreen().getTitle()));
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> TitlePositionReference<H, S> top() {
    return new TitlePositionReference<>((context) -> context.getScreenAccessor().getTitleY());
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> TitlePositionReference<H, S> bottom() {
    return new TitlePositionReference<>((context) -> context.getScreenAccessor().getTitleY() + getHeight());
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> TitlePositionReference<H, S> playerLeft() {
    return new TitlePositionReference<>((context) -> context.getScreenAccessor().getPlayerInventoryTitleX());
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> TitlePositionReference<H, S> playerRight() {
    return new TitlePositionReference<>((context) -> context.getScreenAccessor().getPlayerInventoryTitleX() +
        getWidth(context.getScreenAccessor().getPlayerInventoryTitle()));
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> TitlePositionReference<H, S> playerTop() {
    return new TitlePositionReference<>((context) -> context.getScreenAccessor().getPlayerInventoryTitleY());
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> TitlePositionReference<H, S> playerBottom() {
    return new TitlePositionReference<>(
        (context) -> context.getScreenAccessor().getPlayerInventoryTitleY() + getHeight());
  }

  private static int getWidth(Text text) {
    return GuiUtil.getClient().textRenderer.getWidth(text);
  }

  private static int getHeight() {
    return GuiUtil.getClient().textRenderer.fontHeight;
  }
}
