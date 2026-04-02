package me.roundaround.inventorymanagement.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import java.util.Arrays;

public interface HandleScreenInputCallback {
  Event<HandleScreenInputCallback> EVENT = EventFactory.createArrayBacked(
      HandleScreenInputCallback.class,
      (listeners) -> (screen, input) -> Arrays.stream(listeners)
          .anyMatch((listener) -> listener.interact(screen, input))
  );

  boolean interact(Screen screen, KeyEvent input);
}
