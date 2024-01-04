package me.roundaround.inventorymanagement.client.texture;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.resource.metadata.GuiResourceMetadata;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasHolder;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

import java.util.Set;

@Environment(value = EnvType.CLIENT)
public class GuiAtlasManager extends SpriteAtlasHolder {
  private static final Set<ResourceMetadataReader<?>> METADATA_READERS =
      Set.of(AnimationResourceMetadata.READER, GuiResourceMetadata.SERIALIZER);

  public GuiAtlasManager(TextureManager manager) {
    super(manager,
        new Identifier(InventoryManagementMod.MOD_ID, "textures/atlas/gui.png"),
        new Identifier(InventoryManagementMod.MOD_ID, "gui"),
        METADATA_READERS);
  }

  @Override
  public Sprite getSprite(Identifier objectId) {
    return super.getSprite(objectId);
  }
}
