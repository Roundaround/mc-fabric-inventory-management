package me.roundaround.inventorymanagement.mixin;

import me.roundaround.inventorymanagement.registry.tag.TagKeyWithI18n;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TagKey.class)
public abstract class TagKeyMixin implements TagKeyWithI18n {
  @Shadow
  public abstract RegistryKey<?> registry();

  @Shadow
  public abstract Identifier id();

  @Override
  public String getTranslationKey() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("tag.");

    Identifier registryIdentifier = this.registry().getValue();
    Identifier tagIdentifier = this.id();

    if (!registryIdentifier.getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
      stringBuilder.append(registryIdentifier.getNamespace()).append(".");
    }

    stringBuilder.append(registryIdentifier.getPath().replace("/", "."))
        .append(".")
        .append(tagIdentifier.getNamespace())
        .append(".")
        .append(tagIdentifier.getPath().replace("/", ".").replace(":", "."));

    return stringBuilder.toString();
  }

  @Override
  public Text getName() {
    return Text.translatableWithFallback(getTranslationKey(), "#" + this.id().toString());
  }
}
