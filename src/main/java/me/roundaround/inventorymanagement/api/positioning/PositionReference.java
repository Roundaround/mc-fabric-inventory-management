package me.roundaround.inventorymanagement.api.positioning;

import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

@FunctionalInterface
public interface PositionReference<S extends HandledScreen<?>> {
  int get(S screen, HandledScreenAccessor accessor);
}
