package me.roundaround.inventorymanagement.server.network;

import me.roundaround.inventorymanagement.inventory.InventoryHelper;
import me.roundaround.inventorymanagement.inventory.sorting.itemstack.ItemStackComparator;
import me.roundaround.inventorymanagement.network.Networking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class ServerNetworking {
  private ServerNetworking() {
  }

  public static void registerReceivers() {
    ServerPlayNetworking.registerGlobalReceiver(Networking.StackC2S.ID, ServerNetworking::handleStack);
    ServerPlayNetworking.registerGlobalReceiver(Networking.SortC2S.ID, ServerNetworking::handleSort);
    ServerPlayNetworking.registerGlobalReceiver(Networking.ServerSortC2S.ID, ServerNetworking::handleServerSort);
    ServerPlayNetworking.registerGlobalReceiver(Networking.ServerSortAllC2S.ID, ServerNetworking::handleServerSortAll);
    ServerPlayNetworking.registerGlobalReceiver(Networking.TransferC2S.ID, ServerNetworking::handleTransfer);
    ServerPlayNetworking.registerGlobalReceiver(Networking.RecalculateC2S.ID, ServerNetworking::handleRecalculate);
  }

  private static void handleStack(Networking.StackC2S payload, ServerPlayNetworking.Context context) {
    context.player().server.execute(() -> InventoryHelper.autoStack(context.player(), payload.fromPlayerInventory()));
  }

  private static void handleSort(Networking.SortC2S payload, ServerPlayNetworking.Context context) {
    context.player().server.execute(
        () -> InventoryHelper.applySort(context.player(), payload.isPlayerInventory(), payload.sorted()));
  }

  private static void handleServerSort(Networking.ServerSortC2S payload, ServerPlayNetworking.Context context) {
    context.player().server.execute(() -> {
      ServerI18nTracker.getInstance(context.player().getUuid()).track(payload.itemNames());
      InventoryHelper.sortInventory(context.player(), payload.isPlayerInventory());
    });
  }

  private static void handleServerSortAll(Networking.ServerSortAllC2S payload, ServerPlayNetworking.Context context) {
    context.player().server.execute(() -> {
      ServerI18nTracker.getInstance(context.player().getUuid()).track(payload.itemNames());
      InventoryHelper.sortAll(context.player());
    });
  }

  private static void handleTransfer(Networking.TransferC2S payload, ServerPlayNetworking.Context context) {
    context.player().server.execute(() -> InventoryHelper.transferAll(context.player(), payload.fromPlayerInventory()));
  }

  private static void handleRecalculate(Networking.RecalculateC2S payload, ServerPlayNetworking.Context context) {
    context.player().server.execute(() -> ItemStackComparator.remove(context.player().getUuid()));
  }
}
