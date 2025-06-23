package me.roundaround.inventorymanagement;

import me.roundaround.gradle.api.annotation.Entrypoint;
import me.roundaround.inventorymanagement.config.InventoryManagementConfig;
import me.roundaround.inventorymanagement.network.Networking;
import me.roundaround.inventorymanagement.server.network.ServerNetworking;
import net.fabricmc.api.ModInitializer;

@Entrypoint(Entrypoint.MAIN)
public final class InventoryManagementMod implements ModInitializer {
  @Override
  public void onInitialize() {
    InventoryManagementConfig.getInstance().init();

    Networking.registerC2SPayloads();
    ServerNetworking.registerReceivers();
  }
}
