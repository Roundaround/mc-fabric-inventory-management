package me.roundaround.inventorymanagement.network;

import me.roundaround.inventorymanagement.generated.Constants;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public final class Networking {
  private Networking() {
  }

  public static final Identifier STACK_C2S = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "stack_c2s");
  public static final Identifier SORT_C2S = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "sort_c2s");
  public static final Identifier TRANSFER_C2S = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "transfer_c2s");

  public static void registerC2SPayloads() {
    PayloadTypeRegistry.serverboundPlay().register(StackC2S.ID, StackC2S.CODEC);
    PayloadTypeRegistry.serverboundPlay().register(SortC2S.ID, SortC2S.CODEC);
    PayloadTypeRegistry.serverboundPlay().register(TransferC2S.ID, TransferC2S.CODEC);
  }

  public record StackC2S(boolean fromPlayerInventory) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<StackC2S> ID = new CustomPacketPayload.Type<>(STACK_C2S);
    public static final StreamCodec<RegistryFriendlyByteBuf, StackC2S> CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        StackC2S::fromPlayerInventory,
        StackC2S::new
    );

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
      return ID;
    }
  }

  public record SortC2S(boolean isPlayerInventory) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SortC2S> ID = new CustomPacketPayload.Type<>(SORT_C2S);
    public static final StreamCodec<RegistryFriendlyByteBuf, SortC2S> CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        SortC2S::isPlayerInventory,
        SortC2S::new
    );

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
      return ID;
    }
  }

  public record TransferC2S(boolean fromPlayerInventory) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TransferC2S> ID = new CustomPacketPayload.Type<>(TRANSFER_C2S);
    public static final StreamCodec<RegistryFriendlyByteBuf, TransferC2S> CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        TransferC2S::fromPlayerInventory,
        TransferC2S::new
    );

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
      return ID;
    }
  }
}
