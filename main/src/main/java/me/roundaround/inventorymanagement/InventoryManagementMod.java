package me.roundaround.inventorymanagement;

import me.roundaround.inventorymanagement.api.SlotRangeRegistry;
import me.roundaround.inventorymanagement.config.InventoryManagementConfig;
import me.roundaround.inventorymanagement.inventory.SlotRange;
import me.roundaround.inventorymanagement.network.Networking;
import me.roundaround.inventorymanagement.server.network.ServerNetworking;
import net.fabricmc.api.ModInitializer;
import net.minecraft.screen.HorseScreenHandler;

public final class InventoryManagementMod implements ModInitializer {
  public static final String MOD_ID = "inventorymanagement";

  @Override
  public void onInitialize() {
    InventoryManagementConfig.getInstance().init();

    Networking.registerC2SPayloads();
    ServerNetworking.registerReceivers();

    SlotRangeRegistry.SCREEN_HANDLERS.register(HorseScreenHandler.class)
        .withContainerSide((player, inventory, isPlayerSide) -> SlotRange.horseMainRange(inventory));
  }
}
