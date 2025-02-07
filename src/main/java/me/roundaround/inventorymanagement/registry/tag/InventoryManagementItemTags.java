package me.roundaround.inventorymanagement.registry.tag;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class InventoryManagementItemTags {
  public static final TagKey<Item> CONCRETE_POWDERS = of("concrete_powders");
  public static final TagKey<Item> CONCRETES = of("concretes");
  public static final TagKey<Item> GLASSES = of("glasses");
  public static final TagKey<Item> GLAZED_TERRACOTTAS = of("glazed_terracottas");
  public static final TagKey<Item> HAS_INVENTORY = of("has_inventory");

  private InventoryManagementItemTags() {
  }

  private static TagKey<Item> of(String id) {
    return TagKey.of(RegistryKeys.ITEM, new Identifier(InventoryManagementMod.MOD_ID, id));
  }
}
