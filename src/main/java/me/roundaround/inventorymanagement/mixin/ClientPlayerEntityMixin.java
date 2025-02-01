package me.roundaround.inventorymanagement.mixin;

import me.roundaround.inventorymanagement.event.BeforeCloseHandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
  @Inject(method = "closeHandledScreen", at = @At("HEAD"))
  private void beforeCloseHandledScreen(CallbackInfo ci) {
    BeforeCloseHandledScreen.EVENT.invoker().handle(this.self(), this.self().currentScreenHandler);
  }

  @Unique
  public ClientPlayerEntity self() {
    return (ClientPlayerEntity) (Object) this;
  }
}
