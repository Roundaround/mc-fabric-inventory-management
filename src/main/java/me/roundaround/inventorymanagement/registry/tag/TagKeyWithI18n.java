package me.roundaround.inventorymanagement.registry.tag;

import net.minecraft.text.Text;

// TODO: Remove starting in 1.21
public interface TagKeyWithI18n {
  default String getTranslationKey() {
    return "";
  }

  default Text getName() {
    return Text.empty();
  }
}
