package me.roundaround.inventorymanagement.api.gui.positioning;

import me.roundaround.inventorymanagement.api.gui.ButtonContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;

@FunctionalInterface
public interface PositionReference<H extends ScreenHandler, S extends HandledScreen<H>> {
  int get(ButtonContext<H, S> context);
}
