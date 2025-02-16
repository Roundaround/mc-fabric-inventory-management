package me.roundaround.inventorymanagement.network;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public final class Networking {
  private Networking() {
  }

  public static final Identifier STACK_C2S = new Identifier(InventoryManagementMod.MOD_ID, "stack_c2s");
  public static final Identifier SORT_C2S = new Identifier(InventoryManagementMod.MOD_ID, "sort_c2s");
  public static final Identifier TRANSFER_C2S = new Identifier(InventoryManagementMod.MOD_ID, "transfer_c2s");

  public static void registerC2SPayloads() {
    PayloadTypeRegistry.playC2S().register(StackC2S.ID, StackC2S.CODEC);
    PayloadTypeRegistry.playC2S().register(SortC2S.ID, SortC2S.CODEC);
    PayloadTypeRegistry.playC2S().register(TransferC2S.ID, TransferC2S.CODEC);
  }

  public record StackC2S(boolean fromPlayerInventory) implements CustomPayload {
    public static final CustomPayload.Id<StackC2S> ID = new CustomPayload.Id<>(STACK_C2S);
    public static final PacketCodec<RegistryByteBuf, StackC2S> CODEC = PacketCodec.tuple(PacketCodecs.BOOL,
        StackC2S::fromPlayerInventory,
        StackC2S::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
      return ID;
    }
  }

  public record SortC2S(boolean isPlayerInventory) implements CustomPayload {
    public static final CustomPayload.Id<SortC2S> ID = new CustomPayload.Id<>(SORT_C2S);
    public static final PacketCodec<RegistryByteBuf, SortC2S> CODEC = PacketCodec.tuple(PacketCodecs.BOOL,
        SortC2S::isPlayerInventory,
        SortC2S::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
      return ID;
    }
  }

  public record TransferC2S(boolean fromPlayerInventory) implements CustomPayload {
    public static final CustomPayload.Id<TransferC2S> ID = new CustomPayload.Id<>(TRANSFER_C2S);
    public static final PacketCodec<RegistryByteBuf, TransferC2S> CODEC = PacketCodec.tuple(PacketCodecs.BOOL,
        TransferC2S::fromPlayerInventory,
        TransferC2S::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
      return ID;
    }
  }
}
