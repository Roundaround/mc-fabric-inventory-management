package me.roundaround.inventorymanagement.server.network;

import me.roundaround.inventorymanagement.inventory.InventoryHelper;
import me.roundaround.inventorymanagement.network.Networking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class ServerNetworking {
  private ServerNetworking() {
  }

  public static void registerReceivers() {
    ServerPlayNetworking.registerGlobalReceiver(Networking.StackC2S.ID, ServerNetworking::handleStack);
    ServerPlayNetworking.registerGlobalReceiver(Networking.SortC2S.ID, ServerNetworking::handleSort);
    ServerPlayNetworking.registerGlobalReceiver(Networking.TransferC2S.ID, ServerNetworking::handleTransfer);
  }

  private static void handleStack(Networking.StackC2S payload, ServerPlayNetworking.Context context) {
    context.player().server.execute(() -> InventoryHelper.autoStack(context.player(), payload.fromPlayerInventory()));
  }

  private static void handleSort(Networking.SortC2S payload, ServerPlayNetworking.Context context) {
    context.player().server.execute(() -> InventoryHelper.sortInventory(context.player(), payload.isPlayerInventory()));
  }

  private static void handleTransfer(Networking.TransferC2S payload, ServerPlayNetworking.Context context) {
    context.player().server.execute(() -> InventoryHelper.transferAll(context.player(), payload.fromPlayerInventory()));
  }
}
