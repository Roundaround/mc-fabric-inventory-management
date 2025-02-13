package me.roundaround.inventorymanagement.server.network;

import me.roundaround.inventorymanagement.network.Networking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

import static me.roundaround.inventorymanagement.server.inventory.ServerInventoryHelper.*;

public final class ServerNetworking {
  private ServerNetworking() {
  }

  public static void registerReceivers() {
    ServerPlayNetworking.registerGlobalReceiver(Networking.StackC2S.ID, ServerNetworking::handleStack);
    ServerPlayNetworking.registerGlobalReceiver(Networking.SortC2S.ID, ServerNetworking::handleSort);
    ServerPlayNetworking.registerGlobalReceiver(Networking.SortAllC2S.ID, ServerNetworking::handleSortAll);
    ServerPlayNetworking.registerGlobalReceiver(Networking.TransferC2S.ID, ServerNetworking::handleTransfer);
  }

  public static void sendErrorAlert(PlayerEntity player, String message) {
    // TODO: Implement
  }

  public static void sendCheaterAlert(PlayerEntity player, String message) {
    // TODO: Implement
  }

  private static void handleStack(Networking.StackC2S payload, ServerPlayNetworking.Context context) {
    context.player().server.execute(() -> autoStack(context.player(), payload.fromPlayerInventory(), payload.locked()));
  }

  private static void handleSort(Networking.SortC2S payload, ServerPlayNetworking.Context context) {
    context.player().server.execute(() -> applySort(context.player(),
        payload.isPlayerInventory(),
        payload.sorted(),
        payload.locked()
    ));
  }

  private static void handleSortAll(Networking.SortAllC2S payload, ServerPlayNetworking.Context context) {
    context.player().server.execute(() -> {
      applySort(context.player(), true, payload.player(), payload.locked());
      applySort(context.player(), false, payload.container(), List.of());
    });
  }

  private static void handleTransfer(Networking.TransferC2S payload, ServerPlayNetworking.Context context) {
    context.player().server.execute(() -> transferAll(context.player(),
        payload.fromPlayerInventory(),
        payload.locked()
    ));
  }
}
