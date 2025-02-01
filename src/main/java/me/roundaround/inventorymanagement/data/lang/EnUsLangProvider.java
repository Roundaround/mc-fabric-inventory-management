package me.roundaround.inventorymanagement.data.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class EnUsLangProvider extends FabricLanguageProvider {
  public EnUsLangProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> future) {
    super(dataOutput, "en_us", future);
  }

  @Override
  public void generateTranslations(RegistryWrapper.WrapperLookup lookup, TranslationBuilder builder) {
    // TODO: Remove starting in 1.21
    builder.add("tag.item.c.concrete_powders", "Concrete Powders");
    // TODO: Remove starting in 1.21
    builder.add("tag.item.c.concretes", "Concretes");
    // TODO: Remove starting in 1.21
    builder.add("tag.item.c.dyes", "Dyes");
    // TODO: Remove starting in 1.21
    builder.add("tag.item.c.glazed_terracottas", "Glazed Terracottas");

    // TODO: Replace with ItemTags.BANNERS.getTranslationKey() starting in 1.21
    builder.add("tag.item.inventorymanagement.banners", "Banners");
    // TODO: Replace with ItemTags.BEDS.getTranslationKey() starting in 1.21
    builder.add("tag.item.inventorymanagement.beds", "Beds");
    // TODO: Replace with ItemTags.WOOL_CARPETS.getTranslationKey() starting in 1.21
    builder.add("tag.item.inventorymanagement.wool_carpets", "Carpets");
    // TODO: Replace with ItemTags.WOOL.getTranslationKey() starting in 1.21
    builder.add("tag.item.inventorymanagement.wools", "Wools");
  }
}
