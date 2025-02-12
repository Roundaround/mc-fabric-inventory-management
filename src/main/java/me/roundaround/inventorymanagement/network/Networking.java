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

public final class Networking {
  private Networking() {
  }

  public static final Identifier STACK_C2S = new Identifier(InventoryManagementMod.MOD_ID, "stack_c2s");
  public static final Identifier SORT_C2S = new Identifier(InventoryManagementMod.MOD_ID, "sort_c2s");
  public static final Identifier SORT_ALL_C2S = new Identifier(InventoryManagementMod.MOD_ID, "sort_all_c2s");
  public static final Identifier TRANSFER_C2S = new Identifier(InventoryManagementMod.MOD_ID, "transfer_c2s");

  public static void registerC2SPayloads() {
    PayloadTypeRegistry.playC2S().register(StackC2S.ID, StackC2S.CODEC);
    PayloadTypeRegistry.playC2S().register(SortC2S.ID, SortC2S.CODEC);
    PayloadTypeRegistry.playC2S().register(SortAllC2S.ID, SortAllC2S.CODEC);
    PayloadTypeRegistry.playC2S().register(TransferC2S.ID, TransferC2S.CODEC);
  }

  public record StackC2S(boolean fromPlayerInventory, List<Integer> locked) implements CustomPayload {
    public static final CustomPayload.Id<StackC2S> ID = new CustomPayload.Id<>(STACK_C2S);
    public static final PacketCodec<RegistryByteBuf, StackC2S> CODEC = PacketCodec.tuple(
        PacketCodecs.BOOL,
        StackC2S::fromPlayerInventory,
        CustomCodecs.forList(PacketCodecs.INTEGER),
        StackC2S::locked,
        StackC2S::new
    );

    @Override
    public Id<StackC2S> getId() {
      return ID;
    }
  }

  public record SortC2S(boolean isPlayerInventory,
                        List<Integer> sorted,
                        List<Integer> locked) implements CustomPayload {
    public static final CustomPayload.Id<SortC2S> ID = new CustomPayload.Id<>(SORT_C2S);
    public static final PacketCodec<RegistryByteBuf, SortC2S> CODEC = PacketCodec.tuple(
        PacketCodecs.BOOL,
        SortC2S::isPlayerInventory,
        CustomCodecs.forList(PacketCodecs.INTEGER),
        SortC2S::sorted,
        CustomCodecs.forList(PacketCodecs.INTEGER),
        SortC2S::locked,
        SortC2S::new
    );

    @Override
    public Id<SortC2S> getId() {
      return ID;
    }
  }

  public record SortAllC2S(List<Integer> player,
                           List<Integer> container,
                           List<Integer> locked) implements CustomPayload {
    public static final CustomPayload.Id<SortAllC2S> ID = new CustomPayload.Id<>(SORT_ALL_C2S);
    public static final PacketCodec<RegistryByteBuf, SortAllC2S> CODEC = PacketCodec.tuple(
        CustomCodecs.forList(PacketCodecs.INTEGER),
        SortAllC2S::player,
        CustomCodecs.forList(PacketCodecs.INTEGER),
        SortAllC2S::container,
        CustomCodecs.forList(PacketCodecs.INTEGER),
        SortAllC2S::locked,
        SortAllC2S::new
    );

    @Override
    public Id<SortAllC2S> getId() {
      return ID;
    }
  }

  public record TransferC2S(boolean fromPlayerInventory, List<Integer> locked) implements CustomPayload {
    public static final CustomPayload.Id<TransferC2S> ID = new CustomPayload.Id<>(TRANSFER_C2S);
    public static final PacketCodec<RegistryByteBuf, TransferC2S> CODEC = PacketCodec.tuple(
        PacketCodecs.BOOL,
        TransferC2S::fromPlayerInventory,
        CustomCodecs.forList(PacketCodecs.INTEGER),
        TransferC2S::locked,
        TransferC2S::new
    );

    @Override
    public Id<TransferC2S> getId() {
      return ID;
    }
  }
}
