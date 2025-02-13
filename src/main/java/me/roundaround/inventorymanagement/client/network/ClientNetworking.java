package me.roundaround.inventorymanagement.client.network;

import me.roundaround.inventorymanagement.network.Networking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

import static me.roundaround.inventorymanagement.client.inventory.ClientInventoryHelper.*;

public final class ClientNetworking {
  private ClientNetworking() {
  }

  public static void sendStackFromContainer() {
    ClientPlayNetworking.send(new Networking.StackC2S(false, getLockedSlots()));
  }

  public static void sendStackIntoContainer() {
    ClientPlayNetworking.send(new Networking.StackC2S(true, getLockedSlots()));
  }

  public static void sendSortInventory(PlayerEntity player, boolean isPlayerInventory) {
    if (isPlayerInventory) {
      sendSortPlayer(player);
    } else {
      sendSortContainer(player);
    }
  }

  public static void sendSortContainer(PlayerEntity player) {
    sendSortInventory(false, calculateContainerSort(player));
  }

  public static void sendSortPlayer(PlayerEntity player) {
    sendSortInventory(true, calculatePlayerSort(player));
  }

  public static void sendSortInventory(boolean isPlayerInventory, List<Integer> sorted) {
    List<Integer> locked = isPlayerInventory ? getLockedSlots() : List.of();
    ClientPlayNetworking.send(new Networking.SortC2S(isPlayerInventory, sorted, locked));
  }

  public static void sendSortAll(PlayerEntity player) {
    ClientPlayNetworking.send(new Networking.SortAllC2S(calculatePlayerSort(player),
        calculateContainerSort(player),
        getLockedSlots()
    ));
  }

  public static void sendTransferFromContainer() {
    ClientPlayNetworking.send(new Networking.TransferC2S(false, getLockedSlots()));
  }

  public static void sendTransferIntoContainerPacket() {
    ClientPlayNetworking.send(new Networking.TransferC2S(true, getLockedSlots()));
  }
}
