package me.roundaround.inventorymanagement.network;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.roundalib.network.CustomCodecs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public final class Networking {
  private Networking() {
  }

  public static final Identifier STACK_C2S = new Identifier(InventoryManagementMod.MOD_ID, "stack_c2s");
  public static final Identifier SORT_C2S = new Identifier(InventoryManagementMod.MOD_ID, "sort_c2s");
  public static final Identifier TRANSFER_C2S = new Identifier(InventoryManagementMod.MOD_ID, "transfer_c2s");
  public static final Identifier RECALCULATE_C2S = new Identifier(InventoryManagementMod.MOD_ID, "recalculate_c2s");
  public static final Identifier SERVER_SORT_C2S = new Identifier(InventoryManagementMod.MOD_ID, "server_sort_c2s");
  public static final Identifier SERVER_SORT_ALL_C2S = new Identifier(
      InventoryManagementMod.MOD_ID, "server_sort_all_c2s");

  public static void registerC2SPayloads() {
    PayloadTypeRegistry.playC2S().register(StackC2S.ID, StackC2S.CODEC);
    PayloadTypeRegistry.playC2S().register(SortC2S.ID, SortC2S.CODEC);
    PayloadTypeRegistry.playC2S().register(TransferC2S.ID, TransferC2S.CODEC);
    PayloadTypeRegistry.playC2S().register(RecalculateC2S.ID, RecalculateC2S.CODEC);
    PayloadTypeRegistry.playC2S().register(ServerSortC2S.ID, ServerSortC2S.CODEC);
    PayloadTypeRegistry.playC2S().register(ServerSortAllC2S.ID, ServerSortAllC2S.CODEC);
  }

  public record StackC2S(boolean fromPlayerInventory) implements CustomPayload {
    public static final CustomPayload.Id<StackC2S> ID = new CustomPayload.Id<>(STACK_C2S);
    public static final PacketCodec<RegistryByteBuf, StackC2S> CODEC = PacketCodec.tuple(
        PacketCodecs.BOOL, StackC2S::fromPlayerInventory, StackC2S::new);

    @Override
    public Id<StackC2S> getId() {
      return ID;
    }
  }

  public record SortC2S(boolean isPlayerInventory, List<Integer> sorted) implements CustomPayload {
    public static final CustomPayload.Id<SortC2S> ID = new CustomPayload.Id<>(SORT_C2S);
    public static final PacketCodec<RegistryByteBuf, SortC2S> CODEC = PacketCodec.tuple(PacketCodecs.BOOL,
        SortC2S::isPlayerInventory, CustomCodecs.forList(PacketCodecs.INTEGER), SortC2S::sorted, SortC2S::new
    );

    @Override
    public Id<SortC2S> getId() {
      return ID;
    }
  }

  public record ServerSortC2S(boolean isPlayerInventory, Map<String, String> itemNames) implements CustomPayload {
    public static final CustomPayload.Id<ServerSortC2S> ID = new CustomPayload.Id<>(SERVER_SORT_C2S);
    public static final PacketCodec<RegistryByteBuf, ServerSortC2S> CODEC = PacketCodec.tuple(PacketCodecs.BOOL,
        ServerSortC2S::isPlayerInventory, CustomCodecs.forMap(PacketCodecs.STRING, PacketCodecs.STRING),
        ServerSortC2S::itemNames, ServerSortC2S::new
    );

    @Override
    public Id<ServerSortC2S> getId() {
      return ID;
    }
  }

  public record ServerSortAllC2S(Map<String, String> itemNames) implements CustomPayload {
    public static final CustomPayload.Id<ServerSortAllC2S> ID = new CustomPayload.Id<>(SERVER_SORT_ALL_C2S);
    public static final PacketCodec<RegistryByteBuf, ServerSortAllC2S> CODEC = PacketCodec.tuple(
        CustomCodecs.forMap(PacketCodecs.STRING, PacketCodecs.STRING), ServerSortAllC2S::itemNames,
        ServerSortAllC2S::new
    );

    @Override
    public Id<ServerSortAllC2S> getId() {
      return ID;
    }
  }

  public record TransferC2S(boolean fromPlayerInventory) implements CustomPayload {
    public static final CustomPayload.Id<TransferC2S> ID = new CustomPayload.Id<>(TRANSFER_C2S);
    public static final PacketCodec<RegistryByteBuf, TransferC2S> CODEC = PacketCodec.tuple(
        PacketCodecs.BOOL, TransferC2S::fromPlayerInventory, TransferC2S::new);

    @Override
    public Id<TransferC2S> getId() {
      return ID;
    }
  }

  public record RecalculateC2S() implements CustomPayload {
    public static final CustomPayload.Id<RecalculateC2S> ID = new CustomPayload.Id<>(RECALCULATE_C2S);
    public static final PacketCodec<RegistryByteBuf, RecalculateC2S> CODEC = CustomCodecs.empty(RecalculateC2S::new);

    @Override
    public Id<RecalculateC2S> getId() {
      return ID;
    }
  }
}
