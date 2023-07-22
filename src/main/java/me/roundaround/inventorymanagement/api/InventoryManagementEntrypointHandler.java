package me.roundaround.inventorymanagement.api;

import me.roundaround.inventorymanagement.client.InventoryButtonsManager;

public interface InventoryManagementEntrypointHandler {
  void onInventoryManagementInit(InventoryButtonsManager manager);
}
