package me.roundaround.inventorymanagement.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.roundaround.inventorymanagement.event.ResourcesReloadedEvent;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
  @ModifyReturnValue(
      method = "reloadResources(ZLnet/minecraft/client/MinecraftClient$LoadingContext;)" +
               "Ljava/util/concurrent/CompletableFuture;", at = @At("RETURN")
  )
  private CompletableFuture<Void> onReloadResources(CompletableFuture<Void> originalFuture) {
    return originalFuture.thenAccept((unused) -> ResourcesReloadedEvent.EVENT.invoker().handle(this.self()));
  }

  @Unique
  private MinecraftClient self() {
    return (MinecraftClient) (Object) this;
  }
}
