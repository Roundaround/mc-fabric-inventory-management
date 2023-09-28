package me.roundaround.inventorymanagement;

import me.roundaround.inventorymanagement.config.InventoryManagementConfig;
import me.roundaround.inventorymanagement.network.AutoStackPacket;
import me.roundaround.inventorymanagement.network.SortInventoryPacket;
import me.roundaround.inventorymanagement.network.TransferAllPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.client.gui.screen.Screen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class InventoryManagementMod implements ModInitializer {
  public static final String MOD_ID = "inventorymanagement";
  public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
  public static final InventoryManagementConfig CONFIG = new InventoryManagementConfig();

  @Override
  public void onInitialize() {
    CONFIG.init();

    SortInventoryPacket.registerReceive();
    AutoStackPacket.registerReceive();
    TransferAllPacket.registerReceive();
  }

  public static String getScreenKey(Screen screen) {
    MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
    String unmapped = mappingResolver.unmapClassName("named", screen.getClass().getName());
    return unmapped.replaceAll("\\.", "-");
  }

  public static String getClassKey(Class<?> clazz) {
    MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
    String unmapped = mappingResolver.unmapClassName("named", clazz.getName());
    return unmapped.replaceAll("\\.", "-");
  }
}
