package me.roundaround.inventorymanagement.mixin;

import me.roundaround.inventorymanagement.event.GuiAtlasManagerInitCallback;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
  @Inject(
      method = "<init>", at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/texture/GuiAtlasManager;<init>(Lnet/minecraft/client/texture/TextureManager;)V",
      shift = At.Shift.BEFORE
  )
  )
  private void beforeVanillaGuiAtlasManagerInit(CallbackInfo ci) {
    GuiAtlasManagerInitCallback.EVENT.invoker().interact((MinecraftClient) (Object) this);
  }
}
