package me.roundaround.inventorymanagement.client.network;

import me.roundaround.inventorymanagement.network.Networking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

public final class ClientNetworking {
  private ClientNetworking() {
  }

  public static void sendStackFromContainerPacket() {
    ClientPlayNetworking.send(new Networking.StackC2S(false));
  }

  public static void sendStackIntoContainerPacket() {
    ClientPlayNetworking.send(new Networking.StackC2S(true));
  }

  public static void sendSortContainerPacket() {
    ClientPlayNetworking.send(new Networking.SortC2S(false, getItemNames()));
  }

  public static void sendSortInventoryPacket() {
    ClientPlayNetworking.send(new Networking.SortC2S(true, getItemNames()));
  }

  public static void sendSortAllPacket() {
    ClientPlayNetworking.send(new Networking.SortAllC2S(getItemNames()));
  }

  public static void sendTransferFromContainerPacket() {
    ClientPlayNetworking.send(new Networking.TransferC2S(false));
  }

  public static void sendTransferIntoContainerPacket() {
    ClientPlayNetworking.send(new Networking.TransferC2S(true));
  }

  private static HashMap<String, String> getItemNames() {
    if (!(MinecraftClient.getInstance().currentScreen instanceof HandledScreen<?> screen)) {
      return new HashMap<>(0);
    }

    HashSet<String> translationKeys = screen.getScreenHandler()
        .getStacks()
        .stream()
        .map(ItemStack::getItem)
        .map(Item::getTranslationKey)
        .collect(Collectors.toCollection(HashSet::new));
    return ClientI18nTracker.getInstance().track(translationKeys);
  }
}
