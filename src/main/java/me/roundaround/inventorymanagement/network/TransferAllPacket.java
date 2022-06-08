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

public class TransferAllPacket {
  private static final Identifier TRANSFER_ALL_PACKET = new Identifier(
      InventoryManagementMod.MOD_ID,
      "transfer_all_packet");

  public static void registerReceive() {
    ServerPlayNetworking.registerGlobalReceiver(TRANSFER_ALL_PACKET,
        ((server, player, handler, buffer, responseSender) -> {
          boolean fromPlayerInventory = buffer.readBoolean();
          server.execute(() -> {
            InventoryHelper.transferAll(player, fromPlayerInventory);
          });
        }));
  }

  @Environment(EnvType.CLIENT)
  public static void sendToServer(boolean fromPlayerInventory) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeBoolean(fromPlayerInventory);
    ClientPlayNetworking.send(TRANSFER_ALL_PACKET, new PacketByteBuf(buf));
  }
}
