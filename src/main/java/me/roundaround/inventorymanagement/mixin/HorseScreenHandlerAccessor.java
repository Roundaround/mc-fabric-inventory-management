package me.roundaround.inventorymanagement.mixin;

import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.screen.HorseScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HorseScreenHandler.class)
public interface HorseScreenHandlerAccessor {
  @Accessor
  AbstractHorseEntity getEntity();

  @Invoker
  boolean invokeHasChest(AbstractHorseEntity horse);
}
