package me.roundaround.inventorymanagement.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.roundalib.config.gui.ConfigScreen;

public class ModMenuImpl implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return (screen) -> new ConfigScreen(screen, InventoryManagementMod.CONFIG);
  }
}
