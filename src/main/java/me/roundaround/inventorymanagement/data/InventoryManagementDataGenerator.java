package me.roundaround.inventorymanagement.data;

import me.roundaround.inventorymanagement.data.lang.EnUsLangProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class InventoryManagementDataGenerator implements DataGeneratorEntrypoint {
  @Override
  public void onInitializeDataGenerator(FabricDataGenerator generator) {
    FabricDataGenerator.Pack pack = generator.createPack();
    pack.addProvider(TagProvider::new);
    pack.addProvider(EnUsLangProvider::new);
  }
}
