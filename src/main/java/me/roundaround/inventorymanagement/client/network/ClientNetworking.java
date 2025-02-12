package me.roundaround.inventorymanagement.client.network;

import me.roundaround.inventorymanagement.inventory.InventoryHelper;
import me.roundaround.inventorymanagement.network.Networking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.util.List;

public final class ClientNetworking {
  private ClientNetworking() {
  }

  public static void sendStackFromContainerPacket() {
    ClientPlayNetworking.send(new Networking.StackC2S(false, InventoryHelper.getLockedSlots()));
  }

  public static void sendStackIntoContainerPacket() {
    ClientPlayNetworking.send(new Networking.StackC2S(true, InventoryHelper.getLockedSlots()));
  }

  public static void sendSortContainerPacket(List<Integer> sorted) {
    sendSortInventoryPacket(false, sorted);
  }

  public static void sendSortInventoryPacket(List<Integer> sorted) {
    sendSortInventoryPacket(true, sorted);
  }

  public static void sendSortInventoryPacket(boolean isPlayerInventory, List<Integer> sorted) {
    List<Integer> locked = isPlayerInventory ? InventoryHelper.getLockedSlots() : List.of();
    ClientPlayNetworking.send(new Networking.SortC2S(isPlayerInventory, sorted, locked));
  }

  public static void sendSortAllPacket(List<Integer> player, List<Integer> container) {
    ClientPlayNetworking.send(new Networking.SortAllC2S(player, container, InventoryHelper.getLockedSlots()));
  }

  public static void sendTransferFromContainerPacket() {
    ClientPlayNetworking.send(new Networking.TransferC2S(false, InventoryHelper.getLockedSlots()));
  }

  public static void sendTransferIntoContainerPacket() {
    ClientPlayNetworking.send(new Networking.TransferC2S(true, InventoryHelper.getLockedSlots()));
  }
}
