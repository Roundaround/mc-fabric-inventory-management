package me.roundaround.inventorymanagement.client.texture;

import me.roundaround.inventorymanagement.generated.Constants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.resource.metadata.GuiResourceMetadata;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasHolder;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.Identifier;

import java.util.Set;

@Environment(value = EnvType.CLIENT)
public class GuiAtlasManager extends SpriteAtlasHolder {
  private static final Set<ResourceMetadataSerializer<?>> METADATA_SERIALIZERS = Set.of(
      AnimationResourceMetadata.SERIALIZER,
      GuiResourceMetadata.SERIALIZER
  );

  public GuiAtlasManager(TextureManager manager) {
    super(
        manager,
        Identifier.of(Constants.MOD_ID, "textures/atlas/gui.png"),
        Identifier.of(Constants.MOD_ID, "gui"),
        METADATA_SERIALIZERS
    );
  }

  @Override
  public Sprite getSprite(Identifier objectId) {
    return super.getSprite(objectId);
  }
}
