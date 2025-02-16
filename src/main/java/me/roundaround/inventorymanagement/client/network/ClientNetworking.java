package me.roundaround.inventorymanagement.client.network;

import me.roundaround.inventorymanagement.network.Networking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ClientNetworking {
  private ClientNetworking() {
  }

  public static void sendStack(boolean fromPlayerInventory) {
    ClientPlayNetworking.send(new Networking.StackC2S(fromPlayerInventory));
  }

  public static void sendSort(boolean isPlayerInventory) {
    ClientPlayNetworking.send(new Networking.SortC2S(isPlayerInventory));
  }

  public static void sendTransfer(boolean fromPlayerInventory) {
    ClientPlayNetworking.send(new Networking.TransferC2S(fromPlayerInventory));
  }
}
