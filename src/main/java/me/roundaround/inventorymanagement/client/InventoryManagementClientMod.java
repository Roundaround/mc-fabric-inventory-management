package me.roundaround.inventorymanagement.client;

import net.fabricmc.api.ClientModInitializer;

public class InventoryManagementClientMod implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    InventoryButtonsManager.INSTANCE.init();
  }
}
