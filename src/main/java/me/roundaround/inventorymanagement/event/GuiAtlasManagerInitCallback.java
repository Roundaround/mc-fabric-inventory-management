package me.roundaround.inventorymanagement.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;

import java.util.Arrays;

public interface GuiAtlasManagerInitCallback {
  Event<GuiAtlasManagerInitCallback> EVENT = EventFactory.createArrayBacked(
      GuiAtlasManagerInitCallback.class,
      (listeners) -> (client) -> Arrays.stream(listeners)
          .forEach((listener) -> listener.interact(client)));

  void interact(MinecraftClient client);
}
