package me.roundaround.inventorymanagement.config;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.client.gui.screen.Screen;

import java.util.List;

public final class ConfigHelpers {
  private ConfigHelpers() {
  }

  public static String getScreenKey(Screen screen) {
    MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
    String unmapped = mappingResolver.unmapClassName("named", screen.getClass().getName());
    return unmapped.replaceAll("\\.", "-");
  }

  public static List<Integer> getLockedSlots() {
    return List.copyOf(GameScopedConfig.getInstance().lockedInventorySlots.getValue());
  }
}
