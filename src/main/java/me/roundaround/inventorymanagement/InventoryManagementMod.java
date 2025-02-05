package me.roundaround.inventorymanagement;

import me.roundaround.inventorymanagement.api.registry.SlotRangeRegistry;
import me.roundaround.inventorymanagement.config.InventoryManagementConfig;
import me.roundaround.inventorymanagement.inventory.SlotRange;
import me.roundaround.inventorymanagement.inventory.sorting.itemstack.ItemStackComparator;
import me.roundaround.inventorymanagement.network.Networking;
import me.roundaround.inventorymanagement.server.network.ServerI18nTracker;
import me.roundaround.inventorymanagement.server.network.ServerNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.screen.HorseScreenHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class InventoryManagementMod implements ModInitializer {
  public static final String MOD_ID = "inventorymanagement";
  public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

  @Override
  public void onInitialize() {
    InventoryManagementConfig.getInstance().init();

    Networking.registerC2SPayloads();
    ServerNetworking.registerReceivers();

    ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
      ServerI18nTracker.remove(handler.player);
      ItemStackComparator.remove(handler.player.getUuid());
    });

    ServerLifecycleEvents.SERVER_STOPPED.register((server) -> {
      ItemStackComparator.clear();
    });

    SlotRangeRegistry.SCREEN_HANDLERS.register(HorseScreenHandler.class)
        .withContainerSide((player, inventory, isPlayerSide) -> SlotRange.horseMainRange(inventory));
  }
}
