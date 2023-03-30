package me.roundaround.inventorymanagement.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screen.Screen;

import java.util.Arrays;

public interface HandleScreenInputCallback {
  Event<HandleScreenInputCallback> EVENT = EventFactory.createArrayBacked(HandleScreenInputCallback.class,
      (listeners) -> (screen, keyCode, scanCode, modifiers) -> Arrays
          .stream(listeners)
          .anyMatch((listener) -> listener
              .interact(screen, keyCode, scanCode, modifiers)));

  boolean interact(Screen screen, int keyCode, int scanCode, int modifiers);
}
