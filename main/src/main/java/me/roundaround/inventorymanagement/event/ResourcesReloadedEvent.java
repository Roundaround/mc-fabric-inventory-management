package me.roundaround.inventorymanagement.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;

import java.util.Arrays;

@FunctionalInterface
public interface ResourcesReloadedEvent {
  Event<ResourcesReloadedEvent> EVENT = EventFactory.createArrayBacked(
      ResourcesReloadedEvent.class,
      (handlers) -> (client) -> Arrays.stream(handlers).forEach((handler) -> handler.handle(client))
  );

  void handle(MinecraftClient client);
}
