package me.roundaround.inventorymanagement.mixin;

import me.roundaround.inventorymanagement.event.HandleScreenInputCallback;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class ScreenMixin {
  @Inject(method = "keyPressed", at = @At(value = "HEAD"), cancellable = true)
  public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> info) {
    if (HandleScreenInputCallback.EVENT.invoker().interact((Screen) (Object) this, keyCode, scanCode, modifiers)) {
      info.setReturnValue(true);
    }
  }
}
