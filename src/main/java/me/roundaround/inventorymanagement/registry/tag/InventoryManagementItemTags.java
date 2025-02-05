package me.roundaround.inventorymanagement.registry.tag;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class InventoryManagementItemTags {
  public static final TagKey<Item> CAKES = TagKey.of(
      RegistryKeys.ITEM, Identifier.of(InventoryManagementMod.MOD_ID, "cakes"));
  public static final TagKey<Item> CONCRETE_POWDERS = TagKey.of(
      RegistryKeys.ITEM, Identifier.of(InventoryManagementMod.MOD_ID, "concrete_powders"));
  public static final TagKey<Item> CONCRETES = TagKey.of(
      RegistryKeys.ITEM, Identifier.of(InventoryManagementMod.MOD_ID, "concretes"));
  public static final TagKey<Item> COPPERS = TagKey.of(
      RegistryKeys.ITEM, Identifier.of(InventoryManagementMod.MOD_ID, "coppers"));
  public static final TagKey<Item> GLASSES = TagKey.of(
      RegistryKeys.ITEM, Identifier.of(InventoryManagementMod.MOD_ID, "glasses"));
  public static final TagKey<Item> GLAZED_TERRACOTTAS = TagKey.of(
      RegistryKeys.ITEM, Identifier.of(InventoryManagementMod.MOD_ID, "glazed_terracottas"));
  public static final TagKey<Item> NATURAL_LOGS = TagKey.of(
      RegistryKeys.ITEM, Identifier.of(InventoryManagementMod.MOD_ID, "natural_logs"));
  public static final TagKey<Item> NATURAL_WOODS = TagKey.of(
      RegistryKeys.ITEM, Identifier.of(InventoryManagementMod.MOD_ID, "natural_woods"));
  public static final TagKey<Item> PRESSURE_PLATES = TagKey.of(
      RegistryKeys.ITEM, Identifier.of(InventoryManagementMod.MOD_ID, "pressure_plates"));
  public static final TagKey<Item> SEEDS = TagKey.of(
      RegistryKeys.ITEM, Identifier.of(InventoryManagementMod.MOD_ID, "seeds"));
  public static final TagKey<Item> STRIPPED_LOGS = TagKey.of(
      RegistryKeys.ITEM, Identifier.of(InventoryManagementMod.MOD_ID, "stripped_logs"));
  public static final TagKey<Item> STRIPPED_WOODS = TagKey.of(
      RegistryKeys.ITEM, Identifier.of(InventoryManagementMod.MOD_ID, "stripped_woods"));
}
