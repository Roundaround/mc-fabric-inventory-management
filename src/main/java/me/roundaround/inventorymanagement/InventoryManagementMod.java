package me.roundaround.inventorymanagement;

import me.roundaround.inventorymanagement.api.InventoryButtonsRegistry;
import me.roundaround.inventorymanagement.config.InventoryManagementConfig;
import me.roundaround.inventorymanagement.network.AutoStackPacket;
import me.roundaround.inventorymanagement.network.SortInventoryPacket;
import me.roundaround.inventorymanagement.network.TransferAllPacket;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
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
}
