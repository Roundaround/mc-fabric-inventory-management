package me.roundaround.inventorymanagement.fabric.compat.modmenu;
import me.roundaround.inventorymanagement.Constants;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.roundaround.inventorymanagement.config.InventoryManagementConfig;

import me.roundaround.roundalib.client.gui.screen.ConfigScreen;

public class ModMenuImpl implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return (screen) -> new ConfigScreen(screen, Constants.MOD_ID, InventoryManagementConfig.getInstance());
  }
}
