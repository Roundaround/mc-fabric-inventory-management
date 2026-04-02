package me.roundaround.inventorymanagement.server.network;

import me.roundaround.inventorymanagement.inventory.InventoryHelper;
import me.roundaround.inventorymanagement.network.Networking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public final class ServerNetworking {
  private ServerNetworking() {
  }

  public static void registerReceivers() {
    ServerPlayNetworking.registerGlobalReceiver(Networking.StackC2S.ID, ServerNetworking::handleStack);
    ServerPlayNetworking.registerGlobalReceiver(Networking.SortC2S.ID, ServerNetworking::handleSort);
    ServerPlayNetworking.registerGlobalReceiver(Networking.TransferC2S.ID, ServerNetworking::handleTransfer);
  }

  private static void handleStack(Networking.StackC2S payload, ServerPlayNetworking.Context context) {
    final ServerPlayer player = context.player();
    context.server().execute(() -> InventoryHelper.autoStack(player, payload.fromPlayerInventory()));
  }

  private static void handleSort(Networking.SortC2S payload, ServerPlayNetworking.Context context) {
    final ServerPlayer player = context.player();
    context.server().execute(() -> InventoryHelper.sortInventory(player, payload.isPlayerInventory()));
  }

  private static void handleTransfer(Networking.TransferC2S payload, ServerPlayNetworking.Context context) {
    final ServerPlayer player = context.player();
    context.server().execute(() -> InventoryHelper.transferAll(player, payload.fromPlayerInventory()));
  }
}
