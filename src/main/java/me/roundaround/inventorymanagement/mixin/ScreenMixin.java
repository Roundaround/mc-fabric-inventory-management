package me.roundaround.inventorymanagement.mixin;

import me.roundaround.inventorymanagement.event.HandleScreenInputCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class ScreenMixin {
  @Inject(method = "Lnet/minecraft/client/gui/screen/Screen;keyPressed(Lnet/minecraft/client/input/KeyInput;)Z", at = @At(value = "HEAD"), cancellable = true)
  public void keyPressed(KeyInput input, CallbackInfoReturnable<Boolean> info) {
    if (HandleScreenInputCallback.EVENT.invoker().interact((Screen) (Object) this, input)) {
      info.setReturnValue(true);
    }
  }
}
