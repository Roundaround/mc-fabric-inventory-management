package me.roundaround.inventorymanagement;

import me.roundaround.inventorymanagement.config.InventoryManagementConfig;
import net.fabricmc.api.ModInitializer;

public final class InventoryManagementMod implements ModInitializer {
  public static final String MOD_ID = "inventorymanagement";
  public static final InventoryManagementConfig CONFIG = new InventoryManagementConfig();

  @Override
  public void onInitialize() {
    CONFIG.init();
  }
}
