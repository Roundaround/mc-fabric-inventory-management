package me.roundaround.inventorymanagement.api.positioning;

import me.roundaround.inventorymanagement.api.ButtonContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;

import java.util.function.Function;

public abstract class BasePositionReference<H extends ScreenHandler, S extends HandledScreen<H>> implements
    PositionReference<H, S> {
  private final Function<ButtonContext<H, S>, Integer> function;

  protected BasePositionReference(Function<ButtonContext<H, S>, Integer> function) {
    this.function = function;
  }

  @Override
  public int get(ButtonContext<H, S> context) {
    return this.function.apply(context);
  }
}
