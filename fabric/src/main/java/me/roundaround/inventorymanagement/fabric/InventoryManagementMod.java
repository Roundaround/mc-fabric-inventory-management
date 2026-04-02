package me.roundaround.inventorymanagement.fabric;

import me.roundaround.inventorymanagement.config.InventoryManagementConfig;
import me.roundaround.inventorymanagement.fabric.server.network.ServerNetworking;
import me.roundaround.inventorymanagement.network.Networking;
import me.roundaround.roundalib.util.Platform;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class InventoryManagementMod implements ModInitializer {
  @Override
  public void onInitialize() {
    Platform.init(new FabricPlatform());

    InventoryManagementConfig.getInstance().init();

    PayloadTypeRegistry.playC2S().register(Networking.StackC2S.ID, Networking.StackC2S.CODEC);
    PayloadTypeRegistry.playC2S().register(Networking.SortC2S.ID, Networking.SortC2S.CODEC);
    PayloadTypeRegistry.playC2S().register(Networking.TransferC2S.ID, Networking.TransferC2S.CODEC);

    ServerNetworking.registerReceivers();
  }
}
