package me.roundaround.inventorymanagement.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.config.GameScopedConfig;
import me.roundaround.inventorymanagement.roundalib.client.gui.screen.ConfigScreen;

public class ModMenuImpl implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return (screen) -> new ConfigScreen(screen, InventoryManagementMod.MOD_ID, GameScopedConfig.getInstance());
  }
}
