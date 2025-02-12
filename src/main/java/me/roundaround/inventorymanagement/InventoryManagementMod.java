package me.roundaround.inventorymanagement;

import me.roundaround.inventorymanagement.api.gui.SlotRangeRegistry;
import me.roundaround.inventorymanagement.config.GameScopedConfig;
import me.roundaround.inventorymanagement.inventory.SlotRange;
import me.roundaround.inventorymanagement.network.Networking;
import me.roundaround.inventorymanagement.server.network.ServerNetworking;
import net.fabricmc.api.ModInitializer;
import net.minecraft.screen.HorseScreenHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class InventoryManagementMod implements ModInitializer {
  public static final String MOD_ID = "inventorymanagement";
  public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

  @Override
  public void onInitialize() {
    GameScopedConfig.getInstance().init();

    Networking.registerC2SPayloads();
    ServerNetworking.registerReceivers();

    SlotRangeRegistry.SCREEN_HANDLERS.register(HorseScreenHandler.class)
        .withContainerSide((player, inventory, isPlayerSide) -> SlotRange.horseMainRange(inventory));
  }
}
