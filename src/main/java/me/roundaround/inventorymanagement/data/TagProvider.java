package me.roundaround.inventorymanagement.data;

import me.roundaround.inventorymanagement.registry.tag.InventoryManagementItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class TagProvider extends FabricTagProvider<Item> {
  public TagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> future) {
    super(output, RegistryKeys.ITEM, future);
  }

  @Override
  protected void configure(RegistryWrapper.WrapperLookup lookup) {
    //@formatter:off

    // TODO: Remove in favor of ConventionalItemTags.GLAZED_TERRACOTTAS starting in 1.21
    getOrCreateTagBuilder(InventoryManagementItemTags.GLAZED_TERRACOTTAS).add(
        Items.WHITE_GLAZED_TERRACOTTA,
        Items.ORANGE_GLAZED_TERRACOTTA,
        Items.MAGENTA_GLAZED_TERRACOTTA,
        Items.LIGHT_BLUE_GLAZED_TERRACOTTA,
        Items.YELLOW_GLAZED_TERRACOTTA,
        Items.LIME_GLAZED_TERRACOTTA,
        Items.PINK_GLAZED_TERRACOTTA,
        Items.GRAY_GLAZED_TERRACOTTA,
        Items.LIGHT_GRAY_GLAZED_TERRACOTTA,
        Items.CYAN_GLAZED_TERRACOTTA,
        Items.PURPLE_GLAZED_TERRACOTTA,
        Items.BLUE_GLAZED_TERRACOTTA,
        Items.BROWN_GLAZED_TERRACOTTA,
        Items.GREEN_GLAZED_TERRACOTTA,
        Items.RED_GLAZED_TERRACOTTA,
        Items.BLACK_GLAZED_TERRACOTTA
    );

    // TODO: Remove in favor of ConventionalItemTags.CONCRETES starting in 1.21
    getOrCreateTagBuilder(InventoryManagementItemTags.CONCRETES).add(
        Items.WHITE_CONCRETE,
        Items.ORANGE_CONCRETE,
        Items.MAGENTA_CONCRETE,
        Items.LIGHT_BLUE_CONCRETE,
        Items.YELLOW_CONCRETE,
        Items.LIME_CONCRETE,
        Items.PINK_CONCRETE,
        Items.GRAY_CONCRETE,
        Items.LIGHT_GRAY_CONCRETE,
        Items.CYAN_CONCRETE,
        Items.PURPLE_CONCRETE,
        Items.BLUE_CONCRETE,
        Items.BROWN_CONCRETE,
        Items.GREEN_CONCRETE,
        Items.RED_CONCRETE,
        Items.BLACK_CONCRETE
    );

    // TODO: Remove in favor of ConventionalItemTags.CONCRETE_POWDERS starting in 1.21
    getOrCreateTagBuilder(InventoryManagementItemTags.CONCRETE_POWDERS)
        .addOptionalTag(BlockTags.CONCRETE_POWDER.id());

    //@formatter:on
  }
}
