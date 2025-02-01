package me.roundaround.inventorymanagement.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.screen.ScreenHandler;

import java.util.Arrays;

@FunctionalInterface
public interface BeforeCloseHandledScreen {
  Event<BeforeCloseHandledScreen> EVENT = EventFactory.createArrayBacked(BeforeCloseHandledScreen.class,
      (handlers) -> (player, screenHandler) -> Arrays.stream(handlers)
          .forEach((handler) -> handler.handle(player, screenHandler))
  );

  void handle(ClientPlayerEntity player, ScreenHandler screenHandler);
}
