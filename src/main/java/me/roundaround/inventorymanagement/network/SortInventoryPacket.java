package me.roundaround.inventorymanagement.network;

import io.netty.buffer.Unpooled;
import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.inventory.InventoryHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class SortInventoryPacket {
  private static final Identifier SORT_INVENTORY_PACKET = new Identifier(
      InventoryManagementMod.MOD_ID,
      "sort_inventory_packet");

  public static void registerReceive() {
    ServerPlayNetworking.registerGlobalReceiver(SORT_INVENTORY_PACKET,
        ((server, player, handler, buffer, responseSender) -> {
          boolean isPlayerInventory = buffer.readBoolean();
          server.execute(() -> {
            InventoryHelper.sortInventory(player, isPlayerInventory);
          });
        }));
  }

  @Environment(EnvType.CLIENT)
  public static void sendToServer(boolean isPlayerInventory) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeBoolean(isPlayerInventory);
    ClientPlayNetworking.send(SORT_INVENTORY_PACKET, new PacketByteBuf(buf));
  }
}
