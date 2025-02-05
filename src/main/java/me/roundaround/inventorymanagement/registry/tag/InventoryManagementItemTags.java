package me.roundaround.inventorymanagement.registry.tag;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class InventoryManagementItemTags {
  public static final TagKey<Item> CONCRETE_POWDERS = TagKey.of(
      RegistryKeys.ITEM, Identifier.of(InventoryManagementMod.MOD_ID, "concrete_powders"));
  public static final TagKey<Item> CONCRETES = TagKey.of(
      RegistryKeys.ITEM, Identifier.of(InventoryManagementMod.MOD_ID, "concretes"));
  public static final TagKey<Item> GLASSES = TagKey.of(
      RegistryKeys.ITEM, Identifier.of(InventoryManagementMod.MOD_ID, "glasses"));
  public static final TagKey<Item> GLAZED_TERRACOTTAS = TagKey.of(
      RegistryKeys.ITEM, Identifier.of(InventoryManagementMod.MOD_ID, "glazed_terracottas"));
}
